package com.mdes.mywifi.wifi;

import com.mdes.mywifi.R;
import com.mdes.mywifi.R.drawable;
import com.mdes.mywifi.thread.WifiThread;

public class WifiSecurityImage {

	public static int getWifiSecurityImage(String BSSID){
		
		//	Diferentes imágenes en función del nivel de seguridad
		if(WifiThread.wifiMap.wifiMap.get(BSSID).isSecurity()){
			return R.drawable.lockdown;
		}
		else{
			return R.drawable.lockup;
		}
	}
}
