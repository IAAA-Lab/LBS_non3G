package com.iaaa.sensors;

import java.util.ArrayList;



/**
 * Clase que sigue un patrón Singleton para acceder desde varios sitios
 * a la última localización obtenida del dispositivo.
 *
 * @author Ricardo Pallás
 *
 */
public class LastSensorRead {
	private static LastSensorRead INSTANCE = null;
	private ArrayList<SensorValue> lastPressureValues = new ArrayList<SensorValue>();
	private ArrayList<SensorValue> lastLightValues = new ArrayList<SensorValue>();
	private ArrayList<SensorValue> lastMagneticFieldValues = new ArrayList<SensorValue>();
	
	private LastSensorRead() {}

	/*
	 * Crea una instancia del objeto si no está creada
	 */
	private synchronized static void createInstance() {
		if (INSTANCE == null) { 
			INSTANCE = new LastSensorRead();
		}
	}
	/**
	 * Obtiene la instancia del objeto
	 * @return instancia del objeto
	 */
	public synchronized static LastSensorRead getInstance() {
		createInstance();
		return INSTANCE;
	}
	
	

	public synchronized ArrayList<SensorValue> getLastPressureValues() {
		return lastPressureValues;
	}


	public synchronized void addPressureValue(SensorValue pressure){
		lastPressureValues.add(pressure);
	}
	
	public synchronized ArrayList<SensorValue> getLastLightValues() {
		return lastLightValues;
	}


	public synchronized void addLightValue(SensorValue light){
		lastLightValues.add(light);
	}
	
	public synchronized ArrayList<SensorValue> getLastMagneticFieldValues() {
		return lastMagneticFieldValues;
	}
	
	public synchronized void addMagneticFieldValue (SensorValue magneticField){
		lastMagneticFieldValues.add(magneticField);
	}
}