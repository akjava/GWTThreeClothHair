package com.akjava.gwt.clothhair.client.cloth;

import com.akjava.gwt.three.client.js.math.Vector3;

public class GroundYFloor implements FloorModifier{
private double floor;
	public GroundYFloor(double floor) {
	super();
	this.floor = floor;
}
	@Override
	public void modifyFloor(Vector3 position) {
		if(position.getY()<floor){
			position.setY(floor);
		}
	}
}
