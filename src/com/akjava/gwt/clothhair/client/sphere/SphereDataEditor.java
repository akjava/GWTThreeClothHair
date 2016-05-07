package com.akjava.gwt.clothhair.client.sphere;


import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SphereDataEditor extends VerticalPanel implements Editor<SphereData>,ValueAwareEditor<SphereData>{

	private LabeledInputRangeWidget2 scaleRange;
	private LabeledInputRangeWidget2 zRange;
	private LabeledInputRangeWidget2 yRange;
	private LabeledInputRangeWidget2 xRange;
	
	private SphereData defaultValue;
	private SphereDataPanel panel;
	
	public SphereDataEditor(final SphereData defaultValue,SphereDataPanel panel){
		this.defaultValue=defaultValue;
		this.panel=panel;
		HorizontalPanel h1=new HorizontalPanel();
		this.add(h1);
		xRange = new LabeledInputRangeWidget2("x", -200, 200, 1);
		this.add(xRange);
		xRange.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				flush();
			}
		});
		xRange.getLabel().setWidth("40px");
		xRange.getRange().setWidth("220px");
		
		
		yRange = new LabeledInputRangeWidget2("y", defaultValue.getY()-200,  defaultValue.getY()+200, 1);
		this.add(yRange);
		yRange.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				flush();
			}
		});
		yRange.getLabel().setWidth("40px");
		yRange.getRange().setWidth("220px");
		
		zRange = new LabeledInputRangeWidget2("z", -200, 200, 1);
		this.add(zRange);
		zRange.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				flush();
			}
		});
		zRange.getLabel().setWidth("40px");
		zRange.getRange().setWidth("220px");
		
		Button reset=new Button("reset xyz-scale",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				xRange.setValue(0,false);
				yRange.setValue( defaultValue.getY(),false);
				zRange.setValue(0,false);
				scaleRange.setValue(100,true);//call flush here
			}
		});
		
		h1.add(reset);
		
		scaleRange = new LabeledInputRangeWidget2("scale", 1, 200, 1);
		scaleRange.getLabel().setWidth("40px");
		scaleRange.getRange().setWidth("220px");
		
		this.add(scaleRange);
		scaleRange.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				flush();
			}
		});
	}
@Override
		public void setDelegate(EditorDelegate<SphereData> delegate) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void flush() {
			if(value==null){
				return;
			}
			value.setX(xRange.getValue());
			value.setY(yRange.getValue());
			value.setZ(zRange.getValue());
			value.setSize(scaleRange.getValue());
			//sync here?
			panel.onFlushed();
		}

		@Override
		public void onPropertyChange(String... paths) {
			// TODO Auto-generated method stub
			
		}

		SphereData value;
		@Override
		public void setValue(SphereData value) {
			this.value=value;
			if(value==null){
				xRange.setValue(0);
				yRange.setValue(0);
				zRange.setValue(0);
				scaleRange.setValue(0);
				
				xRange.setEnabled(false);
				yRange.setEnabled(false);
				zRange.setEnabled(false);
				scaleRange.setEnabled(false);
				return;
			}
			
			xRange.setEnabled(true);
			yRange.setEnabled(true);
			zRange.setEnabled(true);
			scaleRange.setEnabled(true);
			
			//no need flush here
			xRange.setValue(value.getX());
			yRange.setValue(value.getY());
			zRange.setValue(value.getZ());
			scaleRange.setValue(value.getSize());
		}
		
		public static interface SphereUpdateListener{
			public void updateSphere(SphereData data);
		}
}