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
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blogspot.bwgypyth.lotro.EMF;
import com.blogspot.bwgypyth.lotro.json.ExportType;
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
		ExportType exportType = "binary".equals(req.getParameter("type")) ? ExportType.BINARY
				: ExportType.JSON;
		EntityManager em = EMF.get().createEntityManager();
		try {
			Packet packet = em.find(Packet.class, packetKey);
			switch (exportType) {
			case JSON:
				resp.setContentType("application/json");
				resp.getOutputStream().print(
						new PacketConverter(IncludeUserdata.INCLUDE_ALL)
								.toJson(packet).toString());
				break;
			case BINARY:
				resp.setContentType("application/octet-stream");
				String data = packet.getData();
				byte[] hexStringToByteArray = hexStringToByteArray(data);
				resp.getOutputStream().write(hexStringToByteArray);
				break;
			default:
				throw new ServletException("Unknown type " + exportType);
			}
		} catch (JSONException e) {
			throw new ServletException(e.getMessage(), e);
		} finally {
			em.close();
		}
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character
					.digit(s.charAt(i + 1), 16));
		}
		return data;
	}
}
