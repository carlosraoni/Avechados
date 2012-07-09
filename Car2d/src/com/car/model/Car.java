package com.car.model;

import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javax.swing.text.Position;

import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Body;
import com.badlogic.gdx.physics.box2d.BodyDef;
import com.badlogic.gdx.physics.box2d.BodyDef.BodyType;
import com.badlogic.gdx.physics.box2d.Fixture;
import com.badlogic.gdx.physics.box2d.PolygonShape;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJoint;
import com.badlogic.gdx.physics.box2d.joints.RevoluteJointDef;
import com.car.ai.CarIntelligenceInterface;
import com.car.ai.WallSensorRayCast;
import com.car.ai.WayPointSensor;
import com.car.ai.WayPointsLine;
import com.car.utils.Constants;
import com.car.utils.Controls;
import com.car.utils.TiledMapHelper;

public class Car {
	
	private int id;
	private float width;
	private float height;
	private Vector2 boundingBoxLocalCenter;
	private Vector2 wallSensorsOrigin;
	private final Vector2 currentForwardNormalBuffer = new Vector2();
	private Body body;
	private World world;
	private List<Tire> tires = new ArrayList<Tire>();
	private List<WallSensorRayCast> wallSensors = new ArrayList<WallSensorRayCast>();
	private WayPointSensor wayPointSensor;
	private RevoluteJoint flJoint, frJoint;
	private CarType type;
	private CarIntelligenceInterface intelligence;
	private Checkpoint lastCheckpointPassed = null;
	private Long lastCheckpointTime = null;
	private Race race;
	private int lap = 0;
	private CarColor color = CarColor.BLUE;
	
	public static enum CarType{
		PLAYER, COMPUTER;
	};
	
	
	public enum CarColor {
		BLUE("azul"),
		LIGHT_BLUE ("azul_claro"),
		YELLOW ("amarelo"),
		RED ("vermelho"),
		PURPLE ("roxo"),
		GREEN ("verde");
		
		private String code;
		
		CarColor(String code){
			this.code = code;
		}
		public String code(){
			return code;
		}
		
		public static CarColor value(int ordinal){
			for (CarColor color : CarColor.values()){
				if(color.ordinal() == ordinal)
					return color;
			}
			return null;
		}
	}

	public Car(Race race, CarPosition position, CarColor color){	
		init(position.getPosition(), race.getWorld(), position.getX(), position.getY(), position.getAngle(), CarType.PLAYER, race.getWayPointsLine(), color, race);        
	}

	public Car(Race race, CarPosition position, CarColor color, CarIntelligenceInterface sensorIntelligence) {
		this.intelligence = sensorIntelligence;
		init(position.getPosition(), race.getWorld(), position.getX(), position.getY(), position.getAngle(), CarType.COMPUTER, race.getWayPointsLine(), color, race);
	}
	
	private void init(int id, World world, float posX, float posY, float initialAngle, CarType type, WayPointsLine wayPointsLine, CarColor color,Race race) {
		this.id = id;
		this.race = race;
		this.world = world;
		this.type = type;
		this.color = color;
		
        //create car body
        BodyDef bodyDef = new BodyDef();
        bodyDef.type = BodyType.DynamicBody;
        bodyDef.position.x = posX;
        bodyDef.position.y = posY;
        bodyDef.angle = initialAngle * MathUtils.degreesToRadians;
        		
        body = world.createBody(bodyDef);
        body.setAngularDamping(3f);
        
        List<Vector2> vertices = new ArrayList<Vector2>();

        vertices.add( new Vector2(1.5f,-5f) );        
        vertices.add( new Vector2(3,-2.5f) );
        vertices.add( new Vector2(2.8f,0.5f) );
        vertices.add( new Vector2(1,5) );
        vertices.add( new Vector2(-1,5) );
        vertices.add( new Vector2(-2.8f,0.5f) );
        vertices.add( new Vector2(-3,-2.5f) );
        vertices.add( new Vector2(-1.5f,-5) );
        
        // Origem dos sensores na frente/meio do carrinho
        this.wallSensorsOrigin = new Vector2(0f, 5f);
        
        PolygonShape polygonShape = new PolygonShape();
        polygonShape.set( vertices.toArray(new Vector2[vertices.size()]) );
        Fixture fixture = body.createFixture(polygonShape, 0.1f);//shape, density
        fixture.setUserData(this);
        setWidthAndHeight(vertices);
                
        //prepare common joint parameters
        RevoluteJointDef jointDef = new RevoluteJointDef();
        jointDef.bodyA = body;
        jointDef.enableLimit = true;
        jointDef.lowerAngle = 0;
        jointDef.upperAngle = 0;
        jointDef.referenceAngle = 0;
        jointDef.localAnchorB.set(Vector2.Zero); 
        
        float maxForwardSpeed = 250;
        float maxBackwardSpeed = -40;
        float backTireMaxDriveForce = 300;
        float frontTireMaxDriveForce = 500;
        float backTireMaxLateralImpulse = 8.5f;
        float frontTireMaxLateralImpulse = 7.5f;
        float shift =-5f;
        //back left tire
        Tire tire = new Tire(world, posX - 0.75f, posY - 3f, initialAngle);
        tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed, backTireMaxDriveForce, backTireMaxLateralImpulse);
        jointDef.bodyB = tire.getBody();
        jointDef.localAnchorA.set(-3, 0.75f+ shift);
        world.createJoint(jointDef);
        tires.add(tire);
        
