package com.avechados.model;

import java.util.LinkedList;
import java.util.Queue;

import com.avechados.utils.Constants;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLayer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;

public class RaceMap {
	
	public enum GroundType{
		ASFALT, SAND, WHITE_SAND, GRASS, WATER, STONE;
	};
	public enum LocationType{
		INSIDE, OUTSIDE;
	};
	public enum Qualifier{
		START;
	};
	
	private RaceMapCell [][] map; 
	private int startPlayerRow;
	private int startPlayerColumn;
	
	private RaceMap(int startPlayerRow, int startPlayerColumn) {
		this.startPlayerRow = startPlayerRow;
		this.startPlayerColumn = startPlayerColumn;
	}
	
	public int getStartPlayerRow() {
		return startPlayerRow;
	}

	public int getStartPlayerColumn() {
		return startPlayerColumn;
	}

	public int getNumRows(){
		if(map != null)
			return map.length;
		return 0;
	}
	
	public int getNumCols(){
		if(map != null && map.length > 0)
			return map[0].length;
		return 0;
	}
	
	public static class RaceMapCell{
		private GroundType ground;
		private LocationType location;
		private Qualifier qualifier;
		private int row;
		private int column;
		// Distancia da largada ate a celula
		private int distance;
		
		public RaceMapCell(int row, int column, GroundType ground, LocationType location, Qualifier qualifier) {			
			this.ground = ground;
			this.location = location;
			this.qualifier = qualifier;
			this.row = row;
			this.column = column;
			this.distance = Constants.UNFILLED_DISTANCE;
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

		void setDistance(int distance) {
			this.distance = distance;
		}
	}	
	
	public static RaceMap buildRaceMapFromTiledMap(TiledMap tiledMap){
		if(tiledMap == null || tiledMap.layers == null || tiledMap.layers.size() == 0)
			return null;
		
		int startPlayerRow = Integer.parseInt(tiledMap.properties.get(Constants.START_PLAYER_ROW_KEY));
		int startPlayerColumn = Integer.parseInt(tiledMap.properties.get(Constants.START_PLAYER_COLUMN_KEY));
							
		RaceMap raceMap = new RaceMap(startPlayerRow, startPlayerColumn);		
		
		TiledLayer trackLayer = tiledMap.layers.get(0);
		if(trackLayer.tiles == null || trackLayer.tiles.length == 0)
			return raceMap;
		
		int numRows = trackLayer.tiles.length;		
		int numCols = trackLayer.tiles[0].length;
				
		buildCellMap(tiledMap, raceMap, trackLayer, numRows, numCols);
		fillDistancesFromStart(raceMap);
				
		return raceMap;
	}

	private static void buildCellMap(TiledMap tiledMap, RaceMap raceMap, TiledLayer trackLayer, int numRows, int numCols) {
		raceMap.map = new RaceMapCell[numRows][numCols];
		
		for(int i=0; i<numRows; i++){
			for(int j=0; j<numCols; j++){
				int tileIndex = trackLayer.tiles[i][j] - 1;
				String locationStr = tiledMap.getTileProperty(tileIndex, Constants.TILE_LOCATION_KEY);
				String groundStr = tiledMap.getTileProperty(tileIndex, Constants.TILE_GROUND_KEY);
				String qualifierStr = tiledMap.getTileProperty(tileIndex, Constants.TILE_QUALIFIER_KEY);
				
				LocationType location = LocationType.valueOf(locationStr.toUpperCase());
				GroundType ground = GroundType.valueOf(groundStr.toUpperCase());
				Qualifier qualifier = null;				
				if(qualifierStr != null)
					qualifier = Qualifier.valueOf(qualifierStr.toUpperCase());
												
				raceMap.map[i][j] = new RaceMapCell(i, j, ground, location, qualifier);				
			}			
		}
	}
	
	private static class BFSNode{
		private int row;
		private int col;
		private int depth;
		
		public BFSNode(int row, int col, int depth) {
			this.row = row;
			this.col = col;
			this.depth = depth;
		}

		public int getRow() {
			return row;
		}

		public int getCol() {
			return col;
		}

		public int getDepth() {
			return depth;
		}		
	}

