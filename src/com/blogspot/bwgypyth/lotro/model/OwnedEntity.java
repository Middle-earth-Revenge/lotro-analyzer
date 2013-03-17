package com.blogspot.bwgypyth.lotro.model;

import java.util.Date;

import javax.persistence.MappedSuperclass;

import com.google.appengine.api.users.User;

@MappedSuperclass
public abstract class OwnedEntity extends Entity {

	private User createdBy;
	private Date created;
	private User modifiedBy;
	private Date modified;

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Date getCreated() {
		return created;
	}

	public void setCreated(Date created) {
		this.created = created;
	}

	public User getModifiedBy() {
		return modifiedBy;
	}

	public void setModifiedBy(User modifiedBy) {
		this.modifiedBy = modifiedBy;
	}

	public Date getModified() {
		return modified;
	}

	public void setModified(Date modified) {
		this.modified = modified;
	}

	public static void setModified(OwnedEntity entity, User user) {
		entity.setModified(new Date());
		entity.setModifiedBy(user);
	}

	public static void setCreated(OwnedEntity entity, User user) {
		entity.setCreated(new Date());
		entity.setCreatedBy(user);
	}

}
