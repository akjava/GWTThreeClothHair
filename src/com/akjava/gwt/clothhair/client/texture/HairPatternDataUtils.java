package com.akjava.gwt.clothhair.client.texture;

import com.akjava.gwt.clothhair.client.texture.HairPatternData.DrawPatternData;
import com.akjava.gwt.lib.client.CanvasUtils;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.lib.client.experimental.RectCanvasUtils;
import com.akjava.lib.common.graphics.Rect;
import com.akjava.lib.common.utils.ColorUtils;
import com.google.gwt.canvas.client.Canvas;
import com.google.gwt.canvas.dom.client.Context2d;

import static com.akjava.gwt.clothhair.client.texture.HairPatternData.*;//mode

public class HairPatternDataUtils {

	public static boolean isCenter(int index,HairPatternData data){
		if(!data.isUseCenter()){
			return false;
		}
		
		int center=data.getSlices()/2;
		int center2=center;
		if(data.isEvenCenter()){
			if(data.getSlices()%2==0){
				center2=center-1;
			}
			//LogUtils.log(center+","+center2+","+(slice%2));
		}
		
		if(data.getExtendCenter()==0){
			return index==center || index==center2;
		}else{
			int min=center-data.getExtendCenter();
			int max=center+data.getExtendCenter();
			if(center!=center2){
				min=center2-data.getExtendCenter();
			}
			
			return index>=min && index<=max;
		}
		
		
		
		
		//TODO more options
	}
	
	public static void paint(Canvas canvas,HairPatternData data){
		//LogUtils.log(data);
		CanvasUtils.clear(canvas);
		canvas.getContext2d().setStrokeStyle("#ffffff");//always stroke
		
		int gray=(int) data.getStrokeGrayscale();
		String strokeColor=ColorUtils.toCssGrayColor(gray);
		if(data.isStroke()){
			canvas.getContext2d().setStrokeStyle(strokeColor);
		}
		
		DrawPatternData defaultPattern=data.getDefaultPatternData();
		
		Context2d context2d= canvas.getContext2d();
		double w=(double)canvas.getCoordinateSpaceWidth()/data.getSlices();
		double h=canvas.getCoordinateSpaceHeight();
		double splitH=h/data.getDefaultPatternData().getSplitVertical();
		double sh=splitH*data.getDefaultPatternData().getStartVertical();
		
		//RectCanvasUtils.fill(new Rect(0,0,canvas.getCoordinateSpaceWidth(),sh), canvas, "#ffffff");
		
		int center=data.getSlices()/2;
		
		context2d.setFillStyle("#ffffff");
		
		//draw center first
		for(int i=0;i<data.getSlices();i++){
			int mode=defaultPattern.getMode();
			double sx=i*w;
			double sy=sh;
			double ex=sx+w;
			double ey=h-splitH*defaultPattern.getEndVertical();
			
			if(isCenter(i,data)){
				
				boolean lr=i<center;
				if(data.getLrMode()==LR_LEFT){
					lr=true;
				}else if(data.getLrMode()==LR_RIGHT){
					lr=false;
				}
				
				double centerSplitH=h/data.getCenterPatternData().getSplitVertical();
				double centerSh=centerSplitH*data.getCenterPatternData().getStartVertical();
				sx=i*w;
				sy=centerSh;
				ex=sx+w;
				ey=h-centerSplitH*data.getCenterPatternData().getEndVertical();
				
				//clear part of center
				/*
				Rect r=new Rect(sx,0,ex-sx,h);
				RectCanvasUtils.clear(canvas,r);//why erasing?
				*/
				
				
				/*
				 * this fill need paint gap between each slices(somehow fill make gap)
				 */
				RectCanvasUtils.fill(new Rect(sx,0,ex-sx,centerSh), canvas, "#ffffff");
				
				mode=data.getCenterPatternData().getMode();
				
				
				context2d.beginPath();
				strokePath(context2d,mode,sx,sy,ex,ey,lr,true);
				//context2d.closePath();
				context2d.fill();
				
				context2d.beginPath();
				strokePath(context2d,mode,sx,sy,ex,ey,lr,false);
				context2d.stroke();//?
				
			}
			
			
			
		}
		
		for(int i=0;i<data.getSlices();i++){
			boolean lr=i<center;
			if(data.getLrMode()==LR_LEFT){
				lr=true;
			}else if(data.getLrMode()==LR_RIGHT){
				lr=false;
			}
			
			int mode=defaultPattern.getMode();
			double sx=i*w;
			double sy=sh;
			double ex=sx+w;
			double ey=h-splitH*defaultPattern.getEndVertical();
			
			if(!isCenter(i,data)){
			
			RectCanvasUtils.fill(new Rect(sx,0,ex-sx,sh), canvas, "#ffffff");
				
			context2d.beginPath();
			strokePath(context2d,mode,sx,sy,ex,ey,lr,true);//TODO support lef-right-mode
			//context2d.closePath();
			context2d.fill();
			
			context2d.beginPath();
			strokePath(context2d,mode,sx,sy,ex,ey,lr,false);
			context2d.stroke();//TODO support switch
			}
		}
		
	}
	
	private static boolean isRightSideCurve(boolean leftSide,int mode){
		if(!leftSide){
			if(mode==CURVE || mode==CURVE2 || mode==CURVE3 || mode==CURVE4 ){
				return true;
			}
			
			
		}else{
			if(mode==RCURVE || mode==RCURVE2 || mode==RCURVE3 || mode==RCURVE4 ){
				return true;
			}
		}
		return false;
	}
	
