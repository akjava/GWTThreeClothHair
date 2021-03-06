package com.akjava.gwt.clothhair.client.cloth;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import javax.annotation.Nullable;


import com.akjava.gwt.clothhair.client.GWTThreeClothHair.SphereCalculatorAndMesh;
import com.akjava.gwt.clothhair.client.SkinningVertexCalculator;
import com.akjava.gwt.clothhair.client.SkinningVertexCalculator.SkinningVertex;
import com.akjava.gwt.clothhair.client.ammo.AmmoHairControler;
import com.akjava.gwt.clothhair.client.ammo.AmmoHairControler.ParticleBodyDatas;
import com.akjava.gwt.clothhair.client.hair.HairData;
import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.akjava.gwt.clothhair.client.hair.HairDataPanel.HairMixedData;
import com.akjava.gwt.clothhair.client.hair.HairPinDataFunctions.HairPinToNormal;
import com.akjava.gwt.clothhair.client.hair.HairPinPredicates;
import com.akjava.gwt.clothhair.client.sphere.JsSphereData;
import com.akjava.gwt.clothhair.client.sphere.SphereData;
import com.akjava.gwt.clothhair.client.sphere.SphereDataCsvConverter;
import com.akjava.gwt.clothhair.client.texture.HairPatternDataUtils;
import com.akjava.gwt.clothhair.client.texture.HairTextureData;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.ImageElementListener;
import com.akjava.gwt.lib.client.ImageElementLoader;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Face3;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.extras.geometries.BoxGeometry;
import com.akjava.gwt.three.client.js.extras.geometries.SphereGeometry;
import com.akjava.gwt.three.client.js.materials.Material;
import com.akjava.gwt.three.client.js.materials.MeshPhongMaterial;
import com.akjava.gwt.three.client.js.math.Euler;
import com.akjava.gwt.three.client.js.math.Quaternion;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.objects.Bone;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;
import com.akjava.gwt.three.client.js.scenes.Scene;
import com.akjava.gwt.three.client.js.textures.Texture;
import com.akjava.gwt.threeammo.client.AmmoBodyPropertyData;
import com.akjava.gwt.threeammo.client.AmmoConstraintPropertyData;
import com.akjava.gwt.threeammo.client.BodyAndMesh;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.ColorUtils;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d.Composite;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ErrorEvent;

public class ClothSimulator  {
	
	private AmmoHairControler ammoHairControler;
	public AmmoHairControler getAmmoHairControler() {
		return ammoHairControler;
	}

	private Scene scene;
	private SkinnedMesh characterMesh;
	public SkinnedMesh getCharacterMesh() {
		return characterMesh;
	}

	public void setCharacterMesh(SkinnedMesh characterMesh) {
		this.characterMesh = characterMesh;
	}

	private SphereGeometry sphereGeometry;
	private BoxGeometry boxGeometry;
	public ClothSimulator(Scene scene,SkinnedMesh characterMesh){
		this(scene,characterMesh,new SimpleHaveClothSimulatorSettings());
	}
	private HaveClothSimulatorSettings haveClothSimulatorSettings;
	public ClothSimulator(Scene scene,SkinnedMesh characterMesh,HaveClothSimulatorSettings haveClothSimulatorSettings){
		this.scene=scene;
		this.characterMesh=characterMesh;
		this.haveClothSimulatorSettings=haveClothSimulatorSettings;
		clothControler=new ClothControler();
		
		ammoHairControler=new AmmoHairControler(scene,haveClothSimulatorSettings.getAmmoGravity(),haveClothSimulatorSettings.getAmmoSubsteps());
		
		updateAmmoProperties();
		
		
		sphereGeometry = THREE.SphereGeometry( 1, 20, 20 );
		boxGeometry = THREE.BoxGeometry( 2, 2, 2,10,10,10 );
		
		canvas = CanvasUtils.createCanvas(256, 256);
		canvas.setCoordinateSpaceWidth(512);
		canvas.setCoordinateSpaceHeight(512);
		
		canvas.setStyleName("transparent_bg");
	}
	public double getAmmoWorldScale(){
		return haveClothSimulatorSettings.getAmmoWorldScale();
	}
	
	private void updateAmmoProperties() {
		
		/*
		 * setting 1
		 */
		//this is easy recover
		AmmoBodyPropertyData collisionBodyData=haveClothSimulatorSettings.getAmmoCollisionBodyData();
		ammoHairControler.setCollisionProperties(collisionBodyData);
		
		//ammoHairControler.getCollisionProperties().setFriction(1);
		//ammoHairControler.getCollisionProperties().setRestitution(1);
		
		//init setting
		AmmoBodyPropertyData particleBodyData=haveClothSimulatorSettings.getAmmoParticleBodyData();
		ammoHairControler.setParticleBodyPropertyData(particleBodyData);
		
		//ammoHairControler.getParticleBodyData().setFriction(particleBodyData.getFriction());
		//ammoHairControler.getParticleBodyData().setRestitution(particleBodyData.getRestitution());
		//ammoHairControler.getParticleBodyData().setDamping(particleBodyData.getDamping().getX(),particleBodyData.getDamping().getY());
		
		AmmoConstraintPropertyData constraintCata=haveClothSimulatorSettings.getAmmoParticleConstraintData();
		ammoHairControler.setParticleConstraintData(constraintCata);
		
		//ammoHairControler.getParticleConstraintData().setEnableSpringsAll(true);
		//ammoHairControler.getParticleConstraintData().setStiffnessAll(10);//1000 is too strong?
		//ammoHairControler.getParticleConstraintData().setDampingAll(0);
		
		
		/* I'm not sure how effect
		ammoHairControler.getSpherehProperties().setFriction(10);
		ammoHairControler.getSpherehProperties().setRestitution(0);
		
		ammoHairControler.getParticleProperties().setFriction(10);
		ammoHairControler.getParticleProperties().setRestitution(0);
		ammoHairControler.getParticleProperties().setDamping(.001,.001);
		ammoHairControler.getConstraintProperties().setEnableSpringsAll(false);
		//ammoHairControler.getConstraintProperties().setStiffnessAll(1000);
		ammoHairControler.getConstraintProperties().setDampingAll(0);
		*/
		
		//for lighter
		//ammoHairControler.getParticleProperties().setDamping(1,1); //for lighter
		//ammoHairControler.getClothProperties().setDamping(0,0);
		
		//ammoHairControler.getConstraintProperties().setDisableCollisionsBetweenLinkedBodies(false);
		
		double mpi=Math.PI/2;
		ammoHairControler.getParticleConstraintData().setAngularLowerLimit(THREE.Vector3(-mpi, -mpi, -mpi));
		ammoHairControler.getParticleConstraintData().setAngularUpperLimit(THREE.Vector3(mpi, mpi, mpi));
		
		
		double v=0.1;
		//ammoHairControler.getConstraintProperties().setLinearLowerLimit(THREE.Vector3(-v, -v, -v));
		//ammoHairControler.getConstraintProperties().setLinearUpperLimit(THREE.Vector3(v, v,v));
	}


