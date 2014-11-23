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
	private static List<String> representableList = new ArrayList<String>();	
	public static String[] representableArray;
	public static int[] channelAP;
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
				wifiMap.get(resultWifiList.get(i).SSID).setRepresentable(true);
			}
		}
		Iterator it = wifiMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry)it.next();
			if(!SSIDList.contains(e.getKey())){
				Log.i("ITERATOR", "La red " + e.getKey() + " ya no está disponible.");
				wifiMap.get(e.getKey()).saveLevel(-100);
				wifiMap.get(e.getKey()).setRepresentable(false);
			}
		}

	}

	public void putZeros(){
		Iterator it = wifiMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry)it.next();
			wifiMap.get(e.getKey()).saveLevel(-100);
			wifiMap.get(e.getKey()).setRepresentable(false);
		}
	}

	public static Wifi getWifi(String SSID){
		return wifiMap.get(SSID);
	}

	public static void getRepresentableKey(){
		Iterator it = wifiMap.entrySet().iterator();
		while (it.hasNext()) {

			Map.Entry e = (Map.Entry)it.next();
			if(wifiMap.get(e.getKey()).isRepresentable()){
				Log.i("INFO","miro: "+ wifiMap.get(e.getKey()).getSSID());
				representableList.add((String) e.getKey());
			}
			representableArray = new String[representableList.size()-1];
			for( int i = 0; i < representableList.size()-1; i++)
				representableArray[i] = representableList.get(i);
		}
	}

	public static int[] getChannelAP(){
		int [] channelAP = new int[11];
		getRepresentableKey();

		for( int i = 0; i<representableArray.length; i++){
			int channel = wifiMap.get(representableArray[i]).getChannel();
			channelAP[channel-1]++; 			
		}

		return channelAP;
	}

	public static int getMaxLevel(){
		Iterator it = wifiMap.entrySet().iterator();
		int max = -120;
		while (it.hasNext()) {

			Map.Entry e = (Map.Entry)it.next();
			if(wifiMap.get(e.getKey()).getLastLevel() > max){
				max = wifiMap.get(e.getKey()).getLastLevel();
			}

		}
		return max;
	}


	public static int getMinLevel(){
		Iterator it = wifiMap.entrySet().iterator();
		int min = 100;
		while (it.hasNext()) {

			Map.Entry e = (Map.Entry)it.next();
			if(wifiMap.get(e.getKey()).getLastLevel() < min){
				min = wifiMap.get(e.getKey()).getLastLevel();
			}

		}
		return min;
	}

}
