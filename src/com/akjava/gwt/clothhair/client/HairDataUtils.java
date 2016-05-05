package com.akjava.gwt.clothhair.client;

import java.util.List;

import com.akjava.gwt.clothhair.client.HairDataFunctions.HairPinToVertex;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.google.common.collect.FluentIterable;

public class HairDataUtils {

	
	public static double getTotalVDistance(double uDistance,int uSize,int vSize){
		return (double)vSize/uSize*uDistance;
	}
	
	public static double getTotalPinDistance(HairData hairData,Mesh mesh){
		double distance=0;
		HairPinToVertex hairPinToVertex= new HairPinToVertex(mesh,true);
		List<Vector3> vecs=FluentIterable.from(hairData.getHairPins()).transform(hairPinToVertex).toList();
		for(int i=0;i<vecs.size()-1;i++){
			distance+=vecs.get(i).distanceTo(vecs.get(i+1));
		}
		return distance;
	}
}
