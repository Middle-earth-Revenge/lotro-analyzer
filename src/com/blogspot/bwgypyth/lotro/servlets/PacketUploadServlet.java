package com.blogspot.bwgypyth.lotro.servlets;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blogspot.bwgypyth.lotro.EMF;
import com.blogspot.bwgypyth.lotro.model.Packet;
import com.google.appengine.api.users.User;
import com.google.appengine.api.users.UserService;
import com.google.appengine.api.users.UserServiceFactory;

public class PacketUploadServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		Packet packet = new Packet();
		UserService userService = UserServiceFactory.getUserService();
		User user = userService.getCurrentUser();
		if (user == null) {
			throw new ServletException("Unauthorized access");
		}
		packet.setName(req.getParameter("name"));
		packet.setData(req.getParameter("data").replace(" ", "")
				.replace("\r", "").replace("\n", ""));
		packet.setUser(user);

		EntityManager em = EMF.get().createEntityManager();
		try {
			em.persist(packet);

			resp.sendRedirect("packets.jsp");
		} finally {
			em.close();
		}
	}

}
