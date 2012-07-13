package com.car.model;

import java.util.BitSet;
import java.util.Set;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.car.fixture.GroundAreaFUD;
import com.car.utils.Constants;
import com.car.utils.Controls;

public class Tire {

	private Body body;
	private Vector2 origin = new Vector2();
	
    private float m_maxForwardSpeed;
    private float m_maxBackwardSpeed;
    private float m_maxDriveForce;
    private float m_maxLateralImpulse;
    private Set<GroundAreaFUD> m_groundAreas;
    private float m_currentTraction;	
				
	public Tire(World world, Body carBody, float localCarX, float localCarY){
		BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        
        origin.set(localCarX, localCarY);
        Vector2 worldPos = carBody.getWorldPoint(origin);
        		
        bodyDef.position.x = worldPos.x;
        bodyDef.position.y = worldPos.y;
        bodyDef.angle = carBody.getAngle();
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
    
    private final Vector2 currentRightNormalBuffer = new Vector2();
    private final Vector2 currentLateralVelocity = new Vector2();
    
	public void updateLateralVelocity() {		
		Vector2 tmp = body.getWorldVector(Constants.UNIT_VECTOR2_X);
		currentRightNormalBuffer.set(tmp.x, tmp.y);		
		currentRightNormalBuffer.mul( currentRightNormalBuffer.dot(body.getLinearVelocity()) );		
		currentLateralVelocity.set(currentRightNormalBuffer.x, currentRightNormalBuffer.y);
	}
	
	private final Vector2 currentForwardNormalBuffer = new Vector2();
	private final Vector2 currentForwardVelocity = new Vector2();
	
    public void updateForwardVelocity() {    	
		Vector2 tmp = body.getWorldVector(Constants.UNIT_VECTOR2_Y);
		currentForwardNormalBuffer.set(tmp.x, tmp.y);    	    	
        currentForwardNormalBuffer.mul( currentForwardNormalBuffer.dot(body.getLinearVelocity()) );
        currentForwardVelocity.set(currentForwardNormalBuffer.x, currentForwardNormalBuffer.y);
    }
    
    private final Vector2 impulseBuffer = new Vector2();
    
    public void updateFriction() {
    	updateLateralVelocity();
    	impulseBuffer.set(currentLateralVelocity.x, currentLateralVelocity.y);
        impulseBuffer.mul(-body.getMass());
        
        if ( impulseBuffer.len() > m_maxLateralImpulse ){        	
        	impulseBuffer.mul(m_maxLateralImpulse/impulseBuffer.len());
        }
        
        body.applyLinearImpulse( impulseBuffer.mul(m_currentTraction), body.getWorldCenter());

        //angular velocity
        body.applyAngularImpulse( m_currentTraction * 0.1f * body.getInertia() * -body.getAngularVelocity() );        
        
        //forward linear velocity
        updateForwardVelocity();
        currentForwardNormalBuffer.set(currentForwardVelocity.x, currentForwardVelocity.y);        
        float currentForwardSpeed = currentForwardNormalBuffer.len();
        currentForwardNormalBuffer.nor();
        float dragForceMagnitude = -2 * currentForwardSpeed;
        body.applyForce( currentForwardNormalBuffer.mul(m_currentTraction * dragForceMagnitude) , body.getWorldCenter() );        
    }
    
    public void updateDrive(BitSet controls) {    	
        float desiredSpeed = 0;
                
        if(controls.get(Controls.TDC_UP.ordinal())){
        	desiredSpeed = m_maxForwardSpeed;
        }else if(controls.get(Controls.TDC_DOWN.ordinal())){
        		desiredSpeed = m_maxBackwardSpeed;
        }else{        	
        	return;
        }

        //find current speed in forward direction
        // TODO verificar b2Dot
        updateForwardVelocity();
        Vector2 tmp = body.getWorldVector( Constants.UNIT_VECTOR2_Y );
        currentForwardNormalBuffer.set(tmp.x, tmp.y);
        float currentSpeed = currentForwardNormalBuffer.dot(currentForwardVelocity);        
        //apply necessary force
        float force = 0;
        if ( desiredSpeed > currentSpeed )
            force = m_maxDriveForce;
        else if ( desiredSpeed < currentSpeed )
            force = -m_maxDriveForce;
        else
            return;
        
        body.applyForce( currentForwardNormalBuffer.mul(m_currentTraction).mul(force), body.getWorldCenter() );        
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
