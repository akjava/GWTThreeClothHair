package com.akjava.gwt.clothhair.client.texture;

import com.google.common.base.MoreObjects;

public class HairPatternData {

	public static final int STRAIGHT=0;
	public static final int OVAL=1;
	public static final int SHARP=2;
	public static final int SOFT=3;
	public static final int TRAPEZOID=4;
	public static final int TRAPEZOID2=5;
	public static final int TRAPEZOID3=6;
	//private static final int TRAPEZOID4=7;
	public static final int TRIANGLE=7; //maybe no need
	public static final int TRIANGLE2=8;
	
	public static final int TRAPEZOID_STRAIGHT=9;
	public static final int TRAPEZOID2STRAIGHT=10;
	public static final int TRAPEZOID3STRAIGHT=11;
	
	public static final int CURVE=12;
	public static final int CURVE2=13;
	public static final int CURVE3=14;
	public static final int CURVE4=15;
	
	public static final int RCURVE=16;
	public static final int RCURVE2=17;
	public static final int RCURVE3=18;
	public static final int RCURVE4=19;
	
	public static final int LR_AUTO=0;
	public static final int LR_LEFT=1;
	public static final int LR_RIGHT=2;
	
public int getSlices() {
	return slices;
}

public void setSlices(int slices) {
	this.slices = slices;
}

public int getLrMode() {
	return lrMode;
}

public void setLrMode(int lrMode) {
	this.lrMode = lrMode;
}

public boolean isUseCenter() {
	return useCenter;
}

public void setUseCenter(boolean useCenter) {
	this.useCenter = useCenter;
}

public boolean isEvenCenter() {
	return evenCenter;
}

public void setEvenCenter(boolean evenCenter) {
	this.evenCenter = evenCenter;
}

public int getExtendCenter() {
	return extendCenter;
}

public void setExtendCenter(int extendCenter) {
	this.extendCenter = extendCenter;
}

public boolean isStroke() {
	return stroke;
}

public void setStroke(boolean stroke) {
	this.stroke = stroke;
}

public int getStrokeGrayscale() {
	return strokeGrayscale;
}

public void setStrokeGrayscale(int strokeGrayscale) {
	this.strokeGrayscale = strokeGrayscale;
}

public DrawPatternData getDefaultPatternData() {
	return defaultPatternData;
}

public void setDefaultPatternData(DrawPatternData defaultPatternData) {
	this.defaultPatternData = defaultPatternData;
}

public DrawPatternData getCenterPatternData() {
	return centerPatternData;
}

public void setCenterPatternData(DrawPatternData centerPatternData) {
	this.centerPatternData = centerPatternData;
}
private int slices=1;
private int lrMode;
private boolean useCenter;
private boolean evenCenter;
private int extendCenter;
private boolean stroke;
private int strokeGrayscale;

private DrawPatternData defaultPatternData=new DrawPatternData();
private DrawPatternData centerPatternData=new DrawPatternData();


public String toString(){
	return MoreObjects.toStringHelper("HairPatternData")
.add("slices", slices)
			.add("lrMode", lrMode)
			.add("useCenter", useCenter)
			.add("evenCenter", evenCenter)
			.add("extendCenter", extendCenter)
			.add("stroke", stroke)
			.add("strokeGrayscale", strokeGrayscale)
			.add("defaultPatternData", defaultPatternData)
			.add("centerPatternData", centerPatternData)
			.toString();
}
public static class DrawPatternData{
	 private int mode;
	 public int getMode() {
		return mode;
	}
	public void setMode(int mode) {
		this.mode = mode;
	}
	public int getSplitVertical() {
		return splitVertical;
	}
	public void setSplitVertical(int splitVertical) {
		this.splitVertical = splitVertical;
	}
	public int getStartVertical() {
		return startVertical;
	}
	public void setStartVertical(int startVertical) {
		this.startVertical = startVertical;
	}
	public int getEndVertical() {
		return endVertical;
	}
	public void setEndVertical(int endVertical) {
		this.endVertical = endVertical;
	}
	private int splitVertical=1;
	 private int startVertical;
	 private int endVertical;
	 
	 public String toString(){
			return MoreObjects.toStringHelper("DrawPatternData")
		.add("mode", mode)
					.add("splitVertical", splitVertical)
					.add("startVertical", startVertical)
					.add("endVertical", endVertical)
					.toString();
		}
	 
}

}
