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
	int[] flags = new int[]{
	        R.drawable.contento,
	        R.drawable.triste,
	        R.drawable.enfadado
	    };
	
	
	public CustomAdapter (Context contexto, List<ScanResult> scanResult){
		this.contexto = contexto;
		this.scanResult = scanResult;
	}
	

    private class ViewHolder {
        ImageView barraNivel;
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
            holder.barraNivel = (ImageView) vistaReciclada.findViewById(R.id.barraNivel);
            vistaReciclada.setTag(holder);
        }
        else {
            holder = (ViewHolder) vistaReciclada.getTag();
        }
		
		ScanResult result = (ScanResult) getItem(posicion);
		Log.i("layout_info", result.toString());
		if (result == null){
			holder.SSID.setText("No hay redes que mostrar");
		}
		else {
			holder.level.setText(Integer.toString(result.level)+" dBm");
			holder.SSID.setText(result.SSID); 
			//	Diferentes imágenes de prueba en función del nivel de señal
			if(result.level <=0 && result.level >-60){
				holder.barraNivel.setImageResource(R.drawable.contento);}
			if(result.level <=-60 && result.level >-80){
				holder.barraNivel.setImageResource(R.drawable.triste);}
			if(result.level <=-80){
				holder.barraNivel.setImageResource(R.drawable.enfadado);
			}
		}
		
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
