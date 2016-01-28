package com.mdes.mywifi.chart;
/**
 * Esta clase se emplea para crear las graficas lineales que representarán
 * los valores de potencia a lo largo del tiempo para todos los punto de acceso detectados.
 *
 */
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;
import android.view.Menu;
import android.view.MenuItem;
import android.view.WindowManager;
import android.widget.LinearLayout;

import com.mdes.mywifi.R;
import com.mdes.mywifi.broadcastreceiver.WifiChangeReceiver;
import com.mdes.mywifi.broadcastreceiver.WifiNotFoundReceiver;
import com.mdes.mywifi.help.HelpDialog;
import com.mdes.mywifi.log.LogManager;
import com.mdes.mywifi.thread.WifiThread;
import com.mdes.mywifi.wifi.Wifi;
import com.mdes.mywifi.wifi.WifiMap;

public class DynamicGraphActivity extends Activity {

	private static GraphicalView view;
	private WifiChangeReceiver wifiReceiver = new WifiChangeReceiver();
	private GraphicalView mChartView;
	private BroadcastReceiver graphReceiver;
	private WifiNotFoundReceiver wifiNotFoundReceiver = new WifiNotFoundReceiver();
	private boolean pausa = false;
	ActionBar actionBar = null;
	private HelpDialog helpDialog;
	private Menu menu;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try{
			super.onCreate(savedInstanceState);
			
			//Quitar título de la actividad y pantalla completa
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
			setContentView(R.layout.xy_chart);
			
			actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(false);
			
			graphReceiver = new BroadcastReceiver(){
				@Override
				public void onReceive(Context context, Intent intent) {
					if (!pausa){
						MultipleGraph.mRenderer.setYAxisMax((WifiMap.getMaxLevel()/10)*10+10);
						MultipleGraph.mRenderer.setYAxisMin(-100);
						if(Wifi.contador > 15){
							MultipleGraph.mRenderer.setXAxisMax(Wifi.contador);
							MultipleGraph.mRenderer.setXAxisMin(Wifi.contador-15);
						}			
						view.repaint();
					}
				}
			};
			
			registerReceivers();
			
		}catch (Exception e){
			e.printStackTrace();
			LogManager lm = new LogManager(e);
		}
	}
	
	/*	---------- Menu ----------- */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.help_menu, menu);
		return true;
	}

	//Opciones del menú
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try{
			switch(item.getItemId()) {
			
			case R.id.ayuda:
				setResult(Activity.RESULT_CANCELED);
				String text = "Esta gráfica muestra la comparativa en tiempo real de la potencia recibida de"
						+ " cada punto de acceso detectado en las últimas 15 búsquedas realizadas."
						+ "Las redes de las que se reciba mayor potencia por lo general ofrecen una conexión"
						+ "más clara y estable, aunque esto depende también de otros factores.";
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

	protected void onStart() {
		super.onStart();
		view = ChartFactory.getLineChartView(this, MultipleGraph.mDataset, MultipleGraph.mRenderer);		
		setContentView(view);
	}

	@Override
	protected void onPause() {
		super.onPause();
		WifiThread.isGraph = false;
		unRegistrerReceivers();
		MultipleGraph.deleteGraph();
	}
	@Override
	protected void onResume() {
		try{
			super.onResume();
			WifiThread.isGraph = true;
			MultipleGraph.createGraph();
			if (mChartView == null) {
				LinearLayout layout = new LinearLayout(this);
				layout.setOrientation(LinearLayout.VERTICAL);
				mChartView = ChartFactory.getLineChartView(this, MultipleGraph.mDataset, MultipleGraph.mRenderer);
				layout.addView(mChartView, new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT));
			} else {
				mChartView.repaint();
			}
			registerReceivers();
		}catch (Exception e){
			e.printStackTrace();
			LogManager lm = new LogManager(e);
		}
	}

	private void registerReceivers(){
		registerReceiver(wifiReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
		registerReceiver(graphReceiver, new IntentFilter("com.mdes.mywifi.timer"));
		registerReceiver(wifiNotFoundReceiver, new IntentFilter("com.mdes.mywifi.wifinotfound"));
		registerReceiver(wifiNotFoundReceiver, new IntentFilter("com.mdes.mywifi.wififound"));
	}

	private void unRegistrerReceivers(){
		unregisterReceiver(wifiReceiver);
		unregisterReceiver(wifiNotFoundReceiver);
		unregisterReceiver(graphReceiver);
	}
	
}
