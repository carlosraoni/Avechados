package com.avechados.main;

import com.avechados.model.Car;
import com.avechados.utils.Constants;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class AvechadosApplicationListener implements ApplicationListener{

	private SpriteBatch carSprite;
	private Car player;
	private Texture carTexture;
	
	/**
	 * The time the last frame was rendered, used for throttling framerate
	 */
	private long lastRender;

	private TiledMapHelper tiledMapHelper;

	/**
	 * The screen coordinates of where a drag event began, used when updating
	 * the camera position.
	 */
	private int lastTouchedX;
	private int lastTouchedY;

	/**
	 * The screen's width and height. This may not match that computed by
	 * libgdx's gdx.graphics.getWidth() / getHeight() on devices that make use
	 * of on-screen menu buttons.
	 */
	private int screenWidth, mapWidth;
	private int screenHeight, mapHeight;

	public AvechadosApplicationListener() {
		super();

		// Defer until create() when Gdx is initialized.
		screenWidth = -1;
		screenHeight = -1;
	}

	public AvechadosApplicationListener(int width, int height) {
		super();

		screenWidth = width;
		screenHeight = height;
	}
	
	@Override
	public void create() {
		carTexture = new Texture(Gdx.files.internal("res/carro.png"));
		carSprite = new SpriteBatch();
		/**
		 * If the viewport's size is not yet known, determine it here.
		 */
		if (screenWidth == -1) {
			screenWidth = Gdx.graphics.getWidth();
			screenHeight = Gdx.graphics.getHeight();
		}

		tiledMapHelper = new TiledMapHelper();
		tiledMapHelper.setPackerDirectory("res");
		tiledMapHelper.loadMap("res/level.tmx");
		tiledMapHelper.prepareCamera(screenWidth, screenHeight);
		
		lastRender = System.nanoTime();
		
		mapWidth = tiledMapHelper.getWidth();
		mapHeight = tiledMapHelper.getHeight();
		
		player = new Car(
				Constants.INITIAL_MAX_SPEED_PLAYER, 
				Constants.INITIAL_ACCELERATION_PLAYER, 
				mapWidth/2, mapHeight/2, mapWidth, mapHeight);		
	}

	@Override
	public void resume() {
	}

	@Override
	public void render() {
		long now = System.nanoTime();

		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

		updateCarPosition();
		updateCameraPosition();
				
		renderMap();
		renderCar();
		
		now = System.nanoTime();
		if (now - lastRender < 30000000) { // 30 ms, ~33FPS
			try {
				Thread.sleep(30 - (now - lastRender) / 1000000);
			} catch (InterruptedException e) {
			}
		}

		lastRender = now;		
	}

	private void renderMap() {
		tiledMapHelper.render();
	}

	private void renderCar() {
		float coordXCarFromCamera = screenWidth / 2;
		float coordYCarFromCamera = screenHeight / 2;
		
		if (player.getPosX() < screenWidth / 2) {
			coordXCarFromCamera -= ((screenWidth / 2) - player.getPosX());
		}
		if (player.getPosX() >= mapWidth - screenWidth / 2) {
			coordXCarFromCamera += player.getPosX() - (mapWidth - (screenWidth / 2));
		}

		if (player.getPosY() < screenHeight / 2) {
			coordYCarFromCamera -= ((screenHeight / 2) - player.getPosY());
		}
		if (player.getPosY() >= mapHeight - screenHeight / 2) {
			coordYCarFromCamera += player.getPosY() - (mapHeight - (screenHeight / 2));
		}
		
		drawCarRotated(coordXCarFromCamera, coordYCarFromCamera, player.getAngle());
	}

	private void drawCarRotated(float coordXCarFromCamera, float coordYCarFromCamera, float angle) {
		carSprite.begin();
		carSprite.draw(
				carTexture, 
				coordXCarFromCamera,
				coordYCarFromCamera, 
				carTexture.getWidth() / 2, 
				carTexture.getHeight() / 2,
				carTexture.getWidth(), 
				carTexture.getHeight(),
				1.0f, 
				0.9f, 
				angle,
				0, 
				0, 
				carTexture.getWidth(), 
				carTexture.getHeight(),
				false,
				false
				);	
		carSprite.end();
	}

	private void updateCameraPosition() {
		/**
		 * Ensure that the camera is only showing the map, nothing outside.
		 */		
		float camX = player.getPosX();
		if (camX < screenWidth / 2) {
			camX = screenWidth / 2;
		}
		if (camX >= mapWidth - screenWidth / 2) {
			camX = mapWidth - (screenWidth / 2);
		}

		//float camY = carY + (screenHeight / 2);
		float camY = player.getPosY();
		if (camY < screenHeight / 2) {
			camY = screenHeight / 2;
		}
		if (camY >= mapHeight - screenHeight / 2) {
			camY = mapHeight - screenHeight / 2;
		}
		tiledMapHelper.getCamera().position.x = camX;
		tiledMapHelper.getCamera().position.y = camY;
		
		tiledMapHelper.getCamera().update();
	}

	private void updateCarPosition() {
		if(Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN)){
			player.decreaseSpeed();			
		} 
		if(Gdx.input.isKeyPressed(Input.Keys.DPAD_UP)){
			//			
		}
		if(Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT)){
			player.turnRight();			
		} 
		if(Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT)){			
			player.turnLeft();
		}

		player.update();
	}

	@Override
	public void resize(int width, int height) {
		/**
		 * Exercise for the reader: implement resizing?
		 */
	}

	@Override
	public void pause() {
	}

	@Override
	public void dispose() {
	}
}
