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
		return analysis;
	}
}
