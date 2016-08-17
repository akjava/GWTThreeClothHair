package com.akjava.gwt.clothhair.client.cloth;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.clothhair.client.GWTThreeClothHair;
import com.akjava.gwt.clothhair.client.ammo.AmmoHairControler;
import com.akjava.gwt.clothhair.client.hair.HairData;
import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.akjava.gwt.clothhair.client.hair.HairDataUtils;
import com.akjava.gwt.clothhair.client.sphere.JsSphereData;
import com.akjava.gwt.clothhair.client.sphere.SphereData;
import com.akjava.gwt.lib.client.JavaScriptUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.gwt.boneanimation.AnimationBone;
import com.akjava.gwt.three.client.java.bone.SimpleAutoWeight;
import com.akjava.gwt.three.client.java.bone.WeightResult;
import com.akjava.gwt.three.client.java.geometry.PointsToGeometry;
import com.akjava.gwt.three.client.java.utils.GWTThreeUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Face3;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.extras.helpers.SkeletonHelper;
import com.akjava.gwt.three.client.js.loaders.JSONLoader.JSONLoadHandler;
import com.akjava.gwt.three.client.js.loaders.XHRLoader.XHRLoadHandler;
import com.akjava.gwt.three.client.js.materials.Material;
import com.akjava.gwt.three.client.js.materials.MeshPhongMaterial;
import com.akjava.gwt.three.client.js.math.Quaternion;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;
import com.akjava.gwt.threeammo.client.AmmoBodyPropertyData;
import com.akjava.gwt.threeammo.client.AmmoConstraintPropertyData;
import com.akjava.gwt.threeammo.client.AmmoControler;
import com.akjava.gwt.threeammo.client.AmmoUtils;
import com.akjava.gwt.threeammo.client.BodyAndMesh;
import com.akjava.gwt.threeammo.client.ConstraintAndLine;
import com.akjava.gwt.threeammo.client.bones.PlainBoneCreator;
import com.akjava.gwt.threeammo.client.core.Ammo;
import com.akjava.gwt.threeammo.client.core.btRigidBody;
import com.akjava.gwt.threeammo.client.core.btTransform;
import com.akjava.gwt.threeammo.client.core.btVector3;
import com.akjava.gwt.threeammo.client.core.constraints.btGeneric6DofSpringConstraint;
import com.akjava.gwt.threeammo.client.functions.BodyAndMeshFunctions;
import com.akjava.gwt.threeammo.client.functions.BodyAndMeshFunctions.CloneDivided;
import com.akjava.lib.common.utils.FileNames;
import com.google.common.base.Stopwatch;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;


/**
 * Contain HairData
 * @author aki
 *
 */
public class HairCloth {
	/*
	 * Cloth Simulation using a relaxed constrains solver
	 */

	// Suggested Readings

	// Advanced Character Physics by Thomas Jakobsen Character
	// http://freespace.virgin.net/hugo.elias/models/m_cloth.htm
	// http://en.wikipedia.org/wiki/Cloth_modeling
	// http://cg.alexandra.dk/tag/spring-mass-system/
	// Real-time Cloth Animation http://www.darwin3d.com/gamedev/articles/col0599.pdf

	
	public int getChannel(){
		return hairData.getChannel();
	}
	
	/**
	 * if ammo cloth use ammoMultipleScalar is 1  better
	 * @return
	 */
	public boolean isSyncMove(){
		return hairData.isSyncMove();
	}
	
	/**
	 * 
	 * @param DAMPING 0.03
	 * @param MASS .1
	 * @param GRAVITY 981 * 1.4
	 */
	
	/*
	 * somehow it's break ,shoudl re-add
	 */
	public void initGravity(double MASS,double DAMPING,double GRAVITY){
		this.PLAIN_CLOTH_DAMPING=DAMPING;
		this.DRAG = 1.0 - DAMPING;
		this.PLAIN_CLOTH_MASS=MASS;
		this.PLAIN_CLOTH_GRAVITY_BASE=GRAVITY;
		plain_cloth_gravity = THREE.Vector3( 0, -GRAVITY, 0 ).multiplyScalar(MASS);
	}
	public void initGravities(double MASS,double DAMPING){
		this.PLAIN_CLOTH_DAMPING=DAMPING;
		this.DRAG = 1.0 - DAMPING;
		this.PLAIN_CLOTH_MASS=MASS;
		plain_cloth_gravity = THREE.Vector3( 0, -PLAIN_CLOTH_GRAVITY_BASE, 0 ).multiplyScalar(MASS);
	}
	
	/**
	 * TODO add format
	 * on circle mode(when point has only two point,start first or second)
	 */
	
	
	public boolean isStartCircleCenter() {
		return hairData.isAmmoStartCenterCircle();
	}
	

	//i'm not sure any advantage when true,maybe no need,there were bug of circling
	private boolean startAndEndSameCircle=false;


	public boolean isStartAndEndSameCircle() {
		return startAndEndSameCircle;
	}
	public void setStartAndEndSameCircle(boolean startAndEndSameCircle) {
		this.startAndEndSameCircle = startAndEndSameCircle;
	}

	double PLAIN_CLOTH_DAMPING = 0.03;
	double DRAG = 1.0 - PLAIN_CLOTH_DAMPING;
	double PLAIN_CLOTH_MASS = .1;
	double restDistance = 25;


	/*
	 * TODO fix when circle mode
	 */
	public double getRestDistance() {
		return restDistance;
	}


	//private static int xSegs = 10; //
	//private static int ySegs = 10; //

	//must be javascript function ParametricGeometry use this function
	JavaScriptObject clothFunction;

	//var cloth = new Cloth(xSegs, ySegs);

	double PLAIN_CLOTH_GRAVITY_BASE = 981 * 1.4; // 
	Vector3 plain_cloth_gravity = THREE.Vector3( 0, -PLAIN_CLOTH_GRAVITY_BASE, 0 ).multiplyScalar(PLAIN_CLOTH_MASS);


	//double TIMESTEP = 9.0 / 1000;
	double PLAIN_CLOTH_TIMESTEP = 18.0 / 1000;//i'm not sure what is this
	
	double TIMESTEP_SQ = PLAIN_CLOTH_TIMESTEP * PLAIN_CLOTH_TIMESTEP;

	/*
	 * created hairPin(excluded having target hairPin)-1 * sizeOfU:-1 means last pin has no interpolate
	 * Pin(static) particle index of particles
	 */
	private int[] pins;


	public int[] getPins() {
		return pins;
	}
	
	
	public void setPins(int[] pins) {
		this.pins = pins;
	}

	//TODO support ammo
	boolean wind = true;
	double windStrength = 2;
	Vector3 windForce = THREE.Vector3(0,0,0);

	//Vector3 ballPosition = THREE.Vector3(0, -45, 0);
	
	//public double ballSize = 60; //40

	Vector3 tmpForce = THREE.Vector3();

	Double lastTime;
	
	//for ParametricGeometry
	public native final JavaScriptObject plane(double width,double height)/*-{
	return function(u, v) {
			var x = (u-0.5) * width;
			var y = (v+0.5) * height;
			var z = 0;

			return new $wnd.THREE.Vector3(x, y, z);
		};
	}-*/;
	
