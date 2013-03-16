package com.blogspot.bwgypyth.lotro.servlets;

import java.io.IOException;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import net.sf.jsr107cache.Cache;
import net.sf.jsr107cache.CacheException;
import net.sf.jsr107cache.CacheFactory;
import net.sf.jsr107cache.CacheManager;

import com.blogspot.bwgypyth.lotro.EMF;
import com.blogspot.bwgypyth.lotro.model.Analysis;
import com.blogspot.bwgypyth.lotro.model.AnalysisEntry;
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
	static {
		try {
			CacheFactory cacheFactory = CacheManager.getInstance()
					.getCacheFactory();
			Map<String, Object> props = new HashMap<>();
			props.put(GCacheFactory.EXPIRATION_DELTA, 3600);
			cache = cacheFactory.createCache(props);
		} catch (CacheException e) {
			cache = null;
		}
	}

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user == null) {
			resp.getOutputStream().print("unauthorized");
		} else {

			switch (req.getParameter("operation")) {
			case "update_packetname": {
				Long packetKey = Long.valueOf(req.getParameter("packet_key"));
				String packetName = req.getParameter("packet_name");
				EntityManager em = EMF.get().createEntityManager();
				try {
					Packet packet = em.find(Packet.class, packetKey);
					packet.setName(packetName);
					packet.setModified(new Date());
					packet.setModifiedBy(user);
					em.merge(packet);
				} finally {
					em.close();
				}
				resp.getOutputStream().print("ok");
				break;
			}
			case "update_analysisname": {
				Long analysisKey = Long.valueOf(req
						.getParameter("analysis_key"));
				Long packetKey = Long.valueOf(req.getParameter("packet_key"));
				String packetName = req.getParameter("analysis_name");
				EntityManager em = EMF.get().createEntityManager();
				try {
					Analysis analysis = em.find(Analysis.class, KeyFactory
							.createKey(
									KeyFactory.createKey("Packet", packetKey),
									"Analysis", analysisKey));
					analysis.setName(packetName);
					analysis.setModified(new Date());
					analysis.setModifiedBy(user);
					em.merge(analysis);
				} finally {
					em.close();
				}
				resp.getOutputStream().print("ok");
				break;
			}
			case "update_analysisentry": {
				Long analysisentryKey = Long.valueOf(req
						.getParameter("entry_key"));
				Long analysisKey = Long.valueOf(req
						.getParameter("analysis_key"));
				Long packetKey = Long.valueOf(req.getParameter("packet_key"));

				Integer start = Integer.valueOf(req.getParameter("entry_start")
						.substring(2), 16);
				Integer end = Integer.valueOf(req.getParameter("entry_end")
						.substring(2), 16);
				String description = req.getParameter("entry_description");
				String color = req.getParameter("entry_color").toLowerCase();
				String foregroundColor = req
						.getParameter("entry_foregroundcolor");
				String name = req.getParameter("entry_name");
				EntityManager em = EMF.get().createEntityManager();
				try {
					AnalysisEntry analysisEntry = em.find(AnalysisEntry.class,
							KeyFactory.createKey(KeyFactory.createKey(
									KeyFactory.createKey("Packet", packetKey),
									"Analysis", analysisKey), "AnalysisEntry",
									analysisentryKey));

					for (AnalysisEntry otherAnalysisEntry : analysisEntry
							.getAnalysis().getAnalysisEntries()) {
						if (otherAnalysisEntry.getName().equals(name)
								&& !otherAnalysisEntry.getKey().equals(
										analysisEntry.getKey())) {
							resp.getOutputStream().print("duplicate");
							return;
						}
					}

					setAnalysisData(user, start, end, description, color,
							foregroundColor, name, analysisEntry);

					em.merge(analysisEntry);

					updateColorCache(analysisEntry);
				} finally {
					em.close();
				}

				resp.getOutputStream().print("ok");
				break;
			}
			case "create_analysisentry": {
				Long analysisKey = Long.valueOf(req
						.getParameter("analysis_key"));
				Long packetKey = Long.valueOf(req.getParameter("packet_key"));
				Integer start = Integer.valueOf(req.getParameter("entry_start")
						.substring(2), 16);
				Integer end = Integer.valueOf(req.getParameter("entry_end")
						.substring(2), 16);
				String description = req.getParameter("entry_description");
				String color = req.getParameter("entry_color").toLowerCase();
				String foregroundColor = req
						.getParameter("entry_foregroundcolor");
				String name = req.getParameter("entry_name");
				EntityManager em = EMF.get().createEntityManager();
				try {
					Analysis analysis = em.find(Analysis.class, KeyFactory
							.createKey(
									KeyFactory.createKey("Packet", packetKey),
									"Analysis", analysisKey));
					analysis.setModified(new Date());
					analysis.setModifiedBy(user);

					for (AnalysisEntry analysisEntry : analysis
							.getAnalysisEntries()) {
						if (analysisEntry.getName().equals(name)) {
							resp.getOutputStream().print("duplicate");
							return;
						}
					}

					AnalysisEntry analysisEntry = new AnalysisEntry();
					analysisEntry.setCreatedBy(user);
					analysisEntry.setCreated(new Date());
					setAnalysisData(user, start, end, description, color,
							foregroundColor, name, analysisEntry);
					analysis.getAnalysisEntries().add(analysisEntry);
					analysisEntry.setAnalysis(analysis);

					em.merge(analysis);

					updateColorCache(analysisEntry);
				} finally {
					em.close();
				}
				resp.getOutputStream().print("ok");
				break;
			}
			case "autocomplete_color": {
				ServletOutputStream out = resp.getOutputStream();
				resp.setContentType("application/json");
				String term = req.getParameter("term");
				if (cache.containsKey(CACHE_KEY_COLORS)) {
					@SuppressWarnings("unchecked")
					List<String> colors = (List<String>) cache
							.get(CACHE_KEY_COLORS);
					out.print(getMatchingColors(colors, term));
				} else {
					EntityManager em = EMF.get().createEntityManager();
					try {
						@SuppressWarnings("unchecked")
						List<String> colors = em
								.createQuery(
										"select distinct entry.color from AnalysisEntry entry")
								.getResultList();

						cache.put(CACHE_KEY_COLORS, colors);
						out.print(getMatchingColors(colors, term));
					} finally {
						em.close();
					}
				}
				break;
			}
			default:
				resp.getOutputStream().print("unknown");
				break;
			}
		}
	}

	private void updateColorCache(AnalysisEntry analysisEntry) {
		if (cache != null) {
			if (cache.containsKey(CACHE_KEY_COLORS)) {
				@SuppressWarnings("unchecked")
				List<String> colors = (List<String>) cache
						.get(CACHE_KEY_COLORS);
				if (!colors.contains(analysisEntry.getColor())) {
					colors.add(analysisEntry.getColor());
					Collections.sort(colors);
				}
			}
		}
	}

	private String getMatchingColors(List<String> colors, String term) {
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

	private void setAnalysisData(User user, Integer start, Integer end,
			String description, String color, String foregroundColor,
			String name, AnalysisEntry analysisEntry) {
		analysisEntry.setColor(color);
		if (foregroundColor != null) {
			foregroundColor = foregroundColor.toLowerCase();
		}
		analysisEntry.setForegroundColor(foregroundColor);
		analysisEntry.setName(name);
		analysisEntry.setDescription(description);
		analysisEntry.setStart(start);
		analysisEntry.setEnd(end);
		analysisEntry.setModifiedBy(user);
		analysisEntry.setModified(new Date());
	}
}
