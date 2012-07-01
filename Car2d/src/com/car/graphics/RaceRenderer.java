package com.car.graphics;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.GL10;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureRegion;
import com.badlogic.gdx.graphics.g2d.tiled.TileMapRenderer;
import com.badlogic.gdx.math.MathUtils;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.car.model.Race;
import com.car.utils.Constants;
import com.car.utils.TiledMapHelper;

public class RaceRenderer {	
	private Race raceWorld;
	private TiledMapHelper tiledHelper;
	
	private SpriteBatch carSprite;
	private Texture carTexture;
	private TextureRegion carTextureRegion;
	
	private static final int[] layersList = { 0 };	
	private TileMapRenderer tileMapRenderer;
	
	private long lastRender;
	private Box2DDebugRenderer debugRenderer;
	
	private float mapW, mapH;
			
	private OrthographicCamera camera;
	
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
		
		carTexture = new Texture(Gdx.files.internal("res/carro90.png"));
		carTextureRegion = new TextureRegion(carTexture);
		carSprite = new SpriteBatch();
	}
	
	private void prepareCamera(float viewW, float viewH) {
		camera = new OrthographicCamera(viewW, viewH);
		camera.position.set(0, 0, 0);
	}
	
	/**
	 * Renders the part of the map that should be visible to the user.
	 */
	public void render() {
		Gdx.gl.glClear(GL10.GL_COLOR_BUFFER_BIT);
		
		updateCameraPosition();
		tileMapRenderer.getProjectionMatrix().set(camera.combined);

//		Vector3 tmp = new Vector3();
//		tmp.set(0, 0, 0);
//		camera.unproject(tmp);

//		tileMapRenderer.render((int) tmp.x, (int) tmp.y,
//				Gdx.graphics.getWidth(), Gdx.graphics.getHeight(), layersList);

		tileMapRenderer.render(camera);
		renderCar();
		debugRenderer.render(raceWorld.getWorld(), camera.combined);
	}

	private void updateCameraPosition() {
		
		float minX = 0.5f * Constants.VIEW_W;
		float maxX = mapW - minX;
		float camX = MathUtils.clamp(raceWorld.getPlayerX(), minX, maxX);

		float minY = 0.5f * Constants.VIEW_H;
		float maxY = mapH - minY;
		float camY = MathUtils.clamp(raceWorld.getPlayerY(), minY, maxY);
				
		camera.position.x = camX;
		camera.position.y = camY;		
		camera.update();
	}

	
	private void renderCar() {
		drawCarRotated(raceWorld.getPlayerX(), raceWorld.getPlayerY(), raceWorld.getPlayerAngleInDegrees());		
	}

	private void drawCarRotated(float coordX, float coordY, float angle) {
		// Centro local do jogador
		Vector2 playerCenter = raceWorld.getBoundingBoxPlayerCenter();
		// Centro local da textura em coordenadas do mundo
		float textureCenterX = carTexture.getWidth() / (2 * Constants.PPM);
		float textureCenterY = carTexture.getHeight() / (2 * Constants.PPM);
		// Deslocamento necess�rio para alinhar o centro do carrinho e o centro da textura
		float centerDx = textureCenterX - playerCenter.x;
		float centerDy = textureCenterY - playerCenter.y;
				
		carSprite.setProjectionMatrix(getCamera().combined);
		carSprite.begin();
		carSprite.draw(carTextureRegion, coordX - centerDx, coordY - centerDy, // the bottom left corner of the box, unrotated
                centerDx, centerDy, // the rotation center relative to the bottom left corner of the box
                (float) carTexture.getWidth() / Constants.PPM, (float) carTexture.getHeight() / Constants.PPM, // the width and height of the box
                1f, 1f, // the scale on the x- and y-axis
                angle);		
		carSprite.end();
	}

	
	public OrthographicCamera getCamera() {
		return camera;
	}
}