package com.akjava.gwt.clothhair.client.sphere;

import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.math.Quaternion;
import com.akjava.gwt.three.client.js.math.Vector3;

public class SphereData {
public static final String DATA_TYPE="SphereData";
public static final int TYPE_SPHERE=0;
public static final int TYPE_BOX=1;
public static final int TYPE_CAPSULE=2;

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
	data.setWidth(this.getWidth());
	data.setHeight(this.getHeight());
	data.setDepth(this.getDepth());
	
	data.setEnabled(enabled);
	data.setBoneIndex(boneIndex);
	data.setChannel(channel);
	data.setCopyHorizontal(copyHorizontal);
	
	data.setType(type);
	data.getRotation().copy(rotation);
	return data;
}

public SphereData(double x, double y, double z, double size,boolean enabled,int boneIndex) {
	super();
	position.set(x, y, z);
	this.width = size;
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
public double getWidth() {
	return width;
}
public void setWidth(double size) {
	this.width = size;
}

private double width;
private double height;
public double getHeight() {
	return height;
}

public void setHeight(double height) {
	this.height = height;
}

public double getDepth() {
	return depth;
}

public void setDepth(double depth) {
	this.depth = depth;
}

private double depth;

private Quaternion rotation=THREE.Quaternion();
private int type; //sphere or box
public Quaternion getRotation() {
	return rotation;
}

public void setRotattion(Quaternion rotate) {
	this.rotation = rotate;
}

public int getType() {
	return type;
}

public void setType(int type) {
	this.type = type;
}

public static String getTypeLabel(int type) {
	if(type==TYPE_SPHERE){
		return "Sphere";
	}else if(type==TYPE_BOX){
		return "Box";
	}else if(type==TYPE_CAPSULE){
		return "Capsule";
	}
	return "Unknown";
}

}
