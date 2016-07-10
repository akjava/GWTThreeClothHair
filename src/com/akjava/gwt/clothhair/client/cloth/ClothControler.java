package com.akjava.gwt.clothhair.client.cloth;

import java.util.List;
import java.util.Map;

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
		data.getCloth().wind=wind;
		data.getCloth().setFloorModifier(floorModifier);
		cloths.add(data);
	}
	public void removeClothData(ClothData data){
		cloths.remove(data);
	}
	
	//made before update
	public void beforeSimulate(ClothSimulator simulator){
		syncPins();
		
		for(ClothData data:cloths){
			HairCloth cloth=data.getCloth();
			Geometry clothGeometry=data.getClothGeometry();
			
			cloth.beforeSimulate(simulator,clothGeometry,getSphereList(cloth.getChannel()));//set otherwhere?
			}
	}
	
	public void afterSimulate(ClothSimulator simulator,double time){
		
		double windStrength= Math.cos( time / 7000 ) * 20 + 40;;
		
		Vector3 windForce=THREE.Vector3().set(Math.sin( time / 2000 ), Math.cos( time / 3000 ), Math.sin( time / 1000 ) ).normalize().multiplyScalar( windStrength );
		for(ClothData data:cloths){
			HairCloth cloth=data.getCloth();
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
			HairCloth cloth=data.getCloth();
			cloth.setFloorModifier(floorModifier);
		}
	}


	private void syncPinPositions(ClothData data) {
		if(data.getCalculator().getResult().size()<2){
			LogUtils.log("syncPinPositions:invalid pin-size:"+data.getCalculator().getResult().size());
			return;
		}
		
		boolean startCenter=data.getCloth().isStartCircleCenter();
		boolean startAndEndSame=data.getCloth().isStartAndEndSameCircle();
		
		if(data.getCalculator().getResult().size()==2){
			//TODO merge method
			Vector3 v1=data.getCalculator().getResult().get(0);
			Vector3 v2=data.getCalculator().getResult().get(1);
			//TODO move and fix
			int cw=data.getCloth().w;
			int angleSplit=startAndEndSame?cw:cw+1;
			
			Vector2 center=THREE.Vector2(v1.getX(), v1.getZ());
			Vector2 point=THREE.Vector2(v2.getX(), v2.getZ());
			List<Vector3> corePositions=Lists.newArrayList();
			double perAngle=360.0/(angleSplit);
			for(int i=0;i<=cw;i++){
				Vector2 rotated=point.clone().rotateAround(center, Math.toRadians(perAngle*i));
				corePositions.add(THREE.Vector3(rotated.getX(), v1.getY(), rotated.getY()));
			}
			
			for(int i=0;i<=cw;i++){
				if(startCenter){
					data.getCloth().particles.get(i).setAllPosition(v1);
				}else{
					data.getCloth().particles.get(i).setAllPosition(corePositions.get(i));
				}
			}
			//TODO sync
		}else //old one not reach code
		if(data.getCalculator().getResult().size()<3){//2pins
			int cw=data.getCloth().w;
			Vector3 v1=data.getCalculator().getResult().get(0);
			Vector3 v2=data.getCalculator().getResult().get(1);
			
			Vector3 diff=THREE.Vector3();
			diff.copy(data.getCloth().particles.get(0).position).sub(v1);
			
			data.getCloth().particles.get(0).setAllPosition(v1);
			data.getCloth().particles.get(cw).setAllPosition(v2);
			
			
			Vector3 sub=v2.clone().sub(v1).divideScalar(cw+1);
			for(int i=1;i<cw;i++){
				Vector3 v=sub.clone().multiplyScalar(i).add(v1);
				data.getCloth().particles.get(i).setAllPosition(v);
			}
			
			if(data.getCloth().isSyncMove()){
				for(int i=cw;i<data.getCloth().particles.size();i++){
					data.getCloth().particles.get(i).position.sub(diff);
					data.getCloth().particles.get(i).previous.sub(diff);
				}
			}
			
			
		}else{
			
			//int pinSize=data.getCalculator().getResult().size();
			
			
			
			List<Vector3> generalPinVectors=Lists.newArrayList();
			List<Vector3> customlVector=Lists.newArrayList();
			List<Integer> customTarget=Lists.newArrayList();
			
			
			for(int i=0;i<data.getCalculator().getSkinningVertexs().size();i++){
				if(data.getCalculator().getSkinningVertexs().get(i).getTargetClothIndex()==-1){
					generalPinVectors.add(data.getCalculator().getResult().get(i));
				}else{
					customlVector.add(data.getCalculator().getResult().get(i));
					customTarget.add(data.getCalculator().getSkinningVertexs().get(i).getTargetClothIndex());
				}
			}
			
			/**
			 * 
			 how sync works
			 
			 pin-pos is never change without character moved.
			 so make a diff from preposition and newposition.
			 that is the moved.
			 
			 add all to help
			 * 
			 */
			
			int pinSize=generalPinVectors.size();
			
			int cw=data.getCloth().w/(pinSize-1);//TODO method
			
			Vector3 diff=THREE.Vector3();
			for(int i=0;i<pinSize;i++){
				Vector3 v1=generalPinVectors.get(i);//data.getCalculator().getResult().get(i);
				
				int index=cw*i;
				
				if(i==0){
					diff.copy(data.getCloth().particles.get(0).position).sub(v1);
				}
				
				if(data.getCloth().isPinned(index)){
					data.getCloth().particles.get(index).setAllPosition(v1);
				}
				//LogUtils.log("main:"+index);
				
				
				if(i!=pinSize-1){
					//has next;
					Vector3 v2=generalPinVectors.get(i+1);
					//Vector3 v2=data.getCalculator().getResult().get(i+1);
					
					
					Vector3 sub=v2.clone().sub(v1).divideScalar(cw);
					
					for(int j=1;j<cw;j++){
						int multiple=j;
						int at=index+j;
						Vector3 v=sub.clone().multiplyScalar(multiple).add(v1);
						
						if(data.getCloth().isPinned(at)){
							data.getCloth().particles.get(at).setAllPosition(v);
						}
						
						//LogUtils.log("sub:"+at);
					}
					
				}
			}
			
			
			//complete sync
			if(data.getCloth().isSyncMove()){
				for(int i=cw;i<data.getCloth().particles.size();i++){
					data.getCloth().particles.get(i).position.sub(diff);
					data.getCloth().particles.get(i).previous.sub(diff);
				}
			}
			
			//custom - update
			
			for(int i=0;i<customlVector.size();i++){
				Vector3 v=customlVector.get(i);
				
				if(data.getCloth().isPinned(customTarget.get(i))){
					data.getCloth().particles.get(customTarget.get(i)).setAllPosition(v);
				}
			}
			
		}
		//for(int i=0;i<data.getCalculator().g)
	}
	
	

	private boolean wind;
	public void setWind(boolean value){
		this.wind=value;
		for(ClothData data:cloths){
			data.getCloth().wind=value;
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
			HairCloth cloth=data.getCloth();
			Geometry clothGeometry=data.getClothGeometry();
			
			List<HairCloth.Particle> p = cloth.particles;

			
			
			if(cloth.isConnectHorizontal()){
			for(int x=0;x<=cloth.w;x++){
				for(int y=0;y<=cloth.h;y++){
					
					
					
					int pIndex=(cloth.w+1)*y+x;
					int gIndex=(cloth.w+1+1)*y+x;//some remains
					
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
				int pIndex=(cloth.w+1)*y;
				int gIndex=(cloth.w+1+1)*y+cloth.w+1;//extra
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
