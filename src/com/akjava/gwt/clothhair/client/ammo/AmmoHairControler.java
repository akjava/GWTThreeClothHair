package com.akjava.gwt.clothhair.client.ammo;

import java.util.List;
import java.util.Map;

import com.akjava.gwt.clothhair.client.cloth.HairCloth;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.js.extras.helpers.SkeletonHelper;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;
import com.akjava.gwt.three.client.js.scenes.Scene;
import com.akjava.gwt.threeammo.client.AmmoBodyPropertyData;
import com.akjava.gwt.threeammo.client.AmmoConstraintPropertyData;
import com.akjava.gwt.threeammo.client.AmmoControler;
import com.akjava.gwt.threeammo.client.BodyAndMesh;
import com.akjava.gwt.threeammo.client.DistanceConstraintProperties;
import com.akjava.gwt.threeammo.client.core.Ammo;
import com.akjava.gwt.threeammo.client.core.constraints.btGeneric6DofSpringConstraint;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.JsArray;

public class AmmoHairControler {

	private boolean creating;//tested
	
	private boolean stopped;

	private AmmoControler ammoControler;

	public AmmoControler getAmmoControler() {
		return ammoControler;
	}


	public boolean isStopped() {
		return stopped;
	}


	public void setStopped(boolean stopped) {
		this.stopped = stopped;
	}


	public boolean isCreating() {
		return creating;
	}


	public boolean isEnabled(){
	return !creating && !stopped;
	}


	public void setCreating(boolean creating) {
		this.creating = creating;
	}

	
	public AmmoHairControler(Scene scene){
		ammoControler = new AmmoControler(scene,Ammo.initWorld(0,-100,0));
	}
	
	Map<HairCloth,ParticleBodyDatas> particleMap=Maps.newHashMap();
	public boolean isExistParticleData(HairCloth hairCloth) {
		return particleMap.get(hairCloth)!=null;
	}
	public boolean isExistSphereData(int channel) {
		return sphereMap.get(channel)!=null;
	}
	
	private boolean disableCollisionsBetweenLinkedBodies=true;
	public void setParticleData(HairCloth hairCloth,ParticleBodyDatas data){
		particleMap.put(hairCloth,data);
		LogUtils.log("particles:particle="+data.getAmmoParticles().size()+",const="+data.getConstraints().length());
		
		
		for(int i=0;i<data.getAmmoParticles().size();i++){
			
			//i'm not sure any reason, add here
			ammoControler.addBodyMesh(data.getAmmoParticles().get(i),1,2);
			//ammoControler.addBodyMesh(data.getAmmoParticles().get(i));
		}
		
		/*
		for(int i=0;i<data.getConstraints().length();i++){
			ammoControler.getWorld().addConstraint(data.getConstraints().get(i),disableCollisionsBetweenLinkedBodies);
		}
		*/
		
	}
	
	public void setSphereData(int channel,SphereBodyData data){
		sphereMap.put(channel,data);
		
		
		
		for(int i=0;i<data.getAmmoSpheres().size();i++){
			//spehre seems ok!

			//i'm not sure any reason, add here
			addSphereBodyData(data.getAmmoSpheres().get(i));
			//ammoControler.addBodyMesh(data.getAmmoSpheres().get(i));
		}
		
		
		LogUtils.log("set-sphere:channel="+channel+",sphere-count="+data.getAmmoSpheres().size());
		
		
	}
	
	public void addSphereBodyData(BodyAndMesh bm){
		ammoControler.addBodyMesh(bm,2,1);
	}
	public void removeSphereBodyData(BodyAndMesh bm){
		ammoControler.destroyBodyAndMesh(bm);
	}
	
	
	public void removeSphereData(int channel){
		SphereBodyData data=sphereMap.get(channel);
		LogUtils.log("sphere-removed:ch="+channel+" size="+data.getAmmoSpheres().size());
		for(int i=0;i<data.getAmmoSpheres().size();i++){
			ammoControler.destroyBodyAndMesh(data.getAmmoSpheres().get(i));
			//ammoControler.destroyBodyAndMesh(data.getAmmoSpheres().get(i));
		}
	}
	public void removeParticleData(HairCloth hairCloth){
		ParticleBodyDatas data=getAmmoData(hairCloth);
		
		if(data==null){
			LogUtils.log("removeParticleData-called but data not exist");
			return;
		}
		for(int i=0;i<data.getAmmoParticles().size();i++){
			ammoControler.destroyBodyAndMesh(data.getAmmoParticles().get(i));
		}
		
		for(int i=0;i<data.getConstraints().length();i++){
			ammoControler.destroyConstraintOnly(data.getConstraints().get(i));
		}
		
		if(data.getSkinnedMesh()!=null){
			ammoControler.getScene().remove(data.getSkinnedMesh());
		}
		
		if(data.getSkeltonHelper()!=null){
			ammoControler.getScene().remove(data.getSkeltonHelper());
		}
		
		particleMap.remove(hairCloth);
		
		LogUtils.log("removeParticleData:bm="+data.getAmmoParticles().size()+",const="+data.getConstraints().length());
		
	}
	public ParticleBodyDatas getAmmoData(HairCloth hairCloth){
		return particleMap.get(hairCloth);
	}
	public SphereBodyData getSphereData(int channel){
		return sphereMap.get(channel);
	}
	
