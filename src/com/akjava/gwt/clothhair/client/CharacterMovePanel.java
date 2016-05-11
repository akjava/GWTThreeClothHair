package com.akjava.gwt.clothhair.client;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.objects.Mesh;
import com.akjava.gwt.three.client.js.objects.SkinnedMesh;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
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
		
		
		this.add(new Label("Animation"));
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
				GWTThreeClothHair.INSTANCE.stopAnimation();	
				
				
			}
		});
		animationButtons.add(stopBt);
		
		Button resetBt=new Button("resetBt",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				GWTThreeClothHair.INSTANCE.stopAnimation();
				GWTThreeClothHair.INSTANCE.startAnimation(0,0,0);
				
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
		

		
	}
	double animationX;
	double animationY;
	double animationZ;

	protected void startAnimation() {
		GWTThreeClothHair.INSTANCE.startAnimation(Math.toRadians(animationX), Math.toRadians(animationY), Math.toRadians(animationZ));
	}
}
