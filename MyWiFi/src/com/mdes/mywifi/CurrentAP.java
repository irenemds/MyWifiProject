package com.mdes.mywifi;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.nio.ByteOrder;

import android.net.wifi.WifiInfo;
import android.text.format.Formatter;
import android.util.Log;
import android.util.SparseIntArray;

public class CurrentAP {

	private String SSID = " ";
	private String MAC = " ";
	private String BSSID = " ";
	private int lastLinkSpeed = 0;
	private SparseIntArray linkSpeedList = new SparseIntArray();
	private Line line;
	private int IP;
	private String IPString;

	public void updateAP(WifiInfo wifiInfo){
		if(HiloWifi.currentAP != null || SSID.equals(" ")){
			if (!SSID.equals(wifiInfo.getSSID())){
				SSID = wifiInfo.getSSID();
				MAC = wifiInfo.getMacAddress();
				BSSID = wifiInfo.getBSSID();
				IP = wifiInfo.getIpAddress();
				formatIP();
				line = new Line();
				createList(lastLinkSpeed);
			}
			lastLinkSpeed = wifiInfo.getLinkSpeed();
			saveSpeedValue(lastLinkSpeed);
		}
		else{
			Log.e("INFO","NO ESTAMOS CONECTADOS A NADA");
			SSID = " ";
			MAC = " ";
			BSSID = " ";
			lastLinkSpeed = 0;
			linkSpeedList = new SparseIntArray();
			line = new Line();
			IP = 0;
		}
	}


	public String getIPString() {
		return IPString;
	}


	public void createList (int speed){
		linkSpeedList = new SparseIntArray();

		if (Wifi.contador != 0){
			for (int i = 0; i < Wifi.contador; i++) {
				linkSpeedList.append(i, 0);
			}
		}
	}

	public void saveSpeedValue(int level){
		linkSpeedList.append(Wifi.contador, level);
		//Cada vez que se guarda un nuevo nivel se crea punto y se añade a la liea de la red
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


	public void setIP(int iP) {
		IP = IP;
	}
	
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
