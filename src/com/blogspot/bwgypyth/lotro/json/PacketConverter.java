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
import com.blogspot.bwgypyth.lotro.model.Packet;
import com.blogspot.bwgypyth.lotro.model.PacketGroup;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class PacketConverter extends AbstractConverter<Packet> {

	public PacketConverter(IncludeUserdata includeUserdata,
			IncludeKey includeKey) {
		super(includeUserdata, includeKey);
	}

	@Override
	public Packet fromJson(JSONObject jsonObject) throws JSONException {
		AnalysisConverter analysisConverter = new AnalysisConverter(
				includeUserdata, includeKey);

		Packet entity = new Packet();
		keyFromJson(jsonObject, entity);
		entity.setData(jsonObject.getString("data"));
		entity.setName(jsonObject.getString("name"));
		if (jsonObject.has("group")) {
			PacketGroup group = new PacketGroup();
			group.setKey(KeyFactory.createKey("PacketGroup",
					jsonObject.getLong("group")));
			entity.setGroupKey(group.getKey());
		}
		userdataFromJson(jsonObject, entity);
		JSONArray jsonArray = jsonObject.getJSONArray("analyses");
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
		AnalysisConverter analysisConverter = new AnalysisConverter(
				includeUserdata, includeKey);

		JSONObject jsonObject = new JSONObject();
		keyToJson(jsonObject, entity);
		jsonObject.put("data", entity.getData());
		jsonObject.put("name", entity.getName());
		if (entity.getGroup() != null) {
			jsonObject.put("group", entity.getGroup().getKey().getId());
		}
		userdataToJson(jsonObject, entity);
		JSONArray jsonArray = new JSONArray();
		for (Analysis analysis : entity.getAnalyses()) {
			jsonArray.put(analysisConverter.toJson(analysis));
		}
		jsonObject.put("analyses", jsonArray);

		return jsonObject;
	}

	protected void keyFromJson(JSONObject jsonObject, Packet entity)
			throws JSONException {
		switch (includeKey) {
		case INCLUDE_ALL:
			if (jsonObject.has("key")) {
				entity.setKey(KeyFactory.createKey(
						Packet.class.getSimpleName(), jsonObject.getLong("key")));
			}
			break;
		default:
		case INCLUDE_NONE:
			break;
		}
	}

}
