package com.iaaa.operations;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.location.Location;

import com.iaaa.appnode.HashCalculator;
import com.iaaa.json.JSONHelper;
import com.iaaa.location.LastLocation;

/**
 * Clase que representa la operación de LocationSet
 * @author Ricardo Pallás
 */
public class LocationSet extends OperationBase {

	/**
	 * Constructor del objeto operación LocationSet
	 */
	public LocationSet(){
		super();
		super.CMD = "DeviceACKRes";
		
	}

	/**
	 * Ejecuta la operación que fija la localización del nodo.
	 */
	@Override
	public String execute(JSONObject jsonObject, Context context) {
		
		//Extraer parametros
		double setLatitude = 0;
		String setNSIndicator = "";
		double setLongitude = 0;
		String setEWIndicator = "";
		
		try {
			JSONArray parametros = jsonObject.getJSONArray("p");
			setLatitude = parametros.getDouble(0);
			setNSIndicator = parametros.getString(1);
			setLongitude = parametros.getDouble(2);
			setEWIndicator = parametros.getString(3);
		} catch (JSONException e) {
			e.printStackTrace();
			//Devuelve un error
			return new DeviceErrorRes("LocationSet", "DEV_ERROR_FRAME", "Valores del parámetro p mal formados").execute(null,null);
		}
		
		
		//Se guarda la nueva localización en el objeto singleton LastLocation
		Location newLocation = new Location("flp");
		newLocation.setLatitude(setLatitude);
		newLocation.setLongitude(setLongitude);
		LastLocation lastLocation = LastLocation.getInstance();
		lastLocation.setLocation(newLocation);
		
		//Generar respuesta
		String commandSource = "LocationSet";
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