	private ClothControler clothControler;
	
	

	public ClothControler getClothControler() {
		return clothControler;
	}



	public void setClothControler(ClothControler clothControler) {
		this.clothControler = clothControler;
	}



	private double fps=1.0/120;
	
	public double getFps() {
		return fps;
	}

	public void setFps(double fps) {
		this.fps = fps;
		
	}

	/**
	 * right now current time.TODO change delta,right now old-cloth simulator use this as last-time
	 * @param timestamp
	 */
	public void update(double timestamp){
		updateSphereMeshs();//sphere first
		getClothControler().beforeSimulate(this);
		
		
		
		
		if(ammoHairControler.isEnabled()){
			//LogUtils.log("ammo-update");
			//ammoHairControler.getAmmoControler().setSubsteps(480);
			ammoHairControler.getAmmoControler().setFixedTimeStep(fps);//TODO set other location
			ammoHairControler.getAmmoControler().update(fps);
		}
		
		
		//do simulate
		getClothControler().afterSimulate(this,timestamp);//back to physics
	}
	
	Map<SphereData,SphereCalculatorAndMesh> sphereMeshMap=Maps.newHashMap();
	
	public Map<SphereData, SphereCalculatorAndMesh> getSphereMeshMap() {
		return sphereMeshMap;
	}

	public void setSphereMeshMap(Map<SphereData, SphereCalculatorAndMesh> sphereMeshMap) {
		this.sphereMeshMap = sphereMeshMap;
	}

	public Mesh getSphereMesh(SphereData data){
		return sphereMeshMap.get(data).getMesh();
	}
	public Mesh getMirrorSphereMesh(SphereData data){
		SphereData mirror=mirrorMap.get(data);
		if(mirror!=null){
		return sphereMeshMap.get(mirror).getMesh();
		}else{
			return null;
		}
	}
	
	private void removeSphereMesh(SphereData data){
		Mesh sphere=sphereMeshMap.get(data).getMesh();
		this.getClothControler().removeSphere(sphere);
		scene.remove(sphere);
	}
	
	public void removeSphereData(SphereData data){
		removeSphereMesh(data);
		
		SphereData data2=mirrorMap.get(data);
		if(data2!=null){
			removeSphereMesh(data2);
			sphereMeshMap.remove(data2);
			mirrorMap.remove(data);
		}
		
		sphereMeshMap.remove(data);
	}
	

	/*
	 * called when flushed from SphereDataEditor via SphereDataPanel
	 */
	public void syncSphereDataAndSkinningVertexCalculator(SphereData data,HaveReselectSphere hasReselectSphere){
		if(data==null){
			return;
		}
		
		SphereCalculatorAndMesh sm=sphereMeshMap.get(data);
		JsSphereData jsData=sm.getMesh().getUserData().cast();
		if(jsData==null){
			LogUtils.log("syncSphereDataAndSkinningVertexCalculator:some how jsData is null");
			return;
		}
		//difference case
		boolean needRecreate=false;
		if(jsData.getType()!=data.getType()){
			needRecreate=true;
		}
		
		//this type can't handle size with scale
		if(jsData.getType()==SphereData.TYPE_CAPSULE){
			if(jsData.getWidth()!=data.getWidth() || jsData.getHeight()!=data.getHeight()){
				needRecreate=true;
			}
		}
		
		if(needRecreate){
			//LogUtils.log("cloth-simulator:sphere need re-create");
			removeSphereData(data);
			addSphereData(data);
			hasReselectSphere.reselectSphere(data);//how to know
			return;
		}
		
		
		
		//update jsData
		
		//replace user data
		jsData=sphereDataToJsData(data);
		sphereMeshMap.get(data).getMesh().setUserData(jsData);
		
		//same time just update size & miror
		
		SkinningVertexCalculator calculator=sphereMeshMap.get(data).getCalculator();
		
		
		
		for(SkinningVertex vertex:calculator.getSkinningVertexs()){
			vertex.getSkinIndices().setX(data.getBoneIndex());
		}
		
		//plus sync channel
		this.getClothControler().updateSphere(sphereMeshMap.get(data).getMesh(), data.getChannel());
		
		if(data.isCopyHorizontal()){
			SphereData data2=mirrorMap.get(data);
			if(data2==null){
				//create here
				data2=data.clone();
				
				updateHorizontalMirror(data2);
				//TODO change bone name & make method
				mirrorMap.put(data, data2);
			}else{
				//copy
				data.copyTo(data2);
				updateHorizontalMirror(data2);
			}
			
			if(sphereMeshMap.get(data2)==null){
				initSphereCalculatorAndMesh(data2,0x880000).getCalculator();;
			}else{//update
				sphereMeshMap.get(data2).getMesh().setUserData(sphereDataToJsData(data2));
			}
			
			SkinningVertexCalculator calculator2=sphereMeshMap.get(data2).getCalculator();
			
			for(SkinningVertex vertex:calculator2.getSkinningVertexs()){
				vertex.getSkinIndices().setX(data2.getBoneIndex());
			}
			
			//plus sync channel
			this.getClothControler().updateSphere(sphereMeshMap.get(data2).getMesh(), data2.getChannel());
			
		}else{
			//no mirror anymore
			SphereData data2=mirrorMap.get(data);
			if(data2!=null){
				removeSphereMesh(data2);
				mirrorMap.remove(data);
				sphereMeshMap.remove(data2);
			}
		}
		
	}
	
