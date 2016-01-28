	package com.mdes.mywifi.wifi;

import com.mdes.mywifi.chart.BandWidthLine;
import com.mdes.mywifi.generic.Line;
import com.mdes.mywifi.generic.Point;

import android.net.wifi.ScanResult;
import android.util.Log;
import android.util.SparseIntArray;

/**
 * Almacena los datos característicos de una determinada red Wifi.
 * @author Usuario1
 *
 */
public class Wifi {
	/**
	 * Contador estático del número de redes captadas durante toda la ejecución.
	 */
	public static int contador;
	private String SSID;
	private String BSSID;
	private int freq;
	private String cap;
	private int savedValue = 0;
	/**
	 * Lista de valores de potencia de la red captados.
	 */
	private SparseIntArray levels;
	private int channel;
	/**
	 * Indica si se está mostrando la red
	 */
	private boolean representable;
	public int getSavedValue() {
		return savedValue;
	}

	public void setSavedValue(int savedValue) {
		this.savedValue = savedValue;
	}

	private int lastLevel;
	/**
	 * Línea que contiene los valores de potencia de la red
	 */
	private Line line;
	/**
	 * Línea que contiene la representación "espectral" de la red
	 */
	private BandWidthLine bwLine;
	/**
	 * Número de AP transmitiendo la misma red.
	 */
	private int antennas;
	/**
	 * Indicador de seguridad
	 */
	private boolean security;

	public Wifi(ScanResult scanResult){

		SSID = scanResult.SSID;
		BSSID = scanResult.BSSID;
		freq = scanResult.frequency;
		cap = scanResult.capabilities;
		//Calculo el canal
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
	/**
	 * Crea la lísta de valores de potencia recibidos
	 * @param level Nivel de potencia captado
	 */
	public void createList (int level){
		//Si el contador de escaneos es distinto de 0
		if (contador != 0){
			//Se rellenan los valores de potencia previos a un valor bajo.
			for (int i = 0; i < contador; i++) {
				levels.append(i, -120);
			}
		}
		saveLevel(level);
	}
	
	/**
	 * Actualiza la información de la red Wifi
	 * @param scanResult Nuevos datos obtenidos de la red
	 */
	public void updateAP (ScanResult scanResult){
		freq = scanResult.frequency;
		cap = scanResult.capabilities;
		saveLevel(scanResult.level);
		channel = (freq-2407)/5;
		
	}
	
	/**
	 * Guarda el nivel de potencia recibido en el último escaneo
	 * @param level
	 */
	private void saveLevel(int level){
		
		levels.append(contador, level);
		//Cada vez que se guarda un nuevo nivel se crea punto y se añade a la linea de potencia la red
		Point p = new Point(contador, levels.get(contador));
		line.addNewPoints(p);
		//Se actualiza la línea "espectral"
		if (representable){
			bwLine.updateBwLine(this);
		}
		
	}

	public BandWidthLine getBwLine() {
		return bwLine;
	}

	public void setBwLine(BandWidthLine bwLine) {
		this.bwLine = bwLine;
	}

	public int getAntennas() {
		return antennas;
	}

	public void setAntennas(int antennas) {
		this.antennas = antennas;
	}
	
	public void setRepresentable (boolean x){
		representable = x;
		//Si el nuevo estado de la línea es no representable
		if (!x){
			//Se le añade valor -120, se sigue mostrando en gráficas de potencia pero con valor muy bajo
			saveLevel(-120);
			antennas = 0;
			//Se deja de mostrar en la ráfica "espectral"
			bwLine.deleteLine();
		}
	}

	/**
	 * Crea cadena de valores de potencia recibidos para exportar.
	 * @return String de los valores de potencia recibidos
	 */
	public String getAPCSV(){
		String csv =Integer.toString(levels.get(0));
		for(int i = 1 ; i < levels.size() ; i++){
			csv = csv +" , "+ Integer.toString(levels.get(i));
		}
		return csv;
	}

	/**
	 * Recorre los valores de potencia obtenidos y consigue el mayor
	 * @return Máximo valor de potencia
	 */
	public int getMaxLevel(){
		int max = -120;
		for(int i = 0 ; i < levels.size() ; i++){
			if (levels.get(i)>max){
				max = levels.get(i);
			}
		};
		return max;
	}

	/**
	 * Recorre los valores de potencia obtenidos y consigue el menor
	 * @return Mínimo valor de potencia
	 */
	public int getMinLevel(){
		int min = 0;
		for(int i = 0 ; i < levels.size() ; i++){
			if (levels.get(i)<min && levels.get(i) != -120){
				min = levels.get(i);
			}
		}
		return min;
	}
	
	public Line getLine(){
		return line;
	}

}
