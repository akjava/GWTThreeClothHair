package com.akjava.gwt.clothhair.client.sphere;


import java.io.IOException;
import java.util.List;

import com.akjava.gwt.clothhair.client.SkeletonUtils;
import com.akjava.gwt.clothhair.client.SkeletonUtils.BoneData;
import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.akjava.gwt.three.client.java.ThreeLog;
import com.akjava.gwt.three.client.java.ui.experiments.SimpleEulerEditor;
import com.akjava.gwt.three.client.java.ui.experiments.SimpleEulerEditor.SimpleEulerEditorListener;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.math.Euler;
import com.akjava.gwt.three.client.js.math.Vector4;
import com.akjava.gwt.three.client.js.objects.Skeleton;
import com.google.common.collect.Lists;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SphereDataEditor extends VerticalPanel implements Editor<SphereData>,ValueAwareEditor<SphereData>{

	private LabeledInputRangeWidget2 scaleRange;
	private LabeledInputRangeWidget2 zRange;
	private LabeledInputRangeWidget2 yRange;
	private LabeledInputRangeWidget2 xRange;
	
	private SphereData defaultValue;
	private SphereDataPanel panel;
	private LabeledInputRangeWidget2 heightRange;
	
	public SphereDataEditor(final SphereData defaultValue,SphereDataPanel panel){
		this.defaultValue=defaultValue;
		this.panel=panel;
		
		
		
		TabPanel tab=new TabPanel();
		this.add(tab);
		
		VerticalPanel positionPanel=new VerticalPanel();
		tab.add(positionPanel,"Position");
		tab.selectTab(0);
		
		HorizontalPanel h1=new HorizontalPanel();
		positionPanel.add(h1);
		
		
		
		xRange = new LabeledInputRangeWidget2("x", -.2, .2, .001);
		positionPanel.add(xRange);
		xRange.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				flush();
			}
		});
		xRange.getLabel().setWidth("40px");
		xRange.getRange().setWidth("220px");
		
		RangeButtons xButtons=new RangeButtons(xRange);
		positionPanel.add(xButtons);
		
		yRange = new LabeledInputRangeWidget2("y", defaultValue.getY()-1.2,  defaultValue.getY()+1.2, .001);
		positionPanel.add(yRange);
		yRange.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				flush();
			}
		});
		yRange.getLabel().setWidth("40px");
		yRange.getRange().setWidth("220px");
		
		RangeButtons yButtons=new RangeButtons(yRange);
		positionPanel.add(yButtons);
		
		zRange = new LabeledInputRangeWidget2("z", -.2, .2, .001);
		positionPanel.add(zRange);
		zRange.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				flush();
			}
		});
		
		RangeButtons zButtons=new RangeButtons(zRange);
		positionPanel.add(zButtons);
		
		zRange.getLabel().setWidth("40px");
		zRange.getRange().setWidth("220px");
		
		Button reset=new Button("reset xyz-scale",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				xRange.setValue(defaultValue.getX(),false);
				yRange.setValue( defaultValue.getY(),false);
				zRange.setValue(defaultValue.getZ(),false);
				scaleRange.setValue(defaultValue.getWidth(),true);//call flush here
			}
		});
		
		h1.add(reset);
		
		scaleRange = new LabeledInputRangeWidget2("width", .001, 0.4, .001);
		scaleRange.getLabel().setWidth("40px");
		scaleRange.getRange().setWidth("220px");
		
		positionPanel.add(scaleRange);
		scaleRange.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				flush();
			}
		});
		
		RangeButtons scaleButtons=new RangeButtons(scaleRange);
		positionPanel.add(scaleButtons);
		
		heightRange = new LabeledInputRangeWidget2("height", .001, 0.4, .001);
		heightRange.getLabel().setWidth("40px");
		heightRange.getRange().setWidth("220px");
		
		positionPanel.add(heightRange);
		heightRange.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				flush();
			}
		});
		
		
		VerticalPanel rotatePanel=new VerticalPanel();
		tab.add(rotatePanel,"rotate");
		rotateEditor = new SimpleEulerEditor(new SimpleEulerEditorListener() {
			@Override
			public void onValueChanged(Euler value) {
				flush();
			}
		});
		rotatePanel.add(new Label("Rotate(for box)"));
		rotatePanel.add(rotateEditor);
		
		
		
		boneIndexBox = new ValueListBox<BoneData>(new Renderer<BoneData>() {

			@Override
			public String render(BoneData object) {
				if(object==null){
					return null;
				}
				// TODO Auto-generated method stub
				return object.getName();
			}

			@Override
			public void render(BoneData object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		boneIndexBox.addValueChangeHandler(new ValueChangeHandler<BoneData>() {
			@Override
			public void onValueChange(ValueChangeEvent<BoneData> event) {
				flush();
			}
		});
		
		HorizontalPanel bonePanel=new HorizontalPanel();
		bonePanel.setVerticalAlignment(ALIGN_MIDDLE);
		bonePanel.add(new Label("TargetBone"));
		bonePanel.add(boneIndexBox);
		this.add(bonePanel);
		
		
		HorizontalPanel panel1=new HorizontalPanel();
		panel1.setVerticalAlignment(ALIGN_MIDDLE);
		
		HorizontalPanel typePanel=new HorizontalPanel();
		panel1.add(typePanel);
		typePanel.setVerticalAlignment(ALIGN_MIDDLE);
		typePanel.add(new Label("Type:"));
		
		typeEditor = new ListBox();
		typeEditor.addItem("Sphere");
		typeEditor.addItem("Box");
		typePanel.add(typeEditor);
		typeEditor.addChangeHandler(new ChangeHandler() {
			@Override
			public void onChange(ChangeEvent event) {
				flush();
			}
		});
		
		
		
		
		panel1.add(new Label("Channel for hair"));
		this.add(panel1);
		
		channelBox = new ListBox();
		for(int i=0;i<16;i++){
			channelBox.addItem(String.valueOf(i));
		}
		panel1.add(channelBox);
		channelBox.setSelectedIndex(0);
		channelBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				
				flush();
			}
		});
		
		copyHorizontalCheck = new CheckBox("copy horizontal");
		panel1.add(copyHorizontalCheck);
		copyHorizontalCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
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
			value.setWidth(scaleRange.getValue());
			
			value.setBoneIndex(boneIndexBox.getValue().getIndex());
			
			value.setChannel(channelBox.getSelectedIndex());
			
			value.setCopyHorizontal(copyHorizontalCheck.getValue());
			
			value.setType(typeEditor.getSelectedIndex());
			
			
			value.getRotation().setFromEuler(rotateEditor.getValue());
			
			value.setHeight(heightRange.getValue());
			
			//sync here?
			panel.onFlushed();
		}

		@Override
		public void onPropertyChange(String... paths) {
			// TODO Auto-generated method stub
			
		}

		public static class RangeButtons extends HorizontalPanel{
			private LabeledInputRangeWidget2 range;

			public RangeButtons(final LabeledInputRangeWidget2 range) {
				super();
				this.range = range;
				
				List<Double> values=Lists.newArrayList(0.001,0.01,0.1);
				
				for(int i=values.size()-1;i>=0;i--){
					final Double v=values.get(i);
					Button bt=new Button("-"+String.valueOf(v),new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							double value=range.getValue();
							value-=v;
							if(value<range.getRange().getMin()){
								value=range.getRange().getMin();
							}
							range.setValue(value,true);
						}
					});
					this.add(bt);
				}
				for(final Double v:values){
					Button bt=new Button(String.valueOf(v),new ClickHandler() {
						
						@Override
						public void onClick(ClickEvent event) {
							double value=range.getValue();
							value+=v;
							if(value>range.getRange().getMax()){
								value=range.getRange().getMax();
							}
							range.setValue(value,true);
						}
					});
					this.add(bt);
				}
				
			}
			
		}
		
		SphereData value;
		public SphereData getValue() {
			return value;
		}


		private ValueListBox<BoneData> boneIndexBox;
		private List<BoneData> boneDatas;
		private ListBox channelBox;
		private CheckBox copyHorizontalCheck;
		private ListBox typeEditor;
		private SimpleEulerEditor rotateEditor;
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
				
				rotateEditor.setEnabled(false);
				typeEditor.setEnabled(false);
				
				heightRange.setEnabled(false);
				return;
			}
			
			xRange.setEnabled(true);
			yRange.setEnabled(true);
			zRange.setEnabled(true);
			scaleRange.setEnabled(true);
			rotateEditor.setEnabled(true);
			typeEditor.setEnabled(true);
			heightRange.setEnabled(true);
			
			//no need flush here
			xRange.setValue(value.getX());
			yRange.setValue(value.getY());
			zRange.setValue(value.getZ());
			scaleRange.setValue(value.getWidth());
			
			typeEditor.setSelectedIndex(value.getType());
			rotateEditor.setValue(THREE.Euler().setFromQuaternion(value.getRotation()));
			
			
			BoneData data=null;
			for(BoneData boneData:boneDatas){
				if(value.getBoneIndex()==boneData.getIndex()){
					data=boneData;
				}
			}
			boneIndexBox.setValue(data);
			
			channelBox.setSelectedIndex(value.getChannel());
			
			copyHorizontalCheck.setValue(value.isCopyHorizontal());
			
			heightRange.setValue(value.getHeight());
		}
		
		public static interface SphereUpdateListener{
			public void updateSphere(SphereData data);
		}


		public void setSkelton(Skeleton skeleton) {
			boneDatas=SkeletonUtils.skeltonToBoneData(skeleton);
			boneIndexBox.setValue(boneDatas.get(0));
			boneIndexBox.setAcceptableValues(boneDatas);
		}
}