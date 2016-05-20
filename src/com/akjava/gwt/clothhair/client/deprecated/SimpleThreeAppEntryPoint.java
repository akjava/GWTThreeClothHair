package com.akjava.gwt.clothhair.client.deprecated;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.gwt.three.client.gwt.renderers.WebGLRendererParameter;
import com.akjava.gwt.three.client.java.utils.GWTThreeUtils;
import com.akjava.gwt.three.client.js.THREE;
import com.akjava.gwt.three.client.js.cameras.PerspectiveCamera;
import com.akjava.gwt.three.client.js.renderers.WebGLRenderer;
import com.akjava.gwt.three.client.js.scenes.Scene;
import com.google.gwt.core.client.EntryPoint;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.core.client.Scheduler.ScheduledCommand;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.logical.shared.ResizeEvent;
import com.google.gwt.event.logical.shared.ResizeHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Window;
import com.google.gwt.user.client.ui.DockLayoutPanel;
import com.google.gwt.user.client.ui.FocusPanel;
import com.google.gwt.user.client.ui.RootLayoutPanel;
import com.google.gwt.user.client.ui.RootPanel;

public abstract class SimpleThreeAppEntryPoint extends AbstractThreeApp implements EntryPoint{

	protected WebGLRenderer renderer;
	protected Scene scene;
	protected PerspectiveCamera camera;
	
	protected double SCREEN_WIDTH;
	protected double SCREEN_HEIGHT;
	//protected FocusPanel 
	protected FocusPanel rendererContainer;

	public void onModuleLoad() {
		final DockLayoutPanel root=new DockLayoutPanel(Unit.PX);
		
		RootLayoutPanel.get().add(root);
		
		RootPanel.get().addDomHandler(new KeyDownHandler() {
			@Override
			public void onKeyDown(KeyDownEvent event) {
				onKeyDownEvent(event);
				
			}
		}, KeyDownEvent.getType());
		
		
		
		onBeforeStartApp();
		Scheduler.get().scheduleDeferred(new ScheduledCommand() {	
			@Override
			public void execute() {
				start(root);
				onAfterStartApp();
			}
		});
	}
	
	public void onKeyDownEvent(KeyDownEvent event){
		
	}
	
	public abstract WebGLRendererParameter createRendererParameter();
	public abstract void onInitializedThree();
	public abstract void onBeforeStartApp();
	public abstract void onAfterStartApp();
	
	//overwrite what ever you need;
	public PerspectiveCamera createCamera(){
		PerspectiveCamera camera=THREE.PerspectiveCamera(45, getWindowInnerWidth()/getWindowInnerHeight(), 0.5, 300000);
		camera.getPosition().set(0, 0, 400);
		return camera;
	}
	
	@Override
	public void animate(double timestamp) {
		// TODO Auto-generated method stub
		renderer.render(scene, camera);
	}

	@Override
	public void init() {
		
		
		SCREEN_WIDTH = getWindowInnerWidth();
		SCREEN_HEIGHT = getWindowInnerHeight();
		 
		
		
		renderer = THREE.WebGLRenderer(createRendererParameter());
		renderer.setPixelRatio( GWTThreeUtils.getWindowDevicePixelRatio() );
		renderer.setSize( SCREEN_WIDTH, SCREEN_HEIGHT );
		
		rendererContainer = createContainerPanel();
		getParent().add(rendererContainer);
		rendererContainer.getElement().appendChild( renderer.getDomElement() );

		// scene
		scene = THREE.Scene();
	
		// camera
		camera = createCamera();
		
		addResizeHandler();
		
		onInitializedThree();
	}
	protected HandlerRegistration resizeHandler;
	
	protected void addResizeHandler(){
		
		//popup.show();
		//moveToAroundRightTop(popup);
		
		
		resizeHandler = Window.addResizeHandler(new ResizeHandler() {
			@Override
			public void onResize(ResizeEvent event) {
				onWindowResize();
			}
		});
		
		
	}

	@Override
	public void onWindowResize() {
		SCREEN_WIDTH = getWindowInnerWidth();
		SCREEN_HEIGHT = getWindowInnerHeight();
	
		//re read because of double
		camera.setAspect(SCREEN_WIDTH / SCREEN_HEIGHT);
		camera.updateProjectionMatrix();

		renderer.setSize( SCREEN_WIDTH , SCREEN_HEIGHT );
	}
}
