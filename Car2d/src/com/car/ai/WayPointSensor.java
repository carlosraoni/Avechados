package com.car.ai;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.car.model.Car;

public class WayPointSensor {

	private float value;
	private float wayPointRange;
	private List<Vector2> waypoints;
	private Car car;
	
	int index = 0;
	private float angle;
	
	public WayPointSensor(World world, Car car, WayPointsLine wayPointsLine, float wayPointRange) {
		this.waypoints = wayPointsLine.getWayPoints();
		this.wayPointRange = wayPointRange;
		this.car = car;
	}

	public float getValue() {
		return value;
	}

	public void update() {
		Vector2 waypoint = getCurrentWayPoint();
		Vector2 v =  car.getBody().getLocalPoint(waypoint);
		this.value  = v.x;
		this.angle  = v.angle();
//		this.value = normalizeAngle(v.angle());
//		
//		System.out.println("Tipo Carro :" +car.getType());
//		if(car.getType().equals(Car.CarType.PLAYER)){
//			System.out.println("Waypoint " + v + " :: Angle " + this.value);
//		}		
	}

	private Vector2 getCurrentWayPoint() {
		Vector2 waypoint = waypoints.get(index);
		Vector2 v =  car.getBody().getLocalPoint(waypoint);
		
		if(v.len() < wayPointRange){
			index = (index + 1) % waypoints.size();
		}
		
		return waypoints.get(index);
	}
	
	//Entre +180 e -180
	private float normalizeAngle(float angle){
		float normalizeAngle;
		if(angle >= 270 && angle <= 360){
			normalizeAngle = angle-450;			
		}else{
			normalizeAngle = angle-90;
		}
		return normalizeAngle;
	}
	
	@Override
	public String toString() {
		return "WayPointSensor [ xvalue=" + Float.toString(value) + ", normalizedAngle="+normalizeAngle(angle)+", angle="+angle+"]";
	}

	public float getAngle() {
		return normalizeAngle(this.angle);
	}

}
