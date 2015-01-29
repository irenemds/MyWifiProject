package com.mdes.mywifi;

public class WifiSecurityImage {

	public static int getWifiSecurityImage(String SSID){
		
		//	Diferentes imágenes en función del nivel de seguridad
		if(HiloWifi.wifiMap.getWifi(SSID).isSecurity()){
			return R.drawable.lockdown;
		}
		else{
			return R.drawable.lockup;
		}
	}
}
