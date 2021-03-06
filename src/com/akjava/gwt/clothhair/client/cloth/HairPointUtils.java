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
	public static void updateCircleStyle(HairCloth hairCloth,Vector3 firstPoint,Vector3 secondPoint,boolean hairPinOnly){
		
		
		
		boolean startCenter=hairCloth.isStartCircleCenter();//start circle from center or not
		boolean startAndEndSame=hairCloth.isStartAndEndSameCircle();
		
		boolean useFirstPointY=hairCloth.isUseFirstPointY();
		
		
		//TODO parameter,to keep extrude shape same direction,not work correctly
		if(firstPoint.getX() == secondPoint.getX()){
			secondPoint.gwtIncrementX(0.001);
		}
		if(firstPoint.getZ() == secondPoint.getZ()){
			secondPoint.gwtIncrementZ(0.001);
		}
		
		
		int sliceFaceCount=hairCloth.getSliceFaceCount();
		
		int angleSplit=startAndEndSame?sliceFaceCount:sliceFaceCount+1;
		
		Vector2 center=THREE.Vector2(firstPoint.getX(), firstPoint.getZ());
		Vector2 point=THREE.Vector2(secondPoint.getX(), secondPoint.getZ());
		List<Vector3> corePositions=Lists.newArrayList();
		
		double perAngle=360.0/(angleSplit); //not support connect-horizontal
		
		//make rounding position
		for(int i=0;i<=sliceFaceCount;i++){//+1
			Vector2 rotated=point.clone().rotateAround(center, Math.toRadians(perAngle*i));
			//ThreeLog.log("angle:"+(perAngle*i),rotated);
			
			if(useFirstPointY){
				corePositions.add(THREE.Vector3(rotated.getX(), firstPoint.getY(), rotated.getY()));
			}else{
				corePositions.add(THREE.Vector3(rotated.getX(), secondPoint.getY(), rotated.getY()));
			}
			
		}
		
		for(int i=0;i<=sliceFaceCount;i++){
			//TODO support merge center option
			Vector3 centerPoint=firstPoint.clone().add(corePositions.get(i).clone().sub(firstPoint).normalize());//avoid same pos
		//	ThreeLog.log("center",centerPoint);
			if(startCenter){//no hole,but unstable as physics
				//LogUtils.log("center");
				hairCloth.particles.get(i).setAllPosition(centerPoint);
				}
			else{//make hole
				hairCloth.particles.get(i).setAllPosition(corePositions.get(i));
			}
		}
		
		if(hairPinOnly){
			return;
		}
		
	//	ThreeLog.log("center",firstPoint);
	//	ThreeLog.log("add",secondPoint);
		
		for(int j=hairCloth.getW()+1;j<hairCloth.particles.size();j++){
			int x=j%(hairCloth.getW()+1);
			int y=(j/(hairCloth.getW()+1) );
			
			Vector3 delta=corePositions.get(x).clone().sub(firstPoint).setY(0);
			double length=delta.length();
			//delta.normalize().multiplyScalar(length);
			// trying merge normal but faild
			 
			if(hairCloth.getHairData().isUseCustomNormal()){
				Vector3 customNormal=hairCloth.getHairData().getCustomNormal();
				Vector3 vector=customNormal.clone().normalize().lerp(delta.clone().normalize(), hairCloth.getHairData().getOriginalNormalRatio());
				
				delta=vector.multiplyScalar(length);
			}
			
			
			
			
			Vector3 newPosition=delta.multiplyScalar(y* hairCloth.getHairData().getScaleOfU());
			
			
			double angle=x*perAngle;
			if(hairCloth.isAmmoInCircleInRange(angle)){
				newPosition.multiplyScalar(hairCloth.getAmmoCircleInRangeRatio());
			}
			
			
			if(startCenter){
				newPosition.add(firstPoint);
				//no need
			}else{
				newPosition.add(corePositions.get(x));
			}
			
			//ThreeLog.log("j="+j+",x="+x+",y="+y,newPosition);
			
			hairCloth.particles.get(j).setAllPosition(newPosition);
		}
		
		//update pins
		int[] newPins=new int[sliceFaceCount+1];
		for(int i=0;i<=sliceFaceCount;i++){
			newPins[i]=i;
		}
		
		hairCloth.setPins(newPins);
	
	}
}
