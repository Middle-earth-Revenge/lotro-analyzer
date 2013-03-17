package com.blogspot.bwgypyth.lotro.json;

import junit.framework.Assert;

import org.junit.Before;
import org.junit.Test;

import com.blogspot.bwgypyth.lotro.model.AnalysisEntry;
import com.google.appengine.labs.repackaged.org.json.JSONException;
import com.google.appengine.labs.repackaged.org.json.JSONObject;

public class AnalysisEntryConverterTest {

	private AnalysisEntryConverter converter;

	@Before
	public void before() {
		converter = new AnalysisEntryConverter(IncludeUserdata.INCLUDE_NONE,
				IncludeKey.INCLUDE_NONE);
	}

	@Test
	public void testFromJsonComplete() throws JSONException {
		String json = "{\n" + "name: 'header',\n" + "start: 0x00,\n"
				+ "end: 0x15,\n" + "description: 'Payload Header part',\n"
				+ "color: '#000000',\n" + "foregroundColor: '#ffffff'\n" + "}";
		AnalysisEntry analysisEntry = converter.fromJson(new JSONObject(json));
		Assert.assertEquals("header", analysisEntry.getName());
		Assert.assertEquals(0x00, analysisEntry.getStart());
		Assert.assertEquals(0x15, analysisEntry.getEnd());
		Assert.assertEquals("Payload Header part",
				analysisEntry.getDescription());
		Assert.assertEquals("#000000", analysisEntry.getColor());
		Assert.assertEquals("#ffffff", analysisEntry.getForegroundColor());
	}

	@Test
	public void testFromJsonNoColors() throws JSONException {
		String json = "{\n" + "name: 'header'," + "start: 0x00," + "end: 0x15,"
				+ "description: 'Payload Header part',\n" + "}";
		AnalysisEntry analysisEntry = converter.fromJson(new JSONObject(json));
		Assert.assertEquals("header", analysisEntry.getName());
		Assert.assertEquals(0x00, analysisEntry.getStart());
		Assert.assertEquals(0x15, analysisEntry.getEnd());
		Assert.assertEquals("Payload Header part",
				analysisEntry.getDescription());
		Assert.assertNull(analysisEntry.getColor());
		Assert.assertNull(analysisEntry.getForegroundColor());
	}

	@Test
	public void testToJson() throws JSONException {
		String jsonIn = "{\n" + "name: 'header',\n" + "start: 0x00,\n"
				+ "end: 0x15,\n" + "description: 'Payload Header part',\n"
				+ "color: '#000000',\n" + "foregroundColor: '#ffffff'\n" + "}";
		AnalysisEntry analysisEntry = converter
				.fromJson(new JSONObject(jsonIn));
		JSONObject json = converter.toJson(analysisEntry);
		Assert.assertEquals("{\n" + " \"color\": \"#000000\",\n"
				+ " \"start\": 0,\n"
				+ " \"description\": \"Payload Header part\",\n"
				+ " \"name\": \"header\",\n"
				+ " \"foregroundColor\": \"#ffffff\",\n" + " \"end\": 21\n"
				+ "}", json.toString(1));
	}
}
