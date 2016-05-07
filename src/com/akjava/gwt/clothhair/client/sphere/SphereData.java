package com.akjava.gwt.clothhair.client.sphere;

import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.math.Vector3;

public class SphereData {
private Vector3 position=THREE.Vector3();
private boolean enabled;
public boolean isEnabled() {
	return enabled;
}

public void setEnabled(boolean enabled) {
	this.enabled = enabled;
}

public Vector3 getPosition() {
	return position;
}

public SphereData clone(){
	return new SphereData(position.getX(),position.getY(), position.getZ(), size, enabled);
}

public SphereData(double x, double y, double z, double size,boolean enabled) {
	super();
	position.set(x, y, z);
	this.size = size;
	this.enabled=enabled;
}

public double getX() {
	return position.getX();
}
public void setX(double x) {
	position.setX(x);
}
public double getY() {
	return position.getY();
}
public void setY(double y) {
	position.setY(y);
}
public double getZ() {
	return position.getZ();
}
public void setZ(double z) {
	position.setZ(z);
}
public double getSize() {
	return size;
}
public void setSize(double size) {
	this.size = size;
}

private double size;
}
