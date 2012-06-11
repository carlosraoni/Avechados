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
	
	public static final String TILE_LOCATION_KEY = "location";
	public static final String TILE_GROUND_KEY = "ground";
	public static final String TILE_QUALIFIER_KEY = "qualifier";

	public static final float PIXELS_PER_METER = 4;
}
