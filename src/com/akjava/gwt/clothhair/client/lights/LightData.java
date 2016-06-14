package com.akjava.gwt.clothhair.client.lights;

import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.lights.Light;
import com.akjava.gwt.three.client.js.math.Vector3;

public class LightData {
public static final int DIRECTIONAL=0;
public static final int AMBIENT=1;
public static final int HEMISPHERE=2;
private String name="";
private Light light;

public boolean hasLight(){
	return light!=null;
}
public Light getLight() {
	return light;
}
public void setLight(Light light) {
	this.light = light;
}
public String getName() {
	return name;
}
public void setName(String name) {
	this.name = name;
}
public int getType() {
	return type;
}
public void setType(int type) {
	this.type = type;
}
public int getColor() {
	return color;
}
public void setColor(int color) {
	this.color = color;
}
public int getColor2() {
	return color2;
}
public void setColor2(int color2) {
	this.color2 = color2;
}
public double getIntensity() {
	return intensity;
}
public void setIntensity(double intensity) {
	this.intensity = intensity;
}
public Vector3 getPosition() {
	return position;
}
public void setPosition(Vector3 position) {
	this.position = position;
}
private int type;
private int color;
private int color2;
private double intensity;
private Vector3 position=THREE.Vector3();
private boolean castShadow;

public boolean isCastShadow() {
	return castShadow;
}
public void setCastShadow(boolean castShadow) {
	this.castShadow = castShadow;
}
}
