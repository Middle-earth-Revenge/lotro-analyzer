package com.blogspot.bwgypyth.lotro.json;

import com.blogspot.bwgypyth.lotro.model.Analysis;
import com.blogspot.bwgypyth.lotro.model.AnalysisEntry;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class AnalysisConverter extends AbstractConverter<Analysis> {

	@Override
	public Analysis fromJson(JSONObject jsonObject) throws JSONException {
		AnalysisEntryConverter analysisEntryConverter = new AnalysisEntryConverter();

		Analysis analysis = new Analysis();
		analysis.setName(jsonObject.getString("name"));
		JSONArray jsonArray = jsonObject.getJSONArray("analysisEntries");
		for (int i = 0; i < jsonArray.length(); i++) {
			AnalysisEntry entry = analysisEntryConverter.fromJson(jsonArray
					.getJSONObject(i));
			entry.setAnalysis(analysis);
			analysis.getAnalysisEntries().add(entry);
		}

		return analysis;
	}

	@Override
	public JSONObject toJson(Analysis analysis) throws JSONException {
		AnalysisEntryConverter analysisEntryConverter = new AnalysisEntryConverter();

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("name", analysis.getName());
		JSONArray jsonArray = new JSONArray();
		for (AnalysisEntry analysisEntry : analysis.getAnalysisEntries()) {
			jsonArray.put(analysisEntryConverter.toJson(analysisEntry));
		}
		jsonObject.put("analysisEntries", jsonArray);

		return jsonObject;
	}

}
