package com.akjava.gwt.clothhair.client;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.clothhair.client.SkeletonUtils.BoneData;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.animation.AnimationClip;
import com.akjava.gwt.three.client.js.animation.KeyframeTrack;
import com.akjava.gwt.three.client.js.loaders.XHRLoader.XHRLoadHandler;
import com.akjava.gwt.three.client.js.objects.Skeleton;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;
import com.akjava.lib.common.utils.FileNames;
import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArray;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CharacterMovePanel extends VerticalPanel{

	private SkinnedMesh mesh;
	private AnimationClip animationClip;
	public CharacterMovePanel(final SkinnedMesh mesh) {
		super();
		this.mesh = mesh;
		
		this.add(new HTML("<h4>Animation Mixer fps control</h4>"));
		HorizontalPanel h1=new HorizontalPanel();
		h1.setSpacing(4);
		h1.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		this.add(h1);
		final IntegerBox frameBox=new IntegerBox();
		
		CheckBox fixedFpsCheck=new CheckBox();
		h1.add(fixedFpsCheck);
		h1.add(new Label("fixed fps"));
		fixedFpsCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				frameBox.setEnabled(event.getValue());
			}
			
		});
		
		
		fixedFpsCheck.setValue(GWTThreeClothHair.INSTANCE.isUseFixedFrame());
		fixedFpsCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				GWTThreeClothHair.INSTANCE.setUseFixedFrame(event.getValue());
			}
		});
		
		
		frameBox.setWidth("40px");
		frameBox.setValue(GWTThreeClothHair.INSTANCE.getMixerFrixedFrameNumber());
		frameBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {

			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				GWTThreeClothHair.INSTANCE.setMixerFrixedFrameNumber(event.getValue());
			}
		});
		
		
		h1.add(new Label("frame"));
		
		h1.add(frameBox);
		
		
		
		
		
		
		
		this.add(new Label("Position"));
		LabeledInputRangeWidget2 xPos=new LabeledInputRangeWidget2("x", -900, 900, 1);
		xPos.getLabel().setWidth("20px");
		xPos.getRange().setWidth("240px");
		this.add(xPos);
		xPos.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				mesh.getPosition().setX(event.getValue().doubleValue());
			}
		});
		LabeledInputRangeWidget2 yPos=new LabeledInputRangeWidget2("y", mesh.getPosition().getY()-200, mesh.getPosition().getY()+1600, 1);
		yPos.getLabel().setWidth("20px");
		yPos.getRange().setWidth("240px");
		this.add(yPos);
		yPos.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				mesh.getPosition().setY(event.getValue().doubleValue());
			}
		});
		LabeledInputRangeWidget2 zPos=new LabeledInputRangeWidget2("z", -200, 200, 1);
		zPos.getLabel().setWidth("20px");
		zPos.getRange().setWidth("240px");
		this.add(zPos);
		zPos.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				mesh.getPosition().setZ(event.getValue().doubleValue());
			}
		});
		
		this.add(new Label("Rotation"));
		LabeledInputRangeWidget2 xRot=new LabeledInputRangeWidget2("x", -180, 180, 1);
		xRot.getLabel().setWidth("20px");
		xRot.getRange().setWidth("240px");
		this.add(xRot);
		xRot.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				mesh.getRotation().setX(Math.toRadians(event.getValue().doubleValue()));
			}
		});
		LabeledInputRangeWidget2 yRot=new LabeledInputRangeWidget2("y", -180, 180, 1);
		yRot.getLabel().setWidth("20px");
		yRot.getRange().setWidth("240px");
		this.add(yRot);
		yRot.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				mesh.getRotation().setY(Math.toRadians(event.getValue().doubleValue()));
			}
		});
		LabeledInputRangeWidget2 zRot=new LabeledInputRangeWidget2("z", -180, 180, 1);
		zRot.getLabel().setWidth("20px");
		zRot.getRange().setWidth("240px");
		this.add(zRot);
		zRot.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				mesh.getRotation().setZ(Math.toRadians(event.getValue().doubleValue()));
			}
		});
		
		this.add(new Label("Scale"));
		LabeledInputRangeWidget2 scaleXYZ=new LabeledInputRangeWidget2("xyz", 800,1200, 1);
		scaleXYZ.getLabel().setWidth("20px");
		scaleXYZ.getRange().setWidth("240px");
		this.add(scaleXYZ);
		scaleXYZ.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				mesh.getScale().setScalar(event.getValue().doubleValue());
			}
		});
		
		scaleXYZ.setValue(mesh.getScale().getX());
		
		//setvalue
		xPos.setValue(mesh.getPosition().getX());
		yPos.setValue(mesh.getPosition().getY());
		zPos.setValue(mesh.getPosition().getZ());
		
		
		
		
		HorizontalPanel loadAnimationPanel=new HorizontalPanel();
		this.add(loadAnimationPanel);
		final Label nameLabel=new Label();
		FileUploadForm upload=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			

			@Override
			public void uploaded(File file, String text) {
				nameLabel.setText(FileNames.getRemovedExtensionName(file.getFileName()));
				JSONValue object=JSONParser.parseStrict(text);
				JavaScriptObject js=object.isObject().getJavaScriptObject();
				animationClip = AnimationClip.parse(js);
				
				//maybe pose editor problem
				JsArray<KeyframeTrack> tracks=JsArray.createArray().cast();
				
				for(int i=0;i<animationClip.getTracks().length();i++){
					KeyframeTrack track=animationClip.getTracks().get(i);
					
					/*
					if(track.getName().endsWith(".position")){
						
						if(track.getName().equals(".bones[0].position")){
							//root position
							//tracks.push(track);
							LogUtils.log("root-move-track");
							LogUtils.log(track);
							
							JsArrayNumber times=JsArray.createArray().cast();
							JsArrayNumber values=JsArray.createArray().cast();
							
							for(int j=0;j<track.getTimes().length();j++){
								times.push(track.getTimes().get(j));
							}
							for(int j=0;j<track.getValues().length();j++){
								double v=track.getValues().get(j);
								v/=10;
								values.push(v);
							}
							
							VectorKeyframeTrack newtrack=THREE.VectorKeyframeTrack(track.getName(), times, values);
							tracks.push(newtrack);
						}
						
					
						
						//remoe position.TODO fix scalling
					}else{
						tracks.push(track);
					}
					*/
					tracks.push(track);
				}
				
				AnimationClip clip=THREE.AnimationClip("play", -1, tracks);
				
				
				GWTThreeClothHair.INSTANCE.playAnimation(clip,true);
				
			}
		}, true);
		upload.setAccept(FileUploadForm.ACCEPT_JSON);
		loadAnimationPanel.add(upload);
		
		HorizontalPanel a2=new HorizontalPanel();
		this.add(a2);
		Button play=new Button("play",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				if(animationClip==null){
					return;
				}
				GWTThreeClothHair.INSTANCE.playAnimation(animationClip,true);
			}
		});
		a2.add(play);
		
		Button test1=new Button("test1",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				testAnimation("animation/anime_test1.json");
			}
		});
		a2.add(test1);
		Button test2=new Button("test2",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				testAnimation("animation/anime_test2.json");
			}
		});
		a2.add(test2);
		Button test3=new Button("test3",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				testAnimation("animation/anime_test3.json");
			}
		});
		a2.add(test3);
		Button test4=new Button("test4",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				testAnimation("animation/anime_test4.json");
			}
		});
		a2.add(test4);
		Button test5=new Button("test5",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				testAnimation("animation/anime_test5.json");
			}
		});
		a2.add(test5);
		
		
		HorizontalPanel animationPanel=new HorizontalPanel();
		animationPanel.setVerticalAlignment(ALIGN_MIDDLE);
		this.add(animationPanel);
		animationPanel.add(new Label("Quick Animation-"));
		
		boneIndexBox = new ValueListBox<BoneData>(new Renderer<BoneData>() {

			@Override
			public String render(BoneData object) {
				if(object==null){
					return null;
				}
				// TODO Auto-generated method stub
				return object.getName();
			}

			@Override
			public void render(BoneData object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		boneIndexBox.addValueChangeHandler(new ValueChangeHandler<BoneData>() {
			@Override
			public void onValueChange(ValueChangeEvent<BoneData> event) {
				lastBoneIndex=boneIndex;
				boneIndex=event.getValue().getIndex();
				
				GWTThreeClothHair.INSTANCE.stopAnimation();
				GWTThreeClothHair.INSTANCE.startAnimation(lastBoneIndex,0,0,0);
			}
		});
		animationPanel.add(new Label("TargetBone:"));
		animationPanel.add(boneIndexBox);
		
		
		
		xAnimation = new LabeledInputRangeWidget2("x", -180, 180, 1);
		xAnimation.getLabel().setWidth("20px");
		xAnimation.getRange().setWidth("240px");
		this.add(xAnimation);
		xAnimation.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				animationX=event.getValue().doubleValue();
				startAnimation();
			}
		});
		yAnimation = new LabeledInputRangeWidget2("y", -180, 180, 1);
		yAnimation.getLabel().setWidth("20px");
		yAnimation.getRange().setWidth("240px");
		this.add(yAnimation);
		yAnimation.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				animationY=event.getValue().doubleValue();
				startAnimation();
			}
		});
		zAnimation = new LabeledInputRangeWidget2("z", -180, 180, 1);
		zAnimation.getLabel().setWidth("20px");
		zAnimation.getRange().setWidth("240px");
		this.add(zAnimation);
		zAnimation.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				animationZ=event.getValue().doubleValue();
				startAnimation();
			}
		});
		
		HorizontalPanel animationButtons=new HorizontalPanel();
		this.add(animationButtons);
		
		//TODO support target bone;
		Button startBt=new Button("Start",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				startAnimation();
			}
		});
		animationButtons.add(startBt);
		
		Button stopBt=new Button("Stop",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				stopAnimation();
				
				
			}
		});
		animationButtons.add(stopBt);
		
		Button resetBt=new Button("Reset",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				GWTThreeClothHair.INSTANCE.stopAnimation();
				GWTThreeClothHair.INSTANCE.resetAnimation();
				//resetAnimationWidget();
				/*
				 * i faild how to update by hand.
				 
				LogUtils.log(mesh);
				mesh.getSkeleton().getBones().get(60).setQuaternion(THREE.Quaternion());
				//LogUtils.log(mesh.getGeometry().getBones().get(60));
				mesh.setMatrixWorldNeedsUpdate(true);
				//mesh.getGeometry().setVerticesNeedUpdate(true);
				
				 */
				
			}
		});
		animationButtons.add(resetBt);
		
		bothCheck = new CheckBox("both-direction");
		bothCheck.setValue(true);
		animationButtons.add(bothCheck);

		this.add(new FacialAnimationPanel(this));
		
	}
	/**
	 * called from GWTThreeClothHair
	 */
	public void resetAnimationWidget(){
		
		//GWTThreeClothHair.INSTANCE.resetAnimation();
		xAnimation.setValue(0);
		yAnimation.setValue(0);
		zAnimation.setValue(0);
		animationX=0;
		animationY=0;
		animationZ=0;
	}
	
	private int lastBoneIndex;
	private int boneIndex;
	
	double animationX;
	double animationY;
	double animationZ;
	private ValueListBox<BoneData> boneIndexBox;
	private CheckBox bothCheck;
	private LabeledInputRangeWidget2 xAnimation;
	private LabeledInputRangeWidget2 yAnimation;
	private LabeledInputRangeWidget2 zAnimation;
	
	private void testAnimation(String name){
		THREE.XHRLoader().load(name, new XHRLoadHandler() {
			@Override
			public void onLoad(String text) {
				JSONValue object=JSONParser.parseStrict(text);
				JavaScriptObject js=object.isObject().getJavaScriptObject();
				animationClip = AnimationClip.parse(js);
				GWTThreeClothHair.INSTANCE.playAnimation(animationClip,true);
			}
		});
		
	}
	
	public void stopAnimation(){
		GWTThreeClothHair.INSTANCE.stopAnimation();	
	}

	public void startAnimation() {
		GWTThreeClothHair.INSTANCE.startAnimation(boneIndex,Math.toRadians(animationX), Math.toRadians(animationY), Math.toRadians(animationZ),bothCheck.getValue(),true);
	}
	
	public void setSkelton(Skeleton skeleton) {
		List<BoneData> boneDatas=SkeletonUtils.skeltonToBoneData(skeleton);
		int defaultTarget=60;
		if(boneDatas.size()<defaultTarget){
			LogUtils.log("differect bone.change default index");
			defaultTarget=0;
		}
		boneIndexBox.setValue(boneDatas.get(defaultTarget),true);//defaut head,watch out only work specific bone
		boneIndexBox.setAcceptableValues(boneDatas);
	}
}
