package com.mdes.mywifi;

import java.util.List;

import com.mdes.mywifi.chart.MultipleGraph;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

public class HiloWifi extends Thread{

	private static boolean bucle;
	private WifiManager  wifiManager;
	private WifiList wifiList;
	private List<ScanResult> resultWifiList;
	//	public LevelList levelList;
	public String SSID;
	public int level;
	//	public int contador;

	public HiloWifi(WifiList wifiList){
		this.wifiList = wifiList;
		wifiManager = this.wifiList.getWifiManager();
		//La variabe bucle sol será false cuando lo indique alguna de las actividades
		bucle = true;
		MultipleGraph multipleGraph = new MultipleGraph();}

	public void run(){
		Wifi.contador = 1;
		Line.lineNumber = 0;
		while(bucle){
			try{
				try{
					if (wifiManager.isWifiEnabled()){
						wifiManager.startScan();
						resultWifiList = wifiManager.getScanResults();
						if (resultWifiList.size()!=0){
							Log.i("HILO","Number Of Wifi connections :"+resultWifiList.size());
							wifiList.updateValues(resultWifiList);
							Intent i = new Intent();
							i.setAction("com.mdes.mywifi.timer");
							wifiList.sendBroadcast(i);
						}
						else{Log.i("HILO","No encuentra redes");}		
					}
					else{Log.e("HILO","Wifi Apagado");}
					//Guardar ceros en las redes que no encuentre
					wifiList.saveLevel();
				}catch(NullPointerException e){
					e.printStackTrace();
				}
				//TODO Comprobar ceros
				Wifi.contador++;
				sleep(4000);
			}catch(InterruptedException e){
				e.printStackTrace();
				Log.e("INFO", "Error en el hilo");}
		}

	}
	
	public void setBucleOff(){
		bucle = false;
		WifiList.isThread = false;
		Log.i("INFO","fuera del hilo (bucle)");
	}

}
