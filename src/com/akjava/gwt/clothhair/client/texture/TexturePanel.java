package com.akjava.gwt.clothhair.client.texture;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.ImageFileListener;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.materials.MeshPhongMaterial;
import com.akjava.gwt.three.client.js.textures.Texture;
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
	private HairTextureDataEditor hairTextureDataEditor;
	public HairTextureDataEditor getHairTextureDataEditor() {
		return hairTextureDataEditor;
	}
	public TexturePanel(final MeshPhongMaterial material){
		this.material=material;
		HorizontalPanel h1=new HorizontalPanel();
		h1.setVerticalAlignment(ALIGN_MIDDLE);
		this.add(h1);
		//TODO add global options
		
		hairTextureDataEditor = new HairTextureDataEditor();
		this.add(hairTextureDataEditor);
		hairTextureDataEditor.setVisible(false);//wake up on set value
		
		
		
		//TODO support map;
		/* enable again later
		final CheckBox check=new CheckBox("enable");
		HorizontalPanel h2=new HorizontalPanel();
		this.add(h2);
		h2.add(createTitle("Map-Image"));
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
		*/
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
