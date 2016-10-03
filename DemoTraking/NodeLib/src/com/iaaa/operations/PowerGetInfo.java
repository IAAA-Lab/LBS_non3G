package com.iaaa.operations;

import org.json.JSONObject;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.iaaa.appnode.HashCalculator;
import com.iaaa.appnode.SettingsActivity;
import com.iaaa.json.JSONHelper;
/**
 * Clase que representa la operación de PowerGetInfo
 * @author Ricardo Pallás
 */
public class PowerGetInfo extends OperationBase {
	
	/**
	 * Constructor del objeto operación PowerGetInfo
	 */
	public PowerGetInfo(){
		super();
		super.CMD = "PowerInfoRes";
		
	}

	/**
	 * Ejecuta la operación que devuelve la información de la energía de un nodo.
	 */
	@Override
	public String execute(JSONObject jsonObject, Context context) {
		
		//Constantes de la información de energía
		final String POWER_DATA_UNITS = "mV"; //Unidades de energía
		final String POWER_MINIMUM = "5"; //minimum operation value
		final String POWER_MAXIMUM = "100"; //maximun operation value
		
		//Obtener variables de información de energía de las preferencias
		SharedPreferences sharedPref = PreferenceManager.getDefaultSharedPreferences(context);
		
		sharedPref.getString("pref_powerModeType", "");
		
		
		//Generar respuesta

		String powerMode = sharedPref.getString("pref_powerModeType", "");
		String powerDataUnits = POWER_DATA_UNITS;
		String powerDataType = sharedPref.getString("pref_powerDataTypeType", "");;
		String powerMinimum = POWER_MINIMUM;
		String powerMaximum = POWER_MAXIMUM;
		super.p = new String[5];
		super.p[0] = powerMode;
		super.p[1] = powerDataUnits;
		super.p[2] = powerDataType;
		super.p[3] = powerMinimum;
		super.p[4] = powerMaximum;
		
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
