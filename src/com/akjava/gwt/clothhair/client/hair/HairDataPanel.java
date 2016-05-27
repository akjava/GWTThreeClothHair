package com.akjava.gwt.clothhair.client.hair;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.akjava.gwt.clothhair.client.GWTThreeClothHair;
import com.akjava.gwt.clothhair.client.HairStorageKeys;
import com.akjava.gwt.clothhair.client.cloth.ClothData;
import com.akjava.gwt.clothhair.client.cloth.HairCloth;
import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.akjava.gwt.clothhair.client.hair.HairDataFunctions.HairPinToVertex;
import com.akjava.gwt.clothhair.client.texture.HairTextureDataEditor;
import com.akjava.gwt.html5.client.download.HTML5Download;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.html5.client.input.ColorBox;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.StorageControler;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.lib.client.experimental.ImageDataUtils;
import com.akjava.gwt.lib.client.widget.cell.EasyCellTableObjects;
import com.akjava.gwt.lib.client.widget.cell.SimpleCellTable;
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.BufferAttribute;
import com.akjava.gwt.three.client.js.core.BufferGeometry;
import com.akjava.gwt.three.client.js.core.Face3;
import com.akjava.gwt.three.client.js.loaders.ImageLoader.ImageLoadHandler;
import com.akjava.gwt.three.client.js.materials.MeshPhongMaterial;
import com.akjava.gwt.three.client.js.math.Matrix3;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.objects.LineSegments;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;
import com.akjava.gwt.three.client.js.textures.Texture;
import com.akjava.lib.common.utils.CSVUtils;
import com.akjava.lib.common.utils.ColorUtils;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Stopwatch;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.cellview.client.CellTable;
import com.google.gwt.user.cellview.client.TextColumn;
import com.google.gwt.user.client.Timer;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Anchor;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HairDataPanel extends VerticalPanel{
	 interface Driver extends SimpleBeanEditorDriver< HairData,  HairDataEditor> {}
	 Driver driver = GWT.create(Driver.class);
	 
	private EasyCellTableObjects<HairCellObjectData> cellObjects;

	private SkinnedMesh characterMesh;

	private HairPinPanel hairPinPanel;
	
	public HairCellObjectData getSelection(){
		return cellObjects.getSelection();
	}
	public List<HairCellObjectData> getDatas(){
		return cellObjects.getDatas();
	}
	private Label verticalDistanceLabel;
	public HairDataPanel(final SkinnedMesh characterMesh,HairTextureDataEditor hairTextureDataEditor,HairPinPanel hairPinPanel){
		this.characterMesh=characterMesh;
		this.hairPinPanel=hairPinPanel;
		
		HorizontalPanel h1=new HorizontalPanel();
		h1.add(new Label("Global Color"));
		ColorBox colorBox = new ColorBox("global color", ColorUtils.toCssColor(GWTThreeClothHair.INSTANCE.getGlobalHairColor()));
		h1.add(colorBox);
		colorBox.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				GWTThreeClothHair.INSTANCE.setGlobalHairColor(ColorUtils.toColor(event.getValue()));
				
				final Stopwatch watch=Stopwatch.createStarted();
				final int colorValue=ColorUtils.toColor(event.getValue());
				
				final Canvas canvas=CanvasUtils.createCanvas(2048, 2048);
				LogUtils.log("create canvas:"+watch.elapsed(TimeUnit.MILLISECONDS));watch.reset();watch.start();
				THREE.ImageLoader().load(GWTThreeClothHair.INSTANCE.textureUrl, new ImageLoadHandler() {
					
					@Override
					public void onProgress(NativeEvent progress) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onLoad(ImageElement imageElement) {
						LogUtils.log("load:"+watch.elapsed(TimeUnit.MILLISECONDS));watch.reset();watch.start();
						ImageData data=ImageDataUtils.create(canvas, imageElement);
						LogUtils.log("imageData:"+watch.elapsed(TimeUnit.MILLISECONDS));watch.reset();watch.start();
						ImageDataUtils.replaceColor(data,GWTThreeClothHair.INSTANCE.defaultHairTextureColor,colorValue);
						LogUtils.log("replace:"+watch.elapsed(TimeUnit.MILLISECONDS));watch.reset();watch.start();
						ImageDataUtils.putImageData(data, canvas);
						LogUtils.log("putData:"+watch.elapsed(TimeUnit.MILLISECONDS));watch.reset();watch.start();
						Texture texture=THREE.TextureLoader().load(canvas.toDataUrl());
						LogUtils.log("make texture:"+watch.elapsed(TimeUnit.MILLISECONDS));watch.reset();watch.start();
						//texture.setFlipY(false);
						GWTThreeClothHair.INSTANCE.getBodyMaterial().setMap(texture);
						LogUtils.log("make texture:"+watch.elapsed(TimeUnit.MILLISECONDS));watch.reset();watch.start();
						//extremly slow
						for(HairCellObjectData cellData:cellObjects.getDatas()){
							//TODO check use local or not
							if(!cellData.getHairData().getHairTextureData().isUseLocalColor()){
							GWTThreeClothHair.INSTANCE.updateHairTextureColor(cellData,colorValue);
							}
						}
					}
					
					@Override
					public void onError(NativeEvent error) {
						// TODO Auto-generated method stub
						
					}
				});
			}
		});
		this.add(h1);
		
		
		
		VerticalPanel hairPanel=new VerticalPanel();
		this.add(hairPanel);

		
		
		HorizontalPanel showHairPanel=new HorizontalPanel();
		hairPanel.add(showHairPanel);
		CheckBox showHair=new CheckBox("show hairs");
		showHair.setValue(true);
		showHairPanel.add(showHair);
		showHair.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				for(HairCellObjectData data:cellObjects.getDatas()){
					data.getMesh().setVisible(event.getValue());
				}
			}
		});
		

		editor = new HairDataEditor(this);
		editor.setHairTextureDataEditor(hairTextureDataEditor);
		editor.setHairPinPanel(hairPinPanel);
		driver.initialize(editor);
		
		
		hairPanel.add(editor);
		createClothPanel(hairPanel);
		
		HorizontalPanel distancePanel=new HorizontalPanel();
		hairPanel.add(distancePanel);
		pinsLabel=new Label("Pins:");
		pinsLabel.setWidth("110px");
		distancePanel.add(pinsLabel);
		
		horizontalDistanceLabel = new Label("H:");
		horizontalDistanceLabel.setWidth("100px");
		distancePanel.add(horizontalDistanceLabel);
		
		verticalDistanceLabel = new Label("V:");
		verticalDistanceLabel.setWidth("100px");
		distancePanel.add(verticalDistanceLabel);
		
		driver.edit(new HairData());//new data
		
		
		SimpleCellTable<HairCellObjectData> table=new SimpleCellTable<HairCellObjectData>() {
			@Override
			public void addColumns(CellTable<HairCellObjectData> table) {
				TextColumn<HairCellObjectData> channelColumn=new TextColumn<HairCellObjectData>() {
					@Override
					public String getValue(HairCellObjectData object) {
						return String.valueOf(object.getHairData().getChannel());
						//return hairDataConverter.convert(object.getHairData());
					}
				};
				table.addColumn(channelColumn,"CH");
				
				TextColumn<HairCellObjectData> pinsColumn=new TextColumn<HairCellObjectData>() {
					@Override
					public String getValue(HairCellObjectData object) {
						
						return String.valueOf(object.getHairData().getHairPins().size());
						//return hairDataConverter.convert(object.getHairData());
					}
				};
				table.addColumn(pinsColumn,"pin");
				
				
				TextColumn<HairCellObjectData> distanceColumn=new TextColumn<HairCellObjectData>() {
					@Override
					public String getValue(HairCellObjectData object) {
						double width=HairDataUtils.getTotalPinDistance(object.getHairData(), characterMesh,false);
						String text=String.valueOf(width);
						
						return text.substring(0,Math.min(7, text.length()));
					}
				};
				table.addColumn(distanceColumn,"distance");
				
				TextColumn<HairCellObjectData> nameColumn=new TextColumn<HairCellObjectData>() {
					@Override
					public String getValue(HairCellObjectData object) {
						
						return Strings.padStart(String.valueOf(object.getHairData().getSizeOfU()),2,'0')+","+
								Strings.padStart(String.valueOf(object.getHairData().getSizeOfV()),2,'0')+","+
						object.getHairData().getScaleOfU();
						//return hairDataConverter.convert(object.getHairData());
					}
				};
				table.addColumn(nameColumn,"UVS");
				
				TextColumn<HairCellObjectData> syncColumn=new TextColumn<HairCellObjectData>() {
					@Override
					public String getValue(HairCellObjectData object) {
						
						return String.valueOf(object.getHairData().isSyncMove());
						//return hairDataConverter.convert(object.getHairData());
					}
				};
				table.addColumn(syncColumn,"sync");
				
				/*
				TextColumn<HairCellObjectData> nameColumn=new TextColumn<HairCellObjectData>() {
					@Override
					public String getValue(HairCellObjectData object) {
						
						Vector3 vec=hairPinToVertex(characterMesh, object.getHairData().getHairPins().get(0), false);
						return vec.getX()+","+vec.getY()+","+vec.getZ();
						//return hairDataConverter.convert(object.getHairData());
					}
				};
				table.addColumn(nameColumn,"1st-pin");
				*/
			}
		};
		hairPanel.add(table);
		
		HorizontalPanel editPanel=new HorizontalPanel();
		hairPanel.add(editPanel);
		
		
		Button edit=new Button("remove & edit",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HairCellObjectData data=cellObjects.getSelection();
				if(data!=null){
					removeHairData(data);
					
					driver.edit(data.getHairData());
					if(data.getHairData().getHairPins().size()<=3){
					firstSelection=data.getHairData().getHairPins().get(0);
					secondSelection=data.getHairData().getHairPins().get(1);
					if(data.getHairData().getHairPins().size()>2){
						thirdSelection=data.getHairData().getHairPins().get(2);
					}
					updateHairPinsByThreePoints();
					}else{
						hairPins.clear();
						for(HairPin pin:data.getHairData().getHairPins()){
							hairPins.add(pin);
						}
					}
					updateHairPinPanel();
					//LogUtils.log(hairDataConverter.convert(data.getHairData()));
					updateHairDataLine();
					
				}
			}
		});
		editPanel.add(edit);
		
		
		
		Button copy=new Button("Copy",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HairCellObjectData data=cellObjects.getSelection();
				if(data!=null){
					HairData copied=data.getHairData().clone();
					driver.edit(copied);
					updateDistanceLabel();
				}
			}
		});
		editPanel.add(copy);
		
		Button remove=new Button("remove",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HairCellObjectData data=cellObjects.getSelection();
				if(data!=null){
					removeHairData(data);
				}
			}
		});
		editPanel.add(remove);
		
		Button removeAll=new Button("remove ll",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				boolean confirm=Window.confirm("remove all?");
				if(!confirm){
					return;
				}
				clearAllHairData();
			}
		});
		editPanel.add(removeAll);
		
		
		cellObjects = new EasyCellTableObjects<HairCellObjectData>(table){
			@Override
			public void onSelect(HairCellObjectData selection) {
				if(selection==null){
					editor.getHairTextureDataEditor().setValue(null);
					return;
				}
				// TODO Auto-generated method stub
				//editor edit
				editor.getHairTextureDataEditor().setValue(selection.getHairData().getHairTextureData());
			}};
			
			
		//
		 String text=storageControler.getValue(HairStorageKeys.temp_hairset, null);
		 if(text!=null && !text.isEmpty()){
			 Iterable<HairData> hairDatas=hairDataConverter.reverse().convertAll(CSVUtils.splitLinesWithGuava(text));
			 final List<HairData> loadingDatas=Lists.newArrayList(hairDatas);
			 //this initial load make problem,without sync because of sharing canvas(without share,crash lack of memory)
			 new Timer(){
				@Override
				public void run() {
					if(!GWTThreeClothHair.INSTANCE.isUpdatingHairTextureMap()){
						HairData hairData=loadingDatas.remove(0);
						addCloth(hairData,false);//reading no need store
						if(loadingDatas.isEmpty()){
							cancel();
						}
					}
				}
				 
			 }.scheduleRepeating(10);
		 }
		
		 
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
				if(uploadModeBox.getSelectedIndex()==0){
					clearAllHairData();
				}
				//todo check validate
				
				 Iterable<HairData> hairDatas=hairDataConverter.reverse().convertAll(CSVUtils.splitLinesWithGuava(text));
				 for(HairData hairData:hairDatas){
					 addCloth(hairData);
				 }
				 
				
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
				Anchor a=HTML5Download.get().generateTextDownloadLink(text, "hair.csv", "click to download",true);
				download.add(a);
			}
		});
		 downloadPanels.add(downloadBt);
		 downloadPanels.add(download);
		 
		 
		 
		 
		 //add
		 hairPanel.add(uploadPanel);
		 hairPanel.add(downloadPanels);
	}
	
	public void updateHairPinPanel(){
		hairPinPanel.setHairPins(hairPins);
	}
	
	public static class HairCellObjectData{
		private HairData hairData;
		public HairCellObjectData(HairData hairData, ClothData clothData, Mesh mesh) {
			super();
			this.hairData = hairData;
			this.clothData = clothData;
			this.mesh = mesh;
		}
		public HairData getHairData() {
			return hairData;
		}
		public void setHairData(HairData hairData) {
			this.hairData = hairData;
		}
		public ClothData getClothData() {
			return clothData;
		}
		public void setClothData(ClothData clothData) {
			this.clothData = clothData;
		}
		public Mesh getMesh() {
			return mesh;
		}
		public void setMesh(Mesh mesh) {
			this.mesh = mesh;
		}
		private ClothData clothData;
		private Mesh mesh;
	}
	
