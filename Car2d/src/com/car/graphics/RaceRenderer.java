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
	private int screenPixelWidth, screenPixelHeight;
	
	private OrthographicCamera raceCamera;
	private OrthographicCamera infoCamera;
	
	private long firstTime;
	private long now;
	
	public RaceRenderer(Race race, TiledMapHelper tiledHelper, int screenPixelWidth, int screenPixelHeight){
		this.raceWorld = race;
		this.tiledHelper = tiledHelper;
		this.screenPixelWidth = screenPixelWidth;
		this.screenPixelHeight = screenPixelHeight;
		
		// setup map renderer
		float unitsPerTileX = tiledHelper.getWorldUnitsPerTileX();
		float unitsPerTileY = tiledHelper.getWorldUnitsPerTileY();
		tileMapRenderer = new TileMapRenderer(
				tiledHelper.getMap(), tiledHelper.getTileAtlas(), 16, 16, unitsPerTileX, unitsPerTileY);
		
		mapW = tiledHelper.getWorldMapWidth();
		mapH = tiledHelper.getWorldMapHeight();
		
		prepareRaceCamera(Constants.VIEW_W, Constants.VIEW_H);
		prepareInfoCamera(screenPixelWidth, screenPixelHeight);
		
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
		FileHandle fontFile = Gdx.files.internal("res/fonts/courierNew.fnt");
		FileHandle imageFontFile = Gdx.files.internal("res/fonts/courierNew.png");
		font = new BitmapFont(fontFile, imageFontFile, false);
		
		this.firstTime = System.currentTimeMillis();
	}
	
	private void prepareInfoCamera(int screenPixelWidth, int screenPixelHeight) {
		infoCamera = new OrthographicCamera(screenPixelWidth, screenPixelHeight);
		infoCamera.position.set(0, 0, 0);		
	}

	private void prepareRaceCamera(float viewW, float viewH) {
		raceCamera = new OrthographicCamera(viewW, viewH);
		raceCamera.position.set(0, 0, 0);
	}
	
	/**
	 * Renders the part of the map that should be visible to the user.
	 */
	public void render() {
		this.now = System.currentTimeMillis();
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		updateCameraPosition();
		tileMapRenderer.getProjectionMatrix().set(raceCamera.combined);

		tileMapRenderer.render(raceCamera);
		
		renderCars();		
		renderInfo();
		//debugRenderer.render(raceWorld.getWorld(), camera.combined);
	}

	private void renderInfo() {		
		float left = -screenPixelWidth / 2;
		float rigth = screenPixelWidth / 4;
		float bottom = -screenPixelHeight / 2.2f;
		float upper = screenPixelHeight / 2;
		
		float leftMiddle = -screenPixelWidth / 4;
		float middle = 0;
		
		long renderTime = (raceWorld.isRaceFinished()) ? raceWorld.getRaceFinishTime(): now;
		int playerPosition = raceWorld.getPlayerPosition();
		
		spriteBatch.setProjectionMatrix(infoCamera.combined);
		spriteBatch.begin();			
			font.setScale(1f);						
			font.draw(spriteBatch, 
						playerPosition + getPositionSuffix(playerPosition), 
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
			font.draw(spriteBatch, 
						" " + (renderTime - firstTime)/1000 + "." + (renderTime - firstTime) % 1000, 
						left, 
						bottom);						
			if(raceWorld.isRaceFinished()){
				font.setScale(1f);
				font.draw(spriteBatch, 
						getFinalResultMessage(playerPosition), 
						leftMiddle, 
						middle);
			}
		spriteBatch.end();
	}

	private String getFinalResultMessage(int playerPosition) {
		switch(playerPosition){
		case 1: return "Congratulations!"; 
		case 2: return "Great Job!"; 
		case 3: return "Good Job!"; 		
		}
		return "Barrichelo!";
	}

	private String getPositionSuffix(int position) {
		switch(position){
		case 1: return "st"; 
		case 2: return "nd"; 
		case 3: return "rd"; 		
		}
		return "th";
	}

	private void updateCameraPosition() {
		
		float minX = 0.5f * Constants.VIEW_W;
		float maxX = mapW - minX;
		float camX = MathUtils.clamp(raceWorld.getFocusCarX(), minX, maxX);

		float minY = 0.5f * Constants.VIEW_H;
		float maxY = mapH - minY;
		float camY = MathUtils.clamp(raceWorld.getFocusCarY(), minY, maxY);
				
		raceCamera.position.x = camX;
		raceCamera.position.y = camY;		
		raceCamera.update();
	}

	
	private void renderCars() {
		spriteBatch.setProjectionMatrix(getRaceCamera().combined);
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

	
	public OrthographicCamera getRaceCamera() {
		return raceCamera;
	}

	public void dispose() {		
		spriteBatch.dispose();
		tiledHelper.dispose();
		tileMapRenderer.dispose();
		debugRenderer.dispose();
	}
}
