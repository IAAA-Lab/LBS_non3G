package com.iaaa.operations;

import org.json.JSONObject;

import android.content.Context;
import android.location.Location;

import com.iaaa.appnode.HashCalculator;
import com.iaaa.json.JSONHelper;
import com.iaaa.location.LastLocation;


/**
 * Clase que representa la operación de LocationGet
 * @author Ricardo Pallás 
 */
public class LocationGet extends OperationBase {

	/**
	 * Constructor del objeto operación LocationGet
	 */
	public LocationGet(){
		super();
		super.CMD = "LocationRes";
		
	}

	/**
	 * Ejecuta la operación que devuelve la localización del nodo.
	 */
	@Override
	public String execute(JSONObject jsonObject, Context context) {
		//Obtener última localización conocida
		Location location = LastLocation.getInstance().getLocation();
		//Obtener parámetros de localización
		String latitude = "latitude";
		String nsIndicator = "N/S";
		String longitude = "longitude";
		String ewIndicator = "E/W";
		String utcTime = "utcTime";
		
		if(location!=null){
			latitude = location.getLatitude()+"";
			longitude = location.getLongitude()+"";
			utcTime = location.getTime()+"";
		}
		
		//Generar respuesta

		super.p = new String[5];
		super.p[0] = latitude;
		super.p[1] = nsIndicator;
		super.p[2] = longitude;
		super.p[3] = ewIndicator;
		super.p[4] = utcTime;
		
		//Convertir a JSON String
		JSONHelper jsonHelper = new JSONHelper();
		String result = jsonHelper.operationToJSONString(this);
		
		//calcular HASH
		HashCalculator hashCalculator = new HashCalculator();
		String hash = hashCalculator.calculateHash(result);

		//actualizar JSON String con el hash
		super.h = hash;
		result = jsonHelper.operationToJSONString(this);
		
		return result;
	}
	

}
