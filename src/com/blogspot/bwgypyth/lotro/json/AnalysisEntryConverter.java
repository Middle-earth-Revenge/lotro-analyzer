package com.blogspot.bwgypyth.lotro.json;

import com.blogspot.bwgypyth.lotro.model.AnalysisEntry;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;
import com.google.common.base.Strings;

public class AnalysisEntryConverter extends AbstractConverter<AnalysisEntry> {

	@Override
	public AnalysisEntry fromJson(JSONObject jsonObject) throws JSONException {

		AnalysisEntry entity = new AnalysisEntry();
		entity.setName(jsonObject.getString("name"));
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
		jsonObject.put("name", entity.getName());
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

}
