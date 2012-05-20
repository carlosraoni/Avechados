package br.com.avechados.model;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.Sprite;

/*
	Classe que Implementa o Carro e seus atributos
*/
public class Carro extends Sprite{
	
	//Possiveis Atributos do Carro
	public int vel,v0,misseis,life,direcao,velMax,acel,voltas;
	//Direcoes que o carro pode ficar
	public static final int ROT_0=1,ROT_30=2,ROT_60=3,ROT_90=4,ROT_120=5,ROT_150=6,
				ROT_180=7,ROT_210=8,ROT_240=9,ROT_270=10,ROT_300=11,ROT_330=12;
	//Constante do Tempo para simulacao das formulas da fisica
	private static final int TIME=1;
	//Variavel que sinaliza se o carro esta acelerando ou se chegou a Velocidade Maxima
	public boolean hasAcel;
	//Construtor do Carro para atribuicao das caracteristicas iniciais do Carro
	public Carro(Image i,int num,int posX,int posY) throws Exception{
		super(i,23,23);
		//Possiveis Velocidades e Aceleracoes que os carros podem possuir
		int velMaxs [] = {200,200,150,200};
		int acels [] = {10,7,12,13};
				
		//Posicao inicial do carro
		setPosition(posX,posY);
		vel = 0;
		v0 = 0;
		misseis = 3;
		life = 100;
		direcao = ROT_0;
		velMax = velMaxs[num];
		acel = acels[num];
		hasAcel = true;
		//Definicao de um retangulo do carro para melhor tratamento de colisao , assim como a alteracao do Pixel de Referencia
		defineCollisionRectangle(0,0,23,23);
		defineReferencePixel(0,0);
	}
	//Metodo para determinacao da Velocidade do Carro
	public void calcVel(){
		v0 = vel;
		//Aumentar a Velocidade gradativamente de Acordo com a Acelera��o 
		if(hasAcel){
			//Se o Carro estiver acelerando simular formula fisica para o calculo da Velocidade(V = Vo + a*t)
			int vtmp = vel + acel*TIME;
			//Se o Carro atingir a Velocidade Maxima a aceleracao eh Parada
			if(vtmp<=velMax)
				vel = vtmp;
			else{
				vel = velMax;
				hasAcel = false;	
			}
		}
		else{
			//Se o carro possui velocidade inferior a Velocidade Maxima ele comeca a acelerar novamente
			if(vel<velMax)
				hasAcel = true;
		}
	}
	
	public void turnRight(){
		direcao = (direcao+1)%ROT_330;
		if(direcao == 0)
			direcao = ROT_330;	
	}
	
	public void turnLeft(){
		direcao--;
		if(direcao == 0)
			direcao = ROT_330;
			
	}
	
	//Calcula o deslocamento do Carro no Eixo X a partir de sua Velociade , Aceleracao e Direcao
	public int getDeslocX(){
		//tmpX eh o valor do deslocamento caso o Carro possuisse velociade no eixo Y sendo 0
		int tmpX,FATOR_30,FATOR_60;
		//Simulacao das formulas fisicas de Calculo do deslocamento 
		//(com aceleracao : As = Vo*t + a*(t*t)/2 , sem aceleracao : As = V*t)
		if(hasAcel)
			tmpX = v0*TIME+(acel*(TIME*TIME))/2;
		else 
			tmpX = vel*TIME;
		//Fator 30 eh o valor do Modulo do deslocamento em x quando o angulo for de 30 em relacao ao eixo X
		//Equivale a tmpX = cos30 * tmpX
		//Fator 60 eh o valor do Modulo do deslocamento em x quando o angulo for de 60 em relacao ao eixo X
		//Equivale a tmpX = sen60 * tmpX
		FATOR_30 = (tmpX/2);
		FATOR_60 = (8*tmpX)/10;
		//Determinacao do deslocamento em relacao a direcao do Carro
		switch(direcao){
			case ROT_0: tmpX*=-1; break;
			case ROT_30: tmpX = FATOR_30*(-1); break;
			case ROT_60: tmpX = FATOR_60*(-1); break;
			case ROT_90: tmpX = 0; break;
			case ROT_120: tmpX = FATOR_60; break;
			case ROT_150: tmpX = FATOR_30; break;
			case ROT_180: break;
			case ROT_210: tmpX = FATOR_30; break;
			case ROT_240: tmpX = FATOR_60; break;
			case ROT_270: tmpX = 0; break;
			case ROT_300: tmpX = FATOR_60*(-1); break;
			case ROT_330: tmpX = FATOR_30*(-1); break;
		}
		//Conversao do deslocamento para pixel
		return (tmpX/12);
	}
	//Calcula o deslocamento do Carro no Eixo Y a partir de sua Velociade , Aceleracao e Direcao
	public int getDeslocY(){
		//tmpY eh o valor do deslocamento caso o Carro possuisse velociade no eixo X sendo 0
		int tmpY,FATOR_30,FATOR_60;
		//Simulacao das formulas fisicas de Calculo do deslocamento 
		//(com aceleracao : As = Vo*t + a*(t*t)/2 , sem aceleracao : As = V*t)
		if(hasAcel)
			tmpY = v0*TIME+(acel*(TIME*TIME))/2;
		else 
			tmpY = vel*TIME;
		//Fator 30 eh o valor do Modulo do deslocamento em y quando o angulo for de 30 em relacao ao eixo X
		//Equivale a tmpY = sen30 * tmpX
		//Fator 60 eh o valor do Modulo do deslocamento em y quando o angulo for de 60 em relacao ao eixo X
		//Equivale a tmpY = cos60 * tmpX
		FATOR_60 = (tmpY/2);
		FATOR_30 = (8*tmpY)/10;
		//Determinacao do deslocamento em relacao a direcao do Carro
		switch(direcao){
			case ROT_0: tmpY = 0; break;
			case ROT_30: tmpY = FATOR_30*(-1); break;
			case ROT_60: tmpY = FATOR_60*(-1); break;
			case ROT_90: tmpY *= (-1); break;
			case ROT_120: tmpY = FATOR_60*(-1); break;
			case ROT_150: tmpY = FATOR_30*(-1); break;
			case ROT_180: tmpY = 0; break;
			case ROT_210: tmpY = FATOR_30; break;
			case ROT_240: tmpY = FATOR_60; break;
			case ROT_270: break;
			case ROT_300: tmpY = FATOR_60; break;
			case ROT_330: tmpY = FATOR_30; break;
		}
		//Conversao do deslocamento para pixel
		return (tmpY/12);
	}
}
