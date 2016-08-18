package com.akjava.gwt.clothhair.client.cloth;

import static com.google.common.base.Preconditions.checkNotNull;

import com.akjava.gwt.clothhair.client.SkinningVertexCalculator;
import com.akjava.gwt.clothhair.client.SkinningVertexCalculator.SkinningVertex;
import com.akjava.gwt.clothhair.client.hair.HairData;
import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.akjava.gwt.clothhair.client.hair.HairPinDataFunctions.HairPinToNormal;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.java.ThreeLog;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Face3;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.math.Vector4;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;
/*
 * contain HairCloth and Mesh
 * 
 */
public class ClothData {
private HairCloth cloth;
private Mesh clothMesh;

public Mesh getClothMesh() {
	return clothMesh;
}
public void setClothMesh(Mesh clothMesh) {
	this.clothMesh = clothMesh;
}
public HairCloth getHairCloth() {
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
	
	int slices=cloth.getW();
	if(cloth.isConnectHorizontal()){
		slices++;
	}
	
	clothGeometry = THREE.ParametricGeometry( cloth.clothFunction, slices, cloth.h );
	clothGeometry.setDynamic(true);
	clothGeometry.computeFaceNormals();
	
	//LogUtils.log("p-size:"+cloth.particles.size());
	//LogUtils.log("g-size:"+clothGeometry.getVertices().length());
	
	
	//SkinningVertex no need apply matrix,
	HairPinToNormal hairPinToNormalFunction=new HairPinToNormal(mesh,false);
	
	double characterScale=mesh.getScale().getX();//not support rotate & move
	
	calculator=new SkinningVertexCalculator(mesh);
	if(hairData.getPointMode()==HairData.POINT_MODE_SEMI_AUTO){
		checkNotNull(hairData.getSemiAutoPoints(),"semi auto points is null");
		checkNotNull(hairData.getSemiAutoPins(),"semi auto pins is null");
		
		for(int i=0;i<hairData.getSemiAutoPins().length();i++){
			int index=(int)hairData.getSemiAutoPins().get(i);
			Vector3 pos=hairData.getSemiAutoPoints().get(index).clone();
			pos.divideScalar(characterScale);
			
			
			if(hairData.getHairPhysicsType() == HairData.TYPE_AMMO_BONE_BODY){
				String suffix="breast-";//TODO add hairData
				//extream special
				
				String rootName=suffix+"root";
				int ammoBoneBodyOffset=-1;
				//TODO make method
				for(int j=0;j<mesh.getGeometry().getBones().length();j++){
					if(mesh.getGeometry().getBones().get(j).getName().equals(rootName)){
						ammoBoneBodyOffset=j;
						break;
					}
				}
				if(ammoBoneBodyOffset==-1){
					LogUtils.log("invalid-bone-index");
					return;
				}
				//ammoBoneBodyOffset possible move
				int parent=mesh.getGeometry().getBones().get(ammoBoneBodyOffset).getParent();
				
				Vector4 skinIndices =THREE.Vector4(parent, 0, 0, 0);
				Vector4 skinWeights =THREE.Vector4(1,0,0,0);
				
				SkinningVertex svertex=new SkinningVertex(pos, skinIndices, skinWeights);
				calculator.add(svertex);
			}else{
			
			int closed=getClosedVertex(mesh.getGeometry(),pos);
			
			
			
			Vector4 skinIndices =mesh.getGeometry().getSkinIndices().get(closed);
			Vector4 skinWeights =mesh.getGeometry().getSkinWeights().get(closed);
			SkinningVertex svertex=new SkinningVertex(pos, skinIndices, skinWeights);
			//LogUtils.log("closed:"+closed+",index="+index+",pos="+ThreeLog.get(pos));
			
			calculator.add(
					svertex
			);
			}
		}
		
		
	}else{
	for(HairPin pin:hairData.getHairPins()){
		Face3 face=mesh.getGeometry().getFaces().get(pin.getFaceIndex());
		int vertexIndex=face.gwtGet(pin.getVertexOfFaceIndex());
		//TODO make function
		SkinningVertex svertex=calculator.createSkinningVertex(vertexIndex,pin.getTargetClothIndex());
		calculator.add(
				svertex
		);
		
		double extendRatio=hairData.getExtendOutsideRatio();
		Vector3 normal=hairPinToNormalFunction.apply(pin);
		double distance=cloth.getRestDistance()/characterScale*extendRatio;
		Vector3 appendPos=normal.normalize().multiplyScalar(distance);
		svertex.getVertex().add(appendPos);
		
		//TODO support direct point
	}
	}
}

public static int getClosedVertex(Geometry geometry,Vector3 point){
	int index=0;
	double distance=geometry.getVertices().get(0).distanceTo(point);
	for(int i=1;i<geometry.getVertices().length();i++){
		double d=geometry.getVertices().get(i).distanceTo(point);
		if(d<distance){
			index=i;
			distance=d;
		}
	}
	return index;
}

}
