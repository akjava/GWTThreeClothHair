package com.akjava.gwt.clothhair.client.hair;

import com.akjava.gwt.lib.client.json.JSONObjectWrapper;
import com.akjava.gwt.clothhair.client.ammo.BodyDataConverter;
import com.akjava.gwt.clothhair.client.ammo.ConstraintDataConverter;
import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.akjava.gwt.clothhair.client.texture.HairTextureDataConverter;
import com.akjava.gwt.lib.client.JavaScriptUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.java.utils.GWTThreeUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.google.common.base.Converter;
import com.google.common.base.Strings;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.json.client.JSONObject;

public class HairDataConverter extends Converter<HairData,JSONObject> {
	private HairTextureDataConverter textureDataConverter=new HairTextureDataConverter();
	
	@Override
	protected JSONObject doForward(HairData hairData) {
		JSONObject object=new JSONObject();
		JSONObjectWrapper wrapper=new JSONObjectWrapper(object);
		//header
		//wrapper.setString("type", DATA_TYPE);
		//wrapper.setDouble("version", 1.0);
		//pins
		JsArrayNumber faceIndexes=JavaScriptUtils.createJSArrayNumber();
		JsArrayNumber vertexIndexes=JavaScriptUtils.createJSArrayNumber();
		JsArrayNumber targetIndexes=JavaScriptUtils.createJSArrayNumber();
		for(int i=0;i<hairData.getHairPins().size();i++){
			HairPin pin=hairData.getHairPins().get(i);
			faceIndexes.push(pin.getFaceIndex());
			vertexIndexes.push(pin.getVertexOfFaceIndex());
			targetIndexes.push(pin.getTargetClothIndex());
		}
		wrapper.setArrayNumber("faceIndexes", faceIndexes);
		wrapper.setArrayNumber("vertexOfFaceIndexes", vertexIndexes);
		wrapper.setArrayNumber("targetIndexes", targetIndexes);
		//general
		wrapper.setInt("sliceFace", hairData.getSliceFaceCount());
		wrapper.setInt("stackFace", hairData.getSizeOfV());
		wrapper.setDouble("faceWidthScale", hairData.getScaleOfU());
		wrapper.setBoolean("cutHorizontal", hairData.isCutU());
		wrapper.setInt("cutHorizontalStartIndex", hairData.getStartCutUIndexV());
		wrapper.setInt("channel", hairData.getChannel());
		wrapper.setDouble("particleMass", hairData.getParticleMass());
		wrapper.setBoolean("syncPosition", hairData.isSyncMove());
		wrapper.setBoolean("connectHorizontal", hairData.isConnectHorizontal());
		wrapper.setInt("physicsType", hairData.getHairPhysicsType());
		
		wrapper.setDouble("extendOutsideRatio", hairData.getExtendOutsideRatio());
		wrapper.setBoolean("averagingNormal", hairData.isExecAverageNormal());
		wrapper.setBoolean("useCustomNormal", hairData.isUseCustomNormal());
		Vector3 customNormal=hairData.getCustomNormal();
		if(customNormal!=null){
			wrapper.setArrayNumber("customNormal", customNormal.toArray());
		}
		wrapper.setDouble("originalNormalRatio", hairData.getOriginalNormalRatio());
		
		if(hairData.getHairTextureData()!=null){
			wrapper.setString("hairTextureData", textureDataConverter.convert(hairData.getHairTextureData()));
		}
		
		
		wrapper.setInt("pointMode",hairData.getPointMode());
		if(hairData.getPointMode()==HairData.POINT_MODE_SEMI_AUTO){
			if(hairData.getSemiAutoPoints()!=null){
				JsArray<JsArrayNumber> pts=GWTThreeUtils.toJsArrayNumber(hairData.getSemiAutoPoints());
				wrapper.setArray("semiAutoPoints",pts);
			}else{
				LogUtils.log("some how semiauto point is null");
			}
			
			if(hairData.getSemiAutoPins()!=null){
				wrapper.setArrayNumber("semiAutoPins",hairData.getSemiAutoPins());
			}
		}
		
		//plain
		JSONObject plainObject=new JSONObject();
		JSONObjectWrapper plainObjectWrapper=new JSONObjectWrapper(plainObject);
		wrapper.setObject("plain-cloth", plainObject);
		plainObjectWrapper.setBoolean("narrow", hairData.isDoNarrow());
		plainObjectWrapper.setDouble("narrowScale", hairData.getNarrowScale());
		plainObjectWrapper.setInt("narrowStartIndex", hairData.getStartNarrowIndexV());
		plainObjectWrapper.setDouble("damping", hairData.getDamping());
		plainObjectWrapper.setDouble("mass", hairData.getMass());
		//ammo
		JSONObject ammoObject=new JSONObject();
		JSONObjectWrapper ammoObjectWrapper=new JSONObjectWrapper(ammoObject);
		wrapper.setObject("ammo", ammoObject);
		
		
		if(!Strings.isNullOrEmpty(hairData.getCustomGeometryName())){
			ammoObjectWrapper.setString("customGeometryName", hairData.getCustomGeometryName());
		}
		ammoObjectWrapper.setBoolean("customGeometryUseAutoSkinning", hairData.isCustomGeometryUseAutoSkinning());
		ammoObjectWrapper.setBoolean("enableCustomGeometry", hairData.isEnableCustomGeometry());
		
		ammoObjectWrapper.setBoolean("contactParticle", hairData.isAmmoContactParticle());
		
		ammoObjectWrapper.setBoolean("circleStyle", hairData.isCircleStyle());
		
		ammoObjectWrapper.setBoolean("useCustomBodyParticleData", hairData.isUseCustomBodyParticleData());
		if(hairData.isUseCustomBodyParticleData() && hairData.getAmmoBodyParticleData()!=null){
			JSONObject ammoParticleObject=new BodyDataConverter().convert(hairData.getAmmoBodyParticleData());
			ammoObjectWrapper.setObject("ammoBodyParticleData", ammoParticleObject);
			
		}
		
		ammoObjectWrapper.setBoolean("useCustomConstraintData", hairData.isUseCustomConstraintData());
		if(hairData.isUseCustomConstraintData() && hairData.getAmmoConstraintData()!=null){
			JSONObject ammoConstraintObject=new ConstraintDataConverter().convert(hairData.getAmmoConstraintData());
			ammoObjectWrapper.setObject("ammoConstraintData", ammoConstraintObject);
			
		}
		
		
		ammoObjectWrapper.setInt("particleType", hairData.getParticleType());
		ammoObjectWrapper.setDouble("particleRadiusRatio", hairData.getParticleRadiusRatio());
		ammoObjectWrapper.setDouble("endParticleRadiusRatio", hairData.getAmmoEndParticleRadiusRatio());
		ammoObjectWrapper.setDouble("syncMoveLinear", hairData.getSyncMoveLinear());
		ammoObjectWrapper.setDouble("syncForceLinear", hairData.getSyncForceLinear());
		ammoObjectWrapper.setBoolean("startCenterCircle", hairData.isAmmoStartCenterCircle());
		
		ammoObjectWrapper.setBoolean("circleUseFirstPointY", hairData.isAmmoCircleUseFirstPointY());
		ammoObjectWrapper.setDouble("circleRangeMin", hairData.getAmmoCircleRangeMin());
		ammoObjectWrapper.setDouble("circleRangeMax", hairData.getAmmoCircleRangeMax());
		ammoObjectWrapper.setDouble("circleInRangeRatio", hairData.getAmmoCircleInRangeRatio());
		
		ammoObjectWrapper.setString("targetBone", hairData.getAmmoTargetBone());
		
		
		//ammo-cloth
		
		//ammo-bone
		JSONObject ammoBoneObject=new JSONObject();
		JSONObjectWrapper ammoBoneObjectWrapper=new JSONObjectWrapper(ammoBoneObject);
		ammoObjectWrapper.setObject("ammo-bone", ammoBoneObject);
		ammoBoneObjectWrapper.setDouble("thick", hairData.getThickRatio());
		ammoBoneObjectWrapper.setDouble("thick2", hairData.getAmmoBoneThickRatio2());
		
		//ammo-bone
		JSONObject ammoHairObject=new JSONObject();
		JSONObjectWrapper ammoHairObjectWrapper=new JSONObjectWrapper(ammoHairObject);
		ammoObjectWrapper.setObject("ammo-hair", ammoHairObject);
		ammoHairObjectWrapper.setInt("circleDummyHairCount", hairData.getAmmoCircleDummyHairCount());
		ammoHairObjectWrapper.setDouble("circleDummyHairAngle", hairData.getAmmoCircleDummyHairAngle());
		ammoHairObjectWrapper.setBoolean("circleHairMergeCenter", hairData.isAmmoCircleHairMergeCenter());
		ammoHairObjectWrapper.setBoolean("circleHairMergeLast", hairData.isAmmoCircleHairMergeLast());
		ammoHairObjectWrapper.setDouble("thinLast", hairData.getAmmoHairThinLast());
		return object;
	}

