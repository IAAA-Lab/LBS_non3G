package com.iaaa.events;

import android.content.Context;

/**
 * Interfaz que deben implementar los eventos mandandos al servidor
 * @author Ricardo Pallas
 */
public interface Event {

		

	/**
	 * Ejecuta el evento necesario y devuelve la respuesta del evento en JSON
	 * 
	 * @return String en formato JSON con la respuesta generada al ejectutar el evento
	 */
	public abstract String execute(Context context);
}
