package com.akjava.gwt.clothhair.client.cloth;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.clothhair.client.ammo.AmmoHairControler;
import com.akjava.gwt.clothhair.client.cannon.CannonControler.ParticleBodyData;
import com.akjava.gwt.clothhair.client.cannon.CannonControler.SphereBodyData;
import com.akjava.gwt.clothhair.client.hair.HairData;
import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.akjava.gwt.clothhair.client.hair.HairDataUtils;
import com.akjava.gwt.lib.client.JavaScriptUtils;
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Face3;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.materials.MeshPhongMaterial;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.threeammo.client.AmmoUtils;
import com.akjava.gwt.threeammo.client.BodyAndMesh;
import com.akjava.gwt.threeammo.client.core.Ammo;
import com.akjava.gwt.threeammo.client.core.btRigidBody;
import com.github.gwtcannonjs.client.CANNON;
import com.github.gwtcannonjs.client.constraints.DistanceConstraint;
import com.github.gwtcannonjs.client.math.Vec3;
import com.github.gwtcannonjs.client.objects.Body;
import com.github.gwtcannonjs.client.shapes.Sphere;
import com.google.common.base.Stopwatch;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;


/**
 * THIS is CANNON SUPPORT VERSION
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

	
	
	public static final int PHYSICS_CANNON=0;
	public static final int PHYSICS_CLOTH=1;
	public static final int PHYSICS_AMMO=2;
	
	private int physicsMode;
	
	
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
		this.DAMPING=DAMPING;
		this.DRAG = 1.0 - DAMPING;
		this.MASS=MASS;
		this.GRAVITY=GRAVITY;
		gravity = THREE.Vector3( 0, -GRAVITY, 0 ).multiplyScalar(MASS);
	}
	public void initGravities(double MASS,double DAMPING){
		this.DAMPING=DAMPING;
		this.DRAG = 1.0 - DAMPING;
		this.MASS=MASS;
		gravity = THREE.Vector3( 0, -GRAVITY, 0 ).multiplyScalar(MASS);
	}
	
	
	double DAMPING = 0.03;
	double DRAG = 1.0 - DAMPING;
	double MASS = .1;
	double restDistance = 25;


	public double getRestDistance() {
		return restDistance;
	}


	private static int xSegs = 10; //
	private static int ySegs = 10; //

	//must be javascript function ParametricGeometry use this function
	JavaScriptObject clothFunction;

	//var cloth = new Cloth(xSegs, ySegs);

	double GRAVITY = 981 * 1.4; // 
	Vector3 gravity = THREE.Vector3( 0, -GRAVITY, 0 ).multiplyScalar(MASS);


	//double TIMESTEP = 9.0 / 1000;
	double TIMESTEP = 18.0 / 1000;//i'm not sure what is this
	
	double TIMESTEP_SQ = TIMESTEP * TIMESTEP;

	private int[] pins;


	public int[] getPins() {
		return pins;
	}
	
	
	public void setPins(int[] pins) {
		this.pins = pins;
	}


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
	
	int w;
	public int getW() {
		return w;
	}


	int h;
	
	private List<Constrain> constrains=new ArrayList<Constrain>();
	public List<Particle> particles=new ArrayList<Particle>();
	
	/*
	public Cloth2(){
		this(xSegs,ySegs);
	}
	*/
	
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
	
	private boolean connectHorizontal=false;
	
	
	public boolean isConnectHorizontal() {
		return connectHorizontal;
	}
	public void setConnectHorizontal(boolean loopHorizontal) {
		this.connectHorizontal = loopHorizontal;
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
	
	private int sizeOfU;
	
	private boolean cutHorizontalConnection;
	private int startCutHorizontalConnection;
	
	public static int calcurateHorizontalPin(int normalPinSize,int sizeOfU){
		return (normalPinSize-1)*sizeOfU+1;
	}
	
	//ignore 0 index
	public boolean needConnectHorizontal(int v){
		return (!cutHorizontalConnection || v<startCutHorizontalConnection);
	}
	public HairCloth(HairData hairData,Mesh mesh){
			
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
		
		this.connectHorizontal=hairData.isConnectHorizontal();
		//LogUtils.log("connect-horizontal:"+connectHorizontal);
		this.cutHorizontalConnection=hairData.isCutU();
		this.startCutHorizontalConnection=hairData.getStartCutUIndexV();
		
		
			//TODO support 0 sizeOfU
			this.w = (normalPin.size()-1)*hairData.getSizeOfU();
			sizeOfU=hairData.getSizeOfU();
			
			this.h = hairData.getSizeOfV();
			
			double width=HairDataUtils.getTotalPinDistance(hairData, mesh,true);
			
			width*=hairData.getScaleOfU();
			
			double height=HairDataUtils.getTotalVDistance(width, w, h);
			
			this.initGravities(hairData.getMass(),hairData.getDamping());

			clothFunction=plane(width,height);
			restDistance=width/(w);
			
			this.channel=hairData.getChannel();
			this.syncMove=hairData.isSyncMove();
			
			initPins();
			
			//LogUtils.log("restDistance:"+restDistance);
			
			// Create particles
			for (int v=0;v<=h;v++) {
				for (int u=0;u<=w;u++) {
					particles.add(
						new Particle((double)u/w*width, (double)v/h*height, 0, MASS)
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
						//if(v%2==1){ // i tried mutually add,and faild
						addConstrain(particles.get(index(u,v)), particles.get(index(u+1,v)), distance);
							
						//}
					}else{
						
						//i tried but not so good
						/*
						if(v==h-1){//try h-1
							constrains.add(
									new Constrain(particles.get(index(u,v)), particles.get(index(u+1,v)), distance));
						}*/
					}
					
					

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
					addConstrain(particles.get(index(u,v)), particles.get(index(u+1,v)), distance);
						
				//	}
				}
			}

			
			
			//loop-horizontal
			if(connectHorizontal){
			for (int v=0;v<=h;v++) {
				int u=w;
				
				addConstrain(particles.get(index(u,v)), particles.get(index(0,v)), distance);
						
			}
			}
			
			
			//trying last one edging
			if(hairData.getEdgeMode()!=0){
				int targetIndex=0;//1
				if(hairData.getEdgeMode()==2){
				targetIndex=w/2;
				}else if(hairData.getEdgeMode()==3){//3
				targetIndex=w;
				}
				
				//LogUtils.log(targetIndex);
				
				Particle centerParticle=particles.get(index(targetIndex,h));

				for(Constrain constrain:constrains){
					if(constrain.p1==centerParticle || constrain.p2==centerParticle){
						constrain.distance=constrain.distance*hairData.getEdgeModeScale();
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
	
	//only effect before cannon constraints created
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
	boolean syncMove=false;
	int channel;//for sphere;
	
	
	
	
	public void simulateCloth(double time,Geometry clothGeometry,List<Mesh> spheres) {
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
			particle.addForce(gravity);

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
			
			for(Mesh data:spheres){
				/*
				if(!data.isEnabled()){
					continue;
				}
				*/
				
				diff.subVectors(pos, data.getPosition());
				if (diff.length() < data.getScale().getX()) {
					// collided
					diff.normalize().multiplyScalar(data.getScale().getX());
					pos.copy(data.getPosition()).add(diff);
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
		//made cannon object or sync cannon-object position
	}
	
	//call after simulate
	public void afterSimulate(ClothSimulator simulator,double time,Geometry clothGeometry,List<Mesh> spheres) {
		//simulate by group means,no grouping support yet
		//not support Wind yet
		if(isConnectHorizontal()){
			physicsMode=PHYSICS_AMMO;
		}
		
		if(physicsMode==PHYSICS_CLOTH){
			simulateCloth(time,clothGeometry,spheres);
			return;
		}else if(physicsMode==PHYSICS_AMMO){
			simulateAmmo(simulator,time,clothGeometry,spheres);
		}
			else{//CANNON.js
		
			simulateCannon(simulator,time,clothGeometry,spheres);
		}
		
	}
	

	private void simulateAmmo(ClothSimulator simulator,double time, Geometry clothGeometry, List<Mesh> spheres) {

		//default simulate cannon
		
		Stopwatch watch=Stopwatch.createStarted();
		
		boolean needInitSphere=false;
		if(!simulator.getAmmoHairControler().isExistSphereData(channel)){
			needInitSphere=true;
		}else{
		AmmoHairControler.SphereBodyData cannonSpheres=simulator.getAmmoHairControler().getSphereData(channel);
		if(spheres.size()!=cannonSpheres.getAmmoSpheres().size()){
			needInitSphere=true;
			simulator.getAmmoHairControler().removeSphereData(channel);
			}
			
		}
		
		
		if(needInitSphere){
			List<BodyAndMesh> cannonSpheres=Lists.newArrayList();
			for(int i=0;i<spheres.size();i++){
				//seems no need sphere
				Mesh sphereMesh=spheres.get(i);
				BodyAndMesh body=createAmmoSphereBody(simulator,sphereMesh.getPosition().clone().divideScalar(1000),sphereMesh.getScale().getX()/1000);
				cannonSpheres.add(body);	
			}
			
			simulator.getAmmoHairControler().setSphereData(channel,new AmmoHairControler.SphereBodyData(cannonSpheres));
			
		}else{
			AmmoHairControler.SphereBodyData data=simulator.getAmmoHairControler().getSphereData(channel);
			List<BodyAndMesh> cannonSpheres=data.getAmmoSpheres();
			for(int i=0;i<spheres.size();i++){
				//TODO modify divided size
				Vector3 threePos=spheres.get(i).getPosition().clone().divideScalar(1000);
				cannonSpheres.get(i).getBody().setPosition(threePos.getX(),threePos.getY(),threePos.getZ());
				double radius=spheres.get(i).getScale().getX()/1000;
				cannonSpheres.get(i).getBody().
				for(int j=0;j<cannonSpheres.get(i).getShapes().length();j++){
					Sphere sphere=cannonSpheres.get(i).getShapes().get(j).cast();
					sphere.setRadius(radius);
				}
				//cannonSpheres.get(i).set
				//sadly not support size yet
			}
		}
		
		if(!simulator.getAmmoHairControler.isExistParticleData(this)){
			
			
			JsArray<Body> cannonParticles=JavaScriptUtils.createJSArray();
			JsArray<DistanceConstraint> cannonConstraints=JavaScriptUtils.createJSArray();
			for(int i=0;i<particles.size();i++){
				Particle particle=particles.get(i);
				int v=i/(w+1);
				//this effect small,TODO more check
				double baseMass=1;//this is very important
				
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
				
				Body p=createParticle(simulator,particle.getOriginal().clone().divideScalar(1000), mass);
				cannonParticles.push(p);
			}
			
			
			for(int i=0;i<constrains.size();i++){
				Constrain con=constrains.get(i);
				int p1=particles.indexOf(con.p1);
				int p2=particles.indexOf(con.p2);
				//max force is no effect?.at least set 1 or Too gravity.
				cannonConstraints.push(CANNON.newDistanceConstraint(cannonParticles.get(p1),cannonParticles.get(p2),con.distance/1000));
			}
			
			
			
			
			simulator.getAmmoHairControler.setParticleData(this, new ParticleBodyData(cannonParticles,cannonConstraints));
		}else{
			ParticleBodyData data=simulator.getAmmoHairControler.getCannonData(this);
			JsArray<Body> cannonParticles=data.getCannonParticles();
			
			//basically never changed length
			for(int i=0;i<cannonParticles.length();i++){
				if(isPinned(i)){
					Vector3 threePos=particles.get(i).getOriginal().clone().divideScalar(1000);
					cannonParticles.get(i).getPosition().set(threePos.getX(),threePos.getY(),threePos.getZ());
				}else{
					Vec3 cannonPos=cannonParticles.get(i).getPosition();
					particles.get(i).position.set(cannonPos.getX(),cannonPos.getY(),cannonPos.getZ()).multiplyScalar(1000);
				}
			}
			
			
			
			
		}
		
		
	}

	private void simulateCannon(ClothSimulator simulator,double time, Geometry clothGeometry, List<Mesh> spheres) {

		//default simulate cannon
		
		Stopwatch watch=Stopwatch.createStarted();
		
		boolean needInitSphere=false;
		if(!simulator.getCannonControler().isExistSphereData(channel)){
			needInitSphere=true;
		}else{
		SphereBodyData cannonSpheres=simulator.getCannonControler().getSphereData(channel);
		if(spheres.size()!=cannonSpheres.getCannonSpheres().length()){
			needInitSphere=true;
			simulator.getCannonControler().removeSphereData(channel);
			}
			
		}
		
		
		if(needInitSphere){
			JsArray<Body> cannonSpheres=JavaScriptUtils.createJSArray();
			for(int i=0;i<spheres.size();i++){
				Mesh sphereMesh=spheres.get(i);
				Body body=createSphereBody(simulator,sphereMesh.getPosition().clone().divideScalar(1000),sphereMesh.getScale().getX()/1000);
				cannonSpheres.push(body);	
			}
			
			simulator.getCannonControler().setSphereData(channel,new SphereBodyData(cannonSpheres));
			
		}else{
			SphereBodyData data=simulator.getCannonControler().getSphereData(channel);
			JsArray<Body> cannonSpheres=data.getCannonSpheres();
			for(int i=0;i<spheres.size();i++){
				Vector3 threePos=spheres.get(i).getPosition().clone().divideScalar(1000);
				cannonSpheres.get(i).getPosition().set(threePos.getX(),threePos.getY(),threePos.getZ());
				double radius=spheres.get(i).getScale().getX()/1000;
				for(int j=0;j<cannonSpheres.get(i).getShapes().length();j++){
					Sphere sphere=cannonSpheres.get(i).getShapes().get(j).cast();
					sphere.setRadius(radius);
				}
				//cannonSpheres.get(i).set
				//sadly not support size yet
			}
		}
		
		if(!simulator.getCannonControler().isExistParticleData(this)){
			
			
			JsArray<Body> cannonParticles=JavaScriptUtils.createJSArray();
			JsArray<DistanceConstraint> cannonConstraints=JavaScriptUtils.createJSArray();
			for(int i=0;i<particles.size();i++){
				Particle particle=particles.get(i);
				int v=i/(w+1);
				//this effect small,TODO more check
				double baseMass=1;//this is very important
				
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
				
				Body p=createParticle(simulator,particle.getOriginal().clone().divideScalar(1000), mass);
				cannonParticles.push(p);
			}
			
			
			for(int i=0;i<constrains.size();i++){
				Constrain con=constrains.get(i);
				int p1=particles.indexOf(con.p1);
				int p2=particles.indexOf(con.p2);
				//max force is no effect?.at least set 1 or Too gravity.
				cannonConstraints.push(CANNON.newDistanceConstraint(cannonParticles.get(p1),cannonParticles.get(p2),con.distance/1000));
			}
			
			
			
			
			simulator.getCannonControler().setParticleData(this, new ParticleBodyData(cannonParticles,cannonConstraints));
		}else{
			ParticleBodyData data=simulator.getCannonControler().getCannonData(this);
			JsArray<Body> cannonParticles=data.getCannonParticles();
			
			//basically never changed length
			for(int i=0;i<cannonParticles.length();i++){
				if(isPinned(i)){
					Vector3 threePos=particles.get(i).getOriginal().clone().divideScalar(1000);
					cannonParticles.get(i).getPosition().set(threePos.getX(),threePos.getY(),threePos.getZ());
				}else{
					Vec3 cannonPos=cannonParticles.get(i).getPosition();
					particles.get(i).position.set(cannonPos.getX(),cannonPos.getY(),cannonPos.getZ()).multiplyScalar(1000);
				}
			}
			
			
			
			
		}
		
		
		
		//sphere out,not work so good
		//TODO switch
		
		/*
		 * Vector3 diff = THREE.Vector3();
		for (int i=0;i<particles.size();i++) {
			if(isPinned(i)){
				continue;
			}
			Particle particle = particles.get(i);
			Vector3 pos = particle.getPosition();
			
			for(Mesh mesh:spheres){
				diff.subVectors(pos, mesh.getPosition());
				if (diff.length() < mesh.getScale().getX()) {
					String before=ThreeLog.get(pos);
					diff.normalize().multiplyScalar(mesh.getScale().getX());
					
					
					pos.copy(mesh.getPosition()).add(diff);
					String after=ThreeLog.get(pos);
					LogUtils.log(i+","+before+","+after);
					//pos.copy(mesh.getPosition()).add(diff);
					//LogUtils.log("scale-out");
					//break;//?
				}
				
			}
		}
		*/
		
		//around 0-1 ms
		
		//LogUtils.log("simulate-time:"+watch.elapsed(TimeUnit.MILLISECONDS)+" ms");
		
	}
	

	
	protected Body createSphereBody(ClothSimulator simulator,Vector3 position, double size) {
		
		com.github.gwtcannonjs.client.shapes.Sphere sphereShape = CANNON.newSphere(size);
		
		//need more try and error,not good at cloth,not smooth
		//com.github.gwtcannonjs.client.shapes.Box sphereShape = CANNON.newBox(CANNON.newVec3(size, size, size));
		
		Body sphereBody  = CANNON.newBody(CANNON.newBodyOptions().withMass(0)
				.withMaterial(simulator.getCannonControler().getSphereMaterial())
				);
		
		sphereBody.addShape(sphereShape);
		
		
		sphereBody.getPosition().set(position.getX(),position.getY(),position.getZ());//sphereBody.position.set(0,0,0);
		return sphereBody;
		
	}
	
	protected BodyAndMesh createAmmoSphereBody(ClothSimulator simulator,Vector3 position, double size) {
		
		MeshPhongMaterial material=THREE.MeshPhongMaterial(GWTParamUtils.MeshPhongMaterial().color(0xff0000));//dummy
		
		BodyAndMesh body=BodyAndMesh.createSphere(size, 0, position.getX(),position.getY(),position.getZ(),material);
		AmmoUtils.setBodyMaterial(body.getBody(),simulator.getAmmoHairControler().getSpherehMaterial());
		body.getBody().setActivationState(Ammo.DISABLE_DEACTIVATION);
		
		return body;
		
	}

	private Body createParticle(ClothSimulator simulator,Vector3 p,double mass){
		Body particle = CANNON.newBody(CANNON.newBodyOptions().withMass(mass)
				.withMaterial(simulator.getCannonControler().getClothMaterial())
		
				);
		double s=restDistance/1000/2;
		
		//so so work,maybe slow
		particle.addShape(CANNON.newSphere(s));
		//seems impossible
		//particle.addShape(CANNON.newBox(CANNON.newVec3(s, s, s)));
		//particle.addShape(CANNON.newParticle());
		
		
		//this seems works when extremly dynamic movement
		particle.setAngularDamping(0.99);
		particle.setLinearDamping(0.99);
		
		//particle.linearDamping = 0.5;
		particle.getPosition().set(//particle.position.set(
		p.getX(),
		p.getY(),
		p.getZ()
		);
		
		return particle;
	}
	

}
