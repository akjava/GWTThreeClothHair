package com.akjava.gwt.clothhair.client.scene;

import java.util.List;

import com.akjava.gwt.clothhair.client.GWTThreeClothHair;
import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Object3D;
import com.akjava.gwt.three.client.js.loaders.ObjectLoader;
import com.akjava.gwt.three.client.js.loaders.ObjectLoader.ObjectLoadHandler;
import com.akjava.gwt.three.client.js.objects.Group;
import com.google.common.collect.Lists;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class ScenePanel extends VerticalPanel{
private  Group house;
private CheckBox sceneVisibleCheck;
public ScenePanel(){
	add(new Label("Only import mesh & material.Testing with SweetHome3D"));
	FileUploadForm upload=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
		
		@Override
		public void uploaded(File file, String text) {
			if(house!=null){
				GWTThreeClothHair.INSTANCE.getScene().remove(house);
			}
			ObjectLoader loader=THREE.ObjectLoader();
			JSONValue json=JSONParser.parseStrict(text);
			Object3D scene=loader.parse(json.isObject().getJavaScriptObject());
			String type=scene.getType();
			house=THREE.Group();
			double scale=10;
			house.getScale().setScalar(scale);
			house.getPosition().set(226*scale,0,340*scale);//scene
			sceneVisibleCheck.setValue(true);
			
			if(type.equals("Scene")){
				List<Object3D> objs=Lists.newArrayList();
				for(int i=0;i<scene.getChildren().length();i++){
					objs.add(scene.getChildren().get(i));	
				}
				
				for(Object3D object:objs){
				if(object.getType().equals("Mesh")){
					scene.remove(object);//remove first
					house.add(object);
				}else{
					LogUtils.log("Scene Importer not support type="+object.getType());
				}
				}
				
				
				GWTThreeClothHair.INSTANCE.getScene().add(house);
			}else{
				Window.alert("Scene Importer not support type="+type);
			}
			
		}
	}, true);
	add(upload);
	upload.setAccept(FileUploadForm.ACCEPT_JSON);
	
	
	
	sceneVisibleCheck = new CheckBox("Visible Scene/House");
	sceneVisibleCheck.setValue(true);
	add(sceneVisibleCheck);
	sceneVisibleCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
		
		@Override
		public void onValueChange(ValueChangeEvent<Boolean> event) {
			if(house!=null){
				house.setVisible(event.getValue());
			}
		}
	});
	}
}
