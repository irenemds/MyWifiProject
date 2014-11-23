package com.mdes.mywifi;

import java.sql.Timestamp;
import java.util.ArrayList;

import android.graphics.Color;
import android.net.wifi.ScanResult;
import android.util.SparseIntArray;

public class Wifi {
	private int aux;
//	public static int contador;
	private String SSID;
	private String BSSID;
	private int freq;
	private String cap;
	private SparseIntArray levels;
	private int channel;
	private boolean representable;
	private int lastLevel;
	private Line line;
	private Timestamp firstSeen;
	private Timestamp lastSeen;
	private int  color;
	private BandWidthLine bwl;
	
	
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
		java.util.Date date= new java.util.Date();
		firstSeen = new Timestamp(date.getTime());
		lastSeen = firstSeen;
		color = calculateColor();
		aux++;
		bwl = new BandWidthLine(this);
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
		if (HiloWifi.contador != 0){
			for (int i = 0; i < HiloWifi.contador; i++) {
				levels.append(i, 0);
			}
		}
		saveLevel(level);
	}
	
	public void saveLevel(int level){
		levels.append(HiloWifi.contador, level);
		//Modificar lastSeen
		if( level != -120){
			java.util.Date date= new java.util.Date();
			lastSeen = new Timestamp(date.getTime());
			//Modificar firstSeen si el valor anterior era -120 (no encontrado)
			if(levels.get(HiloWifi.contador-1)==-120){
				firstSeen = lastSeen;
			}
			bwl = new BandWidthLine(this);
		}
		//Cada vez que se guarda un nuevo nivel se crea punto y se añade a la liea de la red
		Point p = new Point(HiloWifi.contador, level);
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
	
	public int calculateColor(){
		int[] colors = {Color.BLUE, Color.GREEN, Color.MAGENTA, Color.YELLOW, Color.WHITE, Color.RED};
		if(aux > colors.length-1){
			aux = aux-colors.length;
		}
		return colors[aux];
	}

	public int getColor() {
		return color;
	}

	public void setColor(int color) {
		this.color = color;
	}
	
}
