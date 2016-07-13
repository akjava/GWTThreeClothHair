package com.akjava.gwt.clothhair.client.hair;

import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.akjava.gwt.clothhair.client.texture.HairTextureDataConverter;
import com.akjava.gwt.lib.client.JavaScriptUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.google.common.base.Converter;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayBoolean;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONBoolean;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class HairDataConverter extends Converter<HairData,String> {
	private HairTextureDataConverter textureDataConverter=new HairTextureDataConverter();
	public static final String DATA_TYPE="HairData";
	@Override
	protected String doForward(HairData data) {
		JSONObject object=new JSONObject();
		JSONObjectWrapper wrapper=new JSONObjectWrapper(object);
		//header
		wrapper.setString("type", DATA_TYPE);
		wrapper.setDouble("version", 1.0);
		//pins
		JsArrayNumber faceIndexes=JavaScriptUtils.createJSArrayNumber();
		JsArrayNumber vertexIndexes=JavaScriptUtils.createJSArrayNumber();
		JsArrayNumber targetIndexes=JavaScriptUtils.createJSArrayNumber();
		for(int i=0;i<data.getHairPins().size();i++){
			HairPin pin=data.getHairPins().get(i);
			faceIndexes.push(pin.getFaceIndex());
			vertexIndexes.push(pin.getVertexOfFaceIndex());
			targetIndexes.push(pin.getTargetClothIndex());
		}
		wrapper.setArrayNumber("faceIndexes", faceIndexes);
		wrapper.setArrayNumber("vertexOfFaceIndexes", vertexIndexes);
		wrapper.setArrayNumber("targetIndexes", targetIndexes);
		//general
		wrapper.setInt("sliceFace", data.getSizeOfU());
		wrapper.setInt("stackFace", data.getSizeOfV());
		wrapper.setDouble("faceWidthScale", data.getScaleOfU());
		wrapper.setBoolean("cutHorizontal", data.isCutU());
		wrapper.setInt("cutHorizontalStartIndex", data.getStartCutUIndexV());
		wrapper.setInt("channel", data.getChannel());
		wrapper.setBoolean("syncPosition", data.isSyncMove());
		wrapper.setBoolean("connectHorizontal", data.isConnectHorizontal());
		wrapper.setInt("physicsType", data.getHairPhysicsType());
		
		wrapper.setDouble("extendOutsideRatio", data.getExtendOutsideRatio());
		wrapper.setBoolean("averagingNormal", data.isExecAverageNormal());
		wrapper.setBoolean("useCustomNormal", data.isUseCustomNormal());
		wrapper.setDouble("originalNormalRatio", data.getOriginalNormalRatio());
		if(data.getHairTextureData()!=null){
			wrapper.setString("hairTextureData", textureDataConverter.convert(data.getHairTextureData()));
		}
		//plain
		JSONObject plainObject=new JSONObject();
		JSONObjectWrapper plainObjectWrapper=new JSONObjectWrapper(plainObject);
		wrapper.setObject("plain-cloth", plainObject);
		plainObjectWrapper.setBoolean("narrow", data.isDoNarrow());
		plainObjectWrapper.setDouble("narrowScale", data.getNarrowScale());
		plainObjectWrapper.setInt("narrowStartIndex", data.getStartNarrowIndexV());
		plainObjectWrapper.setDouble("damping", data.getDamping());
		plainObjectWrapper.setDouble("mass", data.getMass());
		//ammo
		JSONObject ammoObject=new JSONObject();
		JSONObjectWrapper ammoObjectWrapper=new JSONObjectWrapper(ammoObject);
		wrapper.setObject("ammo", ammoObject);
		ammoObjectWrapper.setDouble("particleRadiusRatio", data.getParticleRadiusRatio());
		ammoObjectWrapper.setDouble("syncMoveLinear", data.getSyncMoveLinear());
		ammoObjectWrapper.setDouble("syncForceLinear", data.getSyncForceLinear());
		ammoObjectWrapper.setBoolean("startCenterCircle", data.isAmmoStartCenterCircle());
		
		//ammo-cloth
		
		//ammo-bone
		JSONObject ammoBoneObject=new JSONObject();
		JSONObjectWrapper ammoBoneObjectWrapper=new JSONObjectWrapper(ammoBoneObject);
		ammoObjectWrapper.setObject("ammo-bone", ammoBoneObject);
		ammoBoneObjectWrapper.setDouble("thick", data.getThickRatio());
		
		return object.toString();
	}

	@Override
	protected HairData doBackward(String json) {
		JSONValue value=JSONParser.parseStrict(json);
		if(value==null){
			LogUtils.log("HairDataConverter:parse json faild "+json);
			return null;
		}
		JSONObject object=value.isObject();
		if(object==null){
			LogUtils.log("HairDataConverter:not json object:"+json);
			return null;
		}
		
		if(object.get("type")==null){
			LogUtils.log("HairDataConverter:has no type attribute:"+object.toString());
			return null;
		}
		
		JSONString typeString=object.get("type").isString();
		if(typeString==null){
			LogUtils.log("HairDataConverter:has a type attribute:"+object.toString());
			return null;
		}
		
		String type=typeString.stringValue();
		if(!type.equals(DATA_TYPE)){
			LogUtils.log("HairDataConverter:difference type:"+type);
			return null;
		}
		
		JSONObjectWrapper wrapper=new JSONObjectWrapper(object);
		
		double version=wrapper.getDouble("version",1.0);
		if(version==1.0){
			return parse1(wrapper);
		}
		
		
		return null;
	}
	//parse version 1
	private HairData parse1(JSONObjectWrapper object) {
		HairData hairData=new HairData();
		
		//parse pins
		JsArrayNumber faceIndexes=object.getArrayNumber("faceIndexes");
		JsArrayNumber vertexIndexes=object.getArrayNumber("vertexOfFaceIndexes");
		JsArrayNumber targetIndexes=object.getArrayNumber("targetIndexes");
		if(faceIndexes==null || vertexIndexes==null || targetIndexes==null){
			LogUtils.log("pins array is invalid");
			return null;
		}
		if(faceIndexes.length()!=vertexIndexes.length() || vertexIndexes.length()!=targetIndexes.length()){
			LogUtils.log("pins array length is invalid");
			return null;
		}
		for(int i=0;i<faceIndexes.length();i++){
			int faceIndex=(int)faceIndexes.get(i);
			int vertexOfFaceIndex=(int)vertexIndexes.get(i);
			int target=(int)targetIndexes.get(i);
			HairPin pin=new HairPin(faceIndex, vertexOfFaceIndex,target);
			hairData.getHairPins().add(pin);
		}
		//parse common
		int sliceFace=object.getInt("sliceFace", hairData.getSizeOfU());
		int stackFace=object.getInt("stackFace", hairData.getSizeOfV());
		double faceWidthScale=object.getDouble("faceWidthScale", hairData.getScaleOfU());
		//TODO add faceHeightScale
		
		hairData.setSizeOfU(sliceFace);
		hairData.setSizeOfV(stackFace);
		hairData.setScaleOfU(faceWidthScale);
		
		hairData.setCutU(object.getBoolean("cutHorizontal", hairData.isCutU()));
		hairData.setStartCutUIndexV(object.getInt("cutHorizontalStartIndex", hairData.getStartCutUIndexV()));
		//TODO add end
		
		hairData.setChannel(object.getInt("channel", hairData.getChannel()));//TODO support bit-channel
		hairData.setSyncMove(object.getBoolean("syncPosition", hairData.isSyncMove()));
		
		hairData.setConnectHorizontal(object.getBoolean("connectHorizontal", hairData.isConnectHorizontal()));
		hairData.setHairPhysicsType(object.getInt("physicsType", hairData.getHairPhysicsType()));
		hairData.setExtendOutsideRatio(object.getDouble("extendOutsideRatio", hairData.getExtendOutsideRatio()));
		
		hairData.setExecAverageNormal(object.getBoolean("averagingNormal", hairData.isExecAverageNormal()));
		hairData.setUseCustomNormal(object.getBoolean("useCustomNormal", hairData.isUseCustomNormal()));
		//TODO add customNormal
		hairData.setOriginalNormalRatio(object.getDouble("originalNormalRatio", hairData.getOriginalNormalRatio()));
		
		
		String hairTextureData=object.getString("hairTextureData",null);
		
		if(hairTextureData!=null){
			hairData.setHairTextureData(textureDataConverter.reverse().convert(hairTextureData));
		}else{
			//no need,had has default
		}
		
		//parse plain-cloth
		JSONObjectWrapper plaintClothObject=object.getObject("plain-cloth");
		if(plaintClothObject!=null){
			hairData.setDoNarrow(plaintClothObject.getBoolean("narrow", hairData.isDoNarrow()));
			hairData.setNarrowScale(plaintClothObject.getDouble("narrowScale", hairData.getNarrowScale()));
			hairData.setStartNarrowIndexV(plaintClothObject.getInt("narrowStartIndex", hairData.getStartNarrowIndexV()));
			
			hairData.setDamping(plaintClothObject.getDouble("damping", hairData.getDamping()));
			hairData.setMass(plaintClothObject.getDouble("mass", hairData.getMass()));
			
			//edge mode is deprecated
		}else{
			LogUtils.log("no plaintClothObject on hairData");
		}
		
		JSONObjectWrapper ammoObject=object.getObject("ammo");
		if(ammoObject!=null){
			hairData.setParticleRadiusRatio(ammoObject.getDouble("particleRadiusRatio", hairData.getParticleRadiusRatio()));
			hairData.setSyncMoveLinear(ammoObject.getDouble("syncMoveLinear", hairData.getSyncMoveLinear()));
			hairData.setSyncForceLinear(ammoObject.getDouble("syncForceLinear", hairData.getSyncForceLinear()));
			
			hairData.setAmmoStartCenterCircle(ammoObject.getBoolean("startCenterCircle", hairData.isAmmoStartCenterCircle()));
			
			
			JSONObjectWrapper ammoClothObject=ammoObject.getObject("ammo-cloth");
			if(ammoClothObject!=null){
				//nothing so far
			}
			JSONObjectWrapper ammoBoneObject=ammoObject.getObject("ammo-bone");
			if(ammoBoneObject!=null){
				hairData.setThickRatio(ammoBoneObject.getDouble("thick", hairData.getThickRatio()));
			}else{
				LogUtils.log("no ammoBoneObject on hairData");
			}
		}else{
			LogUtils.log("no ammoObject on hairData");
		}
		
		return hairData;
	}

	public static class JSONObjectWrapper{
		private JSONObject jsonObject;

		public JSONObjectWrapper(JSONObject object) {
			super();
			this.jsonObject = object;
		}
		
		public void setArrayBoolean(String key,JsArrayBoolean value){
			jsonObject.put(key, new JSONArray(value));
		}
		public JsArrayBoolean getArrayBoolean(String key){
			if(!jsonObject.containsKey(key)){
				return null;
			}
			JSONArray value=jsonObject.get(key).isArray();
			if(value==null){
				LogUtils.log("not array");
				return null;
			}
			
			return value.getJavaScriptObject().cast();
		}
		
		public void setArrayNumber(String key,JsArrayNumber value){
			jsonObject.put(key, new JSONArray(value));
		}
		
		
		
		public void setObject(String key,JSONObject value){
			jsonObject.put(key, value);
		}
		
		public void setInt(String key,int value){
			jsonObject.put(key, new JSONNumber(value));
		}
		public void setDouble(String key,double value){
			jsonObject.put(key, new JSONNumber(value));
		}
		public void setBoolean(String key,boolean value){
			jsonObject.put(key, JSONBoolean.getInstance(value));
		}
		public void setString(String key,String value){
			jsonObject.put(key, new JSONString(value));
		}
		
		public JSONObjectWrapper getObject(String key){
			if(!jsonObject.containsKey(key)){
				return null;
			}
			JSONObject value=jsonObject.get(key).isObject();
			if(value==null){
				LogUtils.log("not object");
				return null;
			}
			return new JSONObjectWrapper(value);
		}
		
		public JsArray<JavaScriptObject> getArray(String key){
			if(!jsonObject.containsKey(key)){
				return null;
			}
			JSONArray value=jsonObject.get(key).isArray();
			if(value==null){
				LogUtils.log("not array");
				return null;
			}
			return value.getJavaScriptObject().cast();
		}
		
		public JsArrayNumber getArrayNumber(String key){
			if(!jsonObject.containsKey(key)){
				return null;
			}
			JSONArray value=jsonObject.get(key).isArray();
			if(value==null){
				LogUtils.log("not array");
				return null;
			}
			
			return value.getJavaScriptObject().cast();
		}
		
		public double getDouble(String key,Double defaultValue){
			if(!jsonObject.containsKey(key)){
				return defaultValue;
			}
			JSONNumber value=jsonObject.get(key).isNumber();
			if(value==null){
				LogUtils.log("not number");
				return defaultValue;
			}
			
			return value.doubleValue();
		}
		public int getInt(String key,Integer defaultValue){
			if(!jsonObject.containsKey(key)){
				return defaultValue;
			}
			JSONNumber value=jsonObject.get(key).isNumber();
			if(value==null){
				LogUtils.log("not number");
				return defaultValue;
			}
			
			return (int)value.doubleValue();
		}
		public String getString(String key,String defaultValue){
			if(!jsonObject.containsKey(key)){
				return defaultValue;
			}
			JSONString value=jsonObject.get(key).isString();
			if(value==null){
				LogUtils.log("not string");
				return defaultValue;
			}
			return value.stringValue();
		}
		public boolean getBoolean(String key,Boolean defaultValue){
			if(!jsonObject.containsKey(key)){
				return defaultValue;
			}
			JSONBoolean number=jsonObject.get(key).isBoolean();
			if(number==null){
				LogUtils.log("not boolean");
				return defaultValue;
			}
			return number.booleanValue();
		}
	}
}
