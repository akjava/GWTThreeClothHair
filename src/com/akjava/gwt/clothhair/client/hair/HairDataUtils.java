package com.akjava.gwt.clothhair.client.hair;

import java.util.List;

import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.akjava.gwt.clothhair.client.hair.HairDataFunctions.HairPinToVertex;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.google.common.collect.FluentIterable;

public class HairDataUtils {

	
	public static double getTotalVDistance(double uDistance,int uSize,int vSize){
		return (double)vSize/(double)uSize*uDistance;
	}
	
	public static double getTotalPinDistance(List<HairPin> pins,Mesh mesh,boolean applymatrix){
		double distance=0;
		HairPinToVertex hairPinToVertex= new HairPinToVertex(mesh,applymatrix);
		List<Vector3> vecs=FluentIterable.from(pins).filter(HairPinPredicates.NoTargetOnly()).transform(hairPinToVertex).toList();
		for(int i=0;i<vecs.size()-1;i++){
			double d=vecs.get(i).distanceTo(vecs.get(i+1));
			distance+=d;
		}
		return distance;
	}
	
	
	
	public static double getTotalPinDistance(HairData hairData,Mesh mesh,boolean applymatrix){
		
		return getTotalPinDistance(hairData.getHairPins(),mesh,applymatrix);
	}
}
