package com.akjava.gwt.clothhair.client.cloth;

import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.java.ThreeLog;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.math.Vector2;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.google.common.collect.Lists;

public class HairPointUtils {
	private HairPointUtils(){}
	//two point style
	
	public static void updateFirstRowStyle(HairCloth hairCloth,List<Vector3> generalPinVertex,List<Vector3> customlPinVertex,List<Integer> customPinTarget){
		int generalPinSize=generalPinVertex.size();
		int sliceFaceCount=hairCloth.getSliceFaceCount();
		
		for(int i=0;i<generalPinSize;i++){
			Vector3 v1=generalPinVertex.get(i);//data.getCalculator().getResult().get(i);
			
			int index=sliceFaceCount*i;
			
			hairCloth.particles.get(index).setAllPosition(v1);
			
			
			if(i!=generalPinSize-1){ //has next;
				Vector3 v2=generalPinVertex.get(i+1);
				for(int j=1;j<sliceFaceCount;j++){//0 is core
					int at=index+j;
					
					double percent=(double)j/sliceFaceCount;
					if(hairCloth.isPinned(at)){
					hairCloth.particles.get(at).setAllPosition(v1.clone().lerp(v2, percent));
					}else{
						
					}
					
				}
				
			}
		}
		
		//handle custom pin not tested so much
		for(int i=0;i<customlPinVertex.size();i++){
			Vector3 v=customlPinVertex.get(i);
			
			if(hairCloth.isPinned(customPinTarget.get(i))){
				hairCloth.particles.get(customPinTarget.get(i)).setAllPosition(v);
			}
		}
		
	}
	public static void updateCircleStyle(HairCloth hairCloth,Vector3 centerPoint,Vector3 addPoint,boolean hairPinOnly){
		
		
		
		boolean startCenter=hairCloth.isStartCircleCenter();//start circle from center or not
		boolean startAndEndSame=hairCloth.isStartAndEndSameCircle();
		
		boolean useFirstPointY=hairCloth.isUseFirstPointY();
		
		
		//TODO parameter,to keep extrude shape same direction
		if(centerPoint.getX() == addPoint.getX()){
			addPoint.gwtIncrementX(0.001);
		}
		if(centerPoint.getZ() == addPoint.getZ()){
			addPoint.gwtIncrementZ(0.001);
		}
		
		
		int cw=hairCloth.getSliceFaceCount();
		
		int angleSplit=startAndEndSame?cw:cw+1;
		
		Vector2 center=THREE.Vector2(centerPoint.getX(), centerPoint.getZ());
		Vector2 point=THREE.Vector2(addPoint.getX(), addPoint.getZ());
		List<Vector3> corePositions=Lists.newArrayList();
		
		double perAngle=360.0/(angleSplit); //not support connect-horizontal
		
		for(int i=0;i<=cw;i++){//+1
			Vector2 rotated=point.clone().rotateAround(center, Math.toRadians(perAngle*i));
			//ThreeLog.log("angle:"+(perAngle*i),rotated);
			
			if(useFirstPointY){
				corePositions.add(THREE.Vector3(rotated.getX(), centerPoint.getY(), rotated.getY()));
			}else{
				corePositions.add(THREE.Vector3(rotated.getX(), addPoint.getY(), rotated.getY()));
			}
			
		}
		
		for(int i=0;i<=cw;i++){
			if(startCenter){//no hole,but unstable as physics
				hairCloth.particles.get(i).setAllPosition(centerPoint);
				}
			else{//make hole
				hairCloth.particles.get(i).setAllPosition(corePositions.get(i));
			}
		}
		
		if(hairPinOnly){
			return;
		}
		
		ThreeLog.log("center",centerPoint);
		ThreeLog.log("add",addPoint);
		
		for(int j=hairCloth.getW()+1;j<hairCloth.particles.size();j++){
			int x=j%(hairCloth.getW()+1);
			int y=j/(hairCloth.getW()+1);
			
			Vector3 delta=corePositions.get(x).clone().sub(centerPoint).setY(0);
			Vector3 newPosition=delta.multiplyScalar(y);
			
			
			double angle=x*perAngle;
			if(hairCloth.isAmmoInCircleInRange(angle)){
				newPosition.multiplyScalar(hairCloth.getAmmoCircleInRangeRatio());
			}
			
			
			if(startCenter){
				newPosition.add(centerPoint);
				//no need
			}else{
				newPosition.add(corePositions.get(x));
			}
			
			//ThreeLog.log("j="+j+",x="+x+",y="+y,newPosition);
			
			hairCloth.particles.get(j).setAllPosition(newPosition);
		}
	}
}
