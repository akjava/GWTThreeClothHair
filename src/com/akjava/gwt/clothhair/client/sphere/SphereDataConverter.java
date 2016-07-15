package com.akjava.gwt.clothhair.client.sphere;

import com.akjava.gwt.clothhair.client.JSONObjectWrapper;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.math.Quaternion;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.google.common.base.Converter;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.json.client.JSONObject;

public class SphereDataConverter extends Converter<SphereData,JSONObject>{

	@Override
	protected JSONObject doForward(SphereData data) {
		JSONObject object=new JSONObject();
		JSONObjectWrapper wrapper=new JSONObjectWrapper(object);
		
		if(data.getPosition()!=null){
			wrapper.setArrayNumber("position", data.getPosition().toArray());
		}
		
		if(data.getRotation()!=null){
			wrapper.setArrayNumber("rotation", data.getRotation().toArray());	
		}
		
		wrapper.setBoolean("enabled", data.isEnabled());
		wrapper.setBoolean("copyHorizontal", data.isCopyHorizontal());
		
		wrapper.setDouble("width", data.getWidth());
		wrapper.setDouble("height", data.getHeight());
		wrapper.setDouble("depth", data.getDepth());
		
		wrapper.setInt("type", data.getType());
		wrapper.setInt("boneIndex", data.getBoneIndex());
		wrapper.setInt("channel", data.getChannel());
		
		return object;
	}

	@Override
	protected SphereData doBackward(JSONObject object) {
		SphereData data=new SphereData();
		JSONObjectWrapper wrapper=new JSONObjectWrapper(object);
		
		
		JsArrayNumber position=wrapper.getArrayNumber("position");
		if(position!=null && position.length()==3){
			Vector3 pos=THREE.Vector3().fromArray(position);
			data.set(pos.getX(), pos.getY(), pos.getZ());
		}
		
		JsArrayNumber rotation=wrapper.getArrayNumber("rotation");	
		
		if(rotation!=null && rotation.length()==4){
			Quaternion q=THREE.Quaternion().fromArray(rotation);
			data.setRotattion(q);
		}
		
		data.setEnabled(wrapper.getBoolean("enabled", data.isEnabled()));
		data.setCopyHorizontal(wrapper.getBoolean("copyHorizontal", data.isCopyHorizontal()));
		
		data.setWidth(wrapper.getDouble("width", data.getWidth()));
		data.setHeight(wrapper.getDouble("height", data.getHeight()));
		data.setDepth(wrapper.getDouble("depth", data.getDepth()));
		
		data.setType(wrapper.getInt("type", data.getType()));
		data.setBoneIndex(wrapper.getInt("boneIndex", data.getBoneIndex()));
		data.setChannel(wrapper.getInt("channel", data.getChannel()));
		
		
		return data;
	}

}
