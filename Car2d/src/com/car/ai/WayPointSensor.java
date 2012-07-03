package com.car.ai;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.car.model.Car;

public class WayPointSensor {

	private float value;
	private List<Vector2> waypoints;
	private Car car;
	
	int index = 0;
	
	public WayPointSensor(World world, Car car, WayPointsLine wayPointsLine) {
		this.waypoints = wayPointsLine.getWayPoints();
		this.car = car;
	}

	public float getValue() {
		Vector2 waypoint = waypoints.get(index);
		Vector2 v =  car.getBody().getLocalPoint(waypoint);
		System.out.println("Tipo Carro :" +car.getType());
		if(car.getType().equals(Car.CarType.PLAYER)){
			System.out.println("Waypoint " + v + " :: Angle "+ normalizeAngle(v.angle()));
		}
		
		return value;
	}

	public void update() {
		
	}
	
	//Entre +180 e -180
	private float normalizeAngle(float angle){
		float normalizeAngle;
		if(angle>=0 && angle<=90){
			normalizeAngle = 90-angle;
		}else if(angle >= 270 && angle <= 360){
			normalizeAngle = angle-450;			
		}else{
			normalizeAngle = angle-90;
		}
		return normalizeAngle;
	}

}
