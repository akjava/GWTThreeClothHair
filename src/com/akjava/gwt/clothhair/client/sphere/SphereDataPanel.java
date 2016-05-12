package com.akjava.gwt.clothhair.client.sphere;

import javax.annotation.Nullable;

import com.akjava.gwt.clothhair.client.GWTThreeClothHair;
import com.akjava.gwt.clothhair.client.HairStorageKeys;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.gwt.three.client.js.objects.Skeleton;
import com.google.common.base.Joiner;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SphereDataPanel extends VerticalPanel{
	 interface Driver extends SimpleBeanEditorDriver< SphereData,  SphereDataEditor> {}
	 Driver driver = GWT.create(Driver.class);
	private EasyCellTableObjects<SphereData> cellObjects;
	 
	SphereDataControler controler;
	SphereData defaultValue;
	
	 public SphereDataPanel(final SphereDataControler controler,final SphereData defaultValue){
		 this.controler=controler;
		 this.defaultValue=defaultValue;
		 sphereDataEditor = new SphereDataEditor(defaultValue,this);  
		 this.add(sphereDataEditor);
		 
		 driver.initialize(sphereDataEditor);
		 
		 driver.edit(null);
		 
		
		 
		 SimpleCellTable<SphereData> table=new SimpleCellTable<SphereData>() {
				@Override
				public void addColumns(CellTable<SphereData> table) {
					TextColumn<SphereData> xColumn=new TextColumn<SphereData>() {
						@Override
						public String getValue(SphereData object) {
							return String.valueOf(object.getX());
						}
					};
					table.addColumn(xColumn);
					
					TextColumn<SphereData> yColumn=new TextColumn<SphereData>() {
						@Override
						public String getValue(SphereData object) {
							return String.valueOf(object.getY());
						}
					};
					table.addColumn(yColumn);
					
					TextColumn<SphereData> zColumn=new TextColumn<SphereData>() {
						@Override
						public String getValue(SphereData object) {
							return String.valueOf(object.getZ());
						}
					};
					table.addColumn(zColumn);
					
					TextColumn<SphereData> sizeColumn=new TextColumn<SphereData>() {
						@Override
						public String getValue(SphereData object) {
							return String.valueOf(object.getSize());
						}
					};
					table.addColumn(sizeColumn);
				}
			};
			
			cellObjects = new EasyCellTableObjects<SphereData>(table){
				@Override
				public void onSelect(SphereData selection) {
					
					driver.edit(selection);
					
					controler.onSelectSphere(selection);
					
				}};
				
			this.add(table);
			
			HorizontalPanel buttons=new HorizontalPanel();
			this.add(buttons);
			Button addBt=new Button("add",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					SphereData newData=defaultValue.clone();
					addSpereData(newData);
					cellObjects.setSelected(newData, true);
					
				}
			});
			buttons.add(addBt);
			Button removeBt=new Button("remove",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					
					removeSpereData(cellObjects.getSelection());
					
				}
			});
			buttons.add(removeBt);
			//make controls
			
			
	 }
	 
	 private SphereDataConverter converter=new SphereDataConverter();
	 private StorageControler storageControler=new StorageControler();
	private SphereDataEditor sphereDataEditor;
	 public void onFlushed(){
		//
		 GWTThreeClothHair.INSTANCE.syncSphereDataAndSkinningVertexCalculator(sphereDataEditor.getValue());
		 
		 //store data
		 String lines=Joiner.on("\r\n").join(converter.convertAll(cellObjects.getDatas()));
		 try {
			storageControler.setValue(HairStorageKeys.KEY_SPHERES, lines);
		} catch (StorageException e) {
			//possible quote error
			LogUtils.log(e.getMessage());
		}
	 }
	 
	 public void addSpereData(SphereData data){
		 cellObjects.addItem(data);
		 controler.addSphereData(data);
		 onFlushed();
	 }
	 public void removeSpereData(SphereData data){
		 if(data==null){
			 return;
		 }
		 cellObjects.removeItem(data);
		 controler.removeSphereData(data);
		 onFlushed();
	 }
	 
	 
		public static interface SphereDataControler{
			public void removeSphereData(SphereData data);
			public void addSphereData(SphereData data);
			public void onSelectSphere(@Nullable SphereData data);
		}


		public void setSkelton(Skeleton skeleton) {
			
			sphereDataEditor.setSkelton(skeleton);
			
		}
}
