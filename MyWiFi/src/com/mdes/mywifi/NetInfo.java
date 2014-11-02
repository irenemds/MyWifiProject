package com.mdes.mywifi;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

public class NetInfo extends Activity {

	private TextView SSID;
	private TextView CAP;
	private TextView FREQ;
	private TextView BSSID;
	private TextView CHAN;
	private ImageView level;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
//		Log.i("INFO", "Comienza ejecución actividad netInfo");
		setContentView(R.layout.net_info);
		
		SSID = (TextView) findViewById(R.id.SSID);
		CAP = (TextView) findViewById(R.id.CAP);
		FREQ = (TextView) findViewById(R.id.FREQ);
		BSSID = (TextView) findViewById(R.id.BSSID);
		CHAN = (TextView) findViewById(R.id.CHAN);
		
		SSID.setText(getWifiExtras().get(0));
		BSSID.setText(getWifiExtras().get(1));
		CAP.setText(getWifiExtras().get(2));
		FREQ.setText(getWifiExtras().get(3));
		CHAN.setText(getWifiExtras().get(4));
	}

	private List<String> getWifiExtras(){
		
		Bundle extras;
		ArrayList<String> list = new ArrayList<String>();
		
		extras = getIntent().getExtras();
		if(extras == null) {
			return null;
		} else {
			list.add(extras.getString("SSID"));
			list.add(extras.getString("BSSID"));
			list.add(extras.getString("CAP"));
			list.add(Integer.toString(extras.getInt("FREQ")) + " MHz");
			list.add(Integer.toString(extras.getInt("CHAN")));
		}
		
		return list;
		
	}
}