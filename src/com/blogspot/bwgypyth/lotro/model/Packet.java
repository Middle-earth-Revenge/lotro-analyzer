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
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityManager;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.blogspot.bwgypyth.lotro.EMF;
import com.google.appengine.api.datastore.Key;
import com.google.appengine.api.datastore.Text;
import com.google.appengine.datanucleus.annotations.Unowned;

@Entity
public class Packet extends OwnedEntity {

	@Column(name = "data")
	private Text dataText;
	private String name;
	@ManyToOne(fetch = FetchType.LAZY)
	@Unowned
	@JoinColumn(name = "group_key")
	private Key groupKey;
	@Transient
	private PacketGroup group;
	@OneToMany(mappedBy = "packet", cascade = CascadeType.ALL)
	private List<Analysis> analyses = new ArrayList<>(0);

	public Text getDataText() {
		return dataText;
	}

	public void setDataText(Text dataText) {
		this.dataText = dataText;
	}

	@Transient
	public String getData() {
		if (this.dataText == null) {
			return null;
		}
		return this.dataText.getValue();
	}

	@Transient
	public void setData(String data) {
		this.dataText = data == null ? null : new Text(data);
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Key getGroupKey() {
		return groupKey;
	}

	public void setGroupKey(Key groupKey) {
		this.groupKey = groupKey;
	}

	public PacketGroup getGroup() {
		if (group == null) {
			if (groupKey != null) {
				EntityManager em = EMF.get().createEntityManager();
				try {
					group = (PacketGroup) em
							.createQuery(
									"select group from PacketGroup group where group.key = :key")
							.setParameter("key", groupKey).getSingleResult();
				} finally {
					em.close();
				}
			}
		}
		return group;
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
