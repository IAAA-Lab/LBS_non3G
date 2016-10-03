package com.iaaa.operations;

import org.json.JSONObject;

import android.content.Context;
/**
 * Clase base para las operaciones
 * @author Ricardo Pallás
 */
public abstract class OperationBase implements Operation {

	protected String CMD; //nombre del comando de la respuesta
	protected String[] p; //par�metros de la respuesta
	protected String h = null; //hash de la respuesta
	
	/**
	 * Ejecuta la operación especificada en el jsonObject y devuelve un string en formato JSON con la respuesta a dicha operación
	 * @param jsonObject Objecto JSON con la petición de operación
	 * @return String en formato JSON con la respuesta generada al ejectutar la operación
	 */
	@Override
	public abstract String execute(JSONObject jsonObject, Context context);

	
	
}
