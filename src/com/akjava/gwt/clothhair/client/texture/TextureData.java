package com.akjava.gwt.clothhair.client.texture;


//no need without editor
public class TextureData {
	private boolean useGlobalColor;
	private int color;
	private double opacity;//0-1.0
	private double alphaTest;
	private int shininess=15;
	private int specular=0xffffff;
	
	private PatternData patternData;
	//TODO add texture-name
	
	public static class PatternData{
		private int slice;
		private int stacks;
		private int startStackAt;
		private int mode;
	}
}
