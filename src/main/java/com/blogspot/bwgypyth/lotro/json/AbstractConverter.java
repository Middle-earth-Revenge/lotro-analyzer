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
package com.blogspot.bwgypyth.lotro.json;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.xml.bind.DatatypeConverter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.blogspot.bwgypyth.lotro.model.OwnedEntity;

/**
 * Base converter for the {@link OwnedEntity} classes. Build the necessary
 * foundation to convert them from/to JSON.
 * 
 * @param <T>
 *            class to be converted
 */
public abstract class AbstractConverter<T extends OwnedEntity> {

	private final boolean isAdmin;

	public AbstractConverter() {
		isAdmin = true;
	}

	/**
	 * Read a JSON object into an entity.
	 * 
	 * @param jsonObject
	 *            JSON object to read
	 * @return the converted object
	 * @throws JSONException
	 *             thrown when the conversion failed
	 */
	public abstract T fromJson(JSONObject jsonObject) throws JSONException;

	public final List<T> fromJson(JSONArray jsonArray) throws JSONException {
		List<T> retval = new ArrayList<>(jsonArray.length());
		for (int i = 0; i < jsonArray.length(); i++) {
			retval.add(fromJson(jsonArray.getJSONObject(i)));
		}
		return retval;
	}

	/**
	 * Convert an entity into a JSON object.
	 * 
	 * @param entity
	 *            entity to convert
	 * @return an JSON object matching the entity
	 * @throws JSONException
	 *             thrown when the conversion failed
	 */
	public abstract JSONObject toJson(T entity) throws JSONException;

	public final JSONArray toJson(List<T> entities) throws JSONException {
		JSONArray jsonArray = new JSONArray();
		for (T entity : entities) {
			jsonArray.put(toJson(entity));
		}
		return jsonArray;
	}

	/**
	 * If converter was constucted using {@link IncludeUserdata#INCLUDE_ALL}
	 * will add userdata (if available) from the JSON object to the entity.
	 * 
	 * @param jsonObject
	 *            JSON object to read
	 * @param entity
	 *            the target entity object
	 * @throws JSONException
	 *             thrown when the conversion failed
	 */
	protected static final void userdataFromJson(JSONObject jsonObject,
			OwnedEntity entity) throws JSONException {
		if (jsonObject.has("created")) {
			entity.setCreated(DatatypeConverter.parseDate(
					jsonObject.getString("created")).getTime());
		}
		if (jsonObject.has("createdBy")) {
			String createdBy = jsonObject.getString("createdBy");
			entity.setCreatedBy(createdBy);
		}
		if (jsonObject.has("modified")) {
			entity.setModified(DatatypeConverter.parseDate(
					jsonObject.getString("modified")).getTime());
		}
		if (jsonObject.has("modifiedBy")) {
			String modifiedBy = jsonObject.getString("modifiedBy");
			entity.setModifiedBy(modifiedBy);
		}
	}

	protected abstract void keyFromJson(JSONObject jsonObject, T entity)
			throws JSONException;

	/**
	 * Copies the userdata into the JSON object if constructed using
	 * {@link IncludeUserdata#INCLUDE_ALL}.
	 * 
	 * @param jsonObject
	 *            the target JSON object
	 * @param entity
	 *            the entity containing user data
	 * @throws JSONException
	 *             thrown when the conversion failed
	 */
	protected final void userdataToJson(JSONObject jsonObject,
			OwnedEntity entity) throws JSONException {
		if (isAdmin) {
			if (entity.getCreated() != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(entity.getCreated());
				jsonObject.put("created", DatatypeConverter.printDate(cal));
			}
			if (entity.getCreatedBy() != null) {
				jsonObject.put("createdBy", entity.getCreatedBy());
			}
			if (entity.getModified() != null) {
				Calendar cal = Calendar.getInstance();
				cal.setTime(entity.getModified());
				jsonObject.put("modified", DatatypeConverter.printDate(cal));
			}
			if (entity.getModifiedBy() != null) {
				jsonObject.put("modifiedBy", entity.getModifiedBy());
			}
		}
	}

	protected static final void keyToJson(JSONObject jsonObject,
			OwnedEntity entity) throws JSONException {
		if (entity.getKey() != null) {
			jsonObject.put("key", entity.getKey());
		} else {
			jsonObject.put("key", "");
		}
	}
}
