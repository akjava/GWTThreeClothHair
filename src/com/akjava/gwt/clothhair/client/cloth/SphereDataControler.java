package com.akjava.gwt.clothhair.client.cloth;

import javax.annotation.Nullable;

import com.akjava.gwt.clothhair.client.sphere.SphereData;

public  interface SphereDataControler{
	public void removeSphereData(SphereData data);
	public void addSphereData(SphereData data);
	public void onSelectSphere(@Nullable SphereData data);
}