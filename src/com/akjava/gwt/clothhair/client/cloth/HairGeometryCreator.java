package com.akjava.gwt.clothhair.client.cloth;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import com.akjava.gwt.lib.client.JavaScriptUtils;
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
import com.akjava.gwt.three.client.js.math.Matrix4;
import com.akjava.gwt.three.client.js.math.Vector2;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.google.gwt.core.client.JsArray;

public class HairGeometryCreator {
	private double horizontalThick=0.5;
	private double verticalThick=0.5;
	
	private int dummyHairCount;
	private double dummyHairAngle=1;
	public HairGeometryCreator  dummyHairCount(int count){
		dummyHairCount=count;
		return this;
	}
	public HairGeometryCreator dummyHairAngle(double angle){
		dummyHairAngle=angle;
		return this;
	}
	/**
	 * for remove hole when not start center
	 */
	private boolean mergeFirstCenter=false;
	public HairGeometryCreator mergeFirstCenter(boolean value){
		mergeFirstCenter=value;
		return this;
	}
	private boolean mergeLastVertex=true;
	public HairGeometryCreator mergeLastVertex(boolean value){
		mergeLastVertex=value;
		return this;
	}
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
		//if(mergeFirstCenter){
		for(int i=0;i<horizontalVertexCount;i++){
			centerPos.add(positions.get(i));
		}
		centerPos.divideScalar(horizontalVertexCount);
		//}
		
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
			
			if(mergeLastVertex){
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
			
			if(mergeFirstCenter){
				/*
				 * TODO make face?
				 */
				
				
				
				for(int j=0;j<lastVertexSize;j++){
					geometry.getVertices().get(j).copy(centerPos);
				}
				
				
				
				}
			if(bonesEnableIndexList!=null && bonesEnableIndexList.size()==horizontalVertexCount){
				WeightResult result=new SimpleAutoWeight(1).enableBones(bonesEnableIndexList.get(i)).autoWeight(geometry, bones);//ignore root
				result.insertToGeometry(geometry);	
			}
			
			
			
			Geometry dummyBase=geometry.clone();
			geometry.gwtHardCopyToWeightsAndIndices(dummyBase);
			
			for(int j=1;j<=dummyHairCount;j++){
				for(int l=-1;l<=1;l++){
					if(l==0){
						continue;
					}
				
				double angleRad=Math.toRadians(dummyHairAngle*l*j);
				Geometry dummy=dummyBase.clone();
				//dummyBase.gwtHardCopyToWeightsAndIndices(dummy);
				
				for(int k=0;k<dummy.getVertices().length();k++){
					Vector3 vertex=dummy.getVertices().get(k);
					vertex.sub(centerPos).applyMatrix4(THREE.Matrix4().makeRotationY(angleRad)).add(centerPos);
					
				}
				
				if(bonesEnableIndexList!=null && bonesEnableIndexList.size()==horizontalVertexCount){
					WeightResult result=new SimpleAutoWeight(1).enableBones(bonesEnableIndexList.get(i)).autoWeight(dummy, bones);//ignore root
					result.insertToGeometry(dummy);	
				}
				
				
				geometry.gwtMergeWithSkinIndicesAndWeights(dummy);
			}
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
