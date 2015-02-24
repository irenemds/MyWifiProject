package com.mdes.mywifi;

import org.achartengine.model.TimeSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.util.Log;

import com.mdes.mywifi.chart.FrequencyGraphActivity;

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
	private boolean shown;
	private static int aux = 0;
	private double xValue = -120;

	/**
	 * Esta función es la encargada de crear las líneas y agregarlas a la gráfica.
	 * @param wifi la red Wifi de la que se está creando la línea.
	 */
	public BandWidthLine(Wifi wifi){		
		dataset = new TimeSeries(wifi.getSSID());
Log.i("TREN","WIFI "+ wifi.getSSID());
		aux++;
		if(aux >= colors.length){
			aux = aux-colors.length;
		}
		//Los valores de y pueden ir de 2402 a 2472 MHz
		for (double yValue = 2402; yValue < 2472; yValue++){
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
		}
		renderer.setLineWidth(4);
		renderer.setDisplayChartValuesDistance(1);
		renderer.setColor(colors[aux]);
		
		shown = true;
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

	public void updateBwLine( Wifi wifi){  

		dataset.clear();
		//Los valores de y pueden ir de 2402 a 2472 MHz
		for (double yValue = 2402; yValue < 2472; yValue++){
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
			if (shown == false){
				FrequencyGraphActivity.mRenderer.addSeriesRenderer(renderer);
				FrequencyGraphActivity.mDataset.addSeries(dataset);
				
Log.i("TREN","WIFI "+ wifi.getSSID());
			}
			shown = true;
		}

	}
	
	public void deleteLine(){
		dataset.clear();
		FrequencyGraphActivity.mRenderer.removeSeriesRenderer(renderer);
		FrequencyGraphActivity.mDataset.removeSeries(dataset);
		shown = false;
	}
}
