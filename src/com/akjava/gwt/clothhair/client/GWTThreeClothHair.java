package com.akjava.gwt.clothhair.client;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;

import com.akjava.gwt.clothhair.client.HairData.HairPin;
import com.akjava.gwt.clothhair.client.HairDataFunctions.HairPinToVertex;
import com.akjava.gwt.clothhair.client.SkinningVertexCalculator.SkinningVertex;
import com.akjava.gwt.clothhair.client.cloth.ClothControler;
import com.akjava.gwt.clothhair.client.cloth.ClothData;
import com.akjava.gwt.clothhair.client.cloth.GroundYFloor;
import com.akjava.gwt.clothhair.client.sphere.SphereData;
import com.akjava.gwt.clothhair.client.sphere.SphereDataConverter;
import com.akjava.gwt.clothhair.client.sphere.SphereDataPanel;
import com.akjava.gwt.clothhair.client.sphere.SphereDataPanel.SphereDataControler;
import com.akjava.gwt.clothhair.client.texture.TexturePanel;
import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.gwt.stats.client.Stats;
import com.akjava.gwt.three.client.examples.js.THREEExp;
import com.akjava.gwt.three.client.examples.js.controls.OrbitControls;
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.gwt.core.BoundingBox;
import com.akjava.gwt.three.client.gwt.core.Intersect;
import com.akjava.gwt.three.client.gwt.renderers.WebGLRendererParameter;
import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.akjava.gwt.three.client.java.ThreeLog;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.animation.AnimationClip;
import com.akjava.gwt.three.client.js.animation.AnimationMixer;
import com.akjava.gwt.three.client.js.animation.KeyframeTrack;
import com.akjava.gwt.three.client.js.animation.tracks.QuaternionKeyframeTrack;
import com.akjava.gwt.three.client.js.cameras.PerspectiveCamera;
import com.akjava.gwt.three.client.js.core.BufferAttribute;
import com.akjava.gwt.three.client.js.core.BufferGeometry;
import com.akjava.gwt.three.client.js.core.Face3;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.core.Raycaster;
import com.akjava.gwt.three.client.js.extras.geometries.SphereGeometry;
import com.akjava.gwt.three.client.js.extras.helpers.SkeletonHelper;
import com.akjava.gwt.three.client.js.extras.helpers.VertexNormalsHelper;
import com.akjava.gwt.three.client.js.lights.AmbientLight;
import com.akjava.gwt.three.client.js.lights.DirectionalLight;
import com.akjava.gwt.three.client.js.loaders.JSONLoader.JSONLoadHandler;
import com.akjava.gwt.three.client.js.materials.Material;
import com.akjava.gwt.three.client.js.materials.MeshPhongMaterial;
import com.akjava.gwt.three.client.js.materials.MultiMaterial;
import com.akjava.gwt.three.client.js.math.Matrix3;
import com.akjava.gwt.three.client.js.math.Quaternion;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.objects.LineSegments;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;
import com.akjava.lib.common.utils.CSVUtils;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;
import com.google.gwt.user.client.ui.Widget;



