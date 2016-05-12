package com.akjava.gwt.clothhair.client.texture;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.ImageFileListener;
import com.akjava.gwt.html5.client.input.ColorBox;
import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.materials.MeshPhongMaterial;
import com.akjava.gwt.three.client.js.textures.Texture;
import com.akjava.lib.common.utils.ColorUtils;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class TexturePanel extends VerticalPanel{

	private final String labelWidth="80px";
	private MeshPhongMaterial material;
	private Texture texture;
	public TexturePanel(final MeshPhongMaterial material){
		this.material=material;
		HorizontalPanel h1=new HorizontalPanel();
		this.add(h1);
		h1.add(createTitle("Color"));
		ColorBox color=new ColorBox("color", "#553817");
		h1.add(color);
		color.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				int colorValue=ColorUtils.toColor(event.getValue());
				material.getColor().setHex(colorValue);
			}
		});
		
		LabeledInputRangeWidget2 opacityRange=new LabeledInputRangeWidget2("Opacity", 0, 1, 0.01);
		opacityRange.getLabel().setWidth(labelWidth);
		this.add(opacityRange);
		opacityRange.addtRangeListener(new ValueChangeHandler<Number>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				material.setOpacity(event.getValue().doubleValue());
			}
		});
		opacityRange.setValue(1);
		
		LabeledInputRangeWidget2 alphaTestRange=new LabeledInputRangeWidget2("AlphaTest", 0, 1, 0.01);
		alphaTestRange.getLabel().setWidth(labelWidth);
		this.add(alphaTestRange);
		alphaTestRange.addtRangeListener(new ValueChangeHandler<Number>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				material.setAlphaTest(event.getValue().doubleValue());
			}
		});
		alphaTestRange.setValue(material.getAlphaTest());
		
		final CheckBox check=new CheckBox("enable");
		
		//TODO support map;
		HorizontalPanel h2=new HorizontalPanel();
		this.add(h2);
		h1.add(createTitle("Map"));
		FileUploadForm mapUpload=FileUtils.createImageFileUploadForm(new ImageFileListener() {
			
			

			@Override
			public void uploaded(File file, ImageElement imageElement) {
				texture = THREE.Texture(imageElement);
				texture.setFlipY(false);
				texture.setNeedsUpdate(true);//very important
				textureUpdate(check.getValue());
			}
		}, true, true);
		mapUpload.setAccept(FileUploadForm.ACCEPT_IMAGE);
		h2.add(mapUpload);
		
		h2.add(check);
		check.setValue(true);
		check.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				textureUpdate(event.getValue());
				
			}
		});
	}
	public void textureUpdate(boolean enable){
		if(enable){
			material.setMap(texture);
		}else{
			material.setMap(null);
		}
		material.setNeedsUpdate(true);
	}
	
	public Label createTitle(String text){
		
		Label label=new Label(text);
		label.setWidth(labelWidth);
		return label;
	}
}
