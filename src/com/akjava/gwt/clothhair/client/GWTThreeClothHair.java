package com.akjava.gwt.clothhair.client;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import com.akjava.gwt.clothhair.client.HairData.HairPin;
import com.akjava.gwt.clothhair.client.cloth.ClothControls;
import com.akjava.gwt.clothhair.client.cloth.ClothData;
import com.akjava.gwt.clothhair.client.texture.TexturePanel;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.three.client.examples.js.THREEExp;
import com.akjava.gwt.three.client.examples.js.controls.OrbitControls;
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.gwt.core.BoundingBox;
import com.akjava.gwt.three.client.gwt.core.Intersect;
import com.akjava.gwt.three.client.java.ThreeLog;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.cameras.PerspectiveCamera;
import com.akjava.gwt.three.client.js.core.BufferAttribute;
import com.akjava.gwt.three.client.js.core.BufferGeometry;
import com.akjava.gwt.three.client.js.core.Face3;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.core.Raycaster;
import com.akjava.gwt.three.client.js.extras.geometries.SphereGeometry;
import com.akjava.gwt.three.client.js.extras.helpers.VertexNormalsHelper;
import com.akjava.gwt.three.client.js.lights.AmbientLight;
import com.akjava.gwt.three.client.js.lights.DirectionalLight;
import com.akjava.gwt.three.client.js.loaders.JSONLoader.JSONLoadHandler;
import com.akjava.gwt.three.client.js.materials.Material;
import com.akjava.gwt.three.client.js.materials.MeshPhongMaterial;
import com.akjava.gwt.three.client.js.materials.MultiMaterial;
import com.akjava.gwt.three.client.js.math.Matrix3;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.objects.LineSegments;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.MouseDownEvent;
import com.google.gwt.event.dom.client.MouseDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;


/**
 * Entry point classes define <code>onModuleLoad()</code>.
 */
public class GWTThreeClothHair  extends HalfSizeThreeAppWithControler{

	double cameraY=700;
	private OrbitControls controls;


