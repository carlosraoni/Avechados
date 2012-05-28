package br.com.avechados.model;
import java.util.Vector;

import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.GameCanvas;
import javax.microedition.lcdui.game.LayerManager;
import javax.microedition.lcdui.game.TiledLayer;

public class Corrida extends LayerManager{
	
	private TiledLayer pista,parteExterna;
	private InitialPosition initialPos;
	private int [][] distance;	
	public CarroJogador jogador;
	public CarroAgente agente;
	private int scX,scY,limX,limY,width,height,npista;
	private int iteracoesMorteAgente = 0;
	private static int TILE_SIZE = 35;
	int [] dirPorRot = {0, 0, 1, 1, 2, 3, 3, 4, 5, 5, 6, 7, 7};
	private boolean [][] faixas;
	private boolean morreu,perdeu;
	
	public Corrida(int npista, int width, int height){
		super();
		this.width = width;
		this.height = height;
		this.npista = npista;
		morreu = false;
		perdeu = false;		
		
		switch(npista){
			case 0: initialPos = initTiles1(); break;
			case 1: initialPos = initTiles2(); break;
			case 2: initialPos = initTiles3(); break;
		}
		limX = parteExterna.getCellWidth()*parteExterna.getColumns();
		limY  = parteExterna.getCellHeight()*parteExterna.getRows();
		
		try{
			Image i = Image.createImage("/sprite2.png");			
			int posXJogador = TILE_SIZE * (initialPos.getCol()) + TILE_SIZE / 2;			
			int posYJogador = TILE_SIZE * (initialPos.getRow()) + TILE_SIZE / 2;
			
			int posXAgente = posXJogador;
			int posYAgente = posYJogador + 25;
			
			jogador = new CarroJogador(i,0,posXJogador,posYJogador);
			agente = new CarroAgente(i,1,posXAgente,posYAgente,parteExterna, distance);
		}catch(Exception e)
		{
			System.out.println("Nao foi possivel criar a imagem do Carro");
		}
	
		jogador.voltas = 1;
		agente.voltas = 1;
		faixas = new boolean[2][5];
		for(int i=0;i<5;i++){
			faixas[0][i] = false;
			faixas[1][i] = false;
		}
		
		append(jogador);	
		append(agente);
		append(pista);
		append(parteExterna);
		//setViewWindow(scX,scY,VISOR,VISOR);
		setScreenPos();		
	}

	public int atualiza(int keyStates){
		int antXj = jogador.getX(),antYj = jogador.getY(),antXa = agente.getX(),antYa = agente.getY();
		if(!morreu){
			agente.atualiza(npista);
		}
		else{
			iteracoesMorteAgente++;
			if(iteracoesMorteAgente < 10)
				agente.atualiza(npista);			
		}
		if((keyStates & GameCanvas.FIRE_PRESSED)==0)
			jogador.atualiza(keyStates);
		else
			return 4;
		int estado = 1;
		calcPos();
		if(jogador.collidesWith(parteExterna,true)){
			estado = collisionImpact(jogador,0);
		}
		if(agente.collidesWith(parteExterna,true)){
			if(collisionImpact(agente,1)==0){
				morreu = true;
			}
		}
		if(jogador.collidesWith(agente,true)){
			jogador.setPosition(antXj,antYj);
			agente.setPosition(antXa,antYa);
			jogador.life -= 6;
			agente.life -= 6;
			if(jogador.life<=0){
				jogador.life = 0;
				estado = 0;
			}	
			Carro carroDireita = jogador, carroEsquerda = agente;
			if(jogador.direcao < agente.direcao){
				carroDireita = agente;
				carroEsquerda = jogador;
			}
			int diffDir = dirPorRot[carroDireita.direcao] - dirPorRot[carroEsquerda.direcao];
			if(diffDir < 4){
				carroDireita.turnRight();
				carroDireita.turnRight();
				carroEsquerda.turnLeft();
				carroEsquerda.turnLeft();
			}else if(diffDir > 4){
				carroDireita.turnLeft();
				carroDireita.turnLeft();
				carroEsquerda.turnRight();
				carroEsquerda.turnRight();
			}else{
				carroDireita.turnLeft();
				carroDireita.turnLeft();
				carroEsquerda.turnRight();
				carroEsquerda.turnRight();
				
//				int tmp = carroDireita.direcao;
//				carroDireita.direcao = carroEsquerda.direcao;
//				carroEsquerda.direcao = tmp;
			}
		}
		//calcPos(jogador.direcao);
		setScreenPos();
		return estado;
	}

