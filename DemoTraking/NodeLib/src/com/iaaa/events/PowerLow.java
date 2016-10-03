package com.iaaa.events;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.BatteryManager;
import android.preference.PreferenceManager;

import com.iaaa.appnode.HashCalculator;
import com.iaaa.json.JSONHelper;

/**
 * Clase que representa el evento PowerLow
 * @author Ricardo Pallás
 */
public class PowerLow extends EventBase {

	
	/**
	 * Constructor del objeto Evento PowerLow
	 */
	public PowerLow(){
		super();
		super.EVT = "PowerLowEvent";
		
	}
	/**
	 * Ejecuta el evento que manda la un aviso por batería baja
	 */
	@Override
	public String execute(Context context) {
		//Obtener nivel de bater�a
		IntentFilter ifilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = context.registerReceiver(null, ifilter);
        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);

        float batteryPct = level / (float)scale;
		
		//Generar respuesta

		String powerLevel = batteryPct+"";
		super.p = new String[1];
		super.p[0] = powerLevel;
		
		//Convertir a JSON String
		JSONHelper jsonHelper = new JSONHelper();
		String result = jsonHelper.operationToJSONString(this);
		
		//calcular HASH
		HashCalculator hashCalculator = new HashCalculator();
		String hash = hashCalculator.calculateHash(result);

		//actualizar JSON String con el hash
		super.h = hash;
		result = jsonHelper.operationToJSONString(this);
		
		//Devolver respuesta
		return result;
	}

}
