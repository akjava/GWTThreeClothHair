package com.akjava.gwt.clothhair.client.cloth;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import com.akjava.gwt.lib.client.JavaScriptUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.gwt.boneanimation.AnimationBone;
import com.akjava.gwt.three.client.gwt.extras.geometries.ExtrudeGeometryParameter;
import com.akjava.gwt.three.client.java.bone.SimpleAutoWeight;
import com.akjava.gwt.three.client.java.bone.WeightResult;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.extras.core.Shape;
import com.akjava.gwt.three.client.js.extras.curves.CatmullRomCurve3;
import com.akjava.gwt.three.client.js.extras.geometries.ExtrudeGeometry;
import com.akjava.gwt.three.client.js.math.Vector2;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.JsArray;

public class HairGeometryCreator {
	private double horizontalThick=0.5;
	private double verticalThick=0.5;
	
	private boolean mergeFirstEdge=true;
	private boolean mergeLastEdge=true;
	public HairGeometryCreator horizontalThick(double v){
		horizontalThick=v;
		return this;
	}
	public HairGeometryCreator verticalThick(double v){
		verticalThick=v;
		return this;
	}
	
	private List<List<Integer>> bonesEnableIndexList;
	private JsArray<AnimationBone> bones;
	public HairGeometryCreator bonesList(JsArray<AnimationBone> bones,List<List<Integer>> bonesEnableIndexList){
		this.bones=bones;
		this.bonesEnableIndexList=bonesEnableIndexList;
		return this;
	}
	
	public JsArray<Geometry> createGeometry(List<Vector3> positions,int slices){
		checkArgument(positions.size()>1,"HairGeometryCreator:need atleast 2 points");
		checkArgument(positions.size()>slices,"need at least slices");
		JsArray<Geometry> geometries=JavaScriptUtils.createJSArray();
		
		//LogUtils.log("HairGeometryCreator:createGeometry() vthick="+verticalThick+",hthick="+horizontalThick);
		
		
		int horizontalVertexCount=slices+1;
		int verticalVertexCount=positions.size()/horizontalVertexCount;
		
		double distance=0;
		if(positions.size()<horizontalVertexCount){
			distance=positions.get(0).distanceTo(positions.get(1));
		}else{
			distance=positions.get(0).distanceTo(positions.get(horizontalVertexCount));
		}
		JsArray<Vector2> pts=JavaScriptUtils.createJSArray();
		pts.push(THREE.Vector2(distance*horizontalThick,-distance*verticalThick));
		pts.push(THREE.Vector2(distance*horizontalThick,distance*verticalThick));
		pts.push(THREE.Vector2(-distance*horizontalThick,distance*verticalThick));
		pts.push(THREE.Vector2(-distance*horizontalThick,-distance*verticalThick));
		
		Shape shape=THREE.Shape(pts);
		int lastVertexSize=shape.getPoints(12).length();//default curve
		
		Vector3 centerPos=THREE.Vector3();
		if(mergeFirstEdge){
		for(int i=0;i<horizontalVertexCount;i++){
			centerPos.add(positions.get(i));
		}
		centerPos.divideScalar(horizontalVertexCount);
		}
		
		for(int i=0;i<horizontalVertexCount;i++){
			JsArray<Vector3> poses=JavaScriptUtils.createJSArray();
			
			for(int j=0;j<verticalVertexCount;j++){
				int ind=(horizontalVertexCount)*j+i;
				Vector3 position=positions.get(ind);
				poses.push(position);
			}
			CatmullRomCurve3 closedSpline = THREE.CatmullRomCurve3(poses);
			ExtrudeGeometryParameter options=GWTParamUtils.ExtrudeGeometry().steps(verticalVertexCount).extrudePath(closedSpline);
			
			
					
					
			ExtrudeGeometry geometry=THREE.ExtrudeGeometry(shape, options);
			//merge last
			//LogUtils.log("last-size:"+lastVertexSize+",total="+geometry.getVertices().length());
			
			if(mergeLastEdge){
			Vector3 pos=THREE.Vector3();
			for(int j=0;j<lastVertexSize;j++){
				int at=geometry.getVertices().length()-1-j;
				pos.add(geometry.getVertices().get(at));
			}
			pos.divideScalar(lastVertexSize);
			
			for(int j=0;j<lastVertexSize;j++){
				int at=geometry.getVertices().length()-1-j;
				geometry.getVertices().get(at).copy(pos);
			}
			}
			
			if(mergeFirstEdge){
				/*
				 * TODO make face?
				 */
				/*
				Vector3 pos=THREE.Vector3();
				
				
				for(int j=0;j<lastVertexSize;j++){
					int at=geometry.getVertices().length()-1-j;
					geometry.getVertices().get(at).copy(pos);
				}
				*/
				
				}
			if(bonesEnableIndexList!=null && bonesEnableIndexList.size()==horizontalVertexCount){
				WeightResult result=new SimpleAutoWeight(1).enableBones(bonesEnableIndexList.get(i)).autoWeight(geometry, bones);//ignore root
				result.insertToGeometry(geometry);
				
			}
			
			geometries.push(geometry);
		}
		
		
		
		
		
		return geometries;
	}
	
	public static Geometry merge(JsArray<Geometry> geometries){
		for(int i=1;i<geometries.length();i++){
			geometries.get(0).gwtMergeWithSkinIndicesAndWeights(geometries.get(i));
		}
		return geometries.get(0);
	}
}
