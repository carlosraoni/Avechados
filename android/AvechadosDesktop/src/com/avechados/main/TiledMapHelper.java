package com.avechados.main;

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

import java.util.Iterator;

import com.avechados.utils.Constants;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.tiled.TileAtlas;
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer;
import com.badlogic.gdx.graphics.g2d.tiled.TileSet;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLayer;
import com.badlogic.gdx.graphics.g2d.tiled.TiledLoader;
import com.badlogic.gdx.graphics.g2d.tiled.TiledMap;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObject;
import com.badlogic.gdx.graphics.g2d.tiled.TiledObjectGroup;
import com.badlogic.gdx.math.Vector3;

public class TiledMapHelper {
	private static final int[] layersList = { 0 };

	/**
	 * Renders the part of the map that should be visible to the user.
	 */
	public void render() {
		tileMapRenderer.getProjectionMatrix().set(camera.combined);

		Vector3 tmp = new Vector3();
		tmp.set(0, 0, 0);
		camera.unproject(tmp);

		tileMapRenderer.render((int) tmp.x, (int) tmp.y,
				Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), layersList);
	}

	/**
	 * Get the height of the map in pixels
	 * 
	 * @return y
	 */
	public int getHeight() {
		return map.height * map.tileHeight;
	}

	/**
	 * Get the width of the map in pixels
	 * 
	 * @return x
	 */
	public int getWidth() {
		return map.width * map.tileWidth;
	}

	/**
	 * Get the map, useful for iterating over the set of tiles found within
	 * 
	 * @return TiledMap
	 */
	public TiledMap getMap() {
		return map;
	}

	/**
	 * Calls dispose on all disposable resources held by this object.
	 */
	public void dispose() {
		tileAtlas.dispose();
		tileMapRenderer.dispose();
	}

	/**
	 * Sets the directory that holds the game's pack files and tile sets.
	 * 
	 * @param packDirectory
	 */
	public void setPackerDirectory(String packDirectory) {
		packFileDirectory = Gdx.files.internal(packDirectory);
	}

	/**
	 * Loads the requested tmx map file in to the helper.
	 * 
	 * @param tmxFile
	 */
	public void loadMap(String tmxFile) {
		if (packFileDirectory == null) {
			throw new IllegalStateException("loadMap() called out of sequence");
		}

		map = TiledLoader.createMap(Gdx.files.internal(tmxFile));
		tileAtlas = new TileAtlas(map, packFileDirectory);

		tileMapRenderer = new TileMapRenderer(map, tileAtlas, 16, 16);
	}

	/**
	 * Prepares the helper's camera object for use.
	 * 
	 * @param screenWidth
	 * @param screenHeight
	 */
	public void prepareCamera(int screenWidth, int screenHeight) {
		camera = new OrthographicCamera(screenWidth, screenHeight);

		camera.position.set(0, 0, 0);
	}

	/**
	 * Returns the camera object created for viewing the loaded map.
	 * 
	 * @return OrthographicCamera
	 */
	public OrthographicCamera getCamera() {
		if (camera == null) {
			throw new IllegalStateException(
					"getCamera() called out of sequence");
		}
		return camera;
	}

	private FileHandle packFileDirectory;

	private OrthographicCamera camera;

	private TileAtlas tileAtlas;
	private TileMapRenderer tileMapRenderer;

	private TiledMap map;

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
	
}
