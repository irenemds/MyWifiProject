package com.mdes.mywifi;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Parcelable;
import android.util.Log;

public class BroadcastService  extends Service {
	private static final String TAG = "BroadcastService";
	public static final String BROADCAST_ACTION = "com.mdes.mywifi.displayevent";
	private final Handler handler = new Handler();
	Intent intent;
	public static List<ScanResult> resultWifiList;
	private WifiManager  wifiManager;
	
	@Override
	public void onCreate() {
		super.onCreate();

		wifiManager = (WifiManager)this.getSystemService(Context.WIFI_SERVICE);
    	intent = new Intent(BROADCAST_ACTION);	
	}
	
    @Override
    public void onStart(Intent intent, int startId) {
        handler.removeCallbacks(sendUpdatesToUI);
        handler.postDelayed(sendUpdatesToUI, 1000); // 1 second
   
    }

    private Runnable sendUpdatesToUI = new Runnable() {
    	public void run() {
    		DisplayLoggingInfo();    		
    	    handler.postDelayed(this, 10000); // 10 seconds
    	}
    };    
    
    private void DisplayLoggingInfo() {
    	Log.d(TAG, "entered DisplayLoggingInfo");
    	resultWifiList = wifiManager.getScanResults();
    	
    	intent.putParcelableArrayListExtra("ScanResults", (ArrayList<? extends Parcelable>) resultWifiList);
    	sendBroadcast(intent);
    }
	
	@Override
	public IBinder onBind(Intent intent) {
		return null;
	}

	@Override
	public void onDestroy() {		
        handler.removeCallbacks(sendUpdatesToUI);		
		super.onDestroy();
	}		
}
