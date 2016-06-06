package com.akjava.gwt.clothhair.client.cannon;

import java.util.Map;

import com.akjava.gwt.clothhair.client.GWTThreeClothHair;
import com.akjava.gwt.clothhair.client.cloth.HairCloth;
import com.akjava.gwt.lib.client.LogUtils;
import com.github.gwtcannonjs.client.CANNON;
import com.github.gwtcannonjs.client.constraints.DistanceConstraint;
import com.github.gwtcannonjs.client.material.ContactMaterial;
import com.github.gwtcannonjs.client.material.ContactMaterialOptions;
import com.github.gwtcannonjs.client.objects.Body;
import com.github.gwtcannonjs.client.shapes.Shape;
import com.github.gwtcannonjs.client.solver.GSSolver;
import com.github.gwtcannonjs.client.world.World;
import com.google.common.base.Joiner;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.JsArray;

public class CannonControler {
	private World world;
	
	private boolean creating;

	public boolean isCreating() {
		return creating;
	}


public boolean isEnabled(){
	return !creating;
}


	public void setCreating(boolean creating) {
		this.creating = creating;
	}

	private boolean hasItem;//TODO
	public CannonControler(){
		initializeCannon();
	}
	
	
	
	
	
	public void initializeCannon() {
		world = CANNON.newWorld();
		//world.setDt(1.0/30); //seems not effect
		
		world.getGravity().set(0,-9.82,0);//world.gravity.set(0,0,0);
		world.setBroadphase(CANNON.newNaiveBroadphase());//world.broadphase = new CANNON.NaiveBroadphase();
		((GSSolver)world.getSolver()).setIterations(10);//world.solver.iterations = 10;
		//((GSSolver)world.getSolver()).setTolerance(1);
		
		clothMaterial = CANNON.newMaterial();
		sphereMaterial = CANNON.newMaterial();
				ContactMaterialOptions options=CANNON.newContactMaterialOptions();
				//options.setFriction(0.9);
				//options.setRestitution(0.01);
				options.setFriction(0);
				options.setRestitution(0);
				
				ContactMaterial clothSphereContactMaterial = CANNON.newContactMaterial(  clothMaterial,
				sphereMaterial,options);
				
				// Adjust constraint equation parameters for ground/ground contact
				//clothSphereContactMaterial.setContactEquationStiffness(1e9);//clothSphereContactMaterial.contactEquationStiffness = 1e9;
				//clothSphereContactMaterial.setContactEquationRelaxation(3);//clothSphereContactMaterial.contactEquationRelaxation = 3;
				// Add contact material to the world
				world.addContactMaterial(clothSphereContactMaterial);
				
				
				//is this need?
				
				/*
				ContactMaterialOptions options2=CANNON.newContactMaterialOptions();
				options2.setFriction(0);
				options2.setRestitution(0);
				ContactMaterial clothSphereContactMaterial2 = CANNON.newContactMaterial(  clothMaterial,
						clothMaterial,options2);
				world.addContactMaterial(clothSphereContactMaterial2);
				*/
				
	}

	public World getWorld() {
		return world;
	}
	public boolean isExistParticleData(HairCloth hairCloth) {
		return particleMap.get(hairCloth)!=null;
	}
	public boolean isExistSphereData(int channel) {
		return sphereMap.get(channel)!=null;
	}
	
	public void setParticleData(HairCloth hairCloth,ParticleBodyData data){
		particleMap.put(hairCloth,data);
		for(int i=0;i<data.getCannonParticles().length();i++){
			world.addBody(data.getCannonParticles().get(i));
		}
		for(int i=0;i<data.getConstraints().length();i++){
			world.addConstraint(data.getConstraints().get(i));
		}
		
		//LogUtils.log(GWTThreeClothHair.INSTANCE.getCannonControler().getInfo());
	}
	
