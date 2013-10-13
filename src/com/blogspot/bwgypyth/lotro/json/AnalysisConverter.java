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

import com.blogspot.bwgypyth.lotro.model.Analysis;
import com.blogspot.bwgypyth.lotro.model.AnalysisEntry;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class AnalysisConverter extends AbstractConverter<Analysis> {

	@Override
	public Analysis fromJson(JSONObject jsonObject) throws JSONException {
		AnalysisEntryConverter analysisEntryConverter = new AnalysisEntryConverter();

		Analysis entity = new Analysis();
		keyFromJson(jsonObject, entity);
		entity.setName(jsonObject.getString("name"));
		userdataFromJson(jsonObject, entity);
		JSONArray jsonArray = jsonObject.getJSONArray("analysisEntries");
		for (int i = 0; i < jsonArray.length(); i++) {
			AnalysisEntry entry = analysisEntryConverter.fromJson(jsonArray
					.getJSONObject(i));
			entry.setAnalysis(entity);
			entity.getAnalysisEntries().add(entry);
		}

		return entity;
	}

	@Override
	public JSONObject toJson(Analysis entity) throws JSONException {
		AnalysisEntryConverter analysisEntryConverter = new AnalysisEntryConverter();

		JSONObject jsonObject = new JSONObject();
		keyToJson(jsonObject, entity);
		jsonObject.put("name", entity.getName());
		userdataToJson(jsonObject, entity);
		JSONArray jsonArray = new JSONArray();
		for (AnalysisEntry analysisEntry : entity.getAnalysisEntries()) {
			jsonArray.put(analysisEntryConverter.toJson(analysisEntry));
		}
		jsonObject.put("analysisEntries", jsonArray);

		return jsonObject;
	}

	protected void keyFromJson(JSONObject jsonObject, Analysis entity)
			throws JSONException {
		if (jsonObject.has("key")) {
			entity.setKey(KeyFactory.createKey(
					KeyFactory.createKey("Packet",
							jsonObject.getLong("parent_key")), "Analysis",
					jsonObject.getLong("key")));
		}
	}

}
