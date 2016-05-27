package com.akjava.gwt.clothhair.client.hair;

import com.akjava.gwt.clothhair.client.hair.HairData.HairPin;
import com.google.common.base.Predicate;

public class HairPinPredicates {
private HairPinPredicates(){}
	public static NoTargetOnly NoTargetOnly(){
		return NoTargetOnly.INSTANCE;
	}
	public enum  NoTargetOnly implements Predicate<HairPin>{
		INSTANCE;

		@Override
		public boolean apply(HairPin input) {
			return input.getTargetClothIndex()==-1;
		}
		
	}
}
