package com.akjava.gwt.clothhair.client.cloth;

import java.util.List;
import java.util.Map;

import com.akjava.gwt.clothhair.client.hair.HairData;
import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.java.ThreeLog;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.math.Vector2;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

public class ClothControler {
	private List<ClothData> cloths=Lists.newArrayList();
	
	public List<ClothData> getCloths() {
		return cloths;
	}

	Map<Integer,List<Mesh>> spheresMap=Maps.newHashMap();
	//List<Mesh> spheres=Lists.newArrayList();
	
	//List<Mesh> spheres=Lists.newArrayList();
	
	//private Mesh sphere;//TODO multiple
	
	
	public List<Mesh> getSphereList(int channel){
		List<Mesh> list=spheresMap.get(channel);
		if(list==null){
			list=Lists.newArrayList();
			spheresMap.put(channel, list);
		}
		return list;
	}
	
	public void addSphere(Mesh data,int channel){
		getSphereList(channel).add(data);
		//LogUtils.log("added  ch:"+channel+"="+getSphereList(channel).size());
	}
	
	public void updateSphere(Mesh data,int channel){
		removeSphere(data);
		addSphere(data,channel);
	}
	
	
	public ClothControler() {
		super();
	}

	public void addClothData(ClothData data){
		data.getHairCloth().wind=wind;
		data.getHairCloth().setFloorModifier(floorModifier);
		cloths.add(data);
	}
	public void removeClothData(ClothData data){
		cloths.remove(data);
	}
	
	//made before update
	public void beforeSimulate(ClothSimulator simulator){
		syncPins();
		
		for(ClothData data:cloths){
			HairCloth cloth=data.getHairCloth();
			Geometry clothGeometry=data.getClothGeometry();
			
			cloth.beforeSimulate(simulator,clothGeometry,getSphereList(cloth.getChannel()));//set otherwhere?
			}
	}
	
	public void afterSimulate(ClothSimulator simulator,double time){
		
		double windStrength= Math.cos( time / 7000 ) * 20 + 40;;
		
		Vector3 windForce=THREE.Vector3().set(Math.sin( time / 2000 ), Math.cos( time / 3000 ), Math.sin( time / 1000 ) ).normalize().multiplyScalar( windStrength );
		for(ClothData data:cloths){
			HairCloth cloth=data.getHairCloth();
			//cloth.wind=true;
			Geometry clothGeometry=data.getClothGeometry();
			
			
			//skinning-pin
			
			
			cloth.windStrength =windStrength;
			cloth.windForce.copy(windForce);
			
			
			//arrow.setLength( cloth.windStrength );
			//arrow.setDirection( cloth.windForce );
			
			//support-matrix4
			
			//should switch to sphere
			cloth.afterSimulate(simulator,time,clothGeometry,getSphereList(cloth.getChannel()));//set otherwhere?
		}
		
		renderCloth();//cloth-vertex to three.js object
		
	}
	
	private void syncPins() {
		for(ClothData data:cloths){
			data.getCalculator().update();
			
			syncPinPositions(data);
			
		}
	}
	
	private FloorModifier floorModifier;
	

	public FloorModifier getFloorModifier() {
		return floorModifier;
	}


	public void setFloorModifier(FloorModifier floorModifier) {
		this.floorModifier = floorModifier;
		for(ClothData data:cloths){
			HairCloth cloth=data.getHairCloth();
			cloth.setFloorModifier(floorModifier);
		}
	}


