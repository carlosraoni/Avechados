package car.com.input;

import java.util.BitSet;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Input;
import com.car.utils.Constants;
import com.car.utils.Controls;

public class PlayerInputHandler implements PlayerInputHandlerInterface {

	// Variaveis usadas nos controles do jogador 
	private float leftScreenX, middleScreenx, rightScreenX;
	
	public PlayerInputHandler(){
		this.middleScreenx = Constants.TARGET_RES_W / 2;
		this.leftScreenX = this.middleScreenx - Constants.BRAKE_CONTROL_RANGE;
		this.rightScreenX = this.middleScreenx + Constants.BRAKE_CONTROL_RANGE;
	}
	
	@Override
	public BitSet getPlayerControls() {
		// Uso de bitSet para interpretação dos controles devido a necessidade de realizar mais de um controle
		// no carro no mesmo passo da simulação física
		BitSet controls = new BitSet();

		
		controls.set(Controls.TDC_DOWN.ordinal(), Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN));
		controls.set(Controls.TDC_UP.ordinal(), !Gdx.input.isKeyPressed(Input.Keys.DPAD_DOWN));
//		controls.set(Controls.TDC_UP.ordinal(), Gdx.input.isKeyPressed(Input.Keys.DPAD_UP));
		controls.set(Controls.TDC_RIGHT.ordinal(), Gdx.input.isKeyPressed(Input.Keys.DPAD_RIGHT));
		controls.set(Controls.TDC_LEFT.ordinal(), Gdx.input.isKeyPressed(Input.Keys.DPAD_LEFT));
		
		if(Gdx.input.isTouched()){
//			System.out.println("X: " + Gdx.input.getX() + ", Y: " + Gdx.input.getY());			
			int inputTouchX = Gdx.input.getX();
			
			// Virar para direita
			if(inputTouchX > rightScreenX){				
				controls.set(Controls.TDC_RIGHT.ordinal(), true);
			}
			// Virar para esquerda
			else if(inputTouchX < leftScreenX){				
				controls.set(Controls.TDC_LEFT.ordinal(), true);
			}
			// Freio e r�
			else{
				controls.set(Controls.TDC_UP.ordinal(), false);
				controls.set(Controls.TDC_DOWN.ordinal(), true);
			}
		}

		
		return controls;			
	}

}
