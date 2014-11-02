package com.mdes.mywifi;

import java.util.List;

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
		bucle = true;
	}
	
	public void run(){		
		while(bucle){
			try{
				try{
				Log.i("INFO","Comienza hilo");
				wifiManager.startScan();
				resultWifiList = wifiManager.getScanResults();
				if (resultWifiList.size()!=0){
					Log.i("INFO","Number Of Wifi connections :"+resultWifiList.size());
					for (int i = 0; i < resultWifiList.size(); i++) {
						Integer level = resultWifiList.get(i).level;
						String SSID = resultWifiList.get(i).SSID;
						Log.i("INFO","SSID: "+resultWifiList.get(i).SSID);	
						wifiList.saveLevel(resultWifiList.get(i));
					}
					Intent i = new Intent();
					i.setAction("com.mdes.mywifi.timer");
					wifiList.sendBroadcast(i);
					wifiList.runOnUiThread(new Runnable() {
						@Override
						public void run() {
							CustomAdapter adapter = new CustomAdapter(wifiList.getApplicationContext(), resultWifiList);
							wifiList.getLista().setAdapter(adapter);
					      //adapter.updateWifiList(resultWifiList);

						}});
				wifiList.updateValues(resultWifiList);
				}
				else{Log.i("INFO","No encuentra redes");}		



			}catch(NullPointerException e){
				e.printStackTrace();
			}
			sleep(10000);
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