	private void syncPinPositions(ClothData data) {
		if(data.getCalculator().getResult().size()<2){
			LogUtils.log("syncPinPositions:invalid pin-size:"+data.getCalculator().getResult().size());
			return;
		}
		
		boolean startCenter=data.getHairCloth().isStartCircleCenter();
		boolean startAndEndSame=data.getHairCloth().isStartAndEndSameCircle();
		
		boolean useFirstPointY=data.getHairCloth().isUseFirstPointY();
		
		if(data.getHairCloth().getHairData().getPointMode()==HairData.POINT_MODE_SEMI_AUTO){
			for(int i=0;i<data.getCalculator().getResult().size();i++){
				Vector3 pos=data.getCalculator().getResult().get(i);//pos is scall upped by calcurateor
				int index=data.getHairCloth().getPins()[i];
				data.getHairCloth().getParticles().get(index).setAllPosition(pos);
				//LogUtils.log("syn-pin:"+index);
			}
		}
		else if(data.getCalculator().getResult().size()==2 && data.getHairCloth().isCircleStyle()){
			//TODO merge method
			Vector3 v1=data.getCalculator().getResult().get(0);
			Vector3 v2=data.getCalculator().getResult().get(1);
			
			HairPointUtils.updateCircleStyle(data.getHairCloth(), v1, v2,true);
			/*
			//TODO move and fix
			int cw=data.getCloth().getW();
			int angleSplit=startAndEndSame?cw:cw+1;
			
			Vector2 center=THREE.Vector2(v1.getX(), v1.getZ());
			Vector2 point=THREE.Vector2(v2.getX(), v2.getZ());
			List<Vector3> corePositions=Lists.newArrayList();
			double perAngle=360.0/(angleSplit);
			for(int i=0;i<=cw;i++){
				Vector2 rotated=point.clone().rotateAround(center, Math.toRadians(perAngle*i));
				
				if(useFirstPointY){
					corePositions.add(THREE.Vector3(rotated.getX(), v1.getY(), rotated.getY()));
				}else{
					corePositions.add(THREE.Vector3(rotated.getX(), v2.getY(), rotated.getY()));
				}
				
				
			}
			
			for(int i=0;i<=cw;i++){
				if(startCenter){
					data.getCloth().particles.get(i).setAllPosition(v1);
				}else{
					data.getCloth().particles.get(i).setAllPosition(corePositions.get(i));
				}
			}
			*/
			//TODO sync
		}
		/*else //old one not reach code
		if(data.getCalculator().getResult().size()<3){//2pins
			int cw=data.getHairCloth().getW();
			Vector3 v1=data.getCalculator().getResult().get(0);
			Vector3 v2=data.getCalculator().getResult().get(1);
			
			Vector3 diff=THREE.Vector3();
			diff.copy(data.getHairCloth().particles.get(0).position).sub(v1);
			
			data.getHairCloth().particles.get(0).setAllPosition(v1);
			data.getHairCloth().particles.get(cw).setAllPosition(v2);
			
			
			Vector3 sub=v2.clone().sub(v1).divideScalar(cw+1);
			for(int i=1;i<cw;i++){
				Vector3 v=sub.clone().multiplyScalar(i).add(v1);
				data.getHairCloth().particles.get(i).setAllPosition(v);
			}
			
			if(data.getHairCloth().isSyncMove()){
				for(int i=cw;i<data.getHairCloth().particles.size();i++){
					data.getHairCloth().particles.get(i).position.sub(diff);
					data.getHairCloth().particles.get(i).previous.sub(diff);
				}
			}
			
			
		}*/else{
			
			//int pinSize=data.getCalculator().getResult().size();
			
			
			
			List<Vector3> generalPinVertex=Lists.newArrayList();
			List<Vector3> customlPinVertex=Lists.newArrayList();
			List<Integer> customPinTarget=Lists.newArrayList();
			
			//LogUtils.log("pinSize:"+data.getCalculator().getSkinningVertexs().size());
			
			for(int i=0;i<data.getCalculator().getSkinningVertexs().size();i++){
				if(data.getCalculator().getSkinningVertexs().get(i).getTargetClothIndex()==-1){
					generalPinVertex.add(data.getCalculator().getResult().get(i));
				}else{
					customlPinVertex.add(data.getCalculator().getResult().get(i));
					customPinTarget.add(data.getCalculator().getSkinningVertexs().get(i).getTargetClothIndex());
				}
			}
			
			
			
			/*int generalPinSize=generalPinVertex.size();*/
			
			//int sizeOfU=data.getHairCloth().getW()/(generalPinSize-1);//TODO change just size of U
			/*if(sizeOfU!=data.getHairCloth().getSliceFaceCount()){
				LogUtils.log("invalid size of u:"+sizeOfU);
			}*/
			
			/*int sliceFaceCount=data.getHairCloth().getSliceFaceCount();*/
			
			
			//for later sync need before update
			Vector3 diff=THREE.Vector3();
			diff.copy(data.getHairCloth().particles.get(0).position).sub(generalPinVertex.get(0));
			
			
			HairPointUtils.updateFirstRowStyle(data.getHairCloth(), generalPinVertex, customlPinVertex, customPinTarget);
			
			
			/**
			 * 
			 how sync works
			 
			 pin-pos is never change without character moved.
			 so make a diff from preposition and newposition.
			 that is the moved.
			 
			 add all to help
			 * 
			 */
			
			//complete sync
			if(data.getHairCloth().isSyncMove()){
				//no static pin need move
				for(int i=data.getHairCloth().getW();i<data.getHairCloth().particles.size();i++){
					data.getHairCloth().particles.get(i).position.sub(diff);
					data.getHairCloth().particles.get(i).previous.sub(diff);
				}
			}
			
			
			
		}
		//for(int i=0;i<data.getCalculator().g)
	}
	
	

