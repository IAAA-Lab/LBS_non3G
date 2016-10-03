package com.iaaa.location;

import java.util.ArrayList;

import android.location.Location;

/**
 * Clase que sigue un patrón Singleton para acceder desde varios sitios
 * a la última localización obtenida del dispositivo.
 *
 * @author Ricardo Pallás
 *
 */
public class LastLocation {
	private static LastLocation INSTANCE = null;
	private Location lastLocation;
	private ArrayList<Location> locations = new ArrayList<Location>();
	
	private LastLocation() {}

	/*
	 * Crea una instancia del objeto si no está creada
	 */
	private synchronized static void createInstance() {
		if (INSTANCE == null) { 
			INSTANCE = new LastLocation();
		}
	}
	/**
	 * Obtiene la instancia del objeto
	 * @return instancia del objeto
	 */
	public synchronized static LastLocation getInstance() {
		createInstance();
		return INSTANCE;
	}
	/**
	 * Devuelve la última localización del dispositivo
	 * @return Última localización del dispositivo
	 */
	public synchronized Location getLocation() {
		return lastLocation;
	}

	/**
	 * Fija la última localización del dispositivo conocida
	 * @param location �ltima localización del dispositivo conocida
	 */
	public synchronized void setLocation(Location location){
		lastLocation = location;
		locations.add(location);
	}
	public synchronized ArrayList<Location> getLocations(){
		return locations;
	}
}