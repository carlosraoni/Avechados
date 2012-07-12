package com.car.fixture;


public class GroundAreaFUD extends FixtureUserData{

    private float frictionModifier;
    private boolean outOfCourse;
    
    public GroundAreaFUD( float frictionModifier, boolean outOfCourse ){
    	super(FixtureUserDataType.FUD_GROUND_AREA);
    	this.frictionModifier = frictionModifier;
    	this.outOfCourse = outOfCourse;    	
    }

	public float getFrictionModifier() {
		return frictionModifier;
	}

	public void setFrictionModifier(float frictionModifier) {
		this.frictionModifier = frictionModifier;
	}

	public boolean isOutOfCourse() {
		return outOfCourse;
	}

	public void setOutOfCourse(boolean outOfCourse) {
		this.outOfCourse = outOfCourse;
	}
}
