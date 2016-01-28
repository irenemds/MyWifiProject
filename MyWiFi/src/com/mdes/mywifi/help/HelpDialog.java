package com.mdes.mywifi.help;

import android.app.AlertDialog;
import android.content.Context;
/**
 * Esta clase contiene los métodos empleados para la creación y visualización de los diálogos de 
 * ayuda que aparecen en las ventanas de la aplicación.
 *
 */
public class HelpDialog {
	
	/**
	 * Este método se emplea desde el menu de la aplicacion para mostrar
	 * los mensajes de ayuda de cada actividad.
	 * @param c Contexto
	 * @param title Título del mensaje
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
