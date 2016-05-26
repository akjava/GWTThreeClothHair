package com.akjava.gwt.clothhair.client.hair;

import java.util.Arrays;
import java.util.List;

import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.akjava.gwt.clothhair.client.texture.HairTextureDataConverter;
import com.akjava.gwt.lib.client.LogUtils;
import com.akjava.lib.common.functions.StringToPrimitiveFunctions;
import com.google.common.base.Converter;
import com.google.common.base.Joiner;
import com.google.common.collect.FluentIterable;
import com.google.common.collect.Lists;

public class HairDataConverter extends Converter<HairData,String> {

	private HairTextureDataConverter converter=new HairTextureDataConverter();
	@Override
	protected String doForward(HairData data) {
		List<String> csv=Lists.newArrayList();
		csv.add("1");//0 format version
		
		Joiner joiner=Joiner.on(":");
		List<Integer> faceIndex=Lists.newArrayList();
		List<Integer> vertexIndex=Lists.newArrayList();
		List<Integer> targetIndex=Lists.newArrayList();
		for(int i=0;i<data.getHairPins().size();i++){
			HairPin pin=data.getHairPins().get(i);
			faceIndex.add(pin.getFaceIndex());
			vertexIndex.add(pin.getVertexOfFaceIndex());
			targetIndex.add(pin.getTargetClothIndex());
		}
		csv.add(joiner.join(faceIndex));//1 face index
		csv.add(joiner.join(vertexIndex));//2 vertex index
		
		csv.add(data.getSizeOfU()+":"+data.getSizeOfV());//3 u x v
		
		csv.add(data.isCutU()+":"+data.getStartCutUIndexV());//4 cutU 
		csv.add(data.isDoNarrow()+":"+data.getStartNarrowIndexV()+":"+data.getNarrowScale());//5 do narrow
		
		csv.add(data.getEdgeMode()+":"+data.getEdgeModeScale());//6 edge mode
		
		
		csv.add(String.valueOf(data.getScaleOfU()));//7
		
		csv.add(String.valueOf(data.getChannel()));//8
		
		csv.add(String.valueOf(data.isSyncMove()));//9
		
		csv.add(data.getMass()+":"+data.getDamping());//10
		
		csv.add(converter.convert(data.getHairTextureData()));//11
		
		csv.add(joiner.join(targetIndex));//12 targetIndex
		
		return Joiner.on(",").join(csv);
	}

	@Override
	protected HairData doBackward(String line) {
		String[] csv=line.split(",");
		String version=csv[0];
		
		if(version.equals("1")){
			return parseVersion1(csv);
		}
		LogUtils.log("invalid version:"+version);
		//invalid
		return null;
	}

	private HairData parseVersion1(String[] csv) {
		if(csv.length<8){
			LogUtils.log("parseVersion1:invalid csv:"+Joiner.on(",").join(csv));
		}
		HairData data=new HairData();
		
		//parse Hair Pin
		List<Integer> faceIndex= FluentIterable.from(Arrays.asList(csv[1].split(":"))).transform(StringToPrimitiveFunctions.toInteger()).toList();
		List<Integer> vertexIndex= FluentIterable.from(Arrays.asList(csv[2].split(":"))).transform(StringToPrimitiveFunctions.toInteger()).toList();
		if(faceIndex.size()!=vertexIndex.size()){
			LogUtils.log("parseVersion1:invalid faceIndexSize "+faceIndex.size()+","+vertexIndex.size());
		}
		for(int i=0;i<faceIndex.size();i++){
			HairPin pin=new HairPin(faceIndex.get(i), vertexIndex.get(i));
			data.getHairPins().add(pin);
		}
		
		List<Integer> uvSize= FluentIterable.from(Arrays.asList(csv[3].split(":"))).transform(StringToPrimitiveFunctions.toInteger()).toList();
		if(uvSize.size()<2){
			LogUtils.log("parseVersion1:uvSize "+csv[3]);
		}
		data.setSizeOfU(uvSize.get(0));
		data.setSizeOfV(uvSize.get(1));
		
		String[] cuts=csv[4].split(":");
		data.setCutU(Boolean.valueOf(cuts[0]));
		data.setStartCutUIndexV(Integer.valueOf(cuts[1]));
		
		String[] narrows=csv[5].split(":");
		data.setDoNarrow(Boolean.valueOf(narrows[0]));
		data.setStartNarrowIndexV(Integer.valueOf(narrows[1]));
		data.setNarrowScale(Double.valueOf(narrows[2]));
		
		String[] edges=csv[6].split(":");
		data.setEdgeMode(Integer.valueOf(edges[0]));
		data.setEdgeModeScale(Double.valueOf(edges[1]));
		
		data.setScaleOfU(Double.valueOf(csv[7]));
		
		if(csv.length>8){
			data.setChannel(Integer.valueOf(csv[8]));
		}
		
		if(csv.length>9){
			data.setSyncMove(Boolean.valueOf(csv[9]));
		}
		
		if(csv.length>10){
			String[] mass_damping=csv[10].split(":");
			data.setMass(Double.valueOf(mass_damping[0]));
			data.setDamping(Double.valueOf(mass_damping[1]));
		}
		if(csv.length>11){
			data.setHairTextureData(converter.reverse().convert(csv[11]));
		}
		if(csv.length>12){
			List<Integer> targetIndex= FluentIterable.from(Arrays.asList(csv[12].split(":"))).transform(StringToPrimitiveFunctions.toInteger()).toList();
			if(targetIndex.size()!=data.getHairPins().size()){
				LogUtils.log("invalid target index size");
			}
			for(int i=0;i<data.getHairPins().size();i++){
				data.getHairPins().get(i).setTargetClothIndex(targetIndex.get(i));
			}
		}
		
		return data;
	}

}
