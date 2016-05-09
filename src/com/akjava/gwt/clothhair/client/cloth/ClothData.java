package com.akjava.gwt.clothhair.client.cloth;

import com.akjava.gwt.clothhair.client.HairData;
import com.akjava.gwt.clothhair.client.HairData.HairPin;
import com.akjava.gwt.clothhair.client.SkinningVertexCalculator;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Face3;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;

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

private SkinningVertexCalculator calculator;

public SkinningVertexCalculator getCalculator() {
	return calculator;
}
public ClothData(HairData hairData,SkinnedMesh mesh){
	cloth=new HairCloth(hairData,mesh);
	cloth.wind=false;
	cloth.pins=cloth.pinsFormation.get(4);//first and last
	
	clothGeometry = THREE.ParametricGeometry( cloth.clothFunction, cloth.w, cloth.h );
	clothGeometry.setDynamic(true);
	clothGeometry.computeFaceNormals();
	
	calculator=new SkinningVertexCalculator(mesh);
	for(HairPin pin:hairData.getHairPins()){
		Face3 face=mesh.getGeometry().getFaces().get(pin.getFaceIndex());
		int vertexIndex=face.gwtGet(pin.getVertexOfFaceIndex());
		calculator.addByVertexIndex(vertexIndex);
	}
}

}
