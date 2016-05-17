package com.akjava.gwt.clothhair.client.hair;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.List;

import com.akjava.gwt.clothhair.client.GWTThreeClothHair;
import com.akjava.gwt.clothhair.client.HairStorageKeys;
import com.akjava.gwt.clothhair.client.cloth.ClothData;
import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.akjava.gwt.clothhair.client.hair.HairDataFunctions.HairPinToVertex;
import com.akjava.gwt.clothhair.client.texture.HairTextureData;
import com.akjava.gwt.clothhair.client.texture.HairTextureDataEditor;
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
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.java.ThreeLog;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.BufferAttribute;
import com.akjava.gwt.three.client.js.core.BufferGeometry;
import com.akjava.gwt.three.client.js.core.Face3;
import com.akjava.gwt.three.client.js.materials.MeshPhongMaterial;
import com.akjava.gwt.three.client.js.math.Matrix3;
import com.akjava.gwt.three.client.js.math.Vector3;
import com.akjava.gwt.three.client.js.objects.LineSegments;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;
import com.akjava.lib.common.utils.CSVUtils;
import com.google.common.base.Function;
import com.google.common.base.Joiner;
import com.google.common.base.Strings;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.gwt.core.client.GWT;
import com.google.gwt.editor.client.SimpleBeanEditorDriver;
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
import com.google.gwt.user.client.ui.Panel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HairDataPanel extends VerticalPanel{
	 interface Driver extends SimpleBeanEditorDriver< HairData,  HairDataEditor> {}
	 Driver driver = GWT.create(Driver.class);
	 
	private EasyCellTableObjects<HairCellObjectData> cellObjects;

	private SkinnedMesh characterMesh;

	public HairCellObjectData getSelection(){
		return cellObjects.getSelection();
	}
	private Label verticalDistanceLabel;
	public HairDataPanel(final SkinnedMesh characterMesh,HairTextureDataEditor hairTextureDataEditor){
		this.characterMesh=characterMesh;
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
		driver.initialize(editor);
		
		
		hairPanel.add(editor);
		createClothPanel(hairPanel);
		
		HorizontalPanel distancePanel=new HorizontalPanel();
		hairPanel.add(distancePanel);
		horizontalDistanceLabel = new Label("H:");
		horizontalDistanceLabel.setWidth("150px");
		distancePanel.add(horizontalDistanceLabel);
		
		verticalDistanceLabel = new Label("V:");
		verticalDistanceLabel.setWidth("150px");
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
					firstSelection=data.getHairData().getHairPins().get(0);
					secondSelection=data.getHairData().getHairPins().get(1);
					if(data.getHairData().getHairPins().size()>2){
						thirdSelection=data.getHairData().getHairPins().get(2);
					}
					//LogUtils.log(hairDataConverter.convert(data.getHairData()));
					updateHairDataLine();
					
				}
			}
		});
		editPanel.add(edit);
		
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
			 for(HairData hairData:hairDatas){
				 addCloth(hairData,false);//reading no need store
			 }
		 }
		
		 
		 FileUploadForm upload=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				clearAllHairData();
				//todo check validate
				
				 Iterable<HairData> hairDatas=hairDataConverter.reverse().convertAll(CSVUtils.splitLinesWithGuava(text));
				 for(HairData hairData:hairDatas){
					 addCloth(hairData);
				 }
				 
				
			}
		}, true, "UTF-8");
		 upload.setAccept(".csv");
		 hairPanel.add(upload);
		 
		 HorizontalPanel downloadPanels=new HorizontalPanel();
		 hairPanel.add(downloadPanels);
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
		
		//tmp
		HorizontalPanel h=new HorizontalPanel();
		parent.add(h);
		
		Button first=new Button("first",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				firstSelection=currentSelection;
				
				updateHairDataLine();
			}
		});
		h.add(first);
		
		
		Button second=new Button("second",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				secondSelection=currentSelection;
				updateHairDataLine();
			}
		});
		h.add(second);
		
		Button third=new Button("third",new ClickHandler() {
			

			@Override
			public void onClick(ClickEvent event) {
				thirdSelection=currentSelection;
				updateHairDataLine();
			}
		});
		h.add(third);
		
		Button clear=new Button("clear third",new ClickHandler() {
			

			@Override
			public void onClick(ClickEvent event) {
				thirdSelection=null;
				updateHairDataLine();
			}
		});
		h.add(clear);
		
		Button addCloth=new Button("add cloth",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				addCloth();
			}
		});
		h.add(addCloth);
		
	}

	void updateDistanceLabel(){
		List<HairPin> pins=Lists.newArrayList();
		if(firstSelection!=null){
			pins.add(firstSelection);
		}
		
		if(secondSelection!=null){
			pins.add(secondSelection);
		}
		
		if(thirdSelection!=null){
			pins.add(thirdSelection);
		}
		
		
		double distance=HairDataUtils.getTotalPinDistance(pins, characterMesh, false);
		horizontalDistanceLabel.setText(
				("H:"+distance).substring(0,7)
						);
		//no need * scaleof u
		
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
		
		GWTThreeClothHair.INSTANCE.getScene().add(selectedLine);
	}