private void createClothPanel(Panel parent){
		
		//i feel no more first second third
		HorizontalPanel h=new HorizontalPanel();
		//parent.add(h);
		
		Button first=new Button("first",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				firstSelection=currentSelection;
				clearAllPoints();
				updateHairPinsByThreePoints();
				updateHairDataLine();
			}
		});
		h.add(first);
		
		
		Button second=new Button("second",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				secondSelection=currentSelection;
				clearAllPoints();
				updateHairPinsByThreePoints();
				updateHairDataLine();
			}
		});
		h.add(second);
		
		Button third=new Button("third",new ClickHandler() {
			

			@Override
			public void onClick(ClickEvent event) {
				thirdSelection=currentSelection;
				clearAllPoints();
				updateHairPinsByThreePoints();
				updateHairDataLine();
			}
		});
		h.add(third);
		
		Button clear=new Button("clear third",new ClickHandler() {
			

			@Override
			public void onClick(ClickEvent event) {
				thirdSelection=null;
				clearAllPoints();
				updateHairPinsByThreePoints();
				updateHairDataLine();
			}
		});
		h.add(clear);
		
		
		/*
		Button addCloth=new Button("add cloth",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addCloth();
				clearAllPoints();
			}
		});
		h.add(addCloth);
		*/
		
		
		HorizontalPanel h2=new HorizontalPanel();
		parent.add(h2);
		
		HorizontalPanel h3=new HorizontalPanel();
		parent.add(h3);
		
		Button addPoint=new Button("add point",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				executeAddPoint();
				
			}
		});
		h2.add(addPoint);
		
		Button addPointFirst=new Button("add point first",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				hairPins.add(0,currentSelection);
				updateHairPinPanel();
				updateHairDataLine();
			}
		});
		h2.add(addPointFirst);
		
		Button removeLastPoint=new Button("remove last",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(hairPins.size()>0){
					hairPins.remove(hairPins.size()-1);
				}
				updateHairPinPanel();
				updateHairDataLine();
			}
		});
		h3.add(removeLastPoint);
		
		
		Button removeSelectedPoint=new Button("remove selection",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				/*
				 * it's hard to re-select same face by hand
				 */
				
				/*
				LogUtils.log("selection:"+currentSelection);
				for(HairPin pin:hairPins){
					LogUtils.log("contain:"+pin);
				}
				*/
				
				hairPins.remove(currentSelection);
				updateHairDataLine();
			}
		});
		//stop using here
		//h3.add(removeSelectedPoint);
		
		
		
		Button removeAllPoint=new Button("remove all point",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				clearAllPoints();
				
				updateHairDataLine();
			}
		});
		h3.add(removeAllPoint);
		
		Button addCloth2=new Button("add cloth",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addCloth();
				clearAllPoints();
			}
		});
		h2.add(addCloth2);
	}

