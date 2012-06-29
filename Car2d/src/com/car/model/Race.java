package com.car.model;

import java.util.BitSet;
import java.util.List;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.World;
import com.car.ai.CarArtificialIntelligence;
import com.car.ai.Inputs;
import com.car.ai.WallSensor;
import com.car.utils.TiledMapHelper;

public class Race {

	private Car player;
	private Car opponent;
	private World world;
	private CarArtificialIntelligence carArtificialIntelligence;
	
	public Race(TiledMapHelper tiledHelper){				
		world = new World(new Vector2(0, 0), false);		
		player = new Car(world, tiledHelper.getStartPlayerXWorld(), tiledHelper.getStartPlayerYWorld());
		opponent = new Car(world, tiledHelper.getStartPlayerXWorld(), tiledHelper.getStartPlayerYWorld()-15);
		
		carArtificialIntelligence = new CarArtificialIntelligence();
		createChainFromVertexs(tiledHelper.getBoudaryLimitsLine(), true);
		createChainFromVertexs(tiledHelper.getInsideTrackLine(), true);
		createChainFromVertexs(tiledHelper.getOutsideTrackLine(), true);
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

		BitSet opponentControls = carArtificialIntelligence.getCarNextControls(opponent);
		//opponent.update(opponentControls);
		player.update(controls);
		opponent.clearWallSensors();
		world.step(timeStep, velocityIterations, positionIterations);	
		//System.out.println(player.getBody().getPosition());
		//System.out.println("-------------------------------");
	}

	public Vector2 getBoundingBoxPlayerCenter() {		
		return player.getBoundingBoxLocalCenter();
	}

	private void createChainFromVertexs(List<Vector2> vertexs, boolean closed){
		if(vertexs == null || vertexs.size() < 3)
			return;
		
		Vector2 [] vsArr = vertexs.toArray(new Vector2[vertexs.size()]);
		
		ChainShape chain = new ChainShape();
		if(closed){
			chain.createLoop(vsArr);
		}
		else{
			chain.createChain(vsArr);
		}
		
		BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        Body body = world.createBody(bodyDef);	
        
        body.createFixture(chain, 0);
	} 
}
