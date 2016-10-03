package com.geoslab.tracking.web.domain;

import java.io.File;
import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonGenerationException;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geoslab.tracking.persistence.domain.Node;
import com.geoslab.tracking.security.UMAC;

/**
 * Clase que representa las peticiones del servidor a los nodos.
 * @author rafarn
 *
 */
@JsonPropertyOrder({"CMD","p","h","s"})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class Request {
	
	/**	Atributos de la clase */
	// CMD o EVT deben tener un valor correcto, pero no los dos a la vez
	// Cadena de texto que representa el tipo de operación
	@JsonProperty("CMD")
	private String CMD;
	
	// Array de strings que contiene la información de la respuesta
	@JsonProperty("p")
	private String[] p;
	
	// Cadena de texto con el Hash
	@JsonProperty("h")
	private String h;
	
	// Número de secuencia de la respuesta
	@JsonProperty("s")
	private long s;
	
	/**********************/
	/**    Constructor    */
	/**********************/
	public Request() {}
	
	public Request(String operationName, String[] p, long s) {
		this.CMD = operationName;
		this.p = p;
		this.s = s;
		
		// Crea el hash
		UMAC u = new UMAC();
		String hash = u.createHash(generateJSON());
		this.h = hash;
	}
	
	/**********************/
	/** Getters & Setters */
	/**********************/
	
	@JsonIgnore public String getCMD(){
		return CMD;
	}
	
	@JsonIgnore public void setCMD(String CMD){
		this.CMD = CMD;
	}
	
	@JsonIgnore public String[] getP(){
		return p;
	}

	@JsonIgnore public void setP(String p[]){
		this.p = p;
	}
	
	@JsonIgnore public String getH(){
		return h;
	}
	
	@JsonIgnore public void setH(String h){
		this.h = h;
	}
	
	@JsonIgnore public long getS(){
		return s;
	}
	
	@JsonIgnore public void setS(long s){
		this.s = s;
	}
	
	/************************/
	/** Funciones axiliares */
	/************************/
	
	/**
	 * Función que realiza el envío de una petición a un nodo
	 * @param node: nodo al que se va a enviar la petición
	 */
	public void sendToNode(Node node){
		// Realiza la conversión del objeto Request a un string con formato JSON
		String jsonRequest = generateJSON();
		
		if (jsonRequest != null){
			// Elige el canal de comunicación
			if (node.getCloudId() != null){
				// Comunicación vía GCM
				String result = Communication.sendGCM(node.getCloudId(), jsonRequest);
				System.out.println(result);
			}
			else {
				// Communicación vía SMS
				String result = Communication.sendSMS(node.getPhoneNumber(), jsonRequest);
				System.out.println(result);
			}
		}
	}
	
	/**
	 * Transforma el objeto Request actual a un String con formato JSON
	 * @return String: cadena de texto con formato JSON
	 */
	private String generateJSON (){
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
}

