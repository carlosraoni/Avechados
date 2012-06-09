package com.car.model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.car.utils.Constants;
import com.car.utils.Controls;

public class Car {
	
	private Body body;
	private List<Tire> tires = new ArrayList<Tire>();
	RevoluteJoint flJoint, frJoint;
	public Car(World world, float pixelXCoord, float pixelYCoord){
        //create car body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.x = pixelXCoord / Constants.PIXELS_PER_METER;
        bodyDef.position.y = pixelYCoord / Constants.PIXELS_PER_METER;
        		
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

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set( vertices.toArray(new Vector2[vertices.size()]) );        
        body.createFixture(polygonShape, 0.1f);//shape, density
        
        //prepare common joint parameters
        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.bodyA = body;
        jointDef.enableLimit = true;
        jointDef.lowerAngle = 0;
        jointDef.upperAngle = 0;
        // TODO setZero()
        jointDef.localAnchorB.set(0, 0);//center of tire
        //jointDef.localAnchorB.set(Vector2.Zero); 
        
        float maxForwardSpeed = 250;
        float maxBackwardSpeed = -40;
        float backTireMaxDriveForce = 300;
        float frontTireMaxDriveForce = 500;
        float backTireMaxLateralImpulse = 8.5f;
        float frontTireMaxLateralImpulse = 7.5f;
        
        //back left tire
        Tire tire = new Tire(world);
        tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed, backTireMaxDriveForce, backTireMaxLateralImpulse);
        jointDef.bodyB = tire.getBody();
        jointDef.localAnchorA.set(-3, 0.75f);
        world.createJoint(jointDef);
        tires.add(tire);
        
        //back right tire
        tire = new Tire(world);
        tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed, backTireMaxDriveForce, backTireMaxLateralImpulse);
        jointDef.bodyB = tire.getBody();
        jointDef.localAnchorA.set(3, 0.75f);
        world.createJoint(jointDef);
        tires.add(tire);

        //front left tire
        tire = new Tire(world);
        tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed, frontTireMaxDriveForce, frontTireMaxLateralImpulse);
        jointDef.bodyB = tire.getBody();
        jointDef.localAnchorA.set( -3, 8.5f );
        flJoint = (RevoluteJoint)world.createJoint(jointDef);
        tires.add(tire);

        //front right tire
        tire = new Tire(world);
        tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed, frontTireMaxDriveForce, frontTireMaxLateralImpulse);
        jointDef.bodyB = tire.getBody();
        jointDef.localAnchorA.set( 3, 8.5f );
        frJoint = (RevoluteJoint)world.createJoint(jointDef);
        tires.add(tire);
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
	
	public void setXCoordFromXPixelCoord(float pixelCoordX){
		getBody().getPosition().x = pixelCoordX / Constants.PIXELS_PER_METER;
	}
	
	public void setYCoordFromYPixelCoord(float pixelCoordY){
		getBody().getPosition().y = pixelCoordY / Constants.PIXELS_PER_METER;
	}
	
	public float getXPixelCoord(){
		return getBody().getPosition().x * Constants.PIXELS_PER_METER;
	}
	
	public float getYPixelCoord(){
		return getBody().getPosition().y * Constants.PIXELS_PER_METER;
	}
}

