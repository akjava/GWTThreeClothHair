package com.akjava.gwt.clothhair.client.hair;

import java.util.List;

import com.akjava.gwt.clothhair.client.GWTThreeClothHair;
import com.akjava.gwt.clothhair.client.cloth.HairCloth;
import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
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
		pinText.setReadOnly(true);
		pinText.setSize("200px", "300px");
		this.add(pinText);
		
	}
	
	public void setHairPins(List<HairPin> pins){
		cellObjects.setDatas(pins);
		cellObjects.update();
		updatePinText();
	}

	private void updatePinText() {
		
		if(hairDataEditor.getValue()==null){
			pinText.setText("");
			return;
		}
		HairData data=hairDataEditor.getValue();
		String allText="";
		int index=0;
		int w=HairCloth.calcurateHorizontalPin(data.countNormalPin(), data.getSizeOfU());
		//LogUtils.log(w+","+data.getSizeOfV());
		for(int y=0;y<data.getSizeOfV();y++){
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
