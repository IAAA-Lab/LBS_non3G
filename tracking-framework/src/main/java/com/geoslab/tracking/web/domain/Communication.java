package com.geoslab.tracking.web.domain;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.PropertySource;
import org.springframework.context.support.PropertySourcesPlaceholderConfigurer;
import org.springframework.core.env.Environment;

import com.geoslab.tracking.persistence.domain.Node;
import com.geoslab.tracking.persistence.repository.NodeRepository;
import com.google.android.gcm.server.Constants;
import com.google.android.gcm.server.Message;
import com.google.android.gcm.server.Result;
import com.google.android.gcm.server.Sender;

/**
 * Clase que permite realizar las comunicaciones del servidor con los nodos
 * @author rafarn
 *
 */
//@PropertySource("classpath:bulksms.properties")
public class Communication {
	
	@Autowired
	private static NodeRepository nodeRepository;
	
	/*******************************/
	/**        BulkSMS             */
	/*******************************/
//	@Autowired
//    private static Environment environment;	// Variable que maneja los ficheros de propiedades
//	
//	@Bean
//    public static PropertySourcesPlaceholderConfigurer propertySourcesPlaceholderConfigurer() {
//       return new PropertySourcesPlaceholderConfigurer();
//    }
	
	/** Credenciales de BulkSMS */
	// Se definen en el pom.xml
	private static String USERNAME = "${bulksms.username}";
	private static String PASSWORD = "${bulksms.passwd}";
	
	/**
	 * Función que envía un mensaje SMS a un nodo
	 * @param phoneNumber: número de teléfono del nodo
	 * @param requestMessage: string con formato JSON que contiene la petición
	 */
	public static String sendSMS (String phoneNumber, String requestMessage) {
		return sendToBulkSMS(phoneNumber, requestMessage);
	}
	
