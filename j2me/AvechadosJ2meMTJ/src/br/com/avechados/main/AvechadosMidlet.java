package br.com.avechados.main;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Image;
import javax.microedition.midlet.MIDlet;

import br.com.avechados.game.Game;
import br.com.avechados.main.splash.SplashScreen;


public class AvechadosMidlet extends MIDlet {

	public Display display;	
	private Image splashLogo;
	private boolean isSplash = true;
	private MenuPrincipal mainMenuScreen;
	
	public void startApp(){
		try{
			display = Display.getDisplay(this);
			mainMenuScreen = new MenuPrincipal(this); //INICIO
			if (isSplash){
				isSplash = false;
				try{
					splashLogo = Image.createImage("/Splash.png");
					new SplashScreen(display, mainMenuScreen, splashLogo, 2000);
				}
				catch(Exception ex){
					System.out.println("Problema na criacao da SplashScreen");
					mainMenuScreenShow(null,0);
				}
			}
			else{
				mainMenuScreenShow(null,0);
			}
		}
		catch(Exception e){
			System.out.println(e);
		}
	}
	
	public Display getDisplay(){
		return display;
	}
	
	
	public void destroyApp(boolean unconditional){
		mainMenuScreen = null;
		splashLogo = null;
		System.gc();
		notifyDestroyed();
	}
	
	//INICIO
	private Image createImage(String filename){
		Image image = null;
		try {
			image = Image.createImage(filename);
		}
		catch (Exception e){}
		return image;
	}
	
	public void mainMenuScreenShow(Alert alert,int pista){
		if (alert == null){
			try{
				Game g = new Game(this,pista);
				g.start();
				display.setCurrent(g);
			}catch(Exception e){
				System.out.println("Erro ao tentar criar o JOGO");
			}
		}
		else
			display.setCurrent(alert, mainMenuScreen);
	}
	
	public void mainMenuScreenQuit(){
		destroyApp(true);
	}
	//FIM

	protected void pauseApp() {
		// TODO Auto-generated method stub
		
	}
}
