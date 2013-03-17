package com.blogspot.bwgypyth.lotro.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

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
}