	/*}
	public  ClothPartiicle createParticle(double x,double y,double z,double mass){
		ClothPartiicle particle=ClothPartiicle.createObject().cast();
		particle.setPosition(doClothFunction(x,y));
		return particle;
	}
	*/
	//horible implements
	public native final Vector3 doClothFunction(JavaScriptObject clothFunction,double x,double y)/*-{
	return clothFunction(x,y);
	}-*/;
	
	public class Constrain{
		private Particle p1;
		private Particle p2;
		private double distance;
		public Particle getP1() {
			return p1;
		}
		public void setP1(Particle p1) {
			this.p1 = p1;
		}
		public Particle getP2() {
			return p2;
		}
		public void setP2(Particle p2) {
			this.p2 = p2;
		}
		public double getDistance() {
			return distance;
		}
		public void setDistance(double distance) {
			this.distance = distance;
		}
		public Constrain(Particle p1, Particle p2, double distance) {
			super();
			this.p1 = p1;
			this.p2 = p2;
			this.distance = distance;
		}
	}
	public class Particle{
		Vector3 position;
		public Vector3 getPosition() {
			return position;
		}

		Vector3 previous;
		private Vector3 original;
		//for after modify
		public Vector3 getOriginal() {
			return original;
		}

		private Vector3 a;
		private double mass;
		public double getMass() {
			return mass;
		}

		private double invMass;
		private Vector3 tmp;
		private Vector3 tmp2;
		public Particle(double x,double y,double z,double mass){
			position=doClothFunction(clothFunction,x, y);
			previous=doClothFunction(clothFunction,x, y);
			original=doClothFunction(clothFunction,x, y);
			a=THREE.Vector3(0, 0, 0);
			this.mass=mass;
			this.invMass=1.0 / mass;
			tmp=THREE.Vector3(0, 0, 0);
			tmp2=THREE.Vector3(0, 0, 0);
		}
		
		public void setAllPosition(Vector3 vec){
			position.copy(vec);
			previous.copy(vec);
			original.copy(vec);
		}
		
		public void addForce(Vector3 force){
			this.a.add(
					this.tmp2.copy(force).multiplyScalar(this.invMass)
					);
		}
		
		public void integrate(double timesq){
			Vector3 newPos = this.tmp.subVectors(this.position, this.previous);
			newPos.multiplyScalar(DRAG).add(this.position);
			newPos.add(this.a.multiplyScalar(timesq));

			this.tmp = this.previous;
			this.previous = this.position;
			this.position = newPos;

			this.a.set(0, 0, 0);
		}
	}
	Vector3 diff = THREE.Vector3();

	public void satisifyConstrains(Particle p1,Particle p2,double distance) {
		diff.subVectors(p2.position, p1.position);
		double currentDist = diff.length();
		if (currentDist==0) return; // prevents division by 0
		Vector3 correction = diff.multiplyScalar(1 - distance/currentDist);
		Vector3 correctionHalf = correction.multiplyScalar(0.5);
		p1.position.add(correctionHalf);
		p2.position.sub(correctionHalf);
	}
	
	/**
	 * (normalPin.size()-1)*hairData.getSizeOfU()
	 */
	private int w;
	public int getW() {
		return w;
	}

	public int getSliceFaceCount(){
		return hairData.getSliceFaceCount();
	}

	/**
	 * right now same of sizeOfV
	 */
	int h;
	
	
	private List<Constrain> constrains=new ArrayList<Constrain>();
	//TODO convert to js array,however particle is no js object
	public List<Particle> particles=new ArrayList<Particle>();
	
	/*
	public Cloth2(){
		this(xSegs,ySegs);
	}
	*/
	
	public List<Particle> getParticles() {
		return particles;
	}


	List<int[]> pinsFormation=new ArrayList<int[]>();
	
	
	public void initPins(){//this is initialized based on Cloth's xSegs TODO link
		int center=w/2+1;//horizontal-center
		
		//0 center only
		pins= new int[]{center};
		pinsFormation.add( pins );

		//default all first line 
		pins = new int[w+1];
		for(int i=0;i<=w;i++){
			pins[i]=i;
		}
		pinsFormation.add( pins );

		pins = new int[]{ 0 };
		pinsFormation.add( pins );

		pins = new int[0]; // cut the rope ;)
		pinsFormation.add( pins );

		pins = new int[]{ 0, w }; // classic 2 pins
		pinsFormation.add( pins );

		pins = pinsFormation.get(1);	
	}
	
	public void setPinAll(){
		pins=pinsFormation.get(1);
	}
	public void setPinTwo(){
		pins=pinsFormation.get(4);
	}
	
	public boolean isPinned(int index){
		for(int i=0;i<pins.length;i++){
			if(pins[i]==index){
				return true;
			}
		}
		return false;
	}
	public int getVerticaPosition(int index){
		return index/(w+1);
	}

	
	//compatible
	/*
	public Cloth2(int w,int h){
		this(w,h,25 * xSegs, 25 * ySegs);
	}
	*/
	
	//private boolean connectHorizontal=false;
	
	
	public boolean isConnectHorizontal() {
		return hairData.isConnectHorizontal();
	}

	public static int calcurateParticleSize(List<HairPin> pins,int sizeOfU,int sizeOfH){
		return calcurateWSize(pins,sizeOfU)*(sizeOfH+1);
	}
	public static int calcurateWSize(List<HairPin> pins,int sizeOfU){
		int normalPin=0;
		for(HairPin pin:pins){
			if(!pin.isCustomPin()){
				normalPin++;
			}
		}
		int w=(normalPin-1)*sizeOfU+1;
		return w;
	}
	
	//private int sizeOfU;
	
	//private boolean cutHorizontalConnection;
	//private int startCutHorizontalConnection;
	
	public static int calcurateHorizontalPin(int normalPinSize,int sizeOfU){
		return (normalPinSize-1)*sizeOfU+1;
	}
	
	//ignore 0 index
	public boolean needConnectHorizontal(int v){
		return (!hairData.isCutU() || v<hairData.getStartCutUIndexV());
	}
	
	//private double ammoThick;
	
	public boolean isCircleStyle(){
		return hairData.isCircleStyle();
	}
	
	//private int hairPhysicsType;
	private HairData hairData;
	public HairData getHairData() {
		return hairData;
	}

	private List<Integer> ignoreConnectionIndexs;
	
	private void createIgnoreHorizontalConnections(){
		if(hairData.getHairPins().size()!=2 || !hairData.isCircleStyle()){
			return;
		}
		
		if(!hairData.isUseAmmoCircleInRange()){
			return;
		}
		
		boolean startAndEndSame=isStartAndEndSameCircle();
		int sliceFaceCount=getSliceFaceCount();
				
		int angleSplit=startAndEndSame?sliceFaceCount:sliceFaceCount+1;

		double perAngle=360.0/(angleSplit); //not support connect-horizontal
		
		List<Integer> ignoreConnection=Lists.newArrayList();
		for(int i=0;i<sliceFaceCount;i++){
			double angle=perAngle*i;
			boolean inRangeMy=hairData.isAmmoInCircleInRange(angle);
			boolean inRangeNext=hairData.isAmmoInCircleInRange(angle+perAngle);
			if(inRangeMy!=inRangeNext){
				ignoreConnection.add(i+1);
			}
		}
		if(hairData.isConnectHorizontal()){
			double angle=perAngle*sliceFaceCount;
			boolean inRangeMy=hairData.isAmmoInCircleInRange(angle);
			boolean inRangeNext=hairData.isAmmoInCircleInRange(0);
			if(inRangeMy!=inRangeNext){
				ignoreConnection.add(0);
			}
		}
		
			if(!ignoreConnection.isEmpty()){
				ignoreConnectionIndexs=ignoreConnection;
			}
		
	}
	
