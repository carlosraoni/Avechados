package com.car.ai;

import java.util.BitSet;

import com.car.model.Car;
import com.car.utils.Controls;

public class SeekWaypointSensorIntelligence implements CarIntelligenceInterface{

	@Override
	public BitSet getCarNextControls(Car opponent) {
		BitSet controls = new BitSet();
		float value = opponent.getWaypointSensor().getValue();
		controls.set(Controls.TDC_UP.ordinal(), true);
		if(Math.abs(value) < 15f){
			return controls;
		}
		if(value > 0.0){
			controls.set(Controls.TDC_RIGHT.ordinal(), true);
		}
		else{
			controls.set(Controls.TDC_LEFT.ordinal(), true);
		}
		
		return controls;
	}

}
