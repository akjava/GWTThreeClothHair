package com.akjava.gwt.clothhair.client.sphere;

import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.math.Quaternion;
import com.akjava.gwt.three.client.js.math.Vector3;

public class SphereData {
public static final int TYPE_SPHERE=0;
public static final int TYPE_BOX=1;
	
private Vector3 position=THREE.Vector3();
private boolean enabled;
private boolean copyHorizontal;
public boolean isCopyHorizontal() {
	return copyHorizontal;
}

public void setCopyHorizontal(boolean copyHorizontal) {
	this.copyHorizontal = copyHorizontal;
}

private int channel;
public int getChannel() {
	return channel;
}

public void setChannel(int channel) {
	this.channel = channel;
}

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
	return copyTo(new SphereData());
}

public SphereData copyTo(SphereData data){
	data.set(this.getX(),this.getY(),this.getZ());
	data.setSize(this.getSize());
	data.setEnabled(enabled);
	data.setBoneIndex(boneIndex);
	data.setChannel(channel);
	data.setCopyHorizontal(copyHorizontal);
	
	return data;
}

public SphereData(double x, double y, double z, double size,boolean enabled,int boneIndex) {
	super();
	position.set(x, y, z);
	this.size = size;
	this.enabled=enabled;
	this.boneIndex=boneIndex;
}
public SphereData(){
	super();
}

private int boneIndex;

public int getBoneIndex() {
	return boneIndex;
}

public void setBoneIndex(int boneIndex) {
	this.boneIndex = boneIndex;
}
public void set(double x,double y,double z){
	position.set(x, y, z);
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

private Quaternion rotate=THREE.Quaternion();
private int type; //sphere or box
public Quaternion getRotate() {
	return rotate;
}

public void setRotate(Quaternion rotate) {
	this.rotate = rotate;
}

public int getType() {
	return type;
}

public void setType(int type) {
	this.type = type;
}

}
