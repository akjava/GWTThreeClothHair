package com.akjava.gwt.clothhair.client;

import com.akjava.gwt.clothhair.client.ammo.BodyDataConverter;
import com.akjava.gwt.clothhair.client.ammo.BodyDataEditor;
import com.akjava.gwt.clothhair.client.ammo.ConstraintDataConverter;
import com.akjava.gwt.clothhair.client.ammo.ConstraintDataEditor;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.akjava.gwt.threeammo.client.AmmoBodyPropertyData;
import com.akjava.gwt.threeammo.client.AmmoConstraintPropertyData;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
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
	
	TabPanel tab=new TabPanel();
	tab.setWidth("100%");
	this.add(tab);
	
	VerticalPanel particleBodyPanel=new VerticalPanel();
	tab.add(particleBodyPanel,"Particle Body");
	
	
	final BodyDataEditor particleBodyEditor=new BodyDataEditor();
	particleBodyPanel.add(particleBodyEditor);
	
	particleBodyEditor.setValue(GWTThreeClothHair.INSTANCE.getAmmoParticleBodyData());
	
	HorizontalPanel p1=new HorizontalPanel();
	p1.setSpacing(2);
	particleBodyPanel.add(p1);
	Button particleSave=new Button("Save",new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			particleBodyEditor.flush();
			AmmoBodyPropertyData data=particleBodyEditor.getValue();
			String dataText=new BodyDataConverter().convert(data);
			try {
				GWTThreeClothHair.INSTANCE.getStorageControler().setValue(GWTThreeClothHairStorageKeys.KEY_AMMO_PARTICLE_BODY,dataText);
			} catch (StorageException e) {
				LogUtils.logAndAlert("maybe quote error:"+e.getMessage());
			}
			
			GWTThreeClothHair.INSTANCE.getClothSimulator().getAmmoHairControler().setParticleBodyData(data);
			
		}
	});
	p1.add(particleSave);
	p1.add(new Label("need readd apply changes"));
	//set value
	
	VerticalPanel particleConstraintPanel=new VerticalPanel();
	tab.add(particleConstraintPanel,"Particle Constraint");
	
	final ConstraintDataEditor particleConstraintEditor=new ConstraintDataEditor();
	particleConstraintPanel.add(particleConstraintEditor);
	
	particleConstraintEditor.setValue(GWTThreeClothHair.INSTANCE.getAmmoParticleConstraintData());
	
	HorizontalPanel p2=new HorizontalPanel();
	p2.setSpacing(2);
	particleConstraintPanel.add(p2);
	Button particleConstraintSave=new Button("Save",new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			particleConstraintEditor.flush();
			AmmoConstraintPropertyData data=particleConstraintEditor.getValue();
			String dataText=new ConstraintDataConverter().convert(data);
			try {
				GWTThreeClothHair.INSTANCE.getStorageControler().setValue(GWTThreeClothHairStorageKeys.KEY_AMMO_PARTICLE_CONSTRAINT,dataText);
			} catch (StorageException e) {
				LogUtils.logAndAlert("maybe quote error:"+e.getMessage());
			}
			
			GWTThreeClothHair.INSTANCE.getClothSimulator().getAmmoHairControler().setParticleConstraintData(data);
			
		}
	});
	p2.add(particleConstraintSave);
	p2.add(new Label("need readd apply changes"));
	
	VerticalPanel particleCollisionPanel=new VerticalPanel();
	tab.add(particleCollisionPanel,"Collision Body");
	
	
	final BodyDataEditor collisionBodyEditor=new BodyDataEditor();
	particleCollisionPanel.add(collisionBodyEditor);
	
	collisionBodyEditor.setValue(GWTThreeClothHair.INSTANCE.getAmmoCollisionBodyData());
	
	HorizontalPanel p4=new HorizontalPanel();
	p4.setSpacing(2);
	particleCollisionPanel.add(p4);
	Button collsionSave=new Button("Save",new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			collisionBodyEditor.flush();
			AmmoBodyPropertyData data=collisionBodyEditor.getValue();
			String dataText=new BodyDataConverter().convert(data);
			try {
				GWTThreeClothHair.INSTANCE.getStorageControler().setValue(GWTThreeClothHairStorageKeys.KEY_AMMO_COLLISION_BODY,dataText);
			} catch (StorageException e) {
				LogUtils.logAndAlert("maybe quote error:"+e.getMessage());
			}
			
			GWTThreeClothHair.INSTANCE.getClothSimulator().getAmmoHairControler().setCollisionProperties(data);
			
		}
	});
	p4.add(collsionSave);
	p4.add(new Label("need readd apply changes"));
	
	
	
	tab.selectTab(0);
	tab.addSelectionHandler(new SelectionHandler<Integer>() {
		
		@Override
		public void onSelection(SelectionEvent<Integer> event) {
			GWTThreeClothHair.INSTANCE.updateGUI();
		}
	});
}
}
