package com.mdes.mywifi;

import java.util.List;

import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.util.Log;

public class HiloWifi extends Thread{
	
	private static boolean bucle;
	private WifiManager  wifiManager;
	private WifiList wifiList;
	private List<ScanResult> resultWifiList;
	public LevelList levelList;
	public String SSID;
	public int level;
	public int contador;
	
	public HiloWifi(WifiList wifiList, LevelList levelList, int contador){
		this.wifiList = wifiList;
		wifiManager = this.wifiList.getWifiManager();
		this.levelList = levelList;
		bucle = true;
		this.contador = contador;
	}
	
	public void run(){		
		while(bucle){
			try{
				try{
				Log.i("INFO","Comienza hilo"+ contador );
				wifiManager.startScan();
				resultWifiList = wifiManager.getScanResults();
				if (resultWifiList.size()!=0){
					Log.i("INFO","Number Of Wifi connections :"+resultWifiList.size());
					for (int i = 0; i < resultWifiList.size(); i++) {
						Integer level = resultWifiList.get(i).level;
						String SSID = resultWifiList.get(i).SSID;
						levelList.saveLevel(level , SSID);
//						Log.i("INFO","SSID: "+resultWifiList.get(i).SSID);
					}
					wifiList.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							CustomAdapter adapter = new CustomAdapter(wifiList.getApplicationContext(), resultWifiList);
							wifiList.getLista().setAdapter(adapter);
					      //adapter.updateWifiList(resultWifiList);

						}});
				wifiList.updateValues(resultWifiList, levelList);
				}
				else{Log.i("INFO","No encuentra redes");}		



			}catch(NullPointerException e){
				e.printStackTrace();
			}
			sleep(5000);
			}catch(InterruptedException e){
				e.printStackTrace();
				Log.e("INFO", "Error en el hilo");}
		}

	}
	
	public void setBucleOff(){
		bucle = false;
		
		Log.i("INFO","fuera del hilo (bucle)");
	}

}
