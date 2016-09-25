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
import java.nio.channels.NonReadableChannelException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.servlet.ServletOutputStream;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blogspot.bwgypyth.lotro.EMF;
import com.blogspot.bwgypyth.lotro.logic.AnalysisFactory;
import com.blogspot.bwgypyth.lotro.model.Analysis;
import com.blogspot.bwgypyth.lotro.model.AnalysisEntry;
import com.blogspot.bwgypyth.lotro.model.OwnedEntity;
import com.blogspot.bwgypyth.lotro.model.Packet;
import com.blogspot.bwgypyth.lotro.model.PacketGroup;

@WebServlet(urlPatterns = "/packet_ajax")
public class PacketAjaxServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException {
		switch (req.getParameter("operation")) {
		case "update_packetname":
			resp.getOutputStream().print(updatePacketname(req));
			break;
		case "update_analysisname":
			resp.getOutputStream().print(updateAnalysisname(req));
			break;
		case "create_analysis":
			resp.getOutputStream().print(createAnalysis());
			break;
		case "update_analysisentry":
			resp.getOutputStream().print(updateAnalysisentry(req));
			break;
		case "create_analysisentry":
			resp.getOutputStream().print(createAnalysisentry(req));
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
		case "select_group":
			resp.getOutputStream().print(selectGroup(req));
			break;
		case "create_group":
			resp.getOutputStream().print(createGroup(req));
			break;
		default:
			resp.getOutputStream().print("unknown");
			break;
		}
	}

	private static String deleteEntry(HttpServletRequest req) {
		int analysisentryKey = Integer.parseInt(req.getParameter("entry_key"));
		EntityManager em = EMF.get().createEntityManager();
		try {
			em.getTransaction().begin();
			AnalysisEntry analysisEntry = em.find(AnalysisEntry.class, analysisentryKey);
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
		int analysisKey = Integer.parseInt(req.getParameter("analysis_key"));
		EntityManager em = EMF.get().createEntityManager();
		try {
			em.getTransaction().begin();
			Analysis analysis = em.find(Analysis.class, analysisKey);
			em.remove(analysis);
			for (AnalysisEntry analysisEntry : analysis.getAnalysisEntries()) {
				em.remove(analysisEntry);
			}

			Packet packet = analysis.getPacket();
			packet.getAnalyses().remove(analysis);
			if (packet.getAnalyses().isEmpty()) {
				Analysis stubAnalysis = AnalysisFactory.createAnalysis(packet);
				OwnedEntity.setModified(packet, "anoynmous");
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
		Integer packetKey = Integer.valueOf(req.getParameter("packet_key"));
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
		ServletOutputStream out = resp.getOutputStream();
		resp.setContentType("application/json");
		String term = req.getParameter("term");
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
							"select distinct entry.foregroundColor from AnalysisEntry entry where entry.foregroundColor is not null order by entry.foregroundColor")
					.getResultList();
			allColors.addAll(foregroundColors);

			List<String> colors = new ArrayList<>(allColors);
			Collections.sort(colors);
			out.print(getMatchingColors(colors, term));
		} finally {
			em.close();
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
		ServletOutputStream out = resp.getOutputStream();
		resp.setContentType("application/json");
		String term = req.getParameter("term");
		EntityManager em = EMF.get().createEntityManager();
		try {
			@SuppressWarnings("unchecked")
			List<String> names = em
					.createQuery(
							"select distinct entry.name from AnalysisEntry entry order by entry.name")
					.getResultList();
			out.print(getMatchingNames(names, term));
		} finally {
			em.close();
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

	private static String createAnalysisentry(HttpServletRequest req) {
		int analysisKey = Integer.parseInt(req.getParameter("analysis_key"));
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
			em.getTransaction().begin();
			Analysis analysis = em.find(Analysis.class, analysisKey);
			OwnedEntity.setModified(analysis, "anonymous");

			AnalysisEntry analysisEntry = new AnalysisEntry();
			OwnedEntity.setCreated(analysisEntry, "anonymous");
			setAnalysisData(start, end, description, color,
					foregroundColor, name, analysisEntry);
			analysis.getAnalysisEntries().add(analysisEntry);
			analysisEntry.setAnalysis(analysis);

			em.merge(analysis);
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			em.close();
		}
		return "ok";
	}

	private static String updateAnalysisentry(HttpServletRequest req) {
		int analysisentryKey = Integer.parseInt(req.getParameter("entry_key"));

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
			em.getTransaction().begin();
			AnalysisEntry analysisEntry = em.find(AnalysisEntry.class, analysisentryKey);

			setAnalysisData(start, end, description, color,
					foregroundColor, name, analysisEntry);

			em.merge(analysisEntry);
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			em.close();
		}

		return "ok";
	}

	private static String createAnalysis() {
		EntityManager em = EMF.get().createEntityManager();
		Analysis analysis = new Analysis();
		try {
			OwnedEntity.setModified(analysis, "anoynmous");
		} finally {
			em.close();
		}

		return Long.toString(analysis.getKey());
	}

	private static String updateAnalysisname(HttpServletRequest req) {
		int analysisKey = Integer.parseInt(req.getParameter("analysis_key"));
		String packetName = req.getParameter("analysis_name");
		EntityManager em = EMF.get().createEntityManager();
		try {
			Analysis analysis = em.find(Analysis.class, analysisKey);
			analysis.setName(packetName);
			OwnedEntity.setModified(analysis, "anonymous");
			em.merge(analysis);
		} finally {
			em.close();
		}
		return "ok";
	}

	private static String updatePacketname(HttpServletRequest req) {
		Integer packetKey = Integer.valueOf(req.getParameter("packet_key"));
		String packetName = req.getParameter("packet_name");
		EntityManager em = EMF.get().createEntityManager();
		try {
			Packet packet = em.find(Packet.class, packetKey);
			packet.setName(packetName);
			OwnedEntity.setModified(packet, "anoynmous");
			em.merge(packet);
		} finally {
			em.close();
		}
		return "ok";
	}

	private static void setAnalysisData(int start, int end,
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
		OwnedEntity.setModified(analysisEntry, "anoynmous");
	}

	private static String selectGroup(HttpServletRequest req) {
		Integer packetKey = Integer.valueOf(req.getParameter("packet_key"));
		Integer groupKey = Integer.valueOf(req.getParameter("group_key"));
		EntityManager em = EMF.get().createEntityManager();
		try {
			em.getTransaction().begin();
			Packet packet = em.find(Packet.class, packetKey);
			PacketGroup group = em.find(PacketGroup.class, groupKey);
			packet.setGroup(group);
			OwnedEntity.setModified(packet, "anoynmous");
			em.persist(packet);
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			em.close();
		}

		return "ok";
	}

	private static String createGroup(HttpServletRequest req) {
		EntityManager em = EMF.get().createEntityManager();
		PacketGroup group;
		try {
			group = em.createQuery("from PacketGroup where name = :name", PacketGroup.class).setParameter("name", req.getParameter("name")).getSingleResult();
		} catch (NoResultException e) {
			group = new PacketGroup();
		}
		try {
			em.getTransaction().begin();
			OwnedEntity.setModified(group, "anoynmous");
			if (group.getKey() == null) {
				group.setName(req.getParameter("name"));
				OwnedEntity.setCreated(group, "anoynmous");
				em.persist(group);
			} else {
				em.merge(group);
			}
			em.getTransaction().commit();
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			em.close();
		}

		return Long.toString(group.getKey());
	}

}
