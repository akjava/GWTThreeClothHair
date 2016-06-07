package com.akjava.gwt.clothhair.client;

import javax.annotation.Nullable;

import com.akjava.gwt.clothhair.client.SkinningVertexCalculator.SkinningVertex;
import com.akjava.gwt.clothhair.client.cannon.CannonControler;
import com.akjava.gwt.clothhair.client.cloth.ClothControler;
import com.akjava.gwt.clothhair.client.cloth.ClothSimulator;
import com.akjava.gwt.clothhair.client.cloth.GroundYFloor;
import com.akjava.gwt.clothhair.client.cloth.SphereDataControler;
import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.akjava.gwt.clothhair.client.hair.HairDataPanel;
import com.akjava.gwt.clothhair.client.hair.HairDataPanel.HairMixedData;
import com.akjava.gwt.clothhair.client.hair.HairPinPanel;
import com.akjava.gwt.clothhair.client.sphere.SphereData;
import com.akjava.gwt.clothhair.client.sphere.SphereDataConverter;
import com.akjava.gwt.clothhair.client.sphere.SphereDataPanel;
import com.akjava.gwt.clothhair.client.texture.HairPatternDataEditor;
import com.akjava.gwt.clothhair.client.texture.HairPatternDataUtils;
import com.akjava.gwt.clothhair.client.texture.HairTextureData;
import com.akjava.gwt.clothhair.client.texture.HairTextureDataEditor;
import com.akjava.gwt.clothhair.client.texture.TexturePanel;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.GWTHTMLUtils;
import com.akjava.gwt.lib.client.ImageElementListener;
import com.akjava.gwt.lib.client.ImageElementLoader;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.JavaScriptUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.experimental.ImageDataUtils;
import com.akjava.gwt.lib.client.experimental.ImageDataUtils.RGBColorFilter;
import com.akjava.gwt.stats.client.Stats;
import com.akjava.gwt.three.client.examples.js.THREEExp;
import com.akjava.gwt.three.client.examples.js.controls.OrbitControls;
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.gwt.core.BoundingBox;
import com.akjava.gwt.three.client.gwt.core.Intersect;
import com.akjava.gwt.three.client.gwt.math.HSL;
import com.akjava.gwt.three.client.gwt.renderers.WebGLRendererParameter;
import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.akjava.gwt.three.client.java.utils.Mbl3dLoader;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.animation.AnimationClip;
import com.akjava.gwt.three.client.js.animation.AnimationMixer;
import com.akjava.gwt.three.client.js.animation.KeyframeTrack;
import com.akjava.gwt.three.client.js.animation.tracks.QuaternionKeyframeTrack;
import com.akjava.gwt.three.client.js.animation.tracks.VectorKeyframeTrack;
import com.akjava.gwt.three.client.js.cameras.PerspectiveCamera;
import com.akjava.gwt.three.client.js.core.BufferAttribute;
import com.akjava.gwt.three.client.js.core.BufferGeometry;
import com.akjava.gwt.three.client.js.core.Clock;
import com.akjava.gwt.three.client.js.core.Face3;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.core.Raycaster;
import com.akjava.gwt.three.client.js.extras.geometries.SphereGeometry;
import com.akjava.gwt.three.client.js.extras.helpers.SkeletonHelper;
import com.akjava.gwt.three.client.js.extras.helpers.VertexNormalsHelper;
import com.akjava.gwt.three.client.js.lights.AmbientLight;
import com.akjava.gwt.three.client.js.lights.DirectionalLight;
import com.akjava.gwt.three.client.js.loaders.ImageLoader.ImageLoadHandler;
import com.akjava.gwt.three.client.js.loaders.JSONLoader.JSONLoadHandler;
import com.akjava.gwt.three.client.js.materials.Material;
import com.akjava.gwt.three.client.js.materials.MeshPhongMaterial;
import com.akjava.gwt.three.client.js.materials.MultiMaterial;
import com.akjava.gwt.three.client.js.math.Color;
import com.akjava.gwt.three.client.js.math.Matrix3;
import com.akjava.gwt.three.client.js.math.Quaternion;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.objects.LineSegments;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;
import com.akjava.gwt.three.client.js.scenes.Scene;
import com.akjava.gwt.three.client.js.textures.Texture;
import com.akjava.lib.common.utils.CSVUtils;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d.Composite;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.core.client.JsArrayNumber;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.dom.client.KeyCodes;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
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


	
	double cameraY=1400;//TODO
	private OrbitControls controls;


	private SkinnedMesh characterMesh;
	private VertexNormalsHelper vertexHelper;
	
	private Stats stats;
	//private Mesh sphere;
	
	public static GWTThreeClothHair INSTANCE;
	@Override
	public WebGLRendererParameter createRendererParameter() {
		return GWTParamUtils.WebGLRenderer().preserveDrawingBuffer(false).logarithmicDepthBuffer(false);
	}
	
	Clock clock;
	private ClothSimulator clothSimulator;
	public ClothSimulator getClothSimulator() {
		return clothSimulator;
	}
	@Override
	public void animate(double timestamp) {
		
		double delta=clock.getDelta();
		
		
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
		
		
		
		
		
		
		//not support skinning
		if(vertexHelper!=null){
			vertexHelper.update();//for moving
		}
		
		
		
		
		if(skeltonHelper!=null){
			skeltonHelper.update();
		}
		
		if(clothSimulator!=null){
			clothSimulator.update(timestamp);
		}
		
		
		//logarithmicDepthBuffer
		renderer.render(scene, camera);//render last,very important
		
		//mixer first,this make animation
				if(mixer!=null){
					if(useFixedFrame){
						mixer.update(1.0/mixerFrixedFrameNumber);
					}else{
						mixer.update(delta);
					}
					 //fixed dt //TODO make option
					//mixer.update(clock.getDelta());
				}
		
		if(stats!=null){
			stats.update();
		}
		
	}
	
	private MeshPhongMaterial hairHeadMaterial;
	private SphereGeometry ballGeo;
	private Mesh tmpSphere;
	
	private SkeletonHelper skeltonHelper;

	private double GROUND=0;
	private double characterScale;
	
	/**
	 * @deprecated
	 * @param materials
	 * @return
	 */
	private JsArray<Material>  fixMaterial(JsArray<Material> materials){
		final MeshPhongMaterial eyeMaterial=THREE.MeshPhongMaterial(GWTParamUtils.MeshPhongMaterial()
				.morphTargets(true)
				.skinning(true)
				.transparent(true)
				.specular(0x111111).shininess(5)
				//.specular(1).shininess(1)
				.map(THREE.TextureLoader().load("models/mbl3d/tmp9.png"))//simpleface2
				);
		
		for(int i=0;i<materials.length();i++){
			MeshPhongMaterial m=materials.get(i).cast();//need cast GWT problem
			m.setMorphTargets(true);
			m.setSkinning(true);
			
			//update material
			if(m.getName().equals("White")){
				m.setColor(THREE.Color(0xf8f8f8));
				m.setSpecular(THREE.Color(0xffffff));//less shine
				m.setShininess(100);
			}else if(m.getName().equals("Blue")){//edge of mouth
				m.setColor(THREE.Color(0x007ebb));
				m.setSpecular(THREE.Color(0xffffff));
				m.setShininess(100);
			}else if(m.getName().equals("Pink01")){//mouth and inside
				m.setColor(THREE.Color(0xffa3ac));
				m.setSpecular(THREE.Color(0x888888));
				m.setShininess(50);
			}else if(m.getName().equals("Pink02") || m.getName().equals("Lip") || m.getName().equals("Face")){//face & lip
				m.setColor(THREE.Color(0xFFE4C6));
				m.setSpecular(THREE.Color(0x111111));
				m.setShininess(5);
				
				if(m.getName().equals("Pink02")){
					m.setMorphTargets(false);//no body morph so far.
				}
				
			}else if(m.getName().equals("gum")){//edge of mouth
				m.setColor(THREE.Color(0x7c4f53));
				m.setSpecular(THREE.Color(0x111111));
				m.setShininess(5);
			}else{//
				m.setSpecular(THREE.Color(0x111111));//less shine
				m.setShininess(5);
			}
			
			
			
		}
		//multi material is slow
		JsArray<Material> filterd=JavaScriptUtils.createJSArray();
		
		for(int i=0;i<materials.length();i++){
			//if exists
			if(materials.get(i).getName().equals("Eyes") || materials.get(i).getName().equals("Pink02")){//eye & tooth
				filterd.push(eyeMaterial);
				continue;
			}
			filterd.push(materials.get(i));
		}
		return filterd;
	}
	private MeshPhongMaterial bodyMaterial;
	public MeshPhongMaterial getBodyMaterial() {
		return bodyMaterial;
	}
	@Override
	public void onKeyDownEvent(KeyDownEvent event){
		if(event.getNativeKeyCode()==KeyCodes.KEY_SPACE){
			event.preventDefault();
			hairDataPanel.executeAddPoint();
		}
	}

	public String textureUrl;

	@Override
	public void onInitializedThree() {
		clock=THREE.Clock();
		//setDebugAnimateCount(100);
		final String modelUrl=GWTHTMLUtils.getInputValueById("model", "model11.json");
		textureUrl=GWTHTMLUtils.getInputValueById("texture", "models/mbl3d/simpleeye-2kbluexxx-extendhead.png");
		LogUtils.log("model-texture:"+modelUrl+","+textureUrl);
		
		super.onInitializedThree();
		setRightTopPopupWidth("360px");
		INSTANCE=this;
		
		renderer.setClearColor(0x888888);
		
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
		
		
		
		
		/*
		rendererContainer.addKeyDownHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				if(event.getNativeKeyCode()==KeyCodes.KEY_SPACE){
					event.preventDefault();
					hairDataPanel.executeAddPoint();
				}
			}
		});
		*/
		
		
		controls = THREEExp.OrbitControls(camera,rendererContainer.getElement() );
		controls.setTarget(THREE.Vector3( 0, cameraY, 0 ));
		controls.update();

		
		AmbientLight ambient = THREE.AmbientLight( 0xb7b7b7 );//var ambient = new THREE.AmbientLight( 0xffffff );
		scene.add( ambient );

		DirectionalLight directionalLight = THREE.DirectionalLight( 0x444444 );//var directionalLight = new THREE.DirectionalLight( 0x444444 );
		directionalLight.getPosition().set( -1, 1, 1 ).normalize();//directionalLight.position.set( -1, 1, 1 ).normalize();
		scene.add( directionalLight );
		
		DirectionalLight directionalLight2 = THREE.DirectionalLight( 0x444444 );//var directionalLight = new THREE.DirectionalLight( 0x444444 );
		directionalLight2.getPosition().set( 1, 1, -1 ).normalize();//directionalLight.position.set( -1, 1, 1 ).normalize();
		scene.add( directionalLight2 );
		
		DirectionalLight directionalLight3 = THREE.DirectionalLight( 0x444444 );//var directionalLight = new THREE.DirectionalLight( 0x444444 );
		directionalLight3.getPosition().set( 1, -1, 1 ).normalize();//directionalLight.position.set( -1, 1, 1 ).normalize();
		scene.add( directionalLight3 );
		
		
		
		MeshPhongMaterial groundMaterial = THREE.MeshPhongMaterial( GWTParamUtils.MeshPhongMaterial().color(0xaaaaaa).specular(0x111111)
				.transparent(true).opacity(1)
				.side(THREE.DoubleSide)
				);
		
		groundMesh = THREE.Mesh( THREE.PlaneBufferGeometry( 20000, 20000 ), groundMaterial );
		groundMesh.getPosition().setY(GROUND);//mesh.position.y = -250;
		groundMesh.getRotation().setX(- Math.PI / 2);//mesh.rotation.x = - Math.PI / 2;
		groundMesh.setReceiveShadow(true);//mesh.receiveShadow = true;
		scene.add( groundMesh );
		
		
		/*
		 * shape morph need special treatment for r74
		 */
		//String url= "models/mbl3d/model8o.json";//var url= "morph.json";
		
		//watch out tmp serieas not much type
		//String url= "models/mbl3d/model11-eyelid-extendhead.json"; //tmp9xxx3-front4
		
		//String url="models/mbl3d/model8-hair-color-expand-bone.json"; //no-morph over 40fps
		Mbl3dLoader loader=new Mbl3dLoader();
		loader.needFix=false;//for test
		loader.load(modelUrl,new JSONLoadHandler() {
			
			private MultiMaterial multiMaterials;
			
		
			

			@Override
			public void loaded(Geometry geometry,JsArray<Material> m) {
				
				//materials=fixMaterial(materials);
				characterGeometry=geometry;
				geometry.computeBoundingBox();
				BoundingBox bb = geometry.getBoundingBox();
				//double x=-20, y=-1270,z= -300,s= 800;

				double x=-0, y=-0,z= -0;
				characterScale = 1000;
				//y=-bb.getMax().getY()/2*s;
				y=bb.getMin().getY()*characterScale;
				
				
				/*
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
				//Multi-material is extreamly slow about 25 fps,
				multiMaterials = THREE.MultiMaterial(materials );
				*/
				
				Texture mapTexture=THREE.TextureLoader().load(textureUrl);//simpleeye2
				//default LinearMipMapLinearFilter use small mipmapping this approach contain some transparent area,
				//change nearest or paint all transparent area
				mapTexture.setMinFilter(THREE.NearestFilter);
				
				
				bodyMaterial = THREE.MeshPhongMaterial(GWTParamUtils.MeshPhongMaterial()
						.morphTargets(true)
						.skinning(true)
						.transparent(true)
						.alphaTest(0.5)
						//.emissiveMap(THREE.TextureLoader().load("models/mbl3d/emissive.png"))
						//.specularMap(THREE.TextureLoader().load("models/mbl3d/specular.png"))
						//.specular(0xffffff)//specular(0xffffff)// ff is for map ,11 is skin
						.specular(0x111111)
						//.emissive(0xffffff)
						.shininess(5)
						.side(THREE.DoubleSide)//for inside mouse
						//.specular(1).shininess(1)
						.map(mapTexture)
						
						//.bumpMap(THREE.TextureLoader().load("models/mbl3d/simpleeye-2kbluexxx-extendhead-bump.png"))
						.bumpScale(0.5)
						//.specular(0xffffff)
						//.specularMap(THREE.TextureLoader().load("models/mbl3d/simpleeye-2kbluexxx-extendhead-bump.png"))
						
						//.map(THREE.TextureLoader().load("models/mbl3d/simpleeye2.png"))
						);
				
				characterMesh = THREE.SkinnedMesh( geometry, bodyMaterial );
				LogUtils.log(characterMesh);
				//characterMesh = THREE.SkinnedMesh( geometry, multiMaterials );//mesh = THREE.SkinnedMesh( geometry, mat );//mesh = THREE.SkinnedMesh( geometry, mat );//mesh = new THREE.SkinnedMesh( geometry, mat );
				
				
				//for updebug
				skeltonHelper = THREE.SkeletonHelper( characterMesh );
				skeltonHelper.setVisible(false);
				scene.add( skeltonHelper );
				
				
				//ThreeLog.logBoneNames(characterMesh);
				
				//LogUtils.log(characterMesh);
				
				characterMesh.setName("model");//mesh.setName("model");//mesh.setName("model");//mesh.name = "model";
				//mesh.getPosition().set( x, y - bb.getMin().getY() * s, z );//mesh.getPosition().set( x, y - bb.getMin().y * s, z );//mesh.getPosition().set( x, y - bb.getMin().y * s, z );//mesh.position.set( x, y - bb.min.y * s, z );
				characterMesh.getPosition().set(x, y, z);
				characterMesh.getScale().set( characterScale, characterScale, characterScale );//mesh.getScale().set( s, s, s );//mesh.getScale().set( s, s, s );//mesh.scale.set( s, s, s );
				scene.add( characterMesh );
				
				
				vertexHelper = THREE.VertexNormalsHelper(characterMesh, 1.6, 0x008800, 2);
				scene.add(vertexHelper);
				//scene.add(THREE.VertexNormalsHelper(mesh, 0.2, 0x0000ff, 3));//can overwrite
			
				
				
				
				
				
				clothSimulator=new ClothSimulator(scene,characterMesh);
				
				clothSimulator.getClothControler().setFloorModifier(new GroundYFloor(GROUND));
				
				
				//sphere.getScale().setScalar(clothSimulator.getClothControler().getBallSize());
				
				
				
				characterMesh.getSkeleton().getBones().get(0).getPosition();
				
				
				createControler();
				
				//dont modify skeleton
				sphereDataPanel.setSkelton(characterMesh.getSkeleton());
				characterMovePanel.setSkelton(characterMesh.getSkeleton());
				
				clothSimulator.getClothControler().setWind(true);
				
				
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
				MeshPhongMaterial().color(globalHairColor).side(THREE.DoubleSide).specular(0xffffff).shininess(15)
				.alphaTest(0.9)//best for cloth
				.transparent(true).opacity(1)
				
				//.map(THREE.TextureLoader().load("models/mbl3d/hair1.png"))
				);
		
		
		
		
	}
	
	//List<SkinningVertexCalculator> skinningVertexCalculators=Lists.newArrayList();
	
//private CannonControler cannonControler;

	


	public CannonControler getCannonControler() {
	return clothSimulator.getCannonControler();
}

	
	
	
	
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
	public PerspectiveCamera createCamera(){
		
		PerspectiveCamera camera=THREE.PerspectiveCamera(45, getWindowInnerWidth()/getWindowInnerHeight(), 10, 20000);
		camera.getPosition().set(0, cameraY, 1000);
		return camera;
	}
	

	//ClothControler clothControls;

	//private int hairColor=0x553817;//brown
	
	public int defaultHairTextureColor=0xb7a9cd;//TODO move outside
	private    int globalHairColor=defaultHairTextureColor;
	
	
	public int getGlobalHairColor() {
		return globalHairColor;
	}

	public void setGlobalHairColor(int globalHairColor) {
		//LogUtils.log("setGlobalHairColor:"+globalHairColor);
		this.globalHairColor = globalHairColor;
	}

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
			//it's hard to re-select same faces
			
			/*
			for(int i=0;i<intersects.length();i++){
				LogUtils.log("face-at:"+intersects.get(i).getFaceIndex());
			}
			*/
			
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
			
			setSelectionVertex(faceIndex,vertexOfFaceIndex);
		}
		
	}
		
	
	public void unselectVertex(){
		hairDataPanel.setNewLine(null);
		hairDataPanel.setCurrentSelection(null);
		hairDataPanel.updateHairDataLine();
	}
		public void setSelectionVertex(int faceIndex,int vertexOfFaceIndex){
			Face3 face=characterMesh.getGeometry().getFaces().get(faceIndex);
			int vertexIndex=face.gwtGet(vertexOfFaceIndex);
			
			Vector3 selection=characterMesh.getGeometry().getVertices().get(vertexIndex);
			
			//make lines
			double size=48;
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
			
			
			
			
			
			
			LineSegments selectedLine=THREE.LineSegments(geometry.gwtCastGeometry(), THREE.LineBasicMaterial(GWTParamUtils.LineBasicMaterial().color(0xff0000).linewidth(1)));
			selectedLine.setVisible(vertexVisible);
			hairDataPanel.setNewLine(selectedLine);
			
	}
	
	public MeshPhongMaterial getHairMaterial(){
		return hairMaterial;
	}

	public ClothControler getClothControler(){
		return clothSimulator.getClothControler();
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
		
		if(index!=3 && index!=2){
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
		
		HorizontalPanel h1=new HorizontalPanel();
		basicPanel.add(h1);
		
		h1.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		basicPanel.add(new Label("Visible"));
		CheckBox windCheck=new CheckBox("Wind");
		windCheck.setValue(true);
		windCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				clothSimulator.getClothControler().setWind(event.getValue());
			}
		});
		h1.add(windCheck);
		
		
		CheckBox groundCheck=new CheckBox("Ground");
		
		groundCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				
				groundMesh.setVisible(event.getValue());
				clothSimulator.getClothControler().getFloorModifier().setEnabled(event.getValue());
			}
		});
		groundCheck.setValue(true);
		h1.add(groundCheck);
		groundCheck.setValue(false,true);
		
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
		CheckBox visibleVertexCheck=new CheckBox("Vertex");
		visibleVertexCheck.setValue(true);
		visibleVertexCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				updateVertexVisible(event.getValue());
			}
		});
		h1.add(visibleVertexCheck);
		
		
		Button test=new Button("test",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				final Canvas canvas=CanvasUtils.createCanvas(2048, 2048);
				THREE.ImageLoader().load(GWTThreeClothHair.INSTANCE.textureUrl, new ImageLoadHandler() {
					
					@Override
					public void onProgress(NativeEvent progress) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onLoad(ImageElement imageElement) {
						ImageData data=ImageDataUtils.create(canvas, imageElement);
						
						//ImageDataUtils.replaceColor(data,GWTThreeClothHair.INSTANCE.defaultHairTextureColor,colorValue);
						
						ImageDataUtils.executeRGBFilter(data, new RGBColorFilter() {
							@Override
							public int[] filterRGB(int r, int g, int b, int a) {
								// TODO Auto-generated method stub
								//TODO make range & rect
								return hslOnRGB(r,g,b,0.1,0.01,0.01);
							}
						
						});
						
						
						ImageDataUtils.putImageData(data, canvas);
						Texture texture=THREE.TextureLoader().load(canvas.toDataUrl());
						//texture.setFlipY(false);
						GWTThreeClothHair.INSTANCE.getBodyMaterial().setMap(texture);
						//extremly slow
						
					}
					
					@Override
					public void onError(NativeEvent error) {
						// TODO Auto-generated method stub
						
					}
				});
				
				
			}
			
			
			
		});
		//basicPanel.add(test);
		
		//cannon controling
		basicPanel.add(new HTML("<h4>Cannon</h4>"));
		HorizontalPanel h2=new HorizontalPanel();
		h2.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		CheckBox cannonStopCheck=new CheckBox("stop");
		
		cannonStopCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				GWTThreeClothHair.INSTANCE.getCannonControler().setStopped(event.getValue());
			}
			
		});
		h2.add(cannonStopCheck);
		Button step=new Button("step",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GWTThreeClothHair.INSTANCE.getCannonControler().getWorld().step(1.0/60);
			}
		});
		h2.add(step);
		basicPanel.add(h2);
		
		
		
		
		//mixer time
		
		return basicPanel;
	}
	
	private int mixerFrixedFrameNumber=60;
	public int getMixerFrixedFrameNumber() {
		return mixerFrixedFrameNumber;
	}
	public void setMixerFrixedFrameNumber(int mixerFrixedFrameNumber) {
		this.mixerFrixedFrameNumber = mixerFrixedFrameNumber;
	}

	private boolean useFixedFrame=true;
	
	public boolean isUseFixedFrame() {
		return useFixedFrame;
	}
	public void setUseFixedFrame(boolean useFixedFrame) {
		this.useFixedFrame = useFixedFrame;
	}

	/*
	 * these value for reduce instances time
	 */
	public static Color hslColor=THREE.Color(0);
	public static int[] rgbColor=new int[3];
	public static int[] hslOnRGB(int r,int g,int b,double addH,double addL,double addS){
		hslColor.setRGB(r/255.0, g/255.0, b/255.0);
		HSL hsl=hslColor.getHSL();
		double h=hsl.h()+addH;
		h%=1.0;
		if(h<0){
			h=1.0-h;
		}
		
		double l=hsl.l()+addL;
		l=Math.max( 0, Math.min( 1.0, l ) );
		
		double s=hsl.s()+addS;
		s=Math.max( 0, Math.min( 1.0, s ) );
		
		hslColor.setHSL(h, s, l);
		rgbColor[0]=(int) (hslColor.getR()*255);
		rgbColor[1]=(int) (hslColor.getG()*255);
		rgbColor[2]=(int) (hslColor.getB()*255);
		return rgbColor;
	}
	
	
	HairDataPanel hairDataPanel;
	private Panel createHairDataPanel(HairTextureDataEditor editor,HairPinPanel hairPinPanel){
		VerticalPanel panel=new VerticalPanel();
		HorizontalPanel buttons=new HorizontalPanel();
		
		Button showSphere=new Button("show/hide sphere",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				updateSphereVisible(!isSphereVisible());
			}
		});
		buttons.add(showSphere);
		panel.add(buttons);
		
		
		hairDataPanel= new HairDataPanel(characterMesh,editor,hairPinPanel);
		panel.add(hairDataPanel);
		
		return panel;
	}
	
	private Panel createTexturePanel(){
		texturePanel = new TexturePanel();
		
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
		
		tab.add(createTexturePanel(),"h-image");
		
		tab.add(createHairPinPanel(),"pin");
		
		//hair data panel make and initialize data inside,TODO load later
		tab.add(createHairDataPanel(texturePanel.getHairTextureDataEditor(),hairPinPanel),"hair");
		
		
		
		tab.add(createBasicPanel(),"basic");
		tab.add(createCharacterMovePanel(),"character");
		
		//no need anymore
		//tab.add(new GravityPanel(clothControls),"gravity");
		
		//link
		
		tab.selectTab(3);
	}
	private Widget createHairPinPanel() {
		hairPinPanel = new HairPinPanel();
		
		return hairPinPanel;
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
	
	private boolean isSphereVisible(){
		boolean visible=false;
		for(SphereCalculatorAndMesh smesh:clothSimulator.getSphereMeshMap().values()){
			visible=smesh.getMesh().isVisible();
		}
		return visible;
	}
	private void updateSphereVisible(boolean visible){
		if(visible){
			sphereDataPanel.updateSphereVisible();
		}else{
		for(SphereCalculatorAndMesh smesh:clothSimulator.getSphereMeshMap().values()){
			smesh.getMesh().setVisible(visible);
		}
		}
	}
	
	private Widget createSpherePanel() {
		VerticalPanel  panel=new VerticalPanel();
		
		double ballSize=.1;
		
		//double s=characterMesh.getScale().getX();
		//initial data
		//TODO find better way
		
		//TODO find better index by automatic
		
		//TODO relative
		SphereData firstOne=new SphereData(0, 1.2, 0, ballSize, true,60);//hard code 60 is head
		//addSphereData(firstOne);
		
		HorizontalPanel controler=new HorizontalPanel();
		panel.add(controler);
		
	
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
	
	/**
	 * @deprecated remove soon
	 */
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
		MeshPhongMaterial material=clothSimulator.getSphereMeshMap().get(selectedSphere).getMesh().getMaterial().gwtCastMeshPhongMaterial();
		material.getColor().setHex(0x0000ff);
	}
	
	private void unselectShpere(SphereData selectedSphere) {
		MeshPhongMaterial material=clothSimulator.getSphereMeshMap().get(selectedSphere).getMesh().getMaterial().gwtCastMeshPhongMaterial();
		material.getColor().setHex(0x888888);
	}
	
	private AnimationMixer mixer;

	private SphereDataPanel sphereDataPanel;

	private Mesh groundMesh;
	
	
	//private int animationBoneIndex=60;
	
	
	private Canvas canvas=CanvasUtils.createCanvas(512, 512);
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
		canvas=HairPatternDataEditor.canvas;
		
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
						// TODO Auto-generated method stub
						
					}
				});
			}
			
			@Override
			public void onError(String url, ErrorEvent event) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
		
		
	}
	private Geometry characterGeometry;
	
	public void updateHairTextureData(boolean updateHairTextureMap){
		//get hair data selection
		HairMixedData selection=hairDataPanel.getSelection();
		updateHairTextureData(selection,updateHairTextureMap);
		
	}
	
	public void updateHairTextureMap(Texture texture){
		//get hair data selection
		for(HairMixedData selection:hairDataPanel.getDatas()){
			updateHairTextureMap(selection,texture);
		}
	}
	
	
	public void updateHairTextureMap(HairMixedData selection,Texture texture){
		if(selection==null){
			LogUtils.log("updateHairTextureData:null selection");
			return;
		}
		//store hair datas 
		//sync textures
		MeshPhongMaterial material=selection.getMesh().getMaterial().gwtCastMeshPhongMaterial();
		HairTextureData textureData=selection.getHairData().getHairTextureData();
		//TODO support local or global
		material.setMap(texture);
	}
	
	public void updateHairTextureColor(HairMixedData selection,int colorValue){
		if(selection==null){
			LogUtils.log("updateHairTextureData:null selection");
			return;
		}
		//store hair datas 
		//sync textures
		MeshPhongMaterial material=selection.getMesh().getMaterial().gwtCastMeshPhongMaterial();
		HairTextureData textureData=selection.getHairData().getHairTextureData();
		//TODO support local or global
		material.setColor(THREE.Color(colorValue));
	}
		
		public void updateHairTextureData(HairMixedData selection,boolean updateHairTextureMap){
		if(selection==null){
			LogUtils.log("updateHairTextureData:null selection");
			return;
		}
		//store hair datas 
		if(hairDataPanel!=null){ //called from init hairDataPanel
			//LogUtils.log("updateHairTextureData-called");
			hairDataPanel.storeDatas();
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
	

		public void playAnimation(AnimationClip clip,boolean facialAnimation) {
			mixer.stopAllAction();
			mixer.uncacheClip(clip);//reset can cache
			mixer.clipAction(clip).play();
			initMorph();
			if(facialAnimation && facialAnimationClip!=null){
				mixer.uncacheClip(facialAnimationClip);//same name cache that.
				mixer.clipAction(facialAnimationClip).play();
			}
		}
	
		public void startAnimation(int boneIndex,double x,double y,double z){
			startAnimation(boneIndex,x,y,z,true,true);
		}
		
		public void resetAnimation(){
			if(mixer==null){
				return;
			}
			JsArray<KeyframeTrack> tracks=JavaScriptObject.createArray().cast();
			Quaternion q=THREE.Quaternion();
			for(int i=0;i<characterMesh.getSkeleton().getBones().length();i++){
				JsArrayNumber times=JavaScriptObject.createArray().cast();
				times.push(0);
				
				JsArrayNumber values=JsArray.createArray().cast();
				concat(values,q.toArray());
				QuaternionKeyframeTrack track=THREE.QuaternionKeyframeTrack(".bones["+i+"].quaternion", times, values);
				tracks.push(track);
				
				
				
				//TODO  make method
				JsArrayNumber times2=JavaScriptObject.createArray().cast();
				times2.push(0);
				
				JsArrayNumber values2=JsArray.createArray().cast();
				concat(values2,characterGeometry.getBones().get(i).getPos());
				VectorKeyframeTrack track2=THREE.VectorKeyframeTrack(".bones["+i+"].position", times2, values2);
				tracks.push(track2);
			}
			AnimationClip clip=THREE.AnimationClip("reset", -1, tracks);
			//LogUtils.log(track.validate());
			
			mixer.stopAllAction();
			mixer.uncacheClip(clip);//reset can cache?
			mixer.clipAction(clip).play();
			
			initMorph();
		}
			
	public void startAnimation(int boneIndex,double x,double y,double z,boolean both,boolean facialAnimation){
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
		if(both){
		times.push(duration*3);
		times.push(duration*4);
		}
		JsArrayNumber values=JsArray.createArray().cast();
		
		
		
		concat(values,THREE.Quaternion().toArray());
		concat(values,q.toArray());
		concat(values,THREE.Quaternion().toArray());
		if(both){
		concat(values,q2.toArray());
		concat(values,THREE.Quaternion().toArray());
		}
		
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
		
		initMorph();
		if(facialAnimation && facialAnimationClip!=null){
			
			
			mixer.uncacheClip(facialAnimationClip);//same name cache that.
			mixer.clipAction(facialAnimationClip).play();
		}
	}
	
	//TODO make method
	private void initMorph(){
		if(characterMesh.getMorphTargetInfluences()==null){
			return;
		}
		for (int i = 0; i < characterMesh.getMorphTargetInfluences().length(); i++) {
			characterMesh.getMorphTargetInfluences().set(i, 0);
		}
	}
	
	
	public void concat(JsArrayNumber target,JsArrayNumber values){
		for(int i=0;i<values.length();i++){
			target.push(values.get(i));
		}
	}
	
	private AnimationClip facialAnimationClip;
	private TexturePanel texturePanel;
	private HairPinPanel hairPinPanel;
	
	public void setFacialAnimationClip(AnimationClip facialAnimationClip) {
		this.facialAnimationClip = facialAnimationClip;
	}

	public void stopAnimation() {
		if(mixer==null){
			return;
		}
		mixer.stopAllAction();
		
		//characterMesh.getGeometry().getBones().get(60).setRotq(q)
	}
	
	public void addHairPin(HairPin pin){
		hairDataPanel.addPin(pin);
	}
	@Override
	public void removeSphereData(SphereData data) {
		clothSimulator.removeSphereData(data);
	}
	@Override
	public void addSphereData(SphereData data) {
		clothSimulator.addSphereData(data);
	}
}
