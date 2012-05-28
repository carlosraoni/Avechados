package br.com.avechados.model;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.game.TiledLayer;

public class CarroAgente extends Carro{

	private TiledLayer parteExterna;
	private int [][] distance;
	private int n, m, maxDist;
	private boolean distanciaMaximaNoRange = false;
	private static final int MAX_RANGE = 3;
	public int pos;
	private int iter = 0;
	int [][] dir = {{0, -1}, {-1, -1}, {-1, 0}, {-1, 1}, {0, 1} , {1, 1}, {1, 0}, {1, -1}};
	int [] dirPorRot = {0, 0, 1, 1, 2, 3, 3, 4, 5, 5, 6, 7, 7};
	
	public boolean [][] pistas = {
	{true,true,false,false,true,true,true,false,false,false,true,true,true,true,false,false,true,true},
	{true,true,true,false,false,true,true,true},
	{true,true,true,true,false,false,false,true,false,false}
	};
	
	public CarroAgente(Image i,int num,int posX,int posY,TiledLayer parteExterna, int [][] distance) throws Exception{
		super(i,num,posX,posY);
		this.parteExterna = parteExterna;
		this.distance = distance;
		this.n = distance.length;
		this.m = distance[0].length;
		pos = 0;
		iter = 0;
		maxDist = 0;

		for(int r = 0; r < n; r++){
			for(int c = 0; c < m; c++){
				if(distance[r][c] > maxDist)
					maxDist = distance[r][c];
			}
		}
	}

	
	public void atualiza(int npista){
		calcVel();		
		simularDecisao();
//		setFrame(direcao-1);		
	}

	
	private void simularDecisao() {	
		int carX = getX(),carY = getY();
		int pcolunas[] = {(carX+3)/35,(carX+18)/35,(carX+3)/35,(carX+18)/35};
		int plinhas[] = {(carY+3)/35,(carY+3)/35,(carY+18)/35,(carY+18)/35};
		
		int manter = 0, direita = 1, esquerda = 2;
		int count [] = {0, 0, 0};
		
		for(int i=0; i<pcolunas.length; i++){
			int row = plinhas[i];
			int col = pcolunas[i];			
			int direction = dirPorRot[direcao];
			int rightDirection = getRightDir(direction);
			int leftDirection = getLeftDir(direction);
			//int rangeDir [] = {0, 1, 2, 3, 4, 5, 6, 7};
			int rangeDir [] = {
					direction, 
					rightDirection, 
					leftDirection, 
					getRightDir(rightDirection), 
					getLeftDir(leftDirection),
					getRightDir(getRightDir(rightDirection)),
					getLeftDir(getLeftDir(leftDirection))};
			
			int dirMaxSum = getMaxDir(row, col, rangeDir);
			
			if(direction != dirMaxSum){
				int nr = 0, nl = 0;
				if(dirMaxSum > direction){
					nr = dirMaxSum - direction;
					nl = direction + 1 + (7 - dirMaxSum);
				}
				else{
					nl = direction - dirMaxSum;
					nr = dirMaxSum + 1 + (7 - direction);
				}
				if(nr < nl){					
					count[direita]++;
				}
				else{					
					count[esquerda]++;
				}
			}
			else{
				count[manter]++;
			}
		}
		int maior = 0, indice = 0;
		for(int i=0; i<count.length; i++){
			if(count[i] > maior){ 
				maior = count[i];
				indice = i;
			}
		}
		if(indice == manter){
			//System.out.println("Manter posicao");
		}else if(indice == direita){
			turnRight();
			//System.out.println("Virar a direita");
		}else{
			turnLeft();
			//System.out.println("Virar a esquerda");
		}
	}


	private int getRightDir(int direction) {		
		return (direction + 1) % dir.length;
	}


	private int getLeftDir(int direction) {
		if(direction == 0)
			return dir.length - 1;
		return direction - 1;
	}


	private int getMaxDir(int row, int col, int [] dirIndexes) {
		int maxSum = -10000, dirMaxSum = -1;
		for(int i=0; i < dirIndexes.length; i++){
			int dx = dir[dirIndexes[i]][0];
			int dy = dir[dirIndexes[i]][1];
			int sum = calcSum(row, col, dx, dy, MAX_RANGE);
			if(dirMaxSum == -1 || sum > maxSum){
				dirMaxSum = dirIndexes[i];
				maxSum = sum;
			}
		}
		return dirMaxSum;
	}


	private int calcSum(int row, int col, int dx, int dy, int maxRange) {
		int sum = 0;
		int lastDistance = distance[row][col];
		for(int i=0; i<maxRange; i++){
			int nRow = row + dx;
			int nCol = col + dy;
			if(nRow < 0 || nRow >= n || nCol < 0 || nCol >= m || distance[nRow][nCol] < 0)
				break;
			sum += getReward(nRow, nCol, lastDistance, i + 1);
			row = nRow;
			col = nCol;
			lastDistance = distance[row][col];
		}
		return sum;
	}


	private int getReward(int row, int col, int lastDistance, int iter) {
		int tot = (distance[row][col] - lastDistance) * iter;
		if(distance[row][col] == maxDist){
			//System.out.println("Ponto final atingido, desabilitando manobras.");
			distanciaMaximaNoRange = true;
		}
		if(distanciaMaximaNoRange && distance[row][col] < 10){
			tot += maxDist;
		}
		return tot;
	}
}