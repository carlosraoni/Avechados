package com.car.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.Texture;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.g2d.TextureAtlas.AtlasRegion;
import com.badlogic.gdx.scenes.scene2d.Action;
import com.badlogic.gdx.scenes.scene2d.OnActionCompleted;
import com.badlogic.gdx.scenes.scene2d.Stage;
import com.badlogic.gdx.scenes.scene2d.actions.Delay;
import com.badlogic.gdx.scenes.scene2d.actions.FadeIn;
import com.badlogic.gdx.scenes.scene2d.actions.FadeOut;
import com.badlogic.gdx.scenes.scene2d.actions.Sequence;
import com.badlogic.gdx.scenes.scene2d.ui.Image;
import com.badlogic.gdx.utils.Scaling;
import com.car.main.AvechadosGame;
import com.car.utils.Constants;

public class SplashScreen implements Screen{

    private Image splashImage;
    private AvechadosGame game;
    private Stage stage;
    
    //Podem ser qualque interiro positivo
    private int width = Constants.TARGET_RES_W;
    private int height = Constants.TARGET_RES_H;
    
    // the fixed viewport dimensions (ratio: 1.6)
            
    /**
     * Constructor for the splash screen
     * @param g Game which called this splash screen.
     */
    public SplashScreen(AvechadosGame g){
    	game = g;
    	
        this.stage = new Stage( width, height, true );
    }
    
    protected boolean isGameScreen()
    {
        return false;
    }

    @Override
    public void render(float delta)
    {
        // update the actors
        stage.act( delta );

        // clear the screen with the given RGB color (black)
        Gdx.gl.glClearColor( 0f, 0f, 0f, 1f );
        Gdx.gl.glClear( GL20.GL_COLOR_BUFFER_BIT );

        // draw the actors
        stage.draw();
    }
	        

    public void show()
    {

        // here we create the splash image actor; its size is set when the
        // resize() method gets called
        splashImage = new Image( new Texture(Gdx.files.internal("res/car3.jpg")), Scaling.stretch );
        splashImage.width = width;
        splashImage.height = height;

        // this is needed for the fade-in effect to work correctly; we're just
        // making the image completely transparent
        splashImage.color.a = 0f;

        // configure the fade-in/out effect on the splash image
        Sequence actions = Sequence.$( FadeIn.$( 0.75f ), Delay.$( FadeOut.$( 0.75f ), 1.75f ) );
        actions.setCompletionListener( new OnActionCompleted() {
            @Override
            public void completed(
                Action action )
            {
                // when the image is faded out, move on to the next screen
                game.setScreen( new MenuScreen(game) );
            }
        } );
        splashImage.action( actions );

        // and finally we add the actor to the stage
        stage.addActor( splashImage );
    }


	@Override
	public void dispose() {		
		stage.dispose();		
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
