package com.mdes.mywifi.broadcastreceiver;


import com.mdes.mywifi.CurrentAP;
import com.mdes.mywifi.WifiThread;
import com.mdes.mywifi.WifiMap;
import com.mdes.mywifi.activity.WifiListActivity;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * Este BroadCastReceiver se encarga de manejar lo que ocurre cuando el Wifi se apaga
 *
 */
public class WifiChangeReceiver extends BroadcastReceiver {

	private boolean isDialog = false;

	@Override
	public void onReceive(Context c, Intent intent) {

		int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);		
		//Realiza distintas operaciones en función del estado del Wifi
		switch(extraWifiState){
		//Si se está desactivando ya no estaremos conectados a ninguna red
		case WifiManager.WIFI_STATE_DISABLING:
			WifiThread.currentAP = new CurrentAP();
			break;
			//Si está desactivado muestra un diálogo	
		case WifiManager.WIFI_STATE_DISABLED:
			Log.i("INFO", "Broadcast -  Wifi off");
			WifiMap.reset();
			wifiAlertDialog(c);
			break;
			//Si se activa el contenido es sólo para informar
		case WifiManager.WIFI_STATE_ENABLED:
			Log.i("INFO", "Broadcast -  Wifi on, lanza hilo");
			break;	

		case WifiManager.WIFI_STATE_UNKNOWN:
			Log.i("INFO", "Broadcast -  Wifi desconocido");
			break;
		}
	}

	/**
	 * Función para mostrar diálogo cuando el Wifi del dispositivo está apagado.
	 * @param c Contexto de la actividad en la que se encuentra en el momento de la llamada.
	 */
	public void wifiAlertDialog(Context c){
		if(isDialog == false)
		{
			AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(c);

			alertDialogBuilder.setTitle("Wifi desactivado");

			// Opciones: encender Wifi o Salir de la aplicación.
			alertDialogBuilder
			.setMessage("Es necesario activar el Wifi para usar esta aplicación")
			.setCancelable(false)
			.setPositiveButton("Activar",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					WifiListActivity.wifiManager.setWifiEnabled(true);
					isDialog = false;
				}
			})
			.setNegativeButton("Salir",new DialogInterface.OnClickListener() {
				public void onClick(DialogInterface dialog,int id) {
					isDialog = false;
					System.exit(0);
				}
			});

			// crear AlertDialog
			AlertDialog alertDialog = alertDialogBuilder.create();
			alertDialog.show();
			isDialog = true;
		}
	}

}