	private boolean notContainIgnoreCoonectionIndexs(int index){
		if(ignoreConnectionIndexs==null){
			return true;
		}
		
		
		
		return !ignoreConnectionIndexs.contains(index);
	}
	
	public HairCloth(HairData hairData,Mesh mesh){
		checkArgument(hairData.getSliceFaceCount()!=0,"HairCloth:invalid u-size 0");
		checkNotNull(mesh,"HairCloth:mesh is null");
		this.hairData=hairData.clone();
		
		ammoMultipleScalar=GWTThreeClothHair.INSTANCE.getAmmoWorldScale();
		LogUtils.log("ammoMultipleScalar:"+ammoMultipleScalar);
		
		
		
		createIgnoreHorizontalConnections();//for circle-style
	
		
		
		List<HairPin> normalPin=Lists.newArrayList();//trying cutom pin
		List<HairPin> customPin=Lists.newArrayList();
		
		for(HairPin pin:hairData.getHairPins()){
			if(pin.getTargetClothIndex()==-1){
				normalPin.add(pin);
			}else{
				customPin.add(pin);
			}
		}
		/*
		 * 
		 * when small cut u setted, sometime connection seems faild.
		 * 
		 */
		//this.ammoThick=hairData.getThickRatio();
		//this.ammoParticleSphereRadius=hairData.getParticleRadiusRatio();
		//this.hairPhysicsType=hairData.getHairPhysicsType();
		
		//this.connectHorizontal=hairData.isConnectHorizontal();
		//LogUtils.log("connect-horizontal:"+connectHorizontal);
		//this.cutHorizontalConnection=hairData.isCutU();
		//this.startCutHorizontalConnection=hairData.getStartCutUIndexV();
		
		
			
			this.w = (normalPin.size()-1)*hairData.getSliceFaceCount();
			
			
			this.h = hairData.getSizeOfV();
			
			double width=HairDataUtils.getTotalPinDistance(hairData, mesh,true);
			
			width*=hairData.getScaleOfU();
			
			double height=HairDataUtils.getTotalVDistance(width, w, h);
			
			this.initGravities(hairData.getMass(),hairData.getDamping());

			clothFunction=plane(width,height);
			
			if(!isCircleStyle()){
				restDistance=width/(w);
			}else{
				restDistance=width;
			}
			
			//this.channel=hairData.getChannel();
			//this.syncMove=hairData.isSyncMove();
			
			initPins();
			
			//LogUtils.log("restDistance:"+restDistance);
			
			// Create particles
			for (int v=0;v<=h;v++) {
				for (int u=0;u<=w;u++) {
					particles.add(
						new Particle((double)u/w*width, (double)v/h*height, 0, PLAIN_CLOTH_MASS)
					);
				}
			}
			
			//double wDistance=width/w;
			//double hDistance=height/h;

			// Structural

			/*
				particle exists v<=h & u<=w
			*/
			
			
			double distance=restDistance;
			for (int v=0;v<h;v++) {
				for (int u=0;u<w;u++) {
					//add vertical constraint
					constrains.add(
							new Constrain(particles.get(index(u,v)), particles.get(index(u,v+1)), distance)
							);
					
					//add horizontal constraint
					//first one skipped to reduce constraint count,i think not so differenct
					if(v!=0 && (!hairData.isCutU() || v<hairData.getStartCutUIndexV())){
						
						if(notContainIgnoreCoonectionIndexs(u+1)){
							addConstrain(particles.get(index(u,v)), particles.get(index(u+1,v)), distance);
						}
						
						//if(v%2==1){ // i tried mutually add,and faild
						
							
						//}
					}else{
						
						//i tried but not so good
						/*
						if(v==h-1){//try h-1
							constrains.add(
									new Constrain(particles.get(index(u,v)), particles.get(index(u+1,v)), distance));
						}*/
					}
					
					//test cross on first one,no care connect horizontal and circle style
					/*if(v==0){
						if(u!=0){//has next
							addConstrain(particles.get(index(u,v)), particles.get(index(u-1,v+1)), distance);
						}
						
						if(u!=w-1){//has prev
							addConstrain(particles.get(index(u,v)), particles.get(index(u+1,v+1)), distance);
						}
					}*/

				}
				
				//narrow mode,however usually make problem
				if(hairData.isDoNarrow()){
					distance*=hairData.getNarrowScale();
				}
				
			}
			
		
			
			
			
			for (int u=w, v=0;v<h;v++) {
				
				addConstrain(particles.get(index(u,v)), particles.get(index(u,v+1)), distance);
			}
			
			
			/*
			 * if add last one always some kind of unstable
			 */
			
			for (int v=h, u=0;u<w;u++) {
				if((!hairData.isCutU() || v<hairData.getStartCutUIndexV())){
				//	if(v%2==1){
					if(notContainIgnoreCoonectionIndexs(u+1)){
					addConstrain(particles.get(index(u,v)), particles.get(index(u+1,v)), distance);
					}
						
				//	}
				}
			}

			
			
			//loop-horizontal
			if(hairData.isConnectHorizontal()){
			for (int v=0;v<=h;v++) {
				int u=w;
				if((!hairData.isCutU() || v<hairData.getStartCutUIndexV())){
					if(notContainIgnoreCoonectionIndexs(0)){
						addConstrain(particles.get(index(u,v)), particles.get(index(0,v)), distance);
					}
				}
						
			}
			}
			
			
	
			
			
			
		
	}
	
	private void addConstrain(Particle p1,Particle p2,double distance){
		//change correct distance
		//distance=p1.getOriginal().distanceTo(p2.getOriginal());
		
		constrains.add(
				new Constrain(p1, p2, distance)
				);
	}
	
	/*
	 * however same grid seems much better
	 */
	public void recalcurateHorizontalConstraintsDistance(){
		
		for(Constrain con:constrains){
			int atX1=getAtX(con.p1);
			int atX2=getAtX(con.p2);
			if(atX1!=atX2){//horizontal connection
				
			double distance=particles.get(atX1).getOriginal().distanceTo(particles.get(atX2).getOriginal());
					
			//con.p1.getOriginal().distanceTo(con.p2.getOriginal());
			if(distance==0){
				distance=restDistance;
			}
			
			con.distance=distance;
			}
		}
		
	}
	
	public boolean isHorizontalConstraints(Constrain constrain){
		int atX1=getAtX(constrain.p1);
		int atX2=getAtX(constrain.p2);
		if(atX1!=atX2){
			return true;
		}else{
			return false;
		}
	}
	
	
	public List<Constrain> getConstrains() {
		return constrains;
	}
	private int getAtX(Particle p){
		int index=particles.indexOf(p);
		if(index==-1){
			return -1;
		}
		return index%(w+1);
	}
	
