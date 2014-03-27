package com.mdes.mywifi;

public class Wifi {
       private String SSID;
       private String BSSID;
       private String capabilities;
       private float level;
       private float frequency;

       public Wifi(String SSID, String BSSID, String capabilities,
             float level, float frequency) {
             this.SSID = SSID;
             this.BSSID = BSSID;
             this.capabilities = capabilities;
             this.level = level;
             this.frequency = frequency;
       }

	public String getSSID() {
		return SSID;
	}

	public void setNombre(String SSID) {
		this.SSID = SSID;
	}

	public String getBSSID() {
		return BSSID;
	}

	public void setBSSID(String BSSID) {
		this.BSSID = BSSID;
	}

	public String getCapabilities() {
		return capabilities;
	}

	public void setCapabilities(String capabilities) {
		this.capabilities = capabilities;
	}

	public float getLevel() {
		return level;
	}

	public void setLevel(float level) {
		this.level = level;
	}

	public float getFrequency() {
		return frequency;
	}

	public void setFrequency(float frequency) {
		this.frequency = frequency;
	}

	public void setSSID(String sSID) {
		SSID = sSID;
	}
}
