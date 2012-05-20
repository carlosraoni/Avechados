package com.avechados.model;

import com.avechados.utils.Constants;
import com.badlogic.gdx.Gdx;

public class Car {	
	public enum CarState {ALIVE, DEAD};
	
	private int speed;
	private int initialSpeed;
	private int maxSpeed;
	private int life;	
	private int acceleration; 
	private int angle;
	private float posX, posY;
	private float mapWidth, mapHeight;
	
	// Variavel que sinaliza se o carro esta acelerando ou se chegou a
	// Velocidade Maxima
	public boolean hasAcel;

	// Construtor do Carro para atribuicao das caracteristicas iniciais do Carro
	public Car(int maxSpeed, int acceleration, int posX, int posY, float mapWidth, float mapHeight) {
		this.maxSpeed = maxSpeed;
		this.acceleration = acceleration;
		this.posX = posX;
		this.posY = posY;
		this.mapWidth = mapWidth;
		this.mapHeight = mapHeight;
	
		this.initialSpeed = this.speed = 0;
		this.angle = 0;
		this.life = Constants.INITIAL_LIFE;
		
		hasAcel = true;		
	}

	public CarState update(){
		updateSpeed();
		updatePosition();
		return CarState.ALIVE;
	}
	
	private void updatePosition() {
		float deltaX = getDeltaX();
		float deltaY = getDeltaY();
		setPosX(getPosX() + deltaX);
		setPosY(getPosY() + deltaY);
	}

	// Metodo para determinacao da Velocidade do Carro
	private void updateSpeed() {
		initialSpeed = speed;
		// Aumentar a Velocidade gradativamente de Acordo com a Aceleracao
		if (hasAcel) {
			// Se o Carro estiver acelerando simular formula fisica para o
			// calculo da Velocidade(V = Vo + a*t)
			int newSpeed = speed + acceleration * Constants.TIME;
			// Se o Carro atingir a Velocidade Maxima a aceleracao eh Parada
			if (newSpeed <= maxSpeed)
				speed = newSpeed;
			else {
				speed = maxSpeed;
				hasAcel = false;
			}
		} else {
			// Se o carro possui velocidade inferior a Velocidade Maxima ele
			// comeca a acelerar novamente
			if (speed < maxSpeed)
				hasAcel = true;
		}
	}

	public void turnLeft() {
		angle = (angle + Constants.DEGREE_TURN) % 360;
	}

	public void turnRight() {
		angle = angle - Constants.DEGREE_TURN;
		if(angle < 0)
			angle += 360;
	}

	// Calcula o deslocamento do Carro no Eixo X a partir de sua Velociade ,
	// Aceleracao e Direcao
	private float getDeltaX() {
		// deltaX eh o valor do deslocamento caso o Carro possuisse velociade no eixo Y sendo 0
		float deltaX = 0.0f;
		// Simulacao das formulas fisicas de Calculo do deslocamento
		// (com aceleracao : As = Vo*t + a*(t*t)/2 , sem aceleracao : As = V*t)
		if (hasAcel)
			deltaX = initialSpeed * Constants.TIME + (acceleration * (Constants.TIME * Constants.TIME)) / 2;
		else
			deltaX = speed * Constants.TIME;
		deltaX *= Math.cos(getAngleInRadians()); 
		// Conversao do deslocamento para pixel
		return deltaX * Constants.CONVERT_DELTA_TO_PIXELS;
	}

	// Calcula o deslocamento do Carro no Eixo Y a partir de sua Velociade ,
	// Aceleracao e Direcao
	private float getDeltaY() {
		// deltaX eh o valor do deslocamento caso o Carro possuisse velociade no eixo Y sendo 0
		float deltaY = 0.0f;
		// Simulacao das formulas fisicas de Calculo do deslocamento
		// (com aceleracao : As = Vo*t + a*(t*t)/2 , sem aceleracao : As = V*t)
		if (hasAcel)
			deltaY = initialSpeed * Constants.TIME + (acceleration * (Constants.TIME * Constants.TIME)) / 2;
		else
			deltaY = speed * Constants.TIME;
		deltaY *= Math.sin(getAngleInRadians());

		// Conversao do deslocamento para pixel
		return deltaY * Constants.CONVERT_DELTA_TO_PIXELS;
	}

	public float getPosX() {
		return posX;
	}

	public void setPosX(float posX) {
		if(posX - Constants.MAP_BOUNDARY < 0 || posX + Constants.MAP_BOUNDARY > mapWidth){
			return;
		}				
		this.posX = posX;
	}

	public float getPosY() {
		return posY;
	}

	public void setPosY(float posY) {
		if(posY - Constants.MAP_BOUNDARY < 0 || posY + Constants.MAP_BOUNDARY > mapHeight){
			return;
		}		
		this.posY = posY;
	}

	public int getAngle(){
		return this.angle;
	}
	
	public double getAngleInRadians(){
		return (getAngle() * Math.PI) / 180.0; 
	}
}
