package com.blogspot.bwgypyth.lotro.json;

import com.blogspot.bwgypyth.lotro.model.Analysis;
import com.blogspot.bwgypyth.lotro.model.AnalysisEntry;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class AnalysisConverter extends AbstractConverter<Analysis> {

	public AnalysisConverter(IncludeUserdata includeUserdata) {
		super(includeUserdata);
	}

	@Override
	public Analysis fromJson(JSONObject jsonObject) throws JSONException {
		AnalysisEntryConverter analysisEntryConverter = new AnalysisEntryConverter(
				includeUserdata);

		Analysis entity = new Analysis();
		if (jsonObject.has("key")) {
			entity.setKey(KeyFactory.createKey("Analysis",
					jsonObject.getLong("key")));
		}
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
		AnalysisEntryConverter analysisEntryConverter = new AnalysisEntryConverter(
				includeUserdata);

		JSONObject jsonObject = new JSONObject();
		if (entity.getKey() != null) {
			jsonObject.put("key", entity.getKey().getId());
		} else {
			jsonObject.put("key", "");
		}
		jsonObject.put("name", entity.getName());
		userdataToJson(jsonObject, entity);
		JSONArray jsonArray = new JSONArray();
		for (AnalysisEntry analysisEntry : entity.getAnalysisEntries()) {
			jsonArray.put(analysisEntryConverter.toJson(analysisEntry));
		}
		jsonObject.put("analysisEntries", jsonArray);

		return jsonObject;
	}

}
