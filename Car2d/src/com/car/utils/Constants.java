package com.car.utils;

import com.badlogic.gdx.math.Vector2;

public interface Constants {
	public static int UNFILLED_DISTANCE = -1;
	
	public static final String START_PLAYER_COLUMN_KEY = "startPlayerColumn";
	public static final String START_PLAYER_ROW_KEY = "startPlayerLine";
	
	public static final String PHYSICAL_LAYER_NAME = "physicalLayer";
	public static final String BOUNDARY_LIMITS_NAME = "boundaryLimits";
	public static final String INSIDE_TRACK_LIMITS_NAME = "insideTrackLimits";
	public static final String OUTSIDE_TRACK_LIMITS_NAME = "outsideTrackLimits";
	public static final String WAYPOINTS_NAME = "waypoints";
	public static final String CAR_POSITION_NAME = "CarPosition";
	public static final String CAR_POSITION_KEY = "position";
	public static final String CAR_ANGLE_KEY = "angle";
	
	public static final String TILE_LOCATION_KEY = "location";
	public static final String TILE_GROUND_KEY = "ground";
	public static final String TILE_QUALIFIER_KEY = "qualifier";

	// we want one tile to be 10mx10m
	public static final float PPM = 1.8f;
	
	// constants below are in meters
	public static final int HTC_EVO_3D_W = 540;
	public static final int HTC_EVO_3D_H = 960;
	
	public static final int GALAXY_5_W = 240;
	public static final int GALAXY_5_H = 320;
	
	public static final int DOUBLE_GALAXY_5_W = 480;
	public static final int DOUBLE_GALAXY_5_H = 640;
	
//	// change to target resolution
	public static final int TARGET_RES_W = DOUBLE_GALAXY_5_H;
	public static final int TARGET_RES_H = DOUBLE_GALAXY_5_W;
//	public static final int TARGET_RES_W = GALAXY_5_H;
//	public static final int TARGET_RES_H = GALAXY_5_W;
//	public static final int TARGET_RES_W = HTC_EVO_3D_W;
//	public static final int TARGET_RES_H = HTC_EVO_3D_H;
	
	//public static final float ASPECT_RATIO = 0.68f;
	public static final float ASPECT_RATIO = TARGET_RES_W  /TARGET_RES_H;
//	public static final float VIEW_W = 150f;
	public static final float VIEW_W = 300f;
//	public static final float VIEW_W = 300f;
	public static final float VIEW_H = VIEW_W / ASPECT_RATIO;
	
	// Range dos sensores da parede
	
	public static final float WALL_SENSOR_APERTURE = 0.1f;
	public static final float WALL_SENSOR_CLEAR_VALUE = Float.MAX_VALUE;
	
	public static final String WALL_SENSOR_RANGE_KEY = "WallSensorRange";
	public static final String WAYPOINT_RANGE_KEY = "WayPointRange";
	public static final int CAR_PLAYER_INITIAL_POSITION = 1;
	public static final String CHECKPOINT_NAME = "Checkpoint";
	public static final String TOTAL_LAPS_KEY = "TotalLaps";
	public static final Object CHECKPOINT_INDEX_KEY = "index";
	
	public static final long RACE_START_TIME_SECONDS = 5; // segundos

	public static final Vector2 UNIT_VECTOR2_X = new Vector2(1,0);
	public static final Vector2 UNIT_VECTOR2_Y = new Vector2(0,1);
	
	public static final int MAX_RACE_CARS = 6;

	public static final float WALL_SENSOR_FRONT_RANGE = 70;

}
