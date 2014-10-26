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
	int[] signals = new int[]{
	        R.drawable.signal1,
	        R.drawable.signal2,
	        R.drawable.signal3,
	        R.drawable.signal4,
	        R.drawable.signal5
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
		
		LayoutInflater inflater = (LayoutInflater)contexto.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

		
		//Si no se ha cargado el layout previamente
		if (vistaReciclada == null){
			vistaReciclada = inflater.inflate(R.layout.elemento_lista, null);
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

			holder.level.setText(Integer.toString(result.level)+" dBm");
			holder.SSID.setText(result.SSID); 
			//	Diferentes imágenes de prueba en función del nivel de señal
//			if(result.level <=0 && result.level >-30){
//				holder.barraNivel.setImageResource(R.drawable.signal5);}
//			else if(result.level <=-30 && result.level >-50){
//				holder.barraNivel.setImageResource(R.drawable.signal4);}
//			else if(result.level <=-50 && result.level >-70){
//				holder.barraNivel.setImageResource(R.drawable.signal3);}
//			else if(result.level <=-70 && result.level >-80){
//				holder.barraNivel.setImageResource(R.drawable.signal2);}
//			if(result.level <=-80){
//				holder.barraNivel.setImageResource(R.drawable.signal1);
//			}
		
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
	
	
	/*
	 * Método para actualizar los valores del ListView
	 * Recibe la nueva lista, borra la anterior 
	 * y la cambia por la nueva
	 */
	public void updateWifiList(List<ScanResult> scanRec) {
	    scanResult.clear();
	    scanResult.addAll(scanRec);
	    this.notifyDataSetChanged();
	}
	
}
