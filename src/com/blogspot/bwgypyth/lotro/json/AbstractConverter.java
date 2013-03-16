package com.blogspot.bwgypyth.lotro.json;

import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public abstract class AbstractConverter<T> {

	public abstract T fromJson(JSONObject jsonObject) throws JSONException;

	public abstract JSONObject toJson(T entity) throws JSONException;

}
