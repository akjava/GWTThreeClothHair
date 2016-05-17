package com.akjava.gwt.clothhair.client;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.js.animation.AnimationClip;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.json.client.JSONParser;
import com.google.gwt.json.client.JSONValue;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FacialAnimationPanel extends VerticalPanel{

	private Label fileNameLabel;

	private AnimationClip animationClip;
	
	public FacialAnimationPanel(final CharacterMovePanel characterMovePanel){
		add(new Label("Facial-Animation"));
		HorizontalPanel namePanel=new HorizontalPanel();
		this.add(namePanel);
		namePanel.add(new Label("name:"));
		fileNameLabel = new Label();
		namePanel.add(fileNameLabel);
		
		final CheckBox enableCheck=new CheckBox("Enable");
		enableCheck.setValue(true);
		enableCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				// TODO Auto-generated method stub
				if(event.getValue()){
					GWTThreeClothHair.INSTANCE.setFacialAnimationClip(animationClip);
				}else{
					GWTThreeClothHair.INSTANCE.setFacialAnimationClip(null);
				}
				
				characterMovePanel.startAnimation();
			}
		});
		this.add(enableCheck);
		
		FileUploadForm jsonUpload=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				fileNameLabel.setText(file.getFileName());
				JSONValue value=JSONParser.parseStrict(text);
				AnimationClip clip=AnimationClip.parse(value.isObject().getJavaScriptObject());
				animationClip=clip;
				
				clip.setName("facial");
				LogUtils.log(clip);
				
				enableCheck.setValue(true);//no event call
				GWTThreeClothHair.INSTANCE.setFacialAnimationClip(animationClip);
				characterMovePanel.startAnimation();
				
			}
		}, true);
		jsonUpload.setAccept(FileUploadForm.ACCEPT_JSON);
		this.add(jsonUpload);
	}
	
}
