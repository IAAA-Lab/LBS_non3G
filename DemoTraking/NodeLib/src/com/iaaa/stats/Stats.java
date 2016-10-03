package com.iaaa.stats;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * Clase encargada de registrar estadísticas de uso del framework; SMS enviados/recibidos, peticiones HTTP enviadas/recibidas
 * @author Ricardo Pallás
 *
 */
public class Stats {

	private Context context; //Contexto de la aplicación
	private SharedPreferences prefs; //Preferencias de la aplicación

	//CLaves de las estad�sticas
	private final String KEY_SENT_MESSAGE = "sentMessage";
	private final String KEY_RECEIVED_MESSAGE = "receivedMessage";
	private final String KEY_SENT_HTTP = "sentHttp";
	private final String KEY_RECEIVED_HTTP = "receivedHttp";

	public Stats(Context context){
		this.context = context;
		prefs = context.getSharedPreferences("Stats", context.MODE_PRIVATE );

	}

	/**
	 * Incrementa las estadística por una unidad de mensajes enviados
	 */
	public void sentMessage(){
		incrementStat(KEY_SENT_MESSAGE);
	}

	/**
	 * Incrementa las estadística por una unidad de mensajes recibidos
	 */
	public void receivedMessage(){
		incrementStat(KEY_RECEIVED_MESSAGE);
	}

	/**
	 * Incrementa las estadística por una unidad de comandos http enviados
	 */
	public void sentHttp(){
		incrementStat(KEY_SENT_HTTP);
	}

	/**
	 * Incrementa las estadística por una unidad de comandos http recibidos
	 */
	public void receivedHttp(){
		incrementStat(KEY_RECEIVED_HTTP);
	}

	/**
	 * Devuelve un vector de enteros con la estadísticas de uso de la aplicación hasta el momento.
	 * El formato del array es:
	 *  
	 * 		[sms enviados, sms recibidos, comandos http enviados, comandos http recibidos]
	 * 
	 * @return Vector de enteros con las estadística de uso de la aplicación hasta el momento
	 */
	public int[] getStats(){
		int[] stats = new int[4];
		stats[0] = prefs.getInt(KEY_SENT_MESSAGE,0);
		stats[1] = prefs.getInt(KEY_RECEIVED_MESSAGE,0);
		stats[2] = prefs.getInt(KEY_SENT_HTTP,0);
		stats[3] = prefs.getInt(KEY_RECEIVED_HTTP,0);
		return stats;
	}

	/**
	 * Resetea a 0 los contadores de estad�sticas
	 */
	public void resetStats(){
		//Añadir nuevo valor, incrementado por 1
		Editor editor = prefs.edit();
		editor.putInt(KEY_SENT_MESSAGE, 0);
		editor.putInt(KEY_RECEIVED_MESSAGE, 0);
		editor.putInt(KEY_SENT_HTTP, 0);
		editor.putInt(KEY_RECEIVED_HTTP, 0);
		editor.commit();
	}


	/*
	 * Incrementa una estadística en una unidad
	 * @param statKey - EL nombre de la estadística a incrementar
	 */
	private void incrementStat(String statKey){
		//Obtener valor previo
		int oldValue = prefs.getInt(statKey, 0);

		//Añadir nuevo valor, incrementado por 1
		Editor editor = prefs.edit();
		editor.putInt(statKey, oldValue + 1);
		editor.commit();
	}

}

