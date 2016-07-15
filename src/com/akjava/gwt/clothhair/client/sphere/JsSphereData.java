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

/**
 * @deprecated
 * @return
 */
public final  native double getRadius()/*-{
return this.radius;
}-*/;
/**
 * @deprecated
 * @return
 */
public final  native void setRadius(double param)/*-{
this.radius=param;
}-*/;

public final  native int getBoneIndex()/*-{
return this.boneIndex;
}-*/;

public final  native void setBoneIndex(int  param)/*-{
this.boneIndex=param;
}-*/;

public final  native double getWidth()/*-{
return this.width;
}-*/;

public final  native void setWidth(double param)/*-{
this.width=param;
}-*/;

public final  native double getHeight()/*-{
return this.height;
}-*/;

public final  native void setHeight(double param)/*-{
this.height=param;
}-*/;

public final  native double getDepth()/*-{
return this.depth;
}-*/;

public final  native void setDepth(double param)/*-{
this.depth=param;
}-*/;
}
