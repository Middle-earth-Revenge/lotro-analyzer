package com.blogspot.bwgypyth.lotro.json;

import org.junit.Before;
import org.junit.Test;

import com.blogspot.bwgypyth.lotro.model.Analysis;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class AnalysisConverterTest {
	private AnalysisConverter converter;

	@Before
	public void before() {
		converter = new AnalysisConverter();
	}

	@Test
	public void testFromJson() throws JSONException {
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
				+ "		color: '#ff66cc'\n" + "	}\n" + "]";
		Analysis analysis = converter.fromJson(new JSONObject(json));
		// System.out.println(analysis);
	}

	@Test
	public void testToJson() throws JSONException {
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
				+ "		color: '#ff66cc'\n" + "	}\n" + "]";
		Analysis analysis = converter.fromJson(new JSONObject(json));
		System.out.println(converter.toJson(analysis));
	}
}