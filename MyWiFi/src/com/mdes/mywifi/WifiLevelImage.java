package com.mdes.mywifi;

public class WifiLevelImage {
	
	private static int[] signals = new int[]{ R.drawable.signal1, R.drawable.signal2, R.drawable.signal3,
			R.drawable.signal4, R.drawable.signal5};
	
	public static int getWifiLevelImage(int level){
		
		//	Diferentes imágenes en función del nivel de señal
		if(level <=-85){
			return signals[0];
		}
		else if(level <= -75){
			return signals[1];
		}
		else if(level <= -55){
			return signals[2];
		}
		else if(level <= -45){
			return signals[3];
		}
		else if(level >= -45){
			return signals[4];
		}
		return 0;
	}

}
