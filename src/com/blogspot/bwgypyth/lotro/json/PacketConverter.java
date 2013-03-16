package com.blogspot.bwgypyth.lotro.json;

import com.blogspot.bwgypyth.lotro.model.Analysis;
import com.blogspot.bwgypyth.lotro.model.Packet;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class PacketConverter extends AbstractConverter<Packet> {

	@Override
	public Packet fromJson(JSONObject jsonObject) throws JSONException {
		AnalysisConverter analysisConverter = new AnalysisConverter();

		Packet entity = new Packet();
		entity.setKey(KeyFactory.createKey("Packet", jsonObject.getLong("key")));
		entity.setData(jsonObject.getString("data"));
		entity.setName(jsonObject.getString("name"));
		JSONArray jsonArray = new JSONArray(jsonObject.get("analyses"));
		for (int i = 0; i < jsonArray.length(); i++) {
			Analysis analysis = analysisConverter.fromJson(jsonArray
					.getJSONObject(i));
			analysis.setPacket(entity);
			entity.getAnalyses().add(analysis);
		}

		return entity;
	}

	@Override
	public JSONObject toJson(Packet entity) throws JSONException {
		AnalysisConverter analysisConverter = new AnalysisConverter();

		JSONObject jsonObject = new JSONObject();
		jsonObject.put("key", entity.getKey().getId());
		jsonObject.put("data", entity.getData());
		jsonObject.put("name", entity.getName());
		JSONArray jsonArray = new JSONArray();
		for (Analysis analysis : entity.getAnalyses()) {
			jsonArray.put(analysisConverter.toJson(analysis));
		}
		jsonObject.put("analyses", jsonArray);

		return jsonObject;
	}

}