	//TODO make method
	protected String getMirroredBoneName(String name) {
		if(name.endsWith("_R")){
			return name.replace("_R", "_L");
		}
		if(name.endsWith("_L")){
			return name.replace("_L", "_R");
		}
		
		if(name.indexOf("Right")!=-1){
			return name.replace("Right", "Left");
		}
		if(name.indexOf("right")!=-1){
			return name.replace("right", "left");
		}
		if(name.indexOf("Left")!=-1){
			return name.replace("Left", "Right");
		}
		if(name.indexOf("left")!=-1){
			return name.replace("left", "right");
		}
		//makehuman 19 bones
		/*
		if(name.startsWith("r")){
			return "l"+name.substring(1);
		}
		else if(name.startsWith("l")){
			return "r"+name.substring(1);
		}
		*/
		
		return null;
	}
	
	/*
	 * replace bone name and position
	 */
	public void updateHorizontalMirror(SphereData data){
		data.setX(data.getX()*-1);
		//boneName
		String boneName=characterMesh.getSkeleton().getBones().get(data.getBoneIndex()).getName();
		String mirrowName=getMirroredBoneName(boneName);
		if(mirrowName!=null){
			int index=-1;
			for(int i=0;i<characterMesh.getSkeleton().getBones().length();i++){
				String name=characterMesh.getSkeleton().getBones().get(i).getName();
				if(name.equals(mirrowName)){
					index=i;
					break;
				}
			}
			if(index!=-1){
				data.setBoneIndex(index);
			}
			//LogUtils.log(mirrowName+","+index);
		}
		
		//swap-x-z
		Euler euler=THREE.Euler().setFromQuaternion(data.getRotation());
		euler.setZ(-euler.getZ());
		euler.setY(-euler.getY());
		data.getRotation().setFromEuler(euler);
		//TODO support rotation
	}

	//direct load from text
	public void loadSphereDatas(String text){
		Iterable<SphereData> newDatas=new SphereDataCsvConverter().reverse().convertAll(CSVUtils.splitLinesWithGuava(text));
		 for(SphereData newData:newDatas){
			 addSphereData(newData);
		 }
	}
	
	public void addSphereData(SphereData data){
		MeshPhongMaterial ballMaterial = THREE.MeshPhongMaterial( GWTParamUtils.MeshPhongMaterial().color(0x888888).side(THREE.DoubleSide).wireframe(true));
		
		Mesh sphere=createFromSphereData(data,ballMaterial);
		
		
		scene.add( sphere );
		
		
		
		this.getClothControler().addSphere(sphere,data.getChannel());
		
		
		sphereMeshMap.put(data, new SphereCalculatorAndMesh(characterMesh, data.getBoneIndex(), sphere));
		
		if(data.isCopyHorizontal()){
			//TODO fix rotation
			SphereData data2=data.clone();
			updateHorizontalMirror(data2);
			mirrorMap.put(data, data2);
			initSphereCalculatorAndMesh(data2,0x880000);
		}
		
	}
	
	private Map<SphereData,SphereData> mirrorMap=Maps.newHashMap();
	
	
	private JsSphereData sphereDataToJsData(SphereData data){
		JsSphereData jsData=JsSphereData.create();
		jsData.setType(data.getType());
		jsData.setRotate(data.getRotation().clone());
		jsData.setRadius(data.getWidth()/2);//deprecated
		jsData.setWidth(data.getWidth());
		jsData.setHeight(data.getHeight());
		jsData.setDepth(data.getDepth());
		jsData.setBoneIndex(data.getBoneIndex());
		return jsData;
	}
	
	private Mesh createFromSphereData(SphereData data,Material material){
		Mesh collisionMesh=null;
		if(data.getType()==SphereData.TYPE_SPHERE){
			collisionMesh = THREE.Mesh( sphereGeometry, material );//		sphere = new THREE.Mesh( ballGeo, ballMaterial );
			collisionMesh.getScale().setScalar(data.getWidth()/2);
			}else if(data.getType()==SphereData.TYPE_CAPSULE){
				Geometry geometry=BodyAndMesh.createCapsuleGeometry(data.getWidth()/2, data.getHeight());
				collisionMesh = THREE.Mesh( geometry, material );//		sphere = new THREE.Mesh( ballGeo, ballMaterial );
				collisionMesh.getScale().setScalar(1);
				}else {
			//rotate here must be good?
			Geometry geometry=boxGeometry;
			//geometry.applyMatrix(THREE.Matrix4().makeRotationFromQuaternion(data.getRotate()));
			collisionMesh = THREE.Mesh( geometry, material );
			collisionMesh.getScale().set(data.getWidth()/2, data.getHeight()/2, data.getDepth()/2);
			}
		
		collisionMesh.setUserData(sphereDataToJsData(data));
		return collisionMesh;
	}
	
	/**
	 * called when mirror as isCopyHorizontal()
	 * @param data
	 * @param color
	 * @return
	 */
	private SphereCalculatorAndMesh initSphereCalculatorAndMesh(SphereData data,int color){
		MeshPhongMaterial ballMaterial = THREE.MeshPhongMaterial( GWTParamUtils.MeshPhongMaterial().color(color).side(THREE.DoubleSide).wireframe(true));
		//TODO support box here
		Mesh sphere = createFromSphereData(data,ballMaterial);
		scene.add( sphere );
		
		
		
		this.getClothControler().addSphere(sphere,data.getChannel());
		
		SphereCalculatorAndMesh calculatorAndMesh=new SphereCalculatorAndMesh(characterMesh, data.getBoneIndex(), sphere);
		sphereMeshMap.put(data, calculatorAndMesh);
		return calculatorAndMesh;
	}
	
