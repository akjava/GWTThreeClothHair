package com.akjava.gwt.clothhair.client.cloth;

import com.akjava.gwt.clothhair.client.SkinningVertexCalculator;
import com.akjava.gwt.clothhair.client.SkinningVertexCalculator.SkinningVertex;
import com.akjava.gwt.clothhair.client.hair.HairData;
import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.akjava.gwt.clothhair.client.hair.HairPinDataFunctions.HairPinToNormal;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Face3;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.math.Vector3;
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
	cloth.setPinAll();
	
	int slices=cloth.w;
	if(cloth.isConnectHorizontal()){
		slices++;
	}
	
	clothGeometry = THREE.ParametricGeometry( cloth.clothFunction, slices, cloth.h );
	clothGeometry.setDynamic(true);
	clothGeometry.computeFaceNormals();
	
	//LogUtils.log("p-size:"+cloth.particles.size());
	//LogUtils.log("g-size:"+clothGeometry.getVertices().length());
	
	
	HairPinToNormal hairPinToNormal=new HairPinToNormal(mesh);
	
	calculator=new SkinningVertexCalculator(mesh);
	for(HairPin pin:hairData.getHairPins()){
		Face3 face=mesh.getGeometry().getFaces().get(pin.getFaceIndex());
		int vertexIndex=face.gwtGet(pin.getVertexOfFaceIndex());
		//TODO make function
		SkinningVertex svertex=calculator.createSkinningVertex(vertexIndex,pin.getTargetClothIndex());
		calculator.add(
				svertex
		);
		//TODO add option
		//if extend outside,extend-ratio
		double extendRatio=hairData.isConnectHorizontal()?0.5:0;//right now connected use Cannon
		Vector3 normal=hairPinToNormal.apply(pin);
		double distance=cloth.getRestDistance()/mesh.getScale().getX()*extendRatio;
		Vector3 appendPos=normal.normalize().multiplyScalar(distance);
		svertex.getVertex().add(appendPos);
		
		//TODO support direct point
	}
}

}
