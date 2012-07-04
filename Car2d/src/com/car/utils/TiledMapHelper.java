package com.car.utils;

/**
 *   Copyright 2011 David Kirchner dpk@dpk.net
 *
 *   Licensed under the Apache License, Version 2.0 (the "License");
 *   you may not use this file except in compliance with the License.
 *   You may obtain a copy of the License at
 *
 *       http://www.apache.org/licenses/LICENSE-2.0
 *
 *   Unless required by applicable law or agreed to in writing, software
 *   distributed under the License is distributed on an "AS IS" BASIS,
 *   WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *   See the License for the specific language governing permissions and
 *   limitations under the License.
 *   
 * TiledMapHelper can simplify your game's tiled map operations. You can find
 * some sample code using this class at my blog:
 * 
 * http://dpk.net/2011/05/08/libgdx-box2d-tiled-maps-full-working-example-part-2/
 * 
 * Note: This code does have some limitations. It only supports single-layered
 * maps.
 * 
 * This code is based on TiledMapTest.java found at:
 * http://code.google.com/p/libgdx/
 */

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLayer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObject;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObjectGroup;
import com.badlogic.gdx.math.Vector2;
import com.car.model.CarPosition;

public class TiledMapHelper {

	private FileHandle packFileDirectory;
	private TileAtlas tileAtlas;	
	private TiledMap map;
	
	// Propriedades do Mapa
	private float wallSensorRange;
	private float wayPointRange;
		
	// Linhas do mapa
	private List<Vector2> boudaryLimitsLine;
	private List<Vector2> insideTrackLine;
	private List<Vector2> outsideTrackLine;
	private List<Vector2> waypoints;
	private Map<Integer,List<Vector2>> checkpoints = new HashMap<Integer, List<Vector2>>();
	
	// Posicoes do Mapa
	private Map<Integer, CarPosition> racePositions;	

	public TiledMapHelper(String tmxFile, String packDirectory) {
		racePositions = new HashMap<Integer, CarPosition>();
		packFileDirectory = Gdx.files.internal(packDirectory);
		map = TiledLoader.createMap(Gdx.files.internal(tmxFile));
		tileAtlas = new TileAtlas(map, packFileDirectory);
		
		loadMapProperties();
		parseMapObjects();
	}
	
	private void loadMapProperties() {
		this.wallSensorRange = Float.parseFloat(map.properties.get(Constants.WALL_SENSOR_RANGE_KEY));
		this.wayPointRange = Float.parseFloat(map.properties.get(Constants.WAYPOINT_RANGE_KEY));		
	}

	private void parseMapObjects() {
		for (TiledObjectGroup group : map.objectGroups) {
			if(Constants.PHYSICAL_LAYER_NAME.equals(group.name)){
				for (TiledObject object : group.objects) {				
					if(Constants.BOUNDARY_LIMITS_NAME.equals(object.name)){
						this.boudaryLimitsLine = buildWorldLineFromTiledObject(object);
					}
					else if(Constants.INSIDE_TRACK_LIMITS_NAME.equals(object.name)){
						this.insideTrackLine = buildWorldLineFromTiledObject(object);
					}
					else if(Constants.OUTSIDE_TRACK_LIMITS_NAME.equals(object.name)){
						this.outsideTrackLine = buildWorldLineFromTiledObject(object);
					}
					else if(Constants.CAR_POSITION_NAME.equals(object.name)){
						createCarPosition(object);
					}
					else if(Constants.WAYPOINTS_NAME.equals(object.name)){
						this.waypoints = buildWorldLineFromTiledObject(object);
					}
					else if(Constants.CHECKPOINT_TYPE.equals(object.type)){
						this.checkpoints.put(Integer.parseInt(object.properties.get("index"))-1, buildWorldLineFromTiledObject(object));
					}
				}
			}			
		}
	}

	private void createCarPosition(TiledObject object) {
		int position = Integer.parseInt(object.properties.get(Constants.CAR_POSITION_KEY));
		float angle = Integer.parseInt(object.properties.get(Constants.CAR_ANGLE_KEY));
		
		float coordX = getWorldXFromMapX(object.x);
		float coordY = getWorldYFromMapY(object.y);
		
		Vector2 worldPos = new Vector2(coordX, coordY);
		System.out.println("Position " + position + " loaded at " + worldPos + " angle " + angle);
		racePositions.put(position, new CarPosition(position, coordX, coordY, angle));
	}

	public List<Vector2> getInsideTrackLine() {
		return insideTrackLine;
	}

	public List<Vector2> getOutsideTrackLine() {
		return outsideTrackLine;
	}

	private List<Vector2> buildWorldLineFromTiledObject(TiledObject object) {
		List<Vector2> line =  new ArrayList<Vector2>();
		float iniX = object.x;
		float iniY = object.y;
		
		String [] points = object.polyline.split(" ");
		for(String point: points){
			String [] coords = point.split(",");
			float x = Float.parseFloat(coords[0]) + iniX;
			float y = Float.parseFloat(coords[1]) + iniY;
			
			Vector2 worldVertex = new Vector2(getWorldXFromMapX(x), getWorldYFromMapY(y));
			//System.out.println(worldVertex);
			line.add(worldVertex);
		}
		
		return line;
	}

