package com.car.main;

import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.audio.Music;
import com.car.screen.RaceScreen;
import com.car.screen.SplashScreen;

public class AvechadosGame extends Game implements ApplicationListener{
		
		private Music music;
		private SplashScreen splashScreen;
		private RaceScreen raceScreen;
		
		public static final String LOG = AvechadosGame.class.getSimpleName();

		
		public AvechadosGame() {
			super();
		}

		public AvechadosGame(int width, int height) {
			super();
		}
		
		@Override
		public void create() {	
			music = Gdx.audio.newMusic(Gdx.files.internal("res/racerx.mp3"));			
			music.setLooping(true);
			music.play();

			splashScreen = new SplashScreen(this);
			setScreen(splashScreen);
		}

		@Override
		public void resume() {
		}

		@Override
		public void render() {
			super.render();		
		}
		
		@Override
		public void resize(int width, int height) {
		}

		@Override
		public void pause() {
			super.pause();
		}

		@Override
		public void dispose() {			
			music.dispose();
//			raceScreen.dispose();
//			splashScreen.dispose();
			getScreen().dispose();
		}

		public SplashScreen getSplashScreen() {
			return splashScreen;
		}

		public RaceScreen getRaceScreen() {
			return raceScreen;
		}
		
	}

