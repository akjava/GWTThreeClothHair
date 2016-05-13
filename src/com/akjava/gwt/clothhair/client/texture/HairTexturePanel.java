package com.akjava.gwt.clothhair.client.texture;

import java.io.IOException;
import java.util.List;

import com.akjava.gwt.clothhair.client.GWTThreeClothHair;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.ImageElementListener;
import com.akjava.gwt.lib.client.ImageElementUtils;
import com.akjava.gwt.lib.client.experimental.RectCanvasUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.textures.Texture;
import com.akjava.lib.common.graphics.Rect;
import com.google.common.collect.Lists;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;
import com.google.gwt.dom.client.ImageElement;
import com.google.gwt.event.dom.client.ChangeEvent;
import com.google.gwt.event.dom.client.ChangeHandler;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.ErrorEvent;
import com.google.gwt.event.logical.shared.ValueChangeEvent;
import com.google.gwt.event.logical.shared.ValueChangeHandler;
import com.google.gwt.text.shared.Renderer;
import com.google.gwt.user.client.ui.Button;
import com.google.gwt.user.client.ui.CheckBox;
import com.google.gwt.user.client.ui.HorizontalPanel;
import com.google.gwt.user.client.ui.Label;
import com.google.gwt.user.client.ui.ListBox;
import com.google.gwt.user.client.ui.ValueListBox;
import com.google.gwt.user.client.ui.VerticalPanel;

public class HairTexturePanel extends VerticalPanel{
	private boolean useCenter;
	private boolean useEven;
	private int extendCenter;
	
	private HairPatternPanel centerPattern;
	private ValueListBox<Integer> sliceBox;
	 int slice=1;
	//part of HairEditor?
	public HairTexturePanel(){
		//dynamic version
		//preset version
		
	
		canvas = CanvasUtils.createCanvas(256, 256);
		canvas.setCoordinateSpaceWidth(512);
		canvas.setCoordinateSpaceHeight(512);
		canvas.getContext2d().setStrokeStyle("#ffffff");
		this.add(canvas);
		canvas.setStyleName("transparent_bg");
		
		//tmp draw
		//final CanvasTexture canvasTexture=THREE.CanvasTexture(canvas.getCanvasElement());
		//canvasTexture.setFlipY(false);
		
		
		HorizontalPanel defaultPanel=new HorizontalPanel();
		add(defaultPanel);
		defaultPanel.add(new Label("Default"));
		
		List<Integer> sliceValues=Lists.newArrayList();
		for(int i=0;i<32;i++){
			sliceValues.add(i+1);
		}
		
		sliceBox = new ValueListBox<Integer>(new Renderer<Integer>() {
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
		defaultPanel.add(new Label("slice:"));
		defaultPanel.add(sliceBox);
		
		
		
		
		
		
		defaultPattern=new HairPatternPanel();
		add(defaultPattern);
		centerPattern=new HairPatternPanel();
		
		
		HorizontalPanel centerOptions=new HorizontalPanel();
		add(centerOptions);
		
		final CheckBox useEvenCheck=new CheckBox("Even center");
		final ValueListBox<Integer> extendCenterBox = new ValueListBox<Integer>(new Renderer<Integer>() {
			@Override
			public String render(Integer object) {
				return String.valueOf(object);
			}

			@Override
			public void render(Integer object, Appendable appendable) throws IOException {
				// TODO Auto-generated method stub
				
			}
		});
		
		CheckBox useCenterCheck=new CheckBox("Use center");
		useCenterCheck.setValue(false);
		useCenterCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				useCenter=event.getValue();
				centerPattern.setEnabled(event.getValue());
				useEvenCheck.setEnabled(event.getValue());
				extendCenterBox.setEnabled(event.getValue());
				updateCanvas();
			}
		});
		centerOptions.add(useCenterCheck);
		add(centerPattern);
		centerPattern.setEnabled(false);
		
		useEvenCheck.setEnabled(false);
		useEvenCheck.setValue(false);
		useEvenCheck.addValueChangeHandler(new ValueChangeHandler<Boolean>() {
			

			@Override
			public void onValueChange(ValueChangeEvent<Boolean> event) {
				useEven=event.getValue();
				updateCanvas();
			}
		});
		centerOptions.add(useEvenCheck);
		
