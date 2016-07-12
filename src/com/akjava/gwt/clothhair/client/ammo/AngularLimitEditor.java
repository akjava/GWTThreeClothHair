package com.akjava.gwt.clothhair.client.ammo;

import javax.annotation.Nullable;

import com.akjava.gwt.three.client.js.math.Vector3;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class AngularLimitEditor extends VerticalPanel{
	
	private Vector3 value;
	private DoubleBox xBox;
	private DoubleBox yBox;
	private DoubleBox zBox;
	
	public void setEnabled(boolean value){

		xBox.setEnabled(value);
		yBox.setEnabled(value);
		zBox.setEnabled(value);
	}
	public AngularLimitEditor(){
		HorizontalPanel panel0=new HorizontalPanel();
		panel0.setVerticalAlignment(ALIGN_MIDDLE);
		panel0.setSpacing(4);
		this.add(panel0);
		
		
		Button mpi1=new Button("-PI",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=-Math.PI;
				
				setValueAll(v);
			}
		});
		panel0.add(mpi1);
		
		Button mpi2=new Button("-PI/2",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=-Math.PI/2;
				
				setValueAll(v);
			}
		});
		panel0.add(mpi2);
		
		Button mpi4=new Button("-PI/4",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=-Math.PI/4;
				
				setValueAll(v);
			}
		});
		panel0.add(mpi4);
		
		Button mpi8=new Button("-PI/8",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=-Math.PI/8;
				
				setValueAll(v);
			}
		});
		panel0.add(mpi8);
		
		Button zero=new Button("0",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=0;
				
				setValueAll(v);
			}
		});
		panel0.add(zero);
		
		Button pi8=new Button("PI/8",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=Math.PI/8;
				
				setValueAll(v);
			}
		});
		panel0.add(pi8);
		
		Button half=new Button("PI/4",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=Math.PI/4;
				
				setValueAll(v);
			}
		});
		panel0.add(half);
		
		Button max=new Button("PI/2",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=Math.PI/2;
				
				setValueAll(v);
			}
		});
		panel0.add(max);
		
		Button pi=new Button("PI",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=Math.PI;
				
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
		//panel0.add(update);
		
		
		HorizontalPanel panel1=new HorizontalPanel();
		panel1.setVerticalAlignment(ALIGN_MIDDLE);
		panel1.setSpacing(4);
		this.add(panel1);
		
		
		
		

		Label label4=new Label("Angular:");
		//panel1.add(label4);
		
		Label label5=new Label("x");
		label5.setWidth("10px");
		panel1.add(label5);
		xBox = new DoubleBox();
		xBox.setWidth("60px");
		panel1.add(xBox);
		
		Label label6=new Label("y");
		label6.setWidth("10px");
		panel1.add(label6);
		yBox = new DoubleBox();
		yBox.setWidth("60px");
		panel1.add(yBox);
		
		Label label7=new Label("z");
		label7.setWidth("10px");
		panel1.add(label7);
		zBox = new DoubleBox();
		zBox.setWidth("60px");
		panel1.add(zBox);
	}
	
	private void setValueAll(double v){
		xBox.setValue(v);
		yBox.setValue(v);
		zBox.setValue(v);
	}
	
	public void flush(){
		
		value.setX(xBox.getValue());
		value.setY(yBox.getValue());
		value.setZ(zBox.getValue());
	}
	
	public Vector3 getValue() {
		return value;
	}
	public void setValue(@Nullable Vector3 value) {
		this.value=value;
		if(value==null){
			return;
		}
		
		xBox.setValue(value.getX());
		yBox.setValue(value.getY());
		zBox.setValue(value.getZ());
	}
}