	private static void fillDistancesFromStart(RaceMap raceMap) {
		int [][] dir = {{0, -1}, {-1, -1}, {-1, 0}, {-1, 1}, {0, 1} , {1, 1}, {1, 0}, {1, -1}};
		
		Queue<BFSNode> q =  new LinkedList<BFSNode>();
		
		int n = raceMap.getNumRows();
		int m = raceMap.getNumCols();
		int rowIni = raceMap.getStartPlayerRow();
		int colIni = raceMap.getStartPlayerColumn();
		RaceMapCell [][] cellMap = raceMap.map;
		
		// Largada
		cellMap[rowIni][colIni].setDistance(0);
		cellMap[rowIni+1][colIni].setDistance(0);
		
		cellMap[rowIni][colIni+1].setDistance(0);
		cellMap[rowIni+1][colIni+1].setDistance(0);		
				
		cellMap[rowIni][colIni-1].setDistance(1);
		cellMap[rowIni+1][colIni-1].setDistance(1);

		q.add(new BFSNode(rowIni, colIni -1 , 1));
		q.add(new BFSNode(rowIni+1, colIni -1 , 1));
		
		while(!q.isEmpty()){
			BFSNode atual = q.remove();
			for(int i=0; i<dir.length; i++){
				int nRow = atual.getRow() + dir[i][0];
				int nCol = atual.getCol() + dir[i][1];
				
				if(nRow < 0 || nCol < 0 || nRow >= n || nCol >= m)
					continue;
				if(cellMap[nRow][nCol].getLocation() == LocationType.OUTSIDE ||
						cellMap[nRow][nCol].getDistance() != Constants.UNFILLED_DISTANCE)
					continue;
				
				cellMap[nRow][nCol].setDistance(atual.getDepth() + 1);				
				q.add(new BFSNode(nRow, nCol, atual.getDepth() + 1));
			}
		}		
	}
	
	public void printGroundMatrix(){
		System.out.println("Ground Matrix ------------------");
		for(int i=0; i<getNumRows(); i++){
			for(int j=0; j<getNumCols(); j++){
				System.out.print(map[i][j].getGround().toString().substring(0,2) + " ");
			}
			System.out.println();
		}
	}
	
	public void printLocationMatrix(){
		System.out.println("Location Matrix ------------------");
		for(int i=0; i<getNumRows(); i++){
			for(int j=0; j<getNumCols(); j++){
				System.out.print(map[i][j].getLocation().toString().substring(0,1) + " ");
			}
			System.out.println();
		}
	}
	
	public void printDistanceMatrix(){
		System.out.println("Distance Matrix ------------------");
		for(int i=0; i<getNumRows(); i++){
			for(int j=0; j<getNumCols(); j++){
				System.out.printf("%4d", map[i][j].getDistance());
			}
			System.out.println();
		}
	}
	
	public void printInfoMatrixs(){
		printGroundMatrix();
		printLocationMatrix();
		printDistanceMatrix();
	}
	
	public GroundType getGroundAt(int row, int col){
		RaceMapCell cell = getCellAt(row, col);
		if(cell == null)
			return null;
		return cell.getGround();
	}
	
	public LocationType getLocationAt(int row, int col){
		RaceMapCell cell = getCellAt(row, col);
		if(cell == null)
			return null;
		return cell.getLocation();
	}
	
	public int getDistanceAt(int row, int col){
		RaceMapCell cell = getCellAt(row, col);
		if(cell == null)
			return Constants.UNFILLED_DISTANCE;
		return cell.getDistance();
	}
	
	public Qualifier getQualifierAt(int row, int col){
		RaceMapCell cell = getCellAt(row, col);
		if(cell == null)
			return null;
		return cell.getQualifier();
	}
	
	private RaceMapCell getCellAt(int row, int col){
		if(row < 0 || col < 0 ||row >= getNumRows() || col >= getNumCols())
			return null;
		return map[row][col];
	}
	
	public static void main(String[] args) {
		TiledMap tiledMap = TiledLoader.createMap(new FileHandle("res/NatalArena.tmx"));
		RaceMap raceMap = buildRaceMapFromTiledMap(tiledMap);
		raceMap.printInfoMatrixs();
	}

}
