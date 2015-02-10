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

import com.mdes.mywifi.WifiThread;
import com.mdes.mywifi.R;
import com.mdes.mywifi.broadcastreceiver.WifiChangeReceiver;
 
public class SplashScreenActivity extends Activity {

    private static final long SPLASH_SCREEN_DELAY = 3000;
    private WifiThread hiloWifi;
    private WifiChangeReceiver wifiReceiver = new WifiChangeReceiver();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.splash_screen);

        //Inicia servicio controlador de Wifi
		WifiListActivity.wifiManager = (WifiManager) this.getSystemService(Context.WIFI_SERVICE);
        
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
    		
    		
        Timer timer = new Timer();
        timer.schedule(task, SPLASH_SCREEN_DELAY);
    }
 
}