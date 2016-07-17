package com.akjava.gwt.clothhair.client.ammo;

import java.util.List;

import javax.annotation.Nullable;

import com.akjava.gwt.lib.client.LogUtils;
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

public class StiffnessesEditor extends VerticalPanel{
	private DoubleBox linearX;
	private DoubleBox linearY;
	private DoubleBox linearZ;

	private List<Double> value;
	private DoubleBox angularX;
	private DoubleBox angularY;
	private DoubleBox angularZ;
	
	public void setEnabled(boolean value){
		linearX.setEnabled(value);
		linearY.setEnabled(value);
		linearZ.setEnabled(value);
		angularX.setEnabled(value);
		angularY.setEnabled(value);
		angularZ.setEnabled(value);
	}
	public StiffnessesEditor(){
		HorizontalPanel panel0=new HorizontalPanel();
		panel0.setVerticalAlignment(ALIGN_MIDDLE);
		panel0.setSpacing(4);
		this.add(panel0);
		
		panel0.add(new Label("set"));
		final ListBox targetBox=new ListBox();
		targetBox.addItem("All");
		targetBox.addItem("Linear");
		targetBox.addItem("Angular");
		panel0.add(targetBox);
		targetBox.setSelectedIndex(0);
		
		Button zero=new Button("0",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=0;
				int target=targetBox.getSelectedIndex();
				setValue(target,v);
			}
		});
		panel0.add(zero);
		
		Button half=new Button("0.5",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=0.5;
				int target=targetBox.getSelectedIndex();
				setValue(target,v);
			}
		});
		panel0.add(half);
		
		Button max=new Button("1",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=1;
				int target=targetBox.getSelectedIndex();
				setValue(target,v);
			}
		});
		panel0.add(max);
		
		Button max2=new Button("10",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=10;
				int target=targetBox.getSelectedIndex();
				setValue(target,v);
			}
		});
		panel0.add(max2);
		
		Button max3=new Button("100",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=100;
				int target=targetBox.getSelectedIndex();
				setValue(target,v);
			}
		});
		panel0.add(max3);
		
		Button max4=new Button("1000",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				double v=1000;
				int target=targetBox.getSelectedIndex();
				setValue(target,v);
			}
		});
		panel0.add(max4);
		
		final DoubleBox customBox=new DoubleBox();
		customBox.setWidth("40px");
		panel0.add(customBox);
		customBox.addValueChangeHandler(new ValueChangeHandler<Double>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				double v=event.getValue();
				int target=targetBox.getSelectedIndex();
				setValue(target,v);
			}
		});
Button update=new Button("Update",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				setValue(targetBox.getSelectedIndex(),customBox.getValue());
			}
		});
		panel0.add(update);
		
		
		
		HorizontalPanel panel1=new HorizontalPanel();
		panel1.setVerticalAlignment(ALIGN_MIDDLE);
		panel1.setSpacing(4);
		this.add(panel1);
		
		
		
		Label label0=new Label("Linear:");
		panel1.add(label0);
		
		Label label1=new Label("x");
		label1.setWidth("10px");
		panel1.add(label1);
		linearX = new DoubleBox();
		panel1.add(linearX);
		linearX.setWidth("40px");
		
		Label label2=new Label("y");
		label2.setWidth("10px");
		panel1.add(label2);
		linearY = new DoubleBox();
		linearY.setWidth("40px");
		panel1.add(linearY);
		
		Label label3=new Label("z");
		label3.setWidth("10px");
		panel1.add(label3);
		linearZ = new DoubleBox();
		linearZ.setWidth("40px");
		panel1.add(linearZ);
		

		Label label4=new Label("Angular:");
		panel1.add(label4);
		
		Label label5=new Label("x");
		label5.setWidth("10px");
		panel1.add(label5);
		angularX = new DoubleBox();
		angularX.setWidth("40px");
		panel1.add(angularX);
		
		Label label6=new Label("y");
		label6.setWidth("10px");
		panel1.add(label6);
		angularY = new DoubleBox();
		angularY.setWidth("40px");
		panel1.add(angularY);
		
		Label label7=new Label("z");
		label7.setWidth("10px");
		panel1.add(label7);
		angularZ = new DoubleBox();
		angularZ.setWidth("40px");
		panel1.add(angularZ);
	}
	
	private void setValue(int target,double v){
		if(target==0 ||target==1){
			linearX.setValue(v);
			linearY.setValue(v);
			linearZ.setValue(v);
		}
		if(target==0||target==2){
			angularX.setValue(v);
			angularY.setValue(v);
			angularZ.setValue(v);
		}
	}
	
	public void flush(){
		value.set(0, linearX.getValue());
		value.set(1, linearY.getValue());
		value.set(2, linearZ.getValue());
		value.set(3, angularX.getValue());
		value.set(4, angularY.getValue());
		value.set(5, angularZ.getValue());
	}
	
	public List<Double> getValue() {
		return value;
	}
	public void setValue(@Nullable List<Double> value) {
		this.value=value;
		if(value==null){
			return;
		}
		if(value.size()!=6){
			LogUtils.log("invalid-size:"+value.size());
			return;
		}
		
		linearX.setValue(value.get(0));
		linearY.setValue(value.get(1));
		linearZ.setValue(value.get(2));
		angularX.setValue(value.get(3));
		angularY.setValue(value.get(4));
		angularZ.setValue(value.get(5));
	}
}
