package com.blogspot.bwgypyth.lotro.logic;

import com.blogspot.bwgypyth.lotro.model.Analysis;
import com.blogspot.bwgypyth.lotro.model.AnalysisEntry;
import com.blogspot.bwgypyth.lotro.model.OwnedEntity;
import com.blogspot.bwgypyth.lotro.model.Packet;
import com.google.appengine.api.users.User;

public class AnalysisFactory {

	public static Analysis createAnalysis(Packet packet, User user) {
		Analysis analysis = new Analysis();
		analysis.setName("Unnamed Analysis");
		OwnedEntity.setCreated(analysis, user);
		OwnedEntity.setModified(analysis, user);
		packet.getAnalyses().add(analysis);
		analysis.setPacket(packet);

		addHeaderEntry(user, analysis);
		addCommunicationIdEntry(user, analysis);
		addSizeEntry(user, analysis);
		addRootCommandEntry(user, analysis);
		addOffsetEntry(user, analysis);
		addDataEntry(user, analysis);
		return analysis;
	}

	private static void addHeaderEntry(User user, Analysis analysis) {
		AnalysisEntry analysisEntry = new AnalysisEntry();
		analysisEntry.setName("header");
		analysisEntry.setStart(0x00);
		analysisEntry.setEnd(0x13);
		analysisEntry.setColor("#000000");
		analysisEntry.setForegroundColor("#ffffff");
		analysisEntry.setDescription("<b>Header</b>");
		analysisEntry.setAnalysis(analysis);
		OwnedEntity.setCreated(analysisEntry, user);
		OwnedEntity.setModified(analysisEntry, user);
		analysis.getAnalysisEntries().add(analysisEntry);
	}

	private static void addCommunicationIdEntry(User user, Analysis analysis) {
		AnalysisEntry analysisEntry = new AnalysisEntry();
		analysisEntry.setName("header_communicationid");
		analysisEntry.setStart(0x00);
		analysisEntry.setEnd(0x01);
		analysisEntry.setColor("#8db3e1");
		analysisEntry.setDescription("Communication ID");
		analysisEntry.setAnalysis(analysis);
		OwnedEntity.setCreated(analysisEntry, user);
		OwnedEntity.setModified(analysisEntry, user);
		analysis.getAnalysisEntries().add(analysisEntry);
	}

	private static void addSizeEntry(User user, Analysis analysis) {
		AnalysisEntry analysisEntry = new AnalysisEntry();
		analysisEntry.setName("header_size");
		analysisEntry.setStart(0x02);
		analysisEntry.setEnd(0x03);
		analysisEntry.setColor("#FF66CC");
		analysisEntry.setDescription("Size of payload data");
		analysisEntry.setAnalysis(analysis);
		OwnedEntity.setCreated(analysisEntry, user);
		OwnedEntity.setModified(analysisEntry, user);
		analysis.getAnalysisEntries().add(analysisEntry);
	}

	private static void addRootCommandEntry(User user, Analysis analysis) {
		AnalysisEntry analysisEntry = new AnalysisEntry();
		analysisEntry.setName("header_rootcommand");
		analysisEntry.setStart(0x04);
		analysisEntry.setEnd(0x07);
		analysisEntry.setColor("#66FF33");
		analysisEntry.setDescription("Root command");
		analysisEntry.setAnalysis(analysis);
		OwnedEntity.setCreated(analysisEntry, user);
		OwnedEntity.setModified(analysisEntry, user);
		analysis.getAnalysisEntries().add(analysisEntry);
	}

	private static void addOffsetEntry(User user, Analysis analysis) {
		String byte5 = analysis.getPacket().getData().substring(8, 10);
		switch (byte5) {
		case "08": {
			AnalysisEntry analysisEntry = new AnalysisEntry();
			analysisEntry.setName("data_offset");
			analysisEntry.setStart(0x14);
			analysisEntry.setEnd(0x19);
			analysisEntry.setColor("#FFFF00");
			analysisEntry.setDescription("Padding (see 0x04)");
			analysisEntry.setAnalysis(analysis);
			OwnedEntity.setCreated(analysisEntry, user);
			OwnedEntity.setModified(analysisEntry, user);
			analysis.getAnalysisEntries().add(analysisEntry);
		}
			{
				AnalysisEntry analysisEntry = new AnalysisEntry();
				analysisEntry.setName("header_offset");
				analysisEntry.setStart(0x04);
				analysisEntry.setEnd(0x04);
				analysisEntry.setColor("#FFFF00");
				analysisEntry.setDescription("Padding (see 0x14 to 0x19)");
				analysisEntry.setAnalysis(analysis);
				OwnedEntity.setCreated(analysisEntry, user);
				OwnedEntity.setModified(analysisEntry, user);
				analysis.getAnalysisEntries().add(analysisEntry);
			}
			break;
		case "0B": {
			AnalysisEntry analysisEntry = new AnalysisEntry();
			analysisEntry.setName("data_offset");
			analysisEntry.setStart(0x14);
			analysisEntry.setEnd(0x25);
			analysisEntry.setColor("#FFFF00");
			analysisEntry.setDescription("Padding (see 0x04)");
			analysisEntry.setAnalysis(analysis);
			OwnedEntity.setCreated(analysisEntry, user);
			OwnedEntity.setModified(analysisEntry, user);
			analysis.getAnalysisEntries().add(analysisEntry);
		}
			{
				AnalysisEntry analysisEntry = new AnalysisEntry();
				analysisEntry.setName("header_offset");
				analysisEntry.setStart(0x04);
				analysisEntry.setEnd(0x04);
				analysisEntry.setColor("#FFFF00");
				analysisEntry.setDescription("Padding (see 0x14 to 0x25)");
				analysisEntry.setAnalysis(analysis);
				OwnedEntity.setCreated(analysisEntry, user);
				OwnedEntity.setModified(analysisEntry, user);
				analysis.getAnalysisEntries().add(analysisEntry);
			}
			break;
		default:
			break;
		}
	}

	private static void addDataEntry(User user, Analysis analysis) {
		AnalysisEntry analysisEntry = new AnalysisEntry();
		analysisEntry.setName("data");
		analysisEntry.setStart(0x14);
		analysisEntry.setEnd((analysis.getPacket().getData().length() / 2) - 1);
		analysisEntry.setColor("#777777");
		analysisEntry.setDescription("<b>Data</b>");
		analysisEntry.setAnalysis(analysis);
		OwnedEntity.setCreated(analysisEntry, user);
		OwnedEntity.setModified(analysisEntry, user);
		analysis.getAnalysisEntries().add(analysisEntry);
	}

}
