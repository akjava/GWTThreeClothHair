package com.akjava.gwt.clothhair.client;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.clothhair.client.SkeletonUtils.BoneData;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.akjava.gwt.three.client.js.objects.Skeleton;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class CharacterMovePanel extends VerticalPanel{

	private SkinnedMesh mesh;

	public CharacterMovePanel(final SkinnedMesh mesh) {
		super();
		this.mesh = mesh;
		
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
		
		
		
		
		HorizontalPanel animationPanel=new HorizontalPanel();
		animationPanel.setVerticalAlignment(ALIGN_MIDDLE);
		this.add(animationPanel);
		animationPanel.add(new Label("Animation-"));
		
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
		
		
		
		LabeledInputRangeWidget2 xAnimation=new LabeledInputRangeWidget2("x", -180, 180, 1);
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
		LabeledInputRangeWidget2 yAnimation=new LabeledInputRangeWidget2("y", -180, 180, 1);
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
		LabeledInputRangeWidget2 zAnimation=new LabeledInputRangeWidget2("z", -180, 180, 1);
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
		Button startBt=new Button("start",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				startAnimation();
			}
		});
		animationButtons.add(startBt);
		
		Button stopBt=new Button("stop",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				stopAnimation();
				
				
			}
		});
		animationButtons.add(stopBt);
		
		Button resetBt=new Button("resetBt",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				resetAnimation();
				
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
		

		this.add(new FacialAnimationPanel(this));
		
	}
	private int lastBoneIndex;
	private int boneIndex;
	private void resetAnimation(){
		GWTThreeClothHair.INSTANCE.stopAnimation();
		GWTThreeClothHair.INSTANCE.startAnimation(boneIndex,0,0,0);
	}
	double animationX;
	double animationY;
	double animationZ;
	private ValueListBox<BoneData> boneIndexBox;
	
	public void stopAnimation(){
		GWTThreeClothHair.INSTANCE.stopAnimation();	
	}

	public void startAnimation() {
		GWTThreeClothHair.INSTANCE.startAnimation(boneIndex,Math.toRadians(animationX), Math.toRadians(animationY), Math.toRadians(animationZ));
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
