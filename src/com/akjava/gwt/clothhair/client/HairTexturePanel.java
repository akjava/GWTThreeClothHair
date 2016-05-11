package com.akjava.gwt.clothhair.client;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.experimental.RectCanvasUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.textures.CanvasTexture;
import com.akjava.lib.common.graphics.Rect;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HairTexturePanel extends VerticalPanel{

	//part of HairEditor?
	public HairTexturePanel(){
		//dynamic version
		//preset version
		
	
		canvas = CanvasUtils.createCanvas(256, 256);
		this.add(canvas);
		canvas.setStyleName("transparent_bg");
		
		//tmp draw
		final CanvasTexture canvasTexture=THREE.CanvasTexture(canvas.getCanvasElement());
		canvasTexture.setFlipY(false);
		
		
		HorizontalPanel controls=new HorizontalPanel();
		controls.setVerticalAlignment(ALIGN_MIDDLE);
		this.add(controls);
		
		List<Integer> sliceValues=Lists.newArrayList();
		for(int i=0;i<16;i++){
			sliceValues.add(i+1);
		}
		
		final ValueListBox<Integer> sliceBox=new ValueListBox<Integer>(new Renderer<Integer>() {
			@Override
			public String render(Integer object) {
				return String.valueOf(object);
			}

			@Override
			public void render(Integer object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		sliceBox.setValue(1);
		sliceBox.setAcceptableValues(sliceValues);
		sliceBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				slice=event.getValue();
				updateCanvas();
			}
		});
		controls.add(new Label("slice:"));
		controls.add(sliceBox);
		
		List<Integer> verticalStartValues=Lists.newArrayList();
		for(int i=0;i<16;i++){
			verticalStartValues.add(i);
		}
		
		final ValueListBox<Integer> startBox=new ValueListBox<Integer>(new Renderer<Integer>() {
			@Override
			public String render(Integer object) {
				return String.valueOf(object);
			}

			@Override
			public void render(Integer object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		startBox.setValue(0);
		startBox.setAcceptableValues(verticalStartValues);
		startBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			

			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				startVertical=event.getValue();
				updateCanvas();
			}
		});
		controls.add(new Label("start:"));
		controls.add(startBox);
		
		final ValueListBox<Integer> splitBox=new ValueListBox<Integer>(new Renderer<Integer>() {
			@Override
			public String render(Integer object) {
				return String.valueOf(object);
			}

			@Override
			public void render(Integer object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		splitBox.setValue(1);
		splitBox.setAcceptableValues(sliceValues);
		splitBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				splitVertical=event.getValue();
				updateCanvas();
			}
		});
		controls.add(new Label("of"));
		controls.add(splitBox);
		
		
		final ListBox modeBox=new ListBox();
		modeBox.addItem("STRAIGHT");
		modeBox.addItem("OVAL");
		modeBox.addItem("SHARP");
		modeBox.addItem("CURVE");
		modeBox.setSelectedIndex(0);
		modeBox.addChangeHandler(new ChangeHandler() {
			
			@Override
			public void onChange(ChangeEvent event) {
				mode=modeBox.getSelectedIndex();
				updateCanvas();
			}
		});
		controls.add(modeBox);
		
		HorizontalPanel buttons=new HorizontalPanel();
		this.add(buttons);
		Button updateBt=new Button("Update",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				//redraw if need
				
				canvasTexture.setNeedsUpdate(true);
				GWTThreeClothHair.INSTANCE.setTextureMap(canvasTexture);
			}
		});
		buttons.add(updateBt);
		
		updateCanvas();
	}
	private void updateCanvas(){
		CanvasUtils.clear(canvas);
		Context2d context2d= canvas.getContext2d();
		double w=(double)canvas.getCoordinateSpaceWidth()/slice;
		double h=canvas.getCoordinateSpaceHeight();
		double sh=h/splitVertical*startVertical;
		RectCanvasUtils.fill(new Rect(0,0,canvas.getCoordinateSpaceWidth(),sh), canvas, "#ffffff");
		
		for(int i=0;i<slice;i++){
			double sx=i*w;
			double sy=sh;
			double ex=sx+w;
			double ey=h;
			
			context2d.beginPath();
			strokePath(context2d,mode,sx,sy,ex,ey);
			context2d.closePath();
			context2d.fill();
		}
	}
	private int mode;
	private static final int STRAIGHT=0;
	private static final int OVAL=1;
	private static final int SHARP=2;
	private static final int CURVE=3;
	private void strokePath(Context2d context,int mode,double sx,double sy,double ex,double ey){
		double w=ex-sx;
		//double h=ey-sy;
		if(mode==STRAIGHT){
			context.moveTo(sx, sy);
			context.lineTo(sx+w/2, ey);
			context.lineTo(ex, sy);
			context.closePath();
		}else if(mode==OVAL){
			context.moveTo(sx, sy);
			context.quadraticCurveTo(sx, ey, sx+w/2,ey);
			context.quadraticCurveTo(ex, ey, ex,sy);
			context.closePath();
		}else if(mode==SHARP){
			context.moveTo(sx, sy);
			context.quadraticCurveTo(sx+w/8*3, ey, sx+w/2,ey);
			context.quadraticCurveTo(sx+w/8*5, ey, ex,sy);
			context.closePath();
		}else if(mode==CURVE){
			context.moveTo(sx, sy);
			context.quadraticCurveTo(sx+w/4, ey, sx+w/2,ey);
			context.quadraticCurveTo(sx+w/4*3, ey, ex,sy);
			context.closePath();
		}
	}
	
	private int splitVertical=1;
	private int startVertical;
	private int slice=1;
	private Canvas canvas;
}