	public void updateSphereMeshs(){
		//LogUtils.log("sphereMeshMap size="+sphereMeshMap.keySet().size());
		for(SphereData data:sphereMeshMap.keySet()){
			SphereCalculatorAndMesh sphereCalculatorAndMesh=sphereMeshMap.get(data);
			sphereCalculatorAndMesh.getCalculator().getSkinningVertexs().get(0).getVertex().copy(data.getPosition());
			//i guess no need size scale
			
			//sphereCalculatorAndMesh.getCalculator().getSkinningVertexs().get(1).getVertex().copy(data.getPosition()).gwtIncrementX(data.getWidth()/2);
			sphereCalculatorAndMesh.getCalculator().update();
			
			//double size=sphereCalculatorAndMesh.getCalculator().getResult().get(0).distanceTo(sphereCalculatorAndMesh.getCalculator().getResult().get(1));
			//update sphere
			
			double characterScale=getCharacterMesh().getScale().getX();
			if(data.getType()==SphereData.TYPE_SPHERE){
			//sphereCalculatorAndMesh.getMesh().getScale().setScalar(data.getX()/2*characterScale);
				sphereCalculatorAndMesh.getMesh().getScale().setScalar(data.getWidth()/2*characterScale);
			}else if(data.getType()==SphereData.TYPE_BOX){
				//LogUtils.log(size+","+(data.getWidth()*characterScale));
				sphereCalculatorAndMesh.getMesh().getScale().set(data.getWidth()/2*characterScale,data.getHeight()/2*characterScale,data.getDepth()/2*characterScale);
			}else if(data.getType()==SphereData.TYPE_CAPSULE){
				
				//how to upgrade geometry
				
				sphereCalculatorAndMesh.getMesh().getScale().setScalar(characterScale);
				}
			sphereCalculatorAndMesh.getMesh().getPosition().copy(sphereCalculatorAndMesh.getCalculator().getResult().get(0));
			
			JsSphereData jsData=sphereCalculatorAndMesh.getMesh().getUserData().cast();
			if(jsData==null){
				LogUtils.log("updateSphereMeshs:jsData is null");
			}
			
			
			//box shape need rotation.
			if(jsData.getType()!=SphereData.TYPE_SPHERE){
			SkinnedMesh skinnedMesh=sphereCalculatorAndMesh.getCalculator().getSkinnedMesh();
			
		//	Matrix4 matrixWorldInv = THREE.Matrix4().getInverse( skinnedMesh.getMatrixWorld() );
		//	Matrix4 boneMatrix =  THREE.Matrix4();
			
			Bone bone=skinnedMesh.getSkeleton().getBones().get(jsData.getBoneIndex());
			
		//	boneMatrix.multiplyMatrices( matrixWorldInv, bone.getMatrixWorld());
			
			/*
			 * i trid this.some how not work correctly
			 */
			Quaternion q=bone.getWorldQuaternion(null);
			
			//Quaternion cq=THREE.Quaternion().setFromEuler(skinnedMesh.getRotation() );
			
			//Quaternion q=THREE.Quaternion().setFromRotationMatrix(boneMatrix);
			//jsData.getRotate().multiplyQuaternions(jsData.getRotate(), cq);
			q.multiplyQuaternions(q,jsData.getRotate());
			
			//q.multiplyQuaternions(q, cq);
			//q.multiplyQuaternions(q, THREE.Quaternion().setFromRotationMatrix(skinnedMesh.getMatrixWorld() ));
			
			
			/*
			 * without animation works good,however faild on animation
			 */
			/*
			Euler euler=THREE.Euler().setFromQuaternion(jsData.getRotate());
			Euler euler2=THREE.Euler().setFromQuaternion(characterMesh.getQuaternion());
			//ThreeLog.log("jsData:",euler);
			
			Euler euler3=THREE.Euler(euler.getX()+euler2.getX(), euler.getY()+euler2.getY(), euler.getZ()+euler2.getZ());
			q.multiplyQuaternions(q,THREE.Quaternion().setFromEuler(euler3));
			*/
			sphereCalculatorAndMesh.getMesh().setRotationFromQuaternion(q);
			//sphereCalculatorAndMesh.getMesh().setRotationFromQuaternion(q.normalize());//why need normalize?
			}
		}
	}
	


