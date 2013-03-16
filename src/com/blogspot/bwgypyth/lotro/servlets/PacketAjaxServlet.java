package com.blogspot.bwgypyth.lotro.servlets;

import java.io.IOException;
import java.util.Date;
import java.util.List;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blogspot.bwgypyth.lotro.EMF;
import com.blogspot.bwgypyth.lotro.model.Analysis;
import com.blogspot.bwgypyth.lotro.model.AnalysisEntry;
import com.blogspot.bwgypyth.lotro.model.Packet;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class PacketAjaxServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

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
					analysisEntry.setColor(color);
					if (foregroundColor != null) {
						foregroundColor = foregroundColor.toLowerCase();
					}
					analysisEntry.setForegroundColor(foregroundColor);
					analysisEntry.setName(name);
					analysisEntry.setDescription(description);
					analysisEntry.setStart(start);
					analysisEntry.setEnd(end);
					analysisEntry.setCreatedBy(user);
					analysisEntry.setCreated(new Date());
					analysisEntry.setModifiedBy(user);
					analysisEntry.setModified(analysis.getCreated());
					analysis.getAnalysisEntries().add(analysisEntry);
					analysisEntry.setAnalysis(analysis);

					em.merge(analysis);
				} finally {
					em.close();
				}
				resp.getOutputStream().print("ok");
				break;
			}
			case "autocomplete_color":
				String term = req.getParameter("term");
				EntityManager em = EMF.get().createEntityManager();
				try {
					@SuppressWarnings("unchecked")
					List<String> colors = em
							.createQuery(
									"select distinct entry.color from AnalysisEntry entry where entry.color like :term")
							.setParameter("term", term + "%").getResultList();
					ServletOutputStream out = resp.getOutputStream();
					resp.setContentType("application/json");
					out.print("[");
					boolean first = true;
					for (String color : colors) {
						if (!first) {
							out.print(",");
						}
						out.print("{\"id\":\"" + color
								+ "\",\"label\":\"<span style=\\\"color: "
								+ color + "\\\">" + color
								+ "</span>\",\"value\":\"" + color + "\"}");
						first = false;
						first = false;
					}
					out.print("]");
				} finally {
					em.close();
				}
			}
		}
	}
}
