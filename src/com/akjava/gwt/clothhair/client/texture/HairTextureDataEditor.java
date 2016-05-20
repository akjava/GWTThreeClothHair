package com.akjava.gwt.clothhair.client.texture;

import com.akjava.gwt.clothhair.client.GWTThreeClothHair;
import com.akjava.gwt.html5.client.input.ColorBox;
import com.akjava.gwt.three.client.gwt.ui.LabeledInputRangeWidget2;
import com.akjava.lib.common.utils.ColorUtils;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HairTextureDataEditor extends VerticalPanel implements Editor<HairTextureData>,ValueAwareEditor<HairTextureData>{

	public HairTextureDataEditor(){

		useLocalColorCheck = new CheckBox("use local color");
		useLocalColorCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				colorBox.setVisible(event.getValue());
				flush();
				GWTThreeClothHair.INSTANCE.updateHairTextureData(false);
			}
		});
		this.add(useLocalColorCheck);
		
		HorizontalPanel h1=new HorizontalPanel();
		h1.setVerticalAlignment(ALIGN_MIDDLE);
		this.add(h1);
		
		
		
		h1.add(createTitle("Local Color"));
		colorBox = new ColorBox("color", "#553817");
		h1.add(colorBox);
		colorBox.setVisible(false);
		
		colorBox.addValueChangeHandler(new ValueChangeHandler<String>() {

			@Override
			public void onValueChange(ValueChangeEvent<String> event) {
				int colorValue=ColorUtils.toColor(event.getValue());
				flush();
				GWTThreeClothHair.INSTANCE.updateHairTextureData(false);
				//material.getColor().setHex(colorValue);
			}
		});
		
		opacityRange = new LabeledInputRangeWidget2("Opacity", 0, 1, 0.01);
		opacityRange.getLabel().setWidth(labelWidth);
		this.add(opacityRange);
		opacityRange.addtRangeListener(new ValueChangeHandler<Number>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				flush();
				GWTThreeClothHair.INSTANCE.updateHairTextureData(false);
				//material.setOpacity(event.getValue().doubleValue());
			}
		});
		opacityRange.setValue(1);
		
		alphaTestRange = new LabeledInputRangeWidget2("AlphaTest", 0, 1, 0.01);
		alphaTestRange.getLabel().setWidth(labelWidth);
		this.add(alphaTestRange);
		alphaTestRange.addtRangeListener(new ValueChangeHandler<Number>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Number> event) {
				flush();
				GWTThreeClothHair.INSTANCE.updateHairTextureData(false);
				
				//material.setAlphaTest(event.getValue().doubleValue());
				//material.setNeedsUpdate(true);
			}
		});
		//alphaTestRange.setValue(material.getAlphaTest());
		
		enablePatternTextureCheck = new CheckBox("enable pattern-texture");
		enablePatternTextureCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				// TODO Auto-generated method stub
				flush();
				GWTThreeClothHair.INSTANCE.updateHairTextureData(true);
				hairPatternDataEditor.setVisible(event.getValue());
			}
		});
		this.add(enablePatternTextureCheck);
		
		hairPatternDataEditor = new HairPatternDataEditor();
		hairPatternDataEditor.setVisible(false);
		this.add(hairPatternDataEditor);
	}
	
	@Override
	public void setDelegate(EditorDelegate<HairTextureData> delegate) {
	}
	
	private final String labelWidth="80px";
	public Label createTitle(String text){
		Label label=new Label(text);
		label.setWidth(labelWidth);
		return label;
	}

	@Override
	public void flush() {
		if(this.value==null){
			return;
		}
		value.setColor(ColorUtils.toColor(colorBox.getValue()));
		value.setOpacity(opacityRange.getValue());
		value.setAlphaTest(alphaTestRange.getValue());
		value.setEnablePatternImage(enablePatternTextureCheck.getValue());
		value.setUseLocalColor(useLocalColorCheck.getValue());
		//hairPatternDataEditor.flush(); //there are set parameter box
		//alredy linked hairPatternDataEditor
	}

	@Override
	public void onPropertyChange(String... paths) {
		// TODO Auto-generated method stub
		
	}

	private HairTextureData value;
	private ColorBox colorBox;
	private LabeledInputRangeWidget2 opacityRange;
	private LabeledInputRangeWidget2 alphaTestRange;
	private HairPatternDataEditor hairPatternDataEditor;
	private CheckBox enablePatternTextureCheck;
	private CheckBox useLocalColorCheck;
	@Override
	public void setValue(HairTextureData value) {
		this.value=value;
		if(value==null){//TODO better disable
			this.setVisible(false);
			return;
		}else{
			this.setVisible(true);
		}
		
		
		useLocalColorCheck.setValue(value.isUseLocalColor());
		
		colorBox.setVisible(value.isUseLocalColor());
		
		colorBox.setValue(ColorUtils.toCssColor(value.getColor()));
		opacityRange.setValue(value.getOpacity());
		alphaTestRange.setValue(value.getAlphaTest());
		
		enablePatternTextureCheck.setValue(value.isEnablePatternImage());
		hairPatternDataEditor.setVisible(value.isEnablePatternImage());
		
		hairPatternDataEditor.setValue(value.getHairPatternData());
	}

}
