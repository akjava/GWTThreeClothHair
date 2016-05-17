package com.akjava.gwt.clothhair.client.texture;

import com.google.common.base.MoreObjects;

public class HairTextureData {
public static final double INITIAL_OPACITY=1;
public static final double INITIAL_ALPHATEST=0.5;
public static final int INITIAL_COLOR=0xb7a9cd;//purple for my custom-model

private HairPatternData hairPatternData=new HairPatternData();

public HairPatternData getHairPatternData() {
	return hairPatternData;
}
public void setHairPatternData(HairPatternData hairPatternData) {
	this.hairPatternData = hairPatternData;
}

//TODO later
private double opacity=INITIAL_OPACITY;
public double getOpacity() {
	return opacity;
}
public void setOpacity(double opacity) {
	this.opacity = opacity;
}
public double getAlphaTest() {
	return alphaTest;
}
public void setAlphaTest(double alphaTest) {
	this.alphaTest = alphaTest;
}
public int getColor() {
	return color;
}
public void setColor(int color) {
	this.color = color;
}

private double alphaTest=INITIAL_ALPHATEST;
private int color=INITIAL_COLOR;

private int specular;
private int shininess;

private boolean useLocalColor;
private boolean useImage;
private String imageName="";

private boolean enablePatternImage;

public boolean isEnablePatternImage() {
	return enablePatternImage;
}
public void setEnablePatternImage(boolean enablePatternImage) {
	this.enablePatternImage = enablePatternImage;
}
public String toString(){
	return MoreObjects.toStringHelper("HairTextureData")
			.add("color", color)
			.add("opacity", opacity)
			.add("alphaText", alphaTest)
			.add("enablePatternImage", enablePatternImage)
			.toString();
}

}
