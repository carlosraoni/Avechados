package br.com.avechados.main;
import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.AlertType;
import javax.microedition.lcdui.Choice;
import javax.microedition.lcdui.Command;
import javax.microedition.lcdui.CommandListener;
import javax.microedition.lcdui.Displayable;
import javax.microedition.lcdui.List;

public class MenuPrincipal extends List implements CommandListener {
	
	private AvechadosMidlet midlet;
	
	private Command selectCommand = new Command("Selecionar", Command.ITEM, 1);
	private Command exitCommand = new Command("Sair", Command.EXIT, 1);
	
	private Alert alert;
	
	public MenuPrincipal ( AvechadosMidlet midlet ){
		super ("Avechados e Irados", Choice.IMPLICIT);
		this.midlet = midlet;
		append("Natal Arena", null);
		append("Crato Circuit", null);
		append("Angola High Speed", null);
		append("Recordes", null);
		append("Creditos", null);
		addCommand(exitCommand);
		addCommand(selectCommand);
		setCommandListener(this);
		setCommandListener(this);
	}
	
	public void commandAction ( Command c, Displayable d ){
		if ( c == exitCommand ){
			midlet.mainMenuScreenQuit();
			return;
		}
		else if ( c == selectCommand ){
			processMenu();
			return;
		}
		else{
			processMenu();
			return;
		}
	}
	
	private void processMenu(){
		try {
			List down = (List) midlet.display.getCurrent();
			switch(down.getSelectedIndex()){
				case 0: scnNovoJogo(0); break;
				case 1: scnNovoJogo(1); break;
				case 2: scnNovoJogo(2); break;
				case 3: scnRecordes(); break;
				case 4: scnCreditos(); break;
			};
		}
		catch ( Exception ex ){
			System.out.println("processMenu::" + ex);
		}
	}
	
	private void scnNovoJogo(int pista){
		midlet.mainMenuScreenShow(null,pista);
	}
	
	private void scnRecordes(){
		alert = new Alert("Recordes", "Recorde........", null, null);
		alert.setTimeout(Alert.FOREVER);
		midlet.mainMenuScreenShow(alert,0);
	}
	
	private void scnCreditos(){
		alert = new Alert("Creditos",
							"Carlos Raoni\ncarlosraoni@yahoo.com.br\n\nGilbran Silva de Andrade\ngilbran@lcc.ufrn.br", null, null);
		alert.setTimeout(Alert.FOREVER);
		alert.setType(AlertType.INFO);
		midlet.mainMenuScreenShow(alert,0);
	}
}
