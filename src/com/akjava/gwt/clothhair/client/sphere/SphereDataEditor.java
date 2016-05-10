package com.akjava.gwt.clothhair.client.sphere;


import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.akjava.gwt.three.client.js.objects.Skeleton;
import com.google.common.collect.Lists;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ValueListBox;
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
		xRange = new LabeledInputRangeWidget2("x", -.2, .2, .001);
		this.add(xRange);
		xRange.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				flush();
			}
		});
		xRange.getLabel().setWidth("40px");
		xRange.getRange().setWidth("220px");
		
		
		yRange = new LabeledInputRangeWidget2("y", defaultValue.getY()-.4,  defaultValue.getY()+.2, .001);
		this.add(yRange);
		yRange.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				flush();
			}
		});
		yRange.getLabel().setWidth("40px");
		yRange.getRange().setWidth("220px");
		
		RangeButtons yButtons=new RangeButtons(yRange);
		this.add(yButtons);
		
		zRange = new LabeledInputRangeWidget2("z", -.2, .2, .001);
		this.add(zRange);
		zRange.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				flush();
			}
		});
		
		RangeButtons zButtons=new RangeButtons(zRange);
		this.add(zButtons);
		
		zRange.getLabel().setWidth("40px");
		zRange.getRange().setWidth("220px");
		
		Button reset=new Button("reset xyz-scale",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				xRange.setValue(defaultValue.getX(),false);
				yRange.setValue( defaultValue.getY(),false);
				zRange.setValue(defaultValue.getZ(),false);
				scaleRange.setValue(defaultValue.getSize(),true);//call flush here
			}
		});
		
		h1.add(reset);
		
		scaleRange = new LabeledInputRangeWidget2("scale", .001, 0.2, .001);
		scaleRange.getLabel().setWidth("40px");
		scaleRange.getRange().setWidth("220px");
		
		this.add(scaleRange);
		scaleRange.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				flush();
			}
		});
		
		boneIndexBox = new ValueListBox<SphereDataEditor.BoneData>(new Renderer<BoneData>() {

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
		boneIndexBox.addValueChangeHandler(new ValueChangeHandler<SphereDataEditor.BoneData>() {
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
	}
	
	public static class BoneData {
		private String name;
		public BoneData(String name, int index) {
			super();
			this.name = name;
			this.index = index;
		}
		public String getName() {
			return name;
		}
		public void setName(String name) {
			this.name = name;
		}
		public int getIndex() {
			return index;
		}
		public void setIndex(int index) {
			this.index = index;
		}
		private int index;
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
			
			value.setBoneIndex(boneIndexBox.getValue().getIndex());
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
			
			BoneData data=null;
			for(BoneData boneData:boneDatas){
				if(value.getBoneIndex()==boneData.getIndex()){
					data=boneData;
				}
			}
			boneIndexBox.setValue(data);
			
		}
		
		public static interface SphereUpdateListener{
			public void updateSphere(SphereData data);
		}


		public void setSkelton(Skeleton skeleton) {
			checkNotNull(skeleton,"SphereDataEditor:need skeleton");
			checkArgument(skeleton.getBones().length()>0,"SphereDataEditor:need atleast one bone");
			boneDatas = Lists.newArrayList();
			for(int i=0;i<skeleton.getBones().length();i++){
				boneDatas.add(new BoneData(skeleton.getBones().get(i).getName(), i));
			}
			boneIndexBox.setValue(boneDatas.get(0));
			boneIndexBox.setAcceptableValues(boneDatas);
		}
}