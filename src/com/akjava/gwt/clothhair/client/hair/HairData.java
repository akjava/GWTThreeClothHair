package com.akjava.gwt.clothhair.client.hair;

import java.util.List;

import com.akjava.gwt.clothhair.client.texture.HairTextureData;
import com.akjava.gwt.threeammo.client.AmmoBodyPropertyData;
import com.akjava.gwt.threeammo.client.AmmoConstraintPropertyData;
import com.akjava.gwt.threeammo.client.BodyAndMesh;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;
import com.google.gwt.json.client.JSONObject;

/**
 * serializable data
 * @author aki
 *
 */
public class HairData {
public static final String DATA_TYPE="HairData";
private int particleType=BodyAndMesh.TYPE_BOX;

public int getParticleType() {
	return particleType;
}

public void setParticleType(int particleType) {
	this.particleType = particleType;
}
private boolean ammoStartCenterCircle;
	
public boolean isAmmoStartCenterCircle() {
	return ammoStartCenterCircle;
}

public void setAmmoStartCenterCircle(boolean ammoStartCenterCircle) {
	this.ammoStartCenterCircle = ammoStartCenterCircle;
}
/*
 * show this hair or not ,not serialized for viewing 
 * not storing	
 */
private boolean visible;
public boolean isVisible() {
	return visible;
}

public void setVisible(boolean visible) {
	this.visible = visible;
}
/*
 * connect to first and last
 * 
 *  actual geometry is difference on physics-type.
 *  
 *  PLAIN_CLOTH,AMMO_CLOTH has a space between connection.
 *  
 *  AMMO_BONE_HAIR no effect on geometry,but linked with constraint(joint)
 *  
 *  bugs
 *  if connected,therea are not adding extra sizeOfU between first and last,usually it make a hole.
 *  so when connected ,you should add last point as same as first or near first.
 *  
 */
private boolean connectHorizontal;
public boolean isConnectHorizontal() {
	return connectHorizontal;
}

public void setConnectHorizontal(boolean connectHorizontal) {
	this.connectHorizontal = connectHorizontal;
}

/*
 * cloth made by normals.
 * if use execAverageNormal more natural curved mesh made.(take care of next of both side vertex)
 * 
 * it's effect on connected horizontal 
 * 
 * actual averaging normal is created in ClothSimulator.addCloth
 * 
 * 
 */
private boolean execAverageNormal=true;
public boolean isExecAverageNormal() {
	return execAverageNormal;
}
public void setExecAverageNormal(boolean execAverageNormal) {
	this.execAverageNormal = execAverageNormal;
}

/*
 * Hair pin is the data position,skinning-indices,skinning-weights
 * right now only support relative position from character-mesh
 * so pin contain only faceindex and vertexIndex(0-3) of face
 * 
 * basically pin is used for first static rows column.
 * if you set target,the target particle become static.(but this is not work on ammo)
 * 
 * 
 * 1 pin only is not suppported (TODO as rope)
 * 2 pin only make special circle,good work on ammo
 */
private List<HairPin> hairPins=Lists.newArrayList();
public List<HairPin> getHairPins() {
return hairPins;
}

/**
 * 
 * @return pin-size which has no target and used for first row column.
 */
public int countNormalPin(){
	int result=0;
	for(HairPin pin:hairPins){
		if(pin.getTargetClothIndex()==-1){
			result++;
		}
	}
	return result;
}

/**
 * how many face has each NormalPin
 * 
 * (normalPinSize-1)*SizeOfU == horizontal vertex (if connected add extra one to geometry)
 * 
 * Interpolate linear.
 */
private int sliceFaceCount=8;//sizeOfU
public int getSliceFaceCount() {
	return sliceFaceCount;
}
public void setSizeOfU(int sizeOfU) {
	this.sliceFaceCount = sizeOfU;
}

/*
 * vertical number of face
 * usually face's height is same as width.
 */
private int stackFaceCount=8;//sizeOfV
public int getSizeOfV() {
	return stackFaceCount;
}
public void setSizeOfV(int sizeOfV) {
	this.stackFaceCount = sizeOfV;
}

/**
width * sizeOfU
usually height is same as width

this complete works on plain-cloth
this effect particle size

bugs,no effect on 2 pin circle-mode

if particle ratio is small large sizeOfU make hole easily
 */

private double faceWidthScale=1.0;
public double getScaleOfU() {
	return faceWidthScale;
}
public void setScaleOfU(double scaleOfU) {
	this.faceWidthScale = scaleOfU;
}


/**
 * cut horizontal constraints(joint)
 */
private boolean cutHorizontalConnection;//cutU
public boolean isCutU() {
	return cutHorizontalConnection;
}
public void setCutU(boolean cutU) {
	this.cutHorizontalConnection = cutU;
}

/**
 when start cut 0 means no horizontal connection 
 */
private int startCutUIndexV;
public int getStartCutUIndexV() {
	return startCutUIndexV;
}

public void setStartCutUIndexV(int startCutUIndexV) {
	this.startCutUIndexV = startCutUIndexV;
}

/*
 on the default normal extrude outside(via face-vertex-normal) and this make bottom's face having large width like skirt.
 
 right now only use -1y normal,or not
 */
private boolean useCustomNormal;
public boolean isUseCustomNormal() {
	return useCustomNormal;
}

public void setUseCustomNormal(boolean useCustomNormal) {
	this.useCustomNormal = useCustomNormal;
}

/*
 * ratio to merge to custom-normal
 */
private double originalNormalRatio;
public double getOriginalNormalRatio() {
	return originalNormalRatio;
}

public void setOriginalNormalRatio(double originalNormalRatio) {
	this.originalNormalRatio = originalNormalRatio;
}

/**
 it works on plain-cloth and not so good working.
 
 TODO support ammo-bone-hair
 */
private boolean doNarrow;
public boolean isDoNarrow() {
	return doNarrow;
}
public void setDoNarrow(boolean doNarrow) {
	this.doNarrow = doNarrow;
}

/*
 * when start of stacks(sizeOfV)
 */

private int startNarrowIndexV=1;
public int getStartNarrowIndexV() {
	return startNarrowIndexV;
}

public void setStartNarrowIndexV(int startNarrowIndexV) {
	this.startNarrowIndexV = startNarrowIndexV;
}
/*
 * each time width*narrowScale.
 */
private double narrowScale=0.9;
public double getNarrowScale() {
	return narrowScale;
}
public void setNarrowScale(double narrowScale) {
	this.narrowScale = narrowScale;
}



/**
 * if you feel hair too bouncing,decrese it
 * for plain cloth
 */
private double plainClothDamping= 0.03;
public double getDamping() {
	return plainClothDamping;
}

public void setDamping(double damping) {
	this.plainClothDamping = damping;
}

/**
 * heavy effected by wind,
 * for plain cloth
 */

private double plainClothMass=.1;
public double getMass() {
	return plainClothMass;
}
public void setMass(double mass) {
	this.plainClothMass = mass;
}

/*
 *  to keep cloth/hair natural
 *  
 *  force move when vertex moved.
 * 
 *  ammo use syncMove and syncForceLinear value
 */
private boolean syncMove;//no fake physics;
public boolean isSyncMove() {
	return syncMove;
}

public void setSyncMove(boolean syncMove) {
	this.syncMove = syncMove;
}

/**
 * TODO support bullet
 * only same channel conflict.but ammo some time fail to ignore
 */
private int channel;
public int getChannel() {
	return channel;
}

public void setChannel(int channel) {
	this.channel = channel;
}


/**
 * only work when sync enabled and ammo
 * 
 * when sync enabled bone works boring move.
 * this add extra move force
 * 
 * -10  - 100 works good
 */
private double ammoSyncForceLinear=0;
public double getSyncForceLinear() {
	return ammoSyncForceLinear;
}

public void setSyncForceLinear(double syncForceLinear) {
	this.ammoSyncForceLinear = syncForceLinear;
}


private double ammoSyncMoveLinear=0.5; //seems stable
public double getSyncMoveLinear() {
	return ammoSyncMoveLinear;
}

public void setSyncMoveLinear(double syncMoveLinear) {
	this.ammoSyncMoveLinear = syncMoveLinear;
}


	
//still format modifying
public HairData clone(){
	HairDataConverter converter=new HairDataConverter();
	JSONObject line=converter.convert(this);
	return converter.reverse().convert(line);
}

public void setHairPins(List<HairPin> hairPins) {
	this.hairPins = hairPins;
}



