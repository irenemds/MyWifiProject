package com.mdes.mywifi;

import java.util.ArrayList;

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
	
	
public Wifi(ScanResult scanResult){
		
		SSID = scanResult.SSID;
		BSSID = scanResult.BSSID;
		freq = scanResult.frequency;
		cap = scanResult.capabilities;
		channel = (freq-2407)/5;
		line = new Line(SSID);
		levels = new SparseIntArray();
		createList(scanResult.level);
		representable = true;
		
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

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((SSID == null) ? 0 : SSID.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Wifi other = (Wifi) obj;
		if (SSID == null) {
			if (other.SSID != null)
				return false;
		} else if (!SSID.equals(other.SSID))
			return false;
		return true;
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
		//Cada vez que se guarda un nuevo nivel se crea punto y se añade a la liea de la red
		Point p = new Point(contador, level);
		line.addNewPoints(p);
	}
	
	public Line getLine() {
		return line;
	}
	
	public void setRepresentable (boolean x){
		representable = x;
	}

	public ArrayList<Integer> getXValues(){
		ArrayList<Integer> xValues = new ArrayList<Integer>();
		
		for (int i = 0; 0 < levels.size(); i++){
			xValues.add(i);
		}
		
		return xValues;
	}
	
	public ArrayList<Integer> getYValues(){
		ArrayList<Integer> yValues = new ArrayList<Integer>();
		
		for (int i = 0; 0 < levels.size(); i++){
			yValues.add(levels.get(i));
		}
		
		return yValues;
	}

	public Point getDataFromReceiver(int x)
	{
		return new Point(x, levels.get(levels.size()-1));
	}
}