	private void setScreenPos() {
		calcScreen();
		//setViewWindow(scX,scY,VISOR,VISOR);
		setViewWindow(scX,scY,width,height);
	}
	
	private int collisionImpact(Carro car,int nj){	
		int carX = car.getX(),carY=car.getY();
		int pcolunas[] = {(carX+3)/35,(carX+18)/35,(carX+3)/35,(carX+18)/35};
		int plinhas[] = {(carY+3)/35,(carY+3)/35,(carY+18)/35,(carY+18)/35};
		int estado = 1;
			
		int celula = 0,choque = 0;
		for(int i=0;i<4;i++){
			int tmp = parteExterna.getCell(pcolunas[i],plinhas[i]);
			//System.out.println(tmp);
			if(tmp == 49){
				celula = 1;
			}else if((tmp == 52 || (tmp>=1 && tmp<=12)) && celula!=1){
				celula = 3;
				choque = i;
			}else if((tmp == 53 || (tmp>=13 && tmp<=36)) && celula==0){
				celula = 2;
			}else if(tmp == 55){
				checkFaixas(plinhas[i],pcolunas[i],nj);
				if(faixas[nj][4]){
					car.voltas--;
					for(int j=0;j<5;j++)
						faixas[nj][j] = false;
				}
				if(nj==0 && car.voltas==0){
					if(!perdeu)
						estado = 2;
					else
						estado = 5;
					return estado;	
				}
				if(nj==1 && car.voltas==0){
					perdeu = true;
					morreu = true;
				}
			}
		}
			
		switch(celula){
			case 1: 
				estado = 0;
				break;
			case 2: 
				if(jogador.vel-20>36) 
					car.vel-=20;
				else
					car.vel = 36;
				break;
			case 3: 
				if(car.vel-30>12) 
					car.vel-=30;
				else
					car.vel = 12;
				//jogador.setPosition(antX,antY);
				jogador.life -= 4;
				if(car.life<=0){
					estado = 0;
					car.life = 0;
				}
				break;
		}
		return estado;
	}
	
	private void checkFaixas(int linha,int coluna,int nj){
		
		int [][] lfaixa = {
		{23,24,6,7,19,20,2,3,23,24},
		{23,24,23,24,20,21,19,19,23,24},
		{10,11,3,4,6,7,21,21,10,11}
		};
	
		int [][] cfaixa = {
		{16,16,7,7,16,16,23,23,18,18},
		{17,17,12,12,14,14,16,17,19,19},
		{15,15,16,16,20,20,15,16,17,17}
		};
		//System.out.println("jogador : "+nj);
		for(int i=0;i<5;i++){
			if(i==0 || faixas[nj][i-1]){
				if(
				   ((linha == lfaixa[npista][2*i])&&(coluna == cfaixa[npista][2*i])) ||
				   ((linha == lfaixa[npista][2*i+1])&&(coluna == cfaixa[npista][2*i+1]))
				){
					faixas[nj][i] = true;
					//System.out.println("Passei pela faixa "+i+" da pista "+npista+" jogador "+nj);
				}
			}
			else
				break;
		}
	}
	
	private void calcPos(){
		int dXa = agente.getDeslocX(),dYa = agente.getDeslocY();
		int dX = jogador.getDeslocX(),dY = jogador.getDeslocY();
		jogador.setPosition(checkLim(jogador.getX()+dX,true),checkLim(jogador.getY()+dY,false));
		if(!morreu)
			agente.setPosition(checkLim(agente.getX()+dXa,true),checkLim(agente.getY()+dYa,false));
	}
	
	private int checkLim(int valor,boolean isX){
		if(valor<=0){
			return 5;
		}
		if(isX){
			if(valor+10>limX)
				return limX-10;
		}
		else{
			if(valor+10>limY)
				return limY-10;
		}
		return valor;
	}
	
	private void calcScreen(){
		//int tmpX = agente.getX()-(width/2),tmpY = agente.getY()-(height/2);
		int tmpX = jogador.getX()-(width/2),tmpY = jogador.getY()-(height/2);
		scX = checkViewLim(tmpX,true);
		scY = checkViewLim(tmpY,false);
	}
	
