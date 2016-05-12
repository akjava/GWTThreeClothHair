package com.akjava.gwt.clothhair.client.cloth;

import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.google.common.collect.Lists;

public class ClothControler {
	private List<ClothData> cloths=Lists.newArrayList();
	
	public List<ClothData> getCloths() {
		return cloths;
	}

	List<Mesh> spheres=Lists.newArrayList();
	
	//List<Mesh> spheres=Lists.newArrayList();
	
	//private Mesh sphere;//TODO multiple
	
	
	public void addSphere(Mesh data){
		spheres.add(data);
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
	
	public void update(double time){
		syncPins();
		animateCloth(time);
		renderCloth();
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
			
			if(data.getCloth().syncMove){
				for(int i=cw;i<data.getCloth().particles.size();i++){
					data.getCloth().particles.get(i).position.sub(diff);
					data.getCloth().particles.get(i).previous.sub(diff);
				}
			}
			
			
		}else{
			int cw=data.getCloth().w/(data.getCalculator().getResult().size()-1);
			int pinSize=data.getCalculator().getResult().size();
			
			Vector3 diff=THREE.Vector3();
			for(int i=0;i<pinSize;i++){
				Vector3 v1=data.getCalculator().getResult().get(i);
				
				int index=cw*i;
				
				if(i==0){
					diff.copy(data.getCloth().particles.get(0).position).sub(v1);
				}
				
				data.getCloth().particles.get(index).setAllPosition(v1);
				
				//LogUtils.log("main:"+index);
				
				
				if(i!=pinSize-1){
					//has next;
					Vector3 v2=data.getCalculator().getResult().get(i+1);
					Vector3 sub=v2.clone().sub(v1).divideScalar(cw);
					
					for(int j=1;j<cw;j++){
						int multiple=j;
						int at=index+j;
						Vector3 v=sub.clone().multiplyScalar(multiple).add(v1);
						data.getCloth().particles.get(at).setAllPosition(v);
						
						//LogUtils.log("sub:"+at);
					}
					
				}
			}
			
			//complete sync
			
			if(data.getCloth().syncMove){
				for(int i=cw;i<data.getCloth().particles.size();i++){
					data.getCloth().particles.get(i).position.sub(diff);
					data.getCloth().particles.get(i).previous.sub(diff);
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
	
	
	
	
	public void animateCloth(double time){
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
			cloth.simulate(time,clothGeometry,spheres);//set otherwhere?
		}
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
	
	public void renderCloth(){
		for(ClothData data:cloths){
			HairCloth cloth=data.getCloth();
			Geometry clothGeometry=data.getClothGeometry();
			
			List<HairCloth.Particle> p = cloth.particles;

			for ( int i = 0, il = p.size(); i < il; i ++ ) {

				clothGeometry.getVertices().get(i).copy( p.get(i).position);

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
	}

	public void removeSphere(Mesh data) {
		spheres.remove(data);
	}
}
