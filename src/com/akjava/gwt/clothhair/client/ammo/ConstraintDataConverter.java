package com.akjava.gwt.clothhair.client.ammo;

import javax.annotation.Nullable;

import com.akjava.gwt.clothhair.client.JSONObjectWrapper;
import com.akjava.gwt.lib.client.JavaScriptUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.threeammo.client.AmmoConstraintPropertyData;
import com.google.common.base.Converter;
import com.google.common.base.MoreObjects;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;

public class ConstraintDataConverter extends Converter<AmmoConstraintPropertyData,JSONObject>{
	public static final String DATA_TYPE="AmmoConstraintData";
	@Override
	protected JSONObject doForward(AmmoConstraintPropertyData data) {
		JSONObject object=new JSONObject();
		JSONObjectWrapper wrapper=new JSONObjectWrapper(object);
		//header
		wrapper.setString("type", DATA_TYPE);
		wrapper.setDouble("version", 1.0);
		
		wrapper.setBoolean("useLinearReferenceFrameA", data.isUseLinearReferenceFrameA());
		wrapper.setBoolean("disableCollisionsBetweenLinkedBodies", data.isDisableCollisionsBetweenLinkedBodies());
		wrapper.setDouble("frameInARelativePosRatio", data.getFrameInARelativePosRatio());
		wrapper.setDouble("frameInBRelativePosRatio", data.getFrameInBRelativePosRatio());
		
		wrapper.setArrayBoolean("enableSprings", JavaScriptUtils.toArrayBoolean(data.getEnableSprings()));
		wrapper.setArrayNumber("dampings", JavaScriptUtils.toArrayNumber(data.getDampings()));
		wrapper.setArrayNumber("stiffnesses", JavaScriptUtils.toArrayNumber(data.getStiffnesses()));
		
		wrapper.setArrayNumber("angularLowerLimit",data.getAngularLowerLimit().toArray());
		wrapper.setArrayNumber("angularUpperLimit",data.getAngularUpperLimit().toArray());
		wrapper.setArrayNumber("linearLowerLimit",data.getLinearLowerLimit().toArray());
		wrapper.setArrayNumber("linearUpperLimit",data.getLinearUpperLimit().toArray());
		
		LogUtils.log(object.getJavaScriptObject());
		
		return object;
	}

	@Override
	protected AmmoConstraintPropertyData doBackward(JSONObject object) {
		
		
		JSONString typeString=object.get("type").isString();
		if(typeString==null){
			LogUtils.log("ConstraintDataConverter:has a type attribute:"+object.toString());
			return null;
		}
		
		String type=typeString.stringValue();
		if(!type.equals(DATA_TYPE)){
			LogUtils.log("ConstraintDataConverter:difference type:"+type);
			return null;
		}
		
		JSONObjectWrapper wrapper=new JSONObjectWrapper(object);
		AmmoConstraintPropertyData constraintData=new AmmoConstraintPropertyData();
		
		
		constraintData.setUseLinearReferenceFrameA(wrapper.getBoolean("useLinearReferenceFrameA", constraintData.isUseLinearReferenceFrameA()));
		constraintData.setDisableCollisionsBetweenLinkedBodies(wrapper.getBoolean("disableCollisionsBetweenLinkedBodies", constraintData.isDisableCollisionsBetweenLinkedBodies()));
		constraintData.setFrameInARelativePosRatio(wrapper.getDouble("frameInARelativePosRatio", constraintData.getFrameInARelativePosRatio()));
		constraintData.setFrameInBRelativePosRatio(wrapper.getDouble("frameInBRelativePosRatio", constraintData.getFrameInBRelativePosRatio()));
		
		constraintData.setEnableSprings(
				MoreObjects.firstNonNull(
				JavaScriptUtils.toList(wrapper.getArrayBoolean("enableSprings"))
				,constraintData.getEnableSprings())
				);
		constraintData.setDampings(
				MoreObjects.firstNonNull(
				JavaScriptUtils.toList(wrapper.getArrayNumber("dampings"))
				,constraintData.getDampings())
		);
		constraintData.setStiffnesses(
				MoreObjects.firstNonNull(
				JavaScriptUtils.toList(wrapper.getArrayNumber("stiffnesses"))
				,constraintData.getStiffnesses())
		);
		
		constraintData.setAngularLowerLimit(toVector3(wrapper.getArrayNumber("angularLowerLimit"), constraintData.getAngularLowerLimit()));
		constraintData.setAngularUpperLimit(toVector3(wrapper.getArrayNumber("angularUpperLimit"), constraintData.getAngularUpperLimit()));
		constraintData.setLinearLowerLimit(toVector3(wrapper.getArrayNumber("linearLowerLimit"), constraintData.getLinearLowerLimit()));
		constraintData.setLinearUpperLimit(toVector3(wrapper.getArrayNumber("linearUpperLimit"), constraintData.getLinearUpperLimit()));
		
		
		
		return constraintData;
	}
	private Vector3 toVector3(@Nullable JsArrayNumber array,Vector3 defaultVector3){
		if(array==null || array.length()!=3){
			return defaultVector3;
		}
		return THREE.Vector3().fromArray(array);
	}

}
