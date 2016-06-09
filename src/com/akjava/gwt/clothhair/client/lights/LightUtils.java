package com.akjava.gwt.clothhair.client.lights;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.lights.Light;
import com.akjava.gwt.three.client.js.scenes.Scene;
import com.akjava.lib.common.utils.CSVUtils;

public class LightUtils {
	private LightUtils(){}
	public static void addLight(Scene scene,LightData data){
		Light light=null;
		if(data.getType()==LightData.AMBIENT){
			light=THREE.AmbientLight(data.getColor(),data.getIntensity());
			LogUtils.log(light);
		}else if(data.getType()==LightData.DIRECTIONAL){
			light=THREE.DirectionalLight(data.getColor(),data.getIntensity());
		}else if(data.getType()==LightData.HEMISPHERE){
			light=THREE.HemisphereLight(data.getColor(), data.getColor2(), data.getIntensity());;
		}
		light.setName(data.getName());
		light.getPosition().copy(data.getPosition());
		
		scene.add(light);
	}
	
	public static void addLight(Scene scene,String csv){
		 Iterable<LightData> newDatas=new LightDataConverter().reverse().convertAll(CSVUtils.splitLinesWithGuava(csv));
		 for(LightData newData:newDatas){
			 addLight(scene,newData);
		 }
	}
}