	private int checkViewLim(int valor,boolean isX){
		if(valor < 0){
			return 0;
		}

		int visorX = width;
		int visorY = height;
		if(isX){
			if(valor+visorX>limX)
				return valor-((valor+visorX)-limX);
		}
		else{
			if(valor+visorY>limY)
				return valor-((valor+visorY)-limY);
		}
		return valor;
	}

	private static class InitialPosition{
		private int row;
		private int col;
		public InitialPosition(int row, int col) {
			this.row = row;
			this.col = col;
		}
		public int getRow() {
			return row;
		}
		public int getCol() {
			return col;
		}
		
		public static boolean isInitialPosition(int tileIndex){
			return (tileIndex == 59 || tileIndex == 65);
		}
	}
	
	private class Node{
		private int row;
		private int col;
		private int depth;
		
		public Node(int row, int col, int depth) {
			this.row = row;
			this.col = col;
			this.depth = depth;
		}

		public int getRow() {
			return row;
		}

		public int getCol() {
			return col;
		}

		public int getDepth() {
			return depth;
		}		
	}
	
	private class Queue{
		Vector queue = new Vector();
		
		public void push(Object o){
			queue.insertElementAt(o, 0);			
		}
		
		public Object pop(){
			Object o = queue.lastElement();
			queue.removeElementAt(queue.size() - 1);
			return o;
		}
		
		public boolean isEmpty(){
			return queue.isEmpty();
		}
	}

	private void calcDistanceMatrix(InitialPosition ini, int [][] mapPE){
		int n = mapPE.length;
		int m = mapPE[0].length;
		int dIni = -10;
		distance = new int[n][m];
		for(int i=0; i<n; i++)
			for(int j=0; j<m; j++)
				distance[i][j] = dIni;
		
		int [][] dir = {{0, -1}, {-1, -1}, {-1, 0}, {-1, 1}, {0, 1} , {1, 1}, {1, 0}, {1, -1}};
		//int [][] dir = {{0, -1},  {-1, 0}, {0, 1} , {1, 0}};
		
		Queue q = new Queue();
		int rowIni = ini.getRow();
		int colIni = ini.getCol();		
		// Largada
		distance[rowIni][colIni] = 0;
		distance[rowIni+1][colIni] = 0;
		
		distance[rowIni][colIni+1] = 0;
		distance[rowIni+1][colIni+1] = 0;
		
		distance[rowIni][colIni+2] = 0;
		distance[rowIni+1][colIni+2] = 0;
		
		distance[rowIni][colIni+3] = 0;
		distance[rowIni+1][colIni+3] = 0;
		
		distance[rowIni][colIni+4] = 0;
		distance[rowIni+1][colIni+4] = 0;
				
		distance[rowIni][colIni-1] = 1;
		distance[rowIni+1][colIni-1] = 1;
		
		q.push(new Node(rowIni,   colIni -1 , 1));
		q.push(new Node(rowIni+1, colIni -1 , 1));
		
		while(!q.isEmpty()){
			Node atual = (Node) q.pop();
			for(int i=0; i<dir.length; i++){
				int nRow = atual.getRow() + dir[i][0];
				int nCol = atual.getCol() + dir[i][1];
				if(nRow < 0 || nCol < 0 || nRow >= n || nCol >= m)
					continue;
				if(!(mapPE[nRow][nCol] == 0 || mapPE[nRow][nCol] == 55) || distance[nRow][nCol] != dIni)
					continue;
				distance[nRow][nCol] = atual.getDepth() + 1;				
				q.push(new Node(nRow, nCol, atual.getDepth() + 1));
			}
		}
				
		printBiDimensionalArrayFormated(distance, 4);
	}
	
	private void printBiDimensionalArrayFormated(int [][] mat, int padding){
		int n = mat.length;
		int m = mat[0].length;
		
		for(int i=0;i<n;i++){
			for(int j=0;j<m;j++){
				String number = Integer.toString(mat[i][j]);
				System.out.print(number);
				for(int k=number.length() ; k<padding; k++)
					System.out.print(" ");				
			}
			System.out.println();
		}
	}
	