public void executeAddPoint(){
	//LogUtils.log(currentSelection);
	if(currentSelection==null){
		return;
	}
	addPin(currentSelection);
}

public void addPin(HairPin pin){
	hairPins.add(currentSelection);
	updateHairDataLine();
	updateHairPinPanel();
}


private void clearAllPoints(){
	firstSelection=null;
	secondSelection=null;
	thirdSelection=null;
	hairPins.clear();
	if(hairDataLine!=null){
		GWTThreeClothHair.INSTANCE.getScene().remove(hairDataLine);
		hairDataLine=null;
	}
	updateHairPinPanel();
}

	void updateDistanceLabel(){
		List<HairPin> pins=Lists.newArrayList();
		
		for(HairPin pin:hairPins){
			if(pin!=null){
				pins.add(pin);
			}
		}
		
		pinsLabel.setText("Pins:"+pins.size()+" p="+HairCloth.calcurateParticleSize(pins,editor.getSizeOfU(),editor.getSizeOfV())
				+",w="+HairCloth.calcurateWSize(pins,editor.getSizeOfU())
				);
		
		double distance=HairDataUtils.getTotalPinDistance(pins, characterMesh, false);
		horizontalDistanceLabel.setText(
				("H:"+distance).substring(0,7)
						);
		//no need * scaleof u
		//update and make method
		int w = (pins.size()-1)*editor.getSizeOfU();
		int h= editor.getSizeOfV();
		
		
		
		double vdistanceh=HairDataUtils.getTotalVDistance(distance*editor.getScaleOfU(), w, h);
		double ratio=vdistanceh/distance;
		verticalDistanceLabel.setText(
				("V:"+vdistanceh).substring(0,7)+" ratio="+String.valueOf(ratio).substring(0,4)
						);
	}

	private HairDataConverter hairDataConverter=new HairDataConverter();
	protected void removeHairData(HairCellObjectData data) {
		checkNotNull(data,"removeHairData:data is null");
		GWTThreeClothHair.INSTANCE.getScene().remove(data.getMesh());
		GWTThreeClothHair.INSTANCE.getClothControler().removeClothData(data.getClothData());
		cellObjects.removeItem(data);
		
		storeDatas();
	}
	private HairPin currentSelection;
	
	
	public void setCurrentSelection(HairPin currentSelection) {
		this.currentSelection = currentSelection;
	}


	LineSegments selectedLine;
	
	LineSegments hairDataLine;
	
	
	
	
	private HairPin firstSelection;
	private HairPin secondSelection;
	private HairPin thirdSelection;
	
	
	public  void setVertexVisible(boolean value){
		if(selectedLine!=null){
			selectedLine.setVisible(value);
		}
		if(hairDataLine!=null){
			hairDataLine.setVisible(value);
		}
	}
	
	public void setNewLine(LineSegments line){
		if(selectedLine!=null){
			GWTThreeClothHair.INSTANCE.getScene().remove(selectedLine);
		}
		
		selectedLine=line;
		
		if(line!=null){
			GWTThreeClothHair.INSTANCE.getScene().add(selectedLine);
		}
	}

	
