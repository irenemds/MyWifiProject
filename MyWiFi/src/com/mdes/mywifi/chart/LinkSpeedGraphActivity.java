package com.mdes.mywifi.chart;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

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
import android.view.Window;
import android.view.WindowManager;
import android.view.ViewGroup.LayoutParams;
import android.widget.LinearLayout;

import com.mdes.mywifi.R;
import com.mdes.mywifi.broadcastreceiver.WifiChangeReceiver;
import com.mdes.mywifi.broadcastreceiver.WifiNotFoundReceiver;
import com.mdes.mywifi.help.HelpDialog;
import com.mdes.mywifi.log.LogManager;
import com.mdes.mywifi.thread.WifiThread;
import com.mdes.mywifi.wifi.Wifi;
import com.mdes.mywifi.wifi.WifiMap;
/**
 * Esta clase se emplea para crear las gráficas que representarán la velocidad de enlace
 * a lo largo del tiempo.
 *
 */
public class LinkSpeedGraphActivity extends Activity{

	public static GraphicalView view;
	public static XYMultipleSeriesDataset mDataset = new XYMultipleSeriesDataset();
	public static XYMultipleSeriesRenderer mRenderer = new XYMultipleSeriesRenderer();
	private WifiChangeReceiver wifiReceiver = new WifiChangeReceiver();
	private GraphicalView mChartView;
	private BroadcastReceiver currentActivityReceiver;
	private WifiNotFoundReceiver wifiNotFoundReceiver = new WifiNotFoundReceiver();

	ActionBar actionBar = null;
	private HelpDialog helpDialog;

	private boolean pausa = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try{

			actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(false);
			
			registerReceivers();

			mRenderer.setClickEnabled(false);
			mRenderer.setShowGrid(true);
			mRenderer.setApplyBackgroundColor(true);
			mRenderer.setBackgroundColor(Color.BLACK);
			mRenderer.setAxisTitleTextSize(16);
			mRenderer.setLabelsTextSize(25);
			mRenderer.setAxisTitleTextSize(25);
			mRenderer.setLegendTextSize(20);
			mRenderer.setYTitle("Velocidad [Mbps]");
			mRenderer.setMargins(new int[] { 50, 40, 10, 30 });
			mRenderer.setPointSize(5);
			mRenderer.setZoomEnabled(false);
			mRenderer.setPanEnabled(false);

			super.onCreate(savedInstanceState);
			
			//Quitar título de la actividad y pantalla completa
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
			setContentView(R.layout.xy_chart);

			currentActivityReceiver = new BroadcastReceiver(){

				@Override
				public void onReceive(Context context, Intent intent) {
					Bundle extras = getIntent().getExtras();
					if(WifiThread.currentAP.getBSSID().equals(extras.getString("BSSID"))){
						LinkSpeedGraphActivity.mRenderer.setYAxisMax(100);
						LinkSpeedGraphActivity.mRenderer.setYAxisMin(0);
						if(Wifi.contador > 15){
							LinkSpeedGraphActivity.mRenderer.setXAxisMax(Wifi.contador);
							LinkSpeedGraphActivity.mRenderer.setXAxisMin(Wifi.contador-15);
						}
						if (!pausa){
							view.repaint();
						}
					}

					else{
						finish();
					}}
			};
		}catch (Exception e){
			e.printStackTrace();
			LogManager lm = new LogManager(e);
		}
	}

	protected void onStart() {
		super.onStart();
		view = ChartFactory.getLineChartView(this, LinkSpeedGraphActivity.mDataset, LinkSpeedGraphActivity.mRenderer);		
		setContentView(view);
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceivers();
	}
	@Override
	protected void onResume() {
		registerReceivers();
		try{
			super.onResume();
			if (mChartView == null) {
				LinearLayout layout = new LinearLayout(this);
				layout.setOrientation(LinearLayout.VERTICAL);
				mChartView = ChartFactory.getLineChartView(this, LinkSpeedGraphActivity.mDataset, LinkSpeedGraphActivity.mRenderer);
				LinkSpeedGraphActivity.mRenderer.setClickEnabled(true);
				LinkSpeedGraphActivity.mRenderer.setSelectableBuffer(10);
				layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT));
			} else {
				mChartView.repaint();
			}
		}catch (Exception e){
			e.printStackTrace();
			LogManager lm = new LogManager(e);
		}
	}

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
				String text = "Esta gráfica muestra una representación en tiempo real de los "
						+ " megabits por segundo recibidos a través del enlace establecido con el punto"
						+ " de acceso seleccionado. Esta velocidad varía a lo largo del tiempo para intentar"
						+ "mantener un enlace estable en todo momento. Puede depender de factores como la "
						+ "distancia entre el dispositivo y el punto de acceso, el número de dispositivos"
						+ "conectados a él.";
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