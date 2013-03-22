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
package com.blogspot.bwgypyth.lotro.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import com.google.appengine.api.users.User;

@Entity
public class Analysis extends OwnedEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	private Packet packet;
	private String name;
	@OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL)
	@OrderBy("start asc, end desc")
	private List<AnalysisEntry> analysisEntries = new ArrayList<>(0);

	public Packet getPacket() {
		return packet;
	}

	public void setPacket(Packet packet) {
		this.packet = packet;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<AnalysisEntry> getAnalysisEntries() {
		return analysisEntries;
	}

	public void setAnalysisEntries(List<AnalysisEntry> analysisEntries) {
		this.analysisEntries = analysisEntries;
	}

	public static Analysis createStubAnalysis(Packet packet, User user) {
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
