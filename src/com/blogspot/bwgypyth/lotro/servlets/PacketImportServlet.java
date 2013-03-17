package com.blogspot.bwgypyth.lotro.servlets;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blogspot.bwgypyth.lotro.EMF;
import com.blogspot.bwgypyth.lotro.json.IncludeUserdata;
import com.blogspot.bwgypyth.lotro.json.PacketConverter;
import com.blogspot.bwgypyth.lotro.model.OwnedEntity;
import com.blogspot.bwgypyth.lotro.model.Packet;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class PacketImportServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		resp.getOutputStream().print(
				"<!DOCTYPE html>\n" + "<html>\n" + "<body>\n"
						+ "<form action=\"/import/packet\" method=\"post\">\n"
						+ "<textarea name=\"packet_data\"></textarea>\n"
						+ "<input type=\"submit\"/>\n" + "</form>\n"
						+ "</body>\n" + "</html>");
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (req.getParameter("packet_data") == null) {
			throw new ServletException("Missing parameter 'packet_data'");
		}

		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user == null) {
			throw new ServletException("Unauthorized access");
		}

		String packetData = req.getParameter("packet_data");
		EntityManager em = EMF.get().createEntityManager();
		try {
			Packet packet = new PacketConverter(IncludeUserdata.INCLUDE_ALL)
					.fromJson(new JSONObject(packetData));
			OwnedEntity.setCreated(packet, user);
			OwnedEntity.setModified(packet, user);

			em.merge(packet);

			resp.sendRedirect("/packets.jsp");
		} catch (JSONException e) {
			throw new ServletException(e.getMessage(), e);
		} finally {
			em.close();
		}
	}
}
