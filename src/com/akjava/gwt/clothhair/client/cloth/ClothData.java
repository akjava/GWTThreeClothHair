package com.akjava.gwt.clothhair.client.cloth;

import com.akjava.gwt.three.client.java.ThreeLog;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.math.Vector2;

public class ClothData {
private Cloth2 cloth;
public Cloth2 getCloth() {
	return cloth;
}
public void setCloth(Cloth2 cloth) {
	this.cloth = cloth;
}
public Geometry getClothGeometry() {
	return clothGeometry;
}
public void setClothGeometry(Geometry clothGeometry) {
	this.clothGeometry = clothGeometry;
}
private Geometry clothGeometry;

public ClothData(int sliceX,int sliceY,double geometryW,double geometryH){
	cloth=new Cloth2(sliceX,sliceY,geometryW, geometryH);
	cloth.wind=false;
	cloth.pins=cloth.pinsFormation.get(4);//first and last
	
	clothGeometry = THREE.ParametricGeometry( cloth.clothFunction, cloth.w, cloth.h );
	clothGeometry.setDynamic(true);
	clothGeometry.computeFaceNormals();
}

}
