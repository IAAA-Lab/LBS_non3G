package com.iaaa.location;

import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;

/**
 * Clase que obtiene la localización del dispositivo a través del GPS.
 * La localización es actualizada dentro de un intervalo de tiempo fijado
 * en el constructor de la clase.
 * Se ofrece un método para obtener la última localización obtenida.
 * @author Ricardo Pallás
 *
 */
public class LocationReader {

	private Location lastLocation; //Variable para almacenar la última localización obtenida
	private DeviceLocationListener locationListener; //LocationListener para escuchar los cambios en la localización
	private LocationManager locationManager; //LocationManager para operar con localizaciones
	private final String DEBUG_TAG = "LocationReader";
	private boolean isGpsEnabled = false; // Flag que indica si está activado el GPS en el dispositivo.
	private LastLocation sharedLocation; //Objeto donde se guarda la última localización (SINGLETON)

	/**
	 * Contructor de la clase
	 * @param context Contexto de la aplicación
	 * @param refreshRate Intervalo en segundos para obtener la localización 
	 */
	public LocationReader(final Context context, int refreshRate){
		//Obtener instancia de LastLocation
		sharedLocation = LastLocation.getInstance();
		//Se obtiene el LocationManager para operar con localizaciones
		locationManager = (LocationManager)context.getSystemService(Context.LOCATION_SERVICE);
		//Crear el LocationListener
		locationListener = new DeviceLocationListener();
		//Comprobar si el GPS está activado
		try {
			isGpsEnabled = locationManager
					.isProviderEnabled(LocationManager.GPS_PROVIDER);
		} catch (Exception ex) {
			Log.d(DEBUG_TAG,"Error: GPS is null");
		}
		//Registrar listener del gps, si esta activo
		registerListener(refreshRate);
	}
	
	
	/**
	 * Registra el listener del GPS en caso de que está activo,
	 * en caso contrario informa de que no está activo.
	 * @param refreshRate Tasa de refresco de la localización en segundos
	 */
	private void registerListener(int refreshRate){
		if(isGpsEnabled){
			//Se convierte la tasa de refresco a milisegundos
			refreshRate = refreshRate * 1000;
			//Se registra el listener
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, refreshRate, 0, locationListener);
			Log.d(DEBUG_TAG,"GPS registered");
		}
		else {
			Log.d(DEBUG_TAG,"Error: GPS is not enabled");
		}
	}
	
	/**
	 * Cambia el intervalo en el cual se obtienen las actualizaciones de localizaci�n
	 * @param refresRate Tiempo en segundos transcurrido entre obtener una localizaci�n y otra.
	 */
	public void changeRefreshRate(int refreshRate){
		locationManager.removeUpdates(locationListener);
		registerListener(refreshRate);
	}

	/**
	 * Used to receive notifications from the Location Manager when they are sent. These methods are called when
	 * the Location Manager is registered with the Location Service and a state changes.
	 */
	public class DeviceLocationListener implements LocationListener {

		/*
		 * (non-Javadoc)
		 * @see android.location.LocationListener#onLocationChanged(android.location.Location)
		 * 
		 * Método llamado cuando cambia la localización del dispositivo
		 */
		public void onLocationChanged(Location location) {
			//Almacena la ultima localización en la variable lastLocation
			lastLocation = location;
			sharedLocation.setLocation(location);
			Log.d(DEBUG_TAG,"GPS location changed: " + location.getLatitude() + " " + location.getLongitude());

		}

		public void onProviderDisabled(String provider) {
			if(provider.equals(LocationManager.GPS_PROVIDER)){
				Log.d(DEBUG_TAG,"GPS has been disabled");
			}
		}

		public void onProviderEnabled(String provider) {
			if(provider.equals(LocationManager.GPS_PROVIDER)){
				Log.d(DEBUG_TAG,"GPS has been enabled");
			}
		}

		public void onStatusChanged(String provider, int status, Bundle extras) {
			//No se hace nada
		}
	}

	/**
	 * Para de escuchar actualizaciones en la localización
	 * del dispositivo. 
	 */
	public void stopListeningLocation(){
		locationManager.removeUpdates(locationListener);
	}

	/**
	 * Devuelve el objeto Location que representa
	 * la última localización del dispositivo obtenida.
	 * @return Última localización obtenida.
	 */
	public Location getLocation(){
		return lastLocation;
	}
}