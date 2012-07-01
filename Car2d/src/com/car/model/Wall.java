package com.car.model;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;

public class Wall {
	private Race race;
	private List<Vector2> vertexs;
	private Fixture fixture;
	private WallType type;	
	
	public static enum WallType{
		INSIDE, OUTSIDE, BOUNDARY;
	};
	
	public Wall(Race race, List<Vector2> vertexs, WallType type){
		this.race = race;
		this.vertexs = vertexs; 
		this.type = type;
	}
	
	public void createWallInPhysicalWorld(World world){
		if(vertexs == null || vertexs.size() < 3)
			return;
		
		Vector2 [] vsArr = vertexs.toArray(new Vector2[vertexs.size()]);
		
		ChainShape chain = new ChainShape();		
		chain.createLoop(vsArr);
		
		BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        Body body = world.createBody(bodyDef);	
        
        this.fixture = body.createFixture(chain, 0);
        this.fixture.setUserData(this);
	}
	
}
