package com.iaaa.json;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;


/**
 * Clase que ofrece métodos para hacer conversiones de string a JSONObject y 
 * de objecto de java a String JSON. Incorpora, también métodos auxiliares para identificar operaciones del protcolo.
 * 
 * @author Ricardo Pallás
 */
public class JSONHelper {

	final String OPERATION = "CMD"; //clave de operacion
	final String SEQ_ID = "h"; //clave de id de secuencia

	/**
	 * Transforma un string JSON a un objeto JSON. En caso de que el string
	 * command no sea un string JSON v�lido, devuelve null
	 * 
	 * @param command String JSON a convertir
	 * @return EL JSONObject convertido, o null si el command no es válido.
	 */
	public JSONObject convertToJSONObject(String command){
		if(isJSONValid(command)){
			JSONObject jsonObj = null;
			try {
				jsonObj = new JSONObject(command);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return jsonObj;
		}
		else { 
			return null;
		}
	}

	/**
	 * Transforma un objeto Java a un string JSON
	 * @param obj Objeto Java para transformar a String JSON
	 * @return
	 * Devuelve el String JSON correspondiente al objeto (sus atributos no nulos y sus valores se codifican en JSON).
	 */
	public String operationToJSONString(Object obj){
		Gson gson = new Gson();
		String json = gson.toJson(obj);
		return json;
	}


	/**
	 * Método que recibe un objeto JSON correspondiente a una petición de una operación del protocolo SMS
	 * y devuelve el nombre de la operación, correspondiente a la clave CMD.
	 * @param obj Objeto JSON que representa una petición de operación
	 * @return Devuelve el nombre de la operación que se invoca.
	 */
	public String getCommandName(JSONObject obj){
		try {
			return obj.getString(OPERATION);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * Método que recibe una cadena y validad si esta correctamente formada en JSON
	 * 
	 * @param jsonString Cadena JSON que se quiere validad
	 * @return Verdadero si es un String valido y falso en caso contrario
	 */
	public boolean isJSONValid(String jsonString)
	{
		try {
			new JSONObject(jsonString);
			return true;
		} catch(JSONException ex) { 
			return false;
		}
	}


	/**
	 * Método que devuelve un objeto JSON que representa la petición de una operación
	 * Esta cadena es obtenida a partir de una petición de operación en formato HTTP
	 * @param httpRequest Petición de operación codificada en HTTP
	 * @return Petición de operación codificada en un objeto JSON 
	 */
	public JSONObject httpRequestToJSON(String httpRequest){
		//to fix par�metro P
		
		
		
		//Se crea el JSONObject que ser� devuelto
		JSONObject result = new JSONObject();
		//Se parsea la petici�n y se obtiene un mapa con tuplas clave, valor
		Map<String, String> map = getQueryMap(httpRequest);
		Set<String> keys = map.keySet();
		//Se ponen los par�metros HTTP en el objeto JSON
		for (String key : keys)
		{
			try {
				result.put(key, map.get(key));
			} catch (JSONException e) {
				e.printStackTrace();
			}
			
		}
		return result;
	}
	
	/*
	 * Devuelve un Mapa<String, String> que contiene las tuplas <clave, valor> de los par�metros
	 * de una petición de operación HTTP como la del protocolo
	 * @param query String con petición de operación HTTP del protocolo
	 * @return Mapa con las tuplas <clave, valor> de los parámetros de la petición
	 */
	private static Map<String, String> getQueryMap(String query)
	//to fix parámetro P
	{
		String[] params = query.split("&");
		Map<String, String> map = new HashMap<String, String>();
		for (String param : params)
		{
			String name = param.split("=")[0];
			String value = param.split("=")[1];
			map.put(name, value);
		}
		return map;
	}
	
	/**
	 * Transforma un objeto JSON que contiene una respuesta de operación o evento
	 * a un String con el formato HTTP de respuesta del protocolo
	 * @param json objeto JSON que contiene una respuesta de operación o evento
	 * @return String con el formato HTTP de respuesta del protocolo
	 */
	public String jsonToHttpRequest (JSONObject json){
		//to fix parámetro P
		
		
		String result = "";
		Iterator<?> keys = json.keys();
		while(keys.hasNext()){
			String key = (String)keys.next();
			String value = "";
			try {
				value = json.getString(key);
			} catch (JSONException e) {
				e.printStackTrace();
			}
			result = result + key + "=" + value;
			if(keys.hasNext()){
				result = result +"&";
			}
		}
		
		return result;
	}
}
