package com.akjava.gwt.clothhair.client.ammo;

import javax.annotation.Nullable;

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
	private DoubleBox linearBox;
	private DoubleBox angulerBox;
	
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
		Label label5=new Label("linear:");
		label5.setWidth("60px");
		linearPanel.add(label5);
		linearBox = new DoubleBox();
		linearBox.setWidth("60px");
		linearPanel.add(linearBox);
		
		Button linear0=new Button("0",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				linearBox.setValue(0.0);
			}
		});
		linearPanel.add(linear0);
		
		Button linear5=new Button("0.5",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				linearBox.setValue(0.5);
			}
		});
		linearPanel.add(linear5);
		
		Button linear1=new Button("1",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				linearBox.setValue(1.0);
			}
		});
		linearPanel.add(linear1);
		
		
		HorizontalPanel angulerPanel=new HorizontalPanel();
		angulerPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		add(angulerPanel);
		
		Label label6=new Label("anguler:");
		label6.setWidth("60px");
		angulerPanel.add(label6);
		angulerBox = new DoubleBox();
		angulerBox.setWidth("60px");
		angulerPanel.add(angulerBox);
		
		
		Button anguler0=new Button("0",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				angulerBox.setValue(0.0);
			}
		});
		angulerPanel.add(anguler0);
		
		Button anguler5=new Button("0.5",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				angulerBox.setValue(0.5);
			}
		});
		angulerPanel.add(anguler5);
		
		Button anguler1=new Button("1",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				angulerBox.setValue(1.0);
			}
		});
		angulerPanel.add(anguler1);
		
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
