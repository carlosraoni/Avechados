package com.car.ai;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.EdgeShape;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.car.model.Car;

public class WallSensor {
	
	private Car car;
	private float value;
	private Body body;
	

	public Car getCar() {
		return car;
	}

	public float getValue(){
		return this.value;
	}

	
	public WallSensor(Body body, Car car, Vector2 v1, Vector2 v2) {

		this.car = car;
		this.body = body;	
		
        EdgeShape edgeShape = new EdgeShape();
        edgeShape.set(v1,v2);

        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = edgeShape;
        fixtureDef.isSensor = true;
        
        body.createFixture(fixtureDef);

	}

	public void setValue(float i) {
		this.value = 0;
	}
		
	/*public class SensorCallback implements  RayCastCallback{

		@Override
		public float reportRayFixture(Fixture arg0, Vector2 arg1, Vector2 arg2,
				float fraction) {
			System.out.println("arg3 :" + arg3);
			return 0;
		}
	}*/
	
	/*public float getValue() {
		Vector2 carPos = car.getBody().getPosition();
		Vector2 linearVel = car.getBody().getLinearVelocity();
		Vector2 cpyLinearVel = new Vector2(linearVel.x + carPos.x , linearVel.y + carPos.y);
		
		Vector2 destPoint = cpyLinearVel.rotate(angle).mul(range);		
		Vector2 srcPoint = car.getBody().getPosition(); 
		
	    world.rayCast(new SensorCallback(), srcPoint, destPoint);
	    System.out.println("Sensor :: angle = " + angle * MathUtils.radiansToDegrees + " :: srcPoint = " + srcPoint + ":: dstPoint = " + destPoint);
		
		return 0f;
	}*/
}
