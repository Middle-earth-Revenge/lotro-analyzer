package com.blogspot.bwgypyth.lotro.json;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.blogspot.bwgypyth.lotro.model.Packet;
import com.blogspot.bwgypyth.lotro.model.PacketGroup;

public class PacketGroupConverter extends AbstractConverter<PacketGroup> {

	@Override
	public PacketGroup fromJson(JSONObject jsonObject) throws JSONException {
		PacketConverter packetConverter = new PacketConverter();

		PacketGroup entity = new PacketGroup();
		keyFromJson(jsonObject, entity);
		entity.setName(jsonObject.getString("name"));
		entity.setDescription(jsonObject.getString("description"));
		userdataFromJson(jsonObject, entity);
		JSONArray jsonArray = jsonObject.getJSONArray("packets");
		for (int i = 0; i < jsonArray.length(); i++) {
			Packet packet = packetConverter
					.fromJson(jsonArray.getJSONObject(i));
			packet.setGroup(entity);
		}

		return entity;
	}

	@Override
	public JSONObject toJson(PacketGroup entity) throws JSONException {
		PacketConverter packetConverter = new PacketConverter();

		JSONObject jsonObject = new JSONObject();
		keyToJson(jsonObject, entity);
		jsonObject.put("name", entity.getName());
		jsonObject.put("description", entity.getDescription());
		userdataToJson(jsonObject, entity);
		JSONArray jsonArray = new JSONArray();
		for (Packet packets : entity.getPackets()) {
			jsonArray.put(packetConverter.toJson(packets));
		}
		jsonObject.put("packets", jsonArray);

		return jsonObject;
	}

	protected void keyFromJson(JSONObject jsonObject, PacketGroup entity)
			throws JSONException {
		if (jsonObject.has("key")) {
			entity.setKey(jsonObject.getInt("key"));
		}
	}
}
