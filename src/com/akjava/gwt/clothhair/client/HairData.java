package com.akjava.gwt.clothhair.client;

import java.util.List;

import com.google.common.collect.Lists;

public class HairData {
private List<HairPin> hairPins=Lists.newArrayList();

private int heightBlockSize;

	public List<HairPin> getHairPins() {
	return hairPins;
}

public void setHairPins(List<HairPin> hairPins) {
	this.hairPins = hairPins;
}

public int getHeightBlockSize() {
	return heightBlockSize;
}

public void setHeightBlockSize(int heightBlockSize) {
	this.heightBlockSize = heightBlockSize;
}

	public static class HairPin{
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
	}
}
