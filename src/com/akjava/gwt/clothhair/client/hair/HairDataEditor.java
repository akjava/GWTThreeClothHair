package com.akjava.gwt.clothhair.client.hair;

import java.util.List;

import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.editor.client.adapters.SimpleEditor;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HairDataEditor extends VerticalPanel implements Editor<HairData>,ValueAwareEditor<HairData>{

		private HairData value;
		private SimpleEditor<List<HairPin>> hairPinEditor;
		private LabeledInputRangeWidget2 uSize;
		private LabeledInputRangeWidget2 vSize;
		private CheckBox cutCheck;
		private LabeledInputRangeWidget2 cutIndex;
		private LabeledInputRangeWidget2 scaleOfU;
		private CheckBox narrowCheck;
		private LabeledInputRangeWidget2 narrowIndex;
		private LabeledInputRangeWidget2 narrowScale;
		private LabeledInputRangeWidget2 edgeScale;
		private ListBox edgeMode;
		private ListBox channelBox;
		private CheckBox syncCheck;
		
		private HairDataPanel hairDataPanel;
		
		public double getScaleOfU(){
			return scaleOfU.getValue();
		}
		public int getSizeOfU(){
			return (int)uSize.getValue();
		}
		public int getSizeOfV(){
			return (int)vSize.getValue();
		}
		
		public HairDataEditor(final HairDataPanel hairDataPanel){
			this.hairDataPanel=hairDataPanel;
			hairPinEditor=SimpleEditor.of();
			
			uSize = new LabeledInputRangeWidget2("U-Size(W)", 0, 80, 1);
			this.add(uSize);
			uSize.addtRangeListener(new ValueChangeHandler<Number>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Number> event) {
					hairDataPanel.updateDistanceLabel();
				}
			});

			vSize = new LabeledInputRangeWidget2("V-Size(H)", 0, 80, 1);
			this.add(vSize);
			vSize.addtRangeListener(new ValueChangeHandler<Number>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Number> event) {
					hairDataPanel.updateDistanceLabel();
				}
			});

			
			scaleOfU = new LabeledInputRangeWidget2("Scale of U", 0.1, 16, 0.1);
			this.add(scaleOfU);
			scaleOfU.addtRangeListener(new ValueChangeHandler<Number>() {
				@Override
				public void onValueChange(ValueChangeEvent<Number> event) {
					hairDataPanel.updateDistanceLabel();
				}
			});
			
			//cut-u
			HorizontalPanel h1=new HorizontalPanel();
			h1.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
			this.add(h1);
			h1.add(createLabel("cut-u"));
			cutCheck = new CheckBox();
			cutCheck.setWidth("20px");
			h1.add(cutCheck);
			
			cutIndex = new LabeledInputRangeWidget2("index", 0, 40, 1);
			cutIndex.getLabel().setWidth("40px");
			h1.add(cutIndex);
			
			//cut-u
			HorizontalPanel h2=new HorizontalPanel();
			h2.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
			this.add(h2);
			h2.add(createLabel("narrow"));
			narrowCheck = new CheckBox();
			narrowCheck.setWidth("20px");
			h2.add(narrowCheck);
			narrowIndex = new LabeledInputRangeWidget2("index", 0, 40, 1);
			narrowIndex.getLabel().setWidth("40px");
			h2.add(narrowIndex);
			narrowScale = new LabeledInputRangeWidget2("Narrow Scale", 0.5, 1.5, 0.01);
			this.add(narrowScale);
			
			
			this.add(createLabel("edge"));
			HorizontalPanel h3=new HorizontalPanel();
			h3.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
			this.add(h3);
			
			edgeMode = new ListBox();
			edgeMode.addItem("none");
			edgeMode.addItem("first");
			edgeMode.addItem("center");
			edgeMode.addItem("end");
			h3.add(edgeMode);
			edgeMode.setWidth("80px");
			edgeScale = new LabeledInputRangeWidget2("scale", 0.25, 4, 0.01);
			edgeScale.getLabel().setWidth("40px");
			h3.add(edgeScale);
			
			//channel
			
			HorizontalPanel channelPanel=new HorizontalPanel();
			channelPanel.setVerticalAlignment(ALIGN_MIDDLE);
			channelPanel.add(createLabel("channel:"));
			this.add(channelPanel);
			channelBox = new ListBox();
			for(int i=0;i<16;i++){
				channelBox.addItem(String.valueOf(i));
			}
			channelPanel.add(channelBox);
			channelBox.setSelectedIndex(0);
			channelBox.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					//flush();
				}
			});
			
			HorizontalPanel syncPanel=new HorizontalPanel();
			syncPanel.setVerticalAlignment(ALIGN_MIDDLE);
			syncPanel.add(createLabel("sync:"));
			this.add(syncPanel);
			syncCheck = new CheckBox();
			syncPanel.add(syncCheck);
			
		}
		private Label createLabel(String name){
			Label label=new Label(name);
			label.setWidth("60px");
			return label;
		}
		@Override
			public void setDelegate(EditorDelegate<HairData> delegate) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void flush() {
				value.setSizeOfU((int) uSize.getValue());
				value.setSizeOfV((int) vSize.getValue());
				value.setScaleOfU(scaleOfU.getValue());
				value.setCutU(cutCheck.getValue());
				value.setStartCutUIndexV((int)cutIndex.getValue());
				value.setDoNarrow(narrowCheck.getValue());
				value.setStartNarrowIndexV((int)narrowIndex.getValue());
				value.setNarrowScale(narrowScale.getValue());
				value.setEdgeMode(edgeMode.getSelectedIndex());
				value.setEdgeModeScale(edgeScale.getValue());
				value.setChannel(channelBox.getSelectedIndex());
				value.setSyncMove(syncCheck.getValue());
			}

			@Override
			public void onPropertyChange(String... paths) {
				// TODO Auto-generated method stub
				
			}
			

			@Override
			public void setValue(HairData value) {
				
				this.value=value;
				uSize.setValue(value.getSizeOfU());
				vSize.setValue(value.getSizeOfV());
				scaleOfU.setValue(value.getScaleOfU());
				cutCheck.setValue(value.isCutU());
				cutIndex.setValue(value.getStartCutUIndexV());
				narrowCheck.setValue(value.isDoNarrow());
				narrowIndex.setValue(value.getStartNarrowIndexV());
				narrowScale.setValue(value.getNarrowScale());
				edgeMode.setSelectedIndex(value.getEdgeMode());
				edgeScale.setValue(value.getEdgeModeScale());
				channelBox.setSelectedIndex(value.getChannel());
				syncCheck.setValue(value.isSyncMove());
			}
	}