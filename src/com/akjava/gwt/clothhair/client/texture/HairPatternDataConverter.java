package com.akjava.gwt.clothhair.client.texture;

import java.util.List;

import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class HairPatternDataConverter extends Converter<HairPatternData, String>{
	DrawPatternDataConverter patternDataConverter=new DrawPatternDataConverter();
	@Override
	protected String doForward(HairPatternData data) {
		List<String> list=Lists.newArrayList();
		if(data!=null){
		list.add(String.valueOf(data.getSlices()));
		list.add(String.valueOf(data.getLrMode()));
		list.add(String.valueOf(data.isUseCenter()));
		list.add(String.valueOf(data.isEvenCenter()));
		list.add(String.valueOf(data.getExtendCenter()));
		list.add(String.valueOf(data.isStroke()));
		list.add(String.valueOf(data.getStrokeGrayscale()));
		
		if(data.getDefaultPatternData()!=null){
			list.add(patternDataConverter.convert(data.getDefaultPatternData()));
		}
		if(data.getCenterPatternData()!=null){
			list.add(patternDataConverter.convert(data.getCenterPatternData()));
		}
		
		
		}
		return Joiner.on(";").join(list);
	}

	@Override
	protected HairPatternData doBackward(String text) {
		HairPatternData data=new HairPatternData();
		
		List<String> list=Splitter.on(";").splitToList(text);
		if(list.size()>0){//maybe empty case call this
			data.setSlices(ValuesUtils.toInt(list.get(0), 1));
		}
		if(list.size()>1){
			data.setLrMode(ValuesUtils.toInt(list.get(1), 0));
		}
		if(list.size()>2){
			data.setUseCenter(ValuesUtils.toBoolean(list.get(2), false));
		}
		if(list.size()>3){
			data.setEvenCenter(ValuesUtils.toBoolean(list.get(3), false));
		}
		if(list.size()>4){
			data.setExtendCenter(ValuesUtils.toInt(list.get(4), 0));
		}
		if(list.size()>5){
			data.setStroke(ValuesUtils.toBoolean(list.get(5), false));
		}
		if(list.size()>6){
			data.setStrokeGrayscale(ValuesUtils.toInt(list.get(6), 0));
		}
		
		if(list.size()>7){
			data.setDefaultPatternData(patternDataConverter.reverse().convert(list.get(7)));
		}
		
		if(list.size()>8){
			data.setCenterPatternData(patternDataConverter.reverse().convert(list.get(8)));
		}
		
		return data;
	}
	
	

}