	@Override
	protected HairData doBackward(JSONObject object) {
		
		JSONObjectWrapper wrapper=new JSONObjectWrapper(object);
		return parse1(wrapper);
	
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
		int sliceFace=object.getInt("sliceFace", hairData.getSliceFaceCount());
		int stackFace=object.getInt("stackFace", hairData.getSizeOfV());
		double faceWidthScale=object.getDouble("faceWidthScale", hairData.getScaleOfU());
		//TODO add faceHeightScale
		
		hairData.setSizeOfU(sliceFace);
		hairData.setSizeOfV(stackFace);
		hairData.setScaleOfU(faceWidthScale);
		
		hairData.setCutU(object.getBoolean("cutHorizontal", hairData.isCutU()));
		hairData.setStartCutUIndexV(object.getInt("cutHorizontalStartIndex", hairData.getStartCutUIndexV()));
		//TODO add end
		
		
		hairData.setParticleMass(object.getDouble("particleMass", hairData.getParticleMass()));
		hairData.setChannel(object.getInt("channel", hairData.getChannel()));//TODO support bit-channel
		hairData.setSyncMove(object.getBoolean("syncPosition", hairData.isSyncMove()));
		
		hairData.setConnectHorizontal(object.getBoolean("connectHorizontal", hairData.isConnectHorizontal()));
		hairData.setHairPhysicsType(object.getInt("physicsType", hairData.getHairPhysicsType()));
		hairData.setExtendOutsideRatio(object.getDouble("extendOutsideRatio", hairData.getExtendOutsideRatio()));
		
		hairData.setExecAverageNormal(object.getBoolean("averagingNormal", hairData.isExecAverageNormal()));
		hairData.setUseCustomNormal(object.getBoolean("useCustomNormal", hairData.isUseCustomNormal()));
		
		
		JsArrayNumber customNormalNumber= object.getArrayNumber("customNormal");
		if(customNormalNumber!=null){
			Vector3 customNormal=THREE.Vector3().fromArray(customNormalNumber);
			hairData.setCustomNormal(customNormal);
		}
		
		//TODO add customNormal
		hairData.setOriginalNormalRatio(object.getDouble("originalNormalRatio", hairData.getOriginalNormalRatio()));
		
		
		String hairTextureData=object.getString("hairTextureData",null);
		
		if(hairTextureData!=null){
			hairData.setHairTextureData(textureDataConverter.reverse().convert(hairTextureData));
		}else{
			//no need,had has default
		}
		
		
		
		
		
		hairData.setPointMode(object.getInt("pointMode",hairData.getPointMode()));
		if(hairData.getPointMode()==HairData.POINT_MODE_SEMI_AUTO){
			JsArray<JavaScriptObject> ptsArray=object.getArray("semiAutoPoints");
			if(ptsArray!=null){
				@SuppressWarnings("unchecked")
				JsArray<Vector3> pts=GWTThreeUtils.fromJsArrayNumberToVector3((JsArray<JsArrayNumber>)ptsArray.cast());
				hairData.setSemiAutoPoints(pts);
			}else{
				LogUtils.log("some how semiauto point is null");
			}
			JsArrayNumber pinsArray=object.getArrayNumber("semiAutoPins");
			if(pinsArray!=null){
				hairData.setSemiAutoPins(pinsArray);
			}
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
		
		JSONObjectWrapper ammoObjectWrapper=object.getObject("ammo");
		if(ammoObjectWrapper!=null){
			
			String customGeometryName=ammoObjectWrapper.getString("customGeometryName", hairData.getCustomGeometryName());;
			if(!Strings.isNullOrEmpty(customGeometryName)){
				hairData.setCustomGeometryName(customGeometryName);
			}
			
			hairData.setCustomGeometryUseAutoSkinning(ammoObjectWrapper.getBoolean("customGeometryUseAutoSkinning", hairData.isCustomGeometryUseAutoSkinning()));
			hairData.setEnableCustomGeometry(ammoObjectWrapper.getBoolean("enableCustomGeometry", hairData.isEnableCustomGeometry()));
			
			hairData.setAmmoContactParticle(ammoObjectWrapper.getBoolean("contactParticle", hairData.isAmmoContactParticle()));
			hairData.setUseCustomBodyParticleData(ammoObjectWrapper.getBoolean("useCustomBodyParticleData", hairData.isUseCustomBodyParticleData()));
			
			if(hairData.isUseCustomBodyParticleData()){
				JSONObjectWrapper ammoObject=ammoObjectWrapper.getObject("ammoBodyParticleData");
				if(ammoObject!=null){
					hairData.setAmmoBodyParticleData(new BodyDataConverter().reverse().convert(ammoObject.jsonObject()));
					
				}else{
					LogUtils.log("useCustomBodyParticleData is true,but no ammoBodyParticleData");
				}
			}
			
			
			hairData.setUseCustomConstraintData(ammoObjectWrapper.getBoolean("useCustomConstraintData", hairData.isUseCustomConstraintData()));
			if(hairData.isUseCustomConstraintData()){
				JSONObjectWrapper ammoObject=ammoObjectWrapper.getObject("ammoConstraintData");
				if(ammoObject!=null){
					hairData.setAmmoConstraintData(new ConstraintDataConverter().reverse().convert(ammoObject.jsonObject()));
					
				}else{
					LogUtils.log("useCustomConstraintData is true,but no ammoConstraintData");
				}

			}
			
			hairData.setCircleStyle(ammoObjectWrapper.getBoolean("circleStyle", hairData.isCircleStyle()));
			
			hairData.setParticleType(ammoObjectWrapper.getInt("particleType", hairData.getParticleType()));
			hairData.setParticleRadiusRatio(ammoObjectWrapper.getDouble("particleRadiusRatio", hairData.getParticleRadiusRatio()));
			hairData.setAmmoEndParticleRadiusRatio(ammoObjectWrapper.getDouble("endParticleRadiusRatio", hairData.getAmmoEndParticleRadiusRatio()));
			hairData.setSyncMoveLinear(ammoObjectWrapper.getDouble("syncMoveLinear", hairData.getSyncMoveLinear()));
			hairData.setSyncForceLinear(ammoObjectWrapper.getDouble("syncForceLinear", hairData.getSyncForceLinear()));
			
			hairData.setAmmoStartCenterCircle(ammoObjectWrapper.getBoolean("startCenterCircle", hairData.isAmmoStartCenterCircle()));
			hairData.setAmmoCircleUseFirstPointY(ammoObjectWrapper.getBoolean("circleUseFirstPointY", hairData.isAmmoCircleUseFirstPointY()));
			hairData.setAmmoCircleRangeMin(ammoObjectWrapper.getDouble("circleRangeMin", hairData.getAmmoCircleRangeMin()));
			hairData.setAmmoCircleRangeMax(ammoObjectWrapper.getDouble("circleRangeMax", hairData.getAmmoCircleRangeMax()));
			hairData.setAmmoCircleInRangeRatio(ammoObjectWrapper.getDouble("circleInRangeRatio", hairData.getAmmoCircleInRangeRatio()));
			
			hairData.setAmmoTargetBone(ammoObjectWrapper.getString("targetBone", hairData.getAmmoTargetBone()));
			
			JSONObjectWrapper ammoClothObject=ammoObjectWrapper.getObject("ammo-cloth");
			if(ammoClothObject!=null){
				//nothing so far
			}
			JSONObjectWrapper ammoBoneObject=ammoObjectWrapper.getObject("ammo-bone");
			if(ammoBoneObject!=null){
				hairData.setThickRatio(ammoBoneObject.getDouble("thick", hairData.getThickRatio()));
				hairData.setAmmoBoneThickRatio2(ammoBoneObject.getDouble("thick2", hairData.getAmmoBoneThickRatio2()));
			}else{
				LogUtils.log("no ammoBoneObject on hairData");
			}
			JSONObjectWrapper ammoHairObjectWrapper=ammoObjectWrapper.getObject("ammo-hair");
			if(ammoHairObjectWrapper!=null){
				hairData.setAmmoCircleDummyHairCount(ammoHairObjectWrapper.getInt("circleDummyHairCount", hairData.getAmmoCircleDummyHairCount()));
				hairData.setAmmoCircleDummyHairAngle(ammoHairObjectWrapper.getDouble("circleDummyHairAngle", hairData.getAmmoCircleDummyHairAngle()));	
			
				hairData.setAmmoCircleHairMergeCenter(ammoHairObjectWrapper.getBoolean("circleHairMergeCenter", hairData.isAmmoCircleHairMergeCenter()));
				hairData.setAmmoCircleHairMergeLast(ammoHairObjectWrapper.getBoolean("circleHairMergeLast", hairData.isAmmoCircleHairMergeLast()));
				hairData.setAmmoHairThinLast(ammoHairObjectWrapper.getDouble("thinLast", hairData.getAmmoHairThinLast()));
				
			}
			
			
		}else{
			LogUtils.log("no ammoObject on hairData");
		}
		
		return hairData;
	}

}
