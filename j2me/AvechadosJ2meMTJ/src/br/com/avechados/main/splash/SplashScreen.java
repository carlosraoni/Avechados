package br.com.avechados.main.splash;
import java.util.Timer;

import javax.microedition.lcdui.Canvas;
import javax.microedition.lcdui.Display;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;

public final class SplashScreen extends Canvas{
	private Display display;
	private Image image;
	private Displayable next;
	private Timer timer;
	private int dismissTimer;
	
	public SplashScreen ( Display display,Displayable next,Image image, int dismissTimer){
		timer = new Timer();
		this.display = display;
		this.next = next;
		this.image = image;
		this.dismissTimer = dismissTimer;
		display.setCurrent(this);
	}
	
	static void access ( SplashScreen splashScreen ){
		splashScreen.dismiss();
	}
	
	private void dismiss (){
		timer.cancel();
		display.setCurrent(next);
	}
	
	protected void keyPress ( int keyPress ){
		dismiss();
	}
	
	protected void paint ( Graphics g ){
		//g.setColor (0x00FFFFFF); //Color = branco
		g.fillRect ( 0, 0, getWidth(), getHeight() );
		g.setColor (0x00000000); //Color = preto
		g.drawImage (image, getWidth()/2, getHeight()/2-5, 3);
	}
	
	protected void pointPressed ( int x, int y ){
		dismiss();
	}
	
	protected void showNotify () {
		if ( dismissTimer > 0 )
			timer.schedule(new CountDown(this), dismissTimer);
	}
}
