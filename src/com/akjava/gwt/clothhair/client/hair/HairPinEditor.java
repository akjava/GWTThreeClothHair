package com.akjava.gwt.clothhair.client.hair;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.google.common.collect.Lists;
import com.google.gwt.editor.client.Editor;
import com.google.gwt.editor.client.EditorDelegate;
import com.google.gwt.editor.client.ValueAwareEditor;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.IntegerBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HairPinEditor extends VerticalPanel implements Editor<HairPin>,ValueAwareEditor<HairPin>{

	

	public HairPinEditor(){
		List<Integer> values=Lists.newArrayList();
		for(int i=0;i<=31;i++){
			values.add(i-1);
		}
		indexBox = new IntegerBox();
		add(indexBox);
		indexBox.setValue(-1);//TODO change checkbox?
		
		indexBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				flush();
			}
		});
	}

	private HairPin value;
	private IntegerBox indexBox;
	
	
@Override
			public void setDelegate(EditorDelegate<HairPin> delegate) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void flush() {
				if(value==null){
					return;
				}
				
				value.setTargetClothIndex(indexBox.getValue());
				
			}

			@Override
			public void onPropertyChange(String... paths) {
				// TODO Auto-generated method stub
				
			}

			@Override
			public void setValue(HairPin value) {
				this.value=value;
				indexBox.setValue(value.getTargetClothIndex());
			}
	
}
