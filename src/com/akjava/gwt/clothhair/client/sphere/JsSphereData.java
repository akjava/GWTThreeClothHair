package com.akjava.gwt.clothhair.client.sphere;

import com.akjava.gwt.three.client.js.math.Quaternion;
import com.google.gwt.core.client.JavaScriptObject;

public class JsSphereData extends JavaScriptObject{
protected JsSphereData(){}

public static final  native JsSphereData create()/*-{
return {};
}-*/;

//SphereData type
public final  native int getType()/*-{
return this.type;
}-*/;

public final  native void setType(int  param)/*-{
this.type=param;
}-*/;
public final  native Quaternion getRotate()/*-{
return this.rotate;
}-*/;

public final  native void setRotate(Quaternion  param)/*-{
this.rotate=param;
}-*/;

public final  native double getRadius()/*-{
return this.radius;
}-*/;

public final  native void setRadius(double param)/*-{
this.radius=param;
}-*/;

public final  native int getBoneIndex()/*-{
return this.boneIndex;
}-*/;

public final  native void setBoneIndex(int  param)/*-{
this.boneIndex=param;
}-*/;
}
