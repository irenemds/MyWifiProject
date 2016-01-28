package com.mdes.mywifi.log;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

import android.os.Environment;
import android.util.Log;

/**
 * Clase Auxiliar. Permite almacenar en un archivo de texto los errores capturados
 * durante la ejecución.
 *
 */
public class LogManager {

	public LogManager(Exception e){
		 java.util.Date date= new java.util.Date();
		try {
			File root = Environment.getExternalStorageDirectory();
			File file = new File(root +"/"+ "ErrorLog" + ".txt");
			if(!file.exists()){
				file.createNewFile();
			}
			else{
				
			}
			FileWriter fileWriter;
			fileWriter = new FileWriter(file, true);
			BufferedWriter out = new BufferedWriter(fileWriter);
			PrintWriter printWriter = new PrintWriter (fileWriter);
			out.write(date.toString());
			e.printStackTrace (printWriter);
			out.close();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}

}