		List<Integer> extendValues=Lists.newArrayList();
		for(int i=0;i<=16;i++){
			extendValues.add(i);
		}
		
		extendCenterBox.setEnabled(false);
		extendCenterBox.setValue(0);
		extendCenterBox.setAcceptableValues(extendValues);
		extendCenterBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
			
			@Override
			public void onValueChange(ValueChangeEvent<Integer> event) {
				extendCenter=event.getValue();
				updateCanvas();
			}
		});
		centerOptions.add(new Label("extend:"));
		centerOptions.add(extendCenterBox);
		
		
		
		
		HorizontalPanel buttons=new HorizontalPanel();
		this.add(buttons);
		Button updateBt=new Button("set pattern",new ClickHandler() {
			
			@Override
			public void onClick(ClickEvent event) {
				//redraw if need
				
				//canvasTexture.setNeedsUpdate(true);
				ImageElementUtils.createWithLoader(canvas.toDataUrl(), new ImageElementListener() {
					
					@Override
					public void onLoad(ImageElement element) {
						Texture texture=THREE.Texture(element);
						texture.setNeedsUpdate(true);
						texture.setFlipY(false);
						GWTThreeClothHair.INSTANCE.setTextureMap(texture);
					}
					
					@Override
					public void onError(String url, ErrorEvent event) {
						// TODO Auto-generated method stub
						
					}
				});
						
				
				
			}
		});
		buttons.add(updateBt);
		
		Button clearBt=new Button("clear pattern",new ClickHandler() {
			@Override
			public void onClick(ClickEvent event) {
				GWTThreeClothHair.INSTANCE.setTextureMap(null);
			}
		}
			);
		buttons.add(clearBt);
		
		updateCanvas();
	}
	
	private class HairPatternPanel extends VerticalPanel{
		 int mode;
		 int splitVertical=1;
		 int startVertical;
		 int endVertical;
		
		
		private ValueListBox<Integer> startBox;
		private ValueListBox<Integer> splitBox;
		private ValueListBox<Integer> endBox;
		private ListBox modeBox;
		public HairPatternPanel(){

			HorizontalPanel controls=new HorizontalPanel();
			controls.setVerticalAlignment(ALIGN_MIDDLE);
			this.add(controls);
			
			List<Integer> sliceValues=Lists.newArrayList();
			for(int i=0;i<32;i++){
				sliceValues.add(i+1);
			}
			
			
			List<Integer> verticalStartValues=Lists.newArrayList();
			for(int i=0;i<32;i++){
				verticalStartValues.add(i);
			}
			
			startBox = new ValueListBox<Integer>(new Renderer<Integer>() {
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
			controls.add(new Label("start"));
			controls.add(startBox);
			
			splitBox = new ValueListBox<Integer>(new Renderer<Integer>() {
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
			
			endBox = new ValueListBox<Integer>(new Renderer<Integer>() {
				@Override
				public String render(Integer object) {
					return String.valueOf(object);
				}

				@Override
				public void render(Integer object, Appendable appendable) throws IOException {
					// TODO Auto-generated method stub
					
				}
			});
			endBox.setValue(0);
			endBox.setAcceptableValues(verticalStartValues);
			endBox.addValueChangeHandler(new ValueChangeHandler<Integer>() {
				@Override
				public void onValueChange(ValueChangeEvent<Integer> event) {
					endVertical=event.getValue();
					updateCanvas();
				}
			});
			controls.add(new Label("end"));
			controls.add(endBox);
			
			
			modeBox = new ListBox();
			modeBox.addItem("STRAIGHT");
			modeBox.addItem("OVAL");
			modeBox.addItem("SHARP");
			modeBox.addItem("SOFT");
			modeBox.addItem("TRAPEZOID");
			modeBox.addItem("TRAPEZOID2");
			modeBox.addItem("TRAPEZOID3");
			
			//modeBox.addItem("TRAPEZOID4"); //somehow not good
			
			modeBox.addItem("TRIANGLE");
			modeBox.addItem("TRIANGLE2");
			
			modeBox.addItem("TRAPEZOID-S");
			modeBox.addItem("TRAPEZOID2-S");
			modeBox.addItem("TRAPEZOID3-S");
			modeBox.addItem("CURVE");
			modeBox.addItem("CURVE2");
			modeBox.addItem("CURVE3");
			modeBox.addItem("CURVE4");
			modeBox.setSelectedIndex(0);
			modeBox.addChangeHandler(new ChangeHandler() {
				
				@Override
				public void onChange(ChangeEvent event) {
					mode=modeBox.getSelectedIndex();
					updateCanvas();
				}
			});
			controls.add(modeBox);
		}
		public void setEnabled(boolean value) {
			endBox.setEnabled(value);
			modeBox.setEnabled(value);
			splitBox.setEnabled(value);
			startBox.setEnabled(value);
		}
	}
	
	public boolean isCenter(int index){
		if(!useCenter){
			return false;
		}
		
		int center=slice/2;
		int center2=center;
		if(useEven){
			if(slice%2==0){
				center2=center-1;
			}
			//LogUtils.log(center+","+center2+","+(slice%2));
		}
		
		if(extendCenter==0){
			return index==center || index==center2;
		}else{
			int min=center-extendCenter;
			int max=center+extendCenter;
			if(center!=center2){
				min=center2-extendCenter;
			}
			
			return index>=min && index<=max;
		}
		
		
		
		
		//TODO more options
	}
	
	private HairPatternPanel defaultPattern;
	
	private void updateCanvas(){
		CanvasUtils.clear(canvas);
		Context2d context2d= canvas.getContext2d();
		double w=(double)canvas.getCoordinateSpaceWidth()/slice;
		double h=canvas.getCoordinateSpaceHeight();
		double splitH=h/defaultPattern.splitVertical;
		double sh=splitH*defaultPattern.startVertical;
		RectCanvasUtils.fill(new Rect(0,0,canvas.getCoordinateSpaceWidth(),sh), canvas, "#ffffff");
		
		int center=slice/2;
		
		for(int i=0;i<slice;i++){
			int mode=defaultPattern.mode;
			double sx=i*w;
			double sy=sh;
			double ex=sx+w;
			double ey=h-splitH*defaultPattern.endVertical;
			
			if(isCenter(i)){
				double centerSplitH=h/centerPattern.splitVertical;
				double centerSh=centerSplitH*centerPattern.startVertical;
				sx=i*w;
				sy=centerSh;
				ex=sx+w;
				ey=h-centerSplitH*centerPattern.endVertical;
				
				//clear part of center
				Rect r=new Rect(sx,0,ex-sx,h);
				RectCanvasUtils.clear(canvas,r);
				
				RectCanvasUtils.fill(new Rect(sx,0,ex-sx,centerSh), canvas, "#ffffff");
				mode=centerPattern.mode;
				
				context2d.beginPath();
				strokePath(context2d,mode,sx,sy,ex,ey,i<center);
				context2d.closePath();
				context2d.fill();
				context2d.stroke();//?
			}
			
			
			
		}
		
		for(int i=0;i<slice;i++){
			int mode=defaultPattern.mode;
			double sx=i*w;
			double sy=sh;
			double ex=sx+w;
			double ey=h-splitH*defaultPattern.endVertical;
			
			if(!isCenter(i)){
			
			context2d.beginPath();
			strokePath(context2d,mode,sx,sy,ex,ey,i<center);
			context2d.closePath();
			context2d.fill();
			context2d.stroke();//?
			}
		}
	}
	
	private static final int STRAIGHT=0;
	private static final int OVAL=1;
	private static final int SHARP=2;
	private static final int SOFT=3;
	private static final int TRAPEZOID=4;
	private static final int TRAPEZOID2=5;
	private static final int TRAPEZOID3=6;
	//private static final int TRAPEZOID4=7;
	private static final int TRIANGLE=7; //maybe no need
	private static final int TRIANGLE2=8;
	
	private static final int TRAPEZOID_STRAIGHT=9;
	private static final int TRAPEZOID2STRAIGHT=10;
	private static final int TRAPEZOID3STRAIGHT=11;
	
	private static final int CURVE=12;
	private static final int CURVE2=13;
	private static final int CURVE3=14;
	private static final int CURVE4=15;
	private void strokePath(Context2d context,int mode,double sx,double sy,double ex,double ey,boolean leftSide){
		double w=ex-sx;
		//double h=ey-sy;
		if(mode==STRAIGHT){
			context.moveTo(sx, sy);
			context.lineTo(sx+w/2, ey);
			context.lineTo(ex, sy);
			
		}else if(mode==OVAL){
			context.moveTo(sx, sy);
			context.quadraticCurveTo(sx, ey, sx+w/2,ey);
			context.quadraticCurveTo(ex, ey, ex,sy);
			
		}else if(mode==SHARP){
			context.moveTo(sx, sy);
			context.quadraticCurveTo(sx+w/8*3, ey, sx+w/2,ey);
			context.quadraticCurveTo(sx+w/8*5, ey, ex,sy);
			
		}else if(mode==SOFT){
			context.moveTo(sx, sy);
			context.quadraticCurveTo(sx+w/4, ey, sx+w/2,ey);
			context.quadraticCurveTo(sx+w/4*3, ey, ex,sy);
			
		}else if(mode==TRAPEZOID || mode==TRAPEZOID2 || mode==TRAPEZOID3 ){
			double sp=w/8;
			if(mode==TRAPEZOID2){
				sp=w/16;
			}else if(mode==TRAPEZOID3){
				sp=w/32;
			}
			context.moveTo(sx, sy);
			context.lineTo(sx+sp, ey);
			context.lineTo(ex-sp, ey);
			context.lineTo(ex, sy);
			
		}else if(mode==TRIANGLE){
			context.moveTo(sx, sy);
			context.lineTo(sx, ey);
			context.lineTo(ex, sy);
			
		}else if(mode==TRIANGLE2){
			context.moveTo(sx, sy);
			context.lineTo(ex, ey);
			context.lineTo(ex, sy);
			
		}else if(mode==TRAPEZOID_STRAIGHT || mode==TRAPEZOID2STRAIGHT || mode==TRAPEZOID3STRAIGHT ){
			double sp=w/8;
			if(mode==TRAPEZOID2STRAIGHT){
				sp=w/16;
			}else if(mode==TRAPEZOID3STRAIGHT){
				sp=w/32;
			}
			double h=ey-sy;
			context.moveTo(sx, sy);
			context.lineTo(sx+sp, sy+h/2);
			
			context.quadraticCurveTo(sx+w/8*3, ey, sx+w/2,ey);
			context.quadraticCurveTo(sx+w/8*5, ey, ex-sp,sy+h/2);
			
			//context.lineTo(sx+w/2, ey); //straight
			
			//context.lineTo(ex-sp, sy+h/2);
			
			context.lineTo(ex, sy);
			
		}else if(mode==CURVE){
			if(leftSide){
				context.moveTo(sx, sy);
				context.quadraticCurveTo(sx, ey, ex+w/4,ey);
				context.quadraticCurveTo(sx+w/4*3, ey, ex,sy);
			}else{
				context.moveTo(ex, sy);
				context.quadraticCurveTo(ex, ey, sx-w/4,ey);
				context.quadraticCurveTo(ex-w/4*3, ey, sx,sy);
			}
			
			
		}else if(mode==CURVE2){
			if(leftSide){
				context.moveTo(sx, sy);
				context.quadraticCurveTo(sx, ey, ex+w/4,ey);
				context.quadraticCurveTo(sx+w/4, ey, ex,sy);
			}else{
				context.moveTo(ex, sy);
				context.quadraticCurveTo(ex, ey, sx-w/4,ey);
				context.quadraticCurveTo(ex-w/4, ey, sx,sy);
			}
			
			
		}else if(mode==CURVE3){
			if(leftSide){
				context.moveTo(sx, sy);
				context.quadraticCurveTo(sx, ey, ex+w,ey);
				context.quadraticCurveTo(sx+w, ey, ex,sy);
			}else{
				context.moveTo(ex, sy);
				context.quadraticCurveTo(ex, ey, sx-w,ey);
				context.quadraticCurveTo(ex-w, ey, sx,sy);
			}
			
			
		}else if(mode==CURVE4){
			if(leftSide){
				context.moveTo(sx, sy);
				context.quadraticCurveTo(sx, ey, ex+w*2,ey);
				context.quadraticCurveTo(sx+w, ey, ex,sy);
			}else{
				context.moveTo(ex, sy);
				context.quadraticCurveTo(ex, ey, sx-w*2,ey);
				context.quadraticCurveTo(ex-w, ey, sx,sy);
			}
			
			
		}
		context.closePath();
	}
	
	private Canvas canvas;
}
