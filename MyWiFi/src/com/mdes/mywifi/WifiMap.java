package com.mdes.mywifi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.net.wifi.ScanResult;
import android.util.Log;

public class WifiMap {

	public static HashMap<String,Wifi> wifiMap = new HashMap<String,Wifi>();
	private List<String> SSIDList = new ArrayList<String>();

	public void putValue(List<ScanResult> resultWifiList){

		for (int i = 0; i < resultWifiList.size(); i++) {
			SSIDList.add(resultWifiList.get(i).SSID);

			//Comprobar si la red ya existe en el HashMap
			//Si no existe
			if (!wifiMap.containsKey(resultWifiList.get(i).SSID)){
				//Crear nuevo objeto de la clase Wifi con ella
				Wifi wifi = new Wifi(resultWifiList.get(i));
				wifiMap.put(resultWifiList.get(i).SSID, wifi);
			}
			else
			{				
				wifiMap.get(resultWifiList.get(i).SSID).saveLevel(resultWifiList.get(i).level);

			}
		}
		Iterator it = wifiMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry)it.next();
			if(!SSIDList.contains(e.getKey())){
				Log.i("INFO", "La red " + e.getKey() + " ya no está disponible.");
				wifiMap.get(e.getKey()).saveLevel(0);
			}
		}

	}
	
	public Wifi getWifi(String SSID){
		return wifiMap.get(SSID);
	}


}
