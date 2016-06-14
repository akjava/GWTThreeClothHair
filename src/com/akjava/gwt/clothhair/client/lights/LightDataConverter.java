package com.akjava.gwt.clothhair.client.lights;

import java.util.List;

import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

/*
 * actually there are json.so when support more complex light,swith it
 */
public class LightDataConverter extends Converter<LightData,String>{

	@Override
	protected String doForward(LightData data) {
		List<String> list=Lists.newArrayList();
		if(data!=null){
			list.add(data.getName());
			list.add(String.valueOf(data.getType()));
			list.add(String.valueOf(data.getColor()));
			list.add(String.valueOf(data.getIntensity()));
			list.add(String.valueOf(data.getColor2()));
			if(data.getPosition()!=null){
				list.add(data.getPosition().getX()+":"+data.getPosition().getY()+":"+data.getPosition().getZ());
			}
			list.add(String.valueOf(data.isCastShadow()));
		}
		return Joiner.on(",").join(list);
	}

	@Override
	protected LightData doBackward(String line) {
		if(Strings.isNullOrEmpty(line)){
			return null;
		}
		LightData data=new LightData();
		List<String> list=Splitter.on(",").splitToList(line);
		data.setName(list.get(0));
		
		if(list.size()>1){
			data.setType(ValuesUtils.toInt(list.get(1), 0));
		}
		if(list.size()>2){
			data.setColor(ValuesUtils.toInt(list.get(2), 0));
		}
		if(list.size()>3){
			data.setIntensity(ValuesUtils.toDouble(list.get(3), 0));
		}
		if(list.size()>4){
			data.setColor2(ValuesUtils.toInt(list.get(4), 0));
		}
		if(list.size()>5){
			String[] pos=list.get(5).split(":");
			if(pos.length>2){
				data.getPosition().set(ValuesUtils.toDouble(pos[0], 0), ValuesUtils.toDouble(pos[1], 0), ValuesUtils.toDouble(pos[2], 0));
			}
		}
		if(list.size()>6){
			data.setCastShadow(ValuesUtils.toBoolean(list.get(6),false));
		}
		
		return data;
	}

}
