package com.car.ai;

import java.util.ArrayList;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.FixtureDef;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.car.model.Car;
import com.car.utils.Constants;

public class WallSensor {
	
	private Car car;
	private Body body;
	private Fixture fixture;
	
	private Vector2 sensorPointOrigin, sensorPointArcBegin, sensorPointArcMiddle, sensorPointArcEnd;
	private WallSensorType type;
	private float wallSensorRange;
	
	private float value;
		
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

		
	public WallSensor(Car car, WallSensorType type, float wallSensorRange) {
		this.car = car;
		this.body = car.getBody();	
		this.type = type;
		
		// Foi necessario definir a forma do sensor como um poligono por conta que o edgeShape
		// não colidia devido a sua espessura ser 0, assim o sensor foi definido como um semi-circulo muito fino
		calculateSensorPoints();
		        
        List<Vector2> vertices = new ArrayList<Vector2>();
        
        vertices.add(sensorPointOrigin);
        vertices.add(sensorPointArcBegin);
        vertices.add(sensorPointArcMiddle);
        vertices.add(sensorPointArcEnd);

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set( vertices.toArray(new Vector2[vertices.size()]) );         
        
        FixtureDef fixtureDef = new FixtureDef();
        fixtureDef.shape = polygonShape;
        fixtureDef.isSensor = true;        
        
        this.fixture = body.createFixture(fixtureDef);
        this.fixture.setUserData(this);
        
        clearSensorValue();
	}

	private void calculateSensorPoints() {
		this.sensorPointOrigin = car.getWallSensorsOrigin();
		this.sensorPointArcBegin = getArcPointAtAngle(sensorPointOrigin, type.getAngleInDegrees() - Constants.WALL_SENSOR_APERTURE, wallSensorRange);
		this.sensorPointArcMiddle = getArcPointAtAngle(sensorPointOrigin, type.getAngleInDegrees(), wallSensorRange);
		this.sensorPointArcEnd = getArcPointAtAngle(sensorPointOrigin, type.getAngleInDegrees() + Constants.WALL_SENSOR_APERTURE, wallSensorRange);		
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
	
	private void processCollision(Vector2 collisionPoint){
		// O valor do sensor será igual ao valor do parametro t da equacao parametrica da reta no ponto de colisao
		// Equacao parametrica da reta:
		// X = X0 + (X1 - X0) * t
		// Y = Y0 + (Y1 - Y0) * t
		
		// Equacao do parametro t no ponto de colisao (X, Y)
		// t = ((X - X0) + (Y - Y0))/((X1 - X0) + (Y1 - Y0))
		
		// (X - X0)
		float deltaCpBeginX = collisionPoint.x - sensorPointArcBegin.x;
		// (Y - Y0)
		float deltaCpBeginY = collisionPoint.y - sensorPointArcBegin.y;
		// (X1 - X0)
		float deltaBeginEndX = sensorPointArcEnd.x - sensorPointArcBegin.x;
		// (Y1 - Y0)
		float deltaBeginEndY = sensorPointArcEnd.y - sensorPointArcBegin.y;
		
		float t = (deltaCpBeginX + deltaCpBeginY) / (deltaBeginEndX + deltaBeginEndY);
		
		// Soh atualiza o valor do sensor se eh o primeiro ponto de colisao processado para o sensor
		// ou o valor eh menor que o valor atual do sensor, de forma que o valor do sensor seja o valor
		// da colisao mais proxima
		if(isSensorCleared() || (t < this.value)){
			this.value = t;
		}
	}
	
	public void processContact(Contact contact){		
		for(Vector2 collisionPoint: contact.getWorldManifold().getPoints()){
			Vector2 localPoint = body.getLocalPoint(collisionPoint);
//			System.out.println("=======================");
//			System.out.println("carro: " + body.getPosition());
//			System.out.println("mundo: " + collisionPoint);
//			System.out.println("local: " + localPoint);
//			System.out.println("=======================");
			processCollision(localPoint);
		}
	}

	@Override
	public String toString() {
		return "WallSensor [type=" + type + ", value=" + (isSensorCleared() ? "CLEAR" : Float.toString(value)) + "]";
	}
	
	
}