	public HairMixedData addCloth(HairData hairData) {
		if(isUpdatingHairTextureMap()){
			LogUtils.log("addCloth:Warning - maybe broken texture added.still texture making");
		}
		
		ClothData data=new ClothData(hairData,characterMesh,this);
		
		
		data.getHairCloth().setPinAll();//force pin all
		
		
		//data.getCloth().ballSize=clothControls.getBallSize();
		
	
		//indivisual haiar material
		
		//TODO extract
		/*
		 * 
		 */
		Texture specularMapTexture=THREE.TextureLoader().load(
				//"models/mbl3d/bump2c.png"
				"/hairpattern/hairpattern1.png"
				);
		
		
		specularMapTexture.setFlipY(false);
		specularMapTexture.setNeedsUpdate(true);
		
		//Texture disp=THREE.TextureLoader().load("models/mbl3d/hairdisp.png");
		//disp.setFlipY(false);
		
		//displacementMap not good at plain when row-poly
		
		//TODO make property;
		boolean isNoNeedPlainCloth=hairData.getHairPhysicsType()==HairData.TYPE_AMMO_BONE_CLOTH 
				|| hairData.getHairPhysicsType()==HairData.TYPE_AMMO_BONE_HAIR
				|| hairData.getHairPhysicsType()==HairData.TYPE_AMMO_BONE_BODY
						;
		
		//TODO hairdisp & bump control
		//little bit 
		MeshPhongMaterial hairMaterial = THREE.MeshPhongMaterial(GWTParamUtils.
				MeshPhongMaterial()
				.side(THREE.DoubleSide)
				.transparent(true)
				//.wireframe(true)
				.specular(0x111111)
				//.specular(0xffffff)
				.shininess(5) //switch default same as texture otherwise not good at connection
				//.wireframe(true)
				.specular(0x888888)//TODO move editor
				
				//.specularMap(texture)
				
				//.shininess(15)
				.visible(!isNoNeedPlainCloth)
				
				//hairdisp.png
				//.displacementMap(disp)
				//.displacementScale(16)
				//.displacementBias(4)
				
				
				//.bumpMap(texture)
				//.bumpScale(4)
				
				//.di
				);
		/*
		GWTParamUtils.
				MeshPhongMaterial()
				.color(hairData.getHairTextureData().getColor()).side(THREE.DoubleSide).specular(0xffffff).shininess(15)
				.alphaTest(hairData.getHairTextureData().getAlphaTest())
				.transparent(true)
				.opacity(hairData.getHairTextureData().getOpacity())
				);
			*/
		
		Mesh clothMesh = THREE.Mesh( data.getClothGeometry(), hairMaterial );
		clothMesh.setCastShadow(true);
		clothMesh.setReceiveShadow(receiveShadow);
		//object.getPosition().set( 0, 0, 0 );
		
		data.setClothMesh(clothMesh);
		
		scene.add( clothMesh );
		
		HairMixedData cellData=new HairMixedData(hairData,data,clothMesh);
		
		boolean startCenter=data.getHairCloth().isStartCircleCenter();//start circle from center or not
		boolean startAndEndSame=data.getHairCloth().isStartAndEndSameCircle();
		
		boolean useFirstPointY=data.getHairCloth().isUseFirstPointY();
		//TODO make function,WARNING do same things in ClothControlers:syncPinPositions()
		
		if(hairData.getPointMode()==HairData.POINT_MODE_SEMI_AUTO){
			checkNotNull(hairData.getSemiAutoPoints(),"semi auto points is null");
			checkNotNull(hairData.getSemiAutoPins(),"semi auto pins is null");
			checkArgument(hairData.getSemiAutoPoints().length() == data.getHairCloth().getParticles().size(),"semi auto points is not same as particle size.semiauto="+(hairData.getSemiAutoPoints().length()));
	
			for(int i=0;i<data.getHairCloth().getParticles().size();i++){
				data.getHairCloth().getParticles().get(i).setAllPosition(hairData.getSemiAutoPoints().get(i));
				//no need mass setting?,maybe mass is set when ammo-particle-made
			}
			int[] pins=new int[hairData.getSemiAutoPins().length()];
			for(int i=0;i<hairData.getSemiAutoPins().length();i++){
				pins[i]=(int)hairData.getSemiAutoPins().get(i);
				//LogUtils.log("addcloth-semi-auto-pin:"+pins[i]);
				//ThreeLog.log("",data.getHairCloth().getParticles().get(i).getPosition());
			}
			data.getHairCloth().setPins(pins);
			
		}else if(hairData.getHairPins().size()==2 && hairData.isCircleStyle()){
			Vector3 v1=hairPinToVertex(characterMesh,hairData.getHairPins().get(0),true);
			Vector3 v2=hairPinToVertex(characterMesh,hairData.getHairPins().get(1),true);
			//TODO move and fix
			HairPointUtils.updateCircleStyle(data.getHairCloth(), v1, v2,false);
			/*
			int cw=hairData.getSizeOfU();
			int ch=hairData.getSizeOfV();
			int angleSplit=startAndEndSame?cw:cw+1;
			Vector2 center=THREE.Vector2(v1.getX(), v1.getZ());
			Vector2 point=THREE.Vector2(v2.getX(), v2.getZ());
			List<Vector3> corePositions=Lists.newArrayList();
			double perAngle=360.0/(angleSplit); //not support connect-horizontal
			ThreeLog.log("center",center);
			ThreeLog.log("point",point);
			for(int i=0;i<=cw;i++){
				Vector2 rotated=point.clone().rotateAround(center, Math.toRadians(perAngle*i));
				//ThreeLog.log("angle:"+(perAngle*i),rotated);
				
				//TODO switch v1 or v2
				if(useFirstPointY){
					corePositions.add(THREE.Vector3(rotated.getX(), v1.getY(), rotated.getY()));
				}else{
					corePositions.add(THREE.Vector3(rotated.getX(), v2.getY(), rotated.getY()));
				}
				
			}
			
			for(int i=0;i<=cw;i++){
				if(startCenter){
					data.getCloth().particles.get(i).setAllPosition(v1);
					}
				else{
					data.getCloth().particles.get(i).setAllPosition(corePositions.get(i));
				}
				
				
				
			}
			
			//for(int i=cw+1;i<data.getCloth().particles.size();i++){
				//data.getCloth().particles.get(i).setAllPosition(v1);
				//linked
				for(int j=data.getCloth().getW()+1;j<data.getCloth().particles.size();j++){
					int x=j%(data.getCloth().getW()+1);
					int y=j/(data.getCloth().getW()+1);
					
					Vector3 delta=corePositions.get(x).clone().sub(v1).setY(0);
					Vector3 newPosition=delta.multiplyScalar(y);
					
					
					double angle=x*perAngle;
					if(data.getCloth().isAmmoInCircleInRange(angle)){
						newPosition.multiplyScalar(data.getCloth().getAmmoCircleInRangeRatio());
					}
					
					
					if(startCenter){
						newPosition.add(v1);
						//no need
					}else{
						newPosition.add(corePositions.get(x));
					}
					
					//ThreeLog.log("j="+j+",x="+x+",y="+y,newPosition);
					
					data.getCloth().particles.get(j).setAllPosition(newPosition);
				}
				*/
			//}
			
		}
		/*
		else 
		if(hairData.getHairPins().size()<3){//old less pin,now not reached
		Vector3 v1=hairPinToVertex(characterMesh,hairData.getHairPins().get(0),true);
		Vector3 v2=hairPinToVertex(characterMesh,hairData.getHairPins().get(1),true);
		
		//TODO move and fix
		int cw=hairData.getSliceFaceCount();
		int ch=hairData.getSizeOfV();
		
		data.getHairCloth().particles.get(0).setAllPosition(v1);
		data.getHairCloth().particles.get(cw).setAllPosition(v2);
		
		
		Vector3 sub=v2.clone().sub(v1).divideScalar(cw+1);
		for(int i=1;i<cw;i++){
			Vector3 v=sub.clone().multiplyScalar(i).add(v1);
			data.getHairCloth().particles.get(i).setAllPosition(v);
		}
		}
		*/
		
		else{
			
			//only core pins,only use no-custom pin
			//THIS IS IMMUTABLE
			List<Vector3> noTargetedPinNormals=FluentIterable.from(hairData.getHairPins()).filter(HairPinPredicates.NoTargetOnly()).transform(new HairPinToNormal(characterMesh,true)).toList();
			//List<Vector3> pinNormals=FluentIterable.from(hairData.getHairPins()).transform(new HairPinToNormal(characterMesh)).toList();
			
			//merging custom normal
			//TODO add vector3,now only -1y
			
			if(hairData.isUseCustomNormal()){
			Vector3 customNormal=hairData.getCustomNormal();
			for(int i=0;i<noTargetedPinNormals.size();i++){
				//Vector3 tmp=noTargetedPinNormals.get(i).clone().normalize();
				//Vector3 tmp2=THREE.Vector3(0, -1, 0).add(tmp.multiplyScalar(hairData.getOriginalNormalRatio())).normalize();
				//ThreeLog.log("normal",tmp2);
				noTargetedPinNormals.get(i).copy(customNormal.clone().lerp(noTargetedPinNormals.get(i).normalize(), hairData.getOriginalNormalRatio()));
			}
			}
			
			// averaing normal add both side and normalize,take care of connect-horizontal
			//TODO make function
			if(hairData.isExecAverageNormal()){
			List<Vector3> averageNormals=Lists.newArrayList();
			for(int i=0;i<noTargetedPinNormals.size();i++){
				Vector3 normal=THREE.Vector3();
				if(i-1<0){
					if(data.getHairCloth().isConnectHorizontal()){
						normal.add(noTargetedPinNormals.get(noTargetedPinNormals.size()-1));
					}
				}else{
					normal.add(noTargetedPinNormals.get(i-1));
				}
				
				normal.add(noTargetedPinNormals.get(i));
				
				if(i+1==noTargetedPinNormals.size()){
					if(data.getHairCloth().isConnectHorizontal()){
						normal.add(noTargetedPinNormals.get(0));
					}
				}else{
					normal.add(noTargetedPinNormals.get(i+1));
				}
				normal.normalize();
				averageNormals.add(normal);
			}
			
			noTargetedPinNormals=averageNormals;
			}
			
			List<Vector3> normals=Lists.newArrayList();
			//PROBLEMS not support custom 
			
			
			
			
			
			
			
			
			
			
			//separate custom and notarget
			int sliceFaceCount=hairData.getSliceFaceCount();
			
			List<HairPin> noTargetPins=Lists.newArrayList();
			List<HairPin> customPin=Lists.newArrayList();
			
			
			
			List<Vector3> generalPinVertex=Lists.newArrayList();
			List<Vector3> customlPinVertex=Lists.newArrayList();
			List<Integer> customPinTarget=Lists.newArrayList();
			
			//LogUtils.log("pinSize:"+data.getCalculator().getSkinningVertexs().size());
			
			for(HairPin pin:hairData.getHairPins()){
				if(pin.getTargetClothIndex()==-1){
					noTargetPins.add(pin);
					
					Vector3 v1=hairPinToVertex(characterMesh,pin,true);
					generalPinVertex.add(v1);
				}else{
					customPin.add(pin);
					
					Vector3 v1=hairPinToVertex(characterMesh,pin,true);
					customlPinVertex.add(v1);
					customPinTarget.add(pin.getTargetClothIndex());
				}
			}
			
			
			
			HairPointUtils.updateFirstRowStyle(data.getHairCloth(), generalPinVertex, customlPinVertex, customPinTarget);
			
			
			int normalSize=0;//for debug
			
			for(int i=0;i<noTargetPins.size();i++){
				//Vector3 v1=hairPinToVertex(characterMesh,noTargetPins.get(i),true);
				
				//executeSphereOut(v1,spheres);//for test
				
				int index=hairData.getSliceFaceCount()*i;
				//data.getHairCloth().particles.get(index).setAllPosition(v1);
				
				//LogUtils.log("main:"+index);
				
				normalSize++;
				normals.add(noTargetedPinNormals.get(i));
				
				//interporating
				if(i!=noTargetPins.size()-1){//not last, has next
					//has next;
				//	Vector3 v2=hairPinToVertex(characterMesh,noTargetPins.get(i+1),true);
					//Vector3 sub=v2.clone().sub(v1).divideScalar(sliceFaceCount);
					
					for(int j=1;j<sliceFaceCount;j++){
						
					//	int at=index+j;
						
						double percent=(double)j/sliceFaceCount;
						//int multiple=j;
						//Vector3 v=sub.clone().multiplyScalar(multiple).add(v1);
						
					//	data.getHairCloth().particles.get(at).setAllPosition(v1.clone().lerp(v2, percent));
						
						normals.add(noTargetedPinNormals.get(i).clone().lerp(noTargetedPinNormals.get(i+1),percent));
						//LogUtils.log("sub:"+at);
						normalSize++;
					}
					
					
					//simply do it
					//normals.add(pinNormals.get(i).clone().add(pinNormals.get(i+2).divideScalar(2)));
				}	
			}
			
			//LogUtils.log("normal-test"+normalSize+","+pinNormals.size()+","+(data.getCloth().getW()+1));
			noTargetedPinNormals=normals;
			//LogUtils.log("normal-pos:"+normals.size()+",pin "+pinNormals.size()+"append:"+normalSize);
			
			
			//init other posisions
			for(int j=data.getHairCloth().getW()+1;j<data.getHairCloth().particles.size();j++){
				
				if(customPinTarget.contains(j)){
					continue;//overwrited custom pin
				}
				
				int x=j%(data.getHairCloth().getW()+1);
				int y=j/(data.getHairCloth().getW()+1);
				//LogUtils.log(j+"="+x);
				Vector3 pos=data.getHairCloth().particles.get(x).getOriginal().clone();
				//copy upper x
				
				//without normal initial position is same as parent
				data.getHairCloth().particles.get(j).setAllPosition(pos);
				//ThreeLog.log("origin:"+j,pos);
				
				//try to narrow but faild
				//pos.add(noTargetedPinNormals.get(x).clone().normalize().multiplyScalar( -data.getCloth().getRestDistance()*0.5 ));
				
				//LogUtils.log("distance:"+(data.getCloth().getRestDistance()*10*y));
				//plus-y
				//Vector3  start=normals.get(x).clone().normalize().multiplyScalar( data.getCloth().getRestDistance()*y ).add(pos);
				//data.getCloth().particles.get(j).setAllPosition(start);
			
				if(noTargetedPinNormals.size()==data.getHairCloth().getW()+1){
				//	LogUtils.log("can use normal");
					Vector3  normalAddPosition=noTargetedPinNormals.get(x).clone().normalize().multiplyScalar( data.getHairCloth().getRestDistance()*y ).add(pos);
					data.getHairCloth().particles.get(j).setAllPosition(normalAddPosition);
					//ThreeLog.log(""+j,normalAddPosition);
				}
				
			}
			
			//overwrite pin
			/*for(HairPin pin:customPin){
				Vector3 v=hairPinToVertex(characterMesh,pin,true);
				data.getHairCloth().particles.get(pin.getTargetClothIndex()).setAllPosition(v);
			}*/
			
			
			/*
			for(int i=0;i<cw*(hairData.getHairPins().size()-1);i++){
				ThreeLog.log(""+i, data.getCloth().particles.get(i).getOriginal());
			}
			*/
			
			//initialize pin
			int w=data.getHairCloth().getW()+1;
			int[] newPins=new int[w+customPin.size()];
			for(int i=0;i<w;i++){
				newPins[i]=i;
			}
			for(int i=0;i<customPin.size();i++){
				newPins[w+i]=customPin.get(i).getTargetClothIndex();
			}
			data.getHairCloth().setPins(newPins);
			
			
			
		
			/*
			if(customPin.size()>0){
			int[] newPins=new int[customPin.size()];
			for(int i=0;i<customPin.size();i++){
				newPins[i]=customPin.get(i).getTargetClothIndex();
			}
			data.getCloth().setPins(newPins);
			
			LogUtils.log("pins");
			for(int i=0;i<newPins.length;i++){
				LogUtils.log(newPins[i]);
			}
			
			
			}
			*/
			
		}
		
		//trying sphere-out,but not good at than expected
		
		
		/*
		Vector3 diff = THREE.Vector3();
		List<Mesh> spheres=GWTThreeClothHair.INSTANCE.getClothControler().getSphereList(hairData.getChannel());
		for (int i=0;i<data.getCloth().particles.size();i++) {
			if(data.getCloth().isPinned(i)){
				continue;
			}
			
			int vIndex=data.getCloth().getVerticaPosition(i);
			Particle particle = data.getCloth().particles.get(i);
			Vector3 pos = particle.getPosition();
			
			for(Mesh mesh:spheres){
				
				
				diff.subVectors(pos, mesh.getPosition());
				if (diff.length() < mesh.getScale().getX()) {
					LogUtils.log("sphere-out");
					// collided
					diff.normalize().multiplyScalar(mesh.getScale().getX()*3);
					particle.setAllPosition(pos.copy(mesh.getPosition()).add(diff));
					//pos.copy(mesh.getPosition()).add(diff);
					//LogUtils.log("scale-out");
					//break;//?
				}
				
			}
			
			
		}
		*/
		
		//works fine?I'm not sure,howwver anyway narrow is not work fine so far
		//data.getCloth().recalcurateHorizontalConstraintsDistance();
		
		//LogUtils.log("constraints-distance");
		for(int i=0;i<data.getHairCloth().getConstrains().size();i++){
			if(!data.getHairCloth().isHorizontalConstraints(data.getHairCloth().getConstrains().get(i))){
			//	LogUtils.log(i+","+data.getCloth().getConstrains().get(i).getDistance());
			}
		}
		
		
		//add data do everything fixed
		clothControler.addClothData(data);
		
		
		updateHairTextureData(cellData,true);
		
		return cellData;
	}
	public void updateHairTextureData(HairMixedData selection,boolean updateHairTextureMap){
		if(selection==null){
			LogUtils.log("updateHairTextureData:null selection");
			return;
		}
		
		updateHairTextureData(selection.getMesh(),selection.getClothData().getHairCloth(),selection.getEditingHairData().getHairTextureData(),updateHairTextureMap);
		
		HairTextureData textureData=selection.getEditingHairData().getHairTextureData();
		
		MeshPhongMaterial material=selection.getMesh().getMaterial().gwtCastMeshPhongMaterial();
		
		if(textureData.isEnablePatternImage()){
			if(updateHairTextureMap){
			updateHairTextureMap(selection);
			}
		}else{
			material.setMap(null);
		}
		material.setNeedsUpdate(true);
		
	}
	/**
	 * direct call not support isEnablePatternImage so far
	 * @param mesh
	 * @param hairCloth
	 * @param updateHairTextureMap
	 */
	public void updateHairTextureData(@Nullable Mesh mesh,HairCloth hairCloth,HairTextureData hairTextureData,boolean updateHairTextureMap){
	
	
	
	//sync textures
	MeshPhongMaterial material=null;
	if(mesh!=null){
		material=mesh.getMaterial().gwtCastMeshPhongMaterial();
	}else{
		//usually next ammodata replace material
	}
	
	//replace if ammo data exist
	if(getAmmoHairControler().getAmmoData(hairCloth)!=null && hairCloth.getHairData().getHairPhysicsType()!=HairData.TYPE_AMMO_CLOTH){
		
		ParticleBodyDatas datas=getAmmoHairControler().getAmmoData(hairCloth);
		//LogUtils.log("debug:has:ammo-data:hash="+datas.hashCode());
		if(datas.getSkinnedMesh()!=null){//AMMO_BONE mode
			material=datas.getSkinnedMesh().getMaterial().gwtCastMeshPhongMaterial();
			LogUtils.log("debug:material replaced to ammo-particle");
		}else{
			LogUtils.log("ParticleBodyDatas has no skinnedMesh:"+hairCloth.getHairData().getHairPhysicsTypeName()+". BoneBody has no skinned mesh.");
		}
	}else{
		LogUtils.log("debug:updateHairTextureData-normal-mode");
	}
	
	
	//HairTextureData hairTextureData=hairCloth.getHairData().getHairTextureData();
	//TODO support local or global
	
	int color=hairTextureData.isUseLocalColor()?hairTextureData.getColor():globalHairColor;
	
	//LogUtils.log("updateHairTextureData:"+color);
	if(material!=null){
	
	material.setColor(THREE.Color(color));
	
	
	material.setOpacity(hairTextureData.getOpacity());
	material.setAlphaTest(hairTextureData.getAlphaTest());
	
	//TODO copy from patterns if enabled
	material.setNeedsUpdate(true);
	
	}else{
	LogUtils.log("updateHairTextureData:material is null");	
	}
}
	public static int defaultHairTextureColor=0xb7a9cd;//TODO move outside
	private    int globalHairColor=defaultHairTextureColor;

