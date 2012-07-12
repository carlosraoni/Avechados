package com.car.ai;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.car.ai.WallSensorRayCast.WallSensorType;
import com.car.model.Car;
import com.car.utils.Constants;
import com.car.utils.Controls;

public class WallSensorIntelligence implements CarIntelligenceInterface {

	/* (non-Javadoc)
	 * @see com.car.ai.CarIntelligenceInterface#getCarNextControls(com.car.model.Car)
	 */
	@Override
	public BitSet getCarNextControls(Car opponent) {

		BitSet controls = new BitSet();		
		
		/*WayPointSensor waypointSensor = opponent.getWaypointSensor();
		
		System.out.println("Waypoint sensor value:" + waypointSensor.getValue());*/
		
		List<WallSensorRayCast> sensors = opponent.getWallSensors();
		Map<WallSensorType, WallSensorRayCast> sensorsMap = new HashMap<WallSensorRayCast.WallSensorType, WallSensorRayCast>();
		int sensorAtivacted = 0;
		for(WallSensorRayCast sensor : sensors){
			if(sensor.getValue() != Constants.WALL_SENSOR_CLEAR_VALUE){
				sensorAtivacted++;
			}
			sensorsMap.put(sensor.getType(),sensor);
		}
		
		if(!sensorsMap.get(WallSensorType.FRONT).isActivated()){
			controls.set(Controls.TDC_UP.ordinal(), true);
		}if(sensorAtivacted == 1
				&&sensorsMap.get(WallSensorType.FRONT).isActivated()){
			controls.set(Controls.TDC_UP.ordinal(), true);
			Random random = new Random();
			if(random.nextBoolean()){
				controls.set(Controls.TDC_LEFT.ordinal(), true);
			}else{
				controls.set(Controls.TDC_RIGHT.ordinal(), false);	
			}
			
		}else if(sensorAtivacted == 2 
				&& sensorsMap.get(WallSensorType.FRONT).isActivated()
				&& sensorsMap.get(WallSensorType.FRONT_LEFT).isActivated()){
			controls.set(Controls.TDC_UP.ordinal(), true);
			controls.set(Controls.TDC_RIGHT.ordinal(), true);
		} else if(sensorAtivacted == 3
				&& sensorsMap.get(WallSensorType.FRONT).isActivated()
				&& sensorsMap.get(WallSensorType.FRONT_LEFT).isActivated()
				&& sensorsMap.get(WallSensorType.LEFT).isActivated()){
			controls.set(Controls.TDC_UP.ordinal(), true);
			controls.set(Controls.TDC_RIGHT.ordinal(), true);			
		}else if(sensorAtivacted == 2 
				&& sensorsMap.get(WallSensorType.FRONT).isActivated()
				&& sensorsMap.get(WallSensorType.FRONT_RIGHT).isActivated()){
			controls.set(Controls.TDC_UP.ordinal(), true);
			controls.set(Controls.TDC_LEFT.ordinal(), true);
		} else if(sensorAtivacted == 3
				&& sensorsMap.get(WallSensorType.FRONT).isActivated()
				&& sensorsMap.get(WallSensorType.FRONT_RIGHT).isActivated()
				&& sensorsMap.get(WallSensorType.RIGHT).isActivated()){
			controls.set(Controls.TDC_UP.ordinal(), true);
			controls.set(Controls.TDC_LEFT.ordinal(), true);			
		} else if(sensorAtivacted == 4
				&& sensorsMap.get(WallSensorType.FRONT).isActivated()
				&& sensorsMap.get(WallSensorType.FRONT_LEFT).isActivated()
				&& sensorsMap.get(WallSensorType.LEFT).isActivated()
				&& sensorsMap.get(WallSensorType.FRONT_RIGHT).isActivated()){
			controls.set(Controls.TDC_UP.ordinal(), true);
			controls.set(Controls.TDC_RIGHT.ordinal(), true);
		} else if(sensorAtivacted == 4
				&& sensorsMap.get(WallSensorType.FRONT).isActivated()
				&& sensorsMap.get(WallSensorType.FRONT_LEFT).isActivated()
				&& sensorsMap.get(WallSensorType.RIGHT).isActivated()
				&& sensorsMap.get(WallSensorType.FRONT_RIGHT).isActivated()){
			controls.set(Controls.TDC_UP.ordinal(), true);
			controls.set(Controls.TDC_LEFT.ordinal(), true);
		}else if(sensorAtivacted == 3
				&& sensorsMap.get(WallSensorType.FRONT).isActivated()
				&& sensorsMap.get(WallSensorType.FRONT_LEFT).isActivated()
				&& sensorsMap.get(WallSensorType.FRONT_RIGHT).isActivated()){
			controls.set(Controls.TDC_DOWN.ordinal(), true);
			Random random = new Random();
			if(random.nextBoolean()){
				controls.set(Controls.TDC_LEFT.ordinal(), true);
			}else{
				controls.set(Controls.TDC_RIGHT.ordinal(), false);	
			}
		}else if(sensorAtivacted == 5
				&& sensorsMap.get(WallSensorType.FRONT).isActivated()
				&& sensorsMap.get(WallSensorType.FRONT_LEFT).isActivated()
				&& sensorsMap.get(WallSensorType.FRONT_RIGHT).isActivated()
				&& sensorsMap.get(WallSensorType.LEFT).isActivated()
				&& sensorsMap.get(WallSensorType.RIGHT).isActivated()){
			System.out.println("All activated");
			controls.set(Controls.TDC_DOWN.ordinal(), true);
			Random random = new Random();
			if(random.nextBoolean()){
				controls.set(Controls.TDC_LEFT.ordinal(), true);
			}else{
				controls.set(Controls.TDC_RIGHT.ordinal(), false);	
			}
			
		}else {
			controls.set(Controls.TDC_DOWN.ordinal(), true);
		}
		return controls;
	}

}
