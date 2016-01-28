
package com.mdes.mywifi.chart;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DialRenderer;
import org.achartengine.renderer.DialRenderer.Type;
import org.achartengine.renderer.SimpleSeriesRenderer;

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

import com.mdes.mywifi.R;
import com.mdes.mywifi.broadcastreceiver.WifiChangeReceiver;
import com.mdes.mywifi.broadcastreceiver.WifiNotFoundReceiver;
import com.mdes.mywifi.help.HelpDialog;
import com.mdes.mywifi.log.LogManager;
import com.mdes.mywifi.wifi.Wifi;
import com.mdes.mywifi.wifi.WifiMap;

/**
 * Esta clase se emplea para realizar la representación gráfica del nivel de potencia recibido de un
 * punto de acceso empleando la opción Dial Graph de la biblioteca de gráficas.
 *
 */
public class DialGraphActivity extends Activity  {

	Wifi wifi;
	private GraphicalView view;
	private CategorySeries category;
	private DialRenderer renderer;
	private SimpleSeriesRenderer r;

	private WifiChangeReceiver wifiReceiver = new WifiChangeReceiver();
	private BroadcastReceiver currentActivityReceiver;
	private WifiNotFoundReceiver wifiNotFoundReceiver = new WifiNotFoundReceiver();

	private boolean saved = false;
	ActionBar actionBar = null;
	private HelpDialog helpDialog;
	
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		
		//Quitar título de la actividad y pantalla completa
		getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

		//Dar de alta receivers
		registerReceivers();

		//Recibe información del punto de acceso actual mediante extras de la actividad previa
		Bundle extras = getIntent().getExtras();
		wifi = WifiMap.wifiMap.get(extras.getString("BSSID"));
		category = new CategorySeries(wifi.getSSID());

		actionBar = getActionBar();
		actionBar.setDisplayHomeAsUpEnabled(false);
		
		//Configuración gráfica
		setUp();
		renderer = new DialRenderer();
		renderer.setZoomEnabled(false);
		renderer.setClickEnabled(false);
		renderer.setPanEnabled(false);
		renderer.setLabelsTextSize(25);
		renderer.setLegendTextSize(25);
		renderer.setMargins(new int[] {50, 40, 15, 30});
		r = new SimpleSeriesRenderer();
		r.setColor(Color.parseColor("#009C8F"));
		renderer.addSeriesRenderer(r);
		r = new SimpleSeriesRenderer();
		r.setColor(Color.WHITE);
		renderer.addSeriesRenderer(r);
		r = new SimpleSeriesRenderer();
		r.setColor(Color.parseColor("#C1CABC"));
		renderer.addSeriesRenderer(r);
		renderer.setLabelsTextSize(20);
		renderer.setLabelsColor(Color.WHITE);
		renderer.setShowLabels(true);
		renderer.setVisualTypes(new DialRenderer.Type[] {Type.ARROW, Type.NEEDLE, Type.NEEDLE});
		renderer.setMinValue(-120);
		renderer.setMaxValue(-20);
		renderer.setApplyBackgroundColor(true);
		renderer.setBackgroundColor(Color.BLACK);

		currentActivityReceiver = new BroadcastReceiver(){
			//Receiver para actualizar información cuando el hilo secundario avise
			@Override
			public void onReceive(Context context, Intent intent) {
				if (wifi.isRepresentable()){
					//vaciar, configurar y actualizar gráfica
					category.clear();
					setUp();
					view.repaint();
				}else{
					finish();
				}
			}

		};
		registerReceivers();

		view = ChartFactory.getDialChartView(this, category, renderer);		
		setContentView(view);
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

	private void setUp(){

		category.add("Actual", wifi.getLastLevel());
		category.add("Mínimo", wifi.getMinLevel());
		category.add("Máximo", wifi.getMaxLevel());
		if (saved){
			category.add("Guardado", wifi.getSavedValue());
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceivers();
	}
	
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.dial_menu, menu);
		return true;
	}

	//Opciones del menú
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try{
			switch(item.getItemId()) {
			case R.id.ayuda:
				setResult(Activity.RESULT_CANCELED);
				String text = "En esta imágen se puede comprobar el valor de potencia obtenido en"
						+ " el último escaneo del Punto de Acceso seleccionado y, también, los valores"
						+ " máximo y mínimo recibidos en anteriores escaneos de dicho AP."
						+ "Permite también guardar el valor de un escaneo para compararlo con los posteriores."
						+ "Esto puede resultar útil para analizar la potencia recibida en función de la posición"
						+ " en la que se encuentre el dispositivo móvil y/o el AP. ";
				helpDialog = new HelpDialog(this,"Ayuda", text);
				return true;
			case R.id.guardar:
				//Guardar valor en objeto wifi
				wifi.setSavedValue(wifi.getLastLevel());
				if (!saved){
					r = new SimpleSeriesRenderer();
					r.setColor(Color.YELLOW);
					renderer.addSeriesRenderer(r);
					renderer.setVisualTypes(new DialRenderer.Type[] {Type.ARROW, Type.NEEDLE, Type.NEEDLE, Type.ARROW});
					saved = true;
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
}
