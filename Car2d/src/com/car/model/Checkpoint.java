package com.car.model;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;

public class Checkpoint {
	private Race race;
	private List<Vector2> vertexs;
	private Fixture fixture;
	private int index;
	
	public Checkpoint(Race race, List<Vector2> vertexs,int index){
		this.index = index;
		this.race = race;
		this.vertexs = vertexs;
	}
	
	public void makeItPhysical(World world){
		if(vertexs == null)
			return;
		
		Vector2 v0 = new Vector2(vertexs.get(0));
		Vector2 v1 = new Vector2(vertexs.get(1));
			
		v0.sub(v1);
		v0.rotate(-90);
		v0.mul(1/v0.len());
		v0.add(v1);
		vertexs.add(v0);
	
		v0 = new Vector2(vertexs.get(0));
		v1 = new Vector2(vertexs.get(1));
		
		v1.sub(v0);
		v1.rotate(90);
		v1.mul(1/v1.len());
		v1.add(v0);
		vertexs.add(v1);
			
		Vector2 [] vsArr = vertexs.toArray(new Vector2[vertexs.size()]);

		PolygonShape polygon = new PolygonShape();
		polygon.set(vsArr);
		
		BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        Body body = world.createBody(bodyDef);	
        
        this.fixture = body.createFixture(polygon, 0);
        this.fixture.setSensor(true);
        this.fixture.setUserData(this);
	}

	public int getIndex() {
		return index;
	}
}
