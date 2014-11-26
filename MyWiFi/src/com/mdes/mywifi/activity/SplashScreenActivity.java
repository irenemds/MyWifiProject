package com.mdes.mywifi.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.Window;

import com.mdes.mywifi.HiloWifi;
import com.mdes.mywifi.R;
import com.mdes.mywifi.broadcastreceiver.WifiChangeReceiver;
 
public class SplashScreenActivity extends Activity {

    private static final long SPLASH_SCREEN_DELAY = 3000;
    private WifiManager wifiManager; 
    private HiloWifi hiloWifi;
    private WifiChangeReceiver wifiReceiver = new WifiChangeReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_screen);
//        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                Intent mainIntent = new Intent().setClass(
                        SplashScreenActivity.this, WifiListActivity.class);
//        		unregisterReceiver(wifiReceiver);
                startActivity(mainIntent);
                finish();
            }
        };
    		wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
    		
    		//Comprobar estado inicial de Wifi, si esta desactivado mostrar dialogo
    		if (wifiManager.isWifiEnabled() == false)
    		{  
    			wifiReceiver.wifiAlertDialog(this);
    		}
    		// El wifi está activado, lanzar hilo
    		
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }
 
}