public void updateHairDataLine(){
	
	updateDistanceLabel();
	
	if(firstSelection==null && secondSelection==null){
		
		return;
	}
	
	//TODO support multiple
	List<HairPin> pins=Lists.newArrayList();
	if(firstSelection!=null){
		pins.add(firstSelection);
	}
	
	if(secondSelection!=null){
		pins.add(secondSelection);
	}
	
	if(thirdSelection!=null){
		pins.add(thirdSelection);
	}
	HairPinToVertex hairPinToVertex=new HairPinToVertex(characterMesh,true);
	Matrix3 normalMatrix=THREE.Matrix3();
	normalMatrix.getNormalMatrix( characterMesh.getMatrixWorld());
	
	
	BufferGeometry geometry = THREE.BufferGeometry();

	BufferAttribute positions = THREE.Float32Attribute( 2*3 * pins.size(), 3 );
	geometry.addAttribute( "position", positions );
	
	
	
	
	
	double size=3.2;
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
	
	if(hairDataLine!=null){
		GWTThreeClothHair.INSTANCE.getScene().remove(hairDataLine);
	}
	
	hairDataLine=THREE.LineSegments(geometry.gwtCastGeometry(), THREE.LineBasicMaterial(GWTParamUtils.LineBasicMaterial().color(0x0000ff).linewidth(2)));
	GWTThreeClothHair.INSTANCE.getScene().add(hairDataLine);
	
	
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
	
	if(applyMatrix4){
	return vertex.clone().applyMatrix4( mesh.getMatrixWorld());
	}else{
	return vertex.clone();
	}
	
}

	protected void addCloth() {
		
		if(firstSelection==null || secondSelection==null){
			LogUtils.log("need first & second");
			return;
		}
		
		Vector3 v1=hairPinToVertex(characterMesh,firstSelection,true);
		Vector3 v2=hairPinToVertex(characterMesh,secondSelection,true);
		
		double distance=v1.distanceTo(v2);
		
		if(distance==0){
			LogUtils.log("invalidly first & second same:"+ThreeLog.get(v1)+","+ThreeLog.get(v1));
			return;
		}
		
		HairData hairData=driver.flush();
		
		driver.edit(new HairData());//add another one.
		
		
		hairData.getHairPins().clear();
		hairData.getHairPins().add(firstSelection);
		hairData.getHairPins().add(secondSelection);
		if(thirdSelection!=null){
			hairData.getHairPins().add(thirdSelection);
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
		
		//little bit 
		MeshPhongMaterial hairMaterial = THREE.MeshPhongMaterial(GWTParamUtils.
				MeshPhongMaterial().side(THREE.DoubleSide)
				.transparent(true)
				
				.specular(0xffffff)//TODO move editor
				.shininess(15)
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
			for(int i=0;i<hairData.getHairPins().size();i++){
				Vector3 v1=hairPinToVertex(characterMesh,hairData.getHairPins().get(i),true);
				int index=hairData.getSizeOfU()*i;
				data.getCloth().particles.get(index).setAllPosition(v1);
				
				//LogUtils.log("main:"+index);
				
				
				if(i!=hairData.getHairPins().size()-1){
					//has next;
					Vector3 v2=hairPinToVertex(characterMesh,hairData.getHairPins().get(i+1),true);
					Vector3 sub=v2.clone().sub(v1).divideScalar(cw);
					
					for(int j=1;j<cw;j++){
						int multiple=j;
						int at=index+j;
						Vector3 v=sub.clone().multiplyScalar(multiple).add(v1);
						data.getCloth().particles.get(at).setAllPosition(v);
						
						//LogUtils.log("sub:"+at);
					}
					
				}
				
				if(i==0){//not pin start with v1
					for(int j=cw*(hairData.getHairPins().size()-1)+1;j<data.getCloth().particles.size();j++){
						data.getCloth().particles.get(j).setAllPosition(v1);
					}
				}
			}
			
			/*
			for(int i=0;i<cw*(hairData.getHairPins().size()-1);i++){
				ThreeLog.log(""+i, data.getCloth().particles.get(i).getOriginal());
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

	private HairDataEditor editor;

	private void clearAllHairData(){
		for(HairCellObjectData data:ImmutableList.copyOf(cellObjects.getDatas())){
			removeHairData(data);
		}
	}



}
