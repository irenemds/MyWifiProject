package com.mdes.mywifi;

import android.net.wifi.ScanResult;
import android.util.SparseIntArray;

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


	public Wifi(ScanResult scanResult){

		SSID = scanResult.SSID;
		BSSID = scanResult.BSSID;
		freq = scanResult.frequency;
		cap = scanResult.capabilities;
		channel = (freq-2407)/5;
		line = new Line(this);
		levels = new SparseIntArray();
		createList(scanResult.level);
		representable = true;
		bwLine = new BandWidthLine(this);
	}

	public String getSSID() {
		return SSID;
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
				levels.append(i, 0);
			}
		}
		saveLevel(level);
	}

	public void saveLevel(int level){
		levels.append(contador, level);
		//Cada vez que se guarda un nuevo nivel se crea punto y se añade a la linea de la red
		Point p = new Point(contador, level);
		line.addNewPoints(p);
		if (representable){
			bwLine = new BandWidthLine(this);
		}
	}

	public void setRepresentable (boolean x){
		representable = x;
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



}
