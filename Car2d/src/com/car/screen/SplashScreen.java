package com.car.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.car.main.AvechadosGame;

public class SplashScreen implements Screen{

    private SpriteBatch spriteBatch;
    private Texture splash;
    private AvechadosGame myGame;
	        
    /**
     * Constructor for the splash screen
     * @param g Game which called this splash screen.
     */
    public SplashScreen(AvechadosGame g){
    	myGame = g;
    }

    @Override
    public void render(float delta)
    {
        if(Gdx.input.justTouched()){
	    	RaceScreen screen = new RaceScreen(myGame);
	    	myGame.setScreen(screen);
        }
    }
	        
    @Override
    public void show()
    {
    	splash = new Texture(Gdx.files.internal("res/car3.jpg"));
        spriteBatch = new SpriteBatch();
        spriteBatch.begin();
        spriteBatch.draw(splash, 0, 0);
        spriteBatch.end();
     
    }

	@Override
	public void dispose() {
		// TODO Auto-generated method stub
		
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
