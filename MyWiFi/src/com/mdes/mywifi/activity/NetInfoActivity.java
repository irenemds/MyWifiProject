package com.mdes.mywifi.activity;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.achartengine.GraphicalView;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.mdes.mywifi.R;
import com.mdes.mywifi.broadcastreceiver.WifiChangeReceiver;
import com.mdes.mywifi.broadcastreceiver.WifiNotFoundReceiver;
import com.mdes.mywifi.chart.DialGraphActivity;
import com.mdes.mywifi.chart.LinkSpeedGraphActivity;
import com.mdes.mywifi.help.HelpDialog;
import com.mdes.mywifi.log.LogManager;
import com.mdes.mywifi.thread.WifiThread;
import com.mdes.mywifi.wifi.Wifi;
import com.mdes.mywifi.wifi.WifiLevelImage;
import com.mdes.mywifi.wifi.WifiMap;

/**
 * En esta actividad se muestra información del punto de acceso seleccionado
 * por el usuario en la actividad WifiListActivity.
 *
 */
public class NetInfoActivity extends Activity implements OnItemClickListener {

	private ListView lista;
	
	private Wifi wifi;
	private TextView SSID;
	private ImageView IMAGE;
	private Button buttonp;
	private Button buttonv;

	private String text;

	ActionBar actionBar = null;
	private HelpDialog helpDialog;

	private BroadcastReceiver currentActivityReceiver;
	private WifiChangeReceiver wifiReceiver = new WifiChangeReceiver();
	private WifiNotFoundReceiver wifiNotFoundReceiver = new WifiNotFoundReceiver();

	private GraphicalView mChartView;
	
