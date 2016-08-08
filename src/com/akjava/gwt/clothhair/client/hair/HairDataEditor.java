package com.akjava.gwt.clothhair.client.hair;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.clothhair.client.GWTThreeClothHair;
import com.akjava.gwt.clothhair.client.ammo.BodyDataEditor;
import com.akjava.gwt.clothhair.client.ammo.ConstraintDataEditor;
import com.akjava.gwt.clothhair.client.texture.HairTextureDataEditor;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.google.common.collect.Lists;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.SelectionEvent;
import com.google.gwt.event.logical.shared.SelectionHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.DoubleBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.TextBox;
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
		
		
		private ListBox channelBox;
		private CheckBox syncCheck;
		private CheckBox connectHorizontalCheck;
		
		private CheckBox execAverageNormalEditor;
		
		private HairDataPanel hairDataPanel;
		private LabeledInputRangeWidget2 mass;
		private LabeledInputRangeWidget2 damping;
		private LabeledInputRangeWidget2 thickEditor;
		private DoubleBox extendOutsideRatioEditor;
		private LabeledInputRangeWidget2 particleRadiusEditor;
		private LabeledInputRangeWidget2 particleEndRadiusEditor;
		private ValueListBox<HairType> hairTypeEditor;
		private List<HairType> hairTypeList;
		
		private DoubleBox syncForceLinearEditor;
		private DoubleBox syncMoveLinearEditor;
		
		private CheckBox useCustomNormalEditor;
		private DoubleBox originalNormalRatioEditor;
		private CheckBox startCenterCircleCheck;
		private LabeledInputRangeWidget2 thickEditor2;
		private CheckBox ammoCircleUseFirstPointYEditor;
		private DoubleBox circleRangeMin;
		private DoubleBox circleRangeMax;
		private DoubleBox circleInRangeRatio;
		private ListBox particleTypeBox;
		private CheckBox useCustomBodyParticleDataEditor;
		private BodyDataEditor ammoParticleBodyEditor;
		private ConstraintDataEditor ammoParticleConstraintEditor;
		private CheckBox useCustomConstraintParticleDataEditor;
		private CheckBox circleStyleCheck;
		private CheckBox contactParticleCheck;
		private TextBox cutstomGeometryEditor;
		private CheckBox customGeometryUseAutoSkinningEditor;
		private CheckBox enableCustomGeometryEditor;
		private IntegerBox ammoCircleDummyHairCountEditor;
		private DoubleBox ammoCircleDummyHairAngleEditor;
		private CheckBox ammoCircleHairMergeLastEditor;
		private CheckBox ammoCircleHairMergeZeroEditor;
		
		private DoubleBox ammoHairThinLastEditor;
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
			
			TabPanel tab=new TabPanel();
			tab.setWidth("100%");
			this.add(tab);
			
			VerticalPanel rootPanel=new VerticalPanel();
			this.add(rootPanel);
			tab.add(rootPanel,"Basic");
			tab.selectTab(0);
			
			
			VerticalPanel corePanel=new VerticalPanel();
			rootPanel.add(corePanel);
			
			final VerticalPanel plainClothPanel=new VerticalPanel();
			plainClothPanel.add(new Label("PlainCloth"));
			rootPanel.add(plainClothPanel);
			
			final VerticalPanel ammoPanel=new VerticalPanel();
			ammoPanel.add(new Label("Ammo"));
			rootPanel.add(ammoPanel);
			ammoPanel.setVisible(false);
			
			final VerticalPanel ammoBonePanel=new VerticalPanel();
			ammoBonePanel.add(new Label("Ammo-Bone"));
			rootPanel.add(ammoBonePanel);
			ammoBonePanel.setVisible(false);
			
			
			final VerticalPanel ammoHairPanel=new VerticalPanel();
			ammoHairPanel.setVisible(false);
			rootPanel.add(ammoHairPanel);
			
			uSize = new LabeledInputRangeWidget2("Slices(horizon)", 1, 80, 1);
			uSize.setTitle("Face Slicing(spliting).\n");
			corePanel.add(uSize);
			uSize.addtRangeListener(new ValueChangeHandler<Number>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Number> event) {
					hairDataPanel.updateDistanceLabel();
				}
			});
			uSize.setButtonVisible(true);
			uSize.getLabel().setWidth("115px");

			vSize = new LabeledInputRangeWidget2("Stacks(vertical)", 1, 80, 1);
			corePanel.add(vSize);
			vSize.addtRangeListener(new ValueChangeHandler<Number>() {
				
				@Override
				public void onValueChange(ValueChangeEvent<Number> event) {
					hairDataPanel.updateDistanceLabel();
				}
			});
			vSize.setButtonVisible(true);
			vSize.getLabel().setWidth("115px");
			
			scaleOfU = new LabeledInputRangeWidget2("Scale of U", 0.1, 16, 0.1);
			corePanel.add(scaleOfU);
			scaleOfU.addtRangeListener(new ValueChangeHandler<Number>() {
				@Override
				public void onValueChange(ValueChangeEvent<Number> event) {
					hairDataPanel.updateDistanceLabel();
				}
			});
			scaleOfU.setButtonVisible(true);
			scaleOfU.getLabel().setWidth("115px");
			
			//cut-u
			HorizontalPanel h1=new HorizontalPanel();
			h1.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
			corePanel.add(h1);
			cutCheck = new CheckBox();
			h1.add(cutCheck);
			h1.add(createLabel("cut-u"));
			
			
			cutIndex = new LabeledInputRangeWidget2("index", 0, 40, 1);
			cutIndex.setButtonVisible(true);
			cutIndex.getLabel().setWidth("35px");
			h1.add(cutIndex);
			
			//narrow
			HorizontalPanel h2=new HorizontalPanel();
			h2.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
			plainClothPanel.add(h2);
			narrowCheck = new CheckBox();
			h2.add(narrowCheck);
			
			h2.add(createLabel("narrow"));
			
			narrowIndex = new LabeledInputRangeWidget2("index", 0, 40, 1);
			narrowIndex.getLabel().setWidth("40px");
			h2.add(narrowIndex);
			narrowScale = new LabeledInputRangeWidget2("Narrow Scale", 0.5, 1.5, 0.01);
			plainClothPanel.add(narrowScale);
			
			
			//this.add(createLabel("edge"));
			HorizontalPanel h3=new HorizontalPanel();
			h3.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
			//this.add(h3);
			
			
			
			//channel
			
			HorizontalPanel channelPanel=new HorizontalPanel();
			channelPanel.setVerticalAlignment(ALIGN_MIDDLE);
			channelPanel.add(createLabel("channel:"));
			corePanel.add(channelPanel);
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
			plainClothPanel.add(resetGrvities);
			
			mass = new LabeledInputRangeWidget2("mass", 0.01, 1, 0.01);
			plainClothPanel.add(mass);
			damping = new LabeledInputRangeWidget2("damping", 0.001, 0.1, 0.001);
			plainClothPanel.add(damping);
			
			
			HorizontalPanel option1Panel0=new HorizontalPanel();
			option1Panel0.setVerticalAlignment(ALIGN_MIDDLE);
			syncCheck = new CheckBox();
			option1Panel0.add(syncCheck);
			option1Panel0.add(new Label("sync:"));
			corePanel.add(option1Panel0);
			
			
			option1Panel0.add(new Label("extend:"));
			extendOutsideRatioEditor = new DoubleBox();
			extendOutsideRatioEditor.setWidth("40px");
			option1Panel0.add(extendOutsideRatioEditor);
			
			connectHorizontalCheck = new CheckBox();
			option1Panel0.add(connectHorizontalCheck);
			option1Panel0.add(new Label("connectHorizontal:"));
			
			
			
			
			
			
			HorizontalPanel option1Panel=new HorizontalPanel();
			option1Panel.setVerticalAlignment(ALIGN_MIDDLE);
			option1Panel.setSpacing(2);
			corePanel.add(option1Panel);
			
			
			
			execAverageNormalEditor=new CheckBox("average normals");
			option1Panel.add(execAverageNormalEditor);
			
			
			
			
			HorizontalPanel option1Panel2=new HorizontalPanel();
			option1Panel2.setVerticalAlignment(ALIGN_MIDDLE);
			option1Panel2.setSpacing(2);
			corePanel.add(option1Panel2);
			
			useCustomNormalEditor=new CheckBox();
			option1Panel2.add(useCustomNormalEditor);
			option1Panel2.add(new Label("useCustomNormal:"));
			
			
			
			option1Panel2.add(new Label("originRatio"));
			originalNormalRatioEditor=new DoubleBox();
			originalNormalRatioEditor.setTitle("works when use custom normal.added custom to origin * ratio.1 measn half-half");
			option1Panel2.add(originalNormalRatioEditor);
			originalNormalRatioEditor.setWidth("40px");
			
			hairTypeList = Lists.newArrayList(
					new HairType("Simple Cloth",HairData.TYPE_SIMPLE_CLOTH),
					new HairType("Ammo Plain Cloth",HairData.TYPE_AMMO_CLOTH),
					new HairType("Ammo Bone Cloth",HairData.TYPE_AMMO_BONE_CLOTH),
					new HairType("Ammo Bone Hair",HairData.TYPE_AMMO_BONE_HAIR)
					);
			
			
			
			HorizontalPanel typePanel=new HorizontalPanel();
			typePanel.setVerticalAlignment(ALIGN_MIDDLE);
			corePanel.add(typePanel);
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
			hairTypeEditor.addValueChangeHandler(new ValueChangeHandler<HairDataEditor.HairType>() {

				@Override
				public void onValueChange(ValueChangeEvent<HairType> event) {
					int type=event.getValue().value;
					if(type==HairData.TYPE_SIMPLE_CLOTH){
						plainClothPanel.setVisible(true);
						ammoPanel.setVisible(false);
						ammoBonePanel.setVisible(false);
						ammoHairPanel.setVisible(false);
					}else if(type==HairData.TYPE_AMMO_CLOTH){
						plainClothPanel.setVisible(false);
						ammoPanel.setVisible(true);
						ammoBonePanel.setVisible(false);
						ammoHairPanel.setVisible(false);
					}else if(type==HairData.TYPE_AMMO_BONE_CLOTH){
						plainClothPanel.setVisible(false);
						ammoPanel.setVisible(true);
						ammoBonePanel.setVisible(true);
						ammoHairPanel.setVisible(false);
					}else if(type==HairData.TYPE_AMMO_BONE_HAIR){
						plainClothPanel.setVisible(false);
						ammoPanel.setVisible(true);
						ammoBonePanel.setVisible(true);
						ammoHairPanel.setVisible(true);
					}
				}
				
			});
			
			
			
			HorizontalPanel option1Pane0=new HorizontalPanel();
			option1Pane0.setVerticalAlignment(ALIGN_MIDDLE);
			ammoPanel.add(option1Pane0);
			
			particleTypeBox = new ListBox();
			particleTypeBox.addItem("Sphere");
			particleTypeBox.addItem("Box");
			particleTypeBox.addItem("Capsule");
			particleTypeBox.addItem("Cylinder");
			particleTypeBox.addItem("Cone");
			particleTypeBox.setSelectedIndex(1);
			option1Pane0.add(particleTypeBox);
			
			
			HorizontalPanel option1Pane2=new HorizontalPanel();
			option1Pane2.setVerticalAlignment(ALIGN_MIDDLE);
			ammoPanel.add(option1Pane2);
			
			
			
			
			//option1Pane2.add(new Label("particleRadiusR:"));
			particleRadiusEditor = new LabeledInputRangeWidget2("particleRadiusR",0.01,10,0.01);
			particleRadiusEditor.setTitle("ratio 0.5 is max.can visible on particle setting");
			particleRadiusEditor.setButtonVisible(true);
			option1Pane2.add(particleRadiusEditor);
			particleRadiusEditor.getLabel().setWidth("115px");
			
			particleEndRadiusEditor = new LabeledInputRangeWidget2("par..EndRadiusR",0,10,0.01);
			particleEndRadiusEditor.setTitle("ratio 0.5 is max.can visible on particle setting");
			particleEndRadiusEditor.setButtonVisible(true);
			ammoPanel.add(particleEndRadiusEditor);
			particleEndRadiusEditor.getLabel().setWidth("115px");
			
			
			HorizontalPanel option1Pane3=new HorizontalPanel();
			option1Pane3.setVerticalAlignment(ALIGN_MIDDLE);
			ammoPanel.add(option1Pane3);
			
			option1Pane3.add(new Label("syncForceLinear:"));
			syncForceLinearEditor = new DoubleBox();
			syncForceLinearEditor.setWidth("40px");
			option1Pane3.add(syncForceLinearEditor);
			
			option1Pane3.add(new Label("syncMoveLinear:"));
			syncMoveLinearEditor = new DoubleBox();
			syncMoveLinearEditor.setWidth("40px");
			option1Pane3.add(syncMoveLinearEditor);
			
			
			HorizontalPanel ammoPanel0=new HorizontalPanel();
			ammoPanel0.setVerticalAlignment(ALIGN_MIDDLE);
			ammoPanel.add(ammoPanel0);
			
			HorizontalPanel ammoPanel1=new HorizontalPanel();
			ammoPanel1.setVerticalAlignment(ALIGN_MIDDLE);
			ammoPanel.add(ammoPanel1);
			
			circleStyleCheck = new CheckBox("circleStyle");
			ammoPanel0.add(circleStyleCheck);
			
			contactParticleCheck = new CheckBox("contactParticle");
			ammoPanel0.add(contactParticleCheck);
			
			startCenterCircleCheck = new CheckBox("start center circle");
			ammoPanel1.add(startCenterCircleCheck);
			
			
			ammoCircleUseFirstPointYEditor = new CheckBox("ammoCircleUseFirstPointY");
			ammoPanel1.add(ammoCircleUseFirstPointYEditor);
			
			HorizontalPanel ammoPanel2=new HorizontalPanel();
			ammoPanel2.setVerticalAlignment(ALIGN_MIDDLE);
			ammoPanel.add(ammoPanel2);
			
			ammoPanel2.add(new Label("min-circle"));
			
			circleRangeMin = new DoubleBox();
			circleRangeMin.setWidth("40px");
			ammoPanel2.add(circleRangeMin);
			
			ammoPanel2.add(new Label("max-circle"));
			circleRangeMax = new DoubleBox();
			circleRangeMax.setWidth("40px");
			ammoPanel2.add(circleRangeMax);
			
			ammoPanel2.add(new Label("in-ratio"));
			circleInRangeRatio = new DoubleBox();
			circleInRangeRatio.setWidth("40px");
			ammoPanel2.add(circleInRangeRatio);
			
			//specific ammo-bone
			
			thickEditor = new LabeledInputRangeWidget2("Thick:",0,10,0.01);
			thickEditor.getLabel().setWidth("115px");
			thickEditor.setButtonVisible(true);
			ammoBonePanel.add(thickEditor);
			
			thickEditor2 = new LabeledInputRangeWidget2("Thick2:",0,10,0.01);
			thickEditor2.getLabel().setWidth("115px");
			thickEditor2.setButtonVisible(true);
			ammoBonePanel.add(thickEditor2);
			
			HorizontalPanel h4=new HorizontalPanel();
			ammoPanel.add(h4);
			h4.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
			h4.add(new Label("custom-geometry"));
			cutstomGeometryEditor = new TextBox();
			h4.add(cutstomGeometryEditor);
			
			enableCustomGeometryEditor = new CheckBox("enable");
			h4.add(enableCustomGeometryEditor);
			enableCustomGeometryEditor.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					cutstomGeometryEditor.setReadOnly(!event.getValue());
				}
			});
			
			HorizontalPanel h5=new HorizontalPanel();
			ammoPanel.add(h5);
			h5.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
			customGeometryUseAutoSkinningEditor=new CheckBox("customGeometryUseAutoSkinning");
			h5.add(customGeometryUseAutoSkinningEditor);
			
			tab.add(createParticleBodyEditor(),"AmmoBody");
			tab.add(createParticleConstraintEditor(),"AmmoConstraint");
			
			tab.addSelectionHandler(new SelectionHandler<Integer>() {
				
				@Override
				public void onSelection(SelectionEvent<Integer> event) {
					GWTThreeClothHair.INSTANCE.updateGUI();
				}
			});
			
			HorizontalPanel ammoHairOption1=new HorizontalPanel();
			ammoHairOption1.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
			ammoHairPanel.add(ammoHairOption1);
			
			ammoHairOption1.add(new Label("Dummy count"));
			ammoCircleDummyHairCountEditor = new IntegerBox();
			ammoCircleDummyHairCountEditor.setWidth("40px");
			ammoHairOption1.add(ammoCircleDummyHairCountEditor);
			ammoHairOption1.add(new Label("angle"));
			ammoCircleDummyHairAngleEditor = new DoubleBox();
			ammoHairOption1.add(ammoCircleDummyHairAngleEditor);
			ammoCircleDummyHairAngleEditor.setWidth("40px");
		
			ammoCircleHairMergeLastEditor = new CheckBox("Merge last");
			ammoHairOption1.add(ammoCircleHairMergeLastEditor);
			ammoCircleHairMergeZeroEditor = new CheckBox("zero");
			ammoHairOption1.add(ammoCircleHairMergeZeroEditor);
			
			HorizontalPanel ammoHairOption2=new HorizontalPanel();
			ammoHairOption2.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
			ammoHairPanel.add(ammoHairOption2);
			ammoHairOption2.add(new Label("Thin Last"));
			ammoHairThinLastEditor=new DoubleBox();
			ammoHairThinLastEditor.setWidth("40px");
			ammoHairOption2.add(ammoHairThinLastEditor);
		}
		
		private Panel createParticleBodyEditor(){
			VerticalPanel bodyPanel=new VerticalPanel();
			bodyPanel.add(new Label("hello"));
			HorizontalPanel h1=new HorizontalPanel();
			bodyPanel.add(h1);
			h1.setVerticalAlignment(ALIGN_MIDDLE);
			useCustomBodyParticleDataEditor = new CheckBox("use custom particle body");
			useCustomBodyParticleDataEditor.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					ammoParticleBodyEditor.setVisible(event.getValue());
				}
			});
			h1.add(useCustomBodyParticleDataEditor);
			
			ammoParticleBodyEditor = new BodyDataEditor();
			bodyPanel.add(ammoParticleBodyEditor);
			ammoParticleBodyEditor.setVisible(false);
			
			return bodyPanel;
		}
		
		private Panel createParticleConstraintEditor(){
			VerticalPanel constraintPanel=new VerticalPanel();
			constraintPanel.add(new Label("world"));
			HorizontalPanel h2=new HorizontalPanel();
			constraintPanel.add(h2);
			h2.setVerticalAlignment(ALIGN_MIDDLE);
			useCustomConstraintParticleDataEditor = new CheckBox("use custom particle constraint");
			useCustomConstraintParticleDataEditor.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
				@Override
				public void onValueChange(ValueChangeEvent<Boolean> event) {
					ammoParticleConstraintEditor.setVisible(event.getValue());
				}
			});
			h2.add(useCustomConstraintParticleDataEditor);
			
			ammoParticleConstraintEditor = new ConstraintDataEditor();
			constraintPanel.add(ammoParticleConstraintEditor);
			ammoParticleConstraintEditor.setVisible(false);
			
			return constraintPanel;
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
				value.setAmmoEndParticleRadiusRatio(particleEndRadiusEditor.getValue());
				
				value.setSyncForceLinear(syncForceLinearEditor.getValue());
				value.setSyncMoveLinear(syncMoveLinearEditor.getValue());
				value.setUseCustomNormal(useCustomNormalEditor.getValue());
				value.setOriginalNormalRatio(originalNormalRatioEditor.getValue());
				//no need getHairTextureData update,because these value are updated dynamic
				
				value.setAmmoContactParticle(contactParticleCheck.getValue());
				value.setAmmoStartCenterCircle(startCenterCircleCheck.getValue());
				value.setAmmoBoneThickRatio2(thickEditor2.getValue());
				value.setAmmoCircleUseFirstPointY(ammoCircleUseFirstPointYEditor.getValue());
				value.setAmmoCircleRangeMin(circleRangeMin.getValue());
				value.setAmmoCircleRangeMax(circleRangeMax.getValue());
				value.setAmmoCircleInRangeRatio(circleInRangeRatio.getValue());
				value.setParticleType(particleTypeBox.getSelectedIndex());
				
				value.setCircleStyle(circleStyleCheck.getValue());
				
				value.setUseCustomBodyParticleData(useCustomBodyParticleDataEditor.getValue());
				if(useCustomBodyParticleDataEditor.getValue()){
					ammoParticleBodyEditor.flush();
					value.setAmmoBodyParticleData(ammoParticleBodyEditor.getValue());
				}else{
					value.setAmmoBodyParticleData(null);
				}
				
				value.setUseCustomConstraintData(useCustomConstraintParticleDataEditor.getValue());
				if(useCustomConstraintParticleDataEditor.getValue()){
					ammoParticleConstraintEditor.flush();
					value.setAmmoConstraintData(ammoParticleConstraintEditor.getValue());
				}else{
					value.setAmmoConstraintData(null);
				}
				
				if(!cutstomGeometryEditor.getValue().isEmpty()){
					value.setCustomGeometryName(cutstomGeometryEditor.getValue());
				}else{
					value.setCustomGeometryName(null);
				}
				value.setEnableCustomGeometry(enableCustomGeometryEditor.getValue());
				value.setCustomGeometryUseAutoSkinning(customGeometryUseAutoSkinningEditor.getValue());
			
				
				value.setAmmoCircleDummyHairCount(ammoCircleDummyHairCountEditor.getValue());
				value.setAmmoCircleDummyHairAngle(ammoCircleDummyHairAngleEditor.getValue());
				value.setAmmoCircleHairMergeCenter(ammoCircleHairMergeZeroEditor.getValue());
				value.setAmmoCircleHairMergeLast(ammoCircleHairMergeLastEditor.getValue());
				value.setAmmoHairThinLast(ammoHairThinLastEditor.getValue());
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
				uSize.setValue(value.getSliceFaceCount());
				vSize.setValue(value.getSizeOfV());
				scaleOfU.setValue(value.getScaleOfU());
				cutCheck.setValue(value.isCutU());
				cutIndex.setValue(value.getStartCutUIndexV());
				narrowCheck.setValue(value.isDoNarrow());
				narrowIndex.setValue(value.getStartNarrowIndexV());
				narrowScale.setValue(value.getNarrowScale());
				channelBox.setSelectedIndex(value.getChannel());
				syncCheck.setValue(value.isSyncMove());
				mass.setValue(value.getMass());
				damping.setValue(value.getDamping());
				connectHorizontalCheck.setValue(value.isConnectHorizontal());
				
				extendOutsideRatioEditor.setValue(value.getExtendOutsideRatio());
				thickEditor.setValue(value.getThickRatio());
				particleRadiusEditor.setValue(value.getParticleRadiusRatio());
				particleEndRadiusEditor.setValue(value.getAmmoEndParticleRadiusRatio());
				
				syncForceLinearEditor.setValue(value.getSyncForceLinear());
				syncMoveLinearEditor.setValue(value.getSyncMoveLinear());
				
				useCustomNormalEditor.setValue(value.isUseCustomNormal());
				originalNormalRatioEditor.setValue(value.getOriginalNormalRatio());
				
				if(value.getHairPhysicsType()>=0 && value.getHairPhysicsType()<hairTypeList.size()){
				hairTypeEditor.setValue(hairTypeList.get(value.getHairPhysicsType()),true);//fire visible control
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
				
				contactParticleCheck.setValue(value.isAmmoContactParticle());
				circleStyleCheck.setValue(value.isCircleStyle());
				startCenterCircleCheck.setValue(value.isAmmoStartCenterCircle());
				thickEditor2.setValue(value.getAmmoBoneThickRatio2());
				ammoCircleUseFirstPointYEditor.setValue(value.isAmmoCircleUseFirstPointY());
				circleRangeMin.setValue(value.getAmmoCircleRangeMin());
				circleRangeMax.setValue(value.getAmmoCircleRangeMax());
				circleInRangeRatio.setValue(value.getAmmoCircleInRangeRatio());
				particleTypeBox.setSelectedIndex(value.getParticleType());
				
				useCustomBodyParticleDataEditor.setValue(value.isUseCustomBodyParticleData(), true);//control visible
				ammoParticleBodyEditor.setValue(value.getAmmoBodyParticleData());
				if(ammoParticleBodyEditor.getValue()==null){//copy default
					ammoParticleBodyEditor.setValue(GWTThreeClothHair.INSTANCE.getAmmoParticleBodyData());//reset here,not checkbox
				}
				
				useCustomConstraintParticleDataEditor.setValue(value.isUseCustomConstraintData(), true);//control visible
				ammoParticleConstraintEditor.setValue(value.getAmmoConstraintData());
				if(ammoParticleConstraintEditor.getValue()==null){//copy default
					ammoParticleConstraintEditor.setValue(GWTThreeClothHair.INSTANCE.getAmmoParticleConstraintData());//reset here,not checkbox
				}
				cutstomGeometryEditor.setValue(value.getCustomGeometryName());
				enableCustomGeometryEditor.setValue(value.isEnableCustomGeometry(),true);
				customGeometryUseAutoSkinningEditor.setValue(value.isCustomGeometryUseAutoSkinning());
				
				if(value.getPointMode()==HairData.POINT_MODE_AUTO){
					uSize.setEnabled(true);
					vSize.setEnabled(true);
				}else{
					LogUtils.log("modofied");
					uSize.setEnabled(false);
					vSize.setEnabled(false);
				}
				
				ammoCircleDummyHairCountEditor.setValue(value.getAmmoCircleDummyHairCount());
				ammoCircleDummyHairAngleEditor.setValue(value.getAmmoCircleDummyHairAngle());
				ammoCircleHairMergeZeroEditor.setValue(value.isAmmoCircleHairMergeCenter());
				ammoCircleHairMergeLastEditor.setValue(value.isAmmoCircleHairMergeLast());
				ammoHairThinLastEditor.setValue(value.getAmmoHairThinLast());
			}
	}