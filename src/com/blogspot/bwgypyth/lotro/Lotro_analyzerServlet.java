package com.blogspot.bwgypyth.lotro;

import java.io.IOException;

import javax.persistence.EntityManager;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.blogspot.bwgypyth.lotro.json.AnalysisConverter;
import com.blogspot.bwgypyth.lotro.model.Analysis;
import com.blogspot.bwgypyth.lotro.model.Packet;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

@SuppressWarnings("serial")
public class Lotro_analyzerServlet extends HttpServlet {
	public void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws IOException, ServletException {

		EntityManager em = EMF.get().createEntityManager();
		try {
			Packet packet = new Packet();
			packet.setData("000000000061000100000000000039F7C460000000003F3036313030345F6E65747665723A373533373B206469647665723A39323643443845332D323938342D344341392D394336422D3644463243384542364243331D000000010000000000000034B5FE5008740065007300740075007300650072");
			String json = "[\n"
					+ "	{\n"
					+ "		name: 'header',\n"
					+ "		start: 0x00,\n"
					+ "		end: 0x15,\n"
					+ "		description: '<b>Payload Header part</b>',\n"
					+ "		color: '#000000',\n"
					+ "		foregroundColor: '#ffffff'\n"
					+ "	},\n"
					+ "	{\n"
					+ "		name: 'header0',\n"
					+ "		start: 0x02,\n"
					+ "		end: 0x03,\n"
					+ "		description: 'The (pseudo) Header part - During session setup (the header starts with 0x00 0x00) its length is 22 bytes, in all other cases its 20 bytes long',\n"
					+ "		color: '#c2d59b'\n"
					+ "	},\n"
					+ "	{\n"
					+ "		name: 'header1',\n"
					+ "		start: 0x04,\n"
					+ "		end: 0x05,\n"
					+ "		description: 'Session ID from client / server',\n"
					+ "		color: '#ff66cc'\n"
					+ "	},\n"
					+ "	{\n"
					+ "		name: 'header2',\n"
					+ "		start: 0x06,\n"
					+ "		end: 0x09,\n"
					+ "		description: 'Size of Payload Data part (0x0061h == 97 bytes payload data part length)',\n"
					+ "		color: '#66ff33'\n"
					+ "	},\n"
					+ "	{\n"
					+ "		name: 'header3',\n"
					+ "		start: 0x0A,\n"
					+ "		end: 0x0D,\n"
					+ "		description: 'Packet sequence number (no sequence during session establishment)',\n"
					+ "		color: '#ffc000'\n"
					+ "	},\n"
					+ "	{\n"
					+ "		name: 'header4',\n"
					+ "		start: 0x0E,\n"
					+ "		end: 0x11,\n"
					+ "		description: 'Checksum for Payload Data part',\n"
					+ "		color: '#ff0000'\n"
					+ "	},\n"
					+ "	{\n"
					+ "		name: 'header5',\n"
					+ "		start: 0x12,\n"
					+ "		end: 0x15,\n"
					+ "		description: 'Temporary session number (???)',\n"
					+ "		color: '#8db3e1'\n"
					+ "	},\n"
					+ "	{\n"
					+ "		name: 'data',\n"
					+ "		start: 0x16,\n"
					+ "		end: 0x75,\n"
					+ "		description: '<b>Payload Data part</b>',\n"
					+ "		color: '#aaaaaa',\n"
					+ "	},\n"
					+ "	{\n"
					+ "		name: 'data0',\n"
					+ "		start: 0x16,\n"
					+ "		end: 0x55,\n"
					+ "		description: 'Client-Version string with leading length',\n"
					+ "		color: '#c2d59b'\n"
					+ "	},\n"
					+ "	{\n"
					+ "		name: 'data1',\n"
					+ "		start: 0x56,\n"
					+ "		end: 0x59,\n"
					+ "		description: 'Length of remaining data inside the packet',\n"
					+ "		color: '#ffc000'\n"
					+ "	},\n"
					+ "	{\n"
					+ "		name: 'data2',\n"
					+ "		start: 0x5A,\n"
					+ "		end: 0x61,\n"
					+ "		description: 'Unknown',\n"
					+ "		color: '#ffff00'\n"
					+ "	},\n"
					+ "	{\n"
					+ "		name: 'data3',\n"
					+ "		start: 0x62,\n"
					+ "		end: 0x65,\n"
					+ "		description: 'Date & Time when packet was generated [since 01.01.1970]',\n"
					+ "		color: '#00afef'\n"
					+ "	},\n"
					+ "	{\n"
					+ "		name: 'data4',\n"
					+ "		start: 0x66,\n"
					+ "		end: 0x75,\n"
					+ "		description: 'Unicode Account name with leading length', \n"
					+ "		color: '#ff66cc'\n" + "	}\n" + "]\n";

			Analysis analysis = new AnalysisConverter()
					.fromJson(new JSONObject(json));
			packet.getAnalyses().add(analysis);

			em.persist(packet);

		} catch (JSONException e) {
			throw new ServletException(e.getMessage(), e);
		} finally {
			em.close();
		}

		resp.setContentType("text/plain");
		resp.getWriter().println("Hello, world");
	}
}
