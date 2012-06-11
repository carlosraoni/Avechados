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

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLayer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObject;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObjectGroup;
import com.badlogic.gdx.math.Vector3;

public class TiledMapHelper {

	private FileHandle packFileDirectory;
	private TileAtlas tileAtlas;	
	private TiledMap map;	

	public TiledMapHelper(String tmxFile, String packDirectory) {
		packFileDirectory = Gdx.files.internal(packDirectory);
		map = TiledLoader.createMap(Gdx.files.internal(tmxFile));
		tileAtlas = new TileAtlas(map, packFileDirectory);		
	}
	
	public void dispose() {
		tileAtlas.dispose();
		// tileMapRenderer.dispose();
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

	
	public static void main(String[] args) {
		System.out.println("--- map ---");
		TiledMap map = TiledLoader.createMap(new FileHandle("res/NatalArena.tmx"));
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
				for (String name : object.properties.keySet()) {					
					String value = object.properties.get(name);
					System.out.println("\t\tproperty :: " + name + ": " + value);
				}
			}
		}
	}	
}
