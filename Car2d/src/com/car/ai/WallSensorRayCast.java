package com.car.ai;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.RayCastCallback;
import com.badlogic.gdx.physics.box2d.World;
import com.car.model.Car;
import com.car.model.Wall;
import com.car.utils.Constants;

public class WallSensorRayCast {
	
	private World world;
	private Car car;
	private Body body;
	private Fixture fixture;
	
	private Vector2 sensorPointBegin, sensorPointEnd;
	private WallSensorType type;
	
	private float value;
	private float wallSensorRange;
	
	private WallSensorRayCastCallback callback;
	
	public static enum WallSensorType{
		RIGHT(0), FRONT_RIGHT(45), FRONT(90), FRONT_LEFT(135), LEFT(180);
		//FRONT(90);
				
		private WallSensorType(float angle){
			this.angle = angle;
		}
		
		private float angle;
		
		public float getAngleInDegrees(){
			return angle;
		}		
	};

		
	public WallSensorRayCast(World world, Car car, WallSensorType type, float wallSensorRange) {
		this.world = world;
		this.car = car;
		this.body = car.getBody();	
		this.type = type;
		this.wallSensorRange = wallSensorRange;
		
		calculateSensorPoints();
		           
		EdgeShape edgeShape = new EdgeShape();
		edgeShape.set(sensorPointBegin, sensorPointEnd);
		        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = edgeShape;
        fixtureDef.isSensor = true;        
        
        this.fixture = body.createFixture(fixtureDef);
        this.fixture.setUserData(this);
        
        clearSensorValue();
        this.callback = new WallSensorRayCastCallback(this);
	}

	private void calculateSensorPoints() {
		this.sensorPointBegin = car.getWallSensorsOrigin();
		this.sensorPointEnd = getArcPointAtAngle(sensorPointBegin, type.getAngleInDegrees(), wallSensorRange);		
	}	
	
	private Vector2 getArcPointAtAngle(Vector2 center, float angle, float r){
		// Usando equação paramétrica do círculo
		// X = X0 + r * cos(O)
		float X = center.x + r * MathUtils.cosDeg(angle);
		// Y = Y0 + r * sin(O)
		float Y = center.y + r * MathUtils.sinDeg(angle);
				
		return new Vector2(X, Y);
	}
	
	public Car getCar() {
		return car;
	}

	public float getValue(){
		return this.value;
	}

	public void clearSensorValue(){
		this.value = Constants.WALL_SENSOR_CLEAR_VALUE;
	}
		
	private boolean isSensorCleared(){
		return (this.value == Constants.WALL_SENSOR_CLEAR_VALUE);
	}
		
	public void updateSensor(){
		clearSensorValue();		
		
		// O retorno de métodos desse tipo sempre retornam uma referencia local do objeto
		// assim sempre que usar estes métodos é necessário realizar uma cópia do vetor
		Vector2 tmp = car.getBody().getWorldPoint(sensorPointBegin);
		Vector2 rayBegin = new Vector2(tmp.x, tmp.y);
		
		tmp = car.getBody().getWorldPoint(sensorPointEnd);
		Vector2 rayEnd = new Vector2(tmp.x, tmp.y);
		
		//System.out.println("RayCast -> rb: " + rayBegin + ", re: " + rayEnd + ", sb: " + sensorPointBegin + ", se: " + sensorPointEnd);
		
		world.rayCast(callback, rayBegin, rayEnd);
	}

	private static class WallSensorRayCastCallback implements RayCastCallback{
		
		WallSensorRayCast sensor;		
		
		public WallSensorRayCastCallback(WallSensorRayCast sensor){
			this.sensor = sensor;
		} 
		
		@Override
		public float reportRayFixture(Fixture fixture, Vector2 point, Vector2 normal, float fraction) {
			Object userData = fixture.getUserData();
			if(userData != null && userData instanceof Wall){
				if(fraction < sensor.getValue()){
					sensor.value = fraction;					
				}
				return fraction;
			}
						
			return -1;
		}
				
	}
	
	@Override
	public String toString() {
		return "WallSensor [type=" + type + ", value=" + (isSensorCleared() ? "CLEAR" : Float.toString(value)) + "]";
	}
	
	public WallSensorType getType() {
		return type;
	}
	
	public boolean isActivated(){
		return this.value  != Constants.WALL_SENSOR_CLEAR_VALUE;
	}
}
