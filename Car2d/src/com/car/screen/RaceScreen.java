package com.car.screen;

import java.util.BitSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.badlogic.gdx.Screen;
import com.car.graphics.RaceRenderer;
import com.car.main.AvechadosGame;
import com.car.model.Car;
import com.car.model.Race;
import com.car.utils.Constants;
import com.car.utils.Controls;
import com.car.utils.TiledMapHelper;

public class RaceScreen implements Screen{
	
	private long firstTime;
	private long lastRender;
	private TiledMapHelper tiledMapHelper;
	private Race race;
	private RaceRenderer raceRenderer;
	private int screenPixelWidth;
	private int screenPixelHeight;
	private AvechadosGame myGame;
	private String raceMap;
	private boolean disposed = false;      
	
    /**
     * Constructor for the splash screen
     * @param g Game which called this splash screen.
     */
    public RaceScreen(AvechadosGame g, String raceMap){
    	this.raceMap = raceMap;    	
    	this.myGame = g;
		screenPixelWidth = -1;
		screenPixelHeight = -1;
		firstTime = System.nanoTime();		
    }

    @Override
    public void render(float delta)
    {
    	long now = System.nanoTime();

		float targetFPS = 30;
    	float timeStep = (1 / targetFPS);
    	int iterations = 100;
    	
    	if(race.isRaceFinished() && Gdx.input.justTouched()){
    		myGame.setScreen(new MenuScreen(myGame));
    	}
    	
    	if(!race.isRaceFinished() && isStartTimeElapsed()){
    		race.update(timeStep, iterations, iterations, getPlayerControls());
    	}
		raceRenderer.render();
		
				
		now = System.nanoTime();
		if (now - lastRender < 30000000) { // 30 ms, ~33FPS
			try {
				Thread.sleep(30 - (now - lastRender) / 1000000);
			} catch (InterruptedException e) {
			}
		}
		
	   lastRender = now;
    }

//	private static final float TICK = 1 / 60f;
//	private static final int COLLISION_SOLVER_ITERATIONS = 1;
//	private float accumulator = 0;
//	
//	@Override
//	public void render(float delta){
//	
//		long now = System.nanoTime();
//		
//
//		if(!race.isRaceFinished() && isStartTimeElapsed()){			
//			accumulator += Gdx.graphics.getDeltaTime();
//			while (accumulator > TICK) {
//				race.update(TICK, COLLISION_SOLVER_ITERATIONS, COLLISION_SOLVER_ITERATIONS, getPlayerControls());
//				accumulator -= TICK;
//			}
//		}
//		
//		raceRenderer.render();
//		lastRender = now;
//	}
    
	public boolean isStartTimeElapsed() {
		return ((lastRender - firstTime) / 1000000000l) >= Constants.RACE_START_TIME_SECONDS;
	}

	private BitSet getPlayerControls() {
		// Uso de bitSet para interpretação dos controles devido a necessidade de realizar mais de um controle
		// no carro no mesmo passo da simulação física
		BitSet controls = new BitSet();

		
		controls.set(Controls.TDC_DOWN.ordinal(), Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN));
		controls.set(Controls.TDC_UP.ordinal(), !Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN));
//		controls.set(Controls.TDC_UP.ordinal(), Gdx.input.isKeyPressed(Input.Keys.DPAD_UP));
		controls.set(Controls.TDC_RIGHT.ordinal(), Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT));
		controls.set(Controls.TDC_LEFT.ordinal(), Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT));
		
		if(Gdx.input.isTouched()){
//			System.out.println("X: " + Gdx.input.getX() + ", Y: " + Gdx.input.getY());
			if(Gdx.input.getX() > Constants.TARGET_RES_W / 2){
				controls.set(Controls.TDC_RIGHT.ordinal(), true);
			}
			else{
				controls.set(Controls.TDC_LEFT.ordinal(), true);
			}
		}

		
		return controls;			
	}
    
	
	
    @Override
    public void show(){
		if (screenPixelWidth == -1) {
			screenPixelWidth = Gdx.graphics.getWidth();
			screenPixelHeight = Gdx.graphics.getHeight();
		}

		//tiledMapHelper = new TiledMapHelper("res/NatalArenaLimits.tmx", "res");
//		tiledMapHelper = new TiledMapHelper("res/caveira.tmx", "res");		
		tiledMapHelper = new TiledMapHelper(raceMap, "res");
		race = new Race(tiledMapHelper);
		raceRenderer = new RaceRenderer(race, tiledMapHelper, screenPixelWidth, screenPixelHeight);
		
		System.gc();
		
		lastRender = System.nanoTime();	
    }

	@Override
	public void dispose() {
		// TODO Auto-generated method stub		
		raceRenderer.dispose();
	}

	@Override
	public void hide() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void pause() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resize(int arg0, int arg1) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void resume() {
		// TODO Auto-generated method stub
		
	}
	
}
