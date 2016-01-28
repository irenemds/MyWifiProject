package com.mdes.mywifi.broadcastreceiver;

import com.mdes.mywifi.R;
import com.mdes.mywifi.R.id;
import com.mdes.mywifi.R.layout;
import com.mdes.mywifi.activity.WifiListActivity;
import com.mdes.mywifi.wifi.WifiMap;

import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;

/**
 * Este BroadCastReceiver muestra un Diálogo cuando la aplicación no encuentra redes,
 * también lo cierra cuando las encuentra.
 *
 */
public class WifiNotFoundReceiver extends BroadcastReceiver {

	private Dialog dialog; 
	private boolean isDialog;

	@Override
	public void onReceive(Context c, Intent intent) {
		//Comprueba el valor del Extra para saber si ha encontrado redes o no
		//Si ya existe un diálogo abierto no hace falta abrir otro.
		if(!intent.getBooleanExtra("WifiFound", false)&& !isDialog && WifiListActivity.wifiManager.isWifiEnabled()){

			Log.i("INFO","No hay redes");
			
			WifiMap.reset();
			
			dialog = new Dialog(c);

			dialog.setContentView(R.layout.no_wifi_layout);
			dialog.setTitle("No se encuentran redes");
			dialog.setCancelable(false);
			TextView txt = (TextView) dialog.findViewById(R.id.txt);
			txt.setText("Se están buscando redes ...");
			isDialog= true;
			dialog.show();
		}
		//Si hay dialogo abierto y se han encontrado redes lo cierra
		else if(intent.getBooleanExtra("WifiFound", false) && isDialog){
			dialog.dismiss();
			isDialog = false;
		}


	}
}

