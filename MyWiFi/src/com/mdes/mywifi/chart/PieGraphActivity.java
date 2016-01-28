package com.mdes.mywifi.chart;
/**
 * Esta clase se emplea para crear las gráficas que representarán el número de puntos de acceso transmitiendo
 * por canal de transmisión.
 *
 */
import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

import android.R.layout;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Color;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
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
import com.mdes.mywifi.wifi.WifiMap;

public class PieGraphActivity extends Activity {
	private DefaultRenderer renderer = new DefaultRenderer();
	private CategorySeries series = new CategorySeries("Pie Graph");
	private static GraphicalView view;
	private WifiChangeReceiver wifiReceiver = new WifiChangeReceiver(); 
	private int[] values;
	private int[] colors = new int[] { Color.parseColor("#009C8F"), Color.parseColor("#74C044"), 
			Color.parseColor("#EEC32E"), Color.parseColor("#84C441"),
			Color.parseColor("#41C4BF"), Color.parseColor("#4166C4"),
			Color.parseColor("#B04E9D"), Color.parseColor("#FF2F2F"),
			Color.parseColor("#33FF99"), Color.parseColor("#DCE45F"),
			Color.parseColor("#FFA500")};
	private SimpleSeriesRenderer[] simpleSeriesRenderer;
	private LinearLayout layout;
	private BroadcastReceiver currentActivityReceiver;
	private WifiNotFoundReceiver wifiNotFoundReceiver = new WifiNotFoundReceiver();
	private boolean pausa = false;

	ActionBar actionBar = null;
	private HelpDialog helpDialog;

	protected void onCreate(Bundle savedInstanceState) {
		try{ 
			registerReceiver(wifiReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
			super.onCreate(savedInstanceState);

			simpleSeriesRenderer = new SimpleSeriesRenderer[11]; 
			for (int i = 0; i < simpleSeriesRenderer.length; i++){
				simpleSeriesRenderer[i] = new SimpleSeriesRenderer();
				simpleSeriesRenderer[i].setColor(colors[i]);				
			}

			//Quitar título de la actividad y pantalla completa
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

			actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(false);

			renderer = new DefaultRenderer();
			series = new CategorySeries("Pie Graph");


			getValues();
			renderer.setChartTitleTextSize(27);
			renderer.setStartAngle(180);
			renderer.setDisplayValues(true);
			renderer.setBackgroundColor(Color.BLACK);
			renderer.setLabelsTextSize(30);
			renderer.setLegendTextSize(25);
			renderer.setApplyBackgroundColor(true);
			renderer.setMargins(new int[] { 50, 40, 10, 30 });
			renderer.setShowLegend(true);
			renderer.setZoomEnabled(false);
			renderer.setPanEnabled(false);

			currentActivityReceiver= new BroadcastReceiver(){

				@Override
				public void onReceive(Context context, Intent intent) {	
					if (!pausa){
						getValues();
						view.repaint();
					}
				}

			};
		}catch(Exception e){
			e.printStackTrace();
			LogManager lm = new LogManager(e);
		}
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
				String text = "Esta gráfica muestra una comparativa gráfica de la proporción "
						+ "de puntos de acceso que están transmitiendo en cada canal. Aquellos "
						+ "canales en los que transmita un menor número de redes sufrirán menos interferencias"
						+ ", por lo que es recomendable seleccionarlos en la configuración de tu punto de "
						+ "acceso inalámbrico, aunque también se debe tener en cuenta las interferencias"
						+ "que pueden provocar los puntos de acceso que transmiten en los canales contiguos."
						+ "Esta información se puede comprobar en la gráfica de frecuencia.";
				helpDialog = new HelpDialog(this,"Ayuda", text);
				return true;
			case R.id.salir:
				Intent intent = new Intent(Intent.ACTION_MAIN);
				intent.addCategory(Intent.CATEGORY_HOME);
				intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
				startActivity(intent);
				return true;
			case R.id.pausa:
				pausa = !pausa;
				if (pausa){
					item.setTitle("Reanudar");
				}
				else{
					 item.setTitle("Pausa");
				}
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
		try{
			super.onStart();
			view = ChartFactory.getPieChartView(this, series, renderer);
			setContentView(view);
		}catch(Exception e){
			e.printStackTrace();
			LogManager lm = new LogManager(e);
		}
	}	
	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceivers();
	}

	private void getValues(){
		series.clear();
		renderer.removeAllRenderers();
		values = WifiMap.getChannelAP();
		for (int i = 0; i<values.length; i++) {
			int canal = i+1;
			if (values[i] > 0){
				series.add("Canal " + canal, values[i]);
				renderer.addSeriesRenderer(simpleSeriesRenderer[i]);
			}
		}
	}


	protected void onResume() {
		registerReceivers();
		try{
			super.onResume();
			registerReceiver(wifiReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
			if (view == null) {
				layout = (LinearLayout) findViewById(R.id.chart);
				view = ChartFactory.getPieChartView(this, series, renderer);
				renderer.setClickEnabled(true);
				layout.addView(view, new LayoutParams(LayoutParams.MATCH_PARENT,
						LayoutParams.MATCH_PARENT));
			} else {
				view.repaint();
			}

		}catch(Exception e){
			e.printStackTrace();
			LogManager lm = new LogManager(e);
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