	private int index(int u,int v){
		return u + v * (w + 1);
	}
	
	private FloorModifier floorModifier;
	
	public FloorModifier getFloorModifier() {
		return floorModifier;
	}

	public void setFloorModifier(FloorModifier floorModifier) {
		this.floorModifier = floorModifier;
	}

	//TODO store
	//boolean syncMove=false;
	//int channel;//for sphere;
	
	
	
	
	public void simulatePlainCloth(double time,Geometry clothGeometry,List<Mesh> spheres) {
		if (lastTime==null) {
			lastTime = time;
			return;
		}
		//var i, il, particles, particle, pt, constrains, constrain;

		// Aerodynamics forces
		if (wind) {
			
			Face3 face;
			JsArray<Face3> faces = clothGeometry.getFaces();
			Vector3 normal;
			

			for (int i=0,il=faces.length();i<il;i++) {
				face = faces.get(i);
				normal = face.getNormal();

				tmpForce.copy(normal).normalize().multiplyScalar(normal.dot(windForce));
				if(face.getA()<particles.size()){
				particles.get(face.getA()).addForce(tmpForce);
				}
				if(face.getB()<particles.size()){
				particles.get(face.getB()).addForce(tmpForce);
				}
				if(face.getC()<particles.size()){
				particles.get(face.getC()).addForce(tmpForce);
				}
			}
			
		}
		
		for (int i=0, il = particles.size()
				;i<il;i++) {
			Particle particle = particles.get(i);
			particle.addForce(plain_cloth_gravity);

			particle.integrate(TIMESTEP_SQ);
		}

		// Start Constrains

		
		
		for (int i=0;i<constrains.size();i++) {
			Constrain constrain = constrains.get(i);
			satisifyConstrains(constrain.p1, constrain.p2, constrain.distance);
		}

		// Ball Constrains

		double now=System.currentTimeMillis();

		//ballPosition.setZ(-Math.sin(now/600) * 90 ) ; 
		//ballPosition.setX(Math.cos(now/400) * 70) ;

		//always
		//if (sphereMesh.isVisible())
		
		//ballSize=((SphereGeometry)sphereMesh.getGeometry().cast()).getRadius();
		
		
		for (int i=0;i<particles.size();i++) {
			Particle particle = particles.get(i);
			Vector3 pos = particle.position;
			
			//this simple style collision detector works on sphere
			for(Mesh mesh:spheres){
				JsSphereData jsData=mesh.getUserData().cast();
				if(jsData.getType()!=SphereData.TYPE_SPHERE){
					continue;
				}
				/*
				if(!data.isEnabled()){
					continue;
				}
				*/
				
				diff.subVectors(pos, mesh.getPosition());
				if (diff.length() < mesh.getScale().getX()) {
					// collided
					diff.normalize().multiplyScalar(mesh.getScale().getX());
					pos.copy(mesh.getPosition()).add(diff);
					//break;//?
				}
				
			}
			
			
		}
		
		if(floorModifier!=null){
			for (int i=0;i<particles.size();i++) {
				Particle particle = particles.get(i);
				Vector3 pos = particle.position;
				floorModifier.modifyFloor(pos);
			}
		}
		
		/*
		 * no floor
		// Floor Constains
		for (int i=0;i<particles.size();i++) {
			Particle particle = particles.get(i);
			Vector3 pos = particle.position;
			if (pos.getY() < -250) {
				pos.setY(-250);
			}
		}
		*/

		// Pin Constrains
		
		for (int i=0;i<pins.length;i++) {
			int xy = pins[i];
			Particle p = particles.get(xy);
			p.position.copy(p.original);
			p.previous.copy(p.original);
		}
	}
	//before simulate
	
	public void beforeSimulate(ClothSimulator simulator,Geometry clothGeometry,List<Mesh> spheres){
		if(initializing){
			return;
		}
		if(skipSync){
			skipSync=false;
			return;
		}
		
		if(!isSyncMove()){
			return;
		}
		
		if(!simulator.getAmmoHairControler().isEnabled()){
			
			return;
		}
		
		if(simulator.getAmmoHairControler().isExistParticleData(this)){
			AmmoHairControler.ParticleBodyDatas data=simulator.getAmmoHairControler().getAmmoData(this);
			List<BodyAndMesh> ammoParticles=data.getAmmoParticles();
			
			Vector3 diff=THREE.Vector3();
			diff.copy(particles.get(0).getOriginal().clone().sub(ammoParticles.get(0).getMesh().getPosition()));
			
			Vector3 move=diff.clone().multiplyScalar(hairData.getSyncMoveLinear()*ammoMultipleScalar);
			Vector3 force=diff.clone().multiplyScalar(hairData.getSyncForceLinear()*ammoMultipleScalar);
			
			
			//TODO custom pin support structure
			btVector3 vector3=simulator.getAmmoHairControler().getAmmoControler().makeVector3();
			for(int i=0;i<ammoParticles.size();i++){
				if(isPinned(i)){
					//Vector3 threePos=particles.get(i).getOriginal().clone().multiplyScalar(ammoMultipleScalar);
					//ammoParticles.get(i).getBody().setPosition(threePos.getX(),threePos.getY(),threePos.getZ());
				}else{
					btRigidBody body=ammoParticles.get(i).getBody();
					Vector3 pos=body.getReadOnlyPosition();
					pos.add(move);
					body.setPosition(pos);
					
					body.applyForce(vector3.copy(force));
				}
			}
		}
	}
	
	
	public boolean isBoneType(int type){
		return type==HairData.TYPE_AMMO_BONE_CLOTH || type==HairData.TYPE_AMMO_BONE_HAIR || type==HairData.TYPE_AMMO_BONE_BODY;
		
	}
	public boolean isAmmoType(int type){
		return type==HairData.TYPE_AMMO_CLOTH || type==HairData.TYPE_AMMO_BONE_CLOTH || type==HairData.TYPE_AMMO_BONE_HAIR || type==HairData.TYPE_AMMO_BONE_BODY;
	}
	
	//call after simulate
	public void afterSimulate(ClothSimulator simulator,double time,Geometry clothGeometry,List<Mesh> spheres) {
		if(initializing){
			return;
		}
		
		//simulate by group means,no grouping support yet
		//not support Wind yet
		
		
		if(hairData.getHairPhysicsType()==HairData.TYPE_SIMPLE_CLOTH){
			simulatePlainCloth(time,clothGeometry,spheres);
			return;
		}else if(isAmmoType(hairData.getHairPhysicsType())){
			simulateAmmo(simulator,time,clothGeometry,spheres);
		}
			
		
	}
	//double ammoParticleSphereRadius=0.5;//indivisual particle sphere radius
	
	//bigger value cloth would fly ( 0.5 is best.bigger easy to stuck,small easy to slip out)
	
	//TODO move ammohair controler and allow change from basic panel
     //double ammoMultipleScalar=0.1;//1;//0.1;//should be small,0.1 seems good,but need modify-function
	
	//very important,if use sync ,value should 1 so far 
	//TODO make global setting
	private double ammoMultipleScalar;
	 //private boolean visibleDummy=true;//use scale 1 is best //TODO fit dummys
	 
	 
	public double getAmmoMultipleScalar() {
		return ammoMultipleScalar;
	}


