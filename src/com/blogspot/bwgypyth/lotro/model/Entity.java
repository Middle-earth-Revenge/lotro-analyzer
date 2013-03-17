package com.blogspot.bwgypyth.lotro.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

import com.google.appengine.api.datastore.Key;

@MappedSuperclass
public class Entity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Key key;

	public Key getKey() {
		return key;
	}

	public void setKey(Key key) {
		this.key = key;
	}

}
