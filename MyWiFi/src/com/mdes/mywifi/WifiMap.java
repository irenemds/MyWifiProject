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
	private List<String> representableList = new ArrayList<String>();	
	public static String[] representableArray;
	
	public void putValue(List<ScanResult> resultWifiList){
		
		SSIDList = new ArrayList<String>();
		
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
				Log.i("ITERATOR", "La red " + e.getKey() + " ya no est� disponible.");
				wifiMap.get(e.getKey()).saveLevel(-100);
			}
		}

	}

	public void putZeros(){
		Iterator it = wifiMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry)it.next();
			wifiMap.get(e.getKey()).saveLevel(-100);
		}
	}
	
	public Wifi getWifi(String SSID){
		return wifiMap.get(SSID);
	}
	
	public String[] getKeys(){
		return (String[]) wifiMap.keySet().toArray();
		
	}
	
	public void getRepresentableKey(){
		Iterator it = wifiMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry)it.next();
			if(wifiMap.get(e.getKey()).isRepresentable()){
				representableList.add((String) e.getKey());
			}
			representableArray = new String[representableList.size()-1];
			for( int i = 0; i < representableList.size()-1; i++)
				representableArray[i] = representableList.get(i);
		}
	}
	


}
