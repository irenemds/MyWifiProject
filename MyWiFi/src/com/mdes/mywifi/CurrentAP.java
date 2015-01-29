package com.mdes.mywifi;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import android.net.wifi.WifiInfo;
import android.text.format.Formatter;
import android.util.Log;
import android.util.SparseIntArray;
/**
 * Esta clase almacena la informaci�n de la red a la que est� conectado el 
 * dispositivo en cada escaneo.
 *
 */
public class CurrentAP {

	private String SSID = " ";
	private String MAC = " ";
	private String BSSID = " ";
	private int lastLinkSpeed = 0;
	private SparseIntArray linkSpeedList = new SparseIntArray();
	private Line line;
	private int IP;
	private String IPString;

	/**
	 * Esta funci�n es la encargada de actualizar la informaci�n 
	 * del punto de acceso
	 * @param WifiInfo Informaci�n de la red wifi
	 */
	public void updateAP(WifiInfo wifiInfo){
		//Comprueba si el dispositivo est� conectado a alguna red
		if(HiloWifi.currentAP != null){
			//Comprueba si est� conectado al mismo punto de acceso que 
			//el que ya ten�a almacenado, si es distinto actualiza
			if (!SSID.equals(wifiInfo.getSSID())){
				SSID = wifiInfo.getSSID();
				MAC = wifiInfo.getMacAddress();
				BSSID = wifiInfo.getBSSID();
				IP = wifiInfo.getIpAddress();
				formatIP();
				//Se resetea la l�nea de valores de velocidad de enlace
				line = new Line();
				createList(lastLinkSpeed);
			}
			//Vuelve a poner la IP por si acaso qued� en null
			else{
				IP = wifiInfo.getIpAddress();
				formatIP();
			}
			lastLinkSpeed = wifiInfo.getLinkSpeed();
			saveSpeedValue(lastLinkSpeed);
		}
		//Si no hay ningun punto de acceso conectado vacia todo
		else{
			SSID = " ";
			MAC = " ";
			BSSID = " ";
			lastLinkSpeed = 0;
			linkSpeedList = new SparseIntArray();
			line = new Line();
			IP = 0;
		}
	}

	/**
	 * Devuelve la IP en formato String, legible para el usuario 
	 * 
	 */
	public String getIPString() {
		return IPString;
	}

	/**
	 * Esta funci�n crea la lista de valores de velocidad de enlace, 
	 * rellenando os valores de escaneos anteriores a 0 ya que
	 * no estaba conectado al punto de acceso
	 * @param speed velocidad detectada en el escaneo en el que se crea la lista
	 */
	public void createList (int speed){
		linkSpeedList = new SparseIntArray();

		if (Wifi.contador != 0){
			for (int i = 0; i < Wifi.contador; i++) {
				linkSpeedList.append(i, 0);
			}
		}
	}
	
	/**
	 * Esta funci�n almacena en la lista de valores de velocidad de enlace
	 * el valor detectado en el �ltimo escaneo 
	 * @param level velocidad detectada en el �ltimo escaneo
	 */
	public void saveSpeedValue(int level){
		linkSpeedList.append(Wifi.contador, level);
		//Cada vez que se guarda un nuevo nivel se crea punto y se a�ade a la liea de la red
		Point p = new Point(Wifi.contador, level);
		line.addNewPoints(p);
	}


	public String getSSID() {
		return SSID;
	}


	public void setSSID(String sSID) {
		SSID = sSID;
	}


	public String getMAC() {
		return MAC;
	}


	public void setMAC(String mAC) {
		MAC = mAC;
	}


	public String getBSSID() {
		return BSSID;
	}


	public void setBSSID(String bSSID) {
		BSSID = bSSID;
	}


	public int getLastLinkSpeed() {
		return lastLinkSpeed;
	}


	public void setLastLinkSpeed(int lastLinkSpeed) {
		this.lastLinkSpeed = lastLinkSpeed;
	}


	public SparseIntArray getLinkSpeedList() {
		return linkSpeedList;
	}


	public void setLinkSpeedList(SparseIntArray linkSpeedList) {
		this.linkSpeedList = linkSpeedList;
	}


	public int getIP() {
		return IP;
	}


	public void setIP(int IP) {
		this.IP = IP;
	}
	/**
	 * Esta funci�n formatea el valor de la IP obtenido mediante 
	 * WifiInfo a un String legible por el usuario
	 */
	private void formatIP(){
		// Convertir de little-endian en big-endian si hiciera falta
	    if (ByteOrder.nativeOrder().equals(ByteOrder.LITTLE_ENDIAN)) {
	        IP = Integer.reverseBytes(IP);
	    }
	    //Transformar a bytes
	    byte[] ipByteArray = BigInteger.valueOf(IP).toByteArray();
	    //Pasar a Caracteres con formato IP
	    try {
	        IPString = InetAddress.getByAddress(ipByteArray).getHostAddress();
	    } catch (UnknownHostException e) {
	        e.printStackTrace();;
	        IPString = null;
	    }
	}
}