        //back right tire
        tire = new Tire(world, posX - 0.75f, posY + 3f, initialAngle);
        tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed, backTireMaxDriveForce, backTireMaxLateralImpulse);
        jointDef.bodyB = tire.getBody();
        jointDef.localAnchorA.set(3, 0.75f+ shift);
        world.createJoint(jointDef);
        tires.add(tire);

        //front left tire
        tire = new Tire(world, posX - 8.5f, posY - 3f, initialAngle);
        tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed, frontTireMaxDriveForce, frontTireMaxLateralImpulse);
        jointDef.bodyB = tire.getBody();
        jointDef.localAnchorA.set( -3, 8.5f + shift);
        flJoint = (RevoluteJoint)world.createJoint(jointDef);
        tires.add(tire);

        //front right tire
        tire = new Tire(world, posX - 8.5f, posY + 3f, initialAngle);
        tire.setCharacteristics(maxForwardSpeed, maxBackwardSpeed, frontTireMaxDriveForce, frontTireMaxLateralImpulse);
        jointDef.bodyB = tire.getBody();
        jointDef.localAnchorA.set( 3, 8.5f + shift);
        frJoint = (RevoluteJoint)world.createJoint(jointDef);
        tires.add(tire);
        
        initSensors(race, world, wayPointsLine);
	}

	private void initSensors(Race race, World world, WayPointsLine wayPointsLine) {
		initWallSensors(race.getWallSensorRange());        
        initWayPointSensors(race.getWayPointRange(), world, wayPointsLine);
	}

	private void initWayPointSensors(float wayPointRange, World world, WayPointsLine wayPointsLine) {
		wayPointSensor = new WayPointSensor(world,this,wayPointsLine, wayPointRange);
	}

	private void initWallSensors(float wallSensorRange){
		for(WallSensorRayCast.WallSensorType sensorType: WallSensorRayCast.WallSensorType.values()){
			wallSensors.add(new WallSensorRayCast(world,this, sensorType, wallSensorRange));
		}
	}


	private void setWidthAndHeight(List<Vector2> vertices) {
		if(vertices == null || vertices.size() == 0)
			return;
		float maxX = vertices.get(0).x, minX = vertices.get(0).x;
		float maxY = vertices.get(0).x, minY = vertices.get(0).y;
		
		for(Vector2 v: vertices){
			if(v.x < minX) minX = v.x;
			if(v.x > maxX) maxX = v.x;
			if(v.y < minY) minY = v.y;
			if(v.y > maxY) maxY = v.y;
		}
		
		this.width = maxX - minX;
		this.height = maxY - minY;		
		this.boundingBoxLocalCenter = new Vector2((minX + maxX)/2f, (minY + maxY)/2f);
	}


	public void update(BitSet controls) {		
		for(Tire p: tires){
			p.updateFriction();
		}
		
		for(Tire p: tires){			
			p.updateDrive(controls);
		}		
		
	    //control steering
		float lockAngle = 35 * MathUtils.degreesToRadians;
		float turnSpeedPerSec = 160 * MathUtils.degreesToRadians;//from lock to lock in 0.5 sec
		float turnPerTimeStep = turnSpeedPerSec / 60.0f;
		float desiredAngle = 0;
		
		if(controls.get(Controls.TDC_LEFT.ordinal())){
			desiredAngle = lockAngle; 
		}else if(controls.get(Controls.TDC_RIGHT.ordinal())){
			desiredAngle = -lockAngle;
		}
		
		float angleNow = flJoint.getJointAngle();
		float angleToTurn = desiredAngle - angleNow;
		angleToTurn = MathUtils.clamp( angleToTurn, -turnPerTimeStep, turnPerTimeStep );
		float newAngle = angleNow + angleToTurn;
		flJoint.setLimits( newAngle, newAngle );
		frJoint.setLimits( newAngle, newAngle );

    }

	public Body getBody() {
		return body;
	}


	public void setBody(Body body) {
		this.body = body;
	}


	public List<Tire> getTires() {
		return tires;
	}


	public void setTires(List<Tire> tires) {
		this.tires = tires;
	}

	
	public float getWidth() {
		return width;
	}


	public float getHeight() {
		return height;
	}

	public Vector2 getBoundingBoxLocalCenter(){		
		return boundingBoxLocalCenter;
	}
		
	public Vector2 getWallSensorsOrigin() {
		return wallSensorsOrigin;
	}

	public void printSensors() {
		System.out.println("-----------------------------------------------------");
		for(WallSensorRayCast sensor: wallSensors){
			System.out.println(sensor);
		}
		System.out.println("-----------------------------------------------------");
		
	}

	public void updateSensors() {	
		wayPointSensor.update();
		for(WallSensorRayCast sensor: wallSensors){
			sensor.updateSensor();
		}
		
	}

	public CarType getType() {		
		return type;
	}

	public float getX(){
		return getBody().getPosition().x;
	}
	
	public float getY(){
		return getBody().getPosition().y;
	}
	
	public float getAngleInDegrees(){
		return getBody().getAngle() * MathUtils.radiansToDegrees;		
	}
	
	public List<WallSensorRayCast> getWallSensors() {
		return wallSensors;
	}

	public WayPointSensor getWaypointSensor() {
		return wayPointSensor;
	}

	public BitSet getCarNextControls() {
		
		return intelligence.getCarNextControls(this);
	}

	public int getLap() {
		return lap;
	}
	
	public int getId() {
		return id;
	}

	public void checkpoint(Checkpoint checkPoint) {
		boolean checkPointUpdated = false;
		if(lastCheckpointPassed != null 
				&& checkPoint.getIndex() == 0 
				&& (lastCheckpointPassed.getIndex() +1 == race.getCheckpoints().size())){
			lastCheckpointPassed = null;
			lastCheckpointTime = System.currentTimeMillis();
			checkPointUpdated = true;
			this.lap++;
			System.out.println("Lap: " + this.lap + " by cartype : " + type);
		}
		if((lastCheckpointPassed == null  && checkPoint.getIndex() == 0)
				|| (lastCheckpointPassed!=null && checkPoint.getIndex() -1 == lastCheckpointPassed.getIndex())){
			
			lastCheckpointPassed = checkPoint;
			lastCheckpointTime = System.currentTimeMillis();
			checkPointUpdated = true;
			if(type.equals(CarType.PLAYER)){
				System.out.println("Checkpoint " + checkPoint.getIndex() + " passed by cartype : " + type);
			}
		}
		if(type == CarType.PLAYER && lap == race.getTotalLaps()){
			race.finishRace();
		}
		if(checkPointUpdated){
			race.updatePositions();
		}
	}

	public int getLastCheckpointIndex() {
		if(lastCheckpointPassed == null)
			return 0;
		return lastCheckpointPassed.getIndex() + 1;
	}

	public Long getLastCheckpointTime() {
		if(lastCheckpointTime == null){
			return Long.MAX_VALUE;
		}
		
		return lastCheckpointTime;
	}
	

	@Override
	public String toString() {
		return "Car [id=" + id + ", type=" + type + ", lastCheckpointPassed="
				+ getLastCheckpointIndex() + ", lastCheckpointTime="
				+ getLastCheckpointTime() + ", lap=" + lap + "]";
	}

	public CarColor getColor() {
		// TODO Auto-generated method stub
		return this.color;
	}

	public float getCurrentForwardVelocity() {
		Vector2 tmp = body.getWorldVector(Constants.UNIT_VECTOR2_Y );
		currentForwardNormalBuffer.set(tmp.x, tmp.y);
    	    	
        return currentForwardNormalBuffer.mul(currentForwardNormalBuffer.dot(body.getLinearVelocity()) ).len();		
	}
}

