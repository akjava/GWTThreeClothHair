package com.akjava.gwt.clothhair.client;

import java.util.Arrays;
import java.util.List;

import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.lib.common.utils.ValuesUtils;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.collect.Lists;
import com.google.common.primitives.Doubles;

public class TmpSphereInfoConverter extends Converter<double[],String>{

	public TmpSphereInfoConverter() {
		
	}

	@Override
	protected String doForward(double[] value) {
		return Joiner.on(",").join(Doubles.asList(value));
	}

	@Override
	protected double[] doBackward(String value) {
		//never null
		
		List<String> v=Lists.newArrayList(Splitter.on(",").split(value));
		double[] values=new double[v.size()];
		for(int i=0;i<v.size();i++){
			values[i]=ValuesUtils.toDouble(v.get(i), 0);
		}
		return values;
		
	}

}
