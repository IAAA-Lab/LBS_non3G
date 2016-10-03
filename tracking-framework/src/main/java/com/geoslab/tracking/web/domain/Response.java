package com.geoslab.tracking.web.domain;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.geoslab.tracking.persistence.domain.Location;
import com.geoslab.tracking.security.UMAC;

/**
 * Clase que representa las respuestas de los nodos al servidor y las
 * del servidor a los clientes.
 * @author rafarn
 *
 */
@JsonPropertyOrder({"CMD","EVT","p","a","e","h","s"})
@JsonInclude(value = JsonInclude.Include.NON_NULL)
public class Response {
	
	/**	Atributos de la clase */
	// CMD o EVT deben tener un valor correcto, pero no los dos a la vez
	// Cadena de texto que representa el tipo de operación
	@JsonProperty("CMD")
	private String CMD;
	// Cadena de texto que representa el evento
	@JsonProperty("EVT")
	private String EVT;
	
	// Array de strings que contiene la información de la respuesta
	@JsonProperty("p")
	private String[] p;
	
	// Array de array de strings que contiene múltiples respuestas
	@JsonProperty("a")
	private ArrayList<String[]> a;
	
	// Array de array de strings para la lista de endpoints de un nodo
	@JsonProperty("e")
	private ArrayList<String[]> e;
	
	// Cadena de texto con el Hash
	@JsonProperty("h")
	private String h;
	
	// Número de secuencia de la respuesta
	@JsonProperty("s")
	private String s;
	
	/** Strings que identifican los eventos */
	//Device events
	private String DeviceErrorEVT = "DeviceErrorEvent";
	public final static  String DeviceDescriptionEVT = "DeviceDescriptionEvent";
	// Location events
	public final static String LocationEVT = "LocationEvent";
	// Power events
	private String PowerLowEVT = "PowerLowEvent";
	// Sensor events
	private String SensorDataEVT = "SensorDataEvent";
	
	/** Strings que identifican las respuestas (Nodos offline) */
	// Common responses
	private String DeviceACK = "DeviceACKRes";
	private String DeviceError = "DeviceErrorRes";
	// Device responses
	private String DeviceGetInfo = "DeviceGetInfoRes";					// Respuesta a DeviceGetInfo
	private String DeviceGetMode = "DeviceModeRes";						// Respuesta a DeviceGetMode
	private String DeviceGetSMSConfig = "DeviceSMSConfigRes";			// Respuesta a DeviceGetSMSConfig
	private String DeviceGetHTTPConfig = "DeviceHTTPConfigRes";			// Respuesta a DeviceGetHTTPConfig
	// Location responses
	private String LocationGetInfo = "LocationInfoRes";					// Respuesta a LocationGetInfo
	private String LocationGet = "LocationRes";							// Respuesta a LocationGet
	private String LocationGetRefreshRate = "LocationRefreshRateRes";	// Respuesta a LocationGetRefreshRate
	// Power responses
	private String PowerGetInfo = "PowerInfoRes";						// Respuesta a PowerGetInfo
	private String PowerGetLevel = "PowerLevelRes";						// Respuesta a PowerGetLevel
	// Sensor responses
	private String SensorGetInfo = "SensorInfoRes";						// Respuesta a SensorGetInfo
	private String SensorGetData = "SensorDataRes";						// Respuesta a SensorGetData
	
	/** Nombres de los parámetros en el campo "p" para cada operación */
	//Device events
	private static String DeviceErrorParams[] = {"deviceID", "deviceErrorCode", "additionalInfo"};
	private static String DeviceDescriptionParams[] = {"deviceID", "deviceVersion", "deviceDescription"};
	// Location events  
	private static String LocationParams[] = {"deviceID","latitude", "nsIndicator", "longitude", "ewIndicator", "utcTime"};     
	// Power events     
	private static String PowerLowParams[] = {"deviceID","powerLevel"};     
	// Sensor events    
	private static String SensorDataParams[] = {"deviceID","sensorID", "sensorData", "utcTime"};
	