	Map<Integer,SphereBodyData> sphereMap=Maps.newHashMap();
	

	public static class SphereBodyData{
		/*
		 * BodyAndMesh is js object
		 */
		List<BodyAndMesh> ammoSpheres;
		public SphereBodyData(List<BodyAndMesh> ammoSpheres) {
			super();
			this.ammoSpheres = ammoSpheres;
		}
		public List<BodyAndMesh> getAmmoSpheres() {
			return ammoSpheres;
		}
	}
	
	
	private AmmoConstraintPropertyData constraintProperties=new AmmoConstraintPropertyData();
	
	public AmmoConstraintPropertyData getParticleConstraintData() {
		return constraintProperties;
	}


	public void setParticleConstraintData(AmmoConstraintPropertyData constraintProperties) {
		this.constraintProperties = constraintProperties;
	}

	private AmmoBodyPropertyData clothProperties=new AmmoBodyPropertyData();
	public AmmoBodyPropertyData getParticleBodyData() {
		return clothProperties;
	}


	public void setParticleBodyData(AmmoBodyPropertyData clothMaterial) {
		this.clothProperties = clothMaterial;
	}
	

	private AmmoBodyPropertyData spherehProperties=new AmmoBodyPropertyData();
	
	public AmmoBodyPropertyData getCollisionProperties() {
		return spherehProperties;
	}


	public void setCollisionProperties(AmmoBodyPropertyData spherehMaterial) {
		this.spherehProperties = spherehMaterial;
	}


	public static class ParticleBodyDatas{
		private SkeletonHelper skeltonHelper;
		public SkeletonHelper getSkeltonHelper() {
			return skeltonHelper;
		}
		public void setSkeltonHelper(SkeletonHelper skeltonHelper) {
			this.skeltonHelper = skeltonHelper;
		}
		private SkinnedMesh skinnedMesh;//GPU version;
		public SkinnedMesh getSkinnedMesh() {
			return skinnedMesh;
		}
		public void setSkinnedMesh(SkinnedMesh skinnedMesh) {
			this.skinnedMesh = skinnedMesh;
		}
		public ParticleBodyDatas(List<BodyAndMesh> ammoParticles,JsArray<btGeneric6DofSpringConstraint> constraints) {
			super();
			this.ammoParticles = ammoParticles;
			this.constraints=constraints;
		}
		public List<BodyAndMesh> getAmmoParticles() {
			return ammoParticles;
		}
		List<BodyAndMesh> ammoParticles;
		JsArray<btGeneric6DofSpringConstraint> constraints;
		public JsArray<btGeneric6DofSpringConstraint> getConstraints() {
			return constraints;
		}
	}
	

	public String getInfo() {
		return "TODO:getInfo()";
	}


	public void updateVisibleBone(Boolean value) {
		visibleBone=value;
		for(ParticleBodyDatas bodyDatas:particleMap.values()){
			if(bodyDatas.getSkeltonHelper()!=null){
				bodyDatas.getSkeltonHelper().setVisible(value);
			}
		}
		
	}
	private boolean visibleBone;
	public boolean isVisibleBone() {
		return visibleBone;
	}

	private boolean visibleParticl;
	public boolean isVisibleParticl() {
		return visibleParticl;
	}


	public void updateVisibleParticle(Boolean value) {
		visibleParticl=value;
		for(ParticleBodyDatas bodyDatas:particleMap.values()){
			for(BodyAndMesh bm:bodyDatas.getAmmoParticles()){
				Mesh mesh=bm.getMesh();
				if(mesh!=null){
					mesh.getMaterial().setVisible(value);
				}
			}
		}
		
		for(SphereBodyData sphereData:sphereMap.values()){
			for(BodyAndMesh bm:sphereData.getAmmoSpheres()){
				Mesh mesh=bm.getMesh();
				if(mesh!=null){
					mesh.getMaterial().setVisible(value);
				}
			}
		}
		
	}
}
