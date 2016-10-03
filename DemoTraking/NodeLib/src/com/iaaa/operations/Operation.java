package com.iaaa.operations;

import org.json.JSONObject;

import android.content.Context;
/**
 * Interfaz que deben implementar las respuesta a una operación
 * @author Ricardo Pallás
 */
public interface Operation {
	
	/**
	 * Ejecuta la operación especificada en el jsonObject y devuelve un string en formato JSON con la respuesta a dicha operación
	 * @param jsonObject Objecto JSON con la petición de operación
	 * @return String en formato JSON con la respuesta generada al ejectutar la operación
	 */
	public abstract String execute(JSONObject jsonObject, Context context);
}
