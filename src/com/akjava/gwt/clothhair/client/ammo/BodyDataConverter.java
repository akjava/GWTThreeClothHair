package com.akjava.gwt.clothhair.client.ammo;

import com.akjava.gwt.clothhair.client.hair.HairDataConverter.JSONObjectWrapper;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.threeammo.client.AmmoBodyPropertyData;
import com.google.common.base.Converter;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class BodyDataConverter extends Converter<AmmoBodyPropertyData,String>{
	public static final String DATA_TYPE="AmmoBodyData";
	@Override
	protected String doForward(AmmoBodyPropertyData data) {
		JSONObject object=new JSONObject();
		JSONObjectWrapper wrapper=new JSONObjectWrapper(object);
		//header
		wrapper.setString("type", DATA_TYPE);
		wrapper.setDouble("version", 1.0);
		
		wrapper.setDouble("friction", data.getFriction());
		wrapper.setDouble("restitution", data.getRestitution());
		
		if(data.getDamping()!=null){
		wrapper.setDouble("damping_linear", data.getDamping().getX());
		wrapper.setDouble("damping_angular", data.getDamping().getY());
		}
		
		return object.toString();
	}

	@Override
	protected AmmoBodyPropertyData doBackward(String json) {
		JSONValue value=JSONParser.parseStrict(json);
		if(value==null){
			LogUtils.log("BodyDataConverter:parse json faild "+json);
			return null;
		}
		JSONObject object=value.isObject();
		if(object==null){
			LogUtils.log("BodyDataConverter:not json object:"+json);
			return null;
		}
		
		if(object.get("type")==null){
			LogUtils.log("BodyDataConverter:has no type attribute:"+object.toString());
			return null;
		}
		
		JSONString typeString=object.get("type").isString();
		if(typeString==null){
			LogUtils.log("BodyDataConverter:has a type attribute:"+object.toString());
			return null;
		}
		
		String type=typeString.stringValue();
		if(!type.equals(DATA_TYPE)){
			LogUtils.log("BodyDataConverter:difference type:"+type);
			return null;
		}
		
		JSONObjectWrapper wrapper=new JSONObjectWrapper(object);
		AmmoBodyPropertyData bodyData=new AmmoBodyPropertyData();
		
		bodyData.setDamping(THREE.Vector2(
				wrapper.getDouble("damping_linear", 0.0),
				wrapper.getDouble("damping_angular", 0.0)
				));
		
		bodyData.setFriction(wrapper.getDouble("friction", bodyData.getFriction()));
		bodyData.setRestitution(wrapper.getDouble("restitution", bodyData.getRestitution()));
		
		return bodyData;
	}

}