	private boolean wind;
	public void setWind(boolean value){
		this.wind=value;
		for(ClothData data:cloths){
			data.getHairCloth().wind=value;
		}
	}
	
	
	
	
	public void afterSimulatex(double time){
		
	}
	
	//temporaly
	//TODO supoort multi ball
	
	/*
	public void updateBallSize(int ballSize){
		
		for(ClothData data:cloths){
			HairCloth cloth=data.getCloth();
			cloth.ballSize=ballSize;
		}
	}
	*/
	
	boolean debugfirstTime=false;
	public void renderCloth(){
		for(ClothData data:cloths){
			HairCloth cloth=data.getHairCloth();
			Geometry clothGeometry=data.getClothGeometry();
			
			List<HairCloth.Particle> p = cloth.particles;

			
			
			if(cloth.isConnectHorizontal()){
			for(int x=0;x<=cloth.getW();x++){
				for(int y=0;y<=cloth.h;y++){
					
					
					
					int pIndex=(cloth.getW()+1)*y+x;
					int gIndex=(cloth.getW()+1+1)*y+x;//some remains
					
					if(!debugfirstTime){
						Map<String,Integer> debugMap=Maps.newLinkedHashMap();
						debugMap.put("x", x);
						debugMap.put("y", y);
						debugMap.put("pIndex", pIndex);
						debugMap.put("gIndex", gIndex);
						}
					
					
					clothGeometry.getVertices().get(gIndex).copy( p.get(pIndex).position);
				}
			}
			for(int y=0;y<=cloth.h;y++){
				int pIndex=(cloth.getW()+1)*y;
				int gIndex=(cloth.getW()+1+1)*y+cloth.getW()+1;//extra
				clothGeometry.getVertices().get(gIndex).copy( p.get(pIndex).position);
			}
			}else{
			for ( int i = 0, il = p.size(); i < il; i ++ ) {
				
				clothGeometry.getVertices().get(i).copy( p.get(i).position);

			}
			}
			
			//try edging,not so good than expected
			
			/*
			int endCenter=p.size()-cloth.w/2;
			for(int i=p.size()-cloth.w-1;i<p.size();i++){
				clothGeometry.getVertices().get(i).copy( p.get(endCenter).position);
			}
			*/
			

			
			clothGeometry.computeFaceNormals();
			clothGeometry.computeVertexNormals();

			clothGeometry.setNormalsNeedUpdate(true);//clothGeometry.normalsNeedUpdate = true;
			clothGeometry.setVerticesNeedUpdate(true);//clothGeometry.verticesNeedUpdate = true;

			//seems not so heavy
			clothGeometry.computeBoundingSphere();//TODO call separately?
			
			//sphere.getPosition().copy( cloth.ballPosition );//sphere.position.copy( ballPosition );
		}
		
		debugfirstTime=true;
	}

	public void removeSphere(Mesh data) {
		for(Integer key:spheresMap.keySet()){
			boolean result=spheresMap.get(key).remove(data);//if exist
			if(result){
				//LogUtils.log("sphere-removed-ch="+key);
			}
			//LogUtils.log("ch:"+key+"="+getSphereList(key).size());
		}
	}
}
