package com.mdes.mywifi;
 
import java.util.List;

import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.renderer.XYMultipleSeriesRenderer;

import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.mdes.mywifi.activity.WifiListActivity;
import com.mdes.mywifi.chart.FrequencyGraphActivity;
import com.mdes.mywifi.chart.MultipleGraph;
/**
 * Esta clase contiene el hilo que mueve toda la aplicación, es el encargado
 * de obtener todos los valores que se mostrarán en las actividades y también
 * se encarga de controlar el estado del Wifi.
 * Manda mensajes BroadCast que son interpretados por las actividades.
 *
 */
public class HiloWifi extends Thread{
    /**
     * Control de ejecución del hilo
     */
    private static boolean bucle;
 
    private WifiManager  wifiManager;
 
    private List<ScanResult> resultWifiList;
    public static WifiMap wifiMap;
    private WifiListActivity wifiList;
    /**
     * Booleano para comprobar que sólo hay un hilo
     */
    public static boolean isThread = false;
     
    public static CurrentAP currentAP = new CurrentAP();
    
    SupplicantState supState; 
 
    /**
     * HiloWifi, inicializa el hilo, dándole valor a sus principales atributos
     * @param wifiList objeto de la clase WifiList, obtiene WifiManager y Contexto
     */
    public HiloWifi(WifiListActivity wifiList){
 
        wifiMap = new WifiMap();
        this.wifiList = wifiList;
        wifiManager = wifiList.getWifiManager();
 
        //La variabe bucle solo será false cuando lo indique alguna de las actividades
        bucle = true;
 
        MultipleGraph multipleGraph = new MultipleGraph();}
 
    public void run(){
        Log.i("INFO","Comienza ejección del hilo");
        //Existe hilo
        isThread = true;
        //Contador de escaneos realizados
        Wifi.contador = 1;
 
        while(bucle){
                      try{
            try{
                //Si el wifi está conectado realiza el escaneo
                if (wifiManager.isWifiEnabled()){
                    wifiManager.startScan();
                    //Comprueba si está conectado a alguna red
                    supState = wifiManager.getConnectionInfo().getSupplicantState();
                    Log.e("INFO", supState.toString());
                    if( supState.toString().equals("ASSOCIATED") || supState.toString().equals("COMPLETED") ){
                        currentAP.updateAP(wifiManager.getConnectionInfo()); 
                    }
 
                    //Reinicializar controladores de gráfica de frecuencia para redibujarla
                    FrequencyGraphActivity.mDataset = new XYMultipleSeriesDataset();
                    FrequencyGraphActivity.mRenderer = new XYMultipleSeriesRenderer();
 
                    resultWifiList = wifiManager.getScanResults();
                    if (resultWifiList.size()!=0 && resultWifiList != null){
                        //Una vez obtenidos los resultados avisa a las demás actividades
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
                }
                //Actualiza los valores de resultados en WifiList
                wifiList.updateValues(resultWifiList);
                 
            }catch(NullPointerException e){
                LogManager lm = new LogManager(e);
                e.printStackTrace();
            }
            Wifi.contador++;
            try {
                sleep(500);
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
 * Esta función se encarga de parar el hilo si existe
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
 * Esta función se encarga de crear el hilo, si no existe
 */
    public void createThread(){
        if (!isThread){
            start();
            isThread = true;
        }
    }
}