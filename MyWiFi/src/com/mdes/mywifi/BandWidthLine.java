package com.mdes.mywifi;

import org.achartengine.model.TimeSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;

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
	private int[] colors = {Color.MAGENTA, Color.WHITE, Color.BLUE, Color.CYAN, Color.RED, Color.YELLOW};

/**
 * Esta función es la encargada de crear las líneas y agregarlas a la gráfica.
 * @param wifi la red Wifi de la que se está creando la línea.
 */
	public BandWidthLine(Wifi wifi){

		dataset = new TimeSeries(wifi.getSSID()); 

		double xValue;
		double yValue;
		//Los valores de y pueden ir de 2402 a 2472 MHz
		for (yValue = 2402; yValue < 2472; yValue++){
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
		renderer.setColor(wifi.getColor());
		//Se añaden los datos a la gráfica de todas las líneas.
		FrequencyGraphActivity.mRenderer.addSeriesRenderer(renderer);	
		FrequencyGraphActivity.mDataset.addSeries(dataset);	

	}
}
