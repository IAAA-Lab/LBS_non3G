package com.iaaa.message;

/**
 * Excepción que es lanzada si se recibe un SMS más largo que 160 caracteres.
 * @author Ricardo Pallás
 *
 */
public class SmsTooLongException extends Exception {

	public SmsTooLongException(){
		
	}
	/**
	 * Constructor de la clase SmsTooLongException. Crea una instancia
	 * de la excepción y muestra el mensaje pasado como parámetro.
	 * @param message 
	 * El mensaje que se desea mostrar cuando se lanza la excepción
	 */
	public SmsTooLongException(String message){
		super(message);
	}
}
