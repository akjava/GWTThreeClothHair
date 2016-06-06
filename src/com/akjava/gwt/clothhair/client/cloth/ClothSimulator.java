package com.akjava.gwt.clothhair.client.cloth;

import java.util.Map;

import com.akjava.gwt.clothhair.client.GWTThreeClothHair.SphereCalculatorAndMesh;
import com.akjava.gwt.clothhair.client.SkinningVertexCalculator;
import com.akjava.gwt.clothhair.client.SkinningVertexCalculator.SkinningVertex;
import com.akjava.gwt.clothhair.client.cannon.CannonControler;
import com.akjava.gwt.clothhair.client.sphere.SphereData;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.extras.geometries.SphereGeometry;
import com.akjava.gwt.three.client.js.materials.MeshPhongMaterial;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;
import com.akjava.gwt.three.client.js.scenes.Scene;
import com.google.common.collect.Maps;

public class ClothSimulator  {
	private CannonControler cannonControler;
	private Scene scene;
	private SkinnedMesh characterMesh;
	private SphereGeometry ballGeo;
	public ClothSimulator(Scene scene,SkinnedMesh characterMesh){
		this.scene=scene;
		this.characterMesh=characterMesh;
		clothControler=new ClothControler();
		cannonControler=new CannonControler();
		
		ballGeo = THREE.SphereGeometry( 1, 20, 20 );
	}
	
	public CannonControler getCannonControler() {
		return cannonControler;
	}



	public void setCannonControler(CannonControler cannonControler) {
		this.cannonControler = cannonControler;
	}

	private ClothControler clothControler;
	
	

	public ClothControler getClothControler() {
		return clothControler;
	}



	public void setClothControler(ClothControler clothControler) {
		this.clothControler = clothControler;
	}



	public void update(double timestamp){
		updateSphereMeshs();//sphere first
		getClothControler().beforeSimulate();
		
		
		if(cannonControler.isEnabled()){//TODO move setting
			int iteration=1; //iteration totally kill fps,but reduce shaking
			for(int i=0;i<iteration;i++)
			cannonControler.getWorld().step(1.0/60);//sphere first
		}
		
		
		//do simulate
		getClothControler().afterSimulate(timestamp);//back to physics
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
			mirrorMap.remove(data);
		}
	}
	

	/*
	 * called when flushed
	 */
	public void syncSphereDataAndSkinningVertexCalculator(SphereData data){
		if(data==null){
			return;
		}
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
			}
			
			SkinningVertexCalculator calculator2=sphereMeshMap.get(data2).getCalculator();
			
			for(SkinningVertex vertex:calculator2.getSkinningVertexs()){
				vertex.getSkinIndices().setX(data2.getBoneIndex());
			}
			
			//plus sync channel
			this.getClothControler().updateSphere(sphereMeshMap.get(data2).getMesh(), data2.getChannel());
			
		}else{
			//no need
			SphereData data2=mirrorMap.get(data);
			if(data2!=null){
				removeSphereMesh(data2);
				mirrorMap.remove(data);
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
			LogUtils.log(mirrowName+","+index);
		}
	}

	public void addSphereData(SphereData data){
		MeshPhongMaterial ballMaterial = THREE.MeshPhongMaterial( GWTParamUtils.MeshPhongMaterial().color(0x888888).side(THREE.DoubleSide).wireframe(true));
		
		Mesh sphere = THREE.Mesh( ballGeo, ballMaterial );//		sphere = new THREE.Mesh( ballGeo, ballMaterial );
		scene.add( sphere );
		
		sphere.getScale().setScalar(data.getSize());
		
		this.getClothControler().addSphere(sphere,data.getChannel());
		
		
		
		sphereMeshMap.put(data, new SphereCalculatorAndMesh(characterMesh, data.getBoneIndex(), sphere));
		
		if(data.isCopyHorizontal()){
			SphereData data2=data.clone();
			updateHorizontalMirror(data2);
			mirrorMap.put(data, data2);
			initSphereCalculatorAndMesh(data2,0x880000);
		}
		
	}
	
	private Map<SphereData,SphereData> mirrorMap=Maps.newHashMap();
	
	private SphereCalculatorAndMesh initSphereCalculatorAndMesh(SphereData data,int color){
		MeshPhongMaterial ballMaterial = THREE.MeshPhongMaterial( GWTParamUtils.MeshPhongMaterial().color(color).side(THREE.DoubleSide).wireframe(true));
		
		Mesh sphere = THREE.Mesh( ballGeo, ballMaterial );//		sphere = new THREE.Mesh( ballGeo, ballMaterial );
		scene.add( sphere );
		
		sphere.getScale().setScalar(data.getSize());
		
		this.getClothControler().addSphere(sphere,data.getChannel());
		
		SphereCalculatorAndMesh calculatorAndMesh=new SphereCalculatorAndMesh(characterMesh, data.getBoneIndex(), sphere);
		sphereMeshMap.put(data, calculatorAndMesh);
		return calculatorAndMesh;
	}
	
	public void updateSphereMeshs(){
		for(SphereData data:sphereMeshMap.keySet()){
			SphereCalculatorAndMesh sphereCalculatorAndMesh=sphereMeshMap.get(data);
			sphereCalculatorAndMesh.getCalculator().getSkinningVertexs().get(0).getVertex().copy(data.getPosition());
			sphereCalculatorAndMesh.getCalculator().getSkinningVertexs().get(1).getVertex().copy(data.getPosition()).gwtIncrementX(data.getSize());
			sphereCalculatorAndMesh.getCalculator().update();
			
			double size=sphereCalculatorAndMesh.getCalculator().getResult().get(0).distanceTo(sphereCalculatorAndMesh.getCalculator().getResult().get(1));
			//update sphere
			sphereCalculatorAndMesh.getMesh().getScale().setScalar(size);
			sphereCalculatorAndMesh.getMesh().getPosition().copy(sphereCalculatorAndMesh.getCalculator().getResult().get(0));
		}
	}
	
	
	

}
