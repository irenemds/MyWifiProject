package com.mdes.mywifi.chart;

import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.util.Log;

import com.mdes.mywifi.wifi.Wifi;

/**
 * Esta clase se emplea para crear las líneas que se representarán en
 * la gráfica de frecuencia.
 * Crean un pulso de 20 MHz de ancho, lo que equivale a ocupar dos canales.
 *
 */
public class BandWidthLine {

	private TimeSeries dataset; 
	private XYSeriesRenderer renderer = new XYSeriesRenderer();
	int[] colors = new int[] { Color.parseColor("#009C8F"), Color.parseColor("#74C044"), 
			Color.parseColor("#EEC32E"), Color.parseColor("#84C441"),
			Color.parseColor("#41C4BF"), Color.parseColor("#4166C4"),
			Color.parseColor("#B04E9D"), Color.parseColor("#FF2F2F"),
			Color.parseColor("#33FF99"), Color.parseColor("#DCE45F"),
			Color.parseColor("#FFD06B"), Color.BLUE, Color.WHITE,
			Color.GRAY, Color.GREEN, Color.RED, Color.YELLOW};
	/**
	 * Variable booleana que indica si la línea se está mostrando.
	 */
	private boolean shown;
	/**
	 * Esta variable será un contador para asignar el color a la línea a representar.
	 */
	private static int aux = 0;
	private double xValue = -120;

	/**
	 * Esta función es la encargada de crear las líneas y agregarlas a la gráfica.
	 * @param wifi Objeto de la clase Wifi que se corresponde con la red Wifi de la que se está creando la línea.
	 */
	public BandWidthLine(Wifi wifi){		
		//Nuevo elemento TimeSeries que contendrá los futuros valores de linea.
		dataset = new TimeSeries(wifi.getSSID());
		aux++;
		//Si la variable auxiliar es mayor o igual que la longitud del array de colores,
		//Se le resta este valor, para que vuelva a ser válido.
		if(aux >= colors.length){
			aux = aux-colors.length;
		}
		
		shown = true;
		
		updateBwLine(wifi);
		
		renderer.setLineWidth(4);
		renderer.setDisplayChartValuesDistance(1);
		renderer.setColor(colors[aux]);
		
		FrequencyGraphActivity.mDataset.addSeries(getDataset());
		FrequencyGraphActivity.mRenderer.addSeriesRenderer(getRenderer());	
	}

	public TimeSeries getDataset() {
		return dataset;
	}

	public void setDataset(TimeSeries dataset) {
		this.dataset = dataset;
	}

	public XYSeriesRenderer getRenderer() {
		return renderer;
	}

	public void setRenderer(XYSeriesRenderer renderer) {
		this.renderer = renderer;
	}

	public boolean isShown() {
		return shown;
	}

	public void setShown(boolean shown) {
		this.shown = shown;
	}

	/**
	 * Esta función se encarga de actualizar los valores de las líneas de ancho de banda.
	 * Se realiza tras cada escaneo.
	 * @param wifi Objeto de la clase Wifi que indica la red que se quiere actualizar.
	 */
	public void updateBwLine( Wifi wifi){  

		dataset.clear();
		//Los valores de y pueden ir de 2402 a 2472 MHz
		for (double yValue = 2402; yValue < 2472; yValue++){
			//Comprueba la frecuencia de la red Wifi y da valor a la línea, teniendo en cuenta los 20 MHz de WB
			if (yValue >= (wifi.getFreq()-10) && yValue <= (wifi.getFreq() + 10)){
				xValue = wifi.getLastLevel();			
			}
			//Fuera de ese rango usamos un valor muy bajo
			else{
				xValue = -120;
			}
			//En cualquier caso añadimos el valor de y, transformado para que marque
			//el canal y no la frecuencia.
			dataset.add((yValue-2407)/5, xValue);
			XYSeries[] xySeries = FrequencyGraphActivity.mDataset.getSeries();
			//Comprueba si la red ya se estaba mostrando
			for (int i = 0 ; i < xySeries.length; i++){
				if (xySeries[i].equals(dataset)){
					//Marca como mostrada la linea.
					shown = true;
				}
			}
			if (shown == false){
				FrequencyGraphActivity.mRenderer.addSeriesRenderer(renderer);
				FrequencyGraphActivity.mDataset.addSeries(dataset);
			}
			shown = true;
		}
	}
	
	/*
	 * Borra la línea. Elimina sus valores y la quita de la gráfica.
	 */
	public void deleteLine(){
		dataset.clear();
		FrequencyGraphActivity.mRenderer.removeSeriesRenderer(renderer);
		FrequencyGraphActivity.mDataset.removeSeries(dataset);
		shown = false;
	}
}
