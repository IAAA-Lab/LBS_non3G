package com.iaaa.operations;

import org.json.JSONObject;

import com.iaaa.json.JSONHelper;
/**
 * Clase base que representa los errores en las operaciones
 * @author Ricardo Pallás
 */
public abstract class ErrorBase implements Operation {

	protected String CMD; //nombre del comando de la respuesta
	protected String[] p; //parámetros de la respuesta
	protected String h = null; //hash de la respuesta
	


	
	
}