	public void setSphereData(int channel,SphereBodyData data){
		sphereMap.put(channel,data);
		
		for(int i=0;i<data.getCannonSpheres().length();i++){
			world.addBody(data.getCannonSpheres().get(i));
		}
		
		//LogUtils.log(GWTThreeClothHair.INSTANCE.getCannonControler().getInfo());
	}
	
	public void removeSphereData(int channel){
		SphereBodyData data=sphereMap.get(channel);
		for(int i=0;i<data.getCannonSpheres().length();i++){
			world.remove(data.getCannonSpheres().get(i));
		}
	}
	public void removeParticleData(HairCloth hairCloth){
		ParticleBodyData data=getCannonData(hairCloth);
		if(data==null){
			return;
		}
		for(int i=0;i<data.getCannonParticles().length();i++){
			world.remove(data.getCannonParticles().get(i));
		}
		for(int i=0;i<data.getConstraints().length();i++){
			world.removeConstraint(data.getConstraints().get(i));
		}
		LogUtils.log(GWTThreeClothHair.INSTANCE.getCannonControler().getInfo());
	}
	public ParticleBodyData getCannonData(HairCloth hairCloth){
		return particleMap.get(hairCloth);
	}
	public SphereBodyData getSphereData(int channel){
		return sphereMap.get(channel);
	}
	
	Map<HairCloth,ParticleBodyData> particleMap=Maps.newHashMap();
	Map<Integer,SphereBodyData> sphereMap=Maps.newHashMap();
	private com.github.gwtcannonjs.client.material.Material clothMaterial;
	public com.github.gwtcannonjs.client.material.Material getClothMaterial() {
		return clothMaterial;
	}
	public com.github.gwtcannonjs.client.material.Material getSphereMaterial() {
		return sphereMaterial;
	}

	private com.github.gwtcannonjs.client.material.Material sphereMaterial;
	
	public static class SphereBodyData{
		JsArray<Body> cannonSpheres;
		public SphereBodyData(JsArray<Body> cannonSpheres) {
			super();
			this.cannonSpheres = cannonSpheres;
		}
		public JsArray<Body> getCannonSpheres() {
			return cannonSpheres;
		}
	}
	public static class ParticleBodyData{
		
		public ParticleBodyData(JsArray<Body> cannonParticles,JsArray<DistanceConstraint> constraints) {
			super();
			this.cannonParticles = cannonParticles;
			this.constraints=constraints;
		}
		public JsArray<Body> getCannonParticles() {
			return cannonParticles;
		}
		JsArray<Body> cannonParticles;
		JsArray<DistanceConstraint> constraints;
		public JsArray<DistanceConstraint> getConstraints() {
			return constraints;
		}
	}
	
	public final native void test()/*-{
		
		console.log($wnd.CANNON.Shape.types.SPHERE);
	}-*/;
	
	public String getInfo() {
		//test();
		Map<String,String> infomap=Maps.newLinkedHashMap();
		
		//LogUtils.log("sphere:"+Shape.SPHERE+",particle="+Shape.PARTICLE);
		
		int spheres=0;
		int particle=0;
		int others=0;
		for(int i=0;i<world.getBodies().length();i++){
			Body body=world.getBodies().get(i);
			for(int j=0;j<body.getShapes().length();j++){
				Shape shape=body.getShapes().get(j);
				double v=shape.getType();
				if(v==Shape.SPHERE){
					spheres++;
				}else if(v==Shape.PARTICLE){
					particle++;
				}else{
					others++;
				}
			}
		}
		infomap.put("bodies", String.valueOf(world.getBodies().length()));
		infomap.put("consts", String.valueOf(world.getConstraints().length()));
		infomap.put("spheres", String.valueOf(spheres));
		infomap.put("particle", String.valueOf(particle));
		infomap.put("others", String.valueOf(others));
		
		return Joiner.on(" ").withKeyValueSeparator("=").join(infomap);
	}
}