	private boolean visibleDummy;
	
	
	/*
	 * just create particle not good at sync
	 */
	private boolean skipSync;
	private void simulateAmmo(ClothSimulator simulator,double time, Geometry clothGeometry, List<Mesh> spheres) {

		int channel=hairData.getChannel();
		
		visibleDummy=GWTThreeClothHair.INSTANCE.getClothSimulator().getAmmoHairControler().isVisibleParticl();
		
		
		Stopwatch watch=Stopwatch.createStarted();
		
		boolean needInitSphere=false;
		if(!simulator.getAmmoHairControler().isExistSphereData(channel)){
			needInitSphere=true;
		}else{
		AmmoHairControler.SphereBodyData ammoSpheres=simulator.getAmmoHairControler().getSphereData(channel);
		if(spheres.size()!=ammoSpheres.getAmmoSpheres().size()){
			needInitSphere=true;
			simulator.getAmmoHairControler().removeSphereData(channel);
			}
			
		}
		
		
		if(needInitSphere){
			List<BodyAndMesh> ammoSpheres=Lists.newArrayList();
			for(int i=0;i<spheres.size();i++){
				//seems no need sphere
				Mesh sphereMesh=spheres.get(i);
				//LogUtils.log("sphere-scale:"+sphereMesh.getScale().getX());
				
				//create boxy mesh real-character-size (character usually scaled * 1000)
				
				/*
				 * 
				 * sphereMesh.scale is same as CharacterMesh on 
				 * 
				 * sphereMesh.getScale().getX()*ammoMultipleScalar
				 * 
				 */
				
				BodyAndMesh body=createAmmoCollisionBody(simulator,sphereMesh.getPosition().clone().multiplyScalar(ammoMultipleScalar),(JsSphereData)sphereMesh.getUserData().cast());
				body.setAmmoMultipleScalar(ammoMultipleScalar);
				ammoSpheres.add(body);	
			}
			
			simulator.getAmmoHairControler().setSphereData(channel,new AmmoHairControler.SphereBodyData(ammoSpheres));
			
		}else{
			AmmoHairControler.SphereBodyData data=simulator.getAmmoHairControler().getSphereData(channel);
			List<BodyAndMesh> ammoCollisions=data.getAmmoSpheres();
			for(int i=0;i<spheres.size();i++){
				
				boolean needReCreate=false;
				
				JsSphereData jsData=spheres.get(i).getUserData().cast();
				BodyAndMesh bodyAndMesh=ammoCollisions.get(i);
				if(jsData.getType()!=bodyAndMesh.getShapeType()){
					LogUtils.log("jsDataType:"+jsData.getType()+",bodyShapeType="+bodyAndMesh.getShapeType());
					needReCreate=true;
				}
				
				if(!needReCreate){
					double characterScale=simulator.getCharacterMesh().getScale().getX();
					if(bodyAndMesh.getShapeType()==BodyAndMesh.TYPE_SPHERE){
						double radius=bodyAndMesh.castToSphere().getRadius()/ammoMultipleScalar/characterScale;
						if(!isSame(4, radius, jsData.getWidth()/2)){
							LogUtils.log("sphere-radius:"+(jsData.getWidth()/2)+","+radius);
							needReCreate=true;
						}
					}else if(bodyAndMesh.getShapeType()==BodyAndMesh.TYPE_CAPSULE){
						double radius=bodyAndMesh.castToCapsule().getRadius()/ammoMultipleScalar/characterScale;
						double height=bodyAndMesh.castToCapsule().getHeight()/ammoMultipleScalar/characterScale;
						if(!isSame(4, radius, jsData.getWidth()/2)){
							LogUtils.log("capsule-radius:"+(jsData.getWidth()/2)+","+radius);
							needReCreate=true;
						}else 
						if(!isSame(4, height, jsData.getHeight())){
							LogUtils.log("capsule-height:"+(jsData.getHeight())+","+height);
							needReCreate=true;
						}
					}else{//box
						
						double width=bodyAndMesh.castToBox().getBoxSize().getX()/ammoMultipleScalar/characterScale;
						//TODO support boxSize
						if(!isSame(4, width, jsData.getWidth())){
							LogUtils.log("box-radius:"+jsData.getWidth()+","+width);
							needReCreate=true;
						}
						
					}
				}
				
				if(needReCreate){
					LogUtils.log("sphere replaced");
					simulator.getAmmoHairControler().removeSphereBodyData(bodyAndMesh);
					
					Mesh sphereMesh=spheres.get(i);
					BodyAndMesh body=createAmmoCollisionBody(simulator,sphereMesh.getPosition().clone().multiplyScalar(ammoMultipleScalar),(JsSphereData)sphereMesh.getUserData().cast());
					body.setAmmoMultipleScalar(ammoMultipleScalar);
					
					
					
					ammoCollisions.set(i, body);//replace
					simulator.getAmmoHairControler().addSphereBodyData(body);
					continue;
				}
				
				
				
				//TODO modify divided size
				Vector3 threePos=spheres.get(i).getPosition().clone().multiplyScalar(ammoMultipleScalar);
				ammoCollisions.get(i).getBody().setPosition(threePos.getX(),threePos.getY(),threePos.getZ());
				
				
				
				
				//ThreeLog.log("sphere-position-updated:"+i,threePos);
				//TODO fix re-create method
				double radius=spheres.get(i).getScale().getX()*ammoMultipleScalar;
				
				if(ammoCollisions.get(i).getShapeType()==BodyAndMesh.TYPE_SPHERE){
					/*
					LogUtils.log(ammoCollisions.get(i).getShapeType()+","+BodyAndMesh.TYPE_SPHERE);
					LogUtils.log(ammoCollisions.get(i));
					SphereBodyAndMesh sphere=ammoCollisions.get(i).castToSphere();
				if(!isSame(4,radius,sphere.getRadius())){
					//resize it costly
					LogUtils.log("sphere different-size-recreate:"+radius+","+sphere.getRadius());
					simulator.getAmmoHairControler().getAmmoControler().setRadiusWithRecreate(radius, ammoCollisions.get(i));
				}
				*/
					
				}else{
				//	LogUtils.log("box rotate updated");
					Quaternion q=spheres.get(i).getQuaternion();
					
					BodyAndMesh bm=ammoCollisions.get(i);
					bm.getBody().setRotation(q);
				}
				
				
			}
		}
		
		if(!simulator.getAmmoHairControler().isExistParticleData(this)){
			
			List<BodyAndMesh> ammoParticles=Lists.newArrayList();
			
			double baseMass=hairData.getParticleMass();//not so effect
			for(int i=0;i<particles.size();i++){
				Particle particle=particles.get(i);
				int v=i/(w+1);
				//this effect small,TODO more check
				
				
				
				if(v>h/2){
					//baseMass=0.5;
					//baseMass*=100;// 
				}
				
				
				/*
				 * trid last one is heavy,but not so good
				
				*/
				if(!needConnectHorizontal(v)){
					//LogUtils.log(v);
					//baseMass=0.05;//try less effect,i'm not sure still this is useful
				}
				
				double mass=isPinned(i)?0:baseMass;
				
				/*
				 * noeffect,try to less pin and keep loose
				mass=baseMass;//test no pin
				if(i%sizeOfU==0 && isPinned(i)){//try to loose
					mass=0;
				}
				*/
				
				
				BodyAndMesh p=createAmmoParticle(simulator,particle.getOriginal().clone().multiplyScalar(ammoMultipleScalar), mass,v);
				p.setAmmoMultipleScalar(ammoMultipleScalar);
				ammoParticles.add(p);
				
				//baseMass*=0.99;
			}
			
			//make constraint

			JsArray<btGeneric6DofSpringConstraint> ammoConstraints=createAmmoConstraints(simulator,ammoParticles);
			
			AmmoHairControler.ParticleBodyDatas data=new AmmoHairControler.ParticleBodyDatas(ammoParticles,ammoConstraints);
			
			
			if(hairData.getHairPhysicsType()==HairData.TYPE_AMMO_BONE_CLOTH){
			createAmmoBoneCloth(simulator, data, ammoParticles);
			skipSync=true;
			}else  if(hairData.getHairPhysicsType()==HairData.TYPE_AMMO_BONE_HAIR){
				//create bone mesh
				//position keep same
				createAmmoBoneHair(simulator,data,ammoParticles);
				
			}else if(hairData.getHairPhysicsType()==HairData.TYPE_AMMO_BONE_BODY){
					String suffix="breast-";//TODO
					//extream special
					SkinnedMesh characterMesh=simulator.getCharacterMesh();
					String rootName=suffix+"root";
					for(int i=0;i<characterMesh.getGeometry().getBones().length();i++){
						if(characterMesh.getGeometry().getBones().get(i).getName().equals(rootName)){
							ammoBoneBodyOffset=i;
							break;
						}
					}
					ammoBoneBodyLength=PlainBoneCreator.calcurateBoneCount(ammoParticles.size(), w);
					
					LogUtils.log("ammoBoneBodyOffset:"+ammoBoneBodyOffset+" ammoBoneBodyLength="+ammoBoneBodyLength);
				}
			
			LogUtils.log("buttlet-object-size:"+restDistance*ammoMultipleScalar);
			
			if(!initializing){
				simulator.getAmmoHairControler().setParticleData(this,data );
			}else{
				LogUtils.log("not initialized");
			}
			skipSync=true;
		}else{
			updateParticles(simulator);
		}
		
		//LogUtils.millisecond("total", watch);
	}
	private JsArray<btGeneric6DofSpringConstraint> createAmmoConstraints(ClothSimulator simulator, List<BodyAndMesh> ammoParticles) {
		JsArray<btGeneric6DofSpringConstraint> ammoConstraints=JavaScriptUtils.createJSArray();
		
		AmmoControler controler=simulator.getAmmoHairControler().getAmmoControler();
		btTransform transform1=controler.makeTransform();
		btTransform transform2=controler.makeTransform();
		for(int i=0;i<constrains.size();i++){
			Constrain con=constrains.get(i);
			int p1=particles.indexOf(con.p1);
			int p2=particles.indexOf(con.p2);
			
			int p1y=p1/(w+1);
			int p2y=p2/(w+1);
			
			
			boolean horizontalConnection=p1y==p2y;
			
			
			
			BodyAndMesh bm1=ammoParticles.get(p1);
			BodyAndMesh bm2=ammoParticles.get(p2);
			
			//already multiple when created
			Vector3 pos1=bm1.getMesh().getPosition();//.multiplyScalar(ammoMultipleScalar);
			Vector3 pos2=bm2.getMesh().getPosition();//.multiplyScalar(ammoMultipleScalar);
			
			
			AmmoConstraintPropertyData distanceConstraintProperties=simulator.getAmmoHairControler().getParticleConstraintData();
			if(hairData.isUseCustomConstraintData() && hairData.getAmmoConstraintData()!=null){
				distanceConstraintProperties=hairData.getAmmoConstraintData();
			}
			
			//right now no difference
			if(horizontalConnection){ 
				//use tight make shaking but tigh together.
				distanceConstraintProperties.setFrameInARelativePosRatio(distanceConstraintProperties.getFrameInARelativePosRatio());
				distanceConstraintProperties.setFrameInBRelativePosRatio(distanceConstraintProperties.getFrameInBRelativePosRatio());
			}else{
				//A alway fixed,never rotate
				distanceConstraintProperties.setFrameInARelativePosRatio(distanceConstraintProperties.getFrameInARelativePosRatio());
				distanceConstraintProperties.setFrameInBRelativePosRatio(distanceConstraintProperties.getFrameInBRelativePosRatio());
			}
			
			
			
			distanceConstraintProperties.updateFrameInA(transform1, pos1, pos2);
			distanceConstraintProperties.updateFrameInB(transform2, pos1, pos2);
			
			
			
			//try to fixed length but not good
			//transform2.getOrigin().copy(pos2.clone().sub(pos1).normalize().multiplyScalar(restDistance*ammoMultipleScalar));//keep length
			
			ConstraintAndLine constraintAndMesh=simulator.getAmmoHairControler().getAmmoControler().createGeneric6DofSpringConstraint(bm1, bm2, transform1, transform2, distanceConstraintProperties.isDisableCollisionsBetweenLinkedBodies());
			constraintAndMesh.setVisibleLine(false);
			
			
			
			simulator.getAmmoHairControler().getAmmoControler().updateConstraint(constraintAndMesh.getConstraint().castToGeneric6DofSpringConstraint(), distanceConstraintProperties,restDistance);
			
			ammoConstraints.push(constraintAndMesh.getConstraint().castToGeneric6DofSpringConstraint());
		}
		return ammoConstraints;
	}


	
	private void updateParticles(ClothSimulator simulator){
		AmmoHairControler.ParticleBodyDatas data=simulator.getAmmoHairControler().getAmmoData(this);
		List<BodyAndMesh> ammoParticles=data.getAmmoParticles();
		Vector3 threePos=THREE.Vector3();//share and improve fps
		//basically never changed length
		for(int i=0;i<ammoParticles.size();i++){
			if(isPinned(i)){//both plain & hair type need sync
				threePos.copy(particles.get(i).getOriginal()).multiplyScalar(ammoMultipleScalar);
				ammoParticles.get(i).getBody().setPosition(threePos.getX(),threePos.getY(),threePos.getZ());
			}else{
				//only plain-cloth need sync position here
				if(!isBoneType(hairData.getHairPhysicsType())){
				Vector3 ammoPos=ammoParticles.get(i).getBody().getReadOnlyPosition(threePos);
				particles.get(i).position.copy(ammoPos).divideScalar(ammoMultipleScalar);
				}
			}
		}
		
		
		//hair is handled as bone
		if(isBoneType(hairData.getHairPhysicsType())){
		if(hairData.getHairPhysicsType() == HairData.TYPE_AMMO_BONE_BODY){
			
			if(ammoBoneBodyOffset==-1){
				return;
			}
			
			SkinnedMesh mesh=simulator.getCharacterMesh();
			String suffix="breast-";//TODO
			double characterScale=mesh.getScale().getX();
			
			
			
			PlainBoneCreator.syncBones(simulator.getAmmoHairControler().getAmmoControler(), mesh, w, ammoParticles,ammoMultipleScalar,suffix,ammoBoneBodyOffset,ammoBoneBodyLength);
				
			//ammoBoneBodyOffset=-1;
			
		}else{
		
		PlainBoneCreator.syncBones(simulator.getAmmoHairControler().getAmmoControler(), data.getSkinnedMesh(), w, ammoParticles,ammoMultipleScalar);
		
		if(GWTThreeClothHair.INSTANCE.getClothSimulator().getAmmoHairControler().isVisibleBone()){
			data.getSkeltonHelper().update();
		}
		
		//Updating bounding sphere
	//	Stopwatch watch3=LogUtils.stopwatch();
		JsArray<Vector3> pos=JavaScriptUtils.createJSArray();
		for(int i=0;i<ammoParticles.size();i+=10){ //omit points for speed 
			pos.push(ammoParticles.get(i).getMesh().getPosition());
		}
		data.getSkinnedMesh().getGeometry().getBoundingSphere().setFromPoints(pos);
		}
		//LogUtils.microsecond("computeSphere",watch3);
			
			//data.getSkinnedMesh().getGeometry().computeBoundingSphere();//for camera
		}
	}
	private int ammoBoneBodyOffset=-1;//not initialized or not found
	private int ammoBoneBodyLength;
	
