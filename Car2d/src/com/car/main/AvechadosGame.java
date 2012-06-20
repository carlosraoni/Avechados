package com.car.main;

import java.util.BitSet;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.car.graphics.RaceRenderer;
import com.car.model.Race;
import com.car.utils.Controls;
import com.car.utils.TiledMapHelper;

public class AvechadosGame implements ApplicationListener{
		
		private long lastRender;
		private TiledMapHelper tiledMapHelper;
		private Race race;
		private RaceRenderer raceRenderer;

		private int screenPixelWidth;
		private int screenPixelHeight;
		
		public AvechadosGame() {
			super();

			// Defer until create() when Gdx is initialized.
			screenPixelWidth = -1;
			screenPixelHeight = -1;
		}

		public AvechadosGame(int width, int height) {
			super();

			screenPixelWidth = width;
			screenPixelHeight = height;
		}
		
		@Override
		public void create() {									
			if (screenPixelWidth == -1) {
				screenPixelWidth = Gdx.graphics.getWidth();
				screenPixelHeight = Gdx.graphics.getHeight();
			}
			
			//tiledMapHelper = new TiledMapHelper("res/NatalArena.tmx", "res");
			tiledMapHelper = new TiledMapHelper("res/NatalArenaLimits.tmx", "res");
			race = new Race(tiledMapHelper);
			raceRenderer = new RaceRenderer(race, tiledMapHelper, screenPixelWidth, screenPixelHeight);
			
			lastRender = System.nanoTime();						
		}

		@Override
		public void resume() {
		}

		@Override
		public void render() {
			long now = System.nanoTime();

			float targetFPS = 30;
	    	float timeStep = (1 / targetFPS);
	    	int iterations = 1;

	    	race.update(timeStep, iterations, iterations, getPlayerControls());
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
		
		private BitSet getPlayerControls() {
			// Uso de bitSet para interpretação dos controles devido a necessidade de realizar mais de um controle
			// no carro no mesmo passo da simulação física
			BitSet controls = new BitSet();
			
			controls.set(Controls.TDC_DOWN.ordinal(), Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN));
			controls.set(Controls.TDC_UP.ordinal(), Gdx.input.isKeyPressed(Input.Keys.DPAD_UP));
			controls.set(Controls.TDC_RIGHT.ordinal(), Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT));
			controls.set(Controls.TDC_LEFT.ordinal(), Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT));
			
			return controls;			
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