	public static class HairPin{
		/*
		 * when particles created pin is used first row and usize increase first row.
		 * 
		 * however when user set this,this is pin used for specific particle be static-pin
		 * main purpose of this technic is pin first row and last row.
		 * but this really make 
		 */
		private int targetParticleIndex=-1;
		
		
		public int getTargetClothIndex() {
			return targetParticleIndex;
		}
		public void setTargetClothIndex(int targetClothIndex) {
			this.targetParticleIndex = targetClothIndex;
		}
		private int faceIndex;
		public HairPin(int faceIndex, int vertexOfFaceIndex) {
			this(faceIndex,vertexOfFaceIndex,-1);
		}
		public HairPin(int faceIndex, int vertexOfFaceIndex,int targetParticleIndex) {
			super();
			this.faceIndex = faceIndex;
			this.vertexOfFaceIndex = vertexOfFaceIndex;
			this.targetParticleIndex=targetParticleIndex;
		}
		public int getFaceIndex() {
			return faceIndex;
		}
		public void setFaceIndex(int faceIndex) {
			this.faceIndex = faceIndex;
		}
		public int getVertexOfFaceIndex() {
			return vertexOfFaceIndex;
		}
		public void setVertexOfFaceIndex(int vertexOfFaceIndex) {
			this.vertexOfFaceIndex = vertexOfFaceIndex;
		}
		private int vertexOfFaceIndex;//0-2		
		
		
		
