package com.akjava.gwt.clothhair.client;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.html5.client.file.File;
import com.akjava.gwt.html5.client.file.FileUploadForm;
import com.akjava.gwt.html5.client.file.FileUtils;
import com.akjava.gwt.html5.client.file.FileUtils.DataURLListener;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.animation.AnimationClip;
import com.akjava.gwt.three.client.js.loaders.XHRLoader.XHRLoadHandler;
import com.google.common.collect.Lists;
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
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class FacialAnimationPanel extends VerticalPanel{

	private Label fileNameLabel;

	private AnimationClip animationClip;

	private CheckBox enableCheck;
	
	CharacterMovePanel characterMovePanel;
	public FacialAnimationPanel(final CharacterMovePanel characterMovePanel){
		this.characterMovePanel=characterMovePanel;
		add(new HTML("<h4>Facial-Animation</h4>"));
		HorizontalPanel namePanel=new HorizontalPanel();
		this.add(namePanel);
		namePanel.add(new Label("name:"));
		fileNameLabel = new Label();
		namePanel.add(fileNameLabel);
		
		HorizontalPanel h0=new HorizontalPanel();
		h0.setSpacing(4);
		this.add(h0);
		enableCheck = new CheckBox("Enable");
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
		h0.add(enableCheck);
		
		ValueListBox<Double> timeValueListBox=new ValueListBox<Double>(new Renderer<Double>() {

			@Override
			public String render(Double time) {
				if(time==null){
				return null;
				}
				if(time==-1){
					return "Use animation-clip time";
				}else{
					return time+" ms";
				}
			}

			@Override
			public void render(Double object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		List<Double> values=Lists.newArrayList(-1.0,0.1,0.25,0.5,1.0,2.0);
		timeValueListBox.setValue(-1.0);
		timeValueListBox.setAcceptableValues(values);
		h0.add(new Label("PlayTime:"));
		h0.add(timeValueListBox);
		timeValueListBox.addValueChangeHandler(new ValueChangeHandler<Double>() {

			@Override
			public void onValueChange(ValueChangeEvent<Double> event) {
				GWTThreeClothHair.INSTANCE.setFacialAnimationTime(event.getValue());
				characterMovePanel.startAnimation();
			}
		});
		
		
		FileUploadForm jsonUpload=FileUtils.createSingleTextFileUploadForm(new DataURLListener() {
			
			@Override
			public void uploaded(File file, String text) {
				fileNameLabel.setText(file.getFileName());
				
				setFacialAnimation(text);
				
			}
		}, true);
		jsonUpload.setAccept(FileUploadForm.ACCEPT_JSON);
		this.add(jsonUpload);
		
		HorizontalPanel h1=new HorizontalPanel();
		this.add(h1);
		
		//TODO add clear button,reset can't clear face animation
		Button test1=new Button("Smile",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				THREE.XHRLoader().load("animation/clip-smile4b.json", new XHRLoadHandler() {
					
					@Override
					public void onLoad(String text) {
						setFacialAnimation(text);
					}
				});
				
			}
		});
		h1.add(test1);
		
		Button test2=new Button("Pero",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				THREE.XHRLoader().load("animation/clip-peropero.json", new XHRLoadHandler() {
					
					@Override
					public void onLoad(String text) {
						setFacialAnimation(text);
					}
				});
				
			}
		});
		h1.add(test2);
	}
	
	public void setFacialAnimation(String text){
		JSONValue value=JSONParser.parseStrict(text);
		AnimationClip clip=AnimationClip.parse(value.isObject().getJavaScriptObject());
		animationClip=clip;
		
		clip.setName("facial");
		//LogUtils.log(clip);
		
		enableCheck.setValue(true);//no event call
		GWTThreeClothHair.INSTANCE.setFacialAnimationClip(animationClip);
		characterMovePanel.startAnimation();
	}
	
}
