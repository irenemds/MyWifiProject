package com.mdes.mywifi.generic;

import java.util.List;

import com.mdes.mywifi.R;
import com.mdes.mywifi.R.id;
import com.mdes.mywifi.R.layout;
import com.mdes.mywifi.thread.WifiThread;
import com.mdes.mywifi.wifi.WifiLevelImage;
import com.mdes.mywifi.wifi.WifiSecurityImage;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.wifi.ScanResult;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Esta clase implementa el adaptador customizado empleado para
 * representar cada una de las redes wifi en la lista inicial.
 *
 */
public class CustomAdapter extends BaseAdapter {
	Context contexto;
	List<ScanResult> scanResult;

	public CustomAdapter (Context contexto, List<ScanResult> scanResult){
		this.contexto = contexto;
		this.scanResult = scanResult;
	}


	private class ViewHolder {
		ImageView barraNivel;
		ImageView security;
		TextView SSID;
		TextView level;
	}
	/**
	 * Esta función rellena los objetos del ListView con el formato de la vista indicada.
	 */
	public View getView (int posicion, View vistaReciclada, ViewGroup padre){

		ViewHolder holder = null;
		LayoutInflater inflater = (LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		vistaReciclada = inflater.inflate(R.layout.elemento_lista, null);
		holder = new ViewHolder();
		holder.level = (TextView) vistaReciclada.findViewById(R.id.level);
		holder.SSID = (TextView) vistaReciclada.findViewById(R.id.SSID);
		holder.barraNivel = (ImageView) vistaReciclada.findViewById(R.id.barraNivel);
		holder.security = (ImageView) vistaReciclada.findViewById(R.id.seguridad);
		vistaReciclada.setTag(holder);

		ScanResult result = (ScanResult) getItem(posicion);

		holder.level.setText(result.BSSID);
		if (result.BSSID.equals(WifiThread.currentAP.getBSSID()))
		{
			holder.SSID.setTypeface(Typeface.DEFAULT_BOLD);
			holder.SSID.setTextColor(Color.parseColor("#009C8F"));
		}
		holder.SSID.setText(result.SSID); 
		holder.barraNivel.setImageResource(WifiLevelImage.getWifiLevelImage(result.level));
		holder.security.setImageResource(WifiSecurityImage.getWifiSecurityImage(result.BSSID));
		return vistaReciclada;
	}

	public int getCount() throws NullPointerException {
		return scanResult.size();
	}
	public Object getItem(int posicion) {
		return scanResult.get(posicion);
	}

	public long getItemId(int posicion) {
		return scanResult.indexOf(getItem(posicion));
	}
}
