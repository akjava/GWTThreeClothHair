package com.akjava.gwt.clothhair.client.lights;

import javax.annotation.Nullable;

import com.akjava.gwt.clothhair.client.GWTThreeClothHair;
import com.akjava.gwt.clothhair.client.lights.SimpleVector3Editor.SimpleVector3EditorListener;
import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.html5.client.input.ColorBox;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.akjava.gwt.three.client.java.ThreeLog;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.lights.AmbientLight;
import com.akjava.gwt.three.client.js.lights.DirectionalLight;
import com.akjava.gwt.three.client.js.lights.HemisphereLight;
import com.akjava.gwt.three.client.js.lights.Light;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.ColorUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.TextBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LightDataPanel extends VerticalPanel{
	 
	private LightDataEditor editor;
	private EasyCellTableObjects<LightData> cellObjects;

	private String storageKey;
	private StorageControler storageControler;
	public LightDataPanel(String storageKey,StorageControler storageControler) {
		
		
		CheckBox simpleLight=new CheckBox("simple 0xffffff");
		
		this.add(simpleLight);
		
		this.storageKey=storageKey;
		this.storageControler=storageControler;
		editor = new LightDataEditor();    
				
		this.add(editor);
		editor.setValue(null);

		
	
	//create easy cell tables
	final SimpleCellTable<LightData> table=new SimpleCellTable<LightData>() {
		@Override
		public void addColumns(CellTable<LightData> table) {
			
			
			TextColumn<LightData> typeColumn=new TextColumn<LightData>() {
				@Override
				public String getValue(LightData object) {
					String type="";
					if(object.getType()==LightData.AMBIENT){
						type="Ambient";
					}else if(object.getType()==LightData.DIRECTIONAL){
						type="Directional";
					}else if(object.getType()==LightData.HEMISPHERE){
						type="Hemisphere";
					}
					return type;
				}
			};
			table.addColumn(typeColumn);
			
			TextColumn<LightData> xyzColumn=new TextColumn<LightData>() {
				@Override
				public String getValue(LightData object) {
					String xyz="";
					xyz=ThreeLog.get(object.getPosition());
					return xyz;
				}
			};
			table.addColumn(xyzColumn);
			
			TextColumn<LightData> intensityColumn=new TextColumn<LightData>() {
				@Override
				public String getValue(LightData object) {
					return String.valueOf(object.getIntensity());
				}
			};
			table.addColumn(intensityColumn);
			
			TextColumn<LightData> nameColumn=new TextColumn<LightData>() {
				@Override
				public String getValue(LightData object) {
					return object.getName();
				}
			};
			//table.addColumn(nameColumn); //sadly no space
		}
	};
	this.add(table);
	
	cellObjects = new EasyCellTableObjects<LightData>(table){
		@Override
		public void onSelect(LightData selection) {
			editor.setValue(selection);
			onDataSelected(selection);
		}};
	
	
	//controler
	HorizontalPanel buttons=new HorizontalPanel();
	this.add(buttons);
	Button addBt=new Button("add",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			LightData newData=makeNewData();
			addData(newData,true);
			cellObjects.setSelected(newData, true);
			
		}
	});
	buttons.add(addBt);
	
	Button copyBt=new Button("copy",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			LightData selection=cellObjects.getSelection();
			if(selection==null){
				return;
			}
			LightData newData=copy(selection);
			addData(newData,true);
			cellObjects.setSelected(newData, true);
			
		}
	});
	buttons.add(copyBt);
	

	
	Button removeBt=new Button("remove",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			
			removeData(cellObjects.getSelection());
			
		}
	});
	buttons.add(removeBt);
	
	Button removeAll=new Button("remove All",new ClickHandler() {
		@Override
		public void onClick(ClickEvent event) {
			boolean confirm=Window.confirm("remove all?");
			if(!confirm){
				return;
			}
			clearAllData();
		}
	});
	buttons.add(removeAll);
	
	
	//download replace import widget
	HorizontalPanel uploadPanel=new HorizontalPanel();
	 uploadPanel.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	 final ListBox uploadModeBox=new ListBox();
	 uploadModeBox.addItem("Replace");
	 uploadModeBox.addItem("Import");
	 uploadPanel.add(uploadModeBox);
	 uploadModeBox.setSelectedIndex(0);
	 
	 FileUploadForm upload=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
		
		@Override
		public void uploaded(File file, String text) {
			if(uploadModeBox.getSelectedIndex()==0){//replace
				clearAllData();
			}
			//todo check validate
			
			 Iterable<LightData> newDatas=new LightDataConverter().reverse().convertAll(CSVUtils.splitLinesWithGuava(text));
			 for(LightData newData:newDatas){
				 addData(newData,false);
				 cellObjects.setSelected(newData, true);//maybe last selected
			 }
			 storeData();
			
		}
	}, true, "UTF-8");
	 upload.setAccept(".csv");
	 uploadPanel.add(upload);
	 
	 //downloads
	 HorizontalPanel downloadPanels=new HorizontalPanel();
	 downloadPanels.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
	 final HorizontalPanel download=new HorizontalPanel();
	 
	 Button downloadBt=new Button("download",new ClickHandler() {
		
		@Override
		public void onClick(ClickEvent event) {
			download.clear();
			String text=toStoreText();
			Anchor a=HTML5Download.get().generateTextDownloadLink(text, getDownloadFileName(), "click to download",true);
			download.add(a);
		}
	});
	 downloadPanels.add(downloadBt);
	 downloadPanels.add(download);
	 
	 this.add(uploadPanel);
	 this.add(downloadPanels);
	
	
	
	//initial load from storage
	String lines=storageControler.getValue(storageKey, null);
	if(lines!=null && !lines.isEmpty()){
		Iterable<LightData> datas=new LightDataConverter().reverse().convertAll(CSVUtils.splitLinesWithGuava(lines));
		for(LightData data:datas){
			addData(data,false);
		}
	}
	
	simpleLight.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
		private String lastLight="";
		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			if(event.getValue()){
				
				lastLight=toStoreText();
				LogUtils.log(lastLight);
				String simpleLight=",1,16777215,1,0,0:0:0,false";
				editor.setVisible(false);
				table.setVisible(false);
				clearAllData();
			
				//todo check validate
			
			 Iterable<LightData> newDatas=new LightDataConverter().reverse().convertAll(CSVUtils.splitLinesWithGuava(simpleLight));
			 for(LightData newData:newDatas){
				 addData(newData,false);
				 //cellObjects.setSelected(newData, true);//maybe last selected
			 }
			 
			storeData(lastLight);
			 
			}else{
				//back
				clearAllData();
				editor.setVisible(true);
				table.setVisible(true);
					
					//todo check validate
				if(lastLight!=null && !lastLight.isEmpty()){//basically never without initialy empty
				 Iterable<LightData> newDatas=new LightDataConverter().reverse().convertAll(CSVUtils.splitLinesWithGuava(lastLight));
				 for(LightData newData:newDatas){
					 addData(newData,false);
					 cellObjects.setSelected(newData, true);//maybe last selected
				 }
				 storeData();
				}
			}
		}
		
	});
	
}
	
	private String baseFileName="LightData";
	protected String getDownloadFileName() {
		return baseFileName+".csv";
	}

	public LightData copy(LightData data){
		LightDataConverter converter=new LightDataConverter();
		String text=converter.convert(data);
		return converter.reverse().convert(text);
	}
	
	protected LightData makeNewData() {
		return new LightData();
	}


	public void addData(LightData data,boolean updateStorages) {
		cellObjects.addItem(data);
		onDataAdded(data);//link something
		
		if(updateStorages){
			storeData();
		}
	}
	
	private void storeData(){
		 storeData( toStoreText());
	}
	
	private void storeData(String lines){
		
		 
		// LogUtils.log("lines:"+lines.length());
		 
		 try {
			storageControler.setValue(storageKey, lines);
		} catch (StorageException e) {
			//possible quote error
			Window.alert(e.getMessage());
		}
	}
	
	protected void clearAllData() {
		 for(LightData data:ImmutableList.copyOf(cellObjects.getDatas())){
				removeData(data);
			}
	}
	
	 public void removeData(LightData data){
		 if(data==null){
			 return;
		 }
		 cellObjects.removeItem(data);
		 onDataRemoved(data);
	 }
	
	public String toStoreText(){
		return Joiner.on("\r\n").join(new LightDataConverter().convertAll(cellObjects.getDatas()));
	 }

	public void onDataSelected(@Nullable LightData selection) {
		
	}

	public void onDataRemoved(LightData data){
		if(data.hasLight()){
			GWTThreeClothHair.INSTANCE.getScene().remove(data.getLight());
		}
		storeData();
	}
	public void onDataAdded(LightData data){
		
		LightUtils.addLight(GWTThreeClothHair.INSTANCE.getScene(), data);
		
	}
	
	public boolean sameType(int type,String typeString){
		if(type==LightData.AMBIENT){
			return "AmbientLight".equals(typeString);
		}else if(type==LightData.DIRECTIONAL){
			return "DirectionalLight".equals(typeString);
		}else if(type==LightData.HEMISPHERE){
			return "HemisphereLight".equals(typeString);
		}
		
		return false;
	}
	public void onDataUpdated(LightData data){
		
		
		if(!data.hasLight()){
			LogUtils.log("onDataUpdated:no light");
			return;
		}
		
		if(!sameType(data.getType(), data.getLight().getType())){
			LogUtils.log("onDataUpdated:type changed");
			onDataRemoved(data);
			onDataAdded(data);
			return;
		}
		
		
		
		
		if(data.getType()==LightData.AMBIENT){
			AmbientLight light=data.getLight().cast();
			light.setName(data.getName());
			light.setIntensity(data.getIntensity());
			light.getColor().setHex(data.getColor());
			light.getPosition().copy(data.getPosition());//no effect
			
			
			
		}else if(data.getType()==LightData.DIRECTIONAL){
			DirectionalLight light=data.getLight().cast();
			light.setName(data.getName());
			light.setIntensity(data.getIntensity());
			light.getColor().setHex(data.getColor());
			light.getPosition().copy(data.getPosition());
			
			LightUtils.updateCastShadow(data,light);
			
		}else if(data.getType()==LightData.HEMISPHERE){
			HemisphereLight light=data.getLight().cast();
			light.setName(data.getName());
			light.setIntensity(data.getIntensity());
			light.getColor().setHex(data.getColor());
			light.getGroundColor().setHex(data.getColor2());
			light.getPosition().copy(data.getPosition());
		}
	}
	
	
	public void updateData(LightData data){
		storeData();
		//TODO link something
		cellObjects.getSimpleCellTable().getCellTable().redraw();
		
		onDataUpdated(data);
	}

	
	public class LightDataEditor extends VerticalPanel implements Editor<LightData>,ValueAwareEditor<LightData>{
		private LightData value;
		private ListBox typeEditor;
		private TextBox nameEditor;
		private ColorBox colorEditor;
		private ColorBox color2Editor;
		private LabeledInputRangeWidget2 intensityEditor;
		private SimpleVector3Editor positionEditor;
		private CheckBox castShadowEditor;
		public LightData getValue() {
			return value;
		}
		
		public LightDataEditor(){
				HorizontalPanel h1=new HorizontalPanel();
				this.add(h1);
				typeEditor = new ListBox();
				h1.add(typeEditor);
				typeEditor.addItem("Direction");
				typeEditor.addItem("Ambient");
				typeEditor.addItem("Hemisphere");
				typeEditor.addChangeHandler(new ChangeHandler() {
					
					@Override
					public void onChange(ChangeEvent event) {
						flush();
					}
				});
				
				h1.add(new Label("Name:"));
				nameEditor = new TextBox();
				nameEditor.setWidth("120px");
				nameEditor.addChangeHandler(new ChangeHandler() {
					
					@Override
					public void onChange(ChangeEvent event) {
						flush();
					}
				});
				h1.add(nameEditor);
				
				
				
				
				HorizontalPanel h2=new HorizontalPanel();
				this.add(h2);
				h2.add(new Label("Color1"));
				colorEditor = new ColorBox();
				colorEditor.addValueChangeHandler(new ValueChangeHandler<String>() {
					
					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						flush();
					}
				});
				colorEditor.setValue("#ffffff");
				h2.add(colorEditor);
				
				h2.add(new Label("Color2"));
				color2Editor = new ColorBox();
				color2Editor.addValueChangeHandler(new ValueChangeHandler<String>() {

					@Override
					public void onValueChange(ValueChangeEvent<String> event) {
						flush();
					}
				});
				color2Editor.setValue("#ffffff");
				h2.add(color2Editor);
				
				intensityEditor = new LabeledInputRangeWidget2("intensity", 0, 5, 0.01);
				intensityEditor.addtRangeListener(new ValueChangeHandler<Number>() {

					@Override
					public void onValueChange(ValueChangeEvent<Number> event) {
						flush();
					}
				});
				add(intensityEditor);
				
				
				castShadowEditor=new CheckBox("cast shadow");
				castShadowEditor.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
					@Override
					public void onValueChange(ValueChangeEvent<Boolean> event) {
						flush();
					}
				});
				add(castShadowEditor);
				
				//TODO make vector3editor
				
				positionEditor = new SimpleVector3Editor(new SimpleVector3EditorListener() {
					
					@Override
					public void onValueChanged(Vector3 value) {
						flush();
					}
				});
				add(positionEditor);
				
		}
