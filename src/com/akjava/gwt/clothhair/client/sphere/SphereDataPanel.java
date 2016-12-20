package com.akjava.gwt.clothhair.client.sphere;

import com.akjava.gwt.clothhair.client.GWTThreeClothHair;
import com.akjava.gwt.clothhair.client.GWTThreeClothHairStorageKeys;
import com.akjava.gwt.clothhair.client.cloth.HaveReselectSphere;
import com.akjava.gwt.clothhair.client.cloth.SphereDataControler;
import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.objects.Skeleton;
import com.akjava.lib.common.utils.CSVUtils;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class SphereDataPanel extends VerticalPanel {
	 interface Driver extends SimpleBeanEditorDriver< SphereData,  SphereDataEditor> {}
	 Driver driver = GWT.create(Driver.class);
	private EasyCellTableObjects<SphereData> cellObjects;
	 
	SphereDataControler controler;
	SphereData defaultValue;
	private SphereDataCsvConverter sphereDataConverter=new SphereDataCsvConverter();
	
	public static final int FILTER_ALL=0;
	public static final int FILTER_CHANNEL=1;
	public static final int FILTER_SELECTION=2;
	private int filterMode;
	
	 public SphereDataPanel(final SphereDataControler controler,final SphereData defaultValue){
		 this.controler=controler;
		 this.defaultValue=defaultValue;
		 sphereDataEditor = new SphereDataEditor(defaultValue,this);  
		 this.add(sphereDataEditor);
		 
		 driver.initialize(sphereDataEditor);
		 
		 driver.edit(null);
		 
		
		 /*
		  * trying add cloth via sphere
		 Button test=new Button("test",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				Vector3 axis = THREE.Vector3( 1, 0, 0 );
				double angle = Math.toRadians( -90 );
				
				SphereData data=cellObjects.getSelection();
				double radius=data.getSize();
				Vector3 center=data.getPosition().clone();
				for(int i=0;i<360;i+=30){
					double rad=Math.toRadians(i);
					double x=radius*Math.cos(rad);
					double y=radius*Math.sin(rad);
					Vector3 pt=THREE.Vector3(x, y, 0);
					pt.applyAxisAngle( axis, angle );
					
					
				}
			}
		});
		 this.add(test);
		 */
		 HorizontalPanel filterPanel=new HorizontalPanel();
		 filterPanel.setVerticalAlignment(ALIGN_MIDDLE);
		 this.add(filterPanel);
		 filterPanel.add(new Label("Show speres:"));
		 final ListBox filterBox=new ListBox();
		 filterBox.addItem("All");
		 filterBox.addItem("Selected Channel Only");
		 filterBox.addItem("Selection Only");
		 filterBox.setSelectedIndex(0);
		 filterBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				filterMode=filterBox.getSelectedIndex();
				updateSphereVisible();
			}
		});
		 filterPanel.add(filterBox);
		 
		 SimpleCellTable<SphereData> table=new SimpleCellTable<SphereData>(20) {
				@Override
				public void addColumns(CellTable<SphereData> table) {
					
					TextColumn<SphereData> channelColumn=new TextColumn<SphereData>() {
						@Override
						public String getValue(SphereData object) {
							return String.valueOf(object.getChannel());
						}
					};
					table.addColumn(channelColumn,"channel");
					
					TextColumn<SphereData> xColumn=new TextColumn<SphereData>() {
						@Override
						public String getValue(SphereData object) {
							return String.valueOf(object.getX());
						}
					};
					table.addColumn(xColumn,"x");
					
					
					TextColumn<SphereData> yColumn=new TextColumn<SphereData>() {
						@Override
						public String getValue(SphereData object) {
							return String.valueOf(object.getY());
						}
					};
					table.addColumn(yColumn,"y");
					
					TextColumn<SphereData> zColumn=new TextColumn<SphereData>() {
						@Override
						public String getValue(SphereData object) {
							return String.valueOf(object.getZ());
						}
					};
					table.addColumn(zColumn,"z");
					
					TextColumn<SphereData> typeColumn=new TextColumn<SphereData>() {
						@Override
						public String getValue(SphereData object) {
							return SphereData.getTypeLabel(object.getType());
						}
					};
					table.addColumn(typeColumn,"type");
				}
			};
			
			cellObjects = new EasyCellTableObjects<SphereData>(table){
				@Override
				public void onSelect(SphereData selection) {
					
					driver.edit(selection);
					
					controler.onSelectSphere(selection);
					
					updateSphereVisible();
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
			
			Button copyBt=new Button("copy",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					SphereData selection=cellObjects.getSelection();
					if(selection==null){
						return;
					}
					SphereData newData=new SphereDataConverter().copy(selection);
					addSpereData(newData);
					cellObjects.setSelected(newData, true);
					
				}
			});
			buttons.add(copyBt);
			
			Button copyHBt=new Button("Copy H",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					SphereData selection=cellObjects.getSelection();
					if(selection==null){
						return;
					}
					SphereData newData=new SphereDataConverter().copy(selection);
					newData.setX(newData.getX()*-1);
					addSpereData(newData);
					cellObjects.setSelected(newData, true);
					
				}
			});
			buttons.add(copyHBt);
			
			Button removeBt=new Button("Remove",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					
					removeSpereData(cellObjects.getSelection());
					
				}
			});
			buttons.add(removeBt);
			
			Button removeAll=new Button("Remove All",new ClickHandler() {
				@Override
				public void onClick(ClickEvent event) {
					boolean confirm=Window.confirm("remove all?");
					if(!confirm){
						return;
					}
					clearAllSphereData();
				}
			});
			buttons.add(removeAll);
			//make controls

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
					
					boolean isCsv=file.getFileName().toLowerCase().endsWith(".csv");
					Iterable<SphereData> sphereDatas=parseSphereDatas(text,isCsv);
					
					if(sphereDatas==null){
						return;
					}
					
					if(uploadModeBox.getSelectedIndex()==0){
						beforeUploadText=toStoreText();
						clearAllSphereData();
					}else{
						beforeUploadText=null;
					}
					
					loadSphereDatas(sphereDatas,file.getFileName().toLowerCase().endsWith(".csv"));
					
				}
			}, true, "UTF-8");
			 upload.setAccept(Lists.newArrayList(".csv",".json"));
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
					Anchor a=HTML5Download.get().generateTextDownloadLink(text, "spheres.json", "click to download",true);
					download.add(a);
				}
			});
			 downloadPanels.add(downloadBt);
			 downloadPanels.add(download);
			 
			 this.add(uploadPanel);
			 this.add(downloadPanels);
			
	 }
	 private String beforeUploadText;
	 
	 public Iterable<SphereData> parseSphereDatas(String text,boolean isCsv){
		 Iterable<SphereData> newDatas=null;
			if(isCsv){
				//csv can't check 
				LogUtils.log("load old csv format");
				newDatas=sphereDataConverter.reverse().convertAll(CSVUtils.splitLinesWithGuava(text));
				 for(SphereData newData:newDatas){
					// LogUtils.log("newData:"+newData.getWidth());
					//newData.setWidth(newData.getWidth()*2);//csv data is radius
				//	 LogUtils.log("newData-m:"+newData.getWidth());
					 //strangely if set here,clear value when read-newData
				 }
			}else{
				try{
				newDatas=new SphereDataListConverter().reverse().convert(text);
				
				}catch (Exception e) {
					Window.alert("invalid sphere data.skipped load");
					return null;
				}
				
			}
			return newDatas;
	 }
	 public void loadSphereDatas(Iterable<SphereData> newDatas,boolean isCsv){
		//todo check validate
			
			// Iterable<SphereData> newDatas=sphereDataConverter.reverse().convertAll(CSVUtils.splitLinesWithGuava(text));
			
			 for(SphereData newData:newDatas){
				// LogUtils.log("newData-a:"+newData.getWidth());
				 if(isCsv){
					 newData.setWidth(newData.getWidth()*2);//csv data used radius need double-size
				 }
				 addSpereData(newData);
				 cellObjects.setSelected(newData, true);//maybe last selected
			 }
	 }
	 
	 public void updateSphereVisible() {
		 SphereData selection=cellObjects.getSelection();
		 if(selection==null){
			 return;
		 }
		 for(SphereData data:cellObjects.getDatas()){
			 Mesh mesh=GWTThreeClothHair.INSTANCE.getClothSimulator().getSphereMesh(data);
			 Mesh mirror=GWTThreeClothHair.INSTANCE.getClothSimulator().getMirrorSphereMesh(data);
			 boolean visible=false;
				if(filterMode==FILTER_ALL){
					visible=true;
				}else if(filterMode==FILTER_CHANNEL){
					if(selection!=null){
						if(data.getChannel()==selection.getChannel()){
							visible=true;
						}
					}
				}else if(filterMode==FILTER_SELECTION){
					if(selection!=null){
						if(data==selection){
							visible=true;
						}
					}
				}
				mesh.setVisible(visible);
				if(mirror!=null){
					mirror.setVisible(visible);
				}
			}
	}

	protected void clearAllSphereData() {
		 for(SphereData data:ImmutableList.copyOf(cellObjects.getDatas())){
				removeSpereData(data);
			}
	}

	private SphereDataCsvConverter converter=new SphereDataCsvConverter();
	 private StorageControler storageControler=new StorageControler();
	private SphereDataEditor sphereDataEditor;
	 public void onFlushed(){
		//
		 GWTThreeClothHair.INSTANCE.getClothSimulator().syncSphereDataAndSkinningVertexCalculator(sphereDataEditor.getValue(), GWTThreeClothHair.INSTANCE);
		 
		 //store data
		 String lines=toStoreText();
		 try {
			storageControler.setValue(GWTThreeClothHairStorageKeys.KEY_SPHERES, lines);
		} catch (StorageException e) {
			//possible quote error
			LogUtils.log(e.getMessage());
		}
	 }
	 
	 public String toStoreText(){
		 
		 return new SphereDataListConverter().convert(cellObjects.getDatas());
		 
		//return Joiner.on("\r\n").join(converter.convertAll(cellObjects.getDatas()));
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
		 sphereDataEditor.setValue(null);
		 onFlushed();
	 }
	 
	 



		public void setSkelton(Skeleton skeleton) {
			
			sphereDataEditor.setSkelton(skeleton);
			
		}

}
