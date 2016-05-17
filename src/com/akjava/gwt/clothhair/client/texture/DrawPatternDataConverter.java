package com.akjava.gwt.clothhair.client.texture;

import java.util.List;

import com.akjava.gwt.clothhair.client.texture.HairPatternData.DrawPatternData;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class DrawPatternDataConverter extends Converter<DrawPatternData,String>{

	@Override
	protected String doForward(DrawPatternData data) {
		List<String> list=Lists.newArrayList();
		if(data!=null){
		list.add(String.valueOf(data.getMode()));
		list.add(String.valueOf(data.getSplitVertical()));
		list.add(String.valueOf(data.getStartVertical()));
		list.add(String.valueOf(data.getEndVertical()));
		}
		return Joiner.on("|").join(list);
	}

	@Override
	protected DrawPatternData doBackward(String text) {
		if(text==null){
			text="";
		}
		DrawPatternData data=new DrawPatternData();
		List<String> list=Splitter.on("|").splitToList(text);
		if(list.size()>0){//maybe empty case call this
			data.setMode(ValuesUtils.toInt(list.get(0), 0));
		}
		if(list.size()>1){
			data.setSplitVertical(ValuesUtils.toInt(list.get(1), 0));
		}
		if(list.size()>2){
			data.setStartVertical(ValuesUtils.toInt(list.get(2), 0));
		}
		if(list.size()>3){
			data.setEndVertical(ValuesUtils.toInt(list.get(3), 0));
		}
		
		return data;
	}

}
