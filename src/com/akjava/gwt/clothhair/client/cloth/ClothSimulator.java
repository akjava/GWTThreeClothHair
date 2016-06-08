package com.akjava.gwt.clothhair.client.cloth;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import com.akjava.gwt.clothhair.client.GWTThreeClothHair.SphereCalculatorAndMesh;
import com.akjava.gwt.clothhair.client.GWTThreeClothHair;
import com.akjava.gwt.clothhair.client.SkinningVertexCalculator;
import com.akjava.gwt.clothhair.client.SkinningVertexCalculator.SkinningVertex;
import com.akjava.gwt.clothhair.client.cannon.CannonControler;
import com.akjava.gwt.clothhair.client.hair.HairData;
import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.akjava.gwt.clothhair.client.hair.HairDataPanel.HairMixedData;
import com.akjava.gwt.clothhair.client.hair.HairPinDataFunctions.HairPinToNormal;
import com.akjava.gwt.clothhair.client.sphere.SphereData;
import com.akjava.gwt.clothhair.client.sphere.SphereDataConverter;
import com.akjava.gwt.clothhair.client.texture.HairPatternDataEditor;
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
import com.akjava.gwt.three.client.js.extras.geometries.SphereGeometry;
import com.akjava.gwt.three.client.js.materials.MeshPhongMaterial;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;
import com.akjava.gwt.three.client.js.scenes.Scene;
import com.akjava.gwt.three.client.js.textures.Texture;
import com.akjava.lib.common.utils.CSVUtils;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d.Composite;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ErrorEvent;

public class ClothSimulator  {
	private CannonControler cannonControler;
	private Scene scene;
	private SkinnedMesh characterMesh;
	public SkinnedMesh getCharacterMesh() {
		return characterMesh;
	}

	public void setCharacterMesh(SkinnedMesh characterMesh) {
		this.characterMesh = characterMesh;
	}

	private SphereGeometry ballGeo;
	public ClothSimulator(Scene scene,SkinnedMesh characterMesh){
		this.scene=scene;
		this.characterMesh=characterMesh;
		clothControler=new ClothControler();
		cannonControler=new CannonControler();
		
		ballGeo = THREE.SphereGeometry( 1, 20, 20 );
		
		canvas = CanvasUtils.createCanvas(256, 256);
		canvas.setCoordinateSpaceWidth(512);
		canvas.setCoordinateSpaceHeight(512);
		
		canvas.setStyleName("transparent_bg");
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
		getClothControler().beforeSimulate(this);
		
		
		if(cannonControler.isEnabled()){//TODO move setting
			int iteration=1; //iteration totally kill fps,but reduce shaking
			for(int i=0;i<iteration;i++)
			cannonControler.getWorld().step(1.0/60);
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
			//LogUtils.log(mirrowName+","+index);
		}
	}

