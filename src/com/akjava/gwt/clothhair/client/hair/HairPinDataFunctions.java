package com.akjava.gwt.clothhair.client.hair;

import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.akjava.gwt.three.client.js.core.Face3;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.google.common.base.Function;

public class HairPinDataFunctions {

	
	
	public static class HairPinToVertex implements Function<HairPin,Vector3>{
		private Mesh mesh;
		public HairPinToVertex(Mesh mesh, boolean applyWorldMatrix) {
			super();
			this.mesh = mesh;
			this.applyWorldMatrix = applyWorldMatrix;
		}

		private boolean applyWorldMatrix;
		
		@Override
		public Vector3 apply(HairPin hairPin) {
			Face3 face=mesh.getGeometry().getFaces().get(hairPin.getFaceIndex());
			Vector3 vertex;
			if(hairPin.getVertexOfFaceIndex()==0){
				vertex=mesh.getGeometry().getVertices().get(face.getA());
			}else if(hairPin.getVertexOfFaceIndex()==1){
				vertex=mesh.getGeometry().getVertices().get(face.getB());
			}else{
				vertex=mesh.getGeometry().getVertices().get(face.getC());
			}
			
			if(applyWorldMatrix){
			return vertex.clone().applyMatrix4( mesh.getMatrixWorld());
			}else{
			return vertex.clone();
		}
		
		}
	}
	
	public static class HairPinToNormal implements Function<HairPin,Vector3>{
		private Mesh mesh;
		public HairPinToNormal(Mesh mesh) {
			super();
			this.mesh = mesh;
		}

		private boolean applyWorldMatrix;
		
		@Override
		public Vector3 apply(HairPin hairPin) {
			Face3 face=mesh.getGeometry().getFaces().get(hairPin.getFaceIndex());
			Vector3 normal=face.getVertexNormals().get(hairPin.getVertexOfFaceIndex());
			return normal.clone();
		}
	}
}
