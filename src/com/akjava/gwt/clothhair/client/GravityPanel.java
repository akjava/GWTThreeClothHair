package com.akjava.gwt.clothhair.client;

import com.akjava.gwt.clothhair.client.cloth.ClothControler;
import com.akjava.gwt.clothhair.client.cloth.ClothData;
import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;


/*
 * controlling gravity hard than expected,should re-add better
 * 
 * but i think at least need mass
 */
/**
 * @deprecated no need anymore
 * @author aki
 *
 */
public class GravityPanel extends VerticalPanel{

	

	private double damping;
	private double mass;
	private double gravity;
	private ClothControler controls;
	public GravityPanel(final ClothControler controls) {
		super();
		this.controls=controls;
		//this.add(new Label("Gravity"));
		final LabeledInputRangeWidget2 dampingRange=new LabeledInputRangeWidget2("DAMPING", 0.000, 0.06, 0.001);
		dampingRange.getLabel().setWidth("40px");
		dampingRange.getRange().setWidth("220px");
		this.add(dampingRange);
		dampingRange.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				damping=event.getValue().doubleValue();
				updateGravity();
			}
		});
		final LabeledInputRangeWidget2 massRange=new LabeledInputRangeWidget2("MASS",0.001,1, 0.001);
		massRange.getLabel().setWidth("40px");
		massRange.getRange().setWidth("220px");
		this.add(massRange);
		massRange.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				mass=event.getValue().doubleValue();
				updateGravity();
			}
		});
		final LabeledInputRangeWidget2 gravityRange=new LabeledInputRangeWidget2("GRAVITY", 1000, 2000, 0.1);
		gravityRange.getLabel().setWidth("40px");
		gravityRange.getRange().setWidth("220px");
		this.add(gravityRange);
		gravityRange.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				gravity=event.getValue().doubleValue();
				updateGravity();
			}
		});
		
		dampingRange.setValue(0.03);
		massRange.setValue(.1);
		gravityRange.setValue(981 * 1.4);
		
		//setvalue
		dampingRange.setValue(0.03);
		massRange.setValue(.1);
		gravityRange.setValue(981 * 1.4);
		
		
		HorizontalPanel buttons=new HorizontalPanel();
		this.add(buttons);
		
		
		
		Button resetBt=new Button("Reset",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				dampingRange.setValue(0.03);
				massRange.setValue(.1);
				gravityRange.setValue(981 * 1.4);
			}
		});
		buttons.add(resetBt);
		

		
	}
	
	protected void updateGravity() {
		
		for(ClothData data:controls.getCloths()){
			data.getCloth().initGravity(mass,damping, gravity);
		}
	}
	
}
