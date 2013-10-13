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

import com.blogspot.bwgypyth.lotro.model.AnalysisEntry;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.common.base.Strings;

public class AnalysisEntryConverter extends AbstractConverter<AnalysisEntry> {

	@Override
	public AnalysisEntry fromJson(JSONObject jsonObject) throws JSONException {

		AnalysisEntry entity = new AnalysisEntry();
		keyFromJson(jsonObject, entity);
		entity.setName(jsonObject.getString("name"));
		userdataFromJson(jsonObject, entity);
		entity.setStart(jsonObject.getInt("start"));
		entity.setEnd(jsonObject.getInt("end"));
		entity.setDescription(jsonObject.getString("description"));
		if (jsonObject.has("color")) {
			entity.setColor(jsonObject.getString("color"));
		}
		if (jsonObject.has("foregroundColor")) {
			entity.setForegroundColor(jsonObject.getString("foregroundColor"));
		}

		return entity;
	}

	@Override
	public JSONObject toJson(AnalysisEntry entity) throws JSONException {

		JSONObject jsonObject = new JSONObject();
		keyToJson(jsonObject, entity);
		jsonObject.put("name", entity.getName());
		userdataToJson(jsonObject, entity);
		jsonObject.put("start", entity.getStart());
		jsonObject.put("end", entity.getEnd());
		jsonObject.put("description", entity.getDescription());
		if (!Strings.isNullOrEmpty(entity.getColor())) {
			jsonObject.put("color", entity.getColor());
		}
		if (!Strings.isNullOrEmpty(entity.getForegroundColor())) {
			jsonObject.put("foregroundColor", entity.getForegroundColor());
		}

		return jsonObject;
	}

	protected void keyFromJson(JSONObject jsonObject, AnalysisEntry entity)
			throws JSONException {
		if (jsonObject.has("key")) {
			entity.setKey(KeyFactory.createKey(KeyFactory.createKey(
					KeyFactory.createKey("Packet",
							jsonObject.getLong("parent_parent_key")),
					"Analysis", jsonObject.getLong("parent_key")),
					"AnalysisEntry", jsonObject.getLong("key")));
		}
	}
}
