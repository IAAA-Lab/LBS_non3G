package com.iaaa.location;

import android.content.Context;
import android.location.Location;
/**
 * 
 * Fachada para invocar a las operaciones de localización del dispositivo.
 * @author Ricardo Pallás
 *
 */
public class LocationFacade {
	private Context context; //Contexto de la aplicación
	private LocationReader locationReader; //Objeto para leer la localización
	private final String DEBUG_TAG = "SensorLib";

	/**
	 * Constructor del objeto.
	 * @param context Contexto de la aplicación
	 */
	public LocationFacade(Context context){
		this.context = context;
	}
	/**
	 * Construye el objeto LocationReader que empieza a escuchar la localización
	 * del dispositivo a través del GPS.
	 * @param refreshRate Tasa de refresco para obtener la localización
	 */
	public void startLocationListening(int refreshRate){
		locationReader = new LocationReader(context, refreshRate);
	}
	/**
	 * Cambia la tasa de refresco para obtener la localización
	 * @param refreshRate Tasa de refresco para obtener la localización
	 */
	public void changeRefreshRate(int refreshRate){
		if (locationReader != null){
			locationReader.changeRefreshRate(refreshRate);
		}
	}
	/**
	 * Para de escuchar las actualizaciones de la localización del dispositivo
	 */
	public void stopLocationListening(){
		if (locationReader != null){
			locationReader.stopListeningLocation();
		}
	}
	/**
	 * Devuelve la última localización obtenida del GPS del dispositivo
	 * @return Última localización obtenida del GPS del dispositivo o null si no la hay
	 */
	public Location getLocation(){
		if (locationReader != null){
			return locationReader.getLocation();
		}
		return null;
	}
}
