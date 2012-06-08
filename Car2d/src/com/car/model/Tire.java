package com.car.model;

import java.util.Set;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.car.fixture.GroundAreaFUD;
import com.car.utils.Controls;

public class Tire {

	private Body body;
	
    float m_maxForwardSpeed;
    float m_maxBackwardSpeed;
    float m_maxDriveForce;
    float m_maxLateralImpulse;
    Set<GroundAreaFUD> m_groundAreas;
    float m_currentTraction;	
	
	public Tire(World world){
		BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        body = world.createBody(bodyDef);	

        PolygonShape polygonShape = new PolygonShape();
        polygonShape.setAsBox( 0.5f, 1.25f );
        body.createFixture(polygonShape, 1);//shape, density
        body.setUserData(this);
        
        m_currentTraction = 1;
	}
	
	// TODO novo construtor?
	public void setCharacteristics(float maxForwardSpeed, float maxBackwardSpeed, float maxDriveForce, float maxLateralImpulse) {
        m_maxForwardSpeed = maxForwardSpeed;
        m_maxBackwardSpeed = maxBackwardSpeed;
        m_maxDriveForce = maxDriveForce;
        m_maxLateralImpulse = maxLateralImpulse;
    }
    
    public void addGroundArea(GroundAreaFUD ga){
    	m_groundAreas.add(ga); 
    	updateTraction(); 
    }
    
    public void removeGroundArea(GroundAreaFUD ga){
    	m_groundAreas.remove(ga); 
    	updateTraction(); 
    }
	
    public void updateTraction()
    {
    	if( m_groundAreas.isEmpty() )
            m_currentTraction = 1;
        else {
            m_currentTraction = 0;            
            for(GroundAreaFUD ga: m_groundAreas){            	 
                if ( ga.getFrictionModifier() > m_currentTraction )
                	m_currentTraction = ga.getFrictionModifier();
            }
        }
    }
    
	public Vector2 getLateralVelocity() {
		Vector2 currentRightNormal = body.getWorldVector( new Vector2(1,0) );
		
		// TODO possível falha		
		return currentRightNormal.mul( currentRightNormal.dot(body.getLinearVelocity()) );
	}
	
    public Vector2 getForwardVelocity() {
    	Vector2 currentForwardNormal = body.getWorldVector(new Vector2(0,1) );
    	
		// TODO possível falha    	
        return currentForwardNormal.mul( currentForwardNormal.dot(body.getLinearVelocity()) );
    }
    
    public void updateFriction() {

        Vector2 impulse = getLateralVelocity().mul(-body.getMass());
        
        if ( impulse.len() > m_maxLateralImpulse ){
        	// TODO ?????
            //impulse = impulse.mul(m_maxLateralImpulse).mul(-impulse.len());
            impulse = impulse.mul(m_maxLateralImpulse/impulse.len());
        }
        
        body.applyLinearImpulse( impulse.mul(m_currentTraction), body.getWorldCenter());

        //angular velocity
        body.applyAngularImpulse( m_currentTraction * 0.1f * body.getInertia() * -body.getAngularVelocity() );

        //forward linear velocity
        Vector2 currentForwardNormal = getForwardVelocity();
        
        // TODO currentForwardNormal.nomalize
        //float currentForwardSpeed = currentForwardNormal.len2();
        float currentForwardSpeed = currentForwardNormal.nor().len();
        float dragForceMagnitude = -2 * currentForwardSpeed;
        body.applyForce( currentForwardNormal.mul(m_currentTraction * dragForceMagnitude) , body.getWorldCenter() );
    }
    
    public void updateDrive(boolean controlState, Controls c) {

        float desiredSpeed = 0;
        
        if(controlState){
        	if(c.equals(Controls.TDC_UP)){
        		desiredSpeed = m_maxForwardSpeed;
        	}else if(c.equals(Controls.TDC_DOWN)){
        		desiredSpeed = m_maxBackwardSpeed;
        	}
        }else{
        	return;
        }

        //find current speed in forward direction
        // TODO verificar b2Dot
        Vector2 currentForwardNormal = body.getWorldVector( new Vector2(0,1) );
        System.out.println("CFNX: " + currentForwardNormal.x + ", CFNY: " + currentForwardNormal.y);
        float currentSpeed = currentForwardNormal.dot( getForwardVelocity() );
        System.out.println("CFNX: " + currentForwardNormal.x + ", CFNY: " + currentForwardNormal.y);
        System.out.println("DesiredSpeed: " + desiredSpeed + ", CurrentSpeed: " + currentSpeed);
        //apply necessary force
        float force = 0;
        if ( desiredSpeed > currentSpeed )
            force = m_maxDriveForce;
        else if ( desiredSpeed < currentSpeed )
            force = -m_maxDriveForce;
        else
            return;
        System.out.println("Force: " + force + ", CFNX: " + currentForwardNormal.x + ", CFNY: " + currentForwardNormal.y);
        
        body.applyForce( currentForwardNormal.mul(m_currentTraction).mul(force), body.getWorldCenter() );
    }
    

    public void updateTurn(boolean controlState, Controls c) {
        float desiredTorque = 0;
        
        if( controlState && c.equals(Controls.TDC_LEFT)){
        	 desiredTorque = 15;
        }else if(controlState && c.equals(Controls.TDC_RIGHT) ){
        	desiredTorque = -15;
        }

        body.applyTorque(desiredTorque);
    }

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

}
