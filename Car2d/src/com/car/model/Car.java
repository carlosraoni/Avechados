package com.car.model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.car.ai.WallSensor;
import com.car.ai.WallSensorRayCast;
import com.car.model.Car.CarType;
import com.car.utils.Controls;

public class Car {
	
	private float width;
	private float height;
	private Vector2 boundingBoxLocalCenter;
	private Vector2 wallSensorsOrigin;
	private Body body;
	private World world;
	private List<Tire> tires = new ArrayList<Tire>();
	private List<WallSensorRayCast> wallSensors = new ArrayList<WallSensorRayCast>();
	private RevoluteJoint flJoint, frJoint;
	private CarType type;	
	
	public static enum CarType{
		PLAYER, COMPUTER;
	};
	
	public Car(World world, float posX, float posY, CarType type){
		this.world = world;
		this.type = type;
		
        //create car body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.x = posX;
        bodyDef.position.y = posY;
        bodyDef.angle = 90 * MathUtils.degreesToRadians;
        		
        body = world.createBody(bodyDef);
        body.setAngularDamping(3f);
        
        List<Vector2> vertices = new ArrayList<Vector2>();

        vertices.add( new Vector2(1.5f,0f) );        
        vertices.add( new Vector2(3,2.5f) );
        vertices.add( new Vector2(2.8f,5.5f) );
        vertices.add( new Vector2(1,10) );
        vertices.add( new Vector2(-1,10) );
        vertices.add( new Vector2(-2.8f,5.5f) );
        vertices.add( new Vector2(-3,2.5f) );
        vertices.add( new Vector2(-1.5f,0) );
        
        // Origem dos sensores na frente/meio do carrinho
        this.wallSensorsOrigin = new Vector2(0f, 10f);
        
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set( vertices.toArray(new Vector2[vertices.size()]) );         
        body.createFixture(polygonShape, 0.1f);//shape, density
        setWidthAndHeight(vertices);
                
        //prepare common joint parameters
        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.bodyA = body;
        jointDef.enableLimit = true;
        jointDef.lowerAngle = 0;
        jointDef.upperAngle = 0;
        jointDef.referenceAngle = 0;
        jointDef.localAnchorB.set(Vector2.Zero); 
        
        float maxForwardSpeed = 250;
        float maxBackwardSpeed = -40;
        float backTireMaxDriveForce = 300;
        float frontTireMaxDriveForce = 500;
        float backTireMaxLateralImpulse = 8.5f;
        float frontTireMaxLateralImpulse = 7.5f;
        
        //back left tire
        Tire tire = new Tire(world, posX - 0.75f, posY - 3f);
        tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed, backTireMaxDriveForce, backTireMaxLateralImpulse);
        jointDef.bodyB = tire.getBody();
        jointDef.localAnchorA.set(-3, 0.75f);
        world.createJoint(jointDef);
        tires.add(tire);
        
        //back right tire
        tire = new Tire(world, posX - 0.75f, posY + 3f);
        tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed, backTireMaxDriveForce, backTireMaxLateralImpulse);
        jointDef.bodyB = tire.getBody();
        jointDef.localAnchorA.set(3, 0.75f);
        world.createJoint(jointDef);
        tires.add(tire);

        //front left tire
        tire = new Tire(world, posX - 8.5f, posY - 3f);
        tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed, frontTireMaxDriveForce, frontTireMaxLateralImpulse);
        jointDef.bodyB = tire.getBody();
        jointDef.localAnchorA.set( -3, 8.5f );
        flJoint = (RevoluteJoint)world.createJoint(jointDef);
        tires.add(tire);

        //front right tire
        tire = new Tire(world, posX - 8.5f, posY + 3f);
        tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed, frontTireMaxDriveForce, frontTireMaxLateralImpulse);
        jointDef.bodyB = tire.getBody();
        jointDef.localAnchorA.set( 3, 8.5f );
        frJoint = (RevoluteJoint)world.createJoint(jointDef);
        tires.add(tire);
        
        //inicializa os sensores de parede
        //if(type == CarType.COMPUTER)
        	initWallSensors();
	}

	private void initWallSensors(){
		for(WallSensorRayCast.WallSensorType sensorType: WallSensorRayCast.WallSensorType.values()){
			wallSensors.add(new WallSensorRayCast(world,this, sensorType));
		}
	}

	private void setWidthAndHeight(List<Vector2> vertices) {
		if(vertices == null || vertices.size() == 0)
			return;
		float maxX = vertices.get(0).x, minX = vertices.get(0).x;
		float maxY = vertices.get(0).x, minY = vertices.get(0).y;
		
		for(Vector2 v: vertices){
			if(v.x < minX) minX = v.x;
			if(v.x > maxX) maxX = v.x;
			if(v.y < minY) minY = v.y;
			if(v.y > maxY) maxY = v.y;
		}
		
		this.width = maxX - minX;
		this.height = maxY - minY;		
		this.boundingBoxLocalCenter = new Vector2((minX + maxX)/2f, (minY + maxY)/2f);
	}


	public void update(BitSet controls) {		
		for(Tire p: tires){
			p.updateFriction();
		}
		
		for(Tire p: tires){			
			p.updateDrive(controls);
		}		
		
	    //control steering
		float lockAngle = 35 * MathUtils.degreesToRadians;
		float turnSpeedPerSec = 160 * MathUtils.degreesToRadians;//from lock to lock in 0.5 sec
		float turnPerTimeStep = turnSpeedPerSec / 60.0f;
		float desiredAngle = 0;
		
		if(controls.get(Controls.TDC_LEFT.ordinal())){
			desiredAngle = lockAngle; 
		}else if(controls.get(Controls.TDC_RIGHT.ordinal())){
			desiredAngle = -lockAngle;
		}
		
		float angleNow = flJoint.getJointAngle();
		float angleToTurn = desiredAngle - angleNow;
		angleToTurn = MathUtils.clamp( angleToTurn, -turnPerTimeStep, turnPerTimeStep );
		float newAngle = angleNow + angleToTurn;
		flJoint.setLimits( newAngle, newAngle );
		frJoint.setLimits( newAngle, newAngle );
    }


	public Body getBody() {
		return body;
	}


	public void setBody(Body body) {
		this.body = body;
	}


	public List<Tire> getTires() {
		return tires;
	}


	public void setTires(List<Tire> tires) {
		this.tires = tires;
	}

	
	public float getWidth() {
		return width;
	}


	public float getHeight() {
		return height;
	}

	public Vector2 getBoundingBoxLocalCenter(){		
		return boundingBoxLocalCenter;
	}
		
	public Vector2 getWallSensorsOrigin() {
		return wallSensorsOrigin;
	}

	public void printSensors() {
		System.out.println("-----------------------------------------------------");
		for(WallSensorRayCast sensor: wallSensors){
			System.out.println(sensor);
		}
		System.out.println("-----------------------------------------------------");
		
	}

	public void updateSensors() {		
		for(WallSensorRayCast sensor: wallSensors){
			sensor.updateSensor();
		}
		
	}

	public CarType getType() {		
		return type;
	}

	public float getX(){
		return getBody().getPosition().x;
	}
	
	public float getY(){
		return getBody().getPosition().y;
	}
	
	public float getAngleInDegrees(){
		return getBody().getAngle() * MathUtils.radiansToDegrees;		
	}
}

