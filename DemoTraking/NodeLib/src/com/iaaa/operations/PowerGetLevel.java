package com.iaaa.operations;

import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;

import com.iaaa.appnode.HashCalculator;
import com.iaaa.appnode.SettingsActivity;
import com.iaaa.json.JSONHelper;

/**
 * Clase que representa la operación de PowerGetLevel
 * @author Ricardo Pallás
 */
public class PowerGetLevel extends OperationBase {
	
	/**
	 * Constructor del objeto operacion PowerGetLevel
	 */
	public PowerGetLevel(){
		super();
		super.CMD = "PowerLevelRes";
		
	}

	/**
	 * Ejecuta la operación que devuelve la información del nivel de batería de un nodo.
	 * 	 
	 */
	@Override
	public String execute(JSONObject jsonObject, Context context) {
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