	private void createAmmoBoneCloth(ClothSimulator simulator,AmmoHairControler.ParticleBodyDatas data,List<BodyAndMesh> ammoParticles) {
		//create bone mesh
		//position keep same
		List<Vector3> positions=FluentIterable.from(ammoParticles).transform(BodyAndMeshFunctions.getMeshPosition()).transform(new CloneDivided(ammoMultipleScalar)).toList();
		
		//force up normal //THREE.Vector3(0,1,0)
		Geometry clothBox=new PointsToGeometry().debug(false).flipNormal(true).reverseFirstSurface(true).createGeometry(positions, w, restDistance*hairData.getThickRatio(), isConnectHorizontal());
		
		//I'm not sure why circle effect vertical thick
		
		
		clothBox.setBones(new PlainBoneCreator().createBone(positions, w));
		
		int influence=1;
		WeightResult result=new SimpleAutoWeight(influence).autoWeight(clothBox, clothBox.getBones(),Lists.newArrayList(0));//ignore root
		result.insertToGeometry(clothBox);
		//LogUtils.log(result.toString());
		
		
		MeshPhongMaterial boxhMaterial = THREE.MeshPhongMaterial(
				GWTParamUtils.MeshPhongMaterial().alphaTest(0.5).color(0x880000).specular(0x030303).emissive(0x111111).shininess(10)
				//.map(clothTexture)
				.skinning(true)
				.visible(true)
				//.wireframe(true)//wire frame
				//.side(THREE.DoubleSide)
				);
		
		SkinnedMesh clothBoxMesh = THREE.SkinnedMesh(clothBox,boxhMaterial);
		data.setSkinnedMesh(clothBoxMesh);
		simulator.getAmmoHairControler().getAmmoControler().getScene().add(clothBoxMesh);
		clothBoxMesh.getGeometry().computeBoundingSphere();//for camera
		clothBoxMesh.getGeometry().computeBoundingBox();
		
		SkeletonHelper helper=THREE.SkeletonHelper(clothBoxMesh);
		simulator.getAmmoHairControler().getAmmoControler().getScene().add(helper);
		data.setSkeltonHelper(helper);
		
		
		helper.setVisible(GWTThreeClothHair.INSTANCE.getClothSimulator().getAmmoHairControler().isVisibleBone());//TODO get visible from setting
		
	}
	private void createAmmoBoneHair(final ClothSimulator simulator,final AmmoHairControler.ParticleBodyDatas data,List<BodyAndMesh> ammoParticles) {
		List<Vector3> positions=FluentIterable.from(ammoParticles).transform(BodyAndMeshFunctions.getMeshPosition()).transform(new CloneDivided(ammoMultipleScalar)).toList();
		
		//force up normal //THREE.Vector3(0,1,0)
		//Geometry clothBox=new PointsToGeometry().debug(false).flipNormal(true).reverseFirstSurface(true).createGeometry(positions, w, restDistance*ammoThick, isConnectHorizontal());
		//
		
		/*
		 * bone skip system  not good,still testing?
		 */
		int boneSkipStack=0; //useless,because particle start 0 make unstable 
		int startPos=boneSkipStack*(w+1);
		
		//TODO fix at least w+1;,boneSkipStack need modifier
		
		List<Vector3> bonePos=Lists.newArrayList();
		for(int i=startPos;i<positions.size();i++){
			bonePos.add(positions.get(i));
		}
		final JsArray<AnimationBone> bones=new PlainBoneCreator().startVerticalIndex(boneSkipStack).createBone(bonePos, w);
		List<List<Integer>> enableList=PlainBoneCreator.splitBySlices(bones, w);
		
		
		final MeshPhongMaterial boxhMaterial = THREE.MeshPhongMaterial(
				GWTParamUtils.MeshPhongMaterial().alphaTest(0.5).color(0x880000).specular(0x030303).emissive(0x111111).shininess(10)
				//.map(clothTexture)
				.skinning(true)
				.visible(true)
				//.wireframe(true)//wire frame
				//.side(THREE.DoubleSide)
				);
		
		
		//I'm not sure why circle effect vertical thick
		//shape would rotate differenctly on path
		double thick2=hairData.getAmmoBoneThickRatio2()==0?hairData.getThickRatio():hairData.getAmmoBoneThickRatio2();
		
		if(!hairData.isEnableCustomGeometry() || hairData.getCustomGeometryName()==null){
		//LogUtils.log("no custom-hair geometry");
		Geometry clothBox=HairGeometryCreator.merge(
				new HairGeometryCreator()
				.bonesList(bones, enableList)
				.horizontalThick(hairData.getThickRatio()).verticalThick(thick2)
				.dummyHairCount(hairData.getAmmoCircleDummyHairCount()).dummyHairAngle(hairData.getAmmoCircleDummyHairAngle())
				.thinLast(hairData.getAmmoHairThinLast())
				.mergeFirstCenter(hairData.isAmmoCircleHairMergeCenter()).mergeLastVertex(hairData.isAmmoCircleHairMergeLast())
				.createGeometry(positions, w));
		
		clothBox.setBones(bones);
		

		SkinnedMesh clothBoxMesh = THREE.SkinnedMesh(clothBox,boxhMaterial);
		data.setSkinnedMesh(clothBoxMesh);
		simulator.getAmmoHairControler().getAmmoControler().getScene().add(clothBoxMesh);
		clothBoxMesh.getGeometry().computeBoundingSphere();//for camera
		clothBoxMesh.getGeometry().computeBoundingBox();
		
		SkeletonHelper helper=THREE.SkeletonHelper(clothBoxMesh);
		simulator.getAmmoHairControler().getAmmoControler().getScene().add(helper);
		data.setSkeltonHelper(helper);
		helper.setVisible(GWTThreeClothHair.INSTANCE.getClothSimulator().getAmmoHairControler().isVisibleBone());//TODO get visible from setting
		
		}else{
			LogUtils.log("custom-hair geometry");
			String basePath="/models/";
			initializing=true;
			
			String name=hairData.getCustomGeometryName();
			if(FileNames.hasNoExtension(name)){
				name+=".json";
			}
			name+="?t="+System.currentTimeMillis();//avoid cache
			
			//load geometry
			THREE.XHRLoader().load(basePath+name, new XHRLoadHandler() {
				
				@Override
				public void onLoad(String text) {
					Geometry geometry=GWTThreeUtils.parseJSONGeometry(text).getGeometry();
					geometry.applyMatrix(GWTThreeClothHair.INSTANCE.getCharacterMesh().getMatrixWorld());
					geometry.setBones(bones);
					
					//TODO auto weight setting
					if(hairData.isCustomGeometryUseAutoSkinning()){
					int influence=1;
					WeightResult result=new SimpleAutoWeight(influence).autoWeight(geometry, bones,Lists.newArrayList(0));//ignore root
					result.insertToGeometry(geometry);
					}
					
					SkinnedMesh clothBoxMesh = THREE.SkinnedMesh(geometry,boxhMaterial);
					data.setSkinnedMesh(clothBoxMesh);
					simulator.getAmmoHairControler().getAmmoControler().getScene().add(clothBoxMesh);
					clothBoxMesh.getGeometry().computeBoundingSphere();//for camera
					clothBoxMesh.getGeometry().computeBoundingBox();
					
					SkeletonHelper helper=THREE.SkeletonHelper(clothBoxMesh);
					simulator.getAmmoHairControler().getAmmoControler().getScene().add(helper);
					data.setSkeltonHelper(helper);
					helper.setVisible(GWTThreeClothHair.INSTANCE.getClothSimulator().getAmmoHairControler().isVisibleBone());//TODO get visible from setting
					
					
					simulator.getAmmoHairControler().setParticleData(HairCloth.this,data );
					
					initializing=false;
				}
			});
			
			
			
		}
		
		
		//int influence=2;
		//WeightResult result=new SimpleAutoWeight(influence).autoWeight(clothBox, clothBox.getBones(),Lists.newArrayList(0));//ignore root
		//result.insertToGeometry(clothBox);
		
		//LogUtils.log(result.toString());
		
		

		
		
		
		//updateParticles(simulator);//for sync need update here
		skipSync=true;
	}

