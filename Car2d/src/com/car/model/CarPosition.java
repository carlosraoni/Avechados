package com.car.model;

public class CarPosition {

	private int position;
	private float x, y;
	private float angle;
	
	public CarPosition(int position, float x, float y, float angle) {		
		this.position = position;
		this.x = x;
		this.y = y;
		this.angle = angle;
	}

	public int getPosition() {
		return position;
	}

	public float getAngle() {
		return angle;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}
		
	
}