	private InitialPosition initTiles1(){
		try{
			Image im = Image.createImage("/Background.png");
			pista = new TiledLayer(32,27,im,35,35);
			parteExterna = new TiledLayer(32,27,im,35,35);
			
		}catch(Exception e){
			System.out.println("Nao foi possivel criar a imagem da Pista");
		}
		
		int[][] mapPista = {
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,55,55,55,55,55,55,55,55,55,55,55,55,55,55,00,00,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,57,50,56,56,56,56,56,56,56,56,56,56,50,58,00,00,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,57,58,00,00,00,00,00,00,00,00,00,00,57,58,00,00,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,57,58,00,00,00,00,00,00,00,00,00,00,57,58,00,00,00,00,00},
		{00,00,00,57,55,55,55,55,55,55,55,00,00,57,58,00,00,00,00,00,00,00,00,00,00,57,58,00,00,00,00,00},
		{00,00,00,57,50,56,56,56,56,50,58,00,00,57,58,00,00,00,00,00,00,00,00,00,00,57,58,00,00,00,00,00},
		{00,00,00,57,58,00,00,00,00,57,58,00,00,57,58,00,00,00,00,00,00,00,00,00,00,57,58,00,00,00,00,00},
		{00,00,00,57,58,00,00,00,00,57,58,00,00,57,58,00,00,00,00,00,00,00,55,55,55,50,58,00,00,00,00,00},
		{00,00,00,57,58,00,00,00,00,57,58,00,00,57,58,00,00,00,00,00,00,00,57,50,56,56,58,00,00,00,00,00},
		{00,00,00,57,58,00,00,00,00,57,58,00,00,57,58,00,00,00,00,00,00,00,57,58,00,00,00,00,00,00,00,00},
		{00,00,00,57,50,55,58,00,00,57,58,00,00,57,58,00,00,00,00,00,00,00,57,58,00,00,00,00,00,00,00,00},
		{00,00,00,57,56,50,58,00,00,57,58,00,00,57,50,55,55,55,55,58,00,00,57,58,00,00,00,00,00,00,00,00},
		{00,00,00,00,00,57,58,00,00,57,58,00,00,56,56,56,56,56,50,58,00,00,57,58,00,00,00,00,00,00,00,00},
		{00,00,00,00,00,57,58,00,00,57,58,00,00,00,00,00,00,00,57,58,00,00,57,58,00,00,00,00,00,00,00,00},
		{00,00,00,00,00,57,58,00,00,57,58,00,00,00,00,00,00,00,57,58,00,00,57,58,00,00,00,00,00,00,00,00},
		{00,00,00,57,55,50,58,00,00,57,58,00,00,00,00,00,00,00,57,58,00,00,57,58,00,00,00,00,00,00,00,00},
		{00,00,00,57,50,56,56,00,00,57,58,00,00,00,00,00,00,00,57,58,00,00,57,58,00,00,00,00,00,00,00,00},
		{00,00,00,57,58,00,00,00,00,57,50,55,55,55,55,55,55,55,50,58,00,00,57,58,00,00,00,00,00,00,00,00},
		{00,00,00,57,58,00,00,00,00,57,56,56,56,56,56,56,56,56,56,58,00,00,57,50,55,55,55,55,55,58,00,00},
		{00,00,00,57,58,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,56,56,56,56,56,56,50,58,00,00},
		{00,00,00,57,58,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,57,58,00,00},
		{00,00,00,57,50,55,55,55,55,55,55,55,55,55,55,55,55,55,55,59,55,55,55,55,55,55,55,55,50,58,00,00},
		{00,00,00,57,56,56,56,56,56,56,56,56,56,56,56,56,56,56,56,60,56,56,56,56,56,56,56,56,56,56,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00}
		};
		
