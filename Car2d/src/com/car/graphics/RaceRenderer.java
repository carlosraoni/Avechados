package com.car.graphics;

import java.util.List;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.car.model.Car;
import com.car.model.Car.CarColor;
import com.car.model.Race;
import com.car.utils.Constants;
import com.car.utils.TiledMapHelper;

public class RaceRenderer {	
	private Race raceWorld;
	private TiledMapHelper tiledHelper;
	
	private SpriteBatch spriteBatch;
	private BitmapFont font;
	private Texture[] carTexture;
	private TextureRegion[] carTextureRegion;
	
	private static final int[] layersList = { 0 };	
	private TileMapRenderer tileMapRenderer;
	
	private long lastRender;
	private Box2DDebugRenderer debugRenderer;
	
	private float mapW, mapH;
			
	private OrthographicCamera camera;
	
	private long firstTime;
	private long now;
	
	public RaceRenderer(Race race, TiledMapHelper tiledHelper, int screenPixelWidth, int screenPixelHeight){
		this.raceWorld = race;
		this.tiledHelper = tiledHelper;
		
		// setup map renderer
		float unitsPerTileX = tiledHelper.getWorldUnitsPerTileX();
		float unitsPerTileY = tiledHelper.getWorldUnitsPerTileY();
		tileMapRenderer = new TileMapRenderer(
				tiledHelper.getMap(), tiledHelper.getTileAtlas(), 16, 16, unitsPerTileX, unitsPerTileY);
		
		mapW = tiledHelper.getWorldMapWidth();
		mapH = tiledHelper.getWorldMapHeight();
		
		prepareCamera(Constants.VIEW_W, Constants.VIEW_H);

		debugRenderer = new Box2DDebugRenderer();
		
		carTexture = new Texture[raceWorld.getCars().size()];
		carTextureRegion = new TextureRegion[raceWorld.getCars().size()];
		List<Car> cars = raceWorld.getCars();
		for(Car car : cars){
			int index = car.getId() - 1;
			carTexture[index] = new Texture(Gdx.files.internal("res/cars/carro90_"+car.getColor().code()+".png"));
			carTextureRegion[index] = new TextureRegion(carTexture[index]);
		}

		spriteBatch = new SpriteBatch();
		FileHandle fontFile = Gdx.files.internal("res/fonts/comicSans.fnt");
		FileHandle imageFontFile = Gdx.files.internal("res/fonts/comicSans.png");
		font = new BitmapFont(fontFile, imageFontFile, false);
		
		this.firstTime = System.currentTimeMillis();
	}
	
	private void prepareCamera(float viewW, float viewH) {
		camera = new OrthographicCamera(viewW, viewH);
		camera.position.set(0, 0, 0);
	}
	
	/**
	 * Renders the part of the map that should be visible to the user.
	 */
	public void render() {
		this.now = System.currentTimeMillis();
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		updateCameraPosition();
		tileMapRenderer.getProjectionMatrix().set(camera.combined);

		tileMapRenderer.render(camera);
		
		renderCars();		
		renderInfo();
		//debugRenderer.render(raceWorld.getWorld(), camera.combined);
	}

	private void renderInfo() {
		float left = camera.position.x - (Constants.VIEW_W * 0.5f);
		float rigth = camera.position.x + (Constants.VIEW_W * 0.28f);
		float bottom = camera.position.y - (Constants.VIEW_H * 0.45f);
		float upper = camera.position.y + (Constants.VIEW_H * 0.5f);
		
		float middleLeft = camera.position.x - (Constants.VIEW_W * 0.35f);
		float middle = camera.position.y;
		
		spriteBatch.begin();			
//			font.setColor(Constants.FONT_INFO_R/255f, Constants.FONT_INFO_G/255f, Constants.FONT_INFO_B/255f, 1f);
			font.setScale(0.3f);			
			font.draw(spriteBatch, 
						"Pos: " +raceWorld.getPlayerPosition(), 
						left, 
						upper);
			font.draw(spriteBatch, 
					raceWorld.getPlayerLaps() + "/" + raceWorld.getTotalLaps(), 
					rigth, 
					upper);
			font.draw(spriteBatch, 
					raceWorld.getPlayerSpeed() + "km/h", 
					rigth, 
					bottom);	
			if(!raceWorld.isRaceFinished()){
				font.draw(spriteBatch, 
							" " + (now - firstTime)/1000 + "." + (now - firstTime) % 1000, 
							left, 
							bottom);			
			}
			else{
				font.setScale(1f);
				font.draw(spriteBatch, 
						"Final Position: " + raceWorld.getPlayerPosition(), 
						middleLeft, 
						middle);
			}
		spriteBatch.end();
	}

	private void updateCameraPosition() {
		
		float minX = 0.5f * Constants.VIEW_W;
		float maxX = mapW - minX;
		float camX = MathUtils.clamp(raceWorld.getFocusCarX(), minX, maxX);

		float minY = 0.5f * Constants.VIEW_H;
		float maxY = mapH - minY;
		float camY = MathUtils.clamp(raceWorld.getFocusCarY(), minY, maxY);
				
		camera.position.x = camX;
		camera.position.y = camY;		
		camera.update();
	}

	
	private void renderCars() {
		spriteBatch.setProjectionMatrix(getCamera().combined);
		spriteBatch.begin();
		for(Car car: raceWorld.getCars()){
			drawCarRotated(car.getX(), car.getY(), car.getAngleInDegrees(), car.getBoundingBoxLocalCenter(),car.getId());
		}		
		spriteBatch.end();
	}

	private void drawCarRotated(float coordX, float coordY, float angle, Vector2 carLocalCenter,int carId) {
		carId--;		
		// Centro local da textura em coordenadas do mundo
		float textureCenterX = carTexture[carId].getWidth() / (2 * Constants.PPM);
		float textureCenterY = carTexture[carId].getHeight() / (2 * Constants.PPM);
		// Deslocamento necess�rio para alinhar o centro do carrinho e o centro da textura
		float centerDx = textureCenterX - carLocalCenter.x;
		float centerDy = textureCenterY - carLocalCenter.y;
						
		spriteBatch.draw(carTextureRegion[carId], coordX - centerDx, coordY - centerDy, // the bottom left corner of the box, unrotated
                centerDx, centerDy, // the rotation center relative to the bottom left corner of the box
                (float) carTexture[carId].getWidth() / Constants.PPM, (float) carTexture[carId].getHeight() / Constants.PPM, // the width and height of the box
                1f, 1f, // the scale on the x- and y-axis
                angle);				
	}

	
	public OrthographicCamera getCamera() {
		return camera;
	}

	public void dispose() {		
		spriteBatch.dispose();
		tiledHelper.dispose();
		tileMapRenderer.dispose();
		debugRenderer.dispose();
	}
}
