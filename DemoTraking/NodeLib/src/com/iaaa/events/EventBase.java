package com.iaaa.events;

import android.content.Context;

/**
 * Clase base para las operaciones
 * @author Ricardo Pallás
 */
public abstract class EventBase implements Event {

	protected String EVT; //nombre del evento de la respuesta
	protected String[] p; //par�metros de la respuesta
	protected String h = null; //hash de la respuesta
	
	/**
	 * Ejecuta el evento y devuelve la respuesta en un String con formato JSON
	 * @return String en formato JSON con la respuesta generada al ejectutar el evento
	 */
	@Override
	public abstract String execute(Context context);

	
	
}
