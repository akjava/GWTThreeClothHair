package com.akjava.gwt.clothhair.client;

import com.akjava.gwt.clothhair.client.lights.LightDataPanel;
import com.akjava.gwt.html5.client.input.ColorBox;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.StorageException;
import com.akjava.gwt.lib.client.experimental.ImageDataUtils;
import com.akjava.gwt.lib.client.experimental.ImageDataUtils.RGBColorFilter;
import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Object3D;
import com.akjava.gwt.three.client.js.lights.DirectionalLight;
import com.akjava.gwt.three.client.js.loaders.ImageLoader.ImageLoadHandler;
import com.akjava.gwt.three.client.js.materials.MeshPhongMaterial;
import com.akjava.gwt.three.client.js.textures.Texture;
import com.akjava.lib.common.utils.ColorUtils;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.ImageData;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.TabPanel;
import com.google.gwt.user.client.ui.VerticalPanel;

public class BasicPanel extends VerticalPanel{

	public BasicPanel(){
		TabPanel tab=new TabPanel();
		this.add(tab);
		VerticalPanel generalPanel=new VerticalPanel();
		
		tab.add(generalPanel,"General");
		//this.add(new HTML("<h4>Visible</h4>"));
		HorizontalPanel h1=new HorizontalPanel();
		generalPanel.add(h1);
		
		h1.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		
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
		
		CheckBox shadowCheck=new CheckBox("Shadow");
		shadowCheck.setValue(true);
		shadowCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				GWTThreeClothHair.INSTANCE.getRenderer().getShadowMap().setEnabled(event.getValue());
				
				for(int i=0;i<GWTThreeClothHair.INSTANCE.getScene().getChildren().length();i++){
				Object3D children=GWTThreeClothHair.INSTANCE.getScene().getChildren().get(i);
				if(children.getType().equals("DirectionalLight")){
					DirectionalLight light=children.cast();
					GWTThreeClothHair.INSTANCE.getRenderer().clearTarget(light.getShadow().getMap());
					}
				
				}
				
			}
		});
		h1.add(shadowCheck);
		
		/*
		 * faild how to control
		 */
		CheckBox receiveShadowCheck=new CheckBox("S-Receive");
		receiveShadowCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				GWTThreeClothHair.INSTANCE.getGroundMesh().setReceiveShadow(event.getValue());
				GWTThreeClothHair.INSTANCE.getScene().remove(GWTThreeClothHair.INSTANCE.getGroundMesh());
				GWTThreeClothHair.INSTANCE.getScene().add(GWTThreeClothHair.INSTANCE.getGroundMesh());
				
				GWTThreeClothHair.INSTANCE.getCharacterMesh().setReceiveShadow(event.getValue());
				GWTThreeClothHair.INSTANCE.getClothSimulator().setReceiveShadow(event.getValue());
			}
		});
		//h1.add(receiveShadowCheck);
		receiveShadowCheck.setTitle("receive shadow self");
		
		generalPanel.add(new HTML("<h4>Camera</h4>"));
		LabeledInputRangeWidget2 near=new LabeledInputRangeWidget2("near", 0.1, 100, 0.1);
		generalPanel.add(near);
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
		
		
		generalPanel.add(new HTML("<h4>Texture</h4>"));
		HorizontalPanel textureButtons=new HorizontalPanel();
		generalPanel.add(textureButtons);
		Button reload=new Button("reload texture",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GWTThreeClothHair.INSTANCE.reloadBodyTexture();
			}
		});
		textureButtons.add(reload);
		
		
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
		VerticalPanel ammoPanel=new VerticalPanel();
		tab.add(ammoPanel,"Ammo");
		HorizontalPanel ha=new HorizontalPanel();
		ha.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		CheckBox ammoStopCheck=new CheckBox("stop");
		
		ammoStopCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				GWTThreeClothHair.INSTANCE.getAmmoControler().setStopped(event.getValue());
			}
			
		});
		ha.add(ammoStopCheck);
		Button ammostep=new Button("step",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GWTThreeClothHair.INSTANCE.getAmmoControler().getAmmoControler().update();
			}
		});
		ha.add(ammostep);
		ammoPanel.add(ha);
		
		//TODO fix effect new-add cloth
		CheckBox ammoBoneCheck=new CheckBox("bone");
		
		ammoBoneCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				GWTThreeClothHair.INSTANCE.getClothSimulator().getAmmoHairControler().updateVisibleBone(event.getValue());
			}
			
		});
		ha.add(ammoBoneCheck);
		
		//cannon controling
		generalPanel.add(new HTML("<h4>Cannon</h4>"));
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
		generalPanel.add(h2);
		
		
		generalPanel.add(new HTML("<h4>Three.js</h4>"));
		HorizontalPanel h3=new HorizontalPanel();
		h3.setVerticalAlignment(VerticalPanel.ALIGN_MIDDLE);
		generalPanel.add(h3);
		CheckBox antialiasCheck=new CheckBox();
		h3.add(antialiasCheck);
		h3.add(new Label("antialias(need reload page)"));
		boolean antialias=GWTThreeClothHair.INSTANCE.getStorageControler().getValue(GWTThreeClothHairStorageKeys.THREEJS_RENDERER_ANTIALIAS, false);
		antialiasCheck.setValue(antialias);
		
		antialiasCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				try {
					GWTThreeClothHair.INSTANCE.getStorageControler().setValue(GWTThreeClothHairStorageKeys.THREEJS_RENDERER_ANTIALIAS, event.getValue());
				} catch (StorageException e) {
					Window.alert("faild maybe quota problem");
					e.printStackTrace();
				}
			}
			
		});
		
		HorizontalPanel h4=new HorizontalPanel();
		generalPanel.add(h4);
		h4.add(new Label("Sky:"));
		ColorBox bgColor=new ColorBox();
		bgColor.setValue(ColorUtils.toCssColor(GWTThreeClothHair.INSTANCE.getStorageControler().getValue(GWTThreeClothHairStorageKeys.THREEJS_CLEAR_COLOR, 0)));
		bgColor.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				int hex=ColorUtils.toColor(event.getValue());
				GWTThreeClothHair.INSTANCE.getRenderer().setClearColor(hex);
				try {
					GWTThreeClothHair.INSTANCE.getStorageControler().setValue(GWTThreeClothHairStorageKeys.THREEJS_CLEAR_COLOR,hex);
				} catch (StorageException e) {
					Window.alert("can't update color,because of quote:"+e.getMessage());
					e.printStackTrace();
				}
			}
		
		});
		h4.add(bgColor);
		
		h4.add(new Label("Ground:"));
		ColorBox groundColor=new ColorBox();
		groundColor.setValue(ColorUtils.toCssColor(GWTThreeClothHair.INSTANCE.getStorageControler().getValue(GWTThreeClothHairStorageKeys.KEY_GROUND_COLOR, 0x888888)));
		groundColor.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				int hex=ColorUtils.toColor(event.getValue());
				MeshPhongMaterial material=GWTThreeClothHair.INSTANCE.getGroundMesh().getMaterial().cast();
				material.getColor().setHex(hex);
				try {
					GWTThreeClothHair.INSTANCE.getStorageControler().setValue(GWTThreeClothHairStorageKeys.KEY_GROUND_COLOR,hex);
				} catch (StorageException e) {
					Window.alert("can't update color,because of quote:"+e.getMessage());
					e.printStackTrace();
				}
			}
		
		});
		h4.add(groundColor);
		
		VerticalPanel lightPanel=new VerticalPanel();
		tab.add(lightPanel,"Light");
		
		lightPanel.add(new LightDataPanel(GWTThreeClothHairStorageKeys.KEY_LIGHTS,GWTThreeClothHair.INSTANCE.getStorageControler()));
		
		tab.selectTab(0);
	}
}
