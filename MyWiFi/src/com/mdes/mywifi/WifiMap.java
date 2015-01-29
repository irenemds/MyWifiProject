package com.mdes.mywifi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.util.Log;

/**
 * Esta clase contiene y gestiona el HashMap que almacena todos los objetos
 * de clase de Wifi de cada una de las redes que el dispositivo ha encontrado
 * desde el primer escaneo.
 *
 */
public class WifiMap {

	/**
	 * El HashMap wifiMap contiene como claves las BSSID de las redes y como valor
	 * el objeto Wifi de cada una de ellas.
	 */
	public static HashMap<String,Wifi> wifiMap = new HashMap<String,Wifi>();
	//Lista de BSSID de las redes disponibles.
	private static List<String> BSSIDList = new ArrayList<String>();
	public static List<String> representableList = new ArrayList<String>();	
	public static String[] representableArray;
	private static int aux = 0;


	/**
	 * Este método se encarga de guardar los últimos valores obtenidos en el escaneo
	 * en los atributos de todos los objetos Wifi.
	 * @param resultWifiList List<ScanResult> Últimos resultados obtenidos en el escaneo de redes
	 */
	@SuppressWarnings("rawtypes")
	public static void putValue(List<ScanResult> resultWifiList){
//		try{
			BSSIDList = new ArrayList<String>();

			for (int i = 0; i < resultWifiList.size(); i++) {
				BSSIDList.add(resultWifiList.get(i).BSSID);
				
				//Comprobar si la red ya existe en el HashMap
				//Si no existe
				if (!wifiMap.containsKey(resultWifiList.get(i).BSSID)){
					//Crear nuevo objeto de la clase Wifi con ella
					Wifi wifi = new Wifi(resultWifiList.get(i));
					wifiMap.put(resultWifiList.get(i).BSSID, wifi);
					Log.i("BSSID","Se guarda nueva la red de BSSID: "+ resultWifiList.get(i).BSSID);
					wifiMap.get(resultWifiList.get(i).BSSID).setColor(calculateColor());
					aux++;
				}
				//Si existe guardar el último valor de potencia obtenido.
				else
				{				
					wifiMap.get(resultWifiList.get(i).BSSID).updateAP(resultWifiList.get(i));
//					wifiMap.get(resultWifiList.get(i).BSSID).setAntennas(1);
					wifiMap.get(resultWifiList.get(i).BSSID).setRepresentable(true);
				}
			}
			//Las redes que no han sido encontradas en el último escaneo
			//se guardan con valor -120 (suficientemente bajo)
			Iterator it = wifiMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry e = (Map.Entry)it.next();
				if(!BSSIDList.contains(e.getKey())){
					wifiMap.get(e.getKey()).setRepresentable(false);
				}
			}
//		}catch (Exception e){
//			e.printStackTrace();
//			LogManager lm = new LogManager(e);
//		}
	}
	
	
	public static void reset(){
		Log.i("BSSID","RESET");
		wifiMap = new HashMap<String,Wifi>();
	}
	
//
//	public static Wifi getWifi(String SSID){
//		return wifiMap.get(SSID);
//	}
	
	/**
	 * Esta función obtiene las BSSID de todas las redes "representables"
	 * del HashMap.
	 */
	@SuppressWarnings("rawtypes")
	public static void getRepresentableKey(){

			representableList = new ArrayList<String>();
			Iterator it = wifiMap.entrySet().iterator();
			while (it.hasNext()) {
				Map.Entry e = (Map.Entry)it.next();
				if(wifiMap.get(e.getKey()).isRepresentable()){
					representableList.add((String) e.getKey());
				}}
			representableArray = new String[representableList.size()];
			for( int i = 0; i < representableArray.length; i++){
				representableArray[i] = representableList.get(i);
			}
	}

	/**
	 * Esta función consigue los canales de todas las redes Wifi y devuelve,
	 * en un array, el total de redes conectadas en canal.
	 * @return int[] Array con el número total de redes que transmiten en cada canal
	 * en cada posición del vector.
	 */
	public static int[] getChannelAP(){
		int [] channelAP = new int[11];
		getRepresentableKey();			

		for( int i = 0; i<representableArray.length; i++){
			int channel = wifiMap.get(representableArray[i]).getChannel();
			if (channel <= 11){
				channelAP[channel-1]++;
			}
		}
		return channelAP;
	}

	/**
	 * Esta función obtiene los valores de potencia de una red en formato CSV
	 * @param BSSID String, nombre de la red de la que se quiere obtener el CSV
	 * @return String, valores de potencia de la red en formato CSV
	 */
	public static String getCSV(String BSSID){
		return wifiMap.get(BSSID).getAPCSV();
	}

	/**
	 * Esta función obtiene el máximo nivel de potencia de los obtenidos 
	 * en el último escaneo.
	 * @return int, máximo valor de potencia
	 */
	@SuppressWarnings("rawtypes")
	public static int getMaxLevel(){
		Iterator it = wifiMap.entrySet().iterator();
		int i = -3000;
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry)it.next();
			if (i < wifiMap.get(e.getKey()).getLastLevel()){
				i = wifiMap.get(e.getKey()).getLastLevel();
			}
		}
		return i;
	}
	
	/**
	 * Esta función obtiene el mínimo nivel de potencia de los obtenidos 
	 * en el último escaneo.
	 * @return int, mínimo valor de potencia
	 */
	@SuppressWarnings("rawtypes")
	public static int getMinLevel(){
		Iterator it = wifiMap.entrySet().iterator();
		int i = 3000;
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry)it.next();
			if (i > wifiMap.get(e.getKey()).getLastLevel()){
				i = wifiMap.get(e.getKey()).getLastLevel();
			}
		}
		return i;
	}

	/**
	 * Esta función calcula el color a asignar a una nueva red
	 * @return int, el color que se debe asignar a la siguiente red.
	 */
	public static int calculateColor(){
		int[] colors = {Color.MAGENTA, Color.WHITE, Color.BLUE, Color.CYAN, Color.RED, Color.YELLOW};
		if(aux > colors.length-1){
			aux = aux-colors.length*aux/colors.length;
		}	
//		aux++;
		return colors[aux];
	}
	
	private void resetAntennas(){
		Iterator it = wifiMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry)it.next();
			wifiMap.get(e.getKey()).setAntennas(0);
		}
	}
}