	/** Arrays con el nombre de los parámetros que incluye cada operación */
	// Common responses
	private static String DeviceACKResParams[] = {"commandSource"};
	private static String DeviceErrorResParams[] = {"commandSource", "deviceErrorCode", "additionalInfo"};
	// Device responses
	private static String DeviceGetInfoResParams[] = {"deviceID", "deviceVersion", "numEndpoints"};
	
	private static String DeviceModeResParams[] = {"mode"};
	private static String DeviceSMSConfigResParams[] = {"phoneNumber", "smsPollTime", "smsTransmitPeriod"};
	private static String DeviceHTTPConfigResParams[] = {"domainName", "httpTransmitPeriod"};
	// Location responses
	private static String LocationInfoResParams[] = {"locationMode", "locationSysRef", "locationDataType"};
	private static String LocationResParams[] = {"latitude", "nsIndicator", "longitude", "ewIndicator", "utcTime"};
	private static String LocationRefreshRateResParams[] = {"locationRefreshRate"};
	// Power responses
	private static String PowerInfoResParams[] = {"powerMode", "powerDataUnits", "powerDataType", "powerMinimum", "powerMaximum"};
	private static String PowerLevelResParams[] = {"powerLevel"};
	// Sensor responses
	private static String SensorInfoResParams[] = {"sensorDataUnits", "sensorDataType", "sensorDataUncertainity", 
		"sensorDataLowerRange", "sensorDataUpperRange", "sensorDataChannels", "sensorDataPacketFormat"};	
	private static String SensorDataResParams[] = {"utcTime","sensorData"};
	
	

	
	/**********************/
	/**    Constructor    */
	/**********************/
	public Response(String operationName, String[] params) {
		this.CMD = operationName;
		this.p = params;
		
		// Crea el hash
		UMAC u = new UMAC();
		String hash = u.createHash(generateJSON());
		this.h = hash;
	}
	
	public Response(String operationName, ArrayList<String[]> array) {
		this.CMD = operationName;
		this.a = array;
		
		// Crea el hash
		UMAC u = new UMAC();
		String hash = u.createHash(generateJSON());
		this.h = hash;
	}
	
	public Response(String operationName, String[] params, ArrayList<String[]> array){
		this.CMD = operationName;
		this.p = params;
		this.e = array;
		
		// Crea el hash
		UMAC u = new UMAC();
		String hash = u.createHash(generateJSON());
		this.h = hash;
	}
	
	/**
	 * Crea un objeto Response a partir de los parámetros de una petición HTTP
	 * @param parameters: parámetros de la petición en un hashMap con clave el
	 * 					  nombre del parámetro y valor el valor del parámetro.
	 */
	public Response(HashMap<String, String> parameters) {
		// Se asigna el nombre de la operación
		this.CMD = parameters.get("CMD");
		this.EVT = parameters.get("EVT");
		
		if (parameters.containsKey("s"))
			this.s = parameters.get("s");
		
		String[] param_names = mapOpNameToParamsArray();
		if (param_names != null){
			ArrayList<String> new_p = new ArrayList<String>();
			for (int i = 0; i < param_names.length; i++){
				String value = parameters.get(param_names[i]);
				if (value != null)
					new_p.add(value);
			}
			this.p = new_p.toArray(new String[new_p.size()]);
		}
		else if ((CMD != null) && (CMD.equals(SensorGetData))){
			ArrayList<String> new_p = new ArrayList<String>();
			// Se coge el parámetro de tiempo y se pone el primero
			for (int i = 0; i < SensorDataResParams.length; i++){
				String value = parameters.get(SensorDataResParams[i]);
				if (value != null)
					new_p.add(value);
			}
			// Se coge el parámetro sensorData y se parte su contenido en array usando la "," 
			// como caracter separador
			String data = parameters.get("sensorData");
			for (String d : data.split(",")){
				new_p.add(d);
			}
			this.p = new_p.toArray(new String[new_p.size()]);
		}
		else if ((EVT != null) && (EVT.equals(SensorDataEVT))){
			// Los dos primeros se añaden buscando sus nombres y el resto (sensorData) al final
			ArrayList<String> new_p = new ArrayList<String>();
			for (int i = 0; i < SensorDataParams.length; i++){
				String value = parameters.get(SensorDataParams[i]);
				if (value != null)
					new_p.add(value);
			}
			// Se crea sensorData del mismo modo
			String data = parameters.get("sensorData");
			for (String d : data.split(",")){
				new_p.add(d);
			}
			this.p = new_p.toArray(new String[new_p.size()]);
		}
	}
	
