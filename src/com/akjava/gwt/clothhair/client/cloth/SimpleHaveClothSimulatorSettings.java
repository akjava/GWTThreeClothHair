package com.akjava.gwt.clothhair.client.cloth;

import com.akjava.gwt.threeammo.client.AmmoBodyPropertyData;
import com.akjava.gwt.threeammo.client.AmmoConstraintPropertyData;

public class SimpleHaveClothSimulatorSettings implements HaveClothSimulatorSettings{

	@Override
	public double getAmmoGravity() {
		return -100;
	}

	@Override
	public int getAmmoSubsteps() {
		return 0;
	}

	@Override
	public AmmoBodyPropertyData getAmmoCollisionBodyData() {
		return new AmmoBodyPropertyData();
	}

	@Override
	public AmmoBodyPropertyData getAmmoParticleBodyData() {
		return new AmmoBodyPropertyData();
	}

	@Override
	public AmmoConstraintPropertyData getAmmoParticleConstraintData() {
		return new AmmoConstraintPropertyData();
	}

	@Override
	public double getAmmoWorldScale() {
		return 0.05;//I'm not sure
	}

}
