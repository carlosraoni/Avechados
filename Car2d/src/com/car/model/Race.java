package com.car.model;

import java.util.BitSet;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.car.utils.TiledMapHelper;

public class Race {

	private Car player;
	private World world;
	
	public Race(TiledMapHelper tiledHelper){
		
//		int posIniX = mapWidth/2;
//		int posIniY = mapHeight/2;
//		try {
//			int startPlayerColumn = Integer.parseInt(tiledMapHelper.getMapProperty(Constants.START_PLAYER_COLUMN_KEY));
//			int startPlayerLine = tiledMapHelper.getNumRows() - Integer.parseInt(tiledMapHelper.getMapProperty(Constants.START_PLAYER_ROW_KEY)) - 1;
//			
//			int tileWidth = tiledMapHelper.getMap().tileWidth;
//			int tileHeight = tiledMapHelper.getMap().tileHeight;
//			
//			posIniX = (startPlayerColumn * tileWidth);
//			posIniY = (startPlayerLine * tileHeight);
//			
//		} catch (NumberFormatException e) {
//			System.out.println("Erro parseando inteiro de posição inicial no mapa: " + e.getMessage());
//			e.printStackTrace();
//		}

		
		world = new World(new Vector2(0, 0), false);
		//player = new Car(world, posIniX, posIniY);
		player = new Car(world, 0, 0);

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
	}
	
}
