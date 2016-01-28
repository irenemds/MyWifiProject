package com.mdes.mywifi.chart;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;

import com.mdes.mywifi.R;
import com.mdes.mywifi.broadcastreceiver.WifiChangeReceiver;
import com.mdes.mywifi.broadcastreceiver.WifiNotFoundReceiver;
import com.mdes.mywifi.help.HelpDialog;
import com.mdes.mywifi.log.LogManager;
import com.mdes.mywifi.thread.WifiThread;
import com.mdes.mywifi.wifi.WifiMap;
/**
 * Esta clase se emplea para crear las gráficas que representarán la potencia recibida en función
 * del canal de transmisión.
 *
 */
public class FrequencyGraphActivity extends Activity {

	public static GraphicalView view;
	public static XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	public static XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private WifiChangeReceiver wifiReceiver = new WifiChangeReceiver();
	private GraphicalView mChartView;
	private Context c;
	private BroadcastReceiver currentActivityReceiver;
	private WifiNotFoundReceiver wifiNotFoundReceiver = new WifiNotFoundReceiver();
	private boolean pausa = false;
	ActionBar actionBar = null;
	private HelpDialog helpDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		registerReceivers();
		rendererSetUp();

		super.onCreate(savedInstanceState);

		//Quitar título de la actividad y pantalla completa
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		view = ChartFactory.getLineChartView(this, FrequencyGraphActivity.mDataset, FrequencyGraphActivity.mRenderer);		
		setContentView(view);

		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);

		currentActivityReceiver = new BroadcastReceiver(){
			//Receiver para actualizar la información
			@Override
			public void onReceive(Context context, Intent intent) {
				if(!pausa){
					rendererSetUp();
					if(WifiMap.getMaxLevel() > -50){ 
						//Mayor que el límite inicial
						mRenderer.setYAxisMax((WifiMap.getMaxLevel()/10)*10+10);
					}
					view.repaint();
				}
			}

		};
	}

	protected void onStart() {
		super.onStart();
		WifiThread.isFreqGraph = true;
		c = this;
		rendererSetUp();
		view = ChartFactory.getLineChartView(this, mDataset, mRenderer);	
		setContentView(view);
		registerReceivers();
	}

	@Override
	protected void onPause() {
		WifiThread.isFreqGraph = false;
		super.onPause();
		unregisterReceivers();
	}

	@Override
	protected void onResume() {
		WifiThread.isFreqGraph = true;
		super.onResume();
		registerReceivers();
	}


	private void rendererSetUp(){
		mRenderer.setClickEnabled(false);
		mRenderer.setShowGrid(true);
		mRenderer.setApplyBackgroundColor(true);
		mRenderer.setBackgroundColor(Color.BLACK);
		mRenderer.setLabelsTextSize(25);
		mRenderer.setLegendTextSize(25);
		mRenderer.setAxisTitleTextSize(25);
		mRenderer.setYTitle("Potencia [dBm]");
		mRenderer.setXTitle("Canal");
		mRenderer.setMargins(new int[] { 50, 40, 10, 30 });
		mRenderer.setXAxisMax(12);
		mRenderer.setXAxisMin(0);
		mRenderer.setYLabels(11);
		mRenderer.setXLabels(11);
		mRenderer.setYAxisMax(-50);
		mRenderer.setYAxisMin(-120);
		mRenderer.setZoomEnabled(false);
		mRenderer.setPanEnabled(false);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.help_menu, menu);
		return true;
	}


	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try{
			switch(item.getItemId()) {

			case R.id.ayuda:
				setResult(Activity.RESULT_CANCELED);
				String text = "Esta gráfica muestra en que canal está transmitiendo "
						+ "cada punto de acceso detectado en los escaneos realizados. "
						+ "A partir de esta es fácil analizar en cual de los canales existen"
						+ "menos interferencias de otras transmisiones Wifi y, por tanto,"
						+ "en cual de ellos se obtendrá el mejor rendimiento para nuestra red.";
				helpDialog = new HelpDialog(this,"Ayuda", text);
				return true;
			case R.id.pausa:
				pausa = !pausa;
				if (pausa){
					item.setTitle("Reanudar");
				}
				else{
					item.setTitle("Pausa");
				}
				return true;
			case R.id.salir:
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				return true;

			default:
				return super.onOptionsItemSelected(item);
			}
		}catch(Exception e){
			e.printStackTrace();
			LogManager lm = new LogManager(e);
			return super.onOptionsItemSelected(item);
		}
	}

	private void registerReceivers(){
		registerReceiver(wifiReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
		registerReceiver(currentActivityReceiver, new IntentFilter("com.mdes.mywifi.timer"));
		registerReceiver(wifiNotFoundReceiver, new IntentFilter("com.mdes.mywifi.wifinotfound"));
		registerReceiver(wifiNotFoundReceiver, new IntentFilter("com.mdes.mywifi.wififound"));
	}

	private void unregisterReceivers(){
		unregisterReceiver(wifiReceiver);
		unregisterReceiver(wifiNotFoundReceiver);
		unregisterReceiver(currentActivityReceiver);
	}



}