	private boolean initializing;
	
	public native final boolean isSame(int c,double value1,double value2)/*-{
	return value1.toFixed(c)==value2.toFixed(c);
	}-*/;

	

	
	protected BodyAndMesh createAmmoCollisionBody(ClothSimulator simulator,Vector3 position,JsSphereData sphereData) {
		double characterScale=GWTThreeClothHair.INSTANCE.getCharacterMesh().getScale().getX();
		MeshPhongMaterial material=THREE.MeshPhongMaterial(GWTParamUtils.MeshPhongMaterial().color(0xff0000)
				.visible(visibleDummy)); //controld by panel basci/ammo
				//.wireframe(true))
				
		
		//
		BodyAndMesh body=null;
		if(sphereData.getType()==SphereData.TYPE_BOX){
		double w=sphereData.getWidth()*ammoMultipleScalar*characterScale;
		double h=sphereData.getHeight()*ammoMultipleScalar*characterScale;
		double d=sphereData.getDepth()*ammoMultipleScalar*characterScale;
		body=BodyAndMesh.createBox(THREE.Vector3(w,h,d), 0, position.getX(),position.getY(),position.getZ(),material);
		//rotate later
		}else if(sphereData.getType()==SphereData.TYPE_CAPSULE){
			double radius=sphereData.getWidth()/2*ammoMultipleScalar*characterScale;
			double h=sphereData.getHeight()*ammoMultipleScalar*characterScale;
			
			body=BodyAndMesh.createCapsule(radius,h, 0, position.getX(),position.getY(),position.getZ(),material);
			//rotate later
			}else{
		double w=sphereData.getWidth()/2*ammoMultipleScalar*characterScale;
		body=BodyAndMesh.createSphere(w, 0, position.getX(),position.getY(),position.getZ(),material);	
		}
		AmmoUtils.updateBodyProperties(body.getBody(),simulator.getAmmoHairControler().getCollisionProperties());
		body.getBody().setActivationState(Ammo.DISABLE_DEACTIVATION);
		
		return body;
		
	}

