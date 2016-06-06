package com.akjava.gwt.clothhair.client.cloth;

import java.util.ArrayList;
import java.util.List;

import com.akjava.gwt.clothhair.client.hair.HairData;
import com.akjava.gwt.clothhair.client.hair.HairDataUtils;
import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Face3;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;



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
		public Constrain(Particle p1, Particle p2, double distance) {
			super();
			this.p1 = p1;
			this.p2 = p2;
			this.distance = distance;
		}
	}
	public class Particle{
		Vector3 position;
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
		
		this.connectHorizontal=hairData.isConnectHorizontal();
		
			//TODO support 0 sizeOfU
			this.w = (normalPin.size()-1)*hairData.getSizeOfU();
			
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
					
					constrains.add(
							new Constrain(particles.get(index(u,v)), particles.get(index(u,v+1)), distance)
							);
					
					if(!hairData.isCutU() || v<hairData.getStartCutUIndexV()){
					constrains.add(
							new Constrain(particles.get(index(u,v)), particles.get(index(u+1,v)), distance)
							);
					}
					

				}
				
				//narrow mode
				if(hairData.isDoNarrow()){
					distance*=hairData.getNarrowScale();
				}
				
			}
			
		
			
			
			//TODO effect cut
			for (int u=w, v=0;v<h;v++) {
				
				constrains.add(
						new Constrain(particles.get(index(u,v)), particles.get(index(u,v+1)), distance)
						);
			}
			
			
			
			for (int v=h, u=0;u<w;u++) {
				if(!hairData.isCutU() || v<hairData.getStartCutUIndexV()){
				constrains.add(
						new Constrain(particles.get(index(u,v)), particles.get(index(u+1,v)), distance)
						);
				}
			}

			
			
			//loop-horizontal
			if(connectHorizontal){
			for (int v=0;v<=h;v++) {
				int u=w;
				constrains.add(
						new Constrain(particles.get(index(u,v)), particles.get(index(0,v)), distance)
						);
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
	
	public void simulate(double time,Geometry clothGeometry,List<Mesh> spheres) {
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


	

}
