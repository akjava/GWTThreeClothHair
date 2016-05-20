package com.akjava.gwt.clothhair.client.texture;

import java.util.List;

import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;

public class HairTextureDataConverter extends Converter<HairTextureData,String>{
	private HairPatternDataConverter converter=new HairPatternDataConverter();
	@Override
	protected String doForward(HairTextureData data) {
		List<String> list=Lists.newArrayList();
		if(data!=null){
			list.add(converter.convert(data.getHairPatternData()));
			list.add(String.valueOf(data.getColor()));
			list.add(String.valueOf(data.getOpacity()));
			list.add(String.valueOf(data.getAlphaTest()));
			list.add(String.valueOf(data.isEnablePatternImage()));
			list.add(String.valueOf(data.isUseLocalColor()));
		}
		return Joiner.on(":").join(list);
	}

	@Override
	protected HairTextureData doBackward(String text) {
		if(text==null){
			text="";
		}
		HairTextureData data=new HairTextureData();
		List<String> list=Splitter.on(":").splitToList(text);
		if(list.size()>0){//maybe empty case call this
			data.setHairPatternData(converter.reverse().convert(list.get(0)));
		}
		if(list.size()>1){
			data.setColor(ValuesUtils.toInt(list.get(1), HairTextureData.INITIAL_COLOR));
		}
		if(list.size()>2){
			data.setOpacity(ValuesUtils.toDouble(list.get(2), HairTextureData.INITIAL_OPACITY));
		}
		if(list.size()>3){
			data.setAlphaTest(ValuesUtils.toDouble(list.get(3),HairTextureData.INITIAL_ALPHATEST));
		}
		if(list.size()>4){
			data.setEnablePatternImage(ValuesUtils.toBoolean(list.get(4),false));
		}
		if(list.size()>5){
			data.setUseLocalColor(ValuesUtils.toBoolean(list.get(5),false));
		}
		
		
		return data;
	}
}
