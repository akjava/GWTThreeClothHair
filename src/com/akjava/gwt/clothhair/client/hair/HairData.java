package com.akjava.gwt.clothhair.client.hair;

import java.util.List;

import com.akjava.gwt.clothhair.client.texture.HairTextureData;
import com.google.common.base.MoreObjects;
import com.google.common.collect.Lists;

public class HairData {
	
private boolean visible;
//not store
public boolean isVisible() {
	return visible;
}

public void setVisible(boolean visible) {
	this.visible = visible;
}


private List<HairPin> hairPins=Lists.newArrayList();

private int sizeOfU=8;	//w
public int getSizeOfU() {
	return sizeOfU;
}

public void setSizeOfU(int sizeOfU) {
	this.sizeOfU = sizeOfU;
}

public int getSizeOfV() {
	return sizeOfV;
}

public void setSizeOfV(int sizeOfV) {
	this.sizeOfV = sizeOfV;
}


private double scaleOfU=1.0;

public double getScaleOfU() {
	return scaleOfU;
}

public void setScaleOfU(double scaleOfU) {
	this.scaleOfU = scaleOfU;
}


private int sizeOfV=8;	//h

private boolean cutU;
public boolean isCutU() {
	return cutU;
}

public void setCutU(boolean cutU) {
	this.cutU = cutU;
}

public int getStartCutUIndexV() {
	return startCutUIndexV;
}

public void setStartCutUIndexV(int startCutUIndexV) {
	this.startCutUIndexV = startCutUIndexV;
}



private int startCutUIndexV;

private boolean doNarrow;
public boolean isDoNarrow() {
	return doNarrow;
}

public void setDoNarrow(boolean doNarrow) {
	this.doNarrow = doNarrow;
}

public int getStartNarrowIndexV() {
	return startNarrowIndexV;
}

public void setStartNarrowIndexV(int startNarrowIndexV) {
	this.startNarrowIndexV = startNarrowIndexV;
}

public double getNarrowScale() {
	return narrowScale;
}

public void setNarrowScale(double narrowScale) {
	this.narrowScale = narrowScale;
}




private int startNarrowIndexV=1;
private double narrowScale=0.9;

public static final int EDGE_NONE=0;
public static final int EDGE_FIRST=1;
public static final int EDGE_CENTEr=2;
public static final int EDGE_LAST=3;
private int edgeMode;
public int getEdgeMode() {
	return edgeMode;
}

public void setEdgeMode(int edgeMode) {
	this.edgeMode = edgeMode;
}

public double getEdgeModeScale() {
	return edgeModeScale;
}

public void setEdgeModeScale(double edgeModeScale) {
	this.edgeModeScale = edgeModeScale;
}



private double edgeModeScale=1.5;

//TODO hair material

	public List<HairPin> getHairPins() {
	return hairPins;
}
	
	
	
private double damping= 0.03;
public double getDamping() {
	return damping;
}

public void setDamping(double damping) {
	this.damping = damping;
}

public double getMass() {
	return mass;
}

public void setMass(double mass) {
	this.mass = mass;
}

public boolean isSyncMove() {
	return syncMove;
}

public void setSyncMove(boolean syncMove) {
	this.syncMove = syncMove;
}

private int channel;//for sphere
public int getChannel() {
	return channel;
}

public void setChannel(int channel) {
	this.channel = channel;
}


private double mass=.1;
private boolean syncMove;//no fake physics;
	
	
//still format fixed
public HairData clone(){
	HairDataConverter converter=new HairDataConverter();
	String line=converter.convert(this);
	return converter.reverse().convert(line);
}

public void setHairPins(List<HairPin> hairPins) {
	this.hairPins = hairPins;
}



	public static class HairPin{
		private int targetClothIndex=-1;
		
		public int getTargetClothIndex() {
			return targetClothIndex;
		}
		public void setTargetClothIndex(int targetClothIndex) {
			this.targetClothIndex = targetClothIndex;
		}
		private int faceIndex;
		public HairPin(int faceIndex, int vertexOfFaceIndex) {
			super();
			this.faceIndex = faceIndex;
			this.vertexOfFaceIndex = vertexOfFaceIndex;
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
			return targetClothIndex!=-1;
		}
	}
	
	private HairTextureData hairTextureData=new HairTextureData();
	public HairTextureData getHairTextureData() {
		return hairTextureData;
	}

	public void setHairTextureData(HairTextureData hairTextureData) {
		this.hairTextureData = hairTextureData;
	}
}
