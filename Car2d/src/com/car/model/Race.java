package com.car.model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.car.ai.CarArtificialIntelligence;
import com.car.utils.TiledMapHelper;

public class Race {
	// Carros da corrida
	private List<Car> cars = new ArrayList<Car>();
	// Carro do jogador
	private Car player;
	// Carro para qual a camera deve manter o foco, criado apenas para facilitar testes da IA
	private Car focusCar;
	
	private World world;	
	private CarArtificialIntelligence carArtificialIntelligence;
	
	public Race(TiledMapHelper tiledHelper){				
		world = new World(new Vector2(0, 0), false);
		// Player
		Vector2 racePos = tiledHelper.getPosition(1);
		player = new Car(world, racePos.x, racePos.y, Car.CarType.PLAYER);
		cars.add(player);
		focusCar = player;
		// Opponents
		racePos = tiledHelper.getPosition(2);
		cars.add(new Car(world, racePos.x, racePos.y, Car.CarType.COMPUTER));
		//cars.add(new Car(world, tiledHelper.getStartPlayerXWorld() + 15, tiledHelper.getStartPlayerYWorld()-15, Car.CarType.COMPUTER));
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
				controls = carArtificialIntelligence.getCarNextControls(car);
			}
			else{
				//car.printSensors();
			}
			car.update(controls);			
		}
		
		world.step(timeStep, velocityIterations, positionIterations);				
	}

	public List<Car> getCars() {		
		return cars;
	}
}
