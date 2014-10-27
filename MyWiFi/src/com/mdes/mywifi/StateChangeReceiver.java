//package com.mdes.mywifi;
//
//import android.content.BroadcastReceiver;
//import android.content.Context;
//import android.content.Intent;
//import android.net.wifi.WifiManager;
//import android.util.Log;
//
//public class StateChangeReceiver extends BroadcastReceiver {
//
//	private WifiList wifiList = new WifiList();
//	
//	
//	
//		@Override
//		public void onReceive(Context c, Intent intent) {
//			
//			int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);		
//				switch(extraWifiState){
//				case WifiManager.WIFI_STATE_DISABLING:
//					Log.i("INFO", "Broadcast -  Wifi off");
//					wifiList.getHiloWifi().setBucleOff();	
//		  			wifiList.wifiAlertDialog(c);
//					break;
//					
//				case WifiManager.WIFI_STATE_ENABLED:
//					Log.i("INFO", "Broadcast -  Wifi on, lanza hilo");							
//					wifiList.NewHiloWifi();
//					break;	
//					
//				case WifiManager.WIFI_STATE_UNKNOWN:
//					Log.i("INFO", "Broadcast -  Wifi desconocido");
//					break;
//				}
//		}
//
//}
