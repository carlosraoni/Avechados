package com.car.utils;

public interface Constants {
	public static final int INITIAL_LIFE = 100;
	public static final float MAP_BOUNDARY = 10.0f;
	public static final int DEGREE_TURN = 5;
	public static final int SLOW_DEGREE_TURN = 3;
	
	public static final int TIME = 1;
	
	public static final int INITIAL_MAX_SPEED_PLAYER = 70;
	public static final int INITIAL_ACCELERATION_PLAYER = 1;
	public static final int BRAKE_SPEED = 5;
	public static final int SLOW_BRAKE_SPEED = 2;
	
	public static final float CONVERT_DELTA_TO_PIXELS = -(1.0f / 20.0f);

	public static int UNFILLED_DISTANCE = -1;
	
	public static final String START_PLAYER_COLUMN_KEY = "startPlayerColumn";
	public static final String START_PLAYER_ROW_KEY = "startPlayerLine";
	
	public static final String PHYSICAL_LAYER_NAME = "physicalLayer";
	public static final String BOUNDARY_LIMITS_NAME = "boundaryLimits";
	public static final String INSIDE_TRACK_LIMITS_NAME = "insideTrackLimits";
	public static final String OUTSIDE_TRACK_LIMITS_NAME = "outsideTrackLimits";
	
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
	
//	// change to target resolution
	public static final int TARGET_RES_W = GALAXY_5_W;
	public static final int TARGET_RES_H = GALAXY_5_H;
//	public static final int TARGET_RES_W = HTC_EVO_3D_W;
//	public static final int TARGET_RES_H = HTC_EVO_3D_H;
	
	public static final float ASPECT_RATIO = 0.68f;
	public static final float VIEW_W = 150f;
	public static final float VIEW_H = VIEW_W / ASPECT_RATIO;
	
	// Range dos sensores da parede
	public static final float WALL_SENSOR_RANGE = 20f;
	public static final float WALL_SENSOR_APERTURE = 0.1f;
	public static final float WALL_SENSOR_CLEAR_VALUE = Float.MAX_VALUE;
}