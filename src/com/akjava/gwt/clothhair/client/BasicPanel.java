package com.akjava.gwt.clothhair.client;

import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.experimental.ImageDataUtils;
import com.akjava.gwt.lib.client.experimental.ImageDataUtils.RGBColorFilter;
import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.loaders.ImageLoader.ImageLoadHandler;
import com.akjava.gwt.three.client.js.textures.Texture;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BasicPanel extends VerticalPanel{

	public BasicPanel(){

		HorizontalPanel h1=new HorizontalPanel();
		this.add(h1);
		
		h1.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		this.add(new Label("Visible"));
		CheckBox windCheck=new CheckBox("Wind");
		windCheck.setValue(true);
		windCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				GWTThreeClothHair.INSTANCE.getClothSimulator().getClothControler().setWind(event.getValue());
			}
		});
		h1.add(windCheck);
		
		
		CheckBox groundCheck=new CheckBox("Ground");
		
		groundCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				
				GWTThreeClothHair.INSTANCE.getGroundMesh().setVisible(event.getValue());
				GWTThreeClothHair.INSTANCE.getClothSimulator().getClothControler().getFloorModifier().setEnabled(event.getValue());
			}
		});
		groundCheck.setValue(true);
		h1.add(groundCheck);
		groundCheck.setValue(false,true);
		
		this.add(new Label("Camera"));
		LabeledInputRangeWidget2 near=new LabeledInputRangeWidget2("near", 0.1, 100, 0.1);
		
		
		
		
		this.add(near);
		near.addtRangeListener(new ValueChangeHandler<Number>() {
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				GWTThreeClothHair.INSTANCE.getCamera().setNear(event.getValue().doubleValue());
				GWTThreeClothHair.INSTANCE.getCamera().updateProjectionMatrix();
			}
		});
		near.setValue(GWTThreeClothHair.INSTANCE.getCamera().getNear());
		
		//vertex
		CheckBox visibleVertexCheck=new CheckBox("Vertex");
		visibleVertexCheck.setValue(true);
		visibleVertexCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				GWTThreeClothHair.INSTANCE.updateVertexVisible(event.getValue());
			}
		});
		h1.add(visibleVertexCheck);
		
		
		Button test=new Button("test",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				final Canvas canvas=CanvasUtils.createCanvas(2048, 2048);
				THREE.ImageLoader().load(GWTThreeClothHair.INSTANCE.textureUrl, new ImageLoadHandler() {
					
					@Override
					public void onProgress(NativeEvent progress) {
						// TODO Auto-generated method stub
						
					}
					
					@Override
					public void onLoad(ImageElement imageElement) {
						ImageData data=ImageDataUtils.create(canvas, imageElement);
						
						//ImageDataUtils.replaceColor(data,GWTThreeClothHair.INSTANCE.defaultHairTextureColor,colorValue);
						
						ImageDataUtils.executeRGBFilter(data, new RGBColorFilter() {
							@Override
							public int[] filterRGB(int r, int g, int b, int a) {
								// TODO Auto-generated method stub
								//TODO make range & rect
								return GWTThreeClothHair.INSTANCE.hslOnRGB(r,g,b,0.1,0.01,0.01);
							}
						
						});
						
						
						ImageDataUtils.putImageData(data, canvas);
						Texture texture=THREE.TextureLoader().load(canvas.toDataUrl());
						//texture.setFlipY(false);
						GWTThreeClothHair.INSTANCE.getBodyMaterial().setMap(texture);
						//extremly slow
						
					}
					
					@Override
					public void onError(NativeEvent error) {
						// TODO Auto-generated method stub
						
					}
				});
				
				
			}
			
			
			
		});
		//basicPanel.add(test);
		
		//cannon controling
		this.add(new HTML("<h4>Cannon</h4>"));
		HorizontalPanel h2=new HorizontalPanel();
		h2.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		CheckBox cannonStopCheck=new CheckBox("stop");
		
		cannonStopCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				GWTThreeClothHair.INSTANCE.getCannonControler().setStopped(event.getValue());
			}
			
		});
		h2.add(cannonStopCheck);
		Button step=new Button("step",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GWTThreeClothHair.INSTANCE.getCannonControler().getWorld().step(1.0/60);
			}
		});
		h2.add(step);
		this.add(h2);
		
		
		
		
	}
}
