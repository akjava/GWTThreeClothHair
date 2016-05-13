package com.akjava.gwt.clothhair.client;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.Map;

import javax.annotation.Nullable;

import com.akjava.gwt.clothhair.client.SkinningVertexCalculator.SkinningVertex;
import com.akjava.gwt.clothhair.client.cloth.ClothControler;
import com.akjava.gwt.clothhair.client.cloth.GroundYFloor;
import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.akjava.gwt.clothhair.client.hair.HairDataPanel;
import com.akjava.gwt.clothhair.client.sphere.SphereData;
import com.akjava.gwt.clothhair.client.sphere.SphereDataConverter;
import com.akjava.gwt.clothhair.client.sphere.SphereDataPanel;
import com.akjava.gwt.clothhair.client.sphere.SphereDataPanel.SphereDataControler;
import com.akjava.gwt.clothhair.client.texture.HairTexturePanel;
import com.akjava.gwt.clothhair.client.texture.TexturePanel;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.stats.client.Stats;
import com.akjava.gwt.three.client.examples.js.THREEExp;
import com.akjava.gwt.three.client.examples.js.controls.OrbitControls;
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.gwt.core.BoundingBox;
import com.akjava.gwt.three.client.gwt.core.Intersect;
import com.akjava.gwt.three.client.gwt.renderers.WebGLRendererParameter;
import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
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
import com.akjava.gwt.three.client.js.scenes.Scene;
import com.akjava.gwt.three.client.js.textures.Texture;
import com.akjava.lib.common.utils.CSVUtils;
import com.google.common.collect.Maps;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
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
		
		//mixer first,this make animation
		if(mixer!=null){
			mixer.update(clock.getDelta());//shoud i?
		}
		
		
		
		
		//not support skinning
		if(vertexHelper!=null){
			vertexHelper.update();//for moving
		}
		
		
		
		
		if(skeltonHelper!=null){
			skeltonHelper.update();
		}
		
		//ThreeLog.log(camera.getPosition());
		
		if(clothControls!=null){
			updateSphereMeshs();//sphere first
			clothControls.update(timestamp);
		}
		
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

	private double GROUND=-750;
	
	@Override
	public void onInitializedThree() {
		super.onInitializedThree();
		setRightTopPopupWidth("360px");
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

		
		AmbientLight ambient = THREE.AmbientLight( 0xcccccc );//var ambient = new THREE.AmbientLight( 0xffffff );
		scene.add( ambient );

		DirectionalLight directionalLight = THREE.DirectionalLight( 0x444444 );//var directionalLight = new THREE.DirectionalLight( 0x444444 );
		directionalLight.getPosition().set( -1, 1, 1 ).normalize();//directionalLight.position.set( -1, 1, 1 ).normalize();
		scene.add( directionalLight );
		
		
		
		MeshPhongMaterial groundMaterial = THREE.MeshPhongMaterial( GWTParamUtils.MeshPhongMaterial().color(0x888888).specular(0x111111)
				.transparent(true).opacity(0.5).side(THREE.DoubleSide));
		
		groundMesh = THREE.Mesh( THREE.PlaneBufferGeometry( 20000, 20000 ), groundMaterial );
		groundMesh.getPosition().setY(GROUND);//mesh.position.y = -250;
		groundMesh.getRotation().setX(- Math.PI / 2);//mesh.rotation.x = - Math.PI / 2;
		groundMesh.setReceiveShadow(true);//mesh.receiveShadow = true;
		scene.add( groundMesh );
		
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
				clothControls.setFloorModifier(new GroundYFloor(GROUND));
				
				
				//sphere.getScale().setScalar(clothControls.getBallSize());
				
				
				
				
				createControler();
				
				//dont modify skeleton
				sphereDataPanel.setSkelton(characterMesh.getSkeleton());
				characterMovePanel.setSkelton(characterMesh.getSkeleton());
				
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
		
		clothControls.addSphere(sphere,data.getChannel());
		
		
		
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
		
		//plus sync channel
		clothControls.updateSphere(sphereMeshMap.get(data).getMesh(), data.getChannel());
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
			

			hairDataPanel.setCurrentSelection(new HairPin(faceIndex,vertexOfFaceIndex));
			
			
			
			
			
			
			LineSegments selectedLine=THREE.LineSegments(geometry.gwtCastGeometry(), THREE.LineBasicMaterial(GWTParamUtils.LineBasicMaterial().color(0xff0000).linewidth(2)));
			selectedLine.setVisible(vertexVisible);
			hairDataPanel.setNewLine(selectedLine);
			
			
			
		}
	}
	
	public MeshPhongMaterial getHairMaterial(){
		return hairMaterial;
	}

	public ClothControler getClothControler(){
		return clothControls;
	}

	public Scene getScene(){
		return scene;
	}
	
	
	public void onTabSelected(int index){
		
		if(index!=0){
			updateSphereVisible(false);
		}else{
			updateSphereVisible(true);
		}
		
		if(index!=1){
			updateVertexVisible(false);
		}else{
			updateVertexVisible(true);
		}
		
	}
	

	private boolean vertexVisible=true;
	/**
	 * must call after sphere initialized;
	 */

	private CharacterMovePanel characterMovePanel;
	
	private Panel createBasicPanel(){
		VerticalPanel basicPanel=new VerticalPanel();

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
		
		basicPanel.add(new Label("Ground"));
		HorizontalPanel groundPanel=new HorizontalPanel();
		basicPanel.add(groundPanel);
		CheckBox groundCheck=new CheckBox();
		groundCheck.setValue(true);
		groundCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				groundMesh.setVisible(event.getValue());
				clothControls.getFloorModifier().setEnabled(event.getValue());
			}
		});
		basicPanel.add(groundCheck);
		
		
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
				updateVertexVisible(event.getValue());
			}
		});
		h0.add(visibleVertexCheck);
		basicPanel.add(h0);
		return basicPanel;
	}
	
	HairDataPanel hairDataPanel;
	private Panel createHairDataPanel(){
		hairDataPanel= new HairDataPanel(characterMesh);
		return hairDataPanel;
	}
	
	private Panel createTexturePanel(){
		TexturePanel texturePanel=new TexturePanel(hairMaterial);
		texturePanel.add(new HairTexturePanel());
		return texturePanel;
	}
	
	private void createControler() {
		TabPanel tab=new TabPanel();
		tab.addSelectionHandler(new SelectionHandler<Integer>() {
			@Override
			public void onSelection(SelectionEvent<Integer> event) {
				int index=event.getSelectedItem();
				onTabSelected(index);
			}
		});
		controlerRootPanel.add(tab);
		
		
		tab.add(createSpherePanel(), "spheres");
		tab.add(createHairDataPanel(),"hair");
		tab.add(createTexturePanel(),"texture");
		tab.add(createBasicPanel(),"basic");
		tab.add(createCharacterMovePanel(),"character");
		tab.add(new GravityPanel(clothControls),"gravity");
		 
		 tab.selectTab(2);
	}
	private Panel createCharacterMovePanel(){
		characterMovePanel=new CharacterMovePanel(characterMesh);
		return characterMovePanel;
	}
	
	protected void updateVertexVisible(boolean value) {
		vertexVisible=value;
		vertexHelper.setVisible(value);
		
		hairDataPanel.setVertexVisible(value);
	}

	public void setTextureMap(Texture texture){
		hairMaterial.setMap(texture);
		hairMaterial.setNeedsUpdate(true);
	}
	
	private void updateSphereVisible(boolean visible){
		for(SphereCalculatorAndMesh smesh:sphereMeshMap.values()){
			smesh.getMesh().setVisible(visible);
		}
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
				updateSphereVisible(event.getValue());
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


	




	

	
	

	

	
	
	
	
	 


	

	private StorageControler storageControler=new StorageControler();

	
	

	
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

	private void selectShpere(@Nullable SphereData selectedSphere) {
		if(selectedSphere==null){
			return;
		}
		MeshPhongMaterial material=sphereMeshMap.get(selectedSphere).getMesh().getMaterial().gwtCastMeshPhongMaterial();
		material.getColor().setHex(0x0000ff);
	}
	
	private void unselectShpere(SphereData selectedSphere) {
		MeshPhongMaterial material=sphereMeshMap.get(selectedSphere).getMesh().getMaterial().gwtCastMeshPhongMaterial();
		material.getColor().setHex(0x888888);
	}
	
	private AnimationMixer mixer;

	private SphereDataPanel sphereDataPanel;

	private Mesh groundMesh;
	
	
	//private int animationBoneIndex=60;
	
	

	
	public void startAnimation(int boneIndex,double x,double y,double z){
		if(mixer==null){
			return;
		}
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
		QuaternionKeyframeTrack track=THREE.QuaternionKeyframeTrack(".bones["+boneIndex+"].quaternion", times, values);
		
		tracks.push(track);
		
		AnimationClip clip=THREE.AnimationClip("anime", -1, tracks);
		//LogUtils.log(track.validate());
		
		mixer.stopAllAction();
		mixer.uncacheClip(clip);//same name cache that.
		mixer.clipAction(clip).play();
	}
	
	public void concat(JsArrayNumber target,JsArrayNumber values){
		for(int i=0;i<values.length();i++){
			target.push(values.get(i));
		}
	}
	
	public void stopAnimation() {
		if(mixer==null){
			return;
		}
		mixer.stopAllAction();
		
		//characterMesh.getGeometry().getBones().get(60).setRotq(q)
	}
}
