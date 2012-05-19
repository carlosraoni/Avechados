package com.avechados.main;

import com.badlogic.gdx.backends.jogl.JoglApplication;

public class AvechadosDesktopStarter {
	public static void main(String [] args){
		new JoglApplication(new AvechadosApplicationListener(), "Teste", 320, 240, false);		
	}
}