	public Response(){}
	
	/**********************/
	/** Getters & Setters */
	/**********************/
	
	@JsonIgnore public String getCMD(){
		return CMD;
	}
	
	@JsonIgnore public void setCMD(String CMD){
		this.CMD = CMD;
	}
	
	@JsonIgnore public String getEVT(){
		return EVT;
	}
	
	@JsonIgnore public void setEVT(String EVT){
		this.EVT = EVT;
	}
	
	@JsonIgnore public String[] getP(){
		return p;
	}

	@JsonIgnore public void setP(String p[]){
		this.p = p;
	}
	
	@JsonIgnore public ArrayList<String[]> getA(){
		return a;
	}
	
	@JsonIgnore public void setA(ArrayList<String[]> a){
		this.a = a;
	}
	
	@JsonIgnore public ArrayList<String[]> getE(){
		return e;
	}
	
	@JsonIgnore public void setE(ArrayList<String[]> e){
		this.e = e;
	}
	
	@JsonIgnore public String getH(){
		return h;
	}
	
	@JsonIgnore public void setH(String h){
		this.h = h;
	}
	
	@JsonIgnore public String getS(){
		return s;
	}
	
	@JsonIgnore public void setS(String s){
		this.s = s;
	}
	
	/************************/
	/** Funciones axiliares */
	/************************/
	
	/**
	 * Esta función transforma el array de String "p" de la clase Response al
	 * formato de parámetros de una URL (p1=v1&p2=v2..)
	 * @param response: objeto de la clase Response con la información de la
	 * petición que ha llegado al servidor
	 * @return String: cadena de texto con los parámetros en el formato URL
	 */
	public String responseToParams(){
		String url_params = "";
		
		// Añade el parámetro de operación
		if (CMD != null)
			url_params += "CMD=" + CMD;
		if (EVT != null)
			url_params += "EVT=" + EVT;
		
		// Añade los parámetros del campo "p"
		String[] param_names = mapOpNameToParamsArray();
		if (param_names != null) {
			for (int i = 0; i < p.length; i++){
			    if (!p[i].isEmpty()){
			        url_params += "&" + param_names[i] + "=" + p[i];
			    }
			}
		}
		else if ((CMD != null) && (CMD.equals(SensorGetData))){
			// El primer valor es el de tiempo
		    if (!p[0].isEmpty())
		        url_params += "&utcTime=" + p[0];
			// Se cogen todos los valores de p y se meten en forma de "vector" al parámetro sensorData
		    if (!p[1].isEmpty())
		        url_params += "&sensorData=" + p[1];
			for (int i = 2; i < p.length; i++){
			    if (!p[i].isEmpty())
			        url_params += "," + p[i];
			}
		}
		else if ((EVT != null) && (EVT.equals(SensorDataEVT))){
			// Los dos primeros parámetros se añaden a sus respectivos parámetros, y el resto a sensorData
			for (int i = 0; i < SensorDataParams.length; i++){
			    if (!p[i].isEmpty())
			        url_params += "&" + SensorDataParams[i] + "=" + p[i];
			}
			// se rellena sensorData
			url_params += "&sensorData=" + p[SensorDataParams.length];
			for (int i = SensorDataParams.length + 1; i < p.length; i++){
			    if (!p[i].isEmpty())
			        url_params += "," + p[i];
			}
		}
		else
			return null;
		
		// Añade el hash
		url_params += "&h=" + h;
		
		// Añade el número de secuencia si existe
		if (s != null)
			url_params += "&s=" + s;
		
		// Añade el array e si existe
		if (e != null)
			for (String[] endpoint : e){
				url_params += "&e[]=" + endpoint[0];
				for (int i = 1; i < endpoint.length; i++)
					url_params += "," + endpoint[i];
			}
		
		return url_params;
	}
	
