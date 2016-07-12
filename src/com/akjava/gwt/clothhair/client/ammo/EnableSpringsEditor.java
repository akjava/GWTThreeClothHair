package com.akjava.gwt.clothhair.client.ammo;

import java.util.List;

import javax.annotation.Nullable;

import com.akjava.gwt.lib.client.LogUtils;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class EnableSpringsEditor extends VerticalPanel{
	private CheckBox linearX;
	private CheckBox linearY;
	private CheckBox linearZ;

	private List<Boolean> value;
	private CheckBox angularX;
	private CheckBox angularY;
	private CheckBox angularZ;
	
	public void setEnabled(boolean value){
		linearX.setEnabled(value);
		linearY.setEnabled(value);
		linearZ.setEnabled(value);
		angularX.setEnabled(value);
		angularY.setEnabled(value);
		angularZ.setEnabled(value);
	}
	public EnableSpringsEditor(){
		HorizontalPanel linearChecks=new HorizontalPanel();
		linearChecks.setVerticalAlignment(ALIGN_MIDDLE);
		linearChecks.setSpacing(4);
		this.add(linearChecks);
		
		Button all=new Button("All",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				boolean v=linearX.getValue();
				linearX.setValue(!v);
				linearY.setValue(!v);
				linearZ.setValue(!v);
				angularX.setValue(!v);
				angularY.setValue(!v);
				angularZ.setValue(!v);
			}
		});
		linearChecks.add(all);
		
Button linear=new Button("Linear",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				boolean v=linearX.getValue();
				linearX.setValue(!v);
				linearY.setValue(!v);
				linearZ.setValue(!v);
			
			}
		});
		linearChecks.add(linear);
		
		
Button angular=new Button("Angular",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				boolean v=angularX.getValue();
				
				angularX.setValue(!v);
				angularY.setValue(!v);
				angularZ.setValue(!v);
			}
		});
		linearChecks.add(angular);
		
		Label label0=new Label("Linear:");
		linearChecks.add(label0);
		
		Label label1=new Label("x");
		label1.setWidth("10px");
		linearChecks.add(label1);
		linearX = new CheckBox();
		linearChecks.add(linearX);
		
		Label label2=new Label("y");
		label2.setWidth("10px");
		linearChecks.add(label2);
		linearY = new CheckBox();
		linearChecks.add(linearY);
		
		Label label3=new Label("z");
		label3.setWidth("10px");
		linearChecks.add(label3);
		linearZ = new CheckBox();
		linearChecks.add(linearZ);
		

		Label label4=new Label("Angular:");
		linearChecks.add(label4);
		
		Label label5=new Label("x");
		label5.setWidth("10px");
		linearChecks.add(label5);
		angularX = new CheckBox();
		linearChecks.add(angularX);
		
		Label label6=new Label("y");
		label6.setWidth("10px");
		linearChecks.add(label6);
		angularY = new CheckBox();
		linearChecks.add(angularY);
		
		Label label7=new Label("z");
		label7.setWidth("10px");
		linearChecks.add(label7);
		angularZ = new CheckBox();
		linearChecks.add(angularZ);
	}
	
	public void flush(){
		value.set(0, linearX.getValue());
		value.set(1, linearY.getValue());
		value.set(2, linearZ.getValue());
		value.set(3, angularX.getValue());
		value.set(4, angularY.getValue());
		value.set(5, angularZ.getValue());
	}
	
	public List<Boolean> getValue() {
		return value;
	}
	public void setValue(@Nullable List<Boolean> value) {
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