		int[][] mapPE = {
		{49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49},
		{49,21,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,18,22,49,49,49},
		{49,16,53,53,53,53,53,53,53,53,53,53,53,00,00,00,00,00,00,00,00,00,00,55,00,00,00,53,20,49,49,49},
		{49,16,53,53,53,53,53,53,53,53,53,53,53,00,00,00,00,00,00,00,00,00,00,55,00,00,00,53,20,49,49,49},
		{49,16,53,53,53,53,53,53,53,53,53,53,53,00,00,25,26,26,26,26,26,26,26,35,52,00,00,53,20,49,49,49},
		{49,16,53,53,53,53,53,53,53,53,53,53,53,00,00,32,1,2,2,2,2,2,2,2,3,00,00,53,20,49,49,49},
		{49,16,53,00,00,00,00,55,00,00,00,53,53,00,00,32,8,49,49,49,49,49,49,49,4,00,00,53,20,49,49,49},
		{49,16,53,00,00,00,00,55,00,00,00,53,53,00,00,32,8,49,49,49,49,49,49,49,4,00,00,53,20,49,49,49},
		{49,16,53,00,00,53,53,53,53,00,00,53,53,00,00,32,8,49,49,49,9,6,6,6,5,00,00,53,20,49,49,49},
		{49,16,53,00,00,53,53,53,53,00,00,53,53,00,00,32,8,49,49,49,4,52,00,00,00,00,00,53,20,49,49,49},
		{49,16,53,00,00,53,53,53,53,00,00,53,53,00,00,32,8,9,6,6,5,52,00,00,00,00,00,53,20,49,49,49},
		{49,16,53,00,00,53,53,53,53,00,00,53,53,00,00,32,7,5,52,52,52,52,00,00,53,53,53,53,20,49,49,49},
		{49,16,53,00,00,00,00,53,53,00,00,53,53,00,00,31,30,30,34,52,52,52,00,00,53,53,53,53,20,49,49,49},
		{49,16,53,00,00,00,00,53,53,00,00,53,53,00,00,00,00,00,00,00,52,52,00,00,53,53,53,53,20,49,49,49},
		{49,24,14,14,15,00,00,53,53,00,00,53,53,00,00,00,00,00,00,00,52,52,00,00,53,53,53,53,20,49,49,49},
		{49,49,49,49,16,00,00,53,53,00,00,53,53,53,53,53,53,53,00,00,52,52,00,00,53,53,53,53,20,49,49,49},
		{49,21,18,18,17,00,00,53,53,00,00,53,53,53,53,53,53,53,00,00,52,52,00,00,53,53,53,53,20,49,49,49},
		{49,16,53,00,00,00,00,53,53,00,00,53,53,53,53,53,53,53,00,00,52,52,00,00,53,53,53,53,20,49,49,49},
		{49,16,53,00,00,00,00,53,53,00,00,53,53,53,53,53,53,53,00,00,52,52,00,00,53,53,53,53,19,18,18,18},
		{49,16,53,00,00,53,53,53,53,00,00,00,00,00,00,00,55,00,00,00,52,52,00,00,53,53,53,53,53,53,53,53},
		{49,16,53,00,00,13,14,14,15,00,00,00,00,00,00,00,55,00,00,00,52,52,00,00,00,00,00,00,00,00,53,53},
		{49,16,53,00,00,20,49,49,16,53,53,53,53,32,52,52,52,52,52,52,52,52,00,00,00,00,00,00,00,00,53,53},
		{49,16,53,00,00,19,18,18,17,53,53,53,53,32,52,52,52,52,52,52,52,52,52,52,52,52,52,52,00,00,53,53},
		{49,16,53,00,00,00,00,00,00,00,00,00,00,00,00,00,55,00,55,00,00,00,00,00,00,00,00,00,00,00,53,53},
		{49,16,53,00,00,00,00,00,00,00,00,00,00,00,00,00,55,00,55,00,00,00,00,00,00,00,00,00,00,00,53,53},
		{49,24,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,14,15,53,53,53,53,53,53,53,53},
		{49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,16,53,53,53,53,53,53,53,53}
		};
		
		InitialPosition initialPos = null;				
		for(int i=0;i<27;i++){
			for(int j=0;j<32;j++){
				pista.setCell(j,i,mapPista[i][j]);
				parteExterna.setCell(j,i,mapPE[i][j]);
				if(initialPos == null && InitialPosition.isInitialPosition(mapPista[i][j]))
					initialPos = new InitialPosition(i, j);
			}
		}
		
		calcDistanceMatrix(initialPos, mapPE);
		
