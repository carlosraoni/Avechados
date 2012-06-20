package com.car.model;

import java.util.BitSet;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.car.utils.Constants;
import com.car.utils.TiledMapHelper;

public class Race {

	private Car player;
	private World world;
	
	public Race(TiledMapHelper tiledHelper){				
		world = new World(new Vector2(0, 0), false);		
		player = new Car(world, tiledHelper.getStartPlayerXWorld(), tiledHelper.getStartPlayerYWorld());
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
		player.update(controls);
		world.step(timeStep, velocityIterations, positionIterations);	
		//System.out.println(player.getBody().getPosition());
		//System.out.println("-------------------------------");
	}

	public Vector2 getBoundingBoxPlayerCenter() {		
		return player.getBoundingBoxLocalCenter();
	}
	
}
