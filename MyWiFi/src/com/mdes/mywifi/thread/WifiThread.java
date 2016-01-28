package com.mdes.mywifi.thread;
 
import java.util.List;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Intent;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.mdes.mywifi.activity.WifiListActivity;
import com.mdes.mywifi.chart.FrequencyGraphActivity;
import com.mdes.mywifi.chart.MultipleGraph;
import com.mdes.mywifi.log.LogManager;
import com.mdes.mywifi.wifi.CurrentAP;
import com.mdes.mywifi.wifi.Wifi;
import com.mdes.mywifi.wifi.WifiMap;
/**
 * Hilo que mueve toda la aplicaci�n, encargado de obtener todos los valores
 * que se mostrar�n en las actividades y tambi�n de controlar el estado del Wifi.
 * Manda mensajes BroadCast que son interpretados por las actividades.
 *
 */
public class WifiThread extends Thread{
    /**
     * Control de ejecuci�n del hilo
     */
    private static boolean bucle;
 
    private WifiManager  wifiManager;
 
    private List<ScanResult> resultWifiList;
    public static WifiMap wifiMap;
    private WifiListActivity wifiList;
    /**
     * Booleano para comprobar que s�lo hay un hilo
     */
    public static boolean isThread = false;
     
    public static CurrentAP currentAP = new CurrentAP();
    
    public static SupplicantState supState; 
    public static boolean isGraph;
    public static boolean isFreqGraph;
    /**
     * HiloWifi, inicializa el hilo, d�ndole valor a sus principales atributos
     * @param wifiList objeto de la clase WifiList, obtiene WifiManager y Contexto
     */
    public WifiThread(WifiListActivity wifiList){
 
        wifiMap = new WifiMap();
        this.wifiList = wifiList;
        wifiManager = wifiList.getWifiManager();
        
        FrequencyGraphActivity.mDataset = new XYMultipleSeriesDataset();
        FrequencyGraphActivity.mRenderer = new XYMultipleSeriesRenderer();
 
        //La variabe bucle solo ser� false cuando lo indique alguna de las actividades
        bucle = true;
        isGraph = false;
 
        MultipleGraph multipleGraph = new MultipleGraph();}
    
    public void run(){
        Log.i("INFO","Comienza ejecci�n del hilo");
        //Existe hilo
        isThread = true;
        //Contador de escaneos realizados
        Wifi.contador = 1;
 
        while(bucle){
                      try{
            try{
           	
                //Si el wifi est� conectado realiza el escaneo
                if (wifiManager.isWifiEnabled()){
                    wifiManager.startScan();
                	//Se resetea el n�mero de repetidores de todos los puntos de acceso.
                	WifiMap.resetAntennas();
                    //Comprueba si est� conectado a alguna red
                    supState = wifiManager.getConnectionInfo().getSupplicantState();
                    if( supState.toString().equals("ASSOCIATED") || supState.toString().equals("COMPLETED")
                    		&& wifiManager.getConnectionInfo().getSSID() != null){
                        currentAP.updateAP(wifiManager.getConnectionInfo()); 
                    }
 
                    resultWifiList = wifiManager.getScanResults();    
                }            
                //Actualiza los valores de resultados en WifiList
                wifiList.updateValues(resultWifiList);
                WifiMap.getRepresentableKey();                 
            }catch(NullPointerException e){
                LogManager lm = new LogManager(e);
                e.printStackTrace();
            }
            Wifi.contador++; 
            if (WifiListActivity.wifiManager.isWifiEnabled() && resultWifiList.size()!=0 && resultWifiList != null){
                //Una vez obtenidos los resultados avisa a las dem�s actividades
                Intent i = new Intent();
                i.setAction("com.mdes.mywifi.timer");
                wifiList.sendBroadcast(i);
                 
                Intent i2 = new Intent();
                i2.putExtra("WifiFound", true);
                i2.setAction("com.mdes.mywifi.wififound");
                wifiList.sendBroadcast(i2);
            }
            else{
                //Si no ha encontrado redes wifi
                Log.i("INFO","No encuentra redes wifi");
                Intent i = new Intent();
                i.putExtra("WifiFound", false);
                i.setAction("com.mdes.mywifi.wifinotfound");
                wifiList.sendBroadcast(i);
            }  
            try {
                sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
                      }catch(Exception e){
                          e.printStackTrace();
                          LogManager lm = new LogManager(e);
                          Log.e("INFO", "Error en el hilo");}
        }
 
    }
/**
 * Esta funci�n se encarga de parar el hilo
 */
    public void stopThread(){
        try{
            if (isThread){
                bucle = false;
                Log.i("INFO","fuera del hilo (bucle)");
                isThread = false;
 
            }
        }catch(Exception e){
            LogManager lm = new LogManager(e);
            e.printStackTrace();
        }
    }
 
/**
 * Esta funci�n se encarga de crear el hilo, si no existe
 */
    public void createThread(){
        if (!isThread){
            start();
            isThread = true;
        }
    }
}