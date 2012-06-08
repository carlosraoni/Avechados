package com.car.main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.car.model.Car;
import com.car.utils.Constants;
import com.car.utils.Controls;
import com.car.utils.TiledMapHelper;

public class AvechadosGame implements ApplicationListener{

		private SpriteBatch carSprite;
		private Car player;
		private Texture carTexture;
		private World world;
		
		/**
		 * The time the last frame was rendered, used for throttling framerate
		 */
		private long lastRender;
		private Box2DDebugRenderer debugRenderer;
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

		private OrthographicCamera cameraDebugRenderer;
		
		public AvechadosGame() {
			super();

			// Defer until create() when Gdx is initialized.
			screenWidth = -1;
			screenHeight = -1;
		}

		public AvechadosGame(int width, int height) {
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

			cameraDebugRenderer = new OrthographicCamera(4 * screenWidth / (Constants.PIXELS_PER_METER) , 4 * screenHeight  / (Constants.PIXELS_PER_METER));
			
			tiledMapHelper = new TiledMapHelper();
			tiledMapHelper.setPackerDirectory("res");
			tiledMapHelper.loadMap("res/NatalArena.tmx");
			tiledMapHelper.prepareCamera(screenWidth, screenHeight);
			
			lastRender = System.nanoTime();
			
			mapWidth = tiledMapHelper.getWidth();
			mapHeight = tiledMapHelper.getHeight();
			
			int posIniX = mapWidth/2;
			int posIniY = mapHeight/2;
			try {
				int startPlayerColumn = Integer.parseInt(tiledMapHelper.getMapProperty(Constants.START_PLAYER_COLUMN_KEY));
				int startPlayerLine = tiledMapHelper.getNumRows() - Integer.parseInt(tiledMapHelper.getMapProperty(Constants.START_PLAYER_ROW_KEY)) - 1;
				
				int tileWidth = tiledMapHelper.getMap().tileWidth;
				int tileHeight = tiledMapHelper.getMap().tileHeight;
				
				posIniX = (startPlayerColumn * tileWidth);
				posIniY = (startPlayerLine * tileHeight);
				
				System.out.println("nc: " + tiledMapHelper.getNumCols() + ", nr: " + tiledMapHelper.getNumRows());
				System.out.println("tw: " + tileWidth + ", th: " + tileHeight);
				System.out.println("spc: " + startPlayerColumn + ", spl, " + startPlayerLine);
				System.out.println("px: " + posIniX + ", py: " + posIniY);
				
			} catch (NumberFormatException e) {
				System.out.println("Erro parseando inteiro de posição inicial no mapa: " + e.getMessage());
				e.printStackTrace();
			}
			
			
			world = new World(new Vector2(0, 0), false);
			//player = new Car(world, posIniX, posIniY);
			player = new Car(world, 0, 0);
			System.out.println("Px: " + player.getBody().getPosition().x + ", Py: " + player.getBody().getPosition().y);
//			player.setXCoordFromXPixelCoord(posIniX);
//			player.setYCoordFromYPixelCoord(posIniY);
//			player.getBody().getPosition().set(posIniX, posIniY);
			System.out.println("Px: " + player.getBody().getPosition().x + ", Py: " + player.getBody().getPosition().y);
			
			debugRenderer = new Box2DDebugRenderer();
/*			player = new Car(
					Constants.INITIAL_MAX_SPEED_PLAYER, 
					Constants.INITIAL_ACCELERATION_PLAYER, 
					posIniX, posIniY, mapWidth, mapHeight);		*/
		}

		@Override
		public void resume() {
		}

		@Override
		public void render() {
			long now = System.nanoTime();

			Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
			
			updateCarPosition();
			

			float targetFPS = 30;
	    	float timeStep = (1 / targetFPS);
	    	int iterations = 1;
			world.step(timeStep, iterations, iterations);
			
			//cameraDebugRenderer.position.set(player.getBody().getPosition().x, player.getBody().getPosition().y, 0);
			cameraDebugRenderer.position.set(0, 0, 0);
			cameraDebugRenderer.update();
			cameraDebugRenderer.apply(Gdx.gl10);						
			
			//updateCameraPosition();			
			
			//renderMap();
			//renderCar();			
	    		    	
	    	//debugRenderer.render(world, tiledMapHelper.getCamera().combined);
			debugRenderer.render(world, cameraDebugRenderer.combined);
			
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
			
			if (player.getXPixelCoord() < screenWidth / 2) {
				coordXCarFromCamera -= ((screenWidth / 2) - player.getXPixelCoord());
			}
			if (player.getXPixelCoord() >= mapWidth - screenWidth / 2) {
				coordXCarFromCamera += player.getXPixelCoord() - (mapWidth - (screenWidth / 2));
			}

			if (player.getYPixelCoord() < screenHeight / 2) {
				coordYCarFromCamera -= ((screenHeight / 2) - player.getYPixelCoord());
			}
			if (player.getYPixelCoord() >= mapHeight - screenHeight / 2) {
				coordYCarFromCamera += player.getYPixelCoord() - (mapHeight - (screenHeight / 2));
			}
			
			drawCarRotated(coordXCarFromCamera, coordYCarFromCamera, player.getBody().getAngle());
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
		
			float camX = player.getXPixelCoord();
			if (camX < screenWidth / 2) {
				camX = screenWidth / 2;
			}
			if (camX >= mapWidth - screenWidth / 2) {
				camX = mapWidth - (screenWidth / 2);
			}

			//float camY = carY + (screenHeight / 2);
			float camY = player.getYPixelCoord();
			if (camY < screenHeight / 2) {
				camY = screenHeight / 2;
			}
			if (camY >= mapHeight - screenHeight / 2) {
				camY = mapHeight - screenHeight / 2;
			}
			
			System.out.println("CarX: " + player.getXPixelCoord() + ", CarY: " + player.getYPixelCoord() + ", camX: " + camX + ", camY: " + camY);
			
			tiledMapHelper.getCamera().position.x = camX;
			tiledMapHelper.getCamera().position.y = camY;
			
			tiledMapHelper.getCamera().update();
		}

		private void updateCarPosition() {
			if(Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN)){				
				player.update(true, Controls.TDC_DOWN);			
			} 
			else if(Gdx.input.isKeyPressed(Input.Keys.DPAD_UP)){				
				player.update(true, Controls.TDC_UP);			
			}
			else if(Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT)){
				player.update(true, Controls.TDC_RIGHT);
			} 
			else if(Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT)){			
				player.update(true, Controls.TDC_LEFT);
			} 
			else {
				player.update(false, null);
			}
			
			System.out.println("X: " + player.getBody().getPosition().x + ", Y: " + player.getBody().getPosition().y);
						
		}
		
/*		private void updateCarPosition() {
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
			float accelerometerX = Gdx.input.getAccelerometerX();
			float accelerometerY = Gdx.input.getAccelerometerY();
			if(accelerometerX < -3.0f){
				player.slowTurnRight();
			}
			else if(accelerometerX > 3.0f){
				player.slowTurnLeft();
			}
			if(accelerometerY > 5.0f){
				player.slowDecreaseSpeed();
			}
			player.update();
		}*/

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