/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTThreeClothHair  extends HalfSizeThreeAppWithControler implements SphereDataControler{

	 interface Driver extends SimpleBeanEditorDriver< HairData,  HairDataEditor> {}
	 Driver driver = GWT.create(Driver.class);
	
	double cameraY=700;
	private OrbitControls controls;


	private SkinnedMesh characterMesh;
	private VertexNormalsHelper vertexHelper;
	
	private Stats stats;
	//private Mesh sphere;
	
	public static GWTThreeClothHair INSTANCE;
	@Override
	public WebGLRendererParameter createRendererParameter() {
		return GWTParamUtils.WebGLRenderer().preserveDrawingBuffer(true).logarithmicDepthBuffer(true);
	}
	
	@Override
	public void animate(double timestamp) {
		
		
		
		/* for test
		for(SkinningVertexCalculator skinningVertexCalculator:skinningVertexCalculators){
			skinningVertexCalculator.update();
			
			//do temp
			//ThreeLog.log(skinningVertexCalculator.getResult().get(0));
			tmpSphere.getPosition().copy(skinningVertexCalculator.getResult().get(0));
			double size=skinningVertexCalculator.getResult().get(0).distanceTo(skinningVertexCalculator.getResult().get(1));
			//LogUtils.log("distance:"+size);
			tmpSphere.getScale().setScalar(size);	
		}
		*/
		
		
		if(clothControls!=null){
			updateSphereMeshs();//sphere first
			clothControls.update(timestamp);
		}
		
		//not support skinning
		if(vertexHelper!=null){
			vertexHelper.update();//for moving
		}
		
		
		if(mixer!=null){
			mixer.update(clock.getDelta());
		}
		
		if(skeltonHelper!=null){
			skeltonHelper.update();
		}
		
		//ThreeLog.log(camera.getPosition());
		
		//logarithmicDepthBuffer
		renderer.render(scene, camera);//render last,very important
		
		if(stats!=null){
			stats.update();
		}
	}
	
	private MeshPhongMaterial hairHeadMaterial;
	private SphereGeometry ballGeo;
	private Mesh tmpSphere;
	
	private SkeletonHelper skeltonHelper;
	
	@Override
	public void onInitializedThree() {
		super.onInitializedThree();
		
		INSTANCE=this;
		
		renderer.setClearColor(0xffffff);
		
		rendererContainer.addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				onDocumentMouseMove(event);
			}
		});
		
		rendererContainer.addMouseDownHandler(new MouseDownHandler() {
			@Override
			public void onMouseDown(MouseDownEvent event) {
				doMouseDown(event);
			}
		});
		
		
		controls = THREEExp.OrbitControls(camera,rendererContainer.getElement() );
		controls.setTarget(THREE.Vector3( 0, cameraY, 0 ));
		controls.update();

		
		AmbientLight ambient = THREE.AmbientLight( 0xeeeeee );//var ambient = new THREE.AmbientLight( 0xffffff );
		scene.add( ambient );

		DirectionalLight directionalLight = THREE.DirectionalLight( 0x333333 );//var directionalLight = new THREE.DirectionalLight( 0x444444 );
		directionalLight.getPosition().set( -1, 1, 1 ).normalize();//directionalLight.position.set( -1, 1, 1 ).normalize();
		scene.add( directionalLight );
		
		/*
		 * shape morph need special treatment for r74
		 */
		//String url= "models/mbl3d/model8o.json";//var url= "morph.json";
		
		String url= "models/mbl3d/model8-hair-color-expand-bone.json";//"models/mbl3d/model8-hair-color-expand.json"
		THREE.JSONLoader().load(url,new JSONLoadHandler() {
			

			

			

			

			

			

			

			@Override
			public void loaded(Geometry geometry,JsArray<Material> materials) {
				
				
				
				geometry.computeBoundingBox();
				BoundingBox bb = geometry.getBoundingBox();
				//LogUtils.log(bb);
				//double x=-20, y=-1270,z= -300,s= 800;

				double x=-0, y=-0,z= -0,s= 1000;
				y=-bb.getMax().getY()/2*s;
				
				for(int i=0;i<materials.length();i++){
					MeshPhongMaterial m=materials.get(i).cast();//need cast GWT problem
					if(m.getName().equals("Hair")){
						hairHeadMaterial=m;
						//m.setColor(THREE.Color(hairColor));
						m.setColor(hairMaterial.getColor());//use same ref
						m.setSpecular(THREE.Color(0xffffff));//less shine
						m.setShininess(15);
					}else{//
						m.setSpecular(THREE.Color(0x111111));//less shine
						m.setShininess(5);
					}
					
					
					
				}
				
				
				
				
				
				for(int i=0;i<materials.length();i++){
					materials.get(i).gwtCastMeshPhongMaterial().setSkinning(true);
				}
				
				MultiMaterial mat=THREE.MultiMaterial(materials );//var mat=THREE.MultiMaterial( materials);//MultiMaterial mat=THREE.MultiMaterial( materials);//var mat=new THREE.MultiMaterial( materials);


				characterMesh = THREE.SkinnedMesh( geometry, mat );//mesh = THREE.SkinnedMesh( geometry, mat );//mesh = THREE.SkinnedMesh( geometry, mat );//mesh = new THREE.SkinnedMesh( geometry, mat );
				
				
				//for updebug
				skeltonHelper = THREE.SkeletonHelper( characterMesh );
				skeltonHelper.setVisible(false);
				scene.add( skeltonHelper );
				
				
				//ThreeLog.logBoneNames(characterMesh);
				
				//LogUtils.log(characterMesh);
				
				characterMesh.setName("model");//mesh.setName("model");//mesh.setName("model");//mesh.name = "model";
				//mesh.getPosition().set( x, y - bb.getMin().getY() * s, z );//mesh.getPosition().set( x, y - bb.getMin().y * s, z );//mesh.getPosition().set( x, y - bb.getMin().y * s, z );//mesh.position.set( x, y - bb.min.y * s, z );
				characterMesh.getPosition().set(x, y, z);
				characterMesh.getScale().set( s, s, s );//mesh.getScale().set( s, s, s );//mesh.getScale().set( s, s, s );//mesh.scale.set( s, s, s );
				scene.add( characterMesh );
				
				
				vertexHelper = THREE.VertexNormalsHelper(characterMesh, 1.6, 0x008800, 2);
				scene.add(vertexHelper);
				//scene.add(THREE.VertexNormalsHelper(mesh, 0.2, 0x0000ff, 3));//can overwrite
			
				
				ballGeo = THREE.SphereGeometry( 1, 20, 20 );
				
				
				
				
				clothControls=new ClothControler();
				clothControls.setFloorModifier(new GroundYFloor(0));
				
				
				//sphere.getScale().setScalar(clothControls.getBallSize());
				
				
				
				
				createControler();
				
				sphereDataPanel.setSkelton(characterMesh.getSkeleton());
				
				
				clothControls.setWind(true);
				
				
				/* for test
				SkinningVertexCalculator temp=new SkinningVertexCalculator(characterMesh);
				//circle style
				temp.add(new SkinningVertex(THREE.Vector3(0,bb.getMax().getY(),0), THREE.Vector4(60, 60, 60, 60), THREE.Vector4(1,0,0,0)));
				temp.add(new SkinningVertex(THREE.Vector3(0,bb.getMax().getY(),.1), THREE.Vector4(60, 60, 60, 60), THREE.Vector4(1,0,0,0)));
				
				skinningVertexCalculators.add(temp);
				//temp sphere
				MeshPhongMaterial tmpMaterial = THREE.MeshPhongMaterial( GWTParamUtils.MeshPhongMaterial().color(0x888888).side(THREE.DoubleSide).transparent(true).opacity(0.5));
				
				tmpSphere = THREE.Mesh( ballGeo, tmpMaterial );
				scene.add( tmpSphere );
				*/
				mixer=THREE.AnimationMixer(characterMesh);
				
				stats = Stats.insertStatsToRootPanel();
			}
			
			
			
			
			
			
			
			
		});
		
		hairMaterial = THREE.MeshPhongMaterial(GWTParamUtils.
				MeshPhongMaterial().color(hairColor).side(THREE.DoubleSide).specular(0xffffff).shininess(15)
				.alphaTest(0.9)//best for cloth
				.transparent(true).opacity(1)
				
				//.map(THREE.TextureLoader().load("models/mbl3d/hair1.png"))
				);
		
		
		
	}
	
	//List<SkinningVertexCalculator> skinningVertexCalculators=Lists.newArrayList();
	

	
	
	Map<SphereData,SphereCalculatorAndMesh> sphereMeshMap=Maps.newHashMap();
	
	public static class SphereCalculatorAndMesh{
		private Mesh mesh;
		public SphereCalculatorAndMesh(SkinnedMesh character,int boneIndex,Mesh sphere) {
			super();
			this.mesh = sphere;
			calculator=new SkinningVertexCalculator(character);
			calculator.add(new SkinningVertex(THREE.Vector3(), THREE.Vector4(boneIndex, boneIndex, boneIndex, boneIndex), THREE.Vector4(1,0,0,0)));
			calculator.add(new SkinningVertex(THREE.Vector3(), THREE.Vector4(boneIndex, boneIndex, boneIndex, boneIndex), THREE.Vector4(1,0,0,0)));
			
		}
		public Mesh getMesh() {
			return mesh;
		}
		public void setMesh(Mesh mesh) {
			this.mesh = mesh;
		}
		public SkinningVertexCalculator getCalculator() {
			return calculator;
		}
		public void setCalculator(SkinningVertexCalculator calculator) {
			this.calculator = calculator;
		}
		private SkinningVertexCalculator calculator;
	}
	
	
	@Override
	public void removeSphereData(SphereData data){
		Mesh sphere=sphereMeshMap.get(data).getMesh();
		clothControls.removeSphere(sphere);
		scene.remove(sphere);
	}
	@Override
	public void addSphereData(SphereData data){
		MeshPhongMaterial ballMaterial = THREE.MeshPhongMaterial( GWTParamUtils.MeshPhongMaterial().color(0x888888).side(THREE.DoubleSide).wireframe(true));
		
		Mesh sphere = THREE.Mesh( ballGeo, ballMaterial );//		sphere = new THREE.Mesh( ballGeo, ballMaterial );
		scene.add( sphere );
		
		sphere.getScale().setScalar(data.getSize());
		
		clothControls.addSphere(sphere);
		
		
		sphereMeshMap.put(data, new SphereCalculatorAndMesh(characterMesh, data.getBoneIndex(), sphere));
	}
	
	public void syncSphereDataAndSkinningVertexCalculator(SphereData data){
		if(data==null){
			return;
		}
		SkinningVertexCalculator calculator=sphereMeshMap.get(data).getCalculator();
		for(SkinningVertex vertex:calculator.getSkinningVertexs()){
			vertex.getSkinIndices().setX(data.getBoneIndex());
		}
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
	
	
	
	@Override
	public PerspectiveCamera createCamera(){
		
		PerspectiveCamera camera=THREE.PerspectiveCamera(45, getWindowInnerWidth()/getWindowInnerHeight(), 10, 20000);
		camera.getPosition().set(0, cameraY, 500);
		return camera;
	}
	

	ClothControler clothControls;

	private int hairColor=0x553817;

	
	protected Vector3 matrixedPoint(Vector3 vec){
		return vec.clone().applyMatrix4(characterMesh.getMatrixWorld());
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
		
		if(applyMatrix4){
		return vertex.clone().applyMatrix4( mesh.getMatrixWorld());
		}else{
		return vertex.clone();
		}
		
	}
	
	protected void doMouseDown(MouseDownEvent event) {
		double mx=event.getX();
		double my=event.getY();
		/*
		 * conver -1-1 cordinate
		 */
		Vector3 screenPosition=THREE.Vector3(( mx / SCREEN_WIDTH ) * 2 - 1, - ( my / SCREEN_HEIGHT ) * 2 + 1,1 );//no idea why 0.5
		screenPosition.unproject(camera);
		Raycaster ray=THREE.Raycaster(camera.getPosition(), screenPosition.sub( camera.getPosition() ).normalize());
	
		JsArray<Intersect> intersects=ray.intersectObject(characterMesh);
		
		//find nearlist vertex
		
		if(intersects.length()>0){
			//LogUtils.log("intersects:"+intersects.length());
			
			Face3 face=intersects.get(0).getFace();
			Vector3 point=intersects.get(0).getPoint();
			int faceIndex=intersects.get(0).getFaceIndex();
			
			
			//ThreeLog.log("point:",point);
			
			int vertexOfFaceIndex=0;
			Vector3 selection=characterMesh.getGeometry().getVertices().get(face.getA());
		
			//ThreeLog.log("vertex1:",mesh.getGeometry().getVertices().get(face.getA()));
			double distance=point.distanceTo(matrixedPoint(characterMesh.getGeometry().getVertices().get(face.getA())));
			
			//ThreeLog.log("vertex2:",mesh.getGeometry().getVertices().get(face.getB()));
			double distance2=point.distanceTo(matrixedPoint(characterMesh.getGeometry().getVertices().get(face.getB())));
			if(distance2<distance){
				vertexOfFaceIndex=1;
				distance=distance2;
				selection=characterMesh.getGeometry().getVertices().get(face.getB());
			}
			//ThreeLog.log("vertex3:",mesh.getGeometry().getVertices().get(face.getC()));
			
			double distance3=point.distanceTo(matrixedPoint(characterMesh.getGeometry().getVertices().get(face.getC())));
			if(distance3<distance){
				vertexOfFaceIndex=2;
				distance=distance3;
				selection=characterMesh.getGeometry().getVertices().get(face.getC());
			}
			
			//make lines
			double size=4.8;
			Matrix3 normalMatrix=THREE.Matrix3();
			Vector3 v1 = THREE.Vector3();
			Vector3 v2 = THREE.Vector3();
			
			normalMatrix.getNormalMatrix( characterMesh.getMatrixWorld());
			
			Vector3 normal = face.getVertexNormals().get(vertexOfFaceIndex);

			v1.copy( selection ).applyMatrix4( characterMesh.getMatrixWorld() );

			v2.copy( normal ).applyMatrix3( normalMatrix ).normalize().multiplyScalar( size ).add( v1 );
			
			BufferGeometry geometry = THREE.BufferGeometry();

			BufferAttribute positions = THREE.Float32Attribute( 2*3, 3 );
			geometry.addAttribute( "position", positions );
			
			positions.setXYZ( 0, v1.getX(), v1.getY(), v1.getZ() );
			positions.setXYZ( 1, v2.getX(), v2.getY(), v2.getZ() );
			

			currentSelection=new HairPin(faceIndex,vertexOfFaceIndex);
			
			
			if(selectedLine!=null){
				scene.remove(selectedLine);
			}
			
			selectedLine=THREE.LineSegments(geometry.gwtCastGeometry(), THREE.LineBasicMaterial(GWTParamUtils.LineBasicMaterial().color(0xff0000).linewidth(2)));
			scene.add(selectedLine);
			
		}
	}
	LineSegments selectedLine;
	
	LineSegments hairDataLine;
	
	private void updateHairDataLine(){
		
		if(firstSelection==null && secondSelection==null){
			return;
		}
		
		//TODO support multiple
		List<HairPin> pins=Lists.newArrayList();
		if(firstSelection!=null){
			pins.add(firstSelection);
		}
		
		if(secondSelection!=null){
			pins.add(secondSelection);
		}
		
		if(thirdSelection!=null){
			pins.add(thirdSelection);
		}
		HairPinToVertex hairPinToVertex=new HairPinToVertex(characterMesh,true);
		Matrix3 normalMatrix=THREE.Matrix3();
		normalMatrix.getNormalMatrix( characterMesh.getMatrixWorld());
		
		
		BufferGeometry geometry = THREE.BufferGeometry();

		BufferAttribute positions = THREE.Float32Attribute( 2*3 * pins.size(), 3 );
		geometry.addAttribute( "position", positions );
		
		
		
		
		
		double size=3.2;
		for(int i=0;i<pins.size();i++){
			HairPin pin=pins.get(i);
			
			Vector3 v2 = THREE.Vector3();
			Vector3 v1=hairPinToVertex.apply(pin);
			
			Face3 face=characterMesh.getGeometry().getFaces().get(pin.getFaceIndex());
			Vector3 normal = face.getVertexNormals().get(pin.getVertexOfFaceIndex());
			v2.copy( normal ).applyMatrix3( normalMatrix ).normalize().multiplyScalar( size ).add( v1 );
			
			positions.setXYZ(2*i+ 0, v1.getX(), v1.getY(), v1.getZ() );
			positions.setXYZ(2*i+ 1, v2.getX(), v2.getY(), v2.getZ() );
		}
		
		if(hairDataLine!=null){
			scene.remove(hairDataLine);
		}
		
		hairDataLine=THREE.LineSegments(geometry.gwtCastGeometry(), THREE.LineBasicMaterial(GWTParamUtils.LineBasicMaterial().color(0x0000ff).linewidth(2)));
		scene.add(hairDataLine);
		
	}
	
	
	
	
	private StorageControler storageControler=new StorageControler();
	/**
	 * must call after sphere initialized;
	 */
	private void createControler() {
		TabPanel tab=new TabPanel();
		
		tab.add(createSpherePanel(), "spheres");
		controlerRootPanel.add(tab);
		tab.selectTab(0);
		
		VerticalPanel basicPanel=new VerticalPanel();
		tab.add(basicPanel,"basic");
		
		basicPanel.add(new Label("Wind"));
		CheckBox windCheck=new CheckBox();
		windCheck.setValue(true);
		windCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				clothControls.setWind(event.getValue());
			}
		});
		basicPanel.add(windCheck);
		
		basicPanel.add(new Label("Camera"));
		LabeledInputRangeWidget2 near=new LabeledInputRangeWidget2("near", 0.1, 100, 0.1);
		
		
		
		basicPanel.add(near);
		near.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				camera.setNear(event.getValue().doubleValue());
				camera.updateProjectionMatrix();
			}
		});
		near.setValue(camera.getNear());
		
		//vertex
		basicPanel.add(new Label("Vertex"));
		HorizontalPanel h0=new HorizontalPanel();
		CheckBox visibleVertexCheck=new CheckBox("visible");
		visibleVertexCheck.setValue(true);
		visibleVertexCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				vertexHelper.setVisible(event.getValue());
				if(selectedLine!=null){
					selectedLine.setVisible(event.getValue());
				}
				if(hairDataLine!=null){
					hairDataLine.setVisible(event.getValue());
				}
			}
		});
		h0.add(visibleVertexCheck);
		basicPanel.add(h0);
		
		
		/*
		SphereInfoPanel sphereInfoPanel=new SphereInfoPanel(storageControler,sphere,clothControls);
		controlerRootPanel.add(sphereInfoPanel);
		*/
		
		
		
		//texture panel;
		basicPanel.add(new Label("Texture"));
		basicPanel.add(new TexturePanel(hairMaterial));
		
		
		basicPanel.add(new HTML("<h4>Hair Editor</h4>"));
		
		
		HorizontalPanel hairPanel=new HorizontalPanel();
		basicPanel.add(hairPanel);
		CheckBox showHair=new CheckBox("show hairs");
		showHair.setValue(true);
		hairPanel.add(showHair);
		showHair.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				for(HairCellObjectData data:cellObjects.getDatas()){
					data.getMesh().setVisible(event.getValue());
				}
			}
		});
		
		
		//editor
		HairDataEditor editor=new HairDataEditor();
		driver.initialize(editor);
		basicPanel.add(editor);
		createClothPanel(basicPanel);
		
		driver.edit(new HairData());//new data
		
		
		SimpleCellTable<HairCellObjectData> table=new SimpleCellTable<HairCellObjectData>() {
			@Override
			public void addColumns(CellTable<HairCellObjectData> table) {
				TextColumn<HairCellObjectData> nameColumn=new TextColumn<HairCellObjectData>() {
					@Override
					public String getValue(HairCellObjectData object) {
						return hairDataConverter.convert(object.getHairData());
					}
				};
				table.addColumn(nameColumn);
			}
		};
		basicPanel.add(table);
		
		HorizontalPanel editPanel=new HorizontalPanel();
		basicPanel.add(editPanel);
		
		
		Button edit=new Button("remove & edit",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HairCellObjectData data=cellObjects.getSelection();
				if(data!=null){
					removeHairData(data);
					driver.edit(data.getHairData());
					firstSelection=data.getHairData().getHairPins().get(0);
					secondSelection=data.getHairData().getHairPins().get(1);
					if(data.getHairData().getHairPins().size()>2){
						thirdSelection=data.getHairData().getHairPins().get(2);
					}
					//LogUtils.log(hairDataConverter.convert(data.getHairData()));
					updateHairDataLine();
				}
			}
		});
		editPanel.add(edit);
		
		Button remove=new Button("remove",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HairCellObjectData data=cellObjects.getSelection();
				if(data!=null){
					removeHairData(data);
				}
			}
		});
		editPanel.add(remove);
		
		Button copy=new Button("Copy",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HairCellObjectData data=cellObjects.getSelection();
				if(data!=null){
					HairData copied=data.getHairData().clone();
					driver.edit(copied);
				}
			}
		});
		editPanel.add(copy);
		
		
		cellObjects = new EasyCellTableObjects<HairCellObjectData>(table){
			@Override
			public void onSelect(HairCellObjectData selection) {
				// TODO Auto-generated method stub
				//editor edit
			}};
			
			
		//
		 String text=storageControler.getValue(HairStorageKeys.temp_hairset, null);
		 if(text!=null && !text.isEmpty()){
			 Iterable<HairData> hairDatas=hairDataConverter.reverse().convertAll(CSVUtils.splitLinesWithGuava(text));
			 for(HairData hairData:hairDatas){
				 addCloth(hairData);
			 }
		 }
		
		 
		 FileUploadForm upload=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				clearAllHairData();
				//todo check validate
				 Iterable<HairData> hairDatas=hairDataConverter.reverse().convertAll(CSVUtils.splitLinesWithGuava(text));
				 for(HairData hairData:hairDatas){
					 addCloth(hairData);
				 }
				
			}
		}, true, "UTF-8");
		 upload.setAccept(".csv");
		 basicPanel.add(upload);
		 
		 HorizontalPanel downloadPanels=new HorizontalPanel();
		 basicPanel.add(downloadPanels);
		 final HorizontalPanel download=new HorizontalPanel();
		 
		 Button downloadBt=new Button("download",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				download.clear();
				String text=toStoreText();
				Anchor a=HTML5Download.get().generateTextDownloadLink(text, "hair.csv", "click to download",true);
				download.add(a);
			}
		});
		 downloadPanels.add(downloadBt);
		 downloadPanels.add(download);
		 
		 tab.add(new CharacterMovePanel(characterMesh),"character");
		 tab.add(new GravityPanel(clothControls),"gravity");
	}
	
	
	private Widget createSpherePanel() {
		VerticalPanel  panel=new VerticalPanel();
		
		double ballSize=.1;
		
		//double s=characterMesh.getScale().getX();
		//initial data
		//TODO find better way
		
		//TODO find better index by automatic
		
		SphereData firstOne=new SphereData(0, characterMesh.getPosition().getY()/characterMesh.getScale().getX()*-1+0.75, 0, ballSize, true,60);//hard code 60 is head
		//addSphereData(firstOne);
		
		HorizontalPanel controler=new HorizontalPanel();
		panel.add(controler);
		CheckBox visibleAllCheck=new CheckBox("visible all");
		visibleAllCheck.setValue(true);
		visibleAllCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				for(SphereCalculatorAndMesh smesh:sphereMeshMap.values()){
					smesh.getMesh().setVisible(event.getValue());
				}
			}
			
		});
		controler.add(visibleAllCheck);
	
		sphereDataPanel = new SphereDataPanel(this, firstOne);
	
		String lines=storageControler.getValue(HairStorageKeys.KEY_SPHERES, null);
		if(lines!=null){
			Iterable<SphereData> datas=new SphereDataConverter().reverse().convertAll(CSVUtils.splitLinesWithGuava(lines));
			for(SphereData data:datas){
				sphereDataPanel.addSpereData(data);
			}
		}else{
			//if empty
			sphereDataPanel.addSpereData(firstOne.clone());
		}
		panel.add(sphereDataPanel);
		
		
		
		// TODO Auto-generated method stub
		return panel;
	}

	protected void removeHairData(HairCellObjectData data) {
		checkNotNull(data,"removeHairData:data is null");
		scene.remove(data.getMesh());
		clothControls.removeClothData(data.getClothData());
		cellObjects.removeItem(data);
		
		storeDatas();
	}
	
	private void clearAllHairData(){
		for(HairCellObjectData data:cellObjects.getDatas()){
			removeHairData(data);
		}
	}


	private HairDataConverter hairDataConverter=new HairDataConverter();
	
	
	private HairPin currentSelection;
	private HairPin firstSelection;
	private HairPin secondSelection;
	private HairPin thirdSelection;
	
	private void createClothPanel(Panel parent){
		
		//tmp
		HorizontalPanel h=new HorizontalPanel();
		parent.add(h);
		
		Button first=new Button("first",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				firstSelection=currentSelection;
				updateHairDataLine();
			}
		});
		h.add(first);
		
		
		Button second=new Button("second",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				secondSelection=currentSelection;
				updateHairDataLine();
			}
		});
		h.add(second);
		
		Button third=new Button("third",new ClickHandler() {
			

			@Override
			public void onClick(ClickEvent event) {
				thirdSelection=currentSelection;
				updateHairDataLine();
			}
		});
		h.add(third);
		
		Button clear=new Button("clear third",new ClickHandler() {
			

			@Override
			public void onClick(ClickEvent event) {
				thirdSelection=null;
				updateHairDataLine();
			}
		});
		h.add(clear);
		
		Button addCloth=new Button("add cloth",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addCloth();
			}
		});
		h.add(addCloth);
		
	}

	
	private class HairCellObjectData{
		private HairData hairData;
		public HairCellObjectData(HairData hairData, ClothData clothData, Mesh mesh) {
			super();
			this.hairData = hairData;
			this.clothData = clothData;
			this.mesh = mesh;
		}
		public HairData getHairData() {
			return hairData;
		}
		public void setHairData(HairData hairData) {
			this.hairData = hairData;
		}
		public ClothData getClothData() {
			return clothData;
		}
		public void setClothData(ClothData clothData) {
			this.clothData = clothData;
		}
		public Mesh getMesh() {
			return mesh;
		}
		public void setMesh(Mesh mesh) {
			this.mesh = mesh;
		}
		private ClothData clothData;
		private Mesh mesh;
	}
	
	protected void addCloth() {
		
		if(firstSelection==null || secondSelection==null){
			LogUtils.log("need first & second");
			return;
		}
		
		Vector3 v1=hairPinToVertex(characterMesh,firstSelection,true);
		Vector3 v2=hairPinToVertex(characterMesh,secondSelection,true);
		
		double distance=v1.distanceTo(v2);
		
		if(distance==0){
			LogUtils.log("invalidly first & second same:"+ThreeLog.get(v1)+","+ThreeLog.get(v1));
			return;
		}
		
		HairData hairData=driver.flush();
		
		driver.edit(new HairData());//add another one.
		
		
		hairData.getHairPins().clear();
		hairData.getHairPins().add(firstSelection);
		hairData.getHairPins().add(secondSelection);
		if(thirdSelection!=null){
			hairData.getHairPins().add(thirdSelection);
		}
		addCloth(hairData);
	}
	protected void addCloth(HairData hairData) {
		
		ClothData data=new ClothData(hairData,characterMesh);
		clothControls.addClothData(data);
		
		data.getCloth().setPinAll();
		
		
		//data.getCloth().ballSize=clothControls.getBallSize();
		
	
		
		Mesh object = THREE.Mesh( data.getClothGeometry(), hairMaterial );
		//object.getPosition().set( 0, 0, 0 );
		
		scene.add( object );
		
		
		cellObjects.addItem(new HairCellObjectData(hairData,data,object));
		
		
		
		//temporaly
		
		if(hairData.getHairPins().size()<3){
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
			int cw=hairData.getSizeOfU();
			for(int i=0;i<hairData.getHairPins().size();i++){
				Vector3 v1=hairPinToVertex(characterMesh,hairData.getHairPins().get(i),true);
				int index=hairData.getSizeOfU()*i;
				data.getCloth().particles.get(index).setAllPosition(v1);
				
				//LogUtils.log("main:"+index);
				
				
				if(i!=hairData.getHairPins().size()-1){
					//has next;
					Vector3 v2=hairPinToVertex(characterMesh,hairData.getHairPins().get(i+1),true);
					Vector3 sub=v2.clone().sub(v1).divideScalar(cw);
					
					for(int j=1;j<cw;j++){
						int multiple=j;
						int at=index+j;
						Vector3 v=sub.clone().multiplyScalar(multiple).add(v1);
						data.getCloth().particles.get(at).setAllPosition(v);
						
						//LogUtils.log("sub:"+at);
					}
					
				}
				
				if(i==0){//not pin start with v1
					for(int j=cw*(hairData.getHairPins().size()-1)+1;j<data.getCloth().particles.size();j++){
						data.getCloth().particles.get(j).setAllPosition(v1);
					}
				}
			}
			
			/*
			for(int i=0;i<cw*(hairData.getHairPins().size()-1);i++){
				ThreeLog.log(""+i, data.getCloth().particles.get(i).getOriginal());
			}
			*/
			
			
			
		}
		
		
		storeDatas();
	}
	
	
	
	 
	private String toStoreText(){
		String text=
				Joiner.on("\r\n").join(hairDataConverter.convertAll(FluentIterable.from(cellObjects.getDatas()).transform(new Function<HairCellObjectData, HairData>() {
					@Override
					public HairData apply(HairCellObjectData input) {
						return input.getHairData();
					}
				})));
		return text;
	}
	private void storeDatas() {
		if(cellObjects.getDatas().size()==0){
			storageControler.removeValue(HairStorageKeys.temp_hairset);
			return;
		}
		
		String text=
				toStoreText();
		
		try {
			storageControler.setValue(HairStorageKeys.temp_hairset, text);
		} catch (StorageException e) {
			Window.alert(e.getMessage());
		}
		
	}


	private EasyCellTableObjects<HairCellObjectData> cellObjects;



	


	
	

	
	int mouseX,mouseY;
	private MeshPhongMaterial hairMaterial;
	
	protected void onDocumentMouseMove(MouseMoveEvent event) {
		mouseX = ( event.getClientX() - windowHalfX );
		mouseY = ( event.getClientY() - windowHalfY )*2;
	}

	private SphereData selectedSphere;

	@Override
	public void onSelectSphere(SphereData data) {
		if(selectedSphere!=null){
			unselectShpere(selectedSphere);
		}
		selectedSphere=data;
		selectShpere(selectedSphere);
	}

	private void selectShpere(SphereData selectedSphere) {
		MeshPhongMaterial material=sphereMeshMap.get(selectedSphere).getMesh().getMaterial().gwtCastMeshPhongMaterial();
		material.getColor().setHex(0x0000ff);
	}
	
	private void unselectShpere(SphereData selectedSphere) {
		MeshPhongMaterial material=sphereMeshMap.get(selectedSphere).getMesh().getMaterial().gwtCastMeshPhongMaterial();
		material.getColor().setHex(0x888888);
	}
	
	private AnimationMixer mixer;

	private SphereDataPanel sphereDataPanel;
	
	//TODO add option mirror
	public void startAnimation(double x,double y,double z){
		
		//LogUtils.log(characterMesh);
		
		stopAnimation();
		Quaternion q=THREE.Quaternion();
		
		Quaternion xq=THREE.Quaternion().setFromAxisAngle(THREE.Vector3(1, 0, 0), x);
		q.multiply(xq);
		
		Quaternion yq=THREE.Quaternion().setFromAxisAngle(THREE.Vector3(0, 1, 0), y);
		q.multiply(yq);
		
		Quaternion zq=THREE.Quaternion().setFromAxisAngle(THREE.Vector3(0, 0, 1), z);
		q.multiply(zq);
		
		Quaternion q2=THREE.Quaternion();
		
		Quaternion xq2=THREE.Quaternion().setFromAxisAngle(THREE.Vector3(1, 0, 0), x*-1);
		q2.multiply(xq2);
		
		Quaternion yq2=THREE.Quaternion().setFromAxisAngle(THREE.Vector3(0, 1, 0), y*-1);
		q2.multiply(yq2);
		
		Quaternion zq2=THREE.Quaternion().setFromAxisAngle(THREE.Vector3(0, 0, 1), z*-1);
		q2.multiply(zq2);
		
		double duration=1.0;
		
		JsArray<KeyframeTrack> tracks=JavaScriptObject.createArray().cast();
		
		JsArrayNumber times=JavaScriptObject.createArray().cast();
		times.push(0);
		times.push(duration);
		times.push(duration*2);
		times.push(duration*3);
		times.push(duration*4);
		JsArrayNumber values=JsArray.createArray().cast();
		
		
		
		concat(values,THREE.Quaternion().toArray());
		concat(values,q.toArray());
		concat(values,THREE.Quaternion().toArray());
		concat(values,q2.toArray());
		concat(values,THREE.Quaternion().toArray());
		
		//LogUtils.log(values);
		
		//value is not valid number
		
		//head fixed
		//quaternion is alias for rot
		QuaternionKeyframeTrack track=THREE.QuaternionKeyframeTrack(".bones[60].quaternion", times, values);
		
		tracks.push(track);
		
		AnimationClip clip=THREE.AnimationClip("anime", -1, tracks);
		//LogUtils.log(track.validate());
		
		mixer.uncacheClip(clip);//same name cache that.
		mixer.clipAction(clip).play();
	}
	
	public void concat(JsArrayNumber target,JsArrayNumber values){
		for(int i=0;i<values.length();i++){
			target.push(values.get(i));
		}
	}
	
	public void stopAnimation() {
		mixer.stopAllAction();
		
		//characterMesh.getGeometry().getBones().get(60).setRotq(q)
	}
}
