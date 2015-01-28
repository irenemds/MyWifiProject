package com.mdes.mywifi;

import java.util.List;

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

	public View getView (int posicion, View vistaReciclada, ViewGroup padre){
		try{
			ViewHolder holder = null;

			LayoutInflater inflater = (LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);


			//Si no se ha cargado el layout previamente
			if (vistaReciclada == null){
				vistaReciclada = inflater.inflate(R.layout.elemento_lista, null);
				holder = new ViewHolder();
				holder.level = (TextView) vistaReciclada.findViewById(R.id.level);
				holder.SSID = (TextView) vistaReciclada.findViewById(R.id.SSID);
				holder.barraNivel = (ImageView) vistaReciclada.findViewById(R.id.barraNivel);
				holder.security = (ImageView) vistaReciclada.findViewById(R.id.seguridad);
				vistaReciclada.setTag(holder);
			}
			else {
				holder = (ViewHolder) vistaReciclada.getTag();
			}

			ScanResult result = (ScanResult) getItem(posicion);

			holder.level.setText(Integer.toString(result.level)+" dBm");
			if (result.SSID.equals(HiloWifi.currentAP.getSSID()))
			{
				holder.SSID.setTypeface(Typeface.DEFAULT_BOLD);
				holder.SSID.setTextColor(Color.parseColor("#009C8F"));
			}
			holder.SSID.setText(result.SSID); 
			if (result.SSID == HiloWifi.currentAP.getSSID()){
				holder.level.setTypeface(null, Typeface.BOLD);
				holder.SSID.setTypeface(null, Typeface.BOLD);
			}
			
			holder.barraNivel.setImageResource(WifiLevelImage.getWifiLevelImage(result.level));
			holder.security.setImageResource(WifiSecurityImage.getWifiSecurityImage(result.SSID));
//			if(posicion % 2 == 0){
//				vistaReciclada.setBackgroundColor(color.darker_gray);
//			}
			
		}catch(Exception e){
			e.printStackTrace();
			LogManager lm = new LogManager(e);
		}

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
