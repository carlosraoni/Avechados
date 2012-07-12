package com.car.ai;

import java.util.BitSet;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.sourceforge.jFuzzyLogic.FIS;

import com.car.ai.WallSensorRayCast.WallSensorType;
import com.car.model.Car;
import com.car.utils.Controls;

public class FuzzyInntelligence implements CarIntelligenceInterface {

	private final BitSet controls = new BitSet();
	@Override
	public BitSet getCarNextControls(Car opponent) {

		
		controls.clear();
		
		String fileName = "res/fuzzy/car.fcl";
        FIS fis = FIS.load(fileName,true);

        if( fis == null ) { 
            System.err.println("Can't load file: '" + fileName + "'");
            return null;
        }
        
        List<WallSensorRayCast> sensors = opponent.getWallSensors();
		Map<WallSensorType, WallSensorRayCast> sensorsMap = new HashMap<WallSensorRayCast.WallSensorType, WallSensorRayCast>();
		for(WallSensorRayCast sensor : sensors){
			sensorsMap.put(sensor.getType(),sensor);
		}
		
		opponent.printSensors();
		
        // Set inputs
        fis.setVariable("wsfront", sensorsMap.get(WallSensorRayCast.WallSensorType.FRONT).getValue());
        fis.setVariable("wsfrontleft", sensorsMap.get(WallSensorRayCast.WallSensorType.FRONT_LEFT).getValue());
        fis.setVariable("wsfrontright", sensorsMap.get(WallSensorRayCast.WallSensorType.FRONT_RIGHT).getValue());
        fis.setVariable("wsleft", sensorsMap.get(WallSensorRayCast.WallSensorType.LEFT).getValue());
        fis.setVariable("wsright", sensorsMap.get(WallSensorRayCast.WallSensorType.RIGHT).getValue());
        fis.setVariable("waypointsensorX", opponent.getWaypointSensor().getValue());
        int signal;
        float y = opponent.getBody().getLocalVector(opponent.getBody().getLinearVelocity()).y;
        if(y > 1){
        	signal = 1;
        }else if(y < 1){
        	signal = -1;
        }else{
        	signal = 0;
        }
        /*if(y < 0)
        	System.exit(1);*/
        fis.setVariable("speedSignal", signal);
        float speed = opponent.getBody().getLocalVector(opponent.getBody().getLinearVelocity()).len()*100/69;
        fis.setVariable("speed", speed);
        System.out.println("Speed : " + speed);
        System.out.println("SpeedSignal : " + signal + ":: Y = " + y);


        // Evaluate
        fis.evaluate();
        
        fis.getVariable("changespeed").chartDefuzzifier(false);
        double changeSpeed = fis.getVariable("changespeed").getLatestDefuzzifiedValue();
        double direction = fis.getVariable("direction").getLatestDefuzzifiedValue();
        System.out.println("ChangeOfSpeed = " + changeSpeed + " :: Direction = " + direction);
        
		controls.set(Controls.TDC_UP.ordinal(), changeSpeed>5);
		controls.set(Controls.TDC_DOWN.ordinal(), changeSpeed<5);
		
		if(direction > -2  && direction <0){
			if(changeSpeed < 5){
				controls.set(Controls.TDC_DOWN.ordinal(), true);
			}
			System.out.println("going back");
		}else if(direction > 0  && direction <2){
			controls.set(Controls.TDC_LEFT.ordinal(), true);
			System.out.println("going left");
		}else if(direction > 2  && direction <4){
			controls.set(Controls.TDC_LEFT.ordinal(), true);
			if(changeSpeed > 5){
				controls.set(Controls.TDC_UP.ordinal(), true);
			}
			System.out.println("going front left");
		}else if(direction > 4  && direction <6){
			if(changeSpeed > 5){
				controls.set(Controls.TDC_UP.ordinal(), true);
			}
			System.out.println("going equal");
		}else if(direction > 6  && direction <8){
			if(changeSpeed > 5){
				controls.set(Controls.TDC_UP.ordinal(), true);
			}
			controls.set(Controls.TDC_RIGHT.ordinal(), true);
			
			System.out.println("going front right");
		}else if(direction > 8  && direction <10){
			controls.set(Controls.TDC_RIGHT.ordinal(), true);
			System.out.println(" going right");
		}
		return controls;
	}
public static void main(String[] args) {
	String fileName = "res/fuzzy/car.fcl";
    FIS fis = FIS.load(fileName,true);

    if( fis == null ) { 
        System.err.println("Can't load file: '" + fileName + "'");
        return ;
    }
    fis.chart();
    
}
}
