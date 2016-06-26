package com.akjava.gwt.clothhair.client.sphere;

import java.util.List;

import com.akjava.gwt.three.client.java.ThreeLog;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;

public class SphereDataConverter extends Converter<SphereData,String>{

	public SphereDataConverter() {
		
	}

	@Override
	protected String doForward(SphereData value) {
		return Joiner.on(",").join(Doubles.asList(new double[]{value.getX(),value.getY(),value.getZ(),value.getSize()
		,value.isEnabled()?1.0:0,value.getBoneIndex(),value.getChannel()		,value.isCopyHorizontal()?1.0:0
				,value.getType(),
				value.getRotate().getX(),value.getRotate().getY(),value.getRotate().getZ(),value.getRotate().getW()
		}));
	}

	@Override
	protected SphereData doBackward(String value) {
		//never null
		
		List<String> v=Lists.newArrayList(Splitter.on(",").split(value));
		double[] values=new double[v.size()];
		for(int i=0;i<v.size();i++){
			values[i]=ValuesUtils.toDouble(v.get(i), 0);
		}
		
		int boneIndex=0;
		if(values.length>5){
			boneIndex=(int)values[5];
		}
		
		SphereData data=new SphereData(values[0],values[1],values[2],values[3],
				values[4]==1?true:false,boneIndex
				);
		
		if(values.length>6){
			int channel=(int)values[6];
			data.setChannel(channel);
		}
		if(values.length>7){
			boolean copy=values[7]==1?true:false;
			data.setCopyHorizontal(copy);
		}
		
		if(values.length>8){
			data.setType((int)values[8]);
		}
		
		if(values.length>12){
			double x=values[9];
			double y=values[10];
			double z=values[11];
			double w=values[12];
			
			data.getRotate().set(x, y, z, w);
			
		}
		
		return data;
	}
	
	public SphereData copy(SphereData data){
		String text=doForward(data);
		return doBackward(text);
	}

}
