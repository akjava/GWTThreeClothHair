package com.akjava.gwt.clothhair.client.ammo;

import javax.annotation.Nullable;

import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.akjava.gwt.three.client.js.math.Vector2;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BodyDampingEditor extends VerticalPanel{
	
	private Vector2 value;
	private LabeledInputRangeWidget2 linearBox;
	private LabeledInputRangeWidget2 angulerBox;
	
	public void setEnabled(boolean value){
		linearBox.setEnabled(value);
		angulerBox.setEnabled(value);
	}
	public BodyDampingEditor(){
		
		/*
		HorizontalPanel panel0=new HorizontalPanel();
		panel0.setVerticalAlignment(ALIGN_MIDDLE);
		panel0.setSpacing(4);
		this.add(panel0);
		
		
		Button mpi1=new Button("-2",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=-2;
				
				setValueAll(v);
			}
		});
		panel0.add(mpi1);
		
		Button mpi2=new Button("-1",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=-1;
				
				setValueAll(v);
			}
		});
		panel0.add(mpi2);
		
		Button mpi4=new Button("-0.5",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=-0.5;
				
				setValueAll(v);
			}
		});
		panel0.add(mpi4);
		
		Button mpi5=new Button("-0.25",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=-0.25;
				
				setValueAll(v);
			}
		});
		panel0.add(mpi5);
		
		
		Button zero=new Button("0",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=0;
				
				setValueAll(v);
			}
		});
		panel0.add(zero);
		

		Button half2=new Button("0.25",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=0.25;
				
				setValueAll(v);
			}
		});
		panel0.add(half2);
		
		
		Button half=new Button("0.5",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=0.5;
				
				setValueAll(v);
			}
		});
		panel0.add(half);
		
		Button max=new Button("1",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=1;
				
				setValueAll(v);
			}
		});
		panel0.add(max);
		
		Button pi=new Button("2",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=2;
				
				setValueAll(v);
			}
		});
		panel0.add(pi);
		
		final DoubleBox customBox=new DoubleBox();
		customBox.setWidth("60px");
		panel0.add(customBox);
		customBox.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				double v=event.getValue();
				
				setValueAll(v);
			}
		});
		
		Button update=new Button("Update",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				setValueAll(customBox.getValue());
			}
		});
		panel0.add(update);
		
		
		HorizontalPanel panel1=new HorizontalPanel();
		panel1.setVerticalAlignment(ALIGN_MIDDLE);
		panel1.setSpacing(4);
		this.add(panel1);
		
		*/
		
		

		HorizontalPanel linearPanel=new HorizontalPanel();
		linearPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		add(linearPanel);
		
		linearBox = new LabeledInputRangeWidget2("Linear:",0,1,0.01);
		linearBox.getLabel().setWidth("90px");
		linearBox.setButtonVisible(true);
		linearBox.setWidth("60px");
		linearPanel.add(linearBox);
		
		
		
		HorizontalPanel angulerPanel=new HorizontalPanel();
		angulerPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		add(angulerPanel);
		
		
		angulerBox = new LabeledInputRangeWidget2("Angular:",0,1,0.01);
		angulerBox.getLabel().setWidth("90px");
		angulerBox.setButtonVisible(true);
		angulerPanel.add(angulerBox);
		
		
	
		
	}
	
	
	public void flush(){
		value.setX(linearBox.getValue());
		value.setY(angulerBox.getValue());
	}
	
	public Vector2 getValue() {
		return value;
	}
	public void setValue(@Nullable Vector2 value) {
		this.value=value;
		if(value==null){
			return;
		}
		
		linearBox.setValue(value.getX());
		angulerBox.setValue(value.getY());
	}
}
