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
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LinearLimitEditor extends VerticalPanel{
	
	private Vector3 value;
	private DoubleBox xBox;
	private DoubleBox yBox;
	private DoubleBox zBox;
	private ListBox targetListBox;
	
	public void setEnabled(boolean value){

		xBox.setEnabled(value);
		yBox.setEnabled(value);
		zBox.setEnabled(value);
	}
	
	public void setValueByTarget(double value){
		int selection=targetListBox.getSelectedIndex();
		if(selection==0){
			setValueAll(value);
		}else if(selection==1){
			xBox.setValue(value);
		}else if(selection==2){
			yBox.setValue(value);
		}else if(selection==3){
			zBox.setValue(value);
		}
	}
	
	public LinearLimitEditor(){
		HorizontalPanel panel0=new HorizontalPanel();
		panel0.setVerticalAlignment(ALIGN_MIDDLE);
		panel0.setSpacing(4);
		this.add(panel0);
		
		targetListBox = new ListBox();
		panel0.add(targetListBox);
		targetListBox.addItem("All");
		targetListBox.addItem("X");
		targetListBox.addItem("Y");
		targetListBox.addItem("Z");
		targetListBox.setSelectedIndex(0);
		
		
		Button mpi1=new Button("-2",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=-2;
				
				setValueByTarget(v);
			}
		});
		panel0.add(mpi1);
		
		Button mpi2=new Button("-1",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=-1;
				
				setValueByTarget(v);
			}
		});
		panel0.add(mpi2);
		
		Button mpi4=new Button("-0.5",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=-0.5;
				
				setValueByTarget(v);
			}
		});
		panel0.add(mpi4);
		
		Button mpi5=new Button("-0.25",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=-0.25;
				
				setValueByTarget(v);
			}
		});
		panel0.add(mpi5);
		
		Button mhalf2a=new Button("-0.1",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=-0.1;
				
				setValueByTarget(v);
			}
		});
		panel0.add(mhalf2a);
		
		
		Button zero=new Button("0",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=0;
				
				setValueByTarget(v);
			}
		});
		panel0.add(zero);
		
		Button half2a=new Button("0.1",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=0.1;
				
				setValueByTarget(v);
			}
		});
		panel0.add(half2a);
		

		Button half2=new Button("0.25",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=0.25;
				
				setValueByTarget(v);
			}
		});
		panel0.add(half2);
		
		
		Button half=new Button("0.5",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=0.5;
				
				setValueByTarget(v);
			}
		});
		panel0.add(half);
		
		Button max=new Button("1",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=1;
				
				setValueByTarget(v);
			}
		});
		panel0.add(max);
		
		Button pi=new Button("2",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=2;
				
				setValueByTarget(v);
			}
		});
		panel0.add(pi);
		
		
		
		
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
		
		panel1.add(new Label("Set All"));
		
		final DoubleBox customBox=new DoubleBox();
		customBox.setWidth("60px");
		panel1.add(customBox);
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
		panel1.add(update);
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
