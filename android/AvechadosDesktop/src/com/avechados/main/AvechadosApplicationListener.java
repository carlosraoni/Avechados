package com.avechados.main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.Sprite;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;

public class AvechadosApplicationListener implements ApplicationListener{

	private SpriteBatch car;
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
	private int screenWidth;
	private int screenHeight;

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

	float carX, carY;
	
	@Override
	public void create() {
		carTexture = new Texture(Gdx.files.internal("res/carro.png"));
		car = new SpriteBatch();
		
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
		
		carX = screenWidth/2;
		carY = screenHeight/2;
	}

	@Override
	public void resume() {
	}

	@Override
	public void render() {
		long now = System.nanoTime();

		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);

//		if (Gdx.input.justTouched()) {
//			lastTouchedX = Gdx.input.getX();
//			lastTouchedY = Gdx.input.getY();
//		} else if (Gdx.input.isTouched()) {
//			tiledMapHelper.getCamera().position.x += lastTouchedX
//					- Gdx.input.getX();
//
//			/**
//			 * Camera y is opposite of Gdx.input y, so the subtraction is
//			 * swapped.
//			 */
//			tiledMapHelper.getCamera().position.y += Gdx.input.getY()
//					- lastTouchedY;
//
//			carX = tiledMapHelper.getCamera().position.x;
//			carY = tiledMapHelper.getCamera().position.y;
//			
//			lastTouchedX = Gdx.input.getX();
//			lastTouchedY = Gdx.input.getY();
//		} else 
		if(Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN)){
			//carY -= 5.0;
			tiledMapHelper.getCamera().position.y -= 5.0;
		} else if(Gdx.input.isKeyPressed(Input.Keys.DPAD_UP)){
			//carY += 5.0;
			tiledMapHelper.getCamera().position.y += 5.0;
		} else if(Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT)){
			//carX += 5.0;
			tiledMapHelper.getCamera().position.x += 5.0;
		} else if(Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT)){
			//carX -= 5.0;
			tiledMapHelper.getCamera().position.x -= 5.0;
		}
				
		/**
		 * Ensure that the camera is only showing the map, nothing outside.
		 */
		if (tiledMapHelper.getCamera().position.x < screenWidth / 2) {
			tiledMapHelper.getCamera().position.x = screenWidth / 2;
		}
		if (tiledMapHelper.getCamera().position.x >= tiledMapHelper.getWidth()
				- screenWidth / 2) {
			tiledMapHelper.getCamera().position.x = tiledMapHelper.getWidth()
					- screenWidth / 2;
		}

		if (tiledMapHelper.getCamera().position.y < screenHeight / 2) {
			tiledMapHelper.getCamera().position.y = screenHeight / 2;
		}
		if (tiledMapHelper.getCamera().position.y >= tiledMapHelper.getHeight()
				- screenHeight / 2) {
			tiledMapHelper.getCamera().position.y = tiledMapHelper.getHeight()
					- screenHeight / 2;
		}

		System.out.println(
				"CarX: " + carX + 
				", CarY: " + carY + 
				", cX: " + tiledMapHelper.getCamera().position.x +
				", cY: " + tiledMapHelper.getCamera().position.y);
		
		tiledMapHelper.getCamera().update();

		tiledMapHelper.render();

		now = System.nanoTime();
		if (now - lastRender < 30000000) { // 30 ms, ~33FPS
			try {
				Thread.sleep(30 - (now - lastRender) / 1000000);
			} catch (InterruptedException e) {
			}
		}

		lastRender = now;
		car.begin();
		car.draw(carTexture, carX, carY);
		car.end();
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
