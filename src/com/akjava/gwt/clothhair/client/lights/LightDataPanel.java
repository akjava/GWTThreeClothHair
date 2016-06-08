package com.akjava.gwt.clothhair.client.lights;

import javax.annotation.Nullable;

import com.akjava.gwt.clothhair.client.sphere.SphereData;
import com.akjava.gwt.clothhair.client.sphere.SphereDataConverter;
import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.lib.common.utils.CSVUtils;
import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class LightDataPanel extends VerticalPanel{
	 
	private LightDataEditor editor;
	private EasyCellTableObjects<LightData> cellObjects;

	private String storageKey;
	private StorageControler storageControler;
	public LightDataPanel(String storageKey,StorageControler storageControler) {
		this.storageKey=storageKey;
		this.storageControler=storageControler;
		editor = new LightDataEditor();    
				
		this.add(editor);


	
	//create easy cell tables
	SimpleCellTable<LightData> table=new SimpleCellTable<LightData>() {
		@Override
		public void addColumns(CellTable<LightData> table) {
			TextColumn<LightData> nameColumn=new TextColumn<LightData>() {
				@Override
				public String getValue(LightData object) {
					return object.getName();
				}
			};
			table.addColumn(nameColumn);
			
			TextColumn<LightData> typeColumn=new TextColumn<LightData>() {
				@Override
				public String getValue(LightData object) {
					return ""+object.getType();
				}
			};
			table.addColumn(typeColumn);
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
	if(lines!=null){
		Iterable<LightData> datas=new LightDataConverter().reverse().convertAll(CSVUtils.splitLinesWithGuava(lines));
		for(LightData data:datas){
			addData(data,false);
		}
	}
	
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
	
	public void storeData(){
		 //store data
		 String lines=toStoreText();
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
		
	}
	public void onDataAdded(LightData data){
		
	}
	public void onDataUpdated(LightData data){
		//TODO link something
		cellObjects.getSimpleCellTable().getCellTable().redraw();
	}
	

	
	public class LightDataEditor extends VerticalPanel implements Editor<LightData>,ValueAwareEditor<LightData>{
		private LightData value;
		
		public LightData getValue() {
			return value;
		}
		
		public LightDataEditor(){

		}
@Override
			public void setDelegate(EditorDelegate<LightData> delegate) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void flush() {
				onDataUpdated(value);
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
				}else{
					//set enable
				}
			}
	}
}
