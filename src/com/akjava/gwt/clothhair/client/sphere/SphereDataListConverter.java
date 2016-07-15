package com.akjava.gwt.clothhair.client.sphere;

import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.google.common.base.Converter;
import com.google.common.collect.Lists;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class SphereDataListConverter extends Converter<List<SphereData>,String>{

	@Override
	protected String doForward(List<SphereData> list) {
		List<JSONObject> objects=
				Lists.newArrayList(new SphereDataConverter().convertAll(list));
		
		
		JSONObject root=new JSONObject();
		root.put("type", new JSONString(SphereData.DATA_TYPE));
		root.put("version", new JSONNumber(1.0));
		
		JSONArray array=new JSONArray();
		for(int i=0;i<objects.size();i++){
			array.set(i, objects.get(i));
		}
		
		root.put("datas", array);
		
		return root.toString();
	}

	@Override
	protected List<SphereData> doBackward(String json) {

		JSONValue value=JSONParser.parseStrict(json);
		if(value==null){
			LogUtils.log("SphereDataListConverter:parse json faild "+json);
			return null;
		}
		JSONObject object=value.isObject();
		if(object==null){
			LogUtils.log("SphereDataListConverter:not json object:"+json);
			return null;
		}
		
		if(object.get("type")==null){
			LogUtils.log("SphereDataListConverter:has no type attribute:"+object.toString());
			return null;
		}
		
		JSONString typeString=object.get("type").isString();
		if(typeString==null){
			LogUtils.log("HSphereDataListConverter:type is not string:"+object.toString());
			return null;
		}
		
		String type=typeString.stringValue();
		if(!type.equals(SphereData.DATA_TYPE)){
			LogUtils.log("SphereDataListConverter:difference type:"+type);
			return null;
		}
		
		JSONValue datasValue=object.get("datas");
		if(datasValue==null){
			LogUtils.log("SphereDataListConverter:no datas:");
			return null;
		}
		JSONArray array=datasValue.isArray();
		if(array==null){
			LogUtils.log("SphereDataListConverter:no jsonarray:");
			return null;
		}
		List<JSONObject> jsonObjects=Lists.newArrayList();
		for(int i=0;i<array.size();i++){
			JSONValue arrayValue=array.get(i);
			JSONObject arrayObject=arrayValue.isObject();
			if(arrayObject==null){
				LogUtils.log("SphereDataListConverter:contain invalid data:"+i+","+arrayValue);
				return null;
			}
			jsonObjects.add(arrayObject);
		}
		
		return Lists.newArrayList(new SphereDataConverter().reverse().convertAll(jsonObjects));
		
	}

}
