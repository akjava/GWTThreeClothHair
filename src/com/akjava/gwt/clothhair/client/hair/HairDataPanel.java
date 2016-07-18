package com.akjava.gwt.clothhair.client.hair;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import com.akjava.gwt.clothhair.client.GWTThreeClothHair;
import com.akjava.gwt.clothhair.client.GWTThreeClothHairStorageKeys;
import com.akjava.gwt.clothhair.client.SkinningVertexCalculator;
import com.akjava.gwt.clothhair.client.ammo.AmmoHairControler.ParticleBodyDatas;
import com.akjava.gwt.clothhair.client.cloth.ClothData;
import com.akjava.gwt.clothhair.client.cloth.ClothSimulator;
import com.akjava.gwt.clothhair.client.cloth.HairCloth;
import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.akjava.gwt.clothhair.client.hair.HairPinDataFunctions.HairPinToVertex;
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
import com.akjava.gwt.three.client.examples.js.THREEExp;
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.java.bone.CloseVertexAutoWeight;
import com.akjava.gwt.three.client.java.bone.WeightResult;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.BufferAttribute;
import com.akjava.gwt.three.client.js.core.BufferGeometry;
import com.akjava.gwt.three.client.js.core.Face3;
import com.akjava.gwt.three.client.js.core.Geometry;
import com.akjava.gwt.three.client.js.loaders.ImageLoader.ImageLoadHandler;
import com.akjava.gwt.three.client.js.materials.MeshPhongMaterial;
import com.akjava.gwt.three.client.js.math.Matrix3;
import com.akjava.gwt.three.client.js.math.Matrix4;
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
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.json.client.JSONArray;
import com.google.gwt.json.client.JSONNumber;
import com.google.gwt.json.client.JSONObject;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONString;
import com.google.gwt.json.client.JSONValue;
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
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HairDataPanel extends VerticalPanel{
	 interface Driver extends SimpleBeanEditorDriver< HairData,  HairDataEditor> {}
	 Driver driver = GWT.create(Driver.class);
	 
	private EasyCellTableObjects<HairMixedData> cellObjects;

	private SkinnedMesh characterMesh;

	private HairPinPanel hairPinPanel;
	
	public HairMixedData getSelection(){
		return cellObjects.getSelection();
	}
	public List<HairMixedData> getDatas(){
		return cellObjects.getDatas();
	}
	private Label verticalDistanceLabel;
	public HairDataPanel(final SkinnedMesh characterMesh,HairTextureDataEditor hairTextureDataEditor,HairPinPanel hairPinPanel){
		this.characterMesh=characterMesh;
		this.hairPinPanel=hairPinPanel;
		
		HorizontalPanel h1=new HorizontalPanel();
		h1.setSpacing(2);
		h1.setVerticalAlignment(ALIGN_MIDDLE);
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
						ImageDataUtils.replaceColor(data,ClothSimulator.defaultHairTextureColor,colorValue);
						LogUtils.log("replace:"+watch.elapsed(TimeUnit.MILLISECONDS));watch.reset();watch.start();
						ImageDataUtils.putImageData(data, canvas);
						LogUtils.log("putData:"+watch.elapsed(TimeUnit.MILLISECONDS));watch.reset();watch.start();
						Texture texture=THREE.TextureLoader().load(canvas.toDataUrl());
						texture.setMinFilter(THREE.NearestFilter);
						//Window.open(canvas.toDataUrl(), "test", null);
						
						LogUtils.log("make texture:"+watch.elapsed(TimeUnit.MILLISECONDS));watch.reset();watch.start();
						//texture.setFlipY(false);
						GWTThreeClothHair.INSTANCE.getBodyMaterial().setMap(texture);
						LogUtils.log("make texture:"+watch.elapsed(TimeUnit.MILLISECONDS));watch.reset();watch.start();
						//extremly slow
						for(HairMixedData cellData:cellObjects.getDatas()){
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
		h1.add(showHairPanel);
		showHair = new CheckBox("show hairs");
		showHair.setValue(true);
		showHairPanel.add(showHair);
		showHair.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				for(HairMixedData data:cellObjects.getDatas()){
					updateHairVisible(data,event.getValue());
					
				}
			}
		});
		
		showSelectionHair = new CheckBox("show selection only");
		
		showHairPanel.add(showSelectionHair);
		showSelectionHair.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				updateSelectionHairVisible(event.getValue());
	
			}
		});
		
		
		
		
		
		
		

		hairDataEditor = new HairDataEditor(this);
		hairDataEditor.setHairTextureDataEditor(hairTextureDataEditor);
		hairDataEditor.setHairPinPanel(hairPinPanel);
		driver.initialize(hairDataEditor);
		
		
		hairPanel.add(hairDataEditor);
		
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
		
		
		SimpleCellTable<HairMixedData> table=new SimpleCellTable<HairMixedData>() {
			@Override
			public void addColumns(CellTable<HairMixedData> table) {
				TextColumn<HairMixedData> channelColumn=new TextColumn<HairMixedData>() {
					@Override
					public String getValue(HairMixedData object) {
						return String.valueOf(object.getHairData().getChannel());
						//return hairDataConverter.convert(object.getHairData());
					}
				};
				table.addColumn(channelColumn,"CH");
				
				TextColumn<HairMixedData> pinsColumn=new TextColumn<HairMixedData>() {
					@Override
					public String getValue(HairMixedData object) {
						
						return String.valueOf(object.getHairData().getHairPins().size());
						//return hairDataConverter.convert(object.getHairData());
					}
				};
				table.addColumn(pinsColumn,"pin");
				
				
				TextColumn<HairMixedData> distanceColumn=new TextColumn<HairMixedData>() {
					@Override
					public String getValue(HairMixedData object) {
						double width=HairDataUtils.getTotalPinDistance(object.getHairData(), characterMesh,false);
						String text=String.valueOf(width);
						
						return text.substring(0,Math.min(7, text.length()));
					}
				};
				table.addColumn(distanceColumn,"distance");
				
				TextColumn<HairMixedData> nameColumn=new TextColumn<HairMixedData>() {
					@Override
					public String getValue(HairMixedData object) {
						
						return Strings.padStart(String.valueOf(object.getHairData().getSliceFaceCount()),2,'0')+","+
								Strings.padStart(String.valueOf(object.getHairData().getSizeOfV()),2,'0')+","+
						object.getHairData().getScaleOfU();
						//return hairDataConverter.convert(object.getHairData());
					}
				};
				table.addColumn(nameColumn,"UVS");
				
				TextColumn<HairMixedData> syncColumn=new TextColumn<HairMixedData>() {
					@Override
					public String getValue(HairMixedData object) {
						
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
		
		
		Button edit=new Button("Remove & Edit",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HairMixedData data=cellObjects.getSelection();
				if(data!=null){
					removeHairData(data);
					
					driver.edit(data.getHairData());
					if(data.getHairData().getHairPins().size()<=3){
					firstSelection=data.getHairData().getHairPins().get(0);
					secondSelection=data.getHairData().getHairPins().get(1);
					if(data.getHairData().getHairPins().size()>2){
						thirdSelection=data.getHairData().getHairPins().get(2);
					}else{
						thirdSelection=null;
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
				HairMixedData data=cellObjects.getSelection();
				if(data!=null){
					HairData copied=data.getHairData().clone();
					driver.edit(copied);
					updateDistanceLabel();
					cellObjects.unselect();
				}
			}
		});
		editPanel.add(copy);
		
		Button remove=new Button("Remove",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				HairMixedData data=cellObjects.getSelection();
				if(data!=null){
					removeHairData(data);
				}
			}
		});
		editPanel.add(remove);
		
		Button removeAll=new Button("Remove all",new ClickHandler() {
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
		
		Button reload=new Button("Reload",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GWTThreeClothHair.INSTANCE.resetAnimation();//without reset usually bad
				List<HairData> oldData=Lists.newArrayList();
				for(HairMixedData data:ImmutableList.copyOf(cellObjects.getDatas())){
					oldData.add(data.getHairData());
				}
				clearAllHairData();
				loadHairDataSync(oldData);
			}
		});
		editPanel.add(reload);
		reload.setTitle("reload cloths without reload page");
		
		
		
		cellObjects = new EasyCellTableObjects<HairMixedData>(table){
			@Override
			public void onSelect(HairMixedData selection) {
				
				updateSelectionHairVisible(showSelectionHair.getValue());
				
				if(selection==null){
					hairDataEditor.getHairTextureDataEditor().setValue(null);
					return;
				}
				// TODO Auto-generated method stub
				//editor edit
				hairDataEditor.getHairTextureDataEditor().setValue(selection.getHairData().getHairTextureData());
			}};
			
			
			
		//
		 String text=storageControler.getValue(GWTThreeClothHairStorageKeys.temp_hairset, null);
		 if(text!=null && !text.isEmpty()){
			 loadHairDataSync(text,false);
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
				GWTThreeClothHair.INSTANCE.resetAnimation();//without reset usually bad
				
				
				if(uploadModeBox.getSelectedIndex()==0){
					clearAllHairData();
				}
				//todo check validate
				
				boolean isCsv=file.getFileName().toLowerCase().endsWith(".csv");
				
				loadHairDataSync(text,isCsv);
				 
				
			}
		}, true, "UTF-8");
		 upload.setAccept(Lists.newArrayList(".csv",".json"));
		 uploadPanel.add(upload);
		 
		 //downloads
		 HorizontalPanel downloadPanels=new HorizontalPanel();
		 downloadPanels.setVerticalAlignment(HorizontalPanel.ALIGN_MIDDLE);
		 downloadArea = new HorizontalPanel();
		 
		 Button downloadBt=new Button("download",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				downloadArea.clear();
				String text=toStoreText();
				Anchor a=HTML5Download.get().generateTextDownloadLink(text, "hair.json", "click to download",true);
				downloadArea.add(a);
			}
		});
		 downloadPanels.add(downloadBt);
		
		 
		 Button downloadSelectionBt=new Button("selection",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					downloadArea.clear();
					if(cellObjects.getSelection()==null){
						return;
					}
					String type="simple";
					if(cellObjects.getSelection().getHairData().getHairPhysicsType()==HairData.TYPE_AMMO_BONE_CLOTH){
						type="ammo_bone_cloth";
					}else if(cellObjects.getSelection().getHairData().getHairPhysicsType()==HairData.TYPE_AMMO_CLOTH){
						type="ammo_cloth";
					}else if(cellObjects.getSelection().getHairData().getHairPhysicsType()==HairData.TYPE_AMMO_BONE_HAIR){
						type="ammo_bone_hair";
					}
					JSONObject selection=hairDataConverter.convert(cellObjects.getSelection().getHairData());
					
					String text=toJsonText(Lists.newArrayList(selection));
					Anchor a=HTML5Download.get().generateTextDownloadLink(text, "hair-"+type+".json", "selection to download",true);
					downloadArea.add(a);
				}
			});
			 downloadPanels.add(downloadSelectionBt);
			
			 downloadSelectionBt.setTitle("download selection only");
		 
			 downloadPanels.add(downloadArea);
		 
		 
		 //add
		 hairPanel.add(uploadPanel);
		 hairPanel.add(downloadPanels);
		 
		
		hairPanel.add(createGeometryImportExportPanel());
	}
	
	
	
	
	public String toJsonText(List<JSONObject> list){
		JSONObject root=new JSONObject();
		root.put("type", new JSONString(HairData.DATA_TYPE));
		root.put("version", new JSONNumber(1.0));
		
		JSONArray array=new JSONArray();
		for(int i=0;i<list.size();i++){
			array.set(i, list.get(i));
		}
		
		root.put("datas", array);
		
		return root.toString();
	}
	
	public Panel createGeometryImportExportPanel(){
		VerticalPanel main=new VerticalPanel();
		
		HorizontalPanel testPanel=new HorizontalPanel();
		testPanel.setVerticalAlignment(ALIGN_MIDDLE);
		main.add(testPanel);
		testPanel.add(new Label("Ammo-Bone-Geometry"));
		 Button test=new Button("export-json",new ClickHandler() {
				
				@Override
				public void onClick(ClickEvent event) {
					if(cellObjects.getSelection()==null){
						return;
					}
					SkinnedMesh character=GWTThreeClothHair.INSTANCE.getCharacterMesh();
					
					Geometry geometry=convertSelectionToGeometry(cellObjects.getSelection());
					
					MeshPhongMaterial material=THREE.MeshPhongMaterial(GWTParamUtils.MeshPhongMaterial().color(0x00ff00).skinning(true));
					
					
					
					JSONObject object=geometry.gwtJSONWithBone();
					downloadArea.clear();
					
					Anchor a=HTML5Download.get().generateTextDownloadLink(object.toString(), "geometry.json", "geometry to download",true);
					downloadArea.add(a);
					
					String text=object.toString();
					//this is XXXX 4.4 FORMAT
					JavaScriptObject js=JSONParser.parseStrict(text).isObject().get("data").isObject().getJavaScriptObject();
					LogUtils.log("json-parsed");
					
					Geometry loadedGeometry=THREE.JSONLoader().parse(js).getGeometry();
					
					LogUtils.log("loaded:");
					
					
					SkinnedMesh newMesh=THREE.SkinnedMesh(loadedGeometry, material);
					newMesh.setScale(character.getScale().getX(), character.getScale().getY(), character.getScale().getZ());
					newMesh.setSkeleton(character.getSkeleton());//can share the bone
					
					GWTThreeClothHair.INSTANCE.getScene().add(newMesh);
				}
			});
			 testPanel.add(test);
			 
			 Button test2=new Button("export-obj",new ClickHandler() {
					
				@Override
				public void onClick(ClickEvent event) {
					if(cellObjects.getSelection()==null){
						return;
					}
					
					Geometry geometry=convertSelectionToGeometry(cellObjects.getSelection());
					LogUtils.log(geometry);
					int m=geometry.mergeVertices();
					LogUtils.log("merged "+m);
					
					//validate face
					for(int i=0;i<geometry.getFaces().length();i++){
						Face3 face=geometry.getFaces().get(i);
						for(int j=0;j<3;j++){
							int index=face.gwtGet(j);
							if(index<0 || index>=geometry.getVertices().length()){
								LogUtils.log("invalid face at:"+i+","+j+" index="+index);
							}
						}
					}
					
					MeshPhongMaterial material=THREE.MeshPhongMaterial(GWTParamUtils.MeshPhongMaterial().color(0x00ff00));
					
					
					Mesh testMesh=THREE.Mesh(geometry);
					
					String text=THREEExp.OBJExporter().parse(testMesh);
					List<String> lines=CSVUtils.splitLinesWithGuava(text);
					int vt=0;
					int f=0;
					int vn=0;
					int v=0;
					for(String line:lines){
						if(line.startsWith("vt")){
							vt++;
						}else if(line.startsWith("vn")){
							vn++;
						}else if(line.startsWith("f")){
							f++;
						}else if(line.startsWith("v")){
							v++;
						}
					}
					Map<String,Integer> map=Maps.newHashMap();
					map.put("vt", vt);
					map.put("f", f);
					map.put("vn", vn);
					map.put("v", v);
					//vt vn v must be same ,i confirmed r74 objexporter faild.
					LogUtils.log(Joiner.on(",").withKeyValueSeparator("=").join(map));
					
					
					
					downloadArea.clear();
					
					Anchor a=HTML5Download.get().generateTextDownloadLink(text, "geometry.obj", "obj to download",true);
					downloadArea.add(a);
					
				}
			});
			 testPanel.add(test2);
			 
			 FileUploadForm testUpload=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
				
				@Override
				public void uploaded(File file, String text) {
					JavaScriptObject js=JSONParser.parseLenient(text).isObject().get("data").isObject().getJavaScriptObject();
					LogUtils.log("json-parsed");
					
					Geometry loadedGeometry=THREE.JSONLoader().parse(js).getGeometry();
					
					LogUtils.log("loaded:");
					MeshPhongMaterial material=THREE.MeshPhongMaterial(GWTParamUtils.MeshPhongMaterial()
							.color(GWTThreeClothHair.INSTANCE.getGlobalHairColor())
							.skinning(true)
							.side(THREE.DoubleSide)
							);
					SkinnedMesh character=GWTThreeClothHair.INSTANCE.getCharacterMesh();
					
					SkinnedMesh newMesh=THREE.SkinnedMesh(loadedGeometry, material);
					newMesh.setScale(character.getScale().getX(), character.getScale().getY(), character.getScale().getZ());
					newMesh.setSkeleton(character.getSkeleton());//can share the bone
					
					GWTThreeClothHair.INSTANCE.getScene().add(newMesh);
				}
			}, true);
			 main.add(testUpload);
			 
			 return main;
	}
	
	
	protected Geometry convertSelectionToGeometry(HairMixedData selection) {
		ParticleBodyDatas data=GWTThreeClothHair.INSTANCE.getClothSimulator().getAmmoHairControler().getAmmoData(selection.getClothData().getHairCloth());
		if(data.getSkinnedMesh()==null){
			LogUtils.log("convertSelectionToGeometry:now only suuport skinnedMesh");
		}
		
		SkinnedMesh character=GWTThreeClothHair.INSTANCE.getCharacterMesh();
		
		
		//AnimationBone ab=character.getGeometry().getBones().get(0).clone();
		
		
		
		Geometry geometry=makeSkeltonAnimationAppliedGeometry(data.getSkinnedMesh());
		
		
		Matrix4 matrix4=THREE.Matrix4();
		matrix4.makeScale(1.0/character.getScale().getX(), 1.0/character.getScale().getY(), 1.0/character.getScale().getZ());
		geometry.applyMatrix(matrix4);
		
		//geometry.setBones(AnimationBone.gwtClone(character.getGeometry().getBones()));
		
		geometry.gwtSetInfluencesPerVertex(4);
		geometry.setBones(character.getGeometry().getBones());
		
		//TODO average
		WeightResult result=new CloseVertexAutoWeight().autoWeight(geometry, characterMesh.getGeometry());
		
		//TODO need more improve,totally not good
		//WeightResult result=new SimpleAutoWeight(4).autoWeight(geometry, geometry.getBones(),Lists.newArrayList(0));
		
		result.insertToGeometry(geometry);
		
		return geometry;
		/*
		 * error
		 * HREE.WebGLProgram: shader error:  0 gl.VALIDATE_STATUS false gl.getProgramInfoLog invalid shaders
		 */
		
	}
	
	public Geometry makeSkeltonAnimationAppliedGeometry(SkinnedMesh mesh){
		Geometry geometry=mesh.getGeometry().clone();
		
		LogUtils.log("vertex-size:"+geometry.getVertices().length());
		for(int i=0;i<geometry.getVertices().length();i++){
		Vector3 transformed=SkinningVertexCalculator.transformSkinningVertex(mesh,i,geometry.getVertices().get(i));
		geometry.getVertices().get(i).copy(transformed);
		}
		
		return geometry;
	}
	
	
	private void updateHairVisible(HairMixedData data,boolean visible){
		
		
		ParticleBodyDatas ammoData=GWTThreeClothHair.INSTANCE.getClothSimulator().getAmmoHairControler().getAmmoData(data.getClothData().getHairCloth());
		if(ammoData!=null){
			if(ammoData.getSkinnedMesh()!=null){
				ammoData.getSkinnedMesh().setVisible(visible);
				data.getMesh().setVisible(false);
				return;
			}
			
		}
		
		data.getMesh().setVisible(visible);
	}
	
	/*
	 * must wait hair texture update
	 */
	private void loadHairDataSync(String json,boolean isCsv){
		Iterable<HairData> hairDatas=null;
		if(isCsv){
			hairDatas=new HairDataCsvConverter().reverse().convertAll(CSVUtils.splitLinesWithGuava(json));
		}else{
			JSONValue value=JSONParser.parseStrict(json);
			if(value==null){
				LogUtils.log("HairDataConverter:parse json faild "+json);
				return ;
			}
			JSONObject object=value.isObject();
			if(object==null){
				LogUtils.log("HairDataConverter:not json object:"+json);
				return ;
			}
			
			if(object.get("type")==null){
				LogUtils.log("HairDataConverter:has no type attribute:"+object.toString());
				return ;
			}
			
			JSONString typeString=object.get("type").isString();
			if(typeString==null){
				LogUtils.log("HairDataConverter:has a type attribute:"+object.toString());
				return ;
			}
			
			String type=typeString.stringValue();
			if(!type.equals(HairData.DATA_TYPE)){
				LogUtils.log("HairDataConverter:difference type:"+type);
				return;
			}
			
			JSONValue datasValue=object.get("datas");
			if(datasValue==null){
				LogUtils.log("HairDataConverter:no datas:");
				return;
			}
			JSONArray array=datasValue.isArray();
			if(array==null){
				LogUtils.log("HairDataConverter:no jsonarray:");
				return;
			}
			List<JSONObject> jsonObjects=Lists.newArrayList();
			for(int i=0;i<array.size();i++){
				JSONValue arrayValue=array.get(i);
				JSONObject arrayObject=arrayValue.isObject();
				if(arrayObject==null){
					LogUtils.log("HairDataConverter:contain invalid data:"+i+","+arrayValue);
					return;
				}
				jsonObjects.add(arrayObject);
			}
			
			hairDatas=hairDataConverter.reverse().convertAll(jsonObjects);
		}
		loadHairDataSync(hairDatas);
	}
	private void loadHairDataSync(Iterable<HairData> hairDatas){
		 final List<HairData> loadingDatas=Lists.newArrayList(hairDatas);
		 //this initial load make problem,without sync because of sharing canvas(without share,crash lack of memory)
		 new Timer(){
			@Override
			public void run() {
				if(!GWTThreeClothHair.INSTANCE.getClothSimulator().isUpdatingHairTextureMap()){
					if(loadingDatas.isEmpty()){
						cancel();
						
						//TODO ammo hair material
						
						storeDatas();
						return;
					}
					
					HairData hairData=loadingDatas.remove(0);
					addCloth(hairData,false);//reading no need store
				}
			}
			 
		 }.scheduleRepeating(10);
	}
	
	
	protected void updateSelectionHairVisible(Boolean value) {
		if(!value){
			for(HairMixedData data:cellObjects.getDatas()){
				updateHairVisible(data,showHair.getValue());
				
			}
		}else{
			HairMixedData selection=cellObjects.getSelection();
			
			
			for(HairMixedData data:cellObjects.getDatas()){
				if(data==selection){
					updateHairVisible(data,true);
					
				}else{
					updateHairVisible(data,false);
				}
			}
		}
	}
	public void updateHairPinPanel(){
		hairPinPanel.setHairPins(hairPins);
	}
	
	public static class HairMixedData{
		private HairData hairData;
		public HairMixedData(HairData hairData, ClothData clothData, Mesh mesh) {
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
		
		pinsLabel.setText("Pins:"+pins.size()+" p="+HairCloth.calcurateParticleSize(pins,hairDataEditor.getSizeOfU(),hairDataEditor.getSizeOfV())
				+",w="+HairCloth.calcurateWSize(pins,hairDataEditor.getSizeOfU())
				);
		
		double distance=HairDataUtils.getTotalPinDistance(pins, characterMesh, false);
		horizontalDistanceLabel.setText(
				("H:"+distance).substring(0,7)
						);
		//no need * scaleof u
		//update and make method
		int w = (pins.size()-1)*hairDataEditor.getSizeOfU();
		int h= hairDataEditor.getSizeOfV();
		
		
		
		double vdistanceh=HairDataUtils.getTotalVDistance(distance*hairDataEditor.getScaleOfU(), w, h);
		double ratio=vdistanceh/distance;
		verticalDistanceLabel.setText(
				("V:"+vdistanceh).substring(0,7)+" ratio="+String.valueOf(ratio).substring(0,4)
						);
		
	}

	//private HairDataCsvConverter csvHairDataConverter=new HairDataCsvConverter();
	private HairDataConverter hairDataConverter=new HairDataConverter();
	protected void removeHairData(HairMixedData data) {
		checkNotNull(data,"removeHairData:data is null");
		GWTThreeClothHair.INSTANCE.getScene().remove(data.getMesh());
		GWTThreeClothHair.INSTANCE.getClothControler().removeClothData(data.getClothData());
		cellObjects.removeItem(data);
		//remove physics data
		
		GWTThreeClothHair.INSTANCE.getAmmoHairControler()
		.removeParticleData(data.getClothData().getHairCloth());
		
		
		//no need to sphere data
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
	if(firstSelection==null || secondSelection==null){
		LogUtils.log("invalidly call updateHairPinsByThreePoints but first or second is null");
	}
	hairPins.clear();
	hairPins.add(firstSelection);
	hairPins.add(secondSelection);
	if(thirdSelection!=null){
		hairPins.add(thirdSelection);
	}
}

	
public void updateHairDataLine(){
	
	updateDistanceLabel();
	
	if(hairPins.size()<2){
		//cloth need two point.
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
		this.hairDataEditor.setHairTextureDataEditor(hairTextureDataEditor);
	}
	
	protected void addCloth(HairData hairData) {
		addCloth(hairData,true);
	}
	protected void addCloth(HairData hairData,boolean storeData) {
		
		final HairMixedData cellData=GWTThreeClothHair.INSTANCE.getClothSimulator().addCloth(hairData);
		
		cellObjects.addItem(cellData);
		cellObjects.setSelected(cellData, true);
		
		if(storeData){
			//TODO update ammo hair material,use schedule and just call update?
			storeDatas();
		}
		
		Scheduler.get().scheduleFinally(new ScheduledCommand() {
			@Override
			public void execute() {
				GWTThreeClothHair.INSTANCE.getClothSimulator().update(0);//this make ammo-mesh
				//re update hair data for ammo-hair
				GWTThreeClothHair.INSTANCE.getClothSimulator().updateHairTextureData(cellData, true);
			}
		});
	}
	
	private Vector3 _diff=THREE.Vector3();
	public void executeSphereOut(Vector3 pos,List<Mesh> spheres){
		
		for(Mesh mesh:spheres){
				
			_diff.subVectors(pos, mesh.getPosition());
				if (_diff.length() < mesh.getScale().getX()) {
					
					_diff.normalize().multiplyScalar(mesh.getScale().getX()*3);
					pos.copy(mesh.getPosition()).add(_diff);
					
				
				
			}	
		}
	}
	
	public void storeDatas() {
		if(cellObjects.getDatas().size()==0){
			storageControler.removeValue(GWTThreeClothHairStorageKeys.temp_hairset);
			return;
		}
		
		String text=
				toStoreText();
		
		try {
			storageControler.setValue(GWTThreeClothHairStorageKeys.temp_hairset, text);
		} catch (StorageException e) {
			Window.alert(e.getMessage());
		}
		
	}
	
	private String toStoreText(){
		List<JSONObject> objects=
				Lists.newArrayList(hairDataConverter.convertAll(FluentIterable.from(cellObjects.getDatas()).transform(new Function<HairMixedData, HairData>() {
					@Override
					public HairData apply(HairMixedData input) {
						return input.getHairData();
					}
				})));
		
		return toJsonText(objects);
	}

	private StorageControler storageControler=new StorageControler();

	private Label horizontalDistanceLabel;
	private Label pinsLabel;
	private HairDataEditor hairDataEditor;

	public HairDataEditor getHairDataEditor() {
		return hairDataEditor;
	}
	public void setHairDataEditor(HairDataEditor hairDataEditor) {
		this.hairDataEditor = hairDataEditor;
	}
	private CheckBox showSelectionHair;

	private CheckBox showHair;

	private HorizontalPanel downloadArea;
	
	private void clearAllHairData(){
		for(HairMixedData data:ImmutableList.copyOf(cellObjects.getDatas())){
			removeHairData(data);
		}
	}



}
