package com.car.model;

import java.util.BitSet;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Contact;
import com.badlogic.gdx.physics.box2d.ContactImpulse;
import com.badlogic.gdx.physics.box2d.ContactListener;
import com.badlogic.gdx.physics.box2d.Manifold;
import com.badlogic.gdx.physics.box2d.World;
import com.car.ai.CarArtificialIntelligence;
import com.car.ai.WallSensor;
import com.car.utils.TiledMapHelper;

public class Race {

	private Car player;
	private Car opponent;
	private World world;	
	private CarArtificialIntelligence carArtificialIntelligence;
	
	public Race(TiledMapHelper tiledHelper){				
		world = new World(new Vector2(0, 0), false);		
		player = new Car(world, tiledHelper.getStartPlayerXWorld(), tiledHelper.getStartPlayerYWorld(), Car.CarType.PLAYER);
		opponent = new Car(world, tiledHelper.getStartPlayerXWorld(), tiledHelper.getStartPlayerYWorld()-15, Car.CarType.COMPUTER);
		
		carArtificialIntelligence = new CarArtificialIntelligence();
		
		createRaceWalls(tiledHelper, world);
	}	

	private void createRaceWalls(TiledMapHelper tiledHelper, World world) {
		Wall insideWall = new Wall(this, tiledHelper.getInsideTrackLine(), Wall.WallType.INSIDE);
		Wall outsideWall = new Wall(this, tiledHelper.getOutsideTrackLine(), Wall.WallType.OUTSIDE);
		Wall boundaryWall = new Wall(this, tiledHelper.getBoudaryLimitsLine(), Wall.WallType.BOUNDARY);
		
		insideWall.createWallInPhysicalWorld(world);
		outsideWall.createWallInPhysicalWorld(world);
		boundaryWall.createWallInPhysicalWorld(world);
	}

	public float getPlayerX(){
		return player.getBody().getPosition().x;
	}
	
	public float getPlayerY(){
		return player.getBody().getPosition().y;
	}
	
	public World getWorld(){
		return world;
	}

	public float getPlayerAngleInDegrees() {
		return player.getBody().getAngle() * MathUtils.radiansToDegrees;
	}

	public void update(float timeStep, int velocityIterations, int positionIterations, BitSet controls) {
		player.updateSensors(controls);		
		BitSet opponentControls = carArtificialIntelligence.getCarNextControls(opponent);
		//opponent.update(opponentControls);
		player.update(controls);
		
		world.step(timeStep, velocityIterations, positionIterations);				
		player.printSensors();		
	}

	public Vector2 getBoundingBoxPlayerCenter() {		
		return player.getBoundingBoxLocalCenter();
	}
}