		@Override
		public int hashCode() {
			final int prime = 31;
			int result = 1;
			result = prime * result + faceIndex;
			result = prime * result + vertexOfFaceIndex;
			return result;
		}
		@Override
		public boolean equals(Object obj) {
			if (this == obj)
				return true;
			if (obj == null)
				return false;
			if (getClass() != obj.getClass())
				return false;
			HairPin other = (HairPin) obj;
			if (faceIndex != other.faceIndex)
				return false;
			if (vertexOfFaceIndex != other.vertexOfFaceIndex)
				return false;
			return true;
		}
		public String toString(){
			return MoreObjects.toStringHelper("HairPin")
					.add("faceIndex", faceIndex)
					.add("vertexOfFaceIndex", vertexOfFaceIndex)
					.toString();
		}
		public boolean isCustomPin() {
			return targetParticleIndex!=-1;
		}
	}
	
	private HairTextureData hairTextureData=new HairTextureData();
	public HairTextureData getHairTextureData() {
		return hairTextureData;
	}

	public void setHairTextureData(HairTextureData hairTextureData) {
		this.hairTextureData = hairTextureData;
	}
	
	
	/**
	 * 
	 * for Ammo CLOTH and BONE
	 * 
	 * small value move smooth but,slip out easily
	 * 
	 * 0.5 is best.
	 * 
	 * actually value is restdistance * particleRadiusRatio
	 * 
	 */
	/*
	 * 0.5 measn almost max,if more big conflict others
	 */
	private double ammoParticleRadiusRatio=0.5;//for ammo cloth
	
	private double ammoEndParticleRadiusRatio=0;
	
	public double getAmmoEndParticleRadiusRatio() {
		return ammoEndParticleRadiusRatio;
	}

	public void setAmmoEndParticleRadiusRatio(double ammoEndParticleRadiusRatio) {
		this.ammoEndParticleRadiusRatio = ammoEndParticleRadiusRatio;
	}

	public double getParticleRadiusRatio() {
		return ammoParticleRadiusRatio;
	}

	public void setParticleRadiusRatio(double particleRadius) {
		this.ammoParticleRadiusRatio = particleRadius;
	}


	public static final int TYPE_SIMPLE_CLOTH=0;
	public static final int TYPE_AMMO_CLOTH=1;
	public static final int TYPE_AMMO_BONE_CLOTH=2;
	public static final int TYPE_AMMO_BONE_HAIR=3;
	
	/**
	 * what kind physics using
	 * 
	 * TYPE_SIMPLE_CLOTH is three.js cloth(ultra first smooth,but not suite cloth)
	 * 
	 * 
	
	 * 
	 */
	private int hairType;//TODO support hair type
	
	
	public int getHairPhysicsType() {
		return hairType;
	}

	public void setHairPhysicsType(int hairType) {
		this.hairType = hairType;
	}


	/**
	 * for ammo Bone Cloth
	 * 
	 * bone cloth has a thick
	 * 
	 * actually value is restdistance * thickRatio
	 */
	private double ammoBoneThickRatio=0.1;//for ammo cloth thick
	public double getThickRatio() {
		return ammoBoneThickRatio;
	}

