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
import javax.persistence.EntityManager;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import com.blogspot.bwgypyth.lotro.EMF;
import com.google.appengine.api.datastore.Key;

@Entity
public class PacketGroup extends OwnedEntity {

	private String name;
	private String description;

	@OneToMany(mappedBy = "groupKey", cascade = CascadeType.ALL)
	private List<Key> packetKeys = new ArrayList<>(0);
	@Transient
	private List<Packet> packets = null;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public List<Key> getPacketKeys() {
		return packetKeys;
	}

	public void setPacketKeys(List<Key> packetKeys) {
		this.packets = null;
		this.packetKeys = packetKeys;
	}

	@SuppressWarnings("unchecked")
	public List<Packet> getPackets() {
		if (packets == null) {
			if (packetKeys.isEmpty()) {
				packets = new ArrayList<>(0);
			} else {
				EntityManager em = EMF.get().createEntityManager();
				try {
					packets = em
							.createQuery(
									"select packet form Packet packet where packet.key in (:keys)")
							.setParameter("keys", packetKeys).getResultList();
				} finally {
					em.close();
				}
			}
		}
		return packets;
	}
}
