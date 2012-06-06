package com.car.main;

import com.badlogic.gdx.backends.jogl.JoglApplication;

public class AvechadosDesktopStarter {
	public static void main(String [] args){
		new JoglApplication(new AvechadosGame(), "Teste", 240, 320, false);		
	}
}