List<HairPin> hairPins=Lists.newArrayList();

public void updateHairPinsByThreePoints(){
	hairPins.clear();
	hairPins.add(firstSelection);
	hairPins.add(secondSelection);
	hairPins.add(thirdSelection);
}

	
public void updateHairDataLine(){
	
	updateDistanceLabel();
	
	if(hairPins.size()<2){
		//i'M not sure why limit 2?
		//return;
	}
	
	
	List<HairPin> pins=Lists.newArrayList();
	
	for(HairPin pin:hairPins){
		if(pin!=null){
			pins.add(pin);
		}
	}
	


	
	if(hairDataLine!=null){
		GWTThreeClothHair.INSTANCE.getScene().remove(hairDataLine);
		hairDataLine=null;
	}
	
	if(pins.size()>0){
		HairPinToVertex hairPinToVertex=new HairPinToVertex(characterMesh,true);
		Matrix3 normalMatrix=THREE.Matrix3();
		normalMatrix.getNormalMatrix( characterMesh.getMatrixWorld());
		
		
		BufferGeometry geometry = THREE.BufferGeometry();

		BufferAttribute positions = THREE.Float32Attribute( 2*3 * pins.size(), 3 );
		geometry.addAttribute( "position", positions );
		
		
		
		
		
		double size=32;
		for(int i=0;i<pins.size();i++){
			HairPin pin=pins.get(i);
			
			Vector3 v2 = THREE.Vector3();
			Vector3 v1=hairPinToVertex.apply(pin);
			
			Face3 face=characterMesh.getGeometry().getFaces().get(pin.getFaceIndex());
			Vector3 normal = face.getVertexNormals().get(pin.getVertexOfFaceIndex());
			v2.copy( normal ).applyMatrix3( normalMatrix ).normalize().multiplyScalar( size ).add( v1 );
			
			positions.setXYZ(2*i+ 0, v1.getX(), v1.getY(), v1.getZ() );
			positions.setXYZ(2*i+ 1, v2.getX(), v2.getY(), v2.getZ() );
			
		}
		hairDataLine=THREE.LineSegments(geometry.gwtCastGeometry(), THREE.LineBasicMaterial(GWTParamUtils.LineBasicMaterial().color(0x0000ff).linewidth(4)));
		GWTThreeClothHair.INSTANCE.getScene().add(hairDataLine);
	}
	
	
	
	
}