	public int getGlobalHairColor() {
		return globalHairColor;
	}

	public void setGlobalHairColor(int globalHairColor) {
		this.globalHairColor = globalHairColor;
	}

	public Vector3 hairPinToVertex(Mesh mesh,HairPin hairPin,boolean applyMatrix4){
		checkNotNull(mesh,"hairPinToVertex:mesh is null");
		checkNotNull(hairPin,"hairPinToVertex:hairPin is null");
		checkArgument(mesh.getGeometry().getFaces().length()>hairPin.getFaceIndex(),"hairPinToVertex:invalid face length");
		Face3 face=mesh.getGeometry().getFaces().get(hairPin.getFaceIndex());
		Vector3 vertex;
		if(hairPin.getVertexOfFaceIndex()==0){
			vertex=mesh.getGeometry().getVertices().get(face.getA());
		}else if(hairPin.getVertexOfFaceIndex()==1){
			vertex=mesh.getGeometry().getVertices().get(face.getB());
		}else{
			vertex=mesh.getGeometry().getVertices().get(face.getC());
		}
		
		//TODO support direct point
		
		if(applyMatrix4){
		return vertex.clone().applyMatrix4( mesh.getMatrixWorld());
		}else{
		return vertex.clone();
		}
		
	}
	//used for making hair texture 
	private Canvas canvas;
	public Canvas getCanvas() {
		return canvas;
	}

	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}

	private boolean updatingHairTextureMap;
	public boolean isUpdatingHairTextureMap() {
		return updatingHairTextureMap;
	}
	
	public void updateHairTextureMap(final HairMixedData selection){
		updatingHairTextureMap=true;
		if(selection==null){
			LogUtils.log("updateHairTextureData:null selection");
			return;
		}
		//for debug
		//canvas=HairPatternDataEditor.canvas;
		
		//selection.getHairData().getHairTextureData().getHairPatternData().setSlices(4);//for debug
		HairPatternDataUtils.paint(canvas, selection.getEditingHairData().getHairTextureData().getHairPatternData());
		
		//String pattern="hairpattern/hairpattern1.png";
		String pattern="img/transparent.png";//do nothing
		/*
		 * 
		 * this async action make problem,only first pattern correctly update
		 * 
		 */
		new ImageElementLoader().load(pattern, new ImageElementListener() {
			
			@Override
			public void onLoad(ImageElement element) {
				//TODO test alpha
				canvas.getContext2d().setGlobalCompositeOperation(Composite.SOURCE_ATOP);
				
				canvas.getContext2d().drawImage(element, 0, 0,canvas.getCoordinateSpaceWidth(),canvas.getCoordinateSpaceHeight());
				
				
				canvas.getContext2d().setGlobalCompositeOperation(Composite.SOURCE_OVER);
				ImageElementUtils.createWithLoader(canvas.toDataUrl(), new ImageElementListener() {
					
					@Override
					public void onLoad(ImageElement element) {
						Texture texture=THREE.Texture(element);
						texture.setNeedsUpdate(true);
						texture.setFlipY(false);
						
						MeshPhongMaterial material=selection.getMesh().getMaterial().gwtCastMeshPhongMaterial();
						material.setMap(texture);
						material.setNeedsUpdate(true);//async load & need here
						updatingHairTextureMap=false;
					}
					
					@Override
					public void onError(String url, ErrorEvent event) {
						LogUtils.log("image not found.update texture skipped:"+url);
						updatingHairTextureMap=false;
					}
				});
			}
			
			@Override
			public void onError(String url, ErrorEvent event) {
				LogUtils.log("Error-on loading:"+url);
				LogUtils.log(event);
				updatingHairTextureMap=false;
			}
		});
		
		
		
		
		
	}
	private boolean receiveShadow;//for new addition
	public boolean isReceiveShadow() {
		return receiveShadow;
	}

	/*
	 * right now not working,i'don't know how to change this later.
	 */
	public void setReceiveShadow(boolean value) {
		receiveShadow=value;
		//replace exists
		for(ClothData data:clothControler.getCloths()){
			data.getClothMesh().setReceiveShadow(value);
		}
	}
	
	/*
	 * basically for hair-mode;
	 */
	public void syncAmmoBodyToParticle(HairCloth hairCloth){
		AmmoHairControler.ParticleBodyDatas data=this.getAmmoHairControler().getAmmoData(hairCloth);
		List<BodyAndMesh> ammoParticles=data.getAmmoParticles();
		Vector3 threePos=THREE.Vector3();//share and improve fps
		//basically never changed length
		for(int i=0;i<ammoParticles.size();i++){
			if(hairCloth.isPinned(i)){//both plain & hair type need sync
				continue;
			}else{
				Vector3 ammoPos=ammoParticles.get(i).getBody().getReadOnlyPosition(threePos);
				hairCloth.getParticles().get(i).position.copy(ammoPos).divideScalar(hairCloth.getAmmoMultipleScalar());
			}
		}
	}

}