	private static void strokePath(Context2d context,int mode,double sx,double sy,double ex,double ey,boolean leftSide,boolean fill){
		double w=ex-sx;
		
		
		/*
		 * without start from 0 made gap between startAt
		 */
			if(isRightSideCurve(leftSide,mode)){
				if(fill){
				context.moveTo(ex, 0);
				context.lineTo(ex, sy);
				}else{
					context.moveTo(ex, sy);
				}
			}else{
				if(fill){
				context.moveTo(sx, 0);
				context.lineTo(sx, sy);
				}else{
					context.moveTo(sx, sy);
				}
			}
			
	
		
		//double h=ey-sy;
		if(mode==STRAIGHT){
		
			context.lineTo(sx+w/2, ey);
			context.lineTo(ex, sy);
			
		}else if(mode==OVAL){
			
			context.quadraticCurveTo(sx, ey, sx+w/2,ey);
			context.quadraticCurveTo(ex, ey, ex,sy);
			
		}else if(mode==SHARP){
			
			context.quadraticCurveTo(sx+w/8*3, ey, sx+w/2,ey);
			context.quadraticCurveTo(sx+w/8*5, ey, ex,sy);
			
		}else if(mode==SOFT){
			
			context.quadraticCurveTo(sx+w/4, ey, sx+w/2,ey);
			context.quadraticCurveTo(sx+w/4*3, ey, ex,sy);
			
		}else if(mode==TRAPEZOID || mode==TRAPEZOID2 || mode==TRAPEZOID3 ){
			double sp=w/8;
			if(mode==TRAPEZOID2){
				sp=w/16;
			}else if(mode==TRAPEZOID3){
				sp=w/32;
			}
			
			context.lineTo(sx+sp, ey);
			context.lineTo(ex-sp, ey);
			context.lineTo(ex, sy);
			
		}else if(mode==TRIANGLE){
			
			context.lineTo(sx, ey);
			context.lineTo(ex, sy);
			
		}else if(mode==TRIANGLE2){
			
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
			
			context.lineTo(sx+sp, sy+h/2);
			
			context.quadraticCurveTo(sx+w/8*3, ey, sx+w/2,ey);
			context.quadraticCurveTo(sx+w/8*5, ey, ex-sp,sy+h/2);
			
			//context.lineTo(sx+w/2, ey); //straight
			
			//context.lineTo(ex-sp, sy+h/2);
			
			context.lineTo(ex, sy);
			
		}else if(mode==CURVE){
			if(leftSide){
				
				context.quadraticCurveTo(sx, ey, ex+w/4,ey);
				context.quadraticCurveTo(sx+w/4*3, ey, ex,sy);
			}else{
				
				context.quadraticCurveTo(ex, ey, sx-w/4,ey);
				context.quadraticCurveTo(ex-w/4*3, ey, sx,sy);
			}
			
			
		}else if(mode==CURVE2){
			if(leftSide){
				
				context.quadraticCurveTo(sx, ey, ex+w/4,ey);
				context.quadraticCurveTo(sx+w/4, ey, ex,sy);
			}else{
				
				context.quadraticCurveTo(ex, ey, sx-w/4,ey);
				context.quadraticCurveTo(ex-w/4, ey, sx,sy);
			}
			
			
		}else if(mode==CURVE3){
			if(leftSide){
				
				context.quadraticCurveTo(sx, ey, ex+w,ey);
				context.quadraticCurveTo(sx+w, ey, ex,sy);
			}else{
				
				context.quadraticCurveTo(ex, ey, sx-w,ey);
				context.quadraticCurveTo(ex-w, ey, sx,sy);
			}
			
			
		}else if(mode==CURVE4){
			if(leftSide){
				
				context.quadraticCurveTo(sx, ey, ex+w*2,ey);
				context.quadraticCurveTo(sx+w, ey, ex,sy);
			}else{
				
				context.quadraticCurveTo(ex, ey, sx-w*2,ey);
				context.quadraticCurveTo(ex-w, ey, sx,sy);
			}
			
			
		}else if(mode==RCURVE){
			if(!leftSide){
				
				context.quadraticCurveTo(sx, ey, ex+w/4,ey);
				context.quadraticCurveTo(sx+w/4*3, ey, ex,sy);
			}else{
				
				context.quadraticCurveTo(ex, ey, sx-w/4,ey);
				context.quadraticCurveTo(ex-w/4*3, ey, sx,sy);
			}
			
		}else if(mode==RCURVE2){
			if(!leftSide){
				
				context.quadraticCurveTo(sx, ey, ex+w/4,ey);
				context.quadraticCurveTo(sx+w/4, ey, ex,sy);
			}else{
				
				context.quadraticCurveTo(ex, ey, sx-w/4,ey);
				context.quadraticCurveTo(ex-w/4, ey, sx,sy);
			}
			
			
		}else if(mode==RCURVE3){
			if(!leftSide){
				
				context.quadraticCurveTo(sx, ey, ex+w,ey);
				context.quadraticCurveTo(sx+w, ey, ex,sy);
			}else{
				
				context.quadraticCurveTo(ex, ey, sx-w,ey);
				context.quadraticCurveTo(ex-w, ey, sx,sy);
			}
			
			
		}else if(mode==RCURVE4){
			if(!leftSide){
				
				context.quadraticCurveTo(sx, ey, ex+w*2,ey);
				context.quadraticCurveTo(sx+w, ey, ex,sy);
			}else{
				
				context.quadraticCurveTo(ex, ey, sx-w*2,ey);
				context.quadraticCurveTo(ex-w, ey, sx,sy);
			}
		}
		if(isRightSideCurve(leftSide,mode)){
			if(fill){
			context.lineTo(sx, 0);
			}
		}else{
			if(fill){
				context.lineTo(ex, 0);
				}
		}
		//context.closePath();
	}
}
