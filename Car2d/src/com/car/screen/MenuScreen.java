package com.car.screen;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.scenes.scene2d.Actor;
import com.badlogic.gdx.scenes.scene2d.ui.ClickListener;
import com.badlogic.gdx.scenes.scene2d.ui.Skin;
import com.badlogic.gdx.scenes.scene2d.ui.TextButton;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.Table;
import com.badlogic.gdx.scenes.scene2d.ui.tablelayout.TableLayout;
import com.car.main.AvechadosGame;

public class MenuScreen extends AbstractScreen {

	private Table table;
	
	public MenuScreen(AvechadosGame game) {
		super(game);
		
	}
	
    @Override
    public void show()
    {
        super.show();

        // retrieve the custom skin for our 2D widgets
        Skin skin = super.getSkin();

        // create the table actor and add it to the stage
        table = new Table( skin );
        table.width = stage.width();
        table.height = stage.height();
        stage.addActor( table );

        // retrieve the table's layout
        TableLayout layout = table.getTableLayout();

        TextButton cleanRaceButton = new TextButton( "Clean Race", skin );
        cleanRaceButton.setClickListener( new ClickListener() {
            @Override
            public void click(
                Actor actor,
                float x,
                float y )
            {
                
                game.setScreen( new RaceScreen( game,"res/CleanRace.tmx" ) );
            }
        } );
        layout.register( "cleanRaceButton", cleanRaceButton );

        TextButton caveiraButton = new TextButton( "Caveira", skin );
        caveiraButton.setClickListener( new ClickListener() {
            @Override
            public void click(
                Actor actor,
                float x,
                float y )
            {
                game.setScreen( new RaceScreen( game, "res/caveira.tmx" )  );
            }
        } );
        layout.register( "caveiraButton", caveiraButton );

        // finally, parse the layout descriptor
        layout.parse( Gdx.files.internal( "res/layout-descriptors/menu-screen.txt" ).readString() );
    }

	
}
