package com.car.ai;

import com.badlogic.gdx.math.Vector2;

public class Inputs {

	private Vector2 velocity;
	private float waypointSensor;
	private WallSensor[] wallSensors;
	
	public Inputs(Vector2 velocity, WallSensor[] wallSensors, float waypointSensor) {
		this.velocity = velocity;
		this.wallSensors= wallSensors; 
		print();
	}
	public void print(){
		System.out.println("Speed: " + this.velocity.len());
	}

}
