package com.blogspot.bwgypyth.lotro.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

@Entity
public class Packet extends OwnedEntity {

	private String data;
	private String name;
	@OneToMany(mappedBy = "packet", cascade = CascadeType.ALL)
	private List<Analysis> analyses = new ArrayList<>(0);

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public List<Analysis> getAnalyses() {
		return analyses;
	}

	@Transient
	public int getAnalysesSize() {
		return getAnalyses().size();
	}

	public void setAnalyses(List<Analysis> analyses) {
		this.analyses = analyses;
	}

}
