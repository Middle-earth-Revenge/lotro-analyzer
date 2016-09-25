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

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONException;
import org.json.JSONObject;

import com.blogspot.bwgypyth.lotro.EMF;
import com.blogspot.bwgypyth.lotro.json.PacketConverter;
import com.blogspot.bwgypyth.lotro.model.OwnedEntity;
import com.blogspot.bwgypyth.lotro.model.Packet;
import com.blogspot.bwgypyth.lotro.model.PacketGroup;

@WebServlet(urlPatterns = "/import/packet")
public class PacketImportServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		if (req.getParameter("packet_data") == null) {
			throw new ServletException("Missing parameter 'packet_data'");
		}
		if (req.getParameter("group_key") == null) {
			throw new ServletException("Missing parameter 'group_key'");
		}

		String packetData = req.getParameter("packet_data");
		Integer groupKey = Integer.valueOf(req.getParameter("group_key"));
		EntityManager em = EMF.get().createEntityManager();
		try {
			em.getTransaction().begin();
			Packet packet = new PacketConverter().fromJson(new JSONObject(
					packetData));
			OwnedEntity.setCreated(packet, "anonymous");
			OwnedEntity.setModified(packet, "anonymous");
			PacketGroup packetGroup = em.find(PacketGroup.class, groupKey);
			packet.setGroup(packetGroup);

			em.merge(packet);

			resp.sendRedirect("/packets.jsp");
			
			em.getTransaction().commit();
		} catch (JSONException e) {
			throw new ServletException(e.getMessage(), e);
		} finally {
			if (em.getTransaction().isActive()) {
				em.getTransaction().rollback();
			}
			em.close();
		}
	}
}
