package com.mdes.mywifi;

import org.achartengine.model.TimeSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;

import com.mdes.mywifi.chart.FrequencyGraphActivity;

/**
 * Esta clase se emplea para crear las l�neas que se representar�n en
 * la gr�fica de frecuencia.
 * Crean un pulso de 20 MHz de ancho, lo que equivale a ocupar dos canales.
 *
 */
public class BandWidthLine {

	private TimeSeries dataset; 
	private XYSeriesRenderer renderer = new XYSeriesRenderer();
	private int[] colors = {Color.MAGENTA, Color.WHITE, Color.BLUE, Color.CYAN, Color.RED, Color.YELLOW};

/**
 * Esta funci�n es la encargada de crear las l�neas y agregarlas a la gr�fica.
 * @param wifi la red Wifi de la que se est� creando la l�nea.
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
			//En cualquier caso a�adimos el valor de y, transformado para que marque
			//el canal y no la frecuencia.
			dataset.add((yValue-2407)/5, xValue);
		}

		renderer.setLineWidth(4);
		renderer.setDisplayChartValuesDistance(1);
		renderer.setColor(wifi.getColor());
		//Se a�aden los datos a la gr�fica de todas las l�neas.
		FrequencyGraphActivity.mRenderer.addSeriesRenderer(renderer);	
		FrequencyGraphActivity.mDataset.addSeries(dataset);	

	}
}
