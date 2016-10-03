package com.iaaa.events;

import android.content.Context;
import android.location.Location;

import com.iaaa.appnode.HashCalculator;
import com.iaaa.json.JSONHelper;
import com.iaaa.location.LocationFacade;

/**
 * Clase que representa el evento Location
 * @author Ricardo Pallás
 */
public class LocationEvent extends EventLocationBase {
	
	/**
	 * Constructor del objeto Evento Location
	 */
	public LocationEvent(){
		super();
		super.EVT = "LocationEvent";
		
	}
	/**
	 * Ejecuta el evento que manda la localizaci�n del nodo
	 * @param locationFacade LocationFacade para acceder a la localizaci�n del dispositivo
	 */
	@Override
	public String execute(Context context, LocationFacade locationFacade) {
		
		//Obtener la última localizaci�n del dispositivo
		Location location = locationFacade.getLocation();
		//Atributos de la respuesta
		String latitude = "latitude";
		String nsIndicator = "N/S";
		String longitude = "longitude";
		String ewIndicator = "E/W";
		String utcTime = "utcTime";
		
		//Si la localización es distinta de null obtener los valores
		if (location!=null){
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
		
		//Devolver respuesta
		return result;
	}

}
