package com.akjava.gwt.clothhair.client.hair;

import java.util.List;

import javax.annotation.Nullable;

import com.akjava.gwt.clothhair.client.GWTThreeClothHair;
import com.akjava.gwt.clothhair.client.cloth.HairCloth;
import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.akjava.gwt.lib.client.JavaScriptUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.gwt.three.client.java.ui.experiments.Vector3Editor;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.google.common.base.Joiner;
import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.TextArea;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HairPinPanel extends VerticalPanel{
	private EasyCellTableObjects<HairPin> cellObjects;
	private HairDataEditor hairDataEditor;
	private TextArea pinText;
	public HairDataEditor getHairDataEditor() {
		return hairDataEditor;
	}

	public void setHairDataEditor(HairDataEditor hairDataEditor) {
		this.hairDataEditor = hairDataEditor;
	}

	public HairPinPanel(){
		
		//create easy cell tables
		SimpleCellTable<HairPin> table=new SimpleCellTable<HairPin>() {
			@Override
			public void addColumns(CellTable<HairPin> table) {
				TextColumn<HairPin> nameColumn=new TextColumn<HairPin>() {
					@Override
					public String getValue(HairPin object) {
						return String.valueOf(object.getFaceIndex());
					}
				};
				table.addColumn(nameColumn,"FaceIndex");
				
				TextColumn<HairPin> vertexColumn=new TextColumn<HairPin>() {
					@Override
					public String getValue(HairPin object) {
						return String.valueOf(object.getVertexOfFaceIndex());
					}
				};
				table.addColumn(vertexColumn,"VertexIndex");
				//TODO add vector3 and etc
			}
		};
		
		HorizontalPanel buttons=new HorizontalPanel();
		this.add(buttons);
		Button remove=new Button("remove",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				cellObjects.removeItem(cellObjects.getSelection());
				//would called
			}
		});
		buttons.add(remove);
		
		Button reverse=new Button("reverse all",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				List<HairPin> pins=Lists.newArrayList();
				for(HairPin pin:cellObjects.getDatas()){
					pins.add(0,pin);
				}
				//because use List-reference
				cellObjects.getDatas().clear();
				for(HairPin pin:pins){
					cellObjects.getDatas().add(pin);
				}
				cellObjects.update();
				//would called
			}
		});
		buttons.add(reverse);
		
		final HairPinEditor editor=new HairPinEditor();
		
		cellObjects = new EasyCellTableObjects<HairPin>(table){
			@Override
			public void onSelect(HairPin selection) {
				if(selection!=null){
					//can detect which one selected
					GWTThreeClothHair.INSTANCE.setSelectionVertex(selection.getFaceIndex(),selection.getVertexOfFaceIndex());
				}else{
					GWTThreeClothHair.INSTANCE.unselectVertex();
				}
				editor.setVisible(selection!=null);
				editor.setValue(selection);
			}};
		this.add(table);
		this.add(editor);
		
		pinText = new TextArea();
		//pinText.setReadOnly(true);
		pinText.setSize("200px", "100px");
		this.add(pinText);
		
		
		
		SimpleCellTable<Vector3> semiAutoTable=new SimpleCellTable<Vector3>(10) {
			@Override
			public void addColumns(CellTable<Vector3> table) {
				TextColumn<Vector3> xColumn=new TextColumn<Vector3>() {
					@Override
					public String getValue(Vector3 object) {
						
						return String.valueOf(JavaScriptUtils.fixNumber(3, object.getX()));
					}
				};
				table.addColumn(xColumn,"X");
				TextColumn<Vector3> yColumn=new TextColumn<Vector3>() {
					@Override
					public String getValue(Vector3 object) {
						
						return String.valueOf(JavaScriptUtils.fixNumber(3, object.getY()));
					}
				};
				table.addColumn(yColumn,"Y");
				TextColumn<Vector3> zColumn=new TextColumn<Vector3>() {
					@Override
					public String getValue(Vector3 object) {
						
						return String.valueOf(JavaScriptUtils.fixNumber(3, object.getZ()));
					}
				};
				table.addColumn(zColumn,"Z");
				
				TextColumn<Vector3> pinColumn=new TextColumn<Vector3>() {
					@Override
					public String getValue(Vector3 object) {
						Optional<Integer> value=semiAutoPinObjects.getSelectedIndex(object);
						if(value.isPresent()){
							int index=value.get();
							if(hairData.getSemiAutoPins()!=null){
							for(int i=0;i<hairData.getSemiAutoPins().length();i++){
								double v=hairData.getSemiAutoPins().get(i);
								if(v==index){
									return "true";
								}
							}
							}
							
							return "";
							
						}else{
							return "";
						}
						
					}
				};
				table.addColumn(pinColumn,"Pin");
				//TODO add vector3 and etc
			}
		};
		final Vector3Editor semiAutoEditor=new Vector3Editor("pos", -200, 1800, 0.1, 0);
		semiAutoEditor.addValueChangeHandler(new ValueChangeHandler<Vector3>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Vector3> event) {
				Optional<Integer> value=semiAutoPinObjects.getSelectedIndex(event.getValue());
				if(value.isPresent()){
					GWTThreeClothHair.INSTANCE.updateSemiAutoPositions(value.get(),event.getValue());
				}
			}
		});
		final CheckBox pinBox=new CheckBox("static pin");
		pinBox.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				Optional<Integer> optional=semiAutoPinObjects.getSelectedIndex();
				if(!optional.isPresent()){
					return;
				}
				boolean contain=hairData.indexInSemiAutoPins(optional.get())!=-1;
				
				if(event.getValue()){
					if(!contain){
						hairData.getSemiAutoPins().push(optional.get());
					}
				}else{
					if(contain){
						int index=hairData.indexInSemiAutoPins(optional.get());
						JavaScriptUtils.remove(hairData.getSemiAutoPins(), index);
					}
				}
			}
		});
		
		semiAutoPinObjects = new EasyCellTableObjects<Vector3>(semiAutoTable){
			@Override
			public void onSelect(Vector3 selection) {
				if(selection!=null){
					semiAutoEditor.setEnabled(true);
					semiAutoEditor.setValue(selection);
					Optional<Integer> value=semiAutoPinObjects.getSelectedIndex(selection);
					if(value.isPresent()){
						int index=value.get();
						GWTThreeClothHair.INSTANCE.updateSemiAutoPositionSelectionColor(index);
					
						if(hairData.indexInSemiAutoPins(index)!=-1){
							pinBox.setValue(true);
						}else{
							pinBox.setValue(false);
						}
						
						pinBox.setEnabled(true);
					}
					
					
					
				}else{
					semiAutoEditor.setValue(null);
					semiAutoEditor.setEnabled(false);
					
					GWTThreeClothHair.INSTANCE.updateSemiAutoPositionSelectionColor(-1);
					
					pinBox.setEnabled(false);
					
					//sync pos
				}
			}};
		this.add(semiAutoTable);
		this.add(semiAutoEditor);
		this.add(pinBox);
	}
	public EasyCellTableObjects<Vector3> getSemiAutoPinObjects() {
		return semiAutoPinObjects;
	}
	private EasyCellTableObjects<Vector3> semiAutoPinObjects;
	
	public void setHairPins(List<HairPin> pins){
		for(int i=0;i<pins.size();i++){
			if(pins.get(i)==null){
				LogUtils.log("setHairPins() invalidly contain null pin");
			}
		}
		cellObjects.setDatas(pins);
		cellObjects.update();
		updatePinText();
	}
	
	
	private HairData hairData;
	public void setSemiAutoPositions(@Nullable HairData data){
		hairData=data;
		if(data!=null){
		JsArray<Vector3> pos=data.getSemiAutoPoints();
		if(pos==null){
			semiAutoPinObjects.setDatas(Lists.<Vector3>newArrayList());
			semiAutoPinObjects.update();
			return;
		}
		List<Vector3> positions=JavaScriptUtils.toList(pos);
		
		semiAutoPinObjects.setDatas(positions);
		semiAutoPinObjects.update();
		}else{
			List<Vector3> positions=Lists.newArrayList();
			semiAutoPinObjects.setDatas(positions);
			semiAutoPinObjects.update();
		}
	}

	private void updatePinText() {
		
		if(hairDataEditor.getValue()==null){
			pinText.setText("");
			return;
		}
		HairData data=hairDataEditor.getValue();
		String allText="";
		int index=0;
		int w=HairCloth.calcurateHorizontalPin(data.countNormalPin(), data.getSliceFaceCount());
		//LogUtils.log(w+","+data.getSizeOfV());
		for(int y=0;y<=data.getSizeOfV();y++){
			List<Integer> ints=Lists.newArrayList();
			for(int x=0;x<w;x++){
				ints.add(index);
				index++;
			}
			allText+=Joiner.on(",").join(ints)+"\n";
		}
		pinText.setText(allText);
	}
}
