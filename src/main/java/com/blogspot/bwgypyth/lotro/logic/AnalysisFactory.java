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
package com.blogspot.bwgypyth.lotro.logic;

import com.blogspot.bwgypyth.lotro.model.Analysis;
import com.blogspot.bwgypyth.lotro.model.AnalysisEntry;
import com.blogspot.bwgypyth.lotro.model.OwnedEntity;
import com.blogspot.bwgypyth.lotro.model.Packet;

public class AnalysisFactory {

	public static Analysis createAnalysis(Packet packet) {
		Analysis analysis = new Analysis();
		analysis.setName("Unnamed Analysis");
		OwnedEntity.setCreated(analysis, "anonymous");
		OwnedEntity.setModified(analysis, "anonymous");
		packet.getAnalyses().add(analysis);
		analysis.setPacket(packet);

		/*addHeaderEntry(analysis);
		addCommunicationIdEntry(analysis);
		addSizeEntry(analysis);
		addRootCommandEntry(analysis);
		int offset = addOffsetEntry(analysis);
		addPacketCount(analysis);
		addDataEntry(analysis);
		addSubcommand(analysis, offset);*/
		return analysis;
	}

	private static void addHeaderEntry(Analysis analysis) {
		AnalysisEntry analysisEntry = new AnalysisEntry();
		analysisEntry.setName("header");
		analysisEntry.setStart(0x00);
		analysisEntry.setEnd(0x13);
		analysisEntry.setColor("#000000");
		analysisEntry.setForegroundColor("#ffffff");
		analysisEntry.setDescription("<b>Header</b>");
		analysisEntry.setAnalysis(analysis);
		OwnedEntity.setCreated(analysisEntry, "anonymous");
		OwnedEntity.setModified(analysisEntry, "anonymous");
		analysis.getAnalysisEntries().add(analysisEntry);
	}

	private static void addCommunicationIdEntry(Analysis analysis) {
		AnalysisEntry analysisEntry = new AnalysisEntry();
		analysisEntry.setName("header_communicationid");
		analysisEntry.setStart(0x00);
		analysisEntry.setEnd(0x01);
		analysisEntry.setColor("#8db3e1");
		analysisEntry.setDescription("Communication ID");
		analysisEntry.setAnalysis(analysis);
		OwnedEntity.setCreated(analysisEntry, "anonymous");
		OwnedEntity.setModified(analysisEntry, "anonymous");
		analysis.getAnalysisEntries().add(analysisEntry);
	}

	private static void addSizeEntry(Analysis analysis) {
		AnalysisEntry analysisEntry = new AnalysisEntry();
		analysisEntry.setName("header_size");
		analysisEntry.setStart(0x02);
		analysisEntry.setEnd(0x03);
		analysisEntry.setColor("#FF66CC");
		analysisEntry.setDescription("Size of payload data");
		analysisEntry.setAnalysis(analysis);
		OwnedEntity.setCreated(analysisEntry, "anonymous");
		OwnedEntity.setModified(analysisEntry, "anonymous");
		analysis.getAnalysisEntries().add(analysisEntry);
	}

	private static void addRootCommandEntry(Analysis analysis) {
		AnalysisEntry analysisEntry = new AnalysisEntry();
		analysisEntry.setName("header_rootcommand");
		analysisEntry.setStart(0x04);
		analysisEntry.setEnd(0x07);
		analysisEntry.setColor("#66FF33");
		analysisEntry.setDescription("Root command");
		analysisEntry.setAnalysis(analysis);
		OwnedEntity.setCreated(analysisEntry, "anonymous");
		OwnedEntity.setModified(analysisEntry, "anonymous");
		analysis.getAnalysisEntries().add(analysisEntry);
	}

	private static int addOffsetEntry(Analysis analysis) {
		String byte5 = analysis.getPacket().getData().substring(8, 10);
		switch (byte5) {
		case "08": {
			{
				AnalysisEntry analysisEntry = new AnalysisEntry();
				analysisEntry.setName("data_offset");
				analysisEntry.setStart(0x14);
				analysisEntry.setEnd(0x19);
				analysisEntry.setColor("#FFFF00");
				analysisEntry.setDescription("Padding (see 0x04)");
				analysisEntry.setAnalysis(analysis);
				OwnedEntity.setCreated(analysisEntry, "anonymous");
				OwnedEntity.setModified(analysisEntry, "anonymous");
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
				OwnedEntity.setCreated(analysisEntry, "anonymous");
				OwnedEntity.setModified(analysisEntry, "anonymous");
				analysis.getAnalysisEntries().add(analysisEntry);
			}
			return 0x6;
		}
		case "0B": {
			{
				AnalysisEntry analysisEntry = new AnalysisEntry();
				analysisEntry.setName("data_offset");
				analysisEntry.setStart(0x14);
				analysisEntry.setEnd(0x25);
				analysisEntry.setColor("#FFFF00");
				analysisEntry.setDescription("Padding (see 0x04)");
				analysisEntry.setAnalysis(analysis);
				OwnedEntity.setCreated(analysisEntry, "anonymous");
				OwnedEntity.setModified(analysisEntry, "anonymous");
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
				OwnedEntity.setCreated(analysisEntry, "anonymous");
				OwnedEntity.setModified(analysisEntry, "anonymous");
				analysis.getAnalysisEntries().add(analysisEntry);
			}
			return 0x12;
		}
		default:
			return 0x0;
		}
	}

	private static void addPacketCount(Analysis analysis) {
		AnalysisEntry analysisEntry = new AnalysisEntry();
		analysisEntry.setName("header_packetcount");
		analysisEntry.setStart(0x08);
		analysisEntry.setEnd(0x0b);
		analysisEntry.setColor("#79109a");
		analysisEntry
				.setDescription("Number of all packets sent in this session");
		analysisEntry.setAnalysis(analysis);
		OwnedEntity.setCreated(analysisEntry, "anonymous");
		OwnedEntity.setModified(analysisEntry, "anonymous");
		analysis.getAnalysisEntries().add(analysisEntry);
	}

	private static void addDataEntry(Analysis analysis) {
		AnalysisEntry analysisEntry = new AnalysisEntry();
		analysisEntry.setName("data");
		analysisEntry.setStart(0x14);
		analysisEntry.setEnd((analysis.getPacket().getData().length() / 2) - 1);
		analysisEntry.setColor("#777777");
		analysisEntry.setDescription("<b>Data</b>");
		analysisEntry.setAnalysis(analysis);
		OwnedEntity.setCreated(analysisEntry, "anonymous");
		OwnedEntity.setModified(analysisEntry, "anonymous");
		analysis.getAnalysisEntries().add(analysisEntry);
	}

	private static void addSubcommand(Analysis analysis, int offset) {
		AnalysisEntry analysisEntry = new AnalysisEntry();
		analysisEntry.setName("data_subcommand");
		analysisEntry.setStart(0x14 + offset);
		analysisEntry.setEnd(0x14 + offset + 2);
		analysisEntry.setColor("#ff0000");
		analysisEntry.setDescription("Subcommand");
		analysisEntry.setAnalysis(analysis);
		OwnedEntity.setCreated(analysisEntry, "anonymous");
		OwnedEntity.setModified(analysisEntry, "anonymous");
		analysis.getAnalysisEntries().add(analysisEntry);
	}

}
