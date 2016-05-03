package com.akjava.gwt.clothhair.client;

import com.akjava.gwt.clothhair.client.deprecated.ThreeAppEntryPointWithControler;
import com.akjava.gwt.three.client.gwt.GWTParamUtils;
import com.akjava.gwt.three.client.gwt.renderers.WebGLRendererParameter;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.core.Clock;

public class HalfSizeThreeAppWithControler extends ThreeAppEntryPointWithControler{

	@Override
	public WebGLRendererParameter createRendererParameter() {
		return GWTParamUtils.WebGLRenderer().preserveDrawingBuffer(true);//for snapshot
	}

	protected int windowHalfX;
	protected int windowHalfY;
	protected Clock clock;
	
	
	@Override
	public void onInitializedThree() {
		clock=THREE.Clock();
		
		//INSTANCE=this;
		
		//LogUtils.log("onInitializedThree");
		//renderer.setClearColor(0xffffff);//default is black?
		
		windowHalfX= (int)(SCREEN_WIDTH/2);
		windowHalfY= (int)(SCREEN_HEIGHT/2);
		 
		 
		 //renderer.setGammaOutput(true);//for blender color,however maybe make color problem  with gimp-created texture
		
		 
		 /*
		rendererContainer.addMouseMoveHandler(new MouseMoveHandler() {
			@Override
			public void onMouseMove(MouseMoveEvent event) {
				
			}
		});
		rendererContainer.addMouseWheelHandler(new MouseWheelHandler() {
			
			@Override
			public void onMouseWheel(MouseWheelEvent event) {
				
			}
		});
		*/
		
	}


	@Override
	public void onBeforeStartApp() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onAfterStartApp() {
		// TODO Auto-generated method stub
		
	}
	
	@Override
	public void onWindowResize() {
		super.onWindowResize();
		 windowHalfX= (int)(SCREEN_WIDTH/2);
		 windowHalfY= (int)(SCREEN_HEIGHT/2);
	}

}