	//direct load from text
	public void loadSphereDatas(String text){
		Iterable<SphereData> newDatas=new SphereDataConverter().reverse().convertAll(CSVUtils.splitLinesWithGuava(text));
		 for(SphereData newData:newDatas){
			 addSphereData(newData);
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
	
	
	public HairMixedData addCloth(HairData hairData) {
		if(isUpdatingHairTextureMap()){
			LogUtils.log("addCloth:Warning - maybe broken texture added.still texture making");
		}
		
		ClothData data=new ClothData(hairData,characterMesh);
		
		
		data.getCloth().setPinAll();//force pin all
		
		
		//data.getCloth().ballSize=clothControls.getBallSize();
		
	
		//indivisual haiar material
		
		//TODO extract
		Texture texture=THREE.TextureLoader().load(
				//"models/mbl3d/bump2c.png"
				"/hairpattern/hairpattern1.png"
				);
		
		
		texture.setFlipY(false);
		texture.setNeedsUpdate(true);
		
		//displacementMap not good at plain when row-poly
		
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
				//.specular(0xffffff)//TODO move editor
				//.specularMap(texture)
				//.shininess(15)
				
				
				/*
				.displacementMap(texture)
				.displacementScale(16)
				.displacementBias(4)
				*/
				
				.bumpMap(texture)
				.bumpScale(0.5)
				
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
		
		Mesh object = THREE.Mesh( data.getClothGeometry(), hairMaterial );
		
		//object.getPosition().set( 0, 0, 0 );
		
		scene.add( object );
		
		HairMixedData cellData=new HairMixedData(hairData,data,object);
		
		
		//temporaly
		
		if(hairData.getHairPins().size()<3){//TODO merge 3pin
		Vector3 v1=hairPinToVertex(characterMesh,hairData.getHairPins().get(0),true);
		Vector3 v2=hairPinToVertex(characterMesh,hairData.getHairPins().get(1),true);
		
		//TODO move and fix
		int cw=hairData.getSizeOfU();
		int ch=hairData.getSizeOfV();
		
		data.getCloth().particles.get(0).setAllPosition(v1);
		data.getCloth().particles.get(cw).setAllPosition(v2);
		
		
		Vector3 sub=v2.clone().sub(v1).divideScalar(cw+1);
		for(int i=1;i<cw;i++){
			Vector3 v=sub.clone().multiplyScalar(i).add(v1);
			data.getCloth().particles.get(i).setAllPosition(v);
		}
		
		for(int i=cw+1;i<data.getCloth().particles.size();i++){
			data.getCloth().particles.get(i).setAllPosition(v1);
		}
		
		}else{
			//only core pins
			List<Vector3> pinNormals=FluentIterable.from(hairData.getHairPins()).transform(new HairPinToNormal(characterMesh)).toList();
			List<Vector3> normals=Lists.newArrayList();
			//PROBLEMS not support custom 
			
			
			
			
			
			
			
			
			
			
			//3 pin
			int cw=hairData.getSizeOfU();
			
			List<HairPin> normalPin=Lists.newArrayList();
			List<HairPin> customPin=Lists.newArrayList();
			
			for(HairPin pin:hairData.getHairPins()){
				if(pin.getTargetClothIndex()==-1){
					normalPin.add(pin);
				}else{
					customPin.add(pin);
				}
			}
			
			
			Vector3 diff = THREE.Vector3();
			//List<Mesh> spheres=clothControler.getSphereList(hairData.getChannel());
			
			
			
			
			
			int normalSize=0;
			
			for(int i=0;i<normalPin.size();i++){
				Vector3 v1=hairPinToVertex(characterMesh,normalPin.get(i),true);
				
				//executeSphereOut(v1,spheres);//for test
				
				int index=hairData.getSizeOfU()*i;
				data.getCloth().particles.get(index).setAllPosition(v1);
				
				//LogUtils.log("main:"+index);
				
				normalSize++;
				normals.add(pinNormals.get(i));
				
				if(i!=normalPin.size()-1){
					//has next;
					Vector3 v2=hairPinToVertex(characterMesh,normalPin.get(i+1),true);
					Vector3 sub=v2.clone().sub(v1).divideScalar(cw);
					
					for(int j=1;j<cw;j++){
						int multiple=j;
						int at=index+j;
						Vector3 v=sub.clone().multiplyScalar(multiple).add(v1);
						data.getCloth().particles.get(at).setAllPosition(v);
						
						normals.add(pinNormals.get(i).clone().add(pinNormals.get(i+1)).divideScalar(2));
						//LogUtils.log("sub:"+at);
						normalSize++;
					}
					
					
					//simply do it
					//normals.add(pinNormals.get(i).clone().add(pinNormals.get(i+2).divideScalar(2)));
				}	
			}
			
			//LogUtils.log("normal-test"+normalSize+","+pinNormals.size()+","+(data.getCloth().getW()+1));
			pinNormals=normals;
			//LogUtils.log("normal-pos:"+normals.size()+",pin "+pinNormals.size()+"append:"+normalSize);
			
			
			//init other posisions
			for(int j=data.getCloth().getW()+1;j<data.getCloth().particles.size();j++){
				int x=j%(data.getCloth().getW()+1);
				int y=j/(data.getCloth().getW()+1);
				//LogUtils.log(j+"="+x);
				Vector3 pos=data.getCloth().particles.get(x).getOriginal();
				//copy upper x
				
				//open widely
				data.getCloth().particles.get(j).setAllPosition(pos);
				
				
				//LogUtils.log("distance:"+(data.getCloth().getRestDistance()*10*y));
				//plus-y
				//Vector3  start=normals.get(x).clone().normalize().multiplyScalar( data.getCloth().getRestDistance()*y ).add(pos);
				//data.getCloth().particles.get(j).setAllPosition(start);
			
				if(pinNormals.size()==data.getCloth().getW()+1){
				//	LogUtils.log("can use normal");
					Vector3  normalPosition=pinNormals.get(x).clone().normalize().multiplyScalar( data.getCloth().getRestDistance()*y ).add(pos);
					data.getCloth().particles.get(j).setAllPosition(normalPosition);
				}
				
			}
			
			for(HairPin pin:customPin){
				Vector3 v=hairPinToVertex(characterMesh,pin,true);
				data.getCloth().particles.get(pin.getTargetClothIndex()).setAllPosition(v);
			}
			
			
			/*
			for(int i=0;i<cw*(hairData.getHairPins().size()-1);i++){
				ThreeLog.log(""+i, data.getCloth().particles.get(i).getOriginal());
			}
			*/
			
			int w=data.getCloth().getW()+1;
			int[] newPins=new int[w+customPin.size()];
			for(int i=0;i<w;i++){
				newPins[i]=i;
			}
			for(int i=0;i<customPin.size();i++){
				newPins[w+i]=customPin.get(i).getTargetClothIndex();
			}
			data.getCloth().setPins(newPins);
			
			/*
			LogUtils.log("pins");
			for(int i=0;i<newPins.length;i++){
				LogUtils.log(newPins[i]);
			}
			*/
		
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
		for(int i=0;i<data.getCloth().getConstrains().size();i++){
			if(!data.getCloth().isHorizontalConstraints(data.getCloth().getConstrains().get(i))){
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
	
	
	//sync textures
	MeshPhongMaterial material=selection.getMesh().getMaterial().gwtCastMeshPhongMaterial();
	HairTextureData textureData=selection.getHairData().getHairTextureData();
	//TODO support local or global
	
	int color=textureData.isUseLocalColor()?textureData.getColor():globalHairColor;
	
	//LogUtils.log("updateHairTextureData:"+color);
	material.setColor(THREE.Color(color));
	
	
	material.setOpacity(textureData.getOpacity());
	material.setAlphaTest(textureData.getAlphaTest());
	
	//TODO copy from patterns if enabled
	
	if(textureData.isEnablePatternImage()){
		if(updateHairTextureMap){
		updateHairTextureMap(selection);
		}
	}else{
		material.setMap(null);
	}
	material.setNeedsUpdate(true);
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
		HairPatternDataUtils.paint(canvas, selection.getHairData().getHairTextureData().getHairPatternData());
		
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

}
