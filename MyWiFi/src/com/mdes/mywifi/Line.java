package com.mdes.mywifi;

import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.model.TimeSeries;
import org.achartengine.renderer.XYSeriesRenderer;

import android.graphics.Color;
import android.util.Log;

import com.mdes.mywifi.chart.LinkSpeedGraphActivity;
import com.mdes.mywifi.chart.MultipleGraph;

public class Line {

	public static int lineNumber;
	private TimeSeries dataset; 
	private XYSeriesRenderer renderer = new XYSeriesRenderer();
	private int[] colors = {Color.MAGENTA, Color.WHITE, Color.BLUE, Color.CYAN, Color.RED, Color.YELLOW};

	public Line(Wifi wifi)
	{
		dataset = new TimeSeries(wifi.getSSID()); 
		MultipleGraph.mDataset.addSeries(dataset);
		lineNumber++;
		setColor();
		renderer.setFillPoints(true);
		renderer.setLineWidth(4);
		renderer.setPointStyle(PointStyle.CIRCLE);
		renderer.setFillPoints(true);
		renderer.setDisplayChartValues(true);
		renderer.setChartValuesTextSize(15);
		renderer.setDisplayChartValuesDistance(10);

		// Add single renderer to multiple renderer
		MultipleGraph.mRenderer.addSeriesRenderer(renderer);	
	}

	public Line()
	{
		dataset = new TimeSeries("Link Speed"); 
		LinkSpeedGraphActivity.mDataset.addSeries(dataset);
		renderer.setColor(Color.LTGRAY);
		renderer.setFillPoints(true);
		renderer.setLineWidth(4);
		renderer.setPointStyle(PointStyle.CIRCLE);
		renderer.setFillPoints(true);
		renderer.setDisplayChartValues(true);
		renderer.setChartValuesTextSize(15);
		renderer.setDisplayChartValuesDistance(10);
		LinkSpeedGraphActivity.mRenderer.addSeriesRenderer(renderer);	
	}
	
	


	public void addNewPoints(Point p)
	{
		dataset.add(p.getX(), p.getY());
	}

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
}