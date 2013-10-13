package com.blogspot.bwgypyth.lotro.json;

import com.blogspot.bwgypyth.lotro.model.Packet;
import com.blogspot.bwgypyth.lotro.model.PacketGroup;
import com.google.appengine.api.datastore.KeyFactory;
import com.google.appengine.labs.repackaged.org.json.JSONArray;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

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
			packet.setGroupKey(entity.getKey());
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
			entity.setKey(KeyFactory.createKey(
					PacketGroup.class.getSimpleName(),
					jsonObject.getLong("key")));
		}
	}
}
