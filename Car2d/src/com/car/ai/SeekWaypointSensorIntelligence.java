package com.car.ai;

import java.util.BitSet;

import com.car.ai.WallSensorRayCast.WallSensorType;
import com.car.model.Car;
import com.car.utils.Controls;

public class SeekWaypointSensorIntelligence implements CarIntelligenceInterface{

	private int countBacking = 0;
	
	@Override
	public BitSet getCarNextControls(Car opponent) {
		BitSet controls = new BitSet();
		float value = opponent.getWaypointSensor().getValue();
		
		if(countBacking > 0 || opponent.getWallSensors().get(WallSensorType.FRONT.ordinal()).getValue() < 0.001){
			countBacking ++;
			controls.set(Controls.TDC_DOWN.ordinal(), true);
			value = -value;
			if(countBacking > 10){
				countBacking = 0;
			}
			
		}else{
			controls.set(Controls.TDC_UP.ordinal(), true);
		}
			

		
		if(Math.abs(value) < 10f && countBacking == 0){
			return controls;
		}
		if(value > 0.0){
			controls.set(Controls.TDC_RIGHT.ordinal(), true);
		}
		else{
			controls.set(Controls.TDC_LEFT.ordinal(), true);
		}
//		System.out.println("CAR ID [" + opponent.getId() + "] controls = [" + controls.toString() + "]");

		return controls;
	}

}