	private SkinnedMesh mesh;
	private VertexNormalsHelper vertexHelper;
	private Mesh sphere;
	
	
	@Override
	public void animate(double timestamp) {
		if(clothControls!=null){
			clothControls.update(timestamp);
		}
		
		renderer.render(scene, camera);//render last,very important
	}
	@Override
	public void onInitializedThree() {
		super.onInitializedThree();
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
		
		LogUtils.log(camera);
		controls = THREEExp.OrbitControls(camera,rendererContainer.getElement() );
		controls.setTarget(THREE.Vector3( 0, cameraY, 0 ));
		controls.update();

		
		AmbientLight ambient = THREE.AmbientLight( 0xeeeeee );//var ambient = new THREE.AmbientLight( 0xffffff );
		scene.add( ambient );

		DirectionalLight directionalLight = THREE.DirectionalLight( 0x333333 );//var directionalLight = new THREE.DirectionalLight( 0x444444 );
		directionalLight.getPosition().set( -1, 1, 1 ).normalize();//directionalLight.position.set( -1, 1, 1 ).normalize();
		scene.add( directionalLight );
		
		
		String url= "models/mbl3d/model8-hair-color-expand.json";//var url= "morph.json";
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
						m.setColor(THREE.Color(hairColor));
						m.setSpecular(THREE.Color(0xffffff));//less shine
						m.setShininess(15);
					}else{//
						m.setSpecular(THREE.Color(0x111111));//less shine
						m.setShininess(5);
					}
					
					
					
				}
				
				
				
				
				MultiMaterial mat=THREE.MultiMaterial(materials );//var mat=THREE.MultiMaterial( materials);//MultiMaterial mat=THREE.MultiMaterial( materials);//var mat=new THREE.MultiMaterial( materials);


				mesh = THREE.SkinnedMesh( geometry, mat );//mesh = THREE.SkinnedMesh( geometry, mat );//mesh = THREE.SkinnedMesh( geometry, mat );//mesh = new THREE.SkinnedMesh( geometry, mat );
				mesh.setName("model");//mesh.setName("model");//mesh.setName("model");//mesh.name = "model";
				//mesh.getPosition().set( x, y - bb.getMin().getY() * s, z );//mesh.getPosition().set( x, y - bb.getMin().y * s, z );//mesh.getPosition().set( x, y - bb.getMin().y * s, z );//mesh.position.set( x, y - bb.min.y * s, z );
				mesh.getPosition().set(x, y, z);
				mesh.getScale().set( s, s, s );//mesh.getScale().set( s, s, s );//mesh.getScale().set( s, s, s );//mesh.scale.set( s, s, s );
				scene.add( mesh );
				
				
				vertexHelper = THREE.VertexNormalsHelper(mesh, 1.6, 0x008800, 2);
				scene.add(vertexHelper);
				//scene.add(THREE.VertexNormalsHelper(mesh, 0.2, 0x0000ff, 3));//can overwrite
			
				
				SphereGeometry ballGeo = THREE.SphereGeometry( 1, 20, 20 );//var ballGeo = new THREE.SphereGeometry( ballSize, 20, 20 );
				
				MeshPhongMaterial ballMaterial = THREE.MeshPhongMaterial( GWTParamUtils.MeshPhongMaterial().color(0x888888).side(THREE.DoubleSide).wireframe(true));//		var ballMaterial = new THREE.MeshPhongMaterial( { color: 0xffffff } );

				sphere = THREE.Mesh( ballGeo, ballMaterial );//		sphere = new THREE.Mesh( ballGeo, ballMaterial );
				scene.add( sphere );
				
				clothControls=new ClothControls(sphere);
				
				sphere.getScale().setScalar(clothControls.getBallSize());
				
				sphere.getPosition().setY(bb.getMax().getY()/2*s-100);
				
				
				
				
				createControler();
				
				
				
				clothControls.setWind(true);
				
			}
			
			
			
			
			
			
			
			
		});
		
		hairMaterial = THREE.MeshPhongMaterial(GWTParamUtils.
				MeshPhongMaterial().color(hairColor).side(THREE.DoubleSide).specular(0xffffff).shininess(15).transparent(true).opacity(1)
				
				//.map(THREE.TextureLoader().load("models/mbl3d/hair1.png"))
				);
		
		
	}
	@Override
	public PerspectiveCamera createCamera(){
		
		PerspectiveCamera camera=THREE.PerspectiveCamera(45, getWindowInnerWidth()/getWindowInnerHeight(), 10, 10000);
		camera.getPosition().set(0, cameraY, 500);
		return camera;
	}
	

	ClothControls clothControls;

	private int hairColor=0x553817;

	
	protected Vector3 matrixedPoint(Vector3 vec){
		return vec.clone().applyMatrix4(mesh.getMatrixWorld());
	}
	
	
	
	public Vector3 hairPinToVertex(Mesh mesh,HairPin hairPin,boolean applyMatrix4){
		checkNotNull(mesh,"hairPinToVertex:mesh is null");
		checkNotNull(hairPin,"hairPinToVertex:hairPin is null");
		checkArgument(mesh.getGeometry().getFaces().length()>hairPin.getFaceIndex(),"hairPinToVertex:invalid face length");
		Face3 face=mesh.getGeometry().getFaces().get(hairPin.getFaceIndex());
		Vector3 vertex;
		if(hairPin.getVertexOfFaceIndex()==0){
			vertex=mesh.getGeometry().getVertices().get(face.getA());
		}else if(hairPin.getVertexOfFaceIndex()==0){
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
	
		JsArray<Intersect> intersects=ray.intersectObject(mesh);
		
		//find nearlist vertex
		
		if(intersects.length()>0){
			//LogUtils.log("intersects:"+intersects.length());
			
			Face3 face=intersects.get(0).getFace();
			Vector3 point=intersects.get(0).getPoint();
			int faceIndex=intersects.get(0).getFaceIndex();
			
			
			//ThreeLog.log("point:",point);
			
			int vertexOfFaceIndex=0;
			Vector3 selection=mesh.getGeometry().getVertices().get(face.getA());
		
			//ThreeLog.log("vertex1:",mesh.getGeometry().getVertices().get(face.getA()));
			double distance=point.distanceTo(matrixedPoint(mesh.getGeometry().getVertices().get(face.getA())));
			
			//ThreeLog.log("vertex2:",mesh.getGeometry().getVertices().get(face.getB()));
			double distance2=point.distanceTo(matrixedPoint(mesh.getGeometry().getVertices().get(face.getB())));
			if(distance2<distance){
				vertexOfFaceIndex=1;
				distance=distance2;
				selection=mesh.getGeometry().getVertices().get(face.getB());
			}
			//ThreeLog.log("vertex3:",mesh.getGeometry().getVertices().get(face.getC()));
			
			double distance3=point.distanceTo(matrixedPoint(mesh.getGeometry().getVertices().get(face.getC())));
			if(distance3<distance){
				vertexOfFaceIndex=2;
				distance=distance3;
				selection=mesh.getGeometry().getVertices().get(face.getC());
			}
			
			//make lines
			double size=3.2;
			Matrix3 normalMatrix=THREE.Matrix3();
			Vector3 v1 = THREE.Vector3();
			Vector3 v2 = THREE.Vector3();
			
			normalMatrix.getNormalMatrix( mesh.getMatrixWorld());
			
			Vector3 normal = face.getVertexNormals().get(vertexOfFaceIndex);

			v1.copy( selection ).applyMatrix4( mesh.getMatrixWorld() );

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
	
	
	
	private StorageControler storageControler=new StorageControler();
	/**
	 * must call after sphere initialized;
	 */
	private void createControler() {
		//vertex
		controlerRootPanel.add(new Label("Vertex"));
		HorizontalPanel h0=new HorizontalPanel();
		CheckBox visibleVertexCheck=new CheckBox("visible");
		visibleVertexCheck.setValue(true);
		visibleVertexCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				vertexHelper.setVisible(event.getValue());
			}
		});
		h0.add(visibleVertexCheck);
		controlerRootPanel.add(h0);
		
		
		SphereInfoPanel sphereInfoPanel=new SphereInfoPanel(storageControler,sphere,clothControls);
		
		controlerRootPanel.add(sphereInfoPanel);
		
		createClothPanel();
		
		//texture panel;
		controlerRootPanel.add(new Label("Texture"));
		controlerRootPanel.add(new TexturePanel(hairMaterial));
	}
	
	
	
	
	private HairPin currentSelection;
	private HairPin firstSelection;
	private HairPin secondSelection;
	
	private void createClothPanel(){
		controlerRootPanel.add(new Label("Cloth"));
		//tmp
		HorizontalPanel h=new HorizontalPanel();
		controlerRootPanel.add(h);
		Button first=new Button("first",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				firstSelection=currentSelection;
			}
		});
		h.add(first);
		
		
		Button second=new Button("second",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				secondSelection=currentSelection;
			}
		});
		h.add(second);
		
		Button addCloth=new Button("add cloth",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addCloth();
			}
		});
		h.add(addCloth);
	}

	protected void addCloth() {
		if(firstSelection==null || secondSelection==null){
			LogUtils.log("need first & second");
			return;
		}
		
		Vector3 v1=hairPinToVertex(mesh,firstSelection,true);
		Vector3 v2=hairPinToVertex(mesh,secondSelection,true);
		
		double distance=v1.distanceTo(v2);
		
		if(distance==0){
			LogUtils.log("invalidly first & second same:"+ThreeLog.get(v1)+","+ThreeLog.get(v1));
			return;
		}
		
		LogUtils.log("distance:"+distance);
		int cw=8;
		ClothData data=new ClothData(cw, 8, distance, distance);
		clothControls.addClothData(data);
		
		data.getCloth().setPinAll();
		
		data.getCloth().ballSize=clothControls.getBallSize();
		
	
		
		Mesh object = THREE.Mesh( data.getClothGeometry(), hairMaterial );
		//object.getPosition().set( 0, 0, 0 );
		
		scene.add( object );
		
		data.getCloth().particles.get(0).getOriginal().copy(v1);
		data.getCloth().particles.get(cw).getOriginal().copy(v2);
		
		Vector3 sub=v2.clone().sub(v1).divideScalar(cw+1);
		for(int i=1;i<cw;i++){
			Vector3 v=sub.clone().multiplyScalar(i).add(v1);
			data.getCloth().particles.get(i).getOriginal().copy(v);
		}
		
		for(int i=cw+1;i<data.getCloth().particles.size();i++){
			data.getCloth().particles.get(i).getOriginal().copy(v1);
		}
	}

	
	

	
	int mouseX,mouseY;
	private MeshPhongMaterial hairMaterial;
	
	protected void onDocumentMouseMove(MouseMoveEvent event) {
		mouseX = ( event.getClientX() - windowHalfX );
		mouseY = ( event.getClientY() - windowHalfY )*2;
	}
}
