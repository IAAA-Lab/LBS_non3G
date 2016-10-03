package com.iaaa.sensors;

import java.util.Date;

/**
 * Clase que representa la lectura del valor de un sensor.
 * Almacena el nombre del sensor, así como la medida numéricas y las unidades
 * de dicha medida.
 * @author Ricardo Pallás
 *
 */
public class SensorValue {

	private String sensorName; //Nombre del sensor o tipo de medida
	private float[] sensorValue; //medida(s) leída del sensor
	private String sensorValueUnits; //unidades de la medida
	private Date time;
	
	/**
	 * Crea un objeto SensorValue que representa una medida de un sensor.
	 * @param sensorName Nombre del sensor o tipo de medida leída
	 * @param sensorValue medida leída o medidas si el sensor tiene más de una
	 * @param sensorValueUnits unidades de la medida
	 */
	public SensorValue(String sensorName, float[] sensorValue, String sensorValueUnits){
		this.sensorName = sensorName;
		this.sensorValue = sensorValue;
		this.sensorValueUnits = sensorValueUnits;
		time = new Date();
	}
	/**
	 * Devuelve el nombre de la medida
	 * @return nombre de la medida
	 */
	public String getSensorName(){
		return sensorName;
	}
	/**
	 * Devuelve el valor de la medida leída
	 * @return valor de la medida leída
	 */
	public float[] getSensorValue(){
		return sensorValue;
	}
	/**
	 * Devuelve las unidades de la medida
	 * @return unidades de la medida
	 */
	public String getSensorValueUnits(){
		return sensorValueUnits;
	}
	
	/**
	 * Devuelve la fecha y hora en la cual la medida fue leída
	 * @return fecha y hora en la cual la medida fue leída
	 */
	public Date getSensorValueTime(){
		return time;
	}
	
}
