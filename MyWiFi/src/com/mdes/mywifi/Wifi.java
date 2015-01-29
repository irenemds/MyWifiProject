package com.mdes.mywifi;

import android.net.wifi.ScanResult;
import android.util.Log;
import android.util.SparseIntArray;

/**
 * 
 * @author Usuario1
 *
 */
public class Wifi {
	public static int contador;
	private String SSID;
	private String BSSID;
	private int freq;
	private String cap;
	private SparseIntArray levels;
	private int channel;
	private boolean representable;
	private int lastLevel;
	private Line line;
	private BandWidthLine bwLine;
	private int color;
	private int antennas;
	private boolean security;

	public Wifi(ScanResult scanResult){

		SSID = scanResult.SSID;
		BSSID = scanResult.BSSID;
		freq = scanResult.frequency;
		cap = scanResult.capabilities;
		Log.i("SEG", "La red "+ SSID + ": "+ cap);
		channel = (freq-2407)/5;
		line = new Line(this);
		levels = new SparseIntArray();
		createList(scanResult.level);
		representable = true;
		bwLine = new BandWidthLine(this);
		antennas = 1;
		if (cap.contains("WPA")||cap.contains("WEP")){
			security = true;
		}
		else{
			Log.i("SEG", "La red "+ SSID + " no tiene seguridad ");
		}
	}

	public String getSSID() {
		return SSID;
	}

	public boolean isSecurity() {
		return security;
	}

	public String getBSSID() {
		return BSSID;
	}

	public int getFreq() {
		return freq;
	}

	public boolean isRepresentable() {
		return representable;
	}

	public String getCap() {
		return cap;
	}

	public SparseIntArray getLevels() {
		return levels;
	}

	public int getLastLevel() {
		return levels.get(levels.size()-1);
	}

	public int getChannel(){
		return channel;
	}

	public void createList (int level){
		if (contador != 0){
			for (int i = 0; i < contador; i++) {
				levels.append(i, -120);
			}
		}
		saveLevel(level);
	}
	
	public void updateAP (ScanResult scanResult){
		antennas++;
		freq = scanResult.frequency;
		cap = scanResult.capabilities;
		saveLevel(scanResult.level);
		channel = (freq-2407)/5;
		
	}

	private void saveLevel(int level){
		//Gestión de repetidores, guardarlo una única vez, sólo el mayor valor de potencia
		if(levels.get(contador) == 0 || levels.get(contador) < level ){	
			levels.append(contador, level);
		}
		//Cada vez que se guarda un nuevo nivel se crea punto y se añade a la linea de la red
		Point p = new Point(contador, levels.get(contador));
		line.addNewPoints(p);
		if (representable){
			bwLine = new BandWidthLine(this);
		}
		
	}

	public int getAntennas() {
		return antennas;
	}

	public void setAntennas(int antennas) {
		this.antennas = antennas;
	}

	public void setRepresentable (boolean x){
		representable = x;
		if (!x){
		saveLevel(-120);
		antennas = 0;
		}
	}

	public String getAPCSV(){
		String csv =Integer.toString(levels.get(0));
		for(int i = 1 ; i < levels.size() ; i++){
			csv = csv +" , "+ Integer.toString(levels.get(i));
		}
		return csv;
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}

	public int getMaxLevel(){
		int max = -120;
		for(int i = 0 ; i < levels.size() ; i++){
			if (levels.get(i)>max){
				max = levels.get(i);
			}
		}
		Log.i("INFO", "Máximo: " + max);
		return max;
	}
	
	public int getMinLevel(){
		int min = 0;
		for(int i = 0 ; i < levels.size() ; i++){
			if (levels.get(i)<min && levels.get(i) != -120){
				min = levels.get(i);
			}
		}
		Log.i("INFO", "Mínimo: " + min);
		return min;
	}

}
