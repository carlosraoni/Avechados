package com.car.main;

import com.badlogic.gdx.backends.jogl.JoglApplication;
import com.car.utils.Constants;

public class AvechadosDesktopStarter {
	public static void main(String [] args){
		new JoglApplication(new AvechadosGame(), "Teste", Constants.TARGET_RES_W, Constants.TARGET_RES_H, false);		
	}
}
