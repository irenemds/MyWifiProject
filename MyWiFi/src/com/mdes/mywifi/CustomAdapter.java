package com.mdes.mywifi;

import java.util.List;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;


public class CustomAdapter extends BaseAdapter {
	Context contexto;
	List<ScanResult> scanResult;

	
	
	public CustomAdapter (Context contexto, List<ScanResult> scanResult){
		this.contexto = contexto;
		this.scanResult = scanResult;
	}
	

    private class ViewHolder {
        ImageView foto;
        TextView SSID;
        TextView level;
    }

	public View getView (int posicion, View vistaReciclada, ViewGroup padre){
		
		ViewHolder holder = null;
		
		LayoutInflater inflador = (LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		
		//Si no se ha cargado el layout previamente
		if (vistaReciclada == null){
			vistaReciclada = inflador.inflate(R.layout.elemento_lista, null);
			holder = new ViewHolder();
            holder.level = (TextView) vistaReciclada.findViewById(R.id.level);
            holder.SSID = (TextView) vistaReciclada.findViewById(R.id.SSID);
            holder.foto = (ImageView) vistaReciclada.findViewById(R.id.foto);
            vistaReciclada.setTag(holder);
        }
        else {
            holder = (ViewHolder) vistaReciclada.getTag();
        }
		
		ScanResult result = (ScanResult) getItem(posicion);
		Log.i("layout_info", result.toString());
		holder.SSID.setText(Integer.toString(result.level)+" dBm");
		holder.level.setText(result.SSID); 
		
		return vistaReciclada;
		
	}
	
	public int getCount() {
        return scanResult.size();
     }
	public Object getItem(int posicion) {
		return scanResult.get(posicion);
	}

	public long getItemId(int posicion) {
		return scanResult.indexOf(getItem(posicion));
	}
	
}
