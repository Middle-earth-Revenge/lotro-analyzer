package com.blogspot.bwgypyth.lotro.json;

import java.util.Calendar;

import javax.xml.bind.DatatypeConverter;

import com.blogspot.bwgypyth.lotro.model.OwnedEntity;
import com.google.appengine.api.users.User;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

;

public abstract class AbstractConverter<T> {

	protected final IncludeUserdata includeUserdata;

	public AbstractConverter(IncludeUserdata includeUserdata) {
		this.includeUserdata = includeUserdata;
	}

	public abstract T fromJson(JSONObject jsonObject) throws JSONException;

	public abstract JSONObject toJson(T entity) throws JSONException;

	protected final void userdataFromJson(JSONObject jsonObject,
			OwnedEntity entity) throws JSONException {
		switch (includeUserdata) {
		case INCLUDE_ALL:
			if (jsonObject.has("created")) {
				entity.setCreated(DatatypeConverter.parseDate(
						jsonObject.getString("created")).getTime());
			}
			if (jsonObject.has("createdBy")) {
				String[] createdBy = jsonObject.getString("createdBy").split(
						"|");
				String email = createdBy[0];
				String authDomain = createdBy[1];
				entity.setCreatedBy(new User(email, authDomain));
			}
			if (jsonObject.has("modified")) {
				entity.setCreated(DatatypeConverter.parseDate(
						jsonObject.getString("modified")).getTime());
			}
			if (jsonObject.has("modifiedBy")) {
				String[] modifiedBy = jsonObject.getString("modifiedBy").split(
						"|");
				String email = modifiedBy[0];
				String authDomain = modifiedBy[1];
				entity.setModifiedBy(new User(email, authDomain));
			}
			break;
		default:
		case INCLUDE_NONE:
			break;
		}
	}

	protected final void userdataToJson(JSONObject jsonObject,
			OwnedEntity entity) throws JSONException {
		switch (includeUserdata) {
		case INCLUDE_ALL:
			if (entity.getCreated() != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(entity.getCreated());
				jsonObject.put("created", DatatypeConverter.printDate(cal));
			}
			if (entity.getCreatedBy() != null) {
				jsonObject.put("createdBy", entity.getCreatedBy().getEmail()
						+ "|" + entity.getCreatedBy().getAuthDomain());
			}
			if (entity.getModified() != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(entity.getModified());
				jsonObject.put("modified", DatatypeConverter.printDate(cal));
			}
			if (entity.getModifiedBy() != null) {
				jsonObject.put("modifiedBy", entity.getModifiedBy().getEmail()
						+ "|" + entity.getModifiedBy().getAuthDomain());
			}
			break;
		default:
		case INCLUDE_NONE:
			break;
		}
	}
}