		return initialPos;
	}
	
		
	private InitialPosition initTiles2(){
		
		try{
			Image im = Image.createImage("/Background.png");
			pista = new TiledLayer(32,27,im,35,35);
			parteExterna = new TiledLayer(32,27,im,35,35);
			
		}catch(Exception e){
			System.out.println("Nao foi possivel criar a imagem da Pista");
		}
		
		int[][] mapPista = {
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,63,61,61,61,61,61,61,61,61,64,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,63,51,62,62,62,62,62,62,51,64,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,63,64,00,00,00,00,00,00,63,64,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,63,64,00,00,00,00,00,00,63,64,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,63,64,00,00,00,00,00,00,63,64,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,63,64,00,00,00,00,00,00,63,64,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,63,64,00,00,00,00,00,00,63,64,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,63,64,00,00,00,00,00,00,63,64,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,63,64,00,00,00,00,00,00,63,64,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,63,64,00,00,00,00,00,00,47,47,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,63,64,00,00,00,00,00,00,69,70,00,00,00,67,67,67,67,67,67,67,67,67,67,67,67,67,70,00,00},
		{00,00,00,63,64,00,00,00,00,00,00,69,70,00,00,00,69,54,68,68,68,68,68,68,68,68,68,68,54,70,00,00},
		{00,00,00,63,64,00,00,00,00,00,00,69,70,00,00,00,69,70,00,00,00,00,00,00,00,00,00,00,69,70,00,00},
		{00,00,00,63,64,00,00,00,00,00,00,69,70,00,00,00,69,70,00,00,00,00,00,00,00,00,00,00,69,70,00,00},
		{00,00,00,63,64,00,00,00,00,00,00,69,70,00,00,00,69,70,00,00,00,00,00,00,00,00,00,00,69,70,00,00},
		{00,00,00,63,64,00,00,00,00,00,00,69,70,00,00,00,69,70,00,00,00,00,00,00,00,00,00,00,69,70,00,00},
		{00,00,00,63,64,00,00,00,00,00,00,69,70,00,00,00,69,70,00,00,00,00,00,00,00,00,00,00,69,70,00,00},
		{00,00,00,63,64,00,00,00,00,00,00,69,70,00,00,00,69,70,00,00,00,00,00,00,00,00,00,00,69,70,00,00},
		{00,00,00,63,64,00,00,00,00,00,00,69,54,67,67,67,54,70,00,00,00,00,00,00,00,00,00,00,69,70,00,00},
		{00,00,00,63,64,00,00,00,00,00,00,68,68,68,68,68,68,70,00,00,00,00,00,00,00,00,00,00,69,70,00,00},
		{00,00,00,63,64,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,69,70,00,00},
		{00,00,00,63,51,61,61,61,61,61,61,61,61,61,61,61,61,61,61,61,65,67,67,67,67,67,67,67,54,70,00,00},
		{00,00,00,62,62,62,62,62,62,62,62,62,62,62,62,62,62,62,62,62,66,68,68,68,68,68,68,68,68,68,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00}
		};
		
		int[][] mapPE = {
		{53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53},
		{53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53},
		{53,53,53,00,00,00,00,00,00,00,00,00,00,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53},
		{53,53,53,00,00,00,00,00,00,00,00,00,00,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53},
		{53,53,53,00,00,13,14,14,14,14,15,00,00,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53},
		{53,53,53,00,00,20,49,49,49,49,16,00,00,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53},
		{53,53,53,00,00,20,49,49,49,49,16,00,00,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53},
		{53,53,53,00,00,20,49,49,49,49,16,00,00,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53},
		{53,53,53,00,00,20,49,49,49,49,16,00,00,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53},
		{53,53,53,00,00,20,49,49,49,49,16,00,00,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53},
		{53,53,53,00,00,20,49,49,49,49,16,00,00,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53},
		{53,53,53,00,00,20,49,49,49,49,16,00,00,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53},
		{53,53,53,00,00,20,49,49,49,49,16,00,00,53,53,53,00,00,00,00,00,00,00,00,00,00,00,00,00,00,53,53},
		{53,53,53,00,00,20,49,49,49,49,16,00,00,53,53,53,00,00,00,00,00,00,00,00,00,00,00,00,00,00,53,53},
		{53,53,53,00,00,20,49,49,49,49,16,00,00,53,53,53,00,00,13,14,14,14,14,14,14,14,14,15,00,00,53,53},
		{53,53,53,00,00,20,49,49,49,49,16,00,00,53,53,53,00,00,20,49,49,49,49,49,49,49,49,16,00,00,53,53},
		{53,53,53,00,00,20,49,49,49,49,16,00,00,53,53,53,00,00,20,49,49,49,49,49,49,49,49,16,00,00,53,53},
		{53,53,53,00,00,20,49,49,49,49,16,00,00,53,53,53,00,00,20,49,49,49,49,49,49,49,49,16,00,00,53,53},
		{53,53,53,00,00,20,49,49,49,49,16,00,00,53,53,53,00,00,20,49,49,49,49,49,49,49,49,16,00,00,53,53},
		{53,53,53,00,00,20,49,49,49,49,16,00,00,53,53,53,55,55,20,49,49,49,49,49,49,49,49,16,00,00,53,53},
		{53,53,53,00,00,20,49,49,49,49,16,00,00,00,55,00,00,00,20,49,49,49,49,49,49,49,49,16,00,00,53,53},
		{53,53,53,00,00,20,49,49,49,49,16,00,00,00,55,00,00,00,20,49,49,49,49,49,49,49,49,16,00,00,53,53},
		{53,53,53,00,00,19,18,18,18,22,24,14,14,14,14,14,14,14,23,21,18,18,18,18,18,18,18,17,00,00,53,53},
		{53,53,53,00,00,00,00,00,00,00,00,00,55,00,00,00,00,55,00,55,00,00,00,00,00,00,00,00,00,00,53,53},
		{53,53,53,00,00,00,00,00,00,00,00,00,55,00,00,00,00,55,00,55,00,00,00,00,00,00,00,00,00,00,53,53},
		{53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53},
		{53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53,53}
		};
		
		InitialPosition initialPos = null;
		for(int i=0;i<27;i++){
			for(int j=0;j<32;j++){
				pista.setCell(j,i,mapPista[i][j]);
				parteExterna.setCell(j,i,mapPE[i][j]);
				if(initialPos == null && InitialPosition.isInitialPosition(mapPista[i][j]))
					initialPos = new InitialPosition(i, j);
			}
		}
		calcDistanceMatrix(initialPos, mapPE);
		
		return initialPos;
	}
	
	private InitialPosition initTiles3(){		
		try{
			Image im = Image.createImage("/Background.png");
			pista = new TiledLayer(32,27,im,35,35);
			parteExterna = new TiledLayer(32,27,im,35,35);
			
		}catch(Exception e){
			System.out.println("Nao foi possivel criar a imagem da Pista");
		}
		
		int[][] mapPista = {
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,57,55,55,55,55,55,55,38,61,61,61,61,61,61,61,61,61,61,61,61,61,61,00,00,00,00,00,00,00},
		{00,00,00,57,50,56,56,56,56,56,38,62,62,62,62,62,62,62,62,62,62,62,62,51,64,00,00,00,00,00,00,00},
		{00,00,00,57,58,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,63,64,00,00,00,00,00,00,00},
		{00,00,00,57,58,00,00,00,00,00,00,00,00,00,00,61,61,61,61,61,61,61,61,51,64,00,00,00,00,00,00,00},
		{00,00,00,57,58,00,00,00,00,00,00,00,00,00,00,63,51,62,62,62,62,62,62,62,64,00,00,00,00,00,00,00},
		{00,00,00,57,58,00,00,00,00,00,00,00,00,00,00,63,64,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,57,58,00,00,00,00,00,00,00,00,00,00,63,64,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,57,50,55,55,55,55,55,55,55,55,55,55,55,55,55,59,55,55,55,55,55,55,55,55,55,58,00,00,00},
		{00,00,00,56,56,56,56,56,56,56,56,56,56,56,56,56,56,56,60,56,56,56,56,56,56,56,56,50,58,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,63,64,00,00,00,00,00,00,00,00,00,00,57,58,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,63,64,00,00,00,00,00,00,00,00,00,00,57,58,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,63,64,00,00,00,00,00,00,00,00,00,00,39,39,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,63,64,00,00,00,00,00,00,00,00,00,00,63,64,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,63,64,00,00,00,00,00,00,00,00,00,00,63,64,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,63,64,00,00,63,51,61,61,61,61,61,61,51,64,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,63,64,00,00,63,51,62,62,62,62,62,62,62,62,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,63,64,00,00,63,64,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,63,64,00,00,63,64,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,63,64,00,00,63,64,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,63,64,00,00,63,64,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,63,51,61,61,51,64,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,63,62,62,62,62,62,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00},
		{00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00,00}
		};
		
		int[][] mapPE = {
		{49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49,49},
		{49,9,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,6,10,49,49,49,49,49},
		{49,4,33,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,30,34,8,49,49,49,49,49},
		{49,4,28,00,00,00,00,00,00,00,00,00,00,00,00,00,55,00,00,00,00,00,00,00,00,32,8,49,49,49,49,49},
		{49,4,28,00,00,00,00,00,00,00,00,00,00,00,00,00,55,00,00,00,00,00,00,00,00,32,8,49,49,49,49,49},
		{49,4,28,00,00,33,30,30,30,30,30,30,30,30,29,53,53,53,53,53,53,53,53,00,00,32,8,49,49,49,49,49},
		{49,4,28,00,00,28,13,14,14,14,14,14,14,15,25,00,00,00,00,00,55,00,00,00,00,32,8,49,49,49,49,49},
		{49,4,28,00,00,28,20,49,49,49,49,49,49,16,32,00,00,00,00,00,55,00,00,00,00,32,8,49,49,49,49,49},
		{49,4,28,00,00,28,19,18,18,18,18,18,18,17,32,00,00,25,26,26,26,26,26,26,26,35,7,6,6,6,10,49},
		{49,4,28,00,00,36,26,26,26,26,26,26,26,26,35,00,00,31,30,30,30,30,30,30,30,30,30,30,30,34,8,49},
		{49,4,36,00,00,00,00,00,00,00,00,00,00,00,00,55,00,55,00,00,00,00,00,00,00,00,00,00,00,32,8,49},
		{49,12,2,00,00,00,00,00,00,00,00,00,00,00,00,55,00,55,00,00,00,00,00,00,00,00,00,00,00,32,8,49},
		{49,49,49,24,14,14,14,14,14,14,14,14,14,14,15,00,00,25,26,26,26,26,26,26,26,26,27,00,00,32,8,49},
		{49,49,49,49,49,21,18,22,49,49,49,49,49,49,16,00,00,31,30,30,30,34,52,52,52,52,28,00,00,32,8,49},
		{49,49,49,49,49,16,53,71,67,67,67,67,67,67,67,00,00,67,67,67,70,32,52,52,52,52,28,00,00,32,8,49},
		{49,49,49,49,49,16,53,72,68,68,68,68,68,68,68,00,00,68,68,54,70,32,52,52,52,52,28,00,00,32,8,49},
		{49,49,49,49,49,24,14,23,49,49,49,49,49,4,28,00,00,32,28,45,45,35,52,52,52,52,36,00,00,32,8,49},
		{49,49,49,49,49,49,49,49,49,49,49,49,49,4,28,00,00,32,28,00,00,00,00,00,00,00,00,00,00,32,8,49},
		{49,49,49,49,49,49,49,49,49,49,49,49,49,4,28,00,00,32,28,00,00,00,00,00,00,00,00,00,00,32,8,49},
		{49,49,49,49,49,49,49,49,49,49,49,49,49,4,28,00,00,32,28,00,00,25,26,26,26,26,26,26,26,35,8,49},
		{49,49,49,49,49,49,49,49,49,49,49,49,49,4,28,00,00,32,28,00,00,32,1,2,2,2,2,2,2,2,11,49},
		{49,49,49,49,49,49,49,49,49,49,49,49,49,4,28,55,55,32,28,00,00,32,8,49,49,49,49,49,49,49,49,49},
		{49,49,49,49,49,49,49,49,49,49,49,49,49,4,28,00,00,35,36,00,00,32,8,49,49,49,49,49,49,49,49,49},
		{49,49,49,49,49,49,49,49,49,49,49,49,49,4,28,00,00,00,00,00,00,32,8,49,49,49,49,49,49,49,49,49},
		{49,49,49,49,49,49,49,49,49,49,49,49,49,4,28,00,00,00,00,00,00,32,8,49,49,49,49,49,49,49,49,49},
		{49,49,49,49,49,49,49,49,49,49,49,49,49,4,36,26,26,26,26,26,26,35,8,49,49,49,49,49,49,49,49,49},
		{49,49,49,49,49,49,49,49,49,49,49,49,49,12,2,2,2,2,2,2,2,2,11,49,49,49,49,49,49,49,49,49},
		};
		
		InitialPosition initialPos = null;
		for(int i=0;i<27;i++){
			for(int j=0;j<32;j++){
				pista.setCell(j,i,mapPista[i][j]);
				parteExterna.setCell(j,i,mapPE[i][j]);
				if(initialPos == null && InitialPosition.isInitialPosition(mapPista[i][j]))
					initialPos = new InitialPosition(i, j);
			}
		}
		calcDistanceMatrix(initialPos, mapPE);
		
		return initialPos;
	}
	
	public void destroy(){
		pista = null;
		parteExterna = null;
		jogador = null;
		faixas = null;
		initialPos = null;
		distance = null;
	}
}
