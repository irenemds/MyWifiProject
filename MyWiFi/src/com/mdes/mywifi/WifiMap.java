package com.mdes.mywifi;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.util.Log;

import com.mdes.mywifi.chart.MultipleGraph;

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
	private static HashMap<String, Integer> SSIDList;


	/**
	 * Este método se encarga de guardar los últimos valores obtenidos en el escaneo
	 * en los atributos de todos los objetos Wifi.
	 * @param resultWifiList List<ScanResult> Últimos resultados obtenidos en el escaneo de redes
	 */
	@SuppressWarnings("rawtypes")
	public static void putValue(List<ScanResult> resultWifiList){

		BSSIDList = new ArrayList<String>();
		SSIDList = new HashMap<String, Integer>();

		for (int i = 0; i < resultWifiList.size(); i++) {
			BSSIDList.add(resultWifiList.get(i).BSSID);
			//Comprobar si la red ya existe en el HashMap
			//Si no existe
			if (!wifiMap.containsKey(resultWifiList.get(i).BSSID)){
				//Crear nuevo objeto de la clase Wifi con ella
				Wifi wifi = new Wifi(resultWifiList.get(i));
				wifiMap.put(resultWifiList.get(i).BSSID, wifi);
				wifiMap.get(resultWifiList.get(i).BSSID).setColor(calculateColor());
				
				aux++;					
				if ( WifiThread.isGraph)
				{
					MultipleGraph.addSingleLine(wifiMap.get(resultWifiList.get(i).BSSID));
				}

				if (WifiThread.isFreqGraph)
				{
					MultipleGraph.addFreqLine(wifiMap.get(resultWifiList.get(i).BSSID));
				}
			}
			//Si existe guardar el último valor de potencia obtenido.
			else
			{					
				wifiMap.get(resultWifiList.get(i).BSSID).updateAP(resultWifiList.get(i));
				if (!wifiMap.get(resultWifiList.get(i).BSSID).isRepresentable() 
						&& WifiThread.isGraph
						&& !wifiMap.get(resultWifiList.get(i).BSSID).getLine().isShown())
				{
					MultipleGraph.addSingleLine(wifiMap.get(resultWifiList.get(i).BSSID));
				}

//				if (!wifiMap.get(resultWifiList.get(i).BSSID).isRepresentable() 
//						&& WifiThread.isFreqGraph
//						&& !wifiMap.get(resultWifiList.get(i).BSSID).getBwLine().isShown())
//				{
//					Log.i("TREN","Añado frecuencia: " + resultWifiList.get(i).SSID);
//					MultipleGraph.addFreqLine(wifiMap.get(resultWifiList.get(i).BSSID));
//				}

				wifiMap.get(resultWifiList.get(i).BSSID).setRepresentable(true);
			}
			//ANTENAS
			if (!SSIDList.containsKey(resultWifiList.get(i).SSID)){
				SSIDList.put(resultWifiList.get(i).SSID,1);
			}
			else{
				findBSSID(resultWifiList.get(i).SSID);
			}
			wifiMap.get(resultWifiList.get(i).BSSID).setRepresentable(true);
		}
		Log.i("BSSID","BSSIDList tiene"+ BSSIDList.size());		
		//Las redes que no han sido encontradas en el último escaneo
		//se guardan con valor -120 (suficientemente bajo)
		Iterator it = wifiMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry)it.next();
			if(!BSSIDList.contains(e.getKey())){
				wifiMap.get(e.getKey()).setRepresentable(false);
			}
		}
	}


	public static void reset(){
		wifiMap = new HashMap<String,Wifi>();
	}

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
				representableList.add(wifiMap.get(e.getKey()).getBSSID());
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
		int[] colors = new int[] { Color.parseColor("#009C8F"), Color.parseColor("#74C044"), 
				Color.parseColor("#EEC32E"), Color.parseColor("#84C441"),
				Color.parseColor("#41C4BF"), Color.parseColor("#4166C4"),
				Color.parseColor("#B04E9D"), Color.parseColor("#FF2F2F"),
				Color.parseColor("#33FF99"), Color.parseColor("#DCE45F"),
				Color.parseColor("#FFD06B")};
		
		if(aux > colors.length-1){
			aux = aux-colors.length*aux/colors.length;
		}	
		
		return colors[aux];
	}

	public static void resetAntennas(){
		Iterator it = wifiMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry)it.next();
			wifiMap.get(e.getKey()).setAntennas(1);
		}
	}

	public static void resetLines(){
		Iterator it = wifiMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry)it.next();
			Line line = wifiMap.get(e.getKey()).getLine();
			line.setShown(false);
			BandWidthLine bwLine = wifiMap.get(e.getKey()).getBwLine();
			bwLine.setShown(false);
		}
	}

	private static void findBSSID (String SSID){
		ArrayList<String> list = new ArrayList<String>();
		Iterator it = wifiMap.entrySet().iterator();
		while (it.hasNext()) {
			Map.Entry e = (Map.Entry)it.next();
			if (wifiMap.get(e.getKey()).getSSID().equals(SSID)){
				list.add(wifiMap.get(e.getKey()).getBSSID());
			}
		}
		for(int i = 0; i<list.size(); i++){
			wifiMap.get(list.get(i)).setAntennas(list.size());			
		}

	}

}