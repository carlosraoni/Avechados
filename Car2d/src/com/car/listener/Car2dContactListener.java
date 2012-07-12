package com.car.listener;

import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.car.model.Car;
import com.car.model.Checkpoint;

public class Car2dContactListener implements ContactListener {

	@Override
	public void beginContact(Contact contact) {
		
		if(isFinishLineContact(contact)){
			//System.out.println("UserData A : " + contact.getFixtureA().getUserData());
			//System.out.println("UserData B : " + contact.getFixtureB().getUserData());
			Car car = (Car) (contact.getFixtureA().getUserData() instanceof Car ?
					contact.getFixtureA().getUserData() : 
						contact.getFixtureB().getUserData());
			Checkpoint checkPoint = (Checkpoint) (contact.getFixtureA().getUserData() instanceof Checkpoint ?
					contact.getFixtureA().getUserData() : 
						contact.getFixtureB().getUserData());
			
			car.checkpoint(checkPoint);
			//System.out.println("Lap : " + car.getLap());
			
		}
				

	}

	private boolean isFinishLineContact(Contact contact) {
		return (contact.getFixtureA().getUserData() instanceof Checkpoint 
				&& contact.getFixtureB().getUserData() instanceof Car)
			|| (contact.getFixtureA().getUserData() instanceof Car 
					&& contact.getFixtureB().getUserData() instanceof Checkpoint);
	}

	@Override
	public void endContact(Contact contact) {
		//System.out.println("endContact");

	}

	@Override
	public void postSolve(Contact arg0, ContactImpulse arg1) {
		// TODO Auto-generated method stub

	}

	@Override
	public void preSolve(Contact arg0, Manifold arg1) {
		// TODO Auto-generated method stub

	}

}
