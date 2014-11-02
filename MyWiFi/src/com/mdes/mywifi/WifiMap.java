package com.mdes.mywifi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.net.wifi.ScanResult;
import android.util.Log;

public class WifiMap {

	public HashMap<String,Wifi> wifiMap;
	private List<String> SSIDList;

	public WifiMap(){
		wifiMap = new HashMap<String,Wifi>();
		SSIDList = new ArrayList<String>();
	}

	public void putValue(List<ScanResult> resultWifiList){

		for (int i = 0; i < resultWifiList.size(); i++) {
			SSIDList.add(resultWifiList.get(i).SSID);

			//Comprobar si la red ya existe en el HashMap
			//Si no existe
			if (!wifiMap.containsKey(resultWifiList.get(i).SSID)){
				Log.i("INFO", resultWifiList.get(i).SSID + " no existía en el HashMap");
				//Crear nuevo objeto de la clase Wifi con ella
				Wifi wifi = new Wifi(resultWifiList.get(i));
				wifiMap.put(resultWifiList.get(i).SSID, wifi);
				Log.i("INFO", resultWifiList.get(i).SSID + " guardado en el HashMap");
			}
			else
			{				
				Log.i("INFO", resultWifiList.get(i).SSID + " ya existía en el HashMap");
				wifiMap.get(resultWifiList.get(i).SSID).saveLevel(resultWifiList.get(i).level);

			}
		}
		Iterator it = wifiMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry)it.next();
			//		System.out.println(e.getKey() + " " + e.getValue());
			if(!SSIDList.contains(e.getKey())){
				wifiMap.get(e.getKey()).saveLevel(0);
			}
		}

	}
	
	public Wifi getWifi(String SSID){
		return wifiMap.get(SSID);
	}


}