	public void setThickRatio(double thick) {
		this.ammoBoneThickRatio = thick;
	}

	/**
	 * used for hair
	 * 0 means same as 2
	 */
	private double ammoBoneThickRatio2=0;
	


	public double getAmmoBoneThickRatio2() {
		return ammoBoneThickRatio2;
	}

	public void setAmmoBoneThickRatio2(double ammoBoneThickRatio2) {
		this.ammoBoneThickRatio2 = ammoBoneThickRatio2;
	}
	/**
	 * how position extend outside.
	 * 
	 * it's better to set 0.01 or 0.1 if data is cloth.
	 * 
	 * 0 mean no extend.exactly same vertex point.
	 * use  restdistance * extendOutsideRatio
	 */
	private double extendOutsideRatio;
	/*
	 * move outside depends on restdistance
	 */
	public double getExtendOutsideRatio() {
		return extendOutsideRatio;
	}

	public void setExtendOutsideRatio(double extendPosition) {
		this.extendOutsideRatio = extendPosition;
	}
	
	private double ammoCircleInRangeRatio=0.5;
	public double getAmmoCircleInRangeRatio() {
		return ammoCircleInRangeRatio;
	}

	public boolean isAmmoInCircleInRange(double angle){
		if(angle>ammoCircleRangeMin && angle<ammoCircleRangeMax){
			return true;
		}
	
		if(angle>ammoCircleRangeMin+360 && angle<ammoCircleRangeMax+360){
			return true;
		}
		return false;
	}
	public boolean isUseAmmoCircleInRange(){
		return ammoCircleRangeMin!=ammoCircleRangeMax;
	}
	public void setAmmoCircleInRangeRatio(double ammoCircleInRangeRatio) {
		this.ammoCircleInRangeRatio = ammoCircleInRangeRatio;
	}
	private double ammoCircleRangeMin;
	public double getAmmoCircleRangeMin() {
		return ammoCircleRangeMin;
	}

	public void setAmmoCircleRangeMin(double ammoCircleRangeMin) {
		this.ammoCircleRangeMin = ammoCircleRangeMin;
	}

	public double getAmmoCircleRangeMax() {
		return ammoCircleRangeMax;
	}

	public void setAmmoCircleRangeMax(double ammoCircleRangeMax) {
		this.ammoCircleRangeMax = ammoCircleRangeMax;
	}
	private double ammoCircleRangeMax;
	private boolean ammoCircleUseFirstPointY;

	public boolean isAmmoCircleUseFirstPointY() {
		return ammoCircleUseFirstPointY;
	}

	public void setAmmoCircleUseFirstPointY(boolean useFirstPointY) {
		this.ammoCircleUseFirstPointY = useFirstPointY;
	}
	
	
	
	private boolean useCustomConstraintData;
	public boolean isUseCustomConstraintData() {
		return useCustomConstraintData;
	}

	public void setUseCustomConstraintData(boolean useCustomConstraintData) {
		this.useCustomConstraintData = useCustomConstraintData;
	}
	private AmmoConstraintPropertyData ammoConstraintData;
	
	public AmmoConstraintPropertyData getAmmoConstraintData() {
		return ammoConstraintData;
	}

	public void setAmmoConstraintData(AmmoConstraintPropertyData ammoConstraintData) {
		this.ammoConstraintData = ammoConstraintData;
	}
	private boolean useCustomBodyParticleData;
	public boolean isUseCustomBodyParticleData() {
		return useCustomBodyParticleData;
	}

	public void setUseCustomBodyParticleData(boolean useCustomBodyParticleData) {
		this.useCustomBodyParticleData = useCustomBodyParticleData;
	}
	private AmmoBodyPropertyData ammoBodyParticleData;

	public AmmoBodyPropertyData getAmmoBodyParticleData() {
		return ammoBodyParticleData;
	}

	public void setAmmoBodyParticleData(AmmoBodyPropertyData ammoBodyParticleData) {
		this.ammoBodyParticleData = ammoBodyParticleData;
	}

	private boolean circleStyle;

	public boolean isCircleStyle() {
		return circleStyle;
	}

	public void setCircleStyle(boolean circleStyle) {
		this.circleStyle = circleStyle;
	}
	
}
