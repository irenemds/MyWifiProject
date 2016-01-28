package com.mdes.mywifi.help;

import android.app.AlertDialog;
import android.content.Context;
/**
 * Esta clase contiene los m�todos empleados para la creaci�n y visualizaci�n de los di�logos de 
 * ayuda que aparecen en las ventanas de la aplicaci�n.
 *
 */
public class HelpDialog {
	
	/**
	 * Este m�todo se emplea desde el menu de la aplicacion para mostrar
	 * los mensajes de ayuda de cada actividad.
	 * @param c Contexto
	 * @param title T�tulo del mensaje
	 * @param text Texto del Mensaje
	 */
	public HelpDialog(Context c,String title, String text){

			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);
			alertDialogBuilder.setTitle(title);

			alertDialogBuilder
			.setMessage(text)
			.setCancelable(true);

			// crear AlertDialog
			AlertDialog alertDialog = alertDialogBuilder.create();

			// mostrar
			alertDialog.show();
		}

}