	public List<Vector2> getBoudaryLimitsLine() {
		return boudaryLimitsLine;
	}
	
	public List<Vector2> getWaypoints() {
		return waypoints;
	}

	public void dispose() {
		tileAtlas.dispose();
		// tileMapRenderer.dispose();
	}

	public int getTileWidth() {
		return map.tileWidth;
	}
	

	public int getTileHeight() {
		return map.tileHeight;
	}

	public int getPixelHeight() {
		return map.height * map.tileHeight;
	}

	public int getPixelWidth() {
		return map.width * map.tileWidth;
	}

	public TiledMap getMap() {
		return map;
	}
	
	public TileAtlas getTileAtlas(){
		return tileAtlas;
	}
	
	public FileHandle getPackerFileDirectory(){
		return packFileDirectory;
	}

	public String getMapProperty(String key) {
		if(map == null)
			return null;
		return map.properties.get(key);
	}

	public int getNumRows() {
		if(map == null)
			return 0;
		return map.layers.get(0).tiles.length;
	}
	
	public int getNumCols() {
		if(map == null)
			return 0;
		return map.layers.get(0).tiles[0].length;
	}

	public int getStartPlayerColumn(){
		return Integer.parseInt(getMapProperty(Constants.START_PLAYER_COLUMN_KEY));
	}
	
	public int getStartPlayerRow(){
		return Integer.parseInt(getMapProperty(Constants.START_PLAYER_ROW_KEY));
	}
	
	public int getStartPlayerXMap(){
		return getStartPlayerColumn() * getTileWidth() + getTileWidth() / 2 ;
	}
	
	public int getStartPlayerYMap(){
		return getStartPlayerRow() * getTileHeight() + getTileHeight() / 2 ;
	}
	
	public float getWorldUnitsPerTileX(){
		return getTileWidth()/Constants.PPM;
	}
	
	public float getWorldUnitsPerTileY(){
		return getTileHeight()/Constants.PPM;
	}
	
	public float getWorldMapWidth(){
		return getNumCols() * getWorldUnitsPerTileX();
	}
	
	public float getWorldMapHeight(){
		return getNumRows() * getWorldUnitsPerTileY();
	}
	
	public float getWorldXFromMapX(float mapX){
		return mapX / Constants.PPM;
	}
	
	public float getWorldYFromMapY(float mapY){
		return getWorldMapHeight() - (mapY / Constants.PPM);
	}
	
	public float getStartPlayerXWorld(){
		return getWorldXFromMapX(getStartPlayerXMap());
	}
	
	public float getStartPlayerYWorld(){
		return getWorldYFromMapY(getStartPlayerYMap());
	}
	
	public Map<Integer, CarPosition> getRacePositions() {
		return racePositions;
	}
	
	public CarPosition getPosition(int position){	
		return racePositions.get(position);
	}
	
	public float getWallSensorRange() {
		return wallSensorRange;
	}

	public float getWayPointRange() {
		return wayPointRange;
	}


	public Map<Integer,List<Vector2>> getCheckPointsTiled() {
		return checkpoints;
	}	
	
	public static void main(String[] args) {
		System.out.println("--- map ---");
		TiledMap map = TiledLoader.createMap(new FileHandle("res/NatalArenaLimits.tmx"));
		for (String name : map.properties.keySet()) {			
			String value = map.properties.get(name);
			System.out.println("\tproperty :: " + name + ": " + value);
		}
		System.out.println();
		System.out.println("--- layers ---");
		for (TiledLayer layer : map.layers) {
			System.out.println("layer: " + layer.name);
			for (String name : layer.properties.keySet()) {				
				String value = layer.properties.get(name);
				System.out.println("layer property :: " + name + ": " + value);
			}
			System.out.println("--- layer tiles ---");
			for(int i=0; i<layer.tiles.length; i++){
				for(int j=0; j<layer.tiles[i].length; j++){
					int tileIndex = layer.tiles[i][j] - 1;
					String locationStr = map.getTileProperty(tileIndex, Constants.TILE_LOCATION_KEY);
					String groundStr = map.getTileProperty(tileIndex, Constants.TILE_GROUND_KEY);
					String qualifierStr = map.getTileProperty(tileIndex, Constants.TILE_QUALIFIER_KEY);
					
					System.out.println("Tile["+ i +"]["+ j +"] = "+ tileIndex+ "(" + locationStr + "," + groundStr + "," + qualifierStr + ")");
					
				}
				System.out.println();
			}
		}
		System.out.println();
		
		System.out.println("--- groups ---");		
		for (TiledObjectGroup group : map.objectGroups) {
			System.out.println("group: " + group.name);
			for (String name : group.properties.keySet()) {				
				String value = group.properties.get(name);
				System.out.println("\tproperty :: " + name + ": " + value);
			}
			for (TiledObject object : group.objects) {				
				System.out.println("\tobject: " + object.name);
				System.out.println("\tx:" + object.x + ", y:" + object.y + " -> " + object.polyline);
				for (String name : object.properties.keySet()) {					
					String value = object.properties.get(name);
					System.out.println("\t\tproperty :: " + name + ": " + value);
				}
			}
		}
	}

}
