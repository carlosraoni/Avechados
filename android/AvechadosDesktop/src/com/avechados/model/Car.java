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
	
	// Variavel que sinaliza se o carro esta acelerando ou se chegou a Velocidade Maxima
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
		setPosX(getPosX() + getDeltaX());
		setPosY(getPosY() + getDeltaY());
	}

	// Metodo para determinacao da Velocidade do Carro
	private void updateSpeed() {
		initialSpeed = speed;
		// Aumentar a Velocidade gradativamente de Acordo com a Aceleracao
		if (hasAcel) {
			// Se o Carro estiver acelerando simular formula fisica para o calculo da Velocidade(V = Vo + a*t)
			int newSpeed = speed + acceleration * Constants.TIME;
			// Se o Carro atingir a Velocidade Maxima a aceleracao eh Parada
			if (newSpeed <= maxSpeed)
				speed = newSpeed;
			else {
				speed = maxSpeed;
				hasAcel = false;
			}
		} else {
			// Se o carro possui velocidade inferior a Velocidade Maxima ele comeca a acelerar novamente
			if (speed < maxSpeed)
				hasAcel = true;
		}
	}

	private void turnLeft(int degree) {
		angle = (angle + degree) % 360;
	}
		

	private void turnRight(int degree) {
		angle = angle - degree;
		if(angle < 0)
			angle += 360;
	}
	
	public void turnLeft() {
		turnLeft(Constants.DEGREE_TURN);
	}
	
	public void turnRight() {
		turnRight(Constants.DEGREE_TURN);
	}

	public void slowTurnLeft() {
		turnLeft(Constants.SLOW_DEGREE_TURN);
	}

	public void slowTurnRight() {
		turnRight(Constants.SLOW_DEGREE_TURN);		
	}

	
	public void decreaseSpeed(){
		speed -= Constants.BRAKE_SPEED;
		if(speed < 0) speed = 0;
	}
	
	public void slowDecreaseSpeed(){
		speed -= Constants.SLOW_BRAKE_SPEED;
		if(speed < 0) speed = 0;
	}
	
	// Retorna o valor do deslocamento do Carro em uma dimens�o
	private float getDelta(){
		// Simulacao das formulas fisicas de Calculo do deslocamento (com aceleracao : As = Vo*t + a*(t*t)/2 , sem aceleracao : As = V*t)
		if (hasAcel)
			return initialSpeed * Constants.TIME + (acceleration * (Constants.TIME * Constants.TIME)) / 2;		
		return speed * Constants.TIME;
	}
	
	// Calcula o deslocamento do Carro no Eixo X a partir de sua Velociade, Aceleracao e Direcao
	private float getDeltaX() {
		float deltaX = (float) (getDelta() * Math.cos(getAngleInRadians())); 
		// Conversao do deslocamento para pixel
		return deltaX * Constants.CONVERT_DELTA_TO_PIXELS;
	}

	// Calcula o deslocamento do Carro no Eixo Y a partir de sua Velociade, Aceleracao e Direcao
	private float getDeltaY() {
		float deltaY = (float) (getDelta() * Math.sin(getAngleInRadians())); 
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