	private BodyAndMesh createAmmoParticle(ClothSimulator simulator,Vector3 p,double mass,int v){
		//i tried x-y-z differenct size,but it's seems impossible to control side on circle
		double x=restDistance*ammoMultipleScalar*hairData.getParticleRadiusRatio();
		double endX=restDistance*ammoMultipleScalar*hairData.getAmmoEndParticleRadiusRatio();
		
		if(v==h){
			//TODO interporate
			if(endX!=0){
				x=endX;
			//	mass*=10;
			}
		}
		int type=hairData.getParticleType();
		
		MeshPhongMaterial material=THREE.MeshPhongMaterial(GWTParamUtils.MeshPhongMaterial().color(0x008800).visible(visibleDummy));//dummy
		
		//LogUtils.log("particle-type:"+type);
		//SphereBodyAndMesh 
		BodyAndMesh body=null;
		if(type==BodyAndMesh.TYPE_BOX){
			body=BodyAndMesh.createBox(THREE.Vector3(x*2,x*2,x*2), mass, p,material);
			//body=BodyAndMesh.createBox(THREE.Vector3(x*2*4,x*2*4,x*2), mass, p,material);//plain
		}else if(type==BodyAndMesh.TYPE_CAPSULE){
			//tryied but faild,maybe becaus of gravity can't keep direction
			body=BodyAndMesh.createCapsule(x,x*2*4, mass, p,THREE.Quaternion().setFromEuler(THREE.Euler(Math.toRadians(0),0, 0)),material);
			//body.getBody().setRotation());
		}else if(type==BodyAndMesh.TYPE_CYLINDER){
			body=BodyAndMesh.createCylinder(x,x*2, mass, p,material);
			
		}else if(type==BodyAndMesh.TYPE_CONE){
			body=BodyAndMesh.createCone(x,x*2, mass, p,material);
		}else{
			body=BodyAndMesh.createSphere(x, mass, p,material);
		}
		
		AmmoBodyPropertyData property=simulator.getAmmoHairControler().getParticleBodyData();
		if(hairData.isUseCustomBodyParticleData()){
			if(hairData.getAmmoBodyParticleData()!=null){
				property=hairData.getAmmoBodyParticleData();
				//LogUtils.log("body-p from data");
			}
		}
		
		//LogUtils.log("tmp-body-property:"+property.getDamping().getX());
		AmmoUtils.updateBodyProperties(body.getBody(),property);
		body.getBody().setActivationState(Ammo.DISABLE_DEACTIVATION);
		
		
		return body;
	}

	public boolean isUseFirstPointY() {
		return hairData.isAmmoCircleUseFirstPointY();
	}
	public boolean isAmmoInCircleInRange(double angle){
		if(!hairData.isUseAmmoCircleInRange()){
			return false;
		}
		return hairData.isAmmoInCircleInRange(angle);
	}
	public double getAmmoCircleInRangeRatio(){
		return hairData.getAmmoCircleInRangeRatio();
	}


	

}
