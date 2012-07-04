package com.car.model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.World;
import com.car.ai.SeekWaypointSensorIntelligence;
import com.car.ai.WayPointsLine;
import com.car.listener.Car2dContactListener;
import com.car.utils.Constants;
import com.car.utils.TiledMapHelper;

public class Race {
	// Carros da corrida
	private List<Car> cars = new ArrayList<Car>();	
	// Carro do jogador
	private Car player;
	
	// Carro para qual a camera deve manter o foco, criado apenas para facilitar testes da IA
	private Car focusCar;
	private World world;	
	private WayPointsLine wayPointsLine;
	private List<Checkpoint> checkpoints;
	
	public Race(TiledMapHelper tiledHelper){				
		world = new World(new Vector2(0, 0), false);
		world.setContactListener(new Car2dContactListener());
		wayPointsLine = new WayPointsLine(tiledHelper, world);
		checkpoints = createCheckPoints(tiledHelper,world);
		loadRaceCars(tiledHelper);
		createRaceWalls(tiledHelper, world);
		
		
	}

	private List<Checkpoint> createCheckPoints(TiledMapHelper tiledHelper,
			World world) {
		List<Checkpoint> checkpoints = new ArrayList<Checkpoint>();
		Map<Integer,List<Vector2>> checkpointsTiled = tiledHelper.getCheckPointsTiled();
		for(Integer i : checkpointsTiled.keySet()){
			checkpoints.add(new Checkpoint(this,checkpointsTiled.get(i),i));
			checkpoints.get(i).makeItPhysical(world);
		}
		return checkpoints;
	}

	private void loadRaceCars(TiledMapHelper tiledHelper) {
		Map<Integer, CarPosition> carPositions = tiledHelper.getRacePositions();
		Car lastComputerCar = null;
		for(Integer position: carPositions.keySet()){			
			CarPosition carPos = carPositions.get(position);
			if(position == Constants.CAR_PLAYER_INITIAL_POSITION){
				// Player
				player = new Car(tiledHelper, world, carPos.getX(), carPos.getY(), carPos.getAngle(), wayPointsLine,this);
				cars.add(player);
			}
			else{
				Car computer = new Car(tiledHelper, world, carPos.getX(), carPos.getY(), carPos.getAngle(), wayPointsLine,this, new SeekWaypointSensorIntelligence());
				cars.add(computer);
				lastComputerCar = computer;
			}
		}
		
		//foco da camera
		//focusCar = lastComputerCar;
		focusCar = player;
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

	public List<Checkpoint> getCheckpoints() {
		return checkpoints;
	}
}
