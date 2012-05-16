package br.com.avechados.model;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;

public class CarroJogador extends Carro{

	//int objetivo;

	public CarroJogador(Image i,int num,int posX,int posY) throws Exception{
		super(i,num,posX,posY);	
		//objetivo = direcao;
	}
	//Metodo utilizado para Atualizar a Direcao e Velocidade do Carro a partir da Entrada do Jogador
	public void atualiza(int keyStates){
		calcVel();
		//vel = 0;
		/* O jogador s� poder� pressionar direita e esquerda
		if((keyStates & UP_PRESSED)!=0){
		}
		if((keyStates & DOWN_PRESSED!=0)){
		}
		*/
		if((keyStates & GameCanvas.RIGHT_PRESSED)!=0){
			turnRight();
			//calcPos();
			
		}
		if((keyStates & GameCanvas.LEFT_PRESSED)!=0){
			turnLeft();
			//calcPos();
		}
		/* Acao que trata do lacamento dos misseis
		if((keyStates & FIRE_PRESSED)!=0){
		}
		*/
		//System.out.println(direcao);
		setFrame(direcao-1);
	}
}