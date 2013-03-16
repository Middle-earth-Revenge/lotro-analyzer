package com.blogspot.bwgypyth.lotro.servlets;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blogspot.bwgypyth.lotro.EMF;
import com.blogspot.bwgypyth.lotro.json.IncludeUserdata;
import com.blogspot.bwgypyth.lotro.json.PacketConverter;
import com.blogspot.bwgypyth.lotro.model.Packet;
import com.google.appengine.labs.repackaged.org.json.JSONException;

public class PacketExportServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (req.getParameter("packet") == null) {
			throw new ServletException("Missing parameter 'packet'");
		}
		Long packetKey = Long.valueOf(req.getParameter("packet"));
		EntityManager em = EMF.get().createEntityManager();
		try {
			Packet packet = em.find(Packet.class, packetKey);
			ServletOutputStream out = resp.getOutputStream();
			resp.setContentType("application/json");
			out.print(new PacketConverter(IncludeUserdata.INCLUDE_ALL).toJson(
					packet).toString());
		} catch (JSONException e) {
			throw new ServletException(e.getMessage(), e);
		} finally {
			em.close();
		}
	}
}
