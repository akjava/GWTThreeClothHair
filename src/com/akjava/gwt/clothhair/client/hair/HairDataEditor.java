package com.akjava.gwt.clothhair.client.hair;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.akjava.gwt.clothhair.client.texture.HairTextureDataEditor;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.google.common.collect.Lists;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.editor.client.adapters.SimpleEditor;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HairDataEditor extends VerticalPanel implements Editor<HairData>,ValueAwareEditor<HairData>{

		private HairData value;
		public HairData getValue() {
			return value;
		}

		private HairTextureDataEditor hairTextureDataEditor;

		public void setHairTextureDataEditor(HairTextureDataEditor hairTextureDataEditor) {
			this.hairTextureDataEditor = hairTextureDataEditor;
		}

		/**
		 * @igIgnore
		 * @return
		 */
		
		@Ignore
		public HairTextureDataEditor getHairTextureDataEditor() {
			return hairTextureDataEditor;
		}

		//private SimpleEditor<List<HairPin>> hairPinEditor;
		
		private HairPinPanel hairPinPanel;//maybe no need;
		
		public HairPinPanel getHairPinPanel() {
			return hairPinPanel;
		}

		public void setHairPinPanel(HairPinPanel hairPinPanel) {
			this.hairPinPanel = hairPinPanel;
		}

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
		private CheckBox connectHorizontalCheck;
		
		private CheckBox execAverageNormalEditor;
		
		private HairDataPanel hairDataPanel;
		private LabeledInputRangeWidget2 mass;
		private LabeledInputRangeWidget2 damping;
		private DoubleBox thickEditor;
		private DoubleBox extendOutsideRatioEditor;
		private DoubleBox particleRadiusEditor;
		private ValueListBox<HairType> hairTypeEditor;
		private List<HairType> hairTypeList;
		
		private DoubleBox syncForceLinearEditor;
		private DoubleBox syncMoveLinearEditor;
		
		private CheckBox useCustomNormalEditor;
		private DoubleBox originalNormalRatioEditor;
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
			//hairPinEditor=SimpleEditor.of();
			
			uSize = new LabeledInputRangeWidget2("U-Size(W)", 1, 80, 1);
			this.add(uSize);
			uSize.addtRangeListener(new ValueChangeHandler<Number>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Number> event) {
					hairDataPanel.updateDistanceLabel();
				}
			});

			vSize = new LabeledInputRangeWidget2("V-Size(H)", 1, 80, 1);
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
			narrowCheck.setTitle("only work on simple cloth");
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
			
			Button resetGrvities=new Button("Reset mass&damping",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					mass.setValue(0.1);
					damping.setValue(0.03);	
				}
			});
			channelPanel.add(resetGrvities);
			
			mass = new LabeledInputRangeWidget2("mass", 0.01, 1, 0.01);
			this.add(mass);
			damping = new LabeledInputRangeWidget2("damping", 0.001, 0.1, 0.001);
			this.add(damping);
			
			
			HorizontalPanel syncPanel=new HorizontalPanel();
			syncPanel.setVerticalAlignment(ALIGN_MIDDLE);
			syncPanel.add(new Label("sync:"));
			this.add(syncPanel);
			syncCheck = new CheckBox();
			syncPanel.add(syncCheck);
			
			syncPanel.add(new Label("connectHorizontal:"));
			
			connectHorizontalCheck = new CheckBox();
			syncPanel.add(connectHorizontalCheck);
			
			syncPanel.add(new Label("useCustomNormal:"));
			useCustomNormalEditor=new CheckBox();
			syncPanel.add(useCustomNormalEditor);
			
			HorizontalPanel option1Panel=new HorizontalPanel();
			option1Panel.setVerticalAlignment(ALIGN_MIDDLE);
			option1Panel.setSpacing(2);
			this.add(option1Panel);
			option1Panel.add(new Label("extend:"));
			extendOutsideRatioEditor = new DoubleBox();
			extendOutsideRatioEditor.setWidth("40px");
			option1Panel.add(extendOutsideRatioEditor);
			
			
			execAverageNormalEditor=new CheckBox("average normals");
			option1Panel.add(execAverageNormalEditor);
			
			option1Panel.add(new Label("originRatio"));
			originalNormalRatioEditor=new DoubleBox();
			originalNormalRatioEditor.setTitle("works when use custom normal.added custom to origin * ratio.1 measn half-half");
			option1Panel.add(originalNormalRatioEditor);
			originalNormalRatioEditor.setWidth("40px");
			
			hairTypeList = Lists.newArrayList(
					new HairType("Simple Cloth",HairData.TYPE_SIMPLE_CLOTH),
					new HairType("Ammo Plain Cloth",HairData.TYPE_AMMO_CLOTH),
					new HairType("Ammo Bone Cloth",HairData.TYPE_AMMO_BONE_CLOTH),
					new HairType("Ammo Bone Hair",HairData.TYPE_AMMO_BONE_HAIR)
					);
			
			
			HorizontalPanel typePanel=new HorizontalPanel();
			typePanel.setVerticalAlignment(ALIGN_MIDDLE);
			this.add(typePanel);
			typePanel.add(new Label("Type"));
			hairTypeEditor = new ValueListBox<HairDataEditor.HairType>(new Renderer<HairType>() {

				@Override
				public String render(HairType object) {
					return object.label;
				}

				@Override
				public void render(HairType object, Appendable appendable) throws IOException {
					// TODO Auto-generated method stub
					
				}
			});
			hairTypeEditor.setValue(hairTypeList.get(0));
			hairTypeEditor.setAcceptableValues(hairTypeList);
			typePanel.add(hairTypeEditor);
			
			
			this.add(new Label("Ammo specific"));
			
			HorizontalPanel option1Pane2=new HorizontalPanel();
			option1Pane2.setVerticalAlignment(ALIGN_MIDDLE);
			this.add(option1Pane2);
			option1Pane2.add(new Label("thick:"));
			thickEditor = new DoubleBox();
			thickEditor.setWidth("40px");
			option1Pane2.add(thickEditor);
			
			option1Pane2.add(new Label("particleRadiusR:"));
			particleRadiusEditor = new DoubleBox();
			particleRadiusEditor.setTitle("ratio 0.5 is max.can visible on particle setting");
			particleRadiusEditor.setWidth("40px");
			option1Pane2.add(particleRadiusEditor);
			
			
			
			HorizontalPanel option1Pane3=new HorizontalPanel();
			option1Pane3.setVerticalAlignment(ALIGN_MIDDLE);
			this.add(option1Pane3);
			
			option1Pane3.add(new Label("syncForceLinear:"));
			syncForceLinearEditor = new DoubleBox();
			syncForceLinearEditor.setWidth("40px");
			option1Pane3.add(syncForceLinearEditor);
			
			option1Pane3.add(new Label("syncMoveLinear:"));
			syncMoveLinearEditor = new DoubleBox();
			syncMoveLinearEditor.setWidth("40px");
			option1Pane3.add(syncMoveLinearEditor);
			
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
				value.setMass(mass.getValue());
				value.setDamping(damping.getValue());
				value.setConnectHorizontal(connectHorizontalCheck.getValue());
				if(hairTypeEditor.getValue()!=null){
					value.setHairPhysicsType(hairTypeEditor.getValue().value);
				}
				value.setExecAverageNormal(execAverageNormalEditor.getValue());
				value.setExtendOutsideRatio(extendOutsideRatioEditor.getValue());
				value.setThickRatio(thickEditor.getValue());
				value.setParticleRadiusRatio(particleRadiusEditor.getValue());
				
				value.setSyncForceLinear(syncForceLinearEditor.getValue());
				value.setSyncMoveLinear(syncMoveLinearEditor.getValue());
				value.setUseCustomNormal(useCustomNormalEditor.getValue());
				value.setOriginalNormalRatio(originalNormalRatioEditor.getValue());
				//no need getHairTextureData update,because these value are updated dynamic
			}

			@Override
			public void onPropertyChange(String... paths) {
				// TODO Auto-generated method stub
				
			}
			
			public static class HairType{
				private String label;
				public HairType(String label, int value) {
					super();
					this.label = label;
					this.value = value;
				}
				private int value;
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
				mass.setValue(value.getMass());
				damping.setValue(value.getDamping());
				connectHorizontalCheck.setValue(value.isConnectHorizontal());
				
				extendOutsideRatioEditor.setValue(value.getExtendOutsideRatio());
				thickEditor.setValue(value.getThickRatio());
				particleRadiusEditor.setValue(value.getParticleRadiusRatio());
				
				syncForceLinearEditor.setValue(value.getSyncForceLinear());
				syncMoveLinearEditor.setValue(value.getSyncMoveLinear());
				
				useCustomNormalEditor.setValue(value.isUseCustomNormal());
				originalNormalRatioEditor.setValue(value.getOriginalNormalRatio());
				
				if(value.getHairPhysicsType()>=0 && value.getHairPhysicsType()<hairTypeList.size()){
				hairTypeEditor.setValue(hairTypeList.get(value.getHairPhysicsType()));
				}else{
					LogUtils.log("invalid hairtype skipped:"+value.getHairPhysicsType());
				}
				execAverageNormalEditor.setValue(value.isExecAverageNormal());
				
				if(hairTextureDataEditor==null){
					LogUtils.log("no hairTextureDataEditor");
				}
				hairTextureDataEditor.setValue(value.getHairTextureData());
				
				if(hairPinPanel==null){
					LogUtils.log("no hairPinPanel");
				}
				
				//hairPinPanel.setHairPins(value.getHairPins());
			}
	}