	/**
	 * Función que gestiona el envío de SMS a través del servicio de BulkSMS
	 * @param phoneNumber: número de teléfono del nodo
	 * @param message: cadena de texto con el mensaje de texto a enviar
	 */
	private static String sendToBulkSMS (String phoneNumber, String message) {
//		http://bulksms.com.es:5567/eapi/submission/send_sms/2/2.0?
//		username=rafarn&password=GsL11382&message=Hello+world&msisdn=34675202044
		String URL = "http://bulksms.com.es:5567/eapi/submission/send_sms/2/2.0";
		String params = "username=" + USERNAME +
						"&password=" + PASSWORD +
						"&message=" + message +
						"&msisdn=" + phoneNumber +
						"&routing_group=2";
		try {
			return sendGet(URL, params);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	/*******************************/
	/**          GCM               */
	/*******************************/
	
//	private static final String gcmApiKey = "${gcm.apikey}";
	private static final String gcmApiKey = "AIzaSyBEO4GIZcBnV3dFUJvFOokV5vC-A_DQgkM";
	
	/**
	 * Función que envía una petición a través de GCM a un nodo
	 * @param GCMCloudId: identificador del nodo en GCM
	 * @param requestMessage: string con formato JSON que contiene la petición
	 */
	public static String sendGCM (String GCMCloudId, String requestMessage) {
		return sendToOneGCM(GCMCloudId, requestMessage);
	}
//	public static String sendGCM (List<String> GCMCloudId, String requestMessage) {
//		return sendToManyGCM(GCMCloudId, requestMessage);
//	}
	
	/**
	 * Función que gestiona el envío de un mensaje a un nodo a través de GCM
	 * @param GCMCloudId: identificador GCM del nodo
	 * @param JSONmessage: cadena de texto con el mensaje de texto a enviar
	 */
	private static String sendToOneGCM (String GCMCloudId, String jsonMessage) {
		
		Sender gcmSender = new Sender(gcmApiKey);
		
		Message.Builder messageBuilder = new Message.Builder();
		
		// Se añade el String con formato JSON a Data
		messageBuilder.addData("json", jsonMessage);
		
		Message message = messageBuilder.build();
		
		System.out.println(message.toString());
		
		try {
			Result result = gcmSender.send(message, GCMCloudId, 5);
			System.out.println("ENVIADO++++++++++++++++++++");
			//Parseo de la respuesta
			if (result.getMessageId() != null) {

				System.out.println("1++++++++++++++++++++");
				String canonicalRegId = result.getCanonicalRegistrationId();
				if (canonicalRegId != null) {

					System.out.println("2++++++++++++++++++++");
					// El dispositivo se ha registrado más de una vez, actualizar su cloudId con
					// el más reciente
					Node node = nodeRepository.findByCloudId(GCMCloudId);
					node.setCloudId(canonicalRegId);
					nodeRepository.save(node);
				}
			}
			else {

				System.out.println("ERROR++++++++++++++++++++");
				String error = result.getErrorCodeName();
				if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
					System.out.println("NOT_REGISTERED++++++++++++++++++++");
					// El dispositivo ya no está registrado en GCM, borrar su información
					Node node = nodeRepository.findByCloudId(GCMCloudId);
					node.setCloudId(null);
					nodeRepository.save(node);
				}
				else if (error.equals(Constants.ERROR_UNAVAILABLE)) {
					System.out.println("ERROR_UNAVAILABLE++++++++++++++++++++");
					// TODO retry
				}
				else {
					System.out.println("ELSE...++++++++++++++++++++");
					System.out.println(error);
					//throw new Exception("...");
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
	
	/**
	 * Función que gestiona el envío de un mensaje a varios nodos a través de GCM
	 * @param GCMCloudId: lista con los identificadores GCM de los nodos
	 * @param JSONmessage: cadena de texto con el mensaje de texto a enviar
	 */
//	private static String sendToManyGCM (List<String> GCMCloudIds, String jsonMessage) {
//		
//		Sender gcmSender = new Sender(gcmApiKey);
//		
//		Message.Builder messageBuilder = new Message.Builder();
////		messageBuilder.addData("type", type);
//		
//		Message message = messageBuilder.build();
//		
//		try {
//			MulticastResult result = gcmSender.send(message, GCMCloudIds, 5);
//			
//			//Parseo de la respuesta
//			if (result.getMessageId() != null) {
//				String canonicalRegId = result.getCanonicalRegistrationId();
//				if (canonicalRegId != null) {
//					// TODO same device has more than on registration ID: update database
//				}
//			}
//			else {
//				String error = result.getErrorCodeName();
//				if (error.equals(Constants.ERROR_NOT_REGISTERED)) {
//					// TODO application has been removed from device - unregister database
//					
//				}
//				else if (error.equals(Constants.ERROR_UNAVAILABLE)) {
//					// TODO retry
//				}
//				else {
//					//throw new Exception("...");
//				}
//			}
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
//		
//		return null;
//	}
	
	/*******************************/
	/**    Funciones axiliares     */
	/*******************************/
	
	/**
	 * Función que realiza una petición HTTP GET
	 * @param URL: cadena de texto con la URL a la que va dirigida la petición
	 * @param params: cadena de texto con los parámetros de la nueva petición
	 * @return string: cadena de texto con la respuesta a la petición
	 */
	private static String sendGet(String URL, String params) throws Exception {
		// Se forma la URL a la que irá dirigida la petición
		String url = URL + "?" + params;
 
		URL obj = new URL(url);
		HttpURLConnection con = (HttpURLConnection) obj.openConnection();
		con.setRequestMethod("GET");
		con.connect();
		
		// Se recibe la respuesta (si no espera la respuesta no funciona O_o)
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(con.getInputStream()));
		String inputLine;
		StringBuffer response = new StringBuffer();
 
		while ((inputLine = in.readLine()) != null) {
			response.append(inputLine);
		}
		in.close();
		
		return response.toString();
	}
}


//public class Communication{
//	public static String sendSMS (String phoneNumber, String requestMessage) {
//		return null;
//	}
//	public static String sendGCM (String GCMCloudId, String requestMessage) {
//		return null;
//	}
//}
