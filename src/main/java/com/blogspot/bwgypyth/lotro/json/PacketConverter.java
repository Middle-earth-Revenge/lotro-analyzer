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

import javax.persistence.EntityManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.blogspot.bwgypyth.lotro.EMF;
import com.blogspot.bwgypyth.lotro.model.Analysis;
import com.blogspot.bwgypyth.lotro.model.Packet;
import com.blogspot.bwgypyth.lotro.model.PacketGroup;

public class PacketConverter extends AbstractConverter<Packet> {

	@Override
	public Packet fromJson(JSONObject jsonObject) throws JSONException {
		AnalysisConverter analysisConverter = new AnalysisConverter();

		Packet entity = new Packet();
		keyFromJson(jsonObject, entity);
		entity.setData(jsonObject.getString("data"));
		entity.setName(jsonObject.getString("name"));
		if (jsonObject.has("group")) {
			PacketGroup group = new PacketGroup();
			group.setKey(jsonObject.getInt("group"));
			entity.setGroup(group);
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
		AnalysisConverter analysisConverter = new AnalysisConverter();

		JSONObject jsonObject = new JSONObject();
		keyToJson(jsonObject, entity);
		jsonObject.put("data", entity.getData());
		jsonObject.put("name", entity.getName());
		if (entity.getGroup() != null) {
			jsonObject.put("group", entity.getGroup().getKey());
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
		if (jsonObject.has("key")) {
			entity.setKey(jsonObject.getInt("key"));
		}
	}

}
