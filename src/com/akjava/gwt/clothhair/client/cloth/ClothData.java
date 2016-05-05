package com.akjava.gwt.clothhair.client.cloth;

import com.akjava.gwt.clothhair.client.HairData;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.objects.Mesh;

public class ClothData {
private HairCloth cloth;
public HairCloth getCloth() {
	return cloth;
}
public void setCloth(HairCloth cloth) {
	this.cloth = cloth;
}
public Geometry getClothGeometry() {
	return clothGeometry;
}
public void setClothGeometry(Geometry clothGeometry) {
	this.clothGeometry = clothGeometry;
}
private Geometry clothGeometry;

public ClothData(HairData hairData,Mesh mesh){
	cloth=new HairCloth(hairData,mesh);
	cloth.wind=false;
	cloth.pins=cloth.pinsFormation.get(4);//first and last
	
	clothGeometry = THREE.ParametricGeometry( cloth.clothFunction, cloth.w, cloth.h );
	clothGeometry.setDynamic(true);
	clothGeometry.computeFaceNormals();
}

}