public Vector3 hairPinToVertex(Mesh mesh,HairPin hairPin,boolean applyMatrix4){
	checkNotNull(mesh,"hairPinToVertex:mesh is null");
	checkNotNull(hairPin,"hairPinToVertex:hairPin is null");
	checkArgument(mesh.getGeometry().getFaces().length()>hairPin.getFaceIndex(),"hairPinToVertex:invalid face length");
	Face3 face=mesh.getGeometry().getFaces().get(hairPin.getFaceIndex());
	Vector3 vertex;
	if(hairPin.getVertexOfFaceIndex()==0){
		vertex=mesh.getGeometry().getVertices().get(face.getA());
	}else if(hairPin.getVertexOfFaceIndex()==1){
		vertex=mesh.getGeometry().getVertices().get(face.getB());
	}else{
		vertex=mesh.getGeometry().getVertices().get(face.getC());
	}
	
	//TODO support direct point
	
	if(applyMatrix4){
	return vertex.clone().applyMatrix4( mesh.getMatrixWorld());
	}else{
	return vertex.clone();
	}
	
}

	

	protected void addCloth() {
		List<HairPin> pins=Lists.newArrayList();
		
		Map<String,String> same=Maps.newHashMap();
		for(HairPin pin:hairPins){
			if(pin==null){
				continue;
			}
			String exist=same.get(pin.toString());
			same.put(pin.toString(), "");
			if(exist!=null){//TODO use set? for ignore point?
				LogUtils.log("addCloth:duplicate point ignored");
				continue;
			}
			pins.add(pin);
		}
		
		
		if(pins.size()<2){
			LogUtils.log("addCloth:need 2 pont");
			return;
		}
		
		
		/*
		Vector3 v1=hairPinToVertex(characterMesh,firstSelection,true);
		Vector3 v2=hairPinToVertex(characterMesh,secondSelection,true);
		
		double distance=v1.distanceTo(v2);
		
		if(distance==0){
			LogUtils.log("invalidly first & second same:"+ThreeLog.get(v1)+","+ThreeLog.get(v1));
			return;
		}
		*/
		
		HairData hairData=driver.flush();
		
		driver.edit(new HairData());//add another one.
		
		
		hairData.getHairPins().clear();
		
		for(HairPin pin:pins){
			hairData.getHairPins().add(pin);
		}
		
		addCloth(hairData);
	}
	
	public void setHairTextureDataEditor(HairTextureDataEditor hairTextureDataEditor) {
		this.editor.setHairTextureDataEditor(hairTextureDataEditor);
	}
	
	protected void addCloth(HairData hairData) {
		addCloth(hairData,true);
	}
	protected void addCloth(HairData hairData,boolean storeData) {
		
		ClothData data=new ClothData(hairData,characterMesh);
		GWTThreeClothHair.INSTANCE.getClothControler().addClothData(data);
		
		data.getCloth().setPinAll();
		
		
		//data.getCloth().ballSize=clothControls.getBallSize();
		
	
		//indivisual haiar material
		
		Texture texture=THREE.TextureLoader().load(
				//"models/mbl3d/bump2c.png"
				"/hairpattern/hairpattern1.png"
				);
		
		
		texture.setFlipY(false);
		texture.setNeedsUpdate(true);
		
		//displacementMap not good at plain when row-poly
		
		//little bit 
		MeshPhongMaterial hairMaterial = THREE.MeshPhongMaterial(GWTParamUtils.
				MeshPhongMaterial()
				.side(THREE.DoubleSide)
				.transparent(true)
				
				.specular(0x111111)
				//.specular(0xffffff)
				.shininess(5) //switch default same as texture otherwise not good at connection
				//.wireframe(true)
				.specular(0xffffff)//TODO move editor
				.specularMap(texture)
				//.shininess(15)
				
				
				/*
				.displacementMap(texture)
				.displacementScale(16)
				.displacementBias(4)
				*/
				
				.bumpMap(texture)
				.bumpScale(0.5)
				
				//.di
				);
		/*
		GWTParamUtils.
				MeshPhongMaterial()
				.color(hairData.getHairTextureData().getColor()).side(THREE.DoubleSide).specular(0xffffff).shininess(15)
				.alphaTest(hairData.getHairTextureData().getAlphaTest())
				.transparent(true)
				.opacity(hairData.getHairTextureData().getOpacity())
				);
			*/
		
		Mesh object = THREE.Mesh( data.getClothGeometry(), hairMaterial );
		//object.getPosition().set( 0, 0, 0 );
		
		GWTThreeClothHair.INSTANCE.getScene().add( object );
		
		HairCellObjectData cellData=new HairCellObjectData(hairData,data,object);
		cellObjects.addItem(cellData);
		cellObjects.setSelected(cellData, true);
		
		
		//temporaly
		
		if(hairData.getHairPins().size()<3){
		Vector3 v1=hairPinToVertex(characterMesh,hairData.getHairPins().get(0),true);
		Vector3 v2=hairPinToVertex(characterMesh,hairData.getHairPins().get(1),true);
		
		//TODO move and fix
		int cw=hairData.getSizeOfU();
		int ch=hairData.getSizeOfV();
		
		data.getCloth().particles.get(0).setAllPosition(v1);
		data.getCloth().particles.get(cw).setAllPosition(v2);
		
		
		Vector3 sub=v2.clone().sub(v1).divideScalar(cw+1);
		for(int i=1;i<cw;i++){
			Vector3 v=sub.clone().multiplyScalar(i).add(v1);
			data.getCloth().particles.get(i).setAllPosition(v);
		}
		
		for(int i=cw+1;i<data.getCloth().particles.size();i++){
			data.getCloth().particles.get(i).setAllPosition(v1);
		}
		
		}else{
			int cw=hairData.getSizeOfU();
			
			List<HairPin> normalPin=Lists.newArrayList();
			List<HairPin> customPin=Lists.newArrayList();
			
			for(HairPin pin:hairData.getHairPins()){
				if(pin.getTargetClothIndex()==-1){
					normalPin.add(pin);
				}else{
					customPin.add(pin);
				}
			}
			
			for(int i=0;i<normalPin.size();i++){
				Vector3 v1=hairPinToVertex(characterMesh,normalPin.get(i),true);
				int index=hairData.getSizeOfU()*i;
				data.getCloth().particles.get(index).setAllPosition(v1);
				
				//LogUtils.log("main:"+index);
				
				
				if(i!=normalPin.size()-1){
					//has next;
					Vector3 v2=hairPinToVertex(characterMesh,normalPin.get(i+1),true);
					Vector3 sub=v2.clone().sub(v1).divideScalar(cw);
					
					for(int j=1;j<cw;j++){
						int multiple=j;
						int at=index+j;
						Vector3 v=sub.clone().multiplyScalar(multiple).add(v1);
						data.getCloth().particles.get(at).setAllPosition(v);
						
						//LogUtils.log("sub:"+at);
					}
					
				}
				
				
			}
			
			//init other posisions
			for(int j=data.getCloth().getW()+1;j<data.getCloth().particles.size();j++){
				int x=j%(data.getCloth().getW()+1);
				//LogUtils.log(j+"="+x);
				Vector3 pos=data.getCloth().particles.get(x).getOriginal();
				//copy upper x
				data.getCloth().particles.get(j).setAllPosition(pos);
			}
			
			for(HairPin pin:customPin){
				Vector3 v=hairPinToVertex(characterMesh,pin,true);
				data.getCloth().particles.get(pin.getTargetClothIndex()).setAllPosition(v);
			}
			
			
			/*
			for(int i=0;i<cw*(hairData.getHairPins().size()-1);i++){
				ThreeLog.log(""+i, data.getCloth().particles.get(i).getOriginal());
			}
			*/
			
			int w=data.getCloth().getW()+1;
			int[] newPins=new int[w+customPin.size()];
			for(int i=0;i<w;i++){
				newPins[i]=i;
			}
			for(int i=0;i<customPin.size();i++){
				newPins[w+i]=customPin.get(i).getTargetClothIndex();
			}
			data.getCloth().setPins(newPins);
			
			/*
			LogUtils.log("pins");
			for(int i=0;i<newPins.length;i++){
				LogUtils.log(newPins[i]);
			}
			*/
		
			/*
			if(customPin.size()>0){
			int[] newPins=new int[customPin.size()];
			for(int i=0;i<customPin.size();i++){
				newPins[i]=customPin.get(i).getTargetClothIndex();
			}
			data.getCloth().setPins(newPins);
			
			LogUtils.log("pins");
			for(int i=0;i<newPins.length;i++){
				LogUtils.log(newPins[i]);
			}
			
			
			}
			*/
			
		}
		
		if(storeData){
			storeDatas();
		}
		
		GWTThreeClothHair.INSTANCE.updateHairTextureData(cellData,true);
	}
	
	public void storeDatas() {
		if(cellObjects.getDatas().size()==0){
			storageControler.removeValue(HairStorageKeys.temp_hairset);
			return;
		}
		
		String text=
				toStoreText();
		
		try {
			storageControler.setValue(HairStorageKeys.temp_hairset, text);
		} catch (StorageException e) {
			Window.alert(e.getMessage());
		}
		
	}
	
	private String toStoreText(){
		String text=
				Joiner.on("\r\n").join(hairDataConverter.convertAll(FluentIterable.from(cellObjects.getDatas()).transform(new Function<HairCellObjectData, HairData>() {
					@Override
					public HairData apply(HairCellObjectData input) {
						return input.getHairData();
					}
				})));
		return text;
	}

	private StorageControler storageControler=new StorageControler();

	private Label horizontalDistanceLabel;
	private Label pinsLabel;
	private HairDataEditor editor;
	
	private void clearAllHairData(){
		for(HairCellObjectData data:ImmutableList.copyOf(cellObjects.getDatas())){
			removeHairData(data);
		}
	}



}
