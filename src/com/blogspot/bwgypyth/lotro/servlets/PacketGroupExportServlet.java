package com.blogspot.bwgypyth.lotro.servlets;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blogspot.bwgypyth.lotro.EMF;
import com.blogspot.bwgypyth.lotro.json.PacketGroupConverter;
import com.blogspot.bwgypyth.lotro.model.PacketGroup;
import com.google.appengine.labs.repackaged.org.json.JSONException;

public class PacketGroupExportServlet extends HttpServlet {

	private static final long serialVersionUID = -3159504001980261194L;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (req.getParameter("group") == null) {
			throw new ServletException("Missing parameter 'group'");
		}
		Long groupKey = Long.valueOf(req.getParameter("group"));
		EntityManager em = EMF.get().createEntityManager();
		try {
			PacketGroup group = em.find(PacketGroup.class, groupKey);
			resp.setContentType("application/json");
			resp.getOutputStream().print(
					new PacketGroupConverter().toJson(group).toString());
		} catch (JSONException e) {
			throw new ServletException(e.getMessage(), e);
		} finally {
			em.close();
		}
	}

}
