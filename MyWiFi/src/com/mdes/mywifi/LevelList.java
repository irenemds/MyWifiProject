//package com.mdes.mywifi;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//
//import android.util.Log;
//
//public class LevelList {
//
//	public HashMap<String,ArrayList<Integer>> levelList;
//	private ArrayList<Integer> lista;
//	
//	public LevelList(){
//		levelList  = new HashMap<String,ArrayList<Integer>>();
//		lista = new ArrayList<Integer>();
//	}
//	
//	public void saveLevel (int level, String SSID){
//		try{
//			
//		
//		if (levelList.containsKey(SSID)){
//			lista = levelList.get(SSID);
////			for (int i = 0; i < lista.size(); i++) {
////				Log.i("VALORES", SSID + lista.get(i));
////			}
//			lista.add(level);
//		}
//		else{
//			Log.i("VALORES", SSID + "primer valor: " + level);
//			lista = new ArrayList<Integer>();
//			lista.add(level);
//		}
//		levelList.put(SSID, lista);
//		}catch(NullPointerException e){
//			e.printStackTrace();
//			Log.e("INFO","ERROR GUARDANDO EN LEVELLIST");
//		}
//	}
//	
//	public List<Integer> getLevels (String SSID){
//		return levelList.get(SSID);
//	}
//	
//	public int getLastLevel (String SSID){
//		 lista = levelList.get(SSID);
//		 return lista.get(lista.size()-1);
//	}
//	
//	public boolean isSSID (String SSID){
//		return levelList.containsKey(SSID);
//	}
//	
//	public void removeSSID (String SSID){
//		levelList.remove(SSID);
//	}
//	
//	
//}
