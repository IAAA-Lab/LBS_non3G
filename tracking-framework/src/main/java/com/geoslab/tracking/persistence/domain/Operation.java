package com.geoslab.tracking.persistence.domain;

import java.util.ArrayList;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import org.springframework.data.annotation.Transient;

/**
 * Clase abstracta que representa los eventos recibidos en el servidor
 * 
 * @author rafarn
 *
 */

@Entity
@Table(name = "OPERATIONS")
public class Operation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.AUTO)
	private long id;
	
	@ManyToOne
    @JoinColumn(name="node")
	private Node node;					// Nodo al que corresponde la operación
	
	private String operationName;		// Nombre de la operación
	private String operationType;		// Tipo de operación: Comando (CMD) o Evento (EVT)
	private String operationClass;		// Clase de operación: petición (Req) o respuesta (Res). Sólo tiene
										// sentido para los comandos
	
	private String[] p;					// Array de string con los parámetros de la operación
	
	private ArrayList<String[]> e;		// Array de array de string con la información de los endpoints en la 
										// operación DeviceGetInfo
	
	private long time;					// Hora de llegada de la operación
	
	private long seqId;					// Número de secuencia
	
//	private boolean answered = false;	// Flag que indica si la operación ha sido contestada
	
	@OneToOne
	@JoinColumn(name="operationRef")
	private Operation operationRef;		// Referencia a la operación con la que está relacionada. Si la
										// Operación es un Evento, no tiene correspondencia. Si la Operación
										// es un comando y se trata del comando de petición, se corresponde
										// con la respuesta, y viceversa, si se trata del comando de respuesta
										// se corresponde con la petición que la ha originado.
										// Además, hace de flag de contestada, si es null no hay respuesta
	
	// Versión del protocolo? TODO
	
	@Transient							// Annotation que evita que se mapee el atributo como columna de la BDD
	public static String commandTag = "CMD";	// Texto que identifica los comandos
	@Transient
	public static String eventTag = "EVT";		// Texto que identifica los eventos
	
	@Transient
	public static String requestTag = "Req";	// Texto que identifica las peticiones
	@Transient 
	public static String responseTag = "Res";	// Texto que identifica las respuestas
	
	/**********************/
	/**    Constructor    */
	/**********************/
	public Operation() {}
	
	// Constructor para las operaciones de clase CMD (Comando)
	public Operation(String operationName, String operationType, String operationClass, 
					String[] p, ArrayList<String[]> e, long seqId, Node node, long time) {
		this.operationName = operationName;
		this.operationType = operationType;
		this.operationClass = operationClass;
		this.p = p;
		this.e = e;
		this.seqId = seqId;
		this.node = node;
		this.time = time;
	}
	
	// Constructor para las operaciones de clase EVT (Evento)
	public Operation(String operationName, String operationType, String[] p, Node node, long time){
		this.operationName = operationName;
		this.operationType = operationType;
		this.p = p;
		this.node = node;
		this.time = time;
	}
	
	/**********************/
	/** Getters & Setters */
	/**********************/
	
	public long getId() {
		return id;
	}
	
	public Node getNode(){
		return node;
	}
	
	public void setNode(Node node){
		this.node = node;
	}
	
	public String getOperationName(){
		return operationName;
	}
	
	public void setOperationName(String operationName){
		this.operationName = operationName;
	}
	
	public String getOperationType(){
		return operationType;
	}
	
	public void setOperationType(String operationType){
		this.operationType = operationType;
	}
	
	public String getOperationClass(){
		return operationClass;
	}
	
	public void setOperationClass(String operationClass){
		this.operationClass = operationClass;
	}
	
	public String[] getP(){
		return p;
	}
	
	public void setP(String[] p){
		this.p = p;
	}
	
	public ArrayList<String[]> getE(){
		return e;
	}
	
	public void setE(ArrayList<String[]> e){
		this.e = e;
	}
	
	public long getTime(){
		return time;
	}
	
	public void setTime(long time){
		this.time = time;
	}
	
	public long getSeqId(){
		return seqId;
	}
	
	public void setSeqId(long seqId){
		this.seqId = seqId;
	}
	
	public Operation getOperationRef(){
		return operationRef;
	}
	
	public void setOperationRef(Operation operationRef){
		this.operationRef = operationRef;
	}

}