	/**
	 * A partir del nombre de la operación devuelve el array con los nombres
	 * de parámetros correspondientes
	 * @param operation: nombre de la operación
	 * @return String[]: array con los nombres de los parámetros
	 */
	private String[] mapOpNameToParamsArray(){
	    if (EVT != null){
			if (EVT.equals(DeviceErrorEVT))
				return DeviceErrorParams;
			else if (EVT.equals(DeviceDescriptionEVT))
				return DeviceDescriptionParams;
			else if (EVT.equals(LocationEVT))
				return LocationParams;
			else if (EVT.equals(PowerLowEVT))
				return PowerLowParams;
			else if (EVT.equals(SensorDataEVT))
				return null;
		}
		else if (CMD != null){
			if (CMD.equals(DeviceACK))
				return DeviceACKResParams;
			else if (CMD.equals(DeviceError))
				return DeviceErrorResParams;
			else if (CMD.equals(DeviceGetInfo))
				return DeviceGetInfoResParams;
			else if (CMD.equals(DeviceGetMode))
				return DeviceModeResParams;
			else if (CMD.equals(DeviceGetSMSConfig))
				return DeviceSMSConfigResParams;
			else if (CMD.equals(DeviceGetHTTPConfig))
				return DeviceHTTPConfigResParams;
			else if (CMD.equals(LocationGetInfo))
				return LocationInfoResParams;
			else if (CMD.equals(LocationGet))
				return LocationResParams;
			else if (CMD.equals(LocationGetRefreshRate))
				return LocationRefreshRateResParams;
			else if (CMD.equals(PowerGetInfo))
				return PowerInfoResParams;
			else if (CMD.equals(PowerGetLevel))
				return PowerLevelResParams;
			else if (CMD.equals(SensorGetInfo))
				return SensorInfoResParams;
			else if (CMD.equals(SensorGetData))
				return null;
		}
		return null;
	}
	
	/**
	 * Transforma el objeto Response actual a un String con formato JSON
	 * @return String: cadena de texto con formato JSON
	 */
	public String generateJSON (){
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writeValueAsString(this);
		} catch (JsonProcessingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}

	@JsonIgnore
	public String getLatitude() {
        return p[1];
    }

    @JsonIgnore
    public String getNsIndicator() {
        return p[2];
    }

    @JsonIgnore
    public String getLongitude() {
        return p[3];
    }

    @JsonIgnore
    public String getEwIndicator() {
        return p[4];
    }
    


    @JsonIgnore
    public long getUtcTime() {
        // Parámetro en formato hhmmss.sss
        return Location.fromNmeaUtcToMilis(p[5]);
    }
    
    @JsonIgnore
    public String getUtcTimeParamValue() {
        return p[5];
    }

    @JsonIgnore
    public long getDeviceId() {
        return Long.parseLong(p[0]);
    }

    @JsonIgnore
    public String getDeviceVersion() {
        return p[1];
    }

    @JsonIgnore
    public String getDeviceDescription() {
        return p[2];
    }

    @JsonIgnore
    public String getCloudId() {
        // TODO Auto-generated method stub
        return null;
    }
}

