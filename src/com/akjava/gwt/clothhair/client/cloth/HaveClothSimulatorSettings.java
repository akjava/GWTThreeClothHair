package com.akjava.gwt.clothhair.client.cloth;

import com.akjava.gwt.threeammo.client.AmmoBodyPropertyData;
import com.akjava.gwt.threeammo.client.AmmoConstraintPropertyData;

public interface HaveClothSimulatorSettings {

	public double getAmmoGravity();
	public int getAmmoSubsteps();
	public double getAmmoWorldScale();
	public AmmoBodyPropertyData getAmmoCollisionBodyData();
	public AmmoBodyPropertyData getAmmoParticleBodyData();
	public AmmoConstraintPropertyData getAmmoParticleConstraintData();
}
