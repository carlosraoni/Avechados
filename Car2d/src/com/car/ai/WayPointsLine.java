package com.car.ai;

import java.util.List;

import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.ChainShape;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.car.utils.TiledMapHelper;

public class WayPointsLine {

	private List<Vector2> wayPoints;
	
	public WayPointsLine(TiledMapHelper tiledHelper, World world) {
		wayPoints = tiledHelper.getWaypoints();
		
		if(wayPoints == null)
			return;
		
		Vector2 [] vsArr = wayPoints.toArray(new Vector2[wayPoints.size()]);
		
		ChainShape chain = new ChainShape();		
		chain.createLoop(vsArr);
		
		BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.StaticBody;
        Body body = world.createBody(bodyDef);	
        
        Fixture fixture = body.createFixture(chain, 0);
        fixture.setSensor(true);
        fixture.setUserData(this);
	}

	public List<Vector2> getWayPoints() {
		return wayPoints;
	}

}