@Override
			public void setDelegate(EditorDelegate<LightData> delegate) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void flush() {
				if(value==null){
					return;
				}
				
				value.setType(typeEditor.getSelectedIndex());
				value.setName(nameEditor.getValue());
				value.setColor(ColorUtils.toColor(colorEditor.getValue()));
				value.setColor2(ColorUtils.toColor(color2Editor.getValue()));
				value.setIntensity(intensityEditor.getValue());

				value.setCastShadow(castShadowEditor.getValue());
				//position value's value already linked no need
				
				updateData(value);
			}

			@Override
			public void onPropertyChange(String... paths) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setValue(@Nullable LightData value) {
				this.value=value;
				if(value==null){
					//set disable
					typeEditor.setEnabled(false);
					nameEditor.setEnabled(false);
					//colorEditor.setEnabled(false);
					//color2Editor.setEnabled(false);
					intensityEditor.setEnabled(false);

					castShadowEditor.setEnabled(false);
					
					return;
				}else{
					//set enable
					typeEditor.setEnabled(true);
					nameEditor.setEnabled(true);
					//colorEditor.setEnabled(true);
					//color2Editor.setEnabled(true);
					intensityEditor.setEnabled(true);
					castShadowEditor.setEnabled(true);
				}
				
				typeEditor.setSelectedIndex(value.getType());
				nameEditor.setValue(value.getName());
				colorEditor.setValue(ColorUtils.toCssColor(value.getColor()));
				color2Editor.setValue(ColorUtils.toCssColor(value.getColor2()));
				intensityEditor.setValue(value.getIntensity());
				castShadowEditor.setValue(value.isCastShadow());
				
				positionEditor.setValue(value.getPosition());

			}
	}
}
