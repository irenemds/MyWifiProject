package com.mdes.mywifi;

import java.util.List;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.mdes.mywifi.activity.WifiListActivity;
import com.mdes.mywifi.chart.FrequencyGraphActivity;
import com.mdes.mywifi.chart.LinkSpeedGraphActivity;
import com.mdes.mywifi.chart.MultipleGraph;

public class HiloWifi extends Thread{

	public static int contador;

	private static boolean bucle;
	private WifiManager  wifiManager;
	private WifiListActivity wifiList;
	private List<ScanResult> resultWifiList;
	public String SSID;
	public int level;

	public static CurrentAP currentAP;

	public HiloWifi(WifiListActivity wifiList){
		this.wifiList = wifiList;
		wifiManager = this.wifiList.getWifiManager();
		//La variabe bucle sol será false cuando lo indique alguna de las actividades
		bucle = true;
		MultipleGraph multipleGraph = new MultipleGraph();
		currentAP = new CurrentAP();
	}

	public void run(){
		contador = 1;
		Line.lineNumber = 0;
		while(bucle){
			try{
				try{
					if (wifiManager.isWifiEnabled()){
						FrequencyGraphActivity.mDataset = new XYMultipleSeriesDataset();
						FrequencyGraphActivity.mRenderer = new XYMultipleSeriesRenderer();
						wifiManager.startScan();
						WifiMap.getRepresentable();
						currentAP.updateAP(wifiManager.getConnectionInfo());
						resultWifiList = wifiManager.getScanResults();
						if (resultWifiList.size()!=0){
							Log.i("HILO","Number Of Wifi connections :"+resultWifiList.size());
							wifiList.updateValues(resultWifiList);
							Intent i = new Intent();
							i.setAction("com.mdes.mywifi.timer");
							wifiList.sendBroadcast(i);

							Intent i2 = new Intent();
							i2.setAction("com.mdes.mywifi.wififound");
							i2.putExtra("WifiFound", true);
							wifiList.sendBroadcast(i2);

						}else{
							Log.e("HILO","No hay redes");
							Intent i = new Intent();
							i.setAction("com.mdes.mywifi.wifinotfound");
							i.putExtra("WifiFound", false);
							wifiList.sendBroadcast(i);
						}
					}		
					else{Log.e("HILO","Wifi Apagado");}
					//Guardar ceros en las redes que no encuentre
					wifiList.saveLevel();
				}catch(NullPointerException e){
					e.printStackTrace();
				}
				//TODO Comprobar ceros
				contador++;
				sleep(500);
			}catch(InterruptedException e){
				e.printStackTrace();
				Log.e("INFO", "Error en el hilo");}
		}

	}

	public void setBucleOff(){
		bucle = false;
		WifiListActivity.isThread = false;
		Log.i("INFO","fuera del hilo (bucle)");
	}

}
