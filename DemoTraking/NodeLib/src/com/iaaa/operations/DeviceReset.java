package com.iaaa.operations;

import org.json.JSONObject;

import android.R;
import android.content.Context;
import android.preference.PreferenceManager;

import com.iaaa.appnode.HashCalculator;
import com.iaaa.json.JSONHelper;

/**
 * Clase que representa la operación de DeviceReset
 * @author Ricardo Pallás
 */
public class DeviceReset extends OperationBase {
	/**
	 * Constructor del objeto operación DeviceReset
	 */
	public DeviceReset(){
		super();
		super.CMD = "DeviceACKRes";
		
	}

	/**
	 * Ejecuta la operación que resetea un nodo
	 */
	@Override
	public String execute(JSONObject jsonObject, Context context) {
		
		//Resetear
		PreferenceManager.getDefaultSharedPreferences(context.getApplicationContext()).edit().clear().commit();
		
		//Generar respuesta

		String commandSource = "DeviceReset";
		super.p = new String[1];
		super.p[0] = commandSource;

		
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
