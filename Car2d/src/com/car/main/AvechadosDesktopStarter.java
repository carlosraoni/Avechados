package com.car.main;

import com.badlogic.gdx.backends.jogl.JoglApplication;

public class AvechadosDesktopStarter {
	public static void main(String [] args){
		new JoglApplication(new AvechadosGame(), "Teste", 640, 480, false);		
	}
}
