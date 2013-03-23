/*
 * Copyright (c) 2013 bwgypyth
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package com.blogspot.bwgypyth.lotro.servlets;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import com.blogspot.bwgypyth.lotro.EMF;
import com.blogspot.bwgypyth.lotro.logic.AnalysisFactory;
import com.blogspot.bwgypyth.lotro.model.Analysis;
import com.blogspot.bwgypyth.lotro.model.AnalysisEntry;
import com.blogspot.bwgypyth.lotro.model.OwnedEntity;
import com.blogspot.bwgypyth.lotro.model.Packet;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.memcache.jsr107cache.GCacheFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class PacketAjaxServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	private static Cache cache;
	private static final String CACHE_KEY_COLORS = "colors";
	private static final String CACHE_KEY_NAMES = "names";
	static {
		try {
			CacheFactory cacheFactory = CacheManager.getInstance()
					.getCacheFactory();
			Map<String, Object> props = new HashMap<>();
			props.put(GCacheFactory.EXPIRATION_DELTA, Integer.valueOf(3600));
			cache = cacheFactory.createCache(props);
		} catch (CacheException e) {
			cache = null;
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user == null) {
			resp.getOutputStream().print("unauthorized");
		} else {

			switch (req.getParameter("operation")) {
			case "update_packetname":
				resp.getOutputStream().print(updatePacketname(req, user));
				break;
			case "update_analysisname":
				resp.getOutputStream().print(updateAnalysisname(req, user));
				break;
			case "create_analysis":
				resp.getOutputStream().print(createAnalysis(user));
				break;
			case "update_analysisentry":
				resp.getOutputStream().print(updateAnalysisentry(req, user));
				break;
			case "create_analysisentry":
				resp.getOutputStream().print(createAnalysisentry(req, user));
				break;
			case "autocomplete_color":
				autocompleteColor(req, resp);
				break;
			case "autocomplete_name":
				autocompleteName(req, resp);
				break;
			case "delete_entry":
				resp.getOutputStream().print(deleteEntry(req));
				break;
			case "delete_analysis":
				resp.getOutputStream().print(deleteAnalysis(req));
				break;
			case "delete_packet":
				resp.getOutputStream().print(deletePacket(req));
				break;
			default:
				resp.getOutputStream().print("unknown");
				break;
			}
		}
	}

	private static String deleteEntry(HttpServletRequest req) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user == null || !userService.isUserAdmin()) {
			return "unauthorized";
		}

		long analysisentryKey = Long.parseLong(req.getParameter("entry_key"));
		long packetKey = Long.parseLong(req.getParameter("packet_key"));
		long analysisKey = Long.parseLong(req.getParameter("analysis_key"));
		EntityManager em = EMF.get().createEntityManager();
		try {
			em.getTransaction().begin();
			AnalysisEntry analysisEntry = em.find(AnalysisEntry.class,
					KeyFactory.createKey(KeyFactory.createKey(
							KeyFactory.createKey("Packet", packetKey),
							"Analysis", analysisKey), "AnalysisEntry",
							analysisentryKey));
			em.remove(analysisEntry);
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			em.close();
		}
		return "ok";
	}

	private static String deleteAnalysis(HttpServletRequest req) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user == null || !userService.isUserAdmin()) {
			return "unauthorized";
		}

		long packetKey = Long.parseLong(req.getParameter("packet_key"));
		long analysisKey = Long.parseLong(req.getParameter("analysis_key"));
		EntityManager em = EMF.get().createEntityManager();
		try {
			em.getTransaction().begin();
			Analysis analysis = em.find(Analysis.class, KeyFactory.createKey(
					KeyFactory.createKey("Packet", packetKey), "Analysis",
					analysisKey));
			em.remove(analysis);
			for (AnalysisEntry analysisEntry : analysis.getAnalysisEntries()) {
				em.remove(analysisEntry);
			}

			Packet packet = analysis.getPacket();
			packet.getAnalyses().remove(analysis);
			if (packet.getAnalyses().isEmpty()) {
				Analysis stubAnalysis = AnalysisFactory.createAnalysis(packet,
						user);
				OwnedEntity.setModified(packet, user);
				em.persist(stubAnalysis);
			}

			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			em.close();
		}
		return "ok";
	}

	private static String deletePacket(HttpServletRequest req) {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user == null || !userService.isUserAdmin()) {
			return "unauthorized";
		}

		Long packetKey = Long.valueOf(req.getParameter("packet_key"));
		EntityManager em = EMF.get().createEntityManager();
		try {
			em.getTransaction().begin();
			Packet packet = em.find(Packet.class, packetKey);
			em.remove(packet);
			for (Analysis analysis : packet.getAnalyses()) {
				em.remove(analysis);
				for (AnalysisEntry analysisEntry : analysis
						.getAnalysisEntries()) {
					em.remove(analysisEntry);
				}
			}
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			em.close();
		}
		return "ok";
	}

	private static void autocompleteColor(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		@SuppressWarnings("resource")
		ServletOutputStream out = resp.getOutputStream();
		resp.setContentType("application/json");
		String term = req.getParameter("term");
		if (cache.containsKey(CACHE_KEY_COLORS)) {
			@SuppressWarnings("unchecked")
			List<String> colors = (List<String>) cache.get(CACHE_KEY_COLORS);
			out.print(getMatchingColors(colors, term));
		} else {
			EntityManager em = EMF.get().createEntityManager();
			try {
				@SuppressWarnings("unchecked")
				List<String> backgroundColors = em
						.createQuery(
								"select distinct entry.color from AnalysisEntry entry order by entry.color")
						.getResultList();
				Set<String> allColors = new HashSet<>(backgroundColors);

				@SuppressWarnings("unchecked")
				List<String> foregroundColors = em
						.createQuery(
								"select distinct entry.foregroundColor from AnalysisEntry entry order by entry.foregroundColor")
						.getResultList();
				allColors.addAll(foregroundColors);

				List<String> colors = new ArrayList<>(allColors);
				Collections.sort(colors);
				cache.put(CACHE_KEY_COLORS, colors);
				out.print(getMatchingColors(colors, term));
			} finally {
				em.close();
			}
		}
	}

	private static String getMatchingColors(List<String> colors, String term) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("[");
		for (String color : colors) {
			if (color.startsWith(term)) {
				if (buffer.length() > 1) {
					buffer.append(",");
				}
				buffer.append("{\"id\":\"");
				buffer.append(color);
				buffer.append("\",\"label\":\"<span style=\\\"color: ");
				buffer.append(color);
				buffer.append("\\\">");
				buffer.append(color);
				buffer.append("</span>\",\"value\":\"");
				buffer.append(color);
				buffer.append("\"}");
			}
		}
		buffer.append("]");
		return buffer.toString();
	}

	private static void autocompleteName(HttpServletRequest req,
			HttpServletResponse resp) throws IOException {
		@SuppressWarnings("resource")
		ServletOutputStream out = resp.getOutputStream();
		resp.setContentType("application/json");
		String term = req.getParameter("term");
		if (cache.containsKey(CACHE_KEY_NAMES)) {
			@SuppressWarnings("unchecked")
			List<String> names = (List<String>) cache.get(CACHE_KEY_NAMES);
			out.print(getMatchingNames(names, term));
		} else {
			EntityManager em = EMF.get().createEntityManager();
			try {
				@SuppressWarnings("unchecked")
				List<String> names = em
						.createQuery(
								"select distinct entry.name from AnalysisEntry entry order by entry.name")
						.getResultList();
				cache.put(CACHE_KEY_NAMES, new ArrayList<>(names));
				out.print(getMatchingNames(names, term));
			} finally {
				em.close();
			}
		}
	}

	private static String getMatchingNames(List<String> names, String term) {
		StringBuilder buffer = new StringBuilder();
		buffer.append("[");
		for (String name : names) {
			if (name.startsWith(term)) {
				if (buffer.length() > 1) {
					buffer.append(",");
				}
				buffer.append("{\"id\":\"");
				buffer.append(name);
				buffer.append("\",\"label\":\"");
				buffer.append(name);
				buffer.append("\",\"value\":\"");
				buffer.append(name);
				buffer.append("\"}");
			}
		}
		buffer.append("]");
		return buffer.toString();
	}

	private static String createAnalysisentry(HttpServletRequest req, User user) {
		long analysisKey = Long.parseLong(req.getParameter("analysis_key"));
		long packetKey = Long.parseLong(req.getParameter("packet_key"));
		int start = Integer.parseInt(
				req.getParameter("entry_start").substring(2), 16);
		int end = Integer.parseInt(req.getParameter("entry_end").substring(2),
				16);
		String description = req.getParameter("entry_description");
		String color = req.getParameter("entry_color").toLowerCase();
		String foregroundColor = req.getParameter("entry_foregroundcolor");
		String name = req.getParameter("entry_name");
		EntityManager em = EMF.get().createEntityManager();
		try {
			Analysis analysis = em.find(Analysis.class, KeyFactory.createKey(
					KeyFactory.createKey("Packet", packetKey), "Analysis",
					analysisKey));
			OwnedEntity.setModified(analysis, user);

			for (AnalysisEntry analysisEntry : analysis.getAnalysisEntries()) {
				if (analysisEntry.getName().equals(name)) {
					return "duplicate";
				}
			}

			AnalysisEntry analysisEntry = new AnalysisEntry();
			OwnedEntity.setCreated(analysisEntry, user);
			setAnalysisData(user, start, end, description, color,
					foregroundColor, name, analysisEntry);
			analysis.getAnalysisEntries().add(analysisEntry);
			analysisEntry.setAnalysis(analysis);

			em.merge(analysis);

			updateCache(analysisEntry);
		} finally {
			em.close();
		}
		return "ok";
	}

	private static String updateAnalysisentry(HttpServletRequest req, User user) {
		long analysisentryKey = Long.parseLong(req.getParameter("entry_key"));
		long analysisKey = Long.parseLong(req.getParameter("analysis_key"));
		long packetKey = Long.parseLong(req.getParameter("packet_key"));

		int start = Integer.parseInt(
				req.getParameter("entry_start").substring(2), 16);
		int end = Integer.parseInt(req.getParameter("entry_end").substring(2),
				16);
		String description = req.getParameter("entry_description");
		String color = req.getParameter("entry_color").toLowerCase();
		String foregroundColor = req.getParameter("entry_foregroundcolor");
		String name = req.getParameter("entry_name");
		EntityManager em = EMF.get().createEntityManager();
		try {
			AnalysisEntry analysisEntry = em.find(AnalysisEntry.class,
					KeyFactory.createKey(KeyFactory.createKey(
							KeyFactory.createKey("Packet", packetKey),
							"Analysis", analysisKey), "AnalysisEntry",
							analysisentryKey));

			for (AnalysisEntry otherAnalysisEntry : analysisEntry.getAnalysis()
					.getAnalysisEntries()) {
				if (otherAnalysisEntry.getName().equals(name)
						&& !otherAnalysisEntry.getKey().equals(
								analysisEntry.getKey())) {
					return "duplicate";
				}
			}

			setAnalysisData(user, start, end, description, color,
					foregroundColor, name, analysisEntry);

			em.merge(analysisEntry);

			updateCache(analysisEntry);
		} finally {
			em.close();
		}

		return "ok";
	}

	private static String createAnalysis(User user) {
		EntityManager em = EMF.get().createEntityManager();
		Analysis analysis = new Analysis();
		try {
			OwnedEntity.setModified(analysis, user);
		} finally {
			em.close();
		}

		return Long.toString(analysis.getKey().getId());
	}

	private static String updateAnalysisname(HttpServletRequest req, User user) {
		long analysisKey = Long.parseLong(req.getParameter("analysis_key"));
		long packetKey = Long.parseLong(req.getParameter("packet_key"));
		String packetName = req.getParameter("analysis_name");
		EntityManager em = EMF.get().createEntityManager();
		try {
			Analysis analysis = em.find(Analysis.class, KeyFactory.createKey(
					KeyFactory.createKey("Packet", packetKey), "Analysis",
					analysisKey));
			analysis.setName(packetName);
			OwnedEntity.setModified(analysis, user);
			em.merge(analysis);
		} finally {
			em.close();
		}
		return "ok";
	}

	private static String updatePacketname(HttpServletRequest req, User user) {
		Long packetKey = Long.valueOf(req.getParameter("packet_key"));
		String packetName = req.getParameter("packet_name");
		EntityManager em = EMF.get().createEntityManager();
		try {
			Packet packet = em.find(Packet.class, packetKey);
			packet.setName(packetName);
			OwnedEntity.setModified(packet, user);
			em.merge(packet);
		} finally {
			em.close();
		}
		return "ok";
	}

	private static void updateCache(AnalysisEntry analysisEntry) {
		if (cache != null) {
			if (cache.containsKey(CACHE_KEY_COLORS)) {
				@SuppressWarnings("unchecked")
				List<String> colors = (List<String>) cache
						.get(CACHE_KEY_COLORS);
				if (!colors.contains(analysisEntry.getColor())) {
					colors.add(analysisEntry.getColor());
					Collections.sort(colors);
				}
				if (!colors.contains(analysisEntry.getForegroundColor())) {
					colors.add(analysisEntry.getForegroundColor());
					Collections.sort(colors);
				}
				cache.put(CACHE_KEY_COLORS, colors);
			}

			if (cache.containsKey(CACHE_KEY_NAMES)) {
				@SuppressWarnings("unchecked")
				List<String> names = (List<String>) cache.get(CACHE_KEY_NAMES);
				if (!names.contains(analysisEntry.getName())) {
					names.add(analysisEntry.getName());
					Collections.sort(names);
				}
				cache.put(CACHE_KEY_NAMES, names);
			}
		}
	}

	private static void setAnalysisData(User user, int start, int end,
			String description, String color, String foregroundColor,
			String name, AnalysisEntry analysisEntry) {
		analysisEntry.setColor(color);
		analysisEntry
				.setForegroundColor(foregroundColor != null ? foregroundColor
						.toLowerCase() : null);
		analysisEntry.setName(name);
		analysisEntry.setDescription(description);
		analysisEntry.setStart(start);
		analysisEntry.setEnd(end);
		OwnedEntity.setModified(analysisEntry, user);
	}
}
