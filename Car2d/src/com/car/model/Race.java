package com.car.model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.car.ai.SeekWaypointSensorIntelligence;
import com.car.ai.WallSensorIntelligence;
import com.car.ai.CarIntelligenceInterface;
import com.car.ai.WayPointsLine;
import com.car.utils.TiledMapHelper;

public class Race {
	// Carros da corrida
	private List<Car> cars = new ArrayList<Car>();
	// Carro do jogador
	private Car player;
	// Carro do oponente
	private Car opponent;
	
	// Carro para qual a camera deve manter o foco, criado apenas para facilitar testes da IA
	private Car focusCar;
	
	private World world;	
	
	private WayPointsLine wayPointsLine;
	
	public Race(TiledMapHelper tiledHelper){				
		world = new World(new Vector2(0, 0), false);
		wayPointsLine = new WayPointsLine(tiledHelper, world);
		// Player
		Vector2 racePos = tiledHelper.getPosition(1);
		player = new Car(world, racePos.x, racePos.y, Car.CarType.PLAYER,wayPointsLine);
		//player = new Car(world, racePos.x, racePos.y, Car.CarType.COMPUTER, wayPointsLine,new SeekWaypointSensorIntelligence());
		cars.add(player);
		
		// Opponents
		racePos = tiledHelper.getPosition(2);
		opponent = new Car(world, racePos.x, racePos.y, Car.CarType.COMPUTER, wayPointsLine, new SeekWaypointSensorIntelligence());
		cars.add(opponent);
		
		//foco da camera
		//focusCar = opponent;
		focusCar = player;
	
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
	
	public float getFocusCarX(){
		return focusCar.getBody().getPosition().x;
	}
	
	public float getFocusCarY(){
		return focusCar.getBody().getPosition().y;
	}
	
	public World getWorld(){
		return world;
	}

	public float getPlayerAngleInDegrees() {
		return player.getBody().getAngle() * MathUtils.radiansToDegrees;
	}

	public void update(float timeStep, int velocityIterations, int positionIterations, BitSet playerControls) {
		for(Car car: cars){
			car.updateSensors();
			BitSet controls = playerControls;
			if(car.getType() == Car.CarType.COMPUTER){
				controls = car.getCarNextControls();
			}
			
			car.update(controls);			
		}
		
		world.step(timeStep, velocityIterations, positionIterations);				
	}

	public List<Car> getCars() {		
		return cars;
	}
}
