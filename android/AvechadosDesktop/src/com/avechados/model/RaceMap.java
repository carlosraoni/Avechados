package com.avechados.model;

import com.avechados.utils.Constants;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLayer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;

public class RaceMap {
	
	public enum GroundType{
		ASFALT, SAND, WHITE_SAND, GRASS, WATER, STONE
	};
	public enum LocationType{
		INSIDE, OUTSIDE
	};
	public enum Qualifier{
		START
	};
	
	private TiledMap tiledMap;
	private RaceMapCell [][] map; 
		
	public class RaceMapCell{
		private GroundType ground;
		private LocationType location;
		private Qualifier qualifier;
		private int row;
		private int column;
		// Distancia da largada até a celula
		private int distance;
		
		public RaceMapCell(int row, int column, GroundType ground, LocationType location, Qualifier qualifier) {			
			this.ground = ground;
			this.location = location;
			this.qualifier = qualifier;
			this.row = row;
			this.column = column;
		}		
		
		public GroundType getGround() {
			return ground;
		}

		public LocationType getLocation() {
			return location;
		}

		public Qualifier getQualifier() {
			return qualifier;
		}

		public int getRow() {
			return row;
		}

		public int getColumn() {
			return column;
		}
		
		public int getDistance() {
			return distance;
		}

		public void setDistance(int distance) {
			this.distance = distance;
		}
	}	
	
	public static RaceMap buildRaceMap(TiledMap tiledMap){
		if(tiledMap == null || tiledMap.layers == null || tiledMap.layers.size() == 0)
			return null;
		RaceMap raceMap = new RaceMap(tiledMap);
		TiledLayer trackLayer = tiledMap.layers.get(0);
		if(trackLayer.tiles == null || trackLayer.tiles.length == 0)
			return raceMap;
		
		int numRows = trackLayer.tiles.length;		
		int numCols = trackLayer.tiles[0].length;
		raceMap.map = new RaceMapCell[numRows][numCols];
		
		for(int i=0; i<numRows; i++){
			for(int j=0; j<numCols; j++){
				int tileIndex = trackLayer.tiles[i][j] - 1;
				String locationStr = tiledMap.getTileProperty(tileIndex, Constants.TILE_LOCATION_KEY);
				String groundStr = tiledMap.getTileProperty(tileIndex, Constants.TILE_GROUND_KEY);
				String qualifierStr = tiledMap.getTileProperty(tileIndex, Constants.TILE_QUALIFIER_KEY);
				
				// TODO: instanciar o raceMapCell correspondente a celula e atribuir no map 
				
				//System.out.println("Tile["+ i +"]["+ j +"] = "+ tileIndex+ "(" + locationStr + "," + groundStr + "," + qualifierStr + ")");
				
			}			
		}
				
				
		return raceMap;
	}
	
	private RaceMap(TiledMap tiledMap){
		this.tiledMap = tiledMap;
	}
}
