package com.akjava.gwt.clothhair.client.hair;

import java.util.List;

import com.akjava.gwt.clothhair.client.GWTThreeClothHair;
import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HairPinPanel extends VerticalPanel{
	private EasyCellTableObjects<HairPin> cellObjects;
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
		
	}
	
	public void setHairPins(List<HairPin> pins){
		cellObjects.setDatas(pins);
		cellObjects.update();
	}
}
