package com.akjava.gwt.clothhair.client.lights;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.lights.DirectionalLight;
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
			updateCastShadow(data, (DirectionalLight)light.cast());
		}else if(data.getType()==LightData.HEMISPHERE){
			light=THREE.HemisphereLight(data.getColor(), data.getColor2(), data.getIntensity());;
		}
		light.setName(data.getName());
		light.getPosition().copy(data.getPosition());
		
		scene.add(light);
		data.setLight(light);
	}
	
	public static void addLight(Scene scene,String csv){
		 Iterable<LightData> newDatas=new LightDataConverter().reverse().convertAll(CSVUtils.splitLinesWithGuava(csv));
		 for(LightData newData:newDatas){
			 addLight(scene,newData);
		 }
	}
	
	/*
	 * this shadow for scale 1000
	 */
	public static void updateCastShadow(LightData data,DirectionalLight light){
		if(data.isCastShadow()){
			light.setCastShadow(true);
			
			DirectionalLight dlight=light.cast();
			LogUtils.log("light-position");
			LogUtils.log(data.getPosition());
			
			/*
			 * if your shadow is dirty increse this.
			 */
			dlight.getShadow().getMapSize().set(2048, 2048);
			
			//if your shadow is dot ,default value is 5
			int d = 2000;
			dlight.gwtGetShadowCamera().setLeft(-d);
			dlight.gwtGetShadowCamera().setRight(d);
			dlight.gwtGetShadowCamera().setTop(d);
			dlight.gwtGetShadowCamera().setBottom(-d);
			
			//default far is 500
			//if nothing shadow,
			
			dlight.gwtGetShadowCamera().setFar(50000);
			}
		else{
			light.setCastShadow(false);
		}
	}
}
