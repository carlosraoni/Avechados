package br.com.avechados.game;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.game.GameCanvas;

import br.com.avechados.main.AvechadosMidlet;
import br.com.avechados.model.Corrida;

public class Game extends GameCanvas implements Runnable{
	
	private int delay,npista;
	private Corrida corrida;
	private boolean isPlay;
	public AvechadosMidlet avechados;
	
	public Game(AvechadosMidlet avechados,int pista) throws Exception{
		super(true);
		//System.out.println("Width: " + this.getWidth() + ", Height: " + this.getHeight());
		this.npista = pista;
		corrida = new Corrida(pista, getWidth(), getHeight());
		delay = 100;
		this.avechados = avechados;
	}
	
	public void start(){
		isPlay = true; 
		Thread t = new Thread(this);	
		t.start();
	}
	
	public void stop(){
		isPlay = false;
	}
	
	public void run(){
		Graphics g = getGraphics();
		g.setColor(255,255,255);
		g.setFont(Font.getFont(Font.FACE_PROPORTIONAL,Font.STYLE_BOLD,Font.SIZE_MEDIUM));
		int estado=1;
		long inicio = System.currentTimeMillis(),fim=0,segundos=0,limite=0;
		long [] limites = {70000,60000,60000};
		while(isPlay){
			fim = System.currentTimeMillis();
			limite=limites[npista];
			segundos = (fim - inicio);
			estado = corrida.atualiza(getKeyStates());
			corrida.paint(g,0,0);
			g.drawString("Life "+corrida.jogador.life,0,0,Graphics.TOP|Graphics.LEFT);
			g.drawString("Velocidade "+corrida.jogador.vel,getWidth() - 100,0,Graphics.TOP|Graphics.LEFT);
			g.drawString("Voltas "+corrida.jogador.voltas,0,getHeight() - 15,Graphics.TOP|Graphics.LEFT);
			g.drawString("Tempo "+segundos/1000+"."+segundos%1000,getWidth() - 90,getHeight() - 15,Graphics.TOP|Graphics.LEFT);
			flushGraphics();
			if(segundos>limite)
				estado = 3;
			if(estado==0 || estado==2 || estado==3 || estado==5)
				isPlay = false;
			if(estado==4){
				g.drawString("Game Paused !!!",60,80,Graphics.TOP|Graphics.LEFT);
				flushGraphics();
				while((getKeyStates() & FIRE_PRESSED)==0){
					try{
						Thread.sleep(delay);
					}catch(Exception e){}
				}
				inicio += System.currentTimeMillis()-fim;
			}
			try{
				Thread.sleep(delay);
			}catch(Exception e){}
		}
		long Score=0;
		if(estado==0)
			g.drawString("GAME OVER",60,80,Graphics.TOP|Graphics.LEFT);
		else if(estado==2){
			g.drawString("YOU WIN !!!",60,80,Graphics.TOP|Graphics.LEFT);
			segundos = limite - segundos;
			Score = segundos>>4;
			g.drawString("Score : "+Score,60,120,Graphics.TOP|Graphics.LEFT);
		}
		else if(estado==5){
			g.drawString("YOU LOSE !!!",60,80,Graphics.TOP|Graphics.LEFT);
			segundos = limite - segundos;
			Score = segundos>>6;
			g.drawString("Score : "+Score,60,120,Graphics.TOP|Graphics.LEFT);
		}
		else
			g.drawString("TIME OUT !!!",60,80,Graphics.TOP|Graphics.LEFT);
		corrida.destroy();
		corrida = null;
		System.gc();
		flushGraphics();
		try{
			Thread.sleep(2000);
		}catch(Exception e){}
		Alert alert = new Alert("Fim de Jogo","  Score  :  "+Score+"   !!!", null, null);
		alert.setTimeout(Alert.FOREVER);
		alert.setType(AlertType.INFO);
		avechados.mainMenuScreenShow(alert,0);	
	}
}
