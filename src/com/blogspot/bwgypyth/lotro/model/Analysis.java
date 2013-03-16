package com.blogspot.bwgypyth.lotro.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.OrderBy;

import com.google.appengine.api.datastore.Key;

@Entity
public class Analysis extends OwnedEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key key;
	@ManyToOne(fetch = FetchType.LAZY)
	private Packet packet;
	private String name;
	@OneToMany(mappedBy = "analysis", cascade = CascadeType.ALL)
	@OrderBy("start asc, end desc")
	private List<AnalysisEntry> analysisEntries = new ArrayList<>(0);

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

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
