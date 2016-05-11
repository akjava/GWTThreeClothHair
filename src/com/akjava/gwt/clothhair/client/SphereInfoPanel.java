package com.akjava.gwt.clothhair.client;

import com.akjava.gwt.clothhair.client.cloth.ClothControler;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.google.common.base.Joiner;
import com.google.common.collect.Lists;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

/**
 * @deprecated
 * @author aki
 *
 */
public class SphereInfoPanel extends VerticalPanel{
	private StorageControler storageControler;
	public SphereInfoPanel(final StorageControler storageControler,final Mesh sphere,final ClothControler clothControls){
			this.storageControler=storageControler;
		
		//sphere
				this.add(new Label("Sphere"));
				
				HorizontalPanel h1=new HorizontalPanel();
				CheckBox visibleCheck=new CheckBox("visible");
				visibleCheck.setValue(true);
				visibleCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						sphere.setVisible(event.getValue());
					}
				});
				h1.add(visibleCheck);
				this.add(h1);
				
				xRange = new LabeledInputRangeWidget2("x", -100, 100, 1);
				xRange.setValue(sphere.getPosition().getX());
				this.add(xRange);
				xRange.addtRangeListener(new ValueChangeHandler<Number>() {
					@Override
					public void onValueChange(ValueChangeEvent<Number> event) {
						sphere.getPosition().setX(event.getValue().doubleValue());
						tmpStoreSphereInfo();
					}
				});
				
				final double resetY=sphere.getPosition().getY();
				
				yRange = new LabeledInputRangeWidget2("y", resetY-100, resetY+100, 1);
				yRange.setValue(sphere.getPosition().getY());
				this.add(yRange);
				yRange.addtRangeListener(new ValueChangeHandler<Number>() {
					@Override
					public void onValueChange(ValueChangeEvent<Number> event) {
						sphere.getPosition().setY(event.getValue().doubleValue());
						tmpStoreSphereInfo();
					}
				});
				
				zRange = new LabeledInputRangeWidget2("z", -200, 200, 1);
				zRange.setValue(sphere.getPosition().getZ());
				this.add(zRange);
				zRange.addtRangeListener(new ValueChangeHandler<Number>() {
					@Override
					public void onValueChange(ValueChangeEvent<Number> event) {
						sphere.getPosition().setZ(event.getValue().doubleValue());
						tmpStoreSphereInfo();
					}
				});
				
				Button reset=new Button("reset xyz-scale",new ClickHandler() {
					
					@Override
					public void onClick(ClickEvent event) {
						xRange.setValue(0,true);
						yRange.setValue(resetY,true);
						zRange.setValue(0,true);
						scaleRange.setValue(100,true);
					}
				});
				h1.add(reset);
				
				scaleRange = new LabeledInputRangeWidget2("scale", 1, 200, 1);
				scaleRange.getLabel().setWidth("40px");
				scaleRange.getRange().setWidth("220px");
				scaleRange.setValue(sphere.getScale().getX());
				this.add(scaleRange);
				scaleRange.addtRangeListener(new ValueChangeHandler<Number>() {
					@Override
					public void onValueChange(ValueChangeEvent<Number> event) {
						sphere.getScale().setScalar(event.getValue().doubleValue());
						
						//clothControls.updateBallSize(event.getValue().intValue());
						tmpStoreSphereInfo();
					}
				});
				
				sphereInfoConverter=new TmpSphereInfoConverter();
				
				
				
				//LogUtils.log("v:"+storageControler.getValue(HairStorageKeys.temp_sphere, null));
				
				String storedVaues=storageControler.getValue(HairStorageKeys.temp_sphere,null);
				
				
				double[] values;
				if(storedVaues==null){
					//values=new double[]{0,sphere.getPosition().getY(),0,clothControls.getBallSize()};
				}else{
					values=sphereInfoConverter.reverse().convert(storedVaues);
				}
				/*
				//too much over write
				xRange.setValue(values[0],true);
				yRange.setValue(values[1],true);
				zRange.setValue(values[2],true);
				scaleRange.setValue(values[3],true);
				*/
	}
	
	private void tmpStoreSphereInfo(){
		double[] values=new double[]{
				xRange.getValue(),
				yRange.getValue(),
				zRange.getValue(),
				scaleRange.getValue()
		};
		String line=sphereInfoConverter.convert(values);
		
		
		try {
			storageControler.setValue(HairStorageKeys.temp_sphere, line);
		} catch (StorageException e) {
			LogUtils.log(e.getMessage());
		}
	}
	
	TmpSphereInfoConverter sphereInfoConverter;
	private LabeledInputRangeWidget2 scaleRange;
	private LabeledInputRangeWidget2 zRange;
	private LabeledInputRangeWidget2 yRange;
	private LabeledInputRangeWidget2 xRange;
	
	/*
	 interface Driver extends SimpleBeanEditorDriver< SphereData,  SphereDataEditor> {}
	 Driver driver = GWT.create(Driver.class);

	public void onModuleLoad() {
		SphereDataEditor editor=new SphereDataEditor();    
		driver.initialize(editor);
		
		VerticalPanel editorPanel=new VerticalPanel();
		driver.edit(new SphereData());
		editorPanel.add(editor);
		
	    	Button updateBt=new Button("Update",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				SphereData data=driver.flush();
				//TODO
				System.out.println(data);
			}
		});
	    editorPanel.add(updateBt);

	VerticalPanel panel=new VerticalPanel();
	
	//create easy cell tables
	SimpleCellTable<SphereData> table=new SimpleCellTable<SphereData>() {
		@Override
		public void addColumns(CellTable<SphereData> table) {
			TextColumn<SphereData> nameColumn=new TextColumn<SphereData>() {
				@Override
				public String getValue(SphereData object) {
					return object.getName();
				}
			};
			table.addColumn(nameColumn);
		}
	};
	
	cellObjects = new EasyCellTableObjects<SphereData>(table){
		@Override
		public void onSelect(SphereData selection) {
			// TODO Auto-generated method stub
			
		}};
	panel.add(table);
	
	
}

	public class SphereData{
		
	}
	*/
	

}
