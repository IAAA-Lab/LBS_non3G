package com.iaaa.operations;

import org.json.JSONObject;

import android.content.Context;

import com.iaaa.appnode.HashCalculator;
import com.iaaa.json.JSONHelper;

/**
 * Clase que representa la operación de DevicePing
 * @author Ricardo Pallás
 */
public class DevicePing extends OperationBase {

	

	
	/**
	 * Constructor del objeto operación DevicePing
	 */
	public DevicePing(){
		super();
		super.CMD = "DeviceACKRes";
		
	}

	/**
	 * Ejecuta la operación que devuelve un ACK del nodo.
	 */
	@Override
	public String execute(JSONObject jsonObject, Context context) {
		
		
		//Generar respuesta
		String commandSource = "DevicePing";
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
