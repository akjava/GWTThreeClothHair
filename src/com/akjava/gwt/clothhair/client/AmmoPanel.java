package com.akjava.gwt.clothhair.client;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AmmoPanel extends VerticalPanel{
public AmmoPanel(){
	HorizontalPanel ha=new HorizontalPanel();
	this.add(ha);
	ha.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
	CheckBox ammoStopCheck=new CheckBox("stop");
	
	ammoStopCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			GWTThreeClothHair.INSTANCE.getAmmoControler().setStopped(event.getValue());
		}
		
	});
	ha.add(ammoStopCheck);
	Button ammostep=new Button("step",new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			GWTThreeClothHair.INSTANCE.getAmmoControler().getAmmoControler().update();
		}
	});
	ha.add(ammostep);
	
	
	//TODO fix effect new-add cloth
	CheckBox ammoBoneCheck=new CheckBox("bone");
	
	ammoBoneCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			GWTThreeClothHair.INSTANCE.getClothSimulator().getAmmoHairControler().updateVisibleBone(event.getValue());
		}
		
	});
	ha.add(ammoBoneCheck);
	
	
	CheckBox particleBoneCheck=new CheckBox("particle");
	
	particleBoneCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			GWTThreeClothHair.INSTANCE.getClothSimulator().getAmmoHairControler().updateVisibleParticle(event.getValue());
		}
		
	});
	ha.add(particleBoneCheck);
	
	LabeledInputRangeWidget2 worldScale=new LabeledInputRangeWidget2("WorldScale", 0.001, 1, 0.001);
	this.add(worldScale);
	worldScale.setButtonVisible(true);
	
	worldScale.setValue(GWTThreeClothHair.INSTANCE.getAmmoWorldScale());
	LogUtils.log(GWTThreeClothHair.INSTANCE.getAmmoWorldScale());
	worldScale.addtRangeListener(new ValueChangeHandler<Number>() {
		@Override
		public void onValueChange(ValueChangeEvent<Number> event) {
			double value=event.getValue().doubleValue();
			try {
				GWTThreeClothHair.INSTANCE.getStorageControler().setValue(GWTThreeClothHairStorageKeys.KEY_AMMO_WORLD_SCALE, String.valueOf(value));
			} catch (StorageException e) {
				LogUtils.logAndAlert("maybe quote error:"+e.getMessage());
			}
		}
	});
	this.add(new Label("Must reload if modified"));
}
}
