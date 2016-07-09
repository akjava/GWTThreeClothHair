package com.akjava.gwt.clothhair.client.cloth;

import static com.google.common.base.Preconditions.checkArgument;

import java.util.List;

import com.akjava.gwt.lib.client.JavaScriptUtils;
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.gwt.extras.geometries.ExtrudeGeometryParameter;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.extras.core.Shape;
import com.akjava.gwt.three.client.js.extras.curves.CatmullRomCurve3;
import com.akjava.gwt.three.client.js.extras.geometries.ExtrudeGeometry;
import com.akjava.gwt.three.client.js.math.Vector2;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.google.gwt.core.client.JsArray;

public class HairGeometryCreator {
	private double horizontalThick=0.5;
	private double verticalThick=0.5;
	
	public HairGeometryCreator horizontalThick(double v){
		horizontalThick=v;
		return this;
	}
	public HairGeometryCreator verticalThick(double v){
		verticalThick=v;
		return this;
	}
	public JsArray<Geometry> createGeometry(List<Vector3> positions,int slices){
		checkArgument(positions.size()>1,"HairGeometryCreator:need atleast 2 points");
		
		JsArray<Geometry> geometries=JavaScriptUtils.createJSArray();
		
		
		int horizontalVertexCount=slices+1;
		int verticalVertexCount=positions.size()/horizontalVertexCount;
		
		double distance=positions.get(0).distanceTo(positions.get(1));
		
		JsArray<Vector2> pts=JavaScriptUtils.createJSArray();
		pts.push(THREE.Vector2(distance*horizontalThick,-distance*verticalThick));
		pts.push(THREE.Vector2(distance*horizontalThick,distance*verticalThick));
		pts.push(THREE.Vector2(-distance*horizontalThick,distance*verticalThick));
		pts.push(THREE.Vector2(-distance*horizontalThick,-distance*verticalThick));
		
		Shape shape=THREE.Shape(pts);
		
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
			geometries.push(geometry);
		}
		return geometries;
	}
	
	public static Geometry merge(JsArray<Geometry> geometries){
		for(int i=1;i<geometries.length();i++){
			geometries.get(0).merge(geometries.get(i));
		}
		return geometries.get(0);
	}
}
