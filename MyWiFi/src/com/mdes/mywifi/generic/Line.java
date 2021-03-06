package com.mdes.mywifi.generic;

import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;

import com.mdes.mywifi.chart.LinkSpeedGraphActivity;
import com.mdes.mywifi.log.LogManager;
import com.mdes.mywifi.wifi.Wifi;

public class Line {

	public static int lineNumber;
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

	/**
	 * Constructor l�nea de potencia de una red.
	 * @param wifi Red Wifi cuya potencia representa.
	 */
	public Line(Wifi wifi)
	{
		dataset = new TimeSeries(wifi.getSSID());
		lineNumber++;
		setColor();
		shown = true;
		renderer.setFillPoints(true);
		renderer.setLineWidth(4);
		renderer.setPointStyle(PointStyle.CIRCLE);
		renderer.setFillPoints(true);
		renderer.setChartValuesTextSize(15);
		renderer.setDisplayChartValuesDistance(10);
	}
	

	public boolean isShown() {
		return shown;
	}


	public void setShown(boolean shown) {
		this.shown = shown;
	}

	/**
	 * Constructor l�nea de velocidad de enlace de la red conectada.
	 */
	public Line()
	{
		//Borra la l�nea anterior de la gr�fica
		LinkSpeedGraphActivity.mDataset.clear();
		LinkSpeedGraphActivity.mRenderer.removeAllRenderers();
		//Crea una nueva l�nea
		dataset = new TimeSeries("Link Speed");
		LinkSpeedGraphActivity.mDataset.addSeries(dataset);
		renderer.setColor(Color.parseColor("#009C8F"));
		renderer.setFillPoints(true);
		renderer.setLineWidth(4);
		renderer.setPointStyle(PointStyle.CIRCLE);
		renderer.setFillPoints(true);
		renderer.setDisplayChartValues(true);
		renderer.setChartValuesTextSize(15);
		renderer.setDisplayChartValuesDistance(10);
		LinkSpeedGraphActivity.mRenderer.addSeriesRenderer(renderer);	
	}
	
	/**
	 * A�ade puntos a la l�nea
	 * @param p Punto que se quiere a�adir.
	 */
	public void addNewPoints(Point p)
	{
		dataset.add(p.getX(), p.getY());
	}

	/**
	 * Define el color de la l�nea
	 */
	public void setColor(){
		try{
			int lineNumberAux = lineNumber;
			if(lineNumberAux > colors.length-1){
				int aux = lineNumberAux/colors.length;
				lineNumberAux = lineNumber-colors.length*aux;
			}
			renderer.setColor(colors[lineNumberAux]);
		}catch (Exception e){
			e.printStackTrace();
			LogManager lm = new LogManager(e);
		}
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
}