	private int index;
	private int offset;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		try{
			super.onCreate(savedInstanceState);
			//Configuración de pantalla
			getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
			
			setContentView(R.layout.net_info);

			actionBar = getActionBar();
			actionBar.setDisplayHomeAsUpEnabled(false);

			IMAGE = (ImageView) findViewById(R.id.container);
		    lista = (ListView) findViewById(R.id.List2);
		    SSID = (TextView) findViewById(R.id.SSID);
		    buttonp = (Button) findViewById(R.id.buttonp);
		    buttonv = (Button) findViewById(R.id.buttonv);
		
		    //Recibe red seleccionada
			Bundle extras = getIntent().getExtras();
			Log.i("BSSID", "recibo" + extras.getString("BSSID"));
			wifi = WifiMap.wifiMap.get(extras.getString("BSSID"));
			
			//Representa información en pantalla
			SSID.setText(wifi.getSSID());
			IMAGE.setImageResource(WifiLevelImage.getWifiLevelImage(wifi.getLastLevel()));
			getInfo();
			
			//Activa listener de la lista
			lista.setOnItemClickListener(this);
			
			//BroadCastReceiver para manejar evento de tiempo,mostrar valores actualizados.
			currentActivityReceiver = new BroadcastReceiver(){

				@Override
				public void onReceive(Context context, Intent intent) {
					if(wifi.isRepresentable()){			
						//Actualiza nivel potencia
						IMAGE.setImageResource(WifiLevelImage.getWifiLevelImage(wifi.getLastLevel()));
						try{
							//Fijar posición scroll en los escaneos
							getScrollPosition();
							getInfo();
							setScrollPosition();
							}catch (NullPointerException e){
								e.printStackTrace();
								LogManager lm = new LogManager(e);
							}
					}
					else{
						//Si la red no está disponible cierra la actividad
						finish();
					}
				}

			};

		}catch(Exception e){
			e.printStackTrace();
			LogManager lm = new LogManager(e);
		}		
	}

	/*	---------- Menu ----------- */

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		getMenuInflater().inflate(R.menu.netinfo, menu);
		return true;
	}


	//Acciones del menú
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		try{
			switch(item.getItemId()) {
			case R.id.guardar:
				//Guardar datos de potencia
				File root = Environment.getExternalStorageDirectory();
				File file = new File(root +"/"+ wifi.getSSID() + ".txt");
				Log.i("INFO","root "+ root);
				FileWriter filewriter;
				try {
					//Almacena información de potencia en el dispositivo
					filewriter = new FileWriter(file);
					BufferedWriter out = new BufferedWriter(filewriter);
					out.write(WifiMap.getCSV(wifi.getBSSID()));
					out.close();
				} catch (IOException e) {
					//Guarda en log errores
					LogManager lm = new LogManager(e);
					e.printStackTrace();
				}

				Toast.makeText(getApplicationContext(), "Datos de potencia almacenados en el dispositivo.", Toast.LENGTH_LONG).show();
				setResult(Activity.RESULT_OK);
				return true;

			case R.id.ayuda:
				//Mensaje de ayuda
				setResult(Activity.RESULT_CANCELED);
				String text = "Se muestra la información principal del punto de acceso seleccionado. Para más información acerca de cada uno de los parámetros"
						+ " que se muestran en la pantalla haga click sobre ellos.";
				helpDialog = new HelpDialog(this,"Ayuda", text);
				return true;
				
			case R.id.salir:
				//Salir de la aplicación.
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

	@Override
	protected void onPause() {
		super.onPause();
		unregisterReceivers();
	}

	@Override
	protected void onResume() {
		super.onResume();
		registerReceivers();
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
	
	public void graphHandler (View view)
	{
		Intent intent = new Intent(NetInfoActivity.this, LinkSpeedGraphActivity.class);
		intent.putExtra("BSSID", wifi.getBSSID());
		startActivity(intent);
	}

	public void potenciometro (View view)
	{
		//Iniciar actividad potenciómetro
		Intent intent = new Intent(NetInfoActivity.this, DialGraphActivity.class);
		intent.putExtra("BSSID", wifi.getBSSID());
		startActivity(intent);
	}
	
	public void getInfo(){
		String[] values;
		//Representar valores de parámetros para el punto de acceso conectado al dispositivo
		if (wifi.getBSSID().equals(WifiThread.currentAP.getBSSID()))
		{
	        values = new String[] { "BSSID: "+ wifi.getBSSID(), 
	                "Frecuencia: " +Integer.toString(wifi.getFreq())+ " MHz",
	                "Canal: " + Integer.toString(wifi.getChannel()),
	                "Número de repetidores: " + Integer.toString(wifi.getAntennas()), 
	                "Propiedades: " + wifi.getCap(),
	                "MAC: " + WifiThread.currentAP.getMAC(),
	                "IP: " + WifiThread.currentAP.getIPString()            
	               };
	        buttonv.setEnabled(true);
		}
		//Representar valores de parámetros para el punto de acceso NO conectado al dispositivo
		else{
	        values = new String[] { "BSSID: "+ wifi.getBSSID(), 
	                "Frecuencia: " +Integer.toString(wifi.getFreq())+ " MHz",
	                "Canal: " + Integer.toString(wifi.getChannel()),
	                "Número de repetidores: " + Integer.toString(wifi.getAntennas()), 
	                "Propiedades: " + wifi.getCap(),
	                "MAC: - - - - - ",
	                "IP: - - - - - "
	               };	
	        buttonv.setEnabled(false);
		}
		lista.setAdapter(new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, android.R.id.text1, values));
	}
	//Guarda en las variables index y offset los valores necesarios
	//para reestablecer la posición del scroll.
	private void getScrollPosition(){
		index = lista.getFirstVisiblePosition();	//primera posición visible (puede estar cortada)
		View v = lista.getChildAt(0);
		offset = (v == null) ? 0 : v.getTop();		//offset respecto a dicha posición
	}

	//Establece la posición del scroll con los valores conseguidos
	private void setScrollPosition(){
		lista.setSelectionFromTop(index, offset);	
	}

	@Override
	public void onItemClick(AdapterView parent, View v, int position, long id) {
		switch (position) {
		//Mensajes de ayuda para cada uno de los parámetros representados
		case 0:
			text = "Es el nombre de identificador único de todos los paquetes de una red inalámbrica. "
					+ "Esta formado con la dirección MAC (Media Access Control) del Punto de Acceso seleccionado.";
			helpDialog = new HelpDialog(this,"BSSID (Basic Service Set Identifier)", text);
			break;
		case 1:
			text = "Este campo indica la frecuencia de transmisión empleada por el punto de acceso seleccionado dentro del posible espectro. ";
			helpDialog = new HelpDialog(this,"Frecuencia de transmisión", text);
			break;
		case 2:
			text = "Este campo indica la porción estandarizada del espectro de frecuencias "
					+ "en la que transmite el punto de acceso. Su correcta elección determina en gran medida la calidad de la conexión, "
					+ ", comprueba en las gráficas de frecuencia y canales si el canal "+wifi.getChannel()+" es el mejor para tu punto de acceso.";
			helpDialog = new HelpDialog(this,"Canal de transmisión", text);
			break;
		case 3:
			text = "Número de Puntos de Acceso (AP) detectados que están transmitiendo ese mismo SSID. La existencia de más "
					+ "puntos de acceso para una misma WLAN implia un mayor área de cobertura y mayor capacidad "
					+ "para dar acceso a más dispositivos conectados.";
			helpDialog = new HelpDialog(this,"Repetidores", text);
			break;
		case 4:
			text = "Características de seguridad, cifrado y configuración del Punto de Acceso (AP) seleccionado. "
					+ "Se indica el tipo de autenticación, uso de claves, cifrado y configuración de la red, además indica si el punto de acceso dispone "
					+ "de funcionalidad WPS. Esta información no es muy relevante para la calidad de conexión, pero sí "
					+ "es interesante conocer las opcioines ofrecidas por el punto de acceso.";
			helpDialog = new HelpDialog(this,"Propiedades del AP", text);
			break;
		case 5:
			text = "También conocida como dirección física, es un código formado por"
					+ " 48 bits, es única para cada tarjeta o dispositivo de red.  Sólo disponible si el dispositivo está conectado a esa red.";
			helpDialog = new HelpDialog(this,"Dirección MAC (Medium Access Control)", text);
			break;
		case 6:
			text = "Información del dispositivo móvil, indica la dirección IP que le ha sido asignada dentro de red "
					+ "la WLAN seleccionada. Sólo disponible si el dispositivo está conectado a esa red.";
			helpDialog = new HelpDialog(this,"Dirección IP (Internet Protocol)", text);
			break;
		}
		
	}
	
	

}