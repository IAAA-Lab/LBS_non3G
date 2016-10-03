package com.geoslab.tracking.web.controller;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import com.geoslab.tracking.config.Application;
import com.geoslab.tracking.persistence.domain.Location;
import com.geoslab.tracking.persistence.domain.Node;
import com.geoslab.tracking.persistence.domain.Operation;
import com.geoslab.tracking.persistence.domain.Power;
import com.geoslab.tracking.persistence.domain.Sensor;
import com.geoslab.tracking.persistence.domain.SensorData;
import com.geoslab.tracking.persistence.repository.LocationRepository;
import com.geoslab.tracking.persistence.repository.NodeRepository;
import com.geoslab.tracking.persistence.repository.OperationRepository;
import com.geoslab.tracking.persistence.repository.PowerRepository;
import com.geoslab.tracking.persistence.repository.SensorDataRepository;
import com.geoslab.tracking.persistence.repository.SensorRepository;
import com.geoslab.tracking.security.UMAC;
import com.geoslab.tracking.web.domain.Request;
import com.geoslab.tracking.web.domain.Response;

@Controller
public class ClientController {
	
	@Autowired
	NodeRepository nodeRepository;
	
	@Autowired
	SensorRepository sensorRepository;
	
	@Autowired
	LocationRepository locationRepository;
	
	@Autowired
	PowerRepository powerRepository;
	
	@Autowired
	SensorDataRepository sensorDataRepository;
	
	@Autowired
	OperationRepository operationRepository;
	
	@PersistenceContext 
	@Autowired
	EntityManager entityManager;   
	
	/** Strings que identifican las operaciones */
	// Es un array en el cual la primera componente es el nombre de la petición 
	// que realiza el cliente y la segunda el nombre de la respuesta del servidor
	// al cliente
	
	// General operations
	private String[] GetCapabilities = {"GetCapabilities", "GetCapabilitiesRes"};
	private String[] GetNodes = {"GetNodes", "GetNodesRes"};
	//Device commands
	private String[] DeviceGetInfo = {"DeviceGetInfoReq", "DeviceGetInfoRes"};
	private String[] DevicePing = {"DevicePingReq", "DevicePingRes"};
	private String[] DeviceReset = {"DeviceResetReq", "DeviceResetRes"};
	private String[] DeviceGetMode = {"DeviceGetModeReq", "DeviceGetModeRes"};
	private String[] DeviceSetMode = {"DeviceSetModeReq", "DeviceACKRes"};
	private String[] DeviceGetSMSConfig = {"DeviceGetSMSConfigReq", "DeviceGetSMSConfigRes"};
	private String[] DeviceSetSMSConfig = {"DeviceSetSMSConfigReq", "DeviceACKRes"};
	private String[] DeviceGetHTTPConfig = {"DeviceGetHTTPConfigReq", "DeviceGetHTTPConfigRes"};
	private String[] DeviceSetHTTPConfig = {"DeviceSetHTTPConfigReq", "DeviceACKRes"};
	// Location commands
	private String[] LocationGetInfo = {"LocationGetInfoReq", "LocationGetInfoRes"};
	private String[] LocationGet = {"LocationGetReq", "LocationGetRes"};
	private String[] LocationGetAll = {"LocationGetAllReq", "LocationGetAllRes"};
	private String[] LocationGetSubset = {"LocationGetSubsetReq", "LocationGetSubsetRes"};
	private String[] LocationSet = {"LocationSetReq", "DeviceACKRes"};
	private String[] LocationGetRefreshRate = {"LocationGetRefreshRateReq", "LocationGetRefreshRateRes"};
	private String[] LocationSetRefreshRate = {"LocationSetRefreshRateReq", "DeviceACKRes"};
	private String[] LocationHistory = {"LocationHistoryReq", "LocationHistoryRes"};
	// Power commands
	private String[] PowerGetInfo = {"PowerGetInfoReq", "PowerGetInfoRes"};
	private String[] PowerGetLevel = {"PowerGetLevelReq", "PowerGetLevelRes"};
	private String[] PowerHistory = {"PowerHistoryReq", "PowerHistoryRes"};
	// Sensor commands
	private String[] SensorGetInfo = {"SensorGetInfoReq", "SensorInfoRes"};
	private String[] SensorGetData = {"SensorGetDataReq", "SensorDataRes"};
	private String[] SensorHistory = {"SensorHistoryReq", "SensorHistoryRes"};
	
	// Formater para la fecha
	SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy' 'HH:mm:ss");
	
	@RequestMapping("/client/")
	public String client(
    		@RequestParam(value="CMD", required=true) String CMD) {
		
		// General operations
		if (CMD.equals(GetCapabilities[0]))
			return "forward:/client/GetCapabilities/";
		else if (CMD.equals(GetNodes[0]))
			return "forward:/client/GetNodes/";
		//Device commands
		else if (CMD.equals(DeviceGetInfo[0]))
			return "forward:/client/DeviceGetInfo/";
		else if (CMD.equals(DevicePing[0]))
			return "forward:/client/DevicePing/";
		else if (CMD.equals(DeviceReset[0]))
			return "forward:/client/DeviceReset/";
		else if (CMD.equals(DeviceGetMode[0]))
			return "forward:/client/DeviceGetMode/";
		else if (CMD.equals(DeviceSetMode[0]))
			return "forward:/client/DeviceSetMode/";
		else if (CMD.equals(DeviceGetSMSConfig[0]))
			return "forward:/client/DeviceGetSMSConfig/";
		else if (CMD.equals(DeviceSetSMSConfig[0]))
			return "forward:/client/DeviceSetSMSConfig/";
		else if (CMD.equals(DeviceGetHTTPConfig[0]))
			return "forward:/client/DeviceGetHTTPConfig/";
		else if (CMD.equals(DeviceSetHTTPConfig[0]))
			return "forward:/client/DeviceSetHTTPConfig/";
		// Location commands
		else if (CMD.equals(LocationGetInfo[0]))
			return "forward:/client/LocationGetInfo/";
		else if (CMD.equals(LocationGet[0]))
			return "forward:/client/LocationGet/";
		else if (CMD.equals(LocationGetAll[0]))
			return "forward:/client/LocationGetAll/";
		else if (CMD.equals(LocationGetSubset[0]))
			return "forward:/client/LocationGetSubset/";
		else if (CMD.equals(LocationSet[0]))
			return "forward:/client/LocationSet/";
		else if (CMD.equals(LocationGetRefreshRate[0]))
			return "forward:/client/LocationGetRefreshRate/";
		else if (CMD.equals(LocationSetRefreshRate[0]))
			return "forward:/client/LocationSetRefreshRate/";
		else if (CMD.equals(LocationHistory[0]))
			return "forward:/client/LocationHistory/";
		// Power commands
		else if (CMD.equals(PowerGetInfo[0]))
			return "forward:/client/PowerGetInfo/";
		else if (CMD.equals(PowerGetLevel[0]))
			return "forward:/client/PowerGetLevel/";
		else if (CMD.equals(PowerHistory[0]))
			return "forward:/client/PowerHistory/";
		// Sensor commands
		else if (CMD.equals(SensorGetInfo[0]))
			return "forward:/client/SensorGetInfo/";
		else if (CMD.equals(SensorGetData[0]))
			return "forward:/client/SensorGetData/";
		else if (CMD.equals(SensorHistory[0]))
			return "forward:/client/SensorHistory/";
		else
			return "error";
    }

	@RequestMapping("/client/cmd/ListSemaphores")
	public @ResponseBody String listSemaphores(){
		return Application.semaphore.getContent();
	}

	@RequestMapping("/client/cmd/hashTest")
//	public @ResponseBody byte[] hashTest(
//	public @ResponseBody ByteBuffer hashTest(
	public @ResponseBody String hashTest(
			@RequestParam(value="m", required=true) String message){
		UMAC u = new UMAC();
		String final_result = u.createHash(message);
	    System.out.println("Final -> " + final_result);
		
		// TODO println de la codificación ASCII y UTF8
//		System.out.println("Resultado -> bytes: " + result_string + " | " +
//						   "String: " + result_real_string + " | " +
//						   "ASCII: " + new String(result_real_ASCII.array(), Charset.forName("ASCII")) + " | " +
//						   "UTF8: " + new String(result_real_UTF8.array(), Charset.forName("UTF-8")));
//		return Charset.forName("ASCII").encode(result_real_string);
		return final_result;
	}
	
	/***********************/
	/** General operations */
	/***********************/
	
	@Autowired
	private RequestMappingHandlerMapping requestMappingHandlerMapping;
	
//	
	@RequestMapping(value = "/client/GetCapabilities/", produces = "application/json")
	public @ResponseBody Resource getCapabilities(
//	public String getCapabilities(
			Model model,
			@RequestParam(value="v", required=false, defaultValue="default") String version,
			@RequestParam(value="h", required=true) String hash){
		ResourcePatternResolver resourceResolver = new PathMatchingResourcePatternResolver();

		// The path to the resource from the root of the JAR file
		Resource fileInJar = resourceResolver.getResource("/templates/capabilities.json");
		return fileInJar;
//		return new FileSystemResource("src/main/resources/templates/capabilities.json");
//		return new FileSystemResource("gsl-tracking-gps-0.4.0/templates/capabilities.json");
//		return "capabilities2";
	}
	
	@RequestMapping("/client/GetNodes/")
	public @ResponseBody Response getNodes(
			@RequestParam(value="stat", required=false) Set<String> status,
			@RequestParam(value="ff", required=false, defaultValue="false") boolean forceFetch,
			@RequestParam(value="h", required=true) String hash){
		
		List<Node> nodes = null;
		
		if (forceFetch){
			// Se encuesta los nodos directamente para obtener su estado
			List<Node> requestedNodes = null;
			if (status == null){
				// Si no se especifica status devuelve todos
				requestedNodes = (List<Node>) nodeRepository.findAll();
			}
			else {
				requestedNodes = (List<Node>) nodeRepository.findByStatusIn(status);
			}
			if (requestedNodes == null)
				return null;
			
			// Se guardan los identificadores de los nodos encuestados
			Set<Long> ids = new HashSet<Long>();  
			for (Node node : requestedNodes)
				ids.add(node.getNodeId());

			fetchNodes(requestedNodes, DevicePing[0], null);
			
			// Clears the cache to avoid inconsistency
			entityManager.clear();
	        
			nodes = nodeRepository.findByNodeIdIn(ids);
		}
		else{
			// El estado se obtiene a partir de la información guardada en la base de datos
			if (status == null){
				// Si no se especifica status devuelve todos
				nodes = (List<Node>) nodeRepository.findAll();
			}
			else {
				nodes = nodeRepository.findByStatusIn(status);
			}
		}
		if (nodes == null)
			return null;
		
		// Se prepara la respuesta
		// Se crea la lista de arrays para incluirla en la respuesta
		ArrayList<String[]> array = new ArrayList<String[]>();
		
		for (int i = 0; i < nodes.size(); i++){
			// Para cada nodo se añade un nuevo elemento en la lista
			Node node = nodes.get(i);
			array.add(new String[]{Long.toString(node.getNodeId()), node.getStatus()});
		}
		
		// Se comienza a crear el objeto que se devolverá como respuesta
		return new Response(GetNodes[1], array);
	}
	
	/***********************/
	/** Device commands    */
	/***********************/
	
	@RequestMapping("/client/DeviceGetInfo/")
	public @ResponseBody Response deviceGetInfo(
			@RequestParam(value="deviceID", required=true) long deviceId,
			@RequestParam(value="ff", required=false, defaultValue="false") boolean forceFetch,
			@RequestParam(value="h", required=true) String hash){

//		/client/?CMD=DeviceGetInfoReq&deviceID=321&h=11
		
		Node node = nodeRepository.findByNodeId(deviceId);
		
		if (node != null){
			if (forceFetch){
				// Se encuesta al nodo directamente para obtener su información
				return fetchNode(node, DeviceGetInfo[0], null);
			}
			else {
				// No hay ff, se devuelve la información conocida del nodo
				entityManager.clear();
				// Se obtienen los sensores conocidos del nodo
				List<Sensor> sensors = sensorRepository.findByNodeNodeIdAndStatus(node.getNodeId(), true);
				
				if (sensors.size() == 0){
					String[] params = 
						{Long.toString(node.getNodeId()), node.getDeviceVersion(), "0"};
					return new Response(DeviceGetInfo[1], params);
				}
				else{
					// Parámetros de la respuesta
					String[] params = 
						{Long.toString(node.getNodeId()), node.getDeviceVersion(), 
						 Integer.toString(sensors.size())};
					
					// Se crea la lista de arrays de sensores para incluirla en la respuesta
					ArrayList<String[]> array = new ArrayList<String[]>();
					for (Sensor sensor : sensors){
						array.add(new String[]{Long.toString(sensor.getSensorId()),
											   Long.toString(sensor.getType())});
					}
					return new Response(DeviceGetInfo[1], params, array);
				}
			}
		}
		else 
			return null;
	}
	
	@RequestMapping("/client/DevicePing/")
	public @ResponseBody Response devicePing(
			@RequestParam(value="deviceID", required=true) long deviceId,
			@RequestParam(value="ff", required=false, defaultValue="false") boolean forceFetch,
			@RequestParam(value="h", required=true) String hash){

		Node node = nodeRepository.findByNodeId(deviceId);
		
		if (node != null){
			if (forceFetch){
				// Se encuesta al nodo directamente para obtener su información
				return fetchNode(node, DevicePing[0], null);
			}
			else {
				// No hay ff, se devuelve la información conocida del nodo
				String[] params = {node.getStatus()};	// additionalInfo en principio vacío FIXME
				return new Response(DevicePing[1], params);
			}
		}
		else 
			return null;
	}
	
	@RequestMapping("/client/DeviceReset/")
	public @ResponseBody Response deviceReset(
			@RequestParam(value="deviceID", required=true) long deviceId,
			@RequestParam(value="h", required=true) String hash){
		
		Node node = nodeRepository.findByNodeId(deviceId);
		
		if (node != null)
			// Se encuesta al nodo directamente para hacer el reset
			return fetchNode(node, DeviceReset[0], null);
		else 
			return null;
	}
	
	@RequestMapping("/client/DeviceGetMode/")
	public @ResponseBody Response deviceGetMode(
			@RequestParam(value="deviceID", required=true) long deviceId,
			@RequestParam(value="ff", required=false, defaultValue="false") boolean forceFetch,
			@RequestParam(value="h", required=true) String hash){
		
		Node node = nodeRepository.findByNodeId(deviceId);
		
		if (node != null){
			if (forceFetch){
				// Se encuesta al nodo directamente para obtener su información
				return fetchNode(node, DeviceGetMode[0], null);
			}
			else {
				// No hay ff, se devuelve la información conocida del nodo
				String[] params = {Integer.toString(node.getMode())};
				return new Response(DeviceGetMode[1], params);
			}
		}
		else 
			return null;
	}
	
	@RequestMapping("/client/DeviceSetMode/")
	public @ResponseBody Response deviceSetMode(
			@RequestParam(value="deviceID", required=true) long deviceId,
			@RequestParam(value="mode", required=true) int mode,
			@RequestParam(value="h", required=true) String hash){
		
		Node node = nodeRepository.findByNodeId(deviceId);
		
		if (node != null){
			// Se encuesta al nodo directamente para establecer la configuración
			String[] params = {Integer.toString(mode)};
			Response response = fetchNode(node, DeviceSetMode[0], params);
			
			if (response.getCMD().equals(DeviceSetMode[1])){
				// La respuesta es ACK, la operación se ha completado correctamente
				// Se actualiza la información del nodo
				node.setMode(mode);
				nodeRepository.save(node);
			}
			
			return response;
		}
		else 
			return null;
	}
	
	@RequestMapping("/client/DeviceGetSMSConfig/")
	public @ResponseBody Response deviceGetSMSConfig(
			@RequestParam(value="deviceID", required=true) long deviceId,
			@RequestParam(value="ff", required=false, defaultValue="false") boolean forceFetch,
			@RequestParam(value="h", required=true) String hash){

		Node node = nodeRepository.findByNodeId(deviceId);
		
		if (node != null){
			if (forceFetch){
				// Se encuesta al nodo directamente para obtener su información
				return fetchNode(node, DeviceGetSMSConfig[0], null);
			}
			else {
				// No hay ff, se devuelve la información conocida del nodo
				String[] params = 
					{node.getServerPhoneNumber(), node.getSmsPollTime(), node.getSmsTransmitPeriod()};
				return new Response(DeviceGetSMSConfig[1], params);
			}
		}
		else 
			return null;
	}
	
	@RequestMapping("/client/DeviceSetSMSConfig/")
	public @ResponseBody Response deviceSetSMSConfig(
			@RequestParam(value="deviceID", required=true) long deviceId,
			@RequestParam(value="phoneNumber", required=true) String phoneNumber,
			@RequestParam(value="smsPollTime", required=true) String smsPollTime,
			@RequestParam(value="smsTransmitPeriod", required=true) String smsTransmitPeriod,
			@RequestParam(value="h", required=true) String hash){

		Node node = nodeRepository.findByNodeId(deviceId);
		
		if (node != null){
			// Se encuesta al nodo directamente para establecer la configuración
			String[] params = {phoneNumber, smsPollTime, smsTransmitPeriod};
			Response response = fetchNode(node, DeviceSetSMSConfig[0], params);
			
			if (response.getCMD().equals(DeviceSetSMSConfig[1])){
				// La respuesta es ACK, la operación se ha completado correctamente
				// Se actualiza la información del nodo
				node.setServerPhoneNumber(phoneNumber);
				node.setSmsPollTime(smsPollTime);
				node.setSmsTransmitPeriod(smsTransmitPeriod);
				nodeRepository.save(node);
			}
			
			return response;
		}
		else 
			return null;
	}
	
	@RequestMapping("/client/DeviceGetHTTPConfig/")
	public @ResponseBody Response deviceGetHTTPConfig(
			@RequestParam(value="deviceID", required=true) long deviceId,
			@RequestParam(value="ff", required=false, defaultValue="false") boolean forceFetch,
			@RequestParam(value="h", required=true) String hash){

		Node node = nodeRepository.findByNodeId(deviceId);
		
		if (node != null){
			if (forceFetch){
				// Se encuesta al nodo directamente para obtener su información
				return fetchNode(node, DeviceGetHTTPConfig[0], null);
			}
			else {
				// No hay ff, se devuelve la información conocida del nodo
				String[] params = {node.getServerIp(), node.getHttpTransmitPeriod()};
				return new Response(DeviceGetHTTPConfig[1], params);
			}
		}
		else 
			return null;
	}
	
	@RequestMapping("/client/DeviceSetHTTPConfig/")
	public @ResponseBody Response deviceSetHTTPConfig(
			@RequestParam(value="deviceID", required=true) long deviceId,
			@RequestParam(value="domainName", required=true) String domainName,
			@RequestParam(value="httpTransmitPeriod", required=true) String httpTransmitPeriod,
			@RequestParam(value="h", required=true) String hash){

		Node node = nodeRepository.findByNodeId(deviceId);
		
		if (node != null){
			// Se encuesta al nodo directamente para establecer la configuración
			String[] params = {domainName, httpTransmitPeriod};
			Response response = fetchNode(node, DeviceSetHTTPConfig[0], params);
			
			if (response.getCMD().equals(DeviceSetHTTPConfig[1])){
				// La respuesta es ACK, la operación se ha completado correctamente
				// Se actualiza la información del nodo
				node.setServerIp(domainName);
				node.setHttpTransmitPeriod(httpTransmitPeriod);
				nodeRepository.save(node);
			}
			
			return response;
		}
		else 
			return null;
	}
	
	/***********************/
	/** Location commands  */
	/***********************/
	
	@RequestMapping("/client/LocationGetInfo/")
	public @ResponseBody Response locationGetInfo(
			@RequestParam(value="deviceID", required=true) long deviceId,
			@RequestParam(value="ff", required=false, defaultValue="false") boolean forceFetch,
			@RequestParam(value="h", required=true) String hash){

		Node node = nodeRepository.findByNodeId(deviceId);
		
		if (node != null){
			if (forceFetch){
				// Se encuesta al nodo directamente para obtener su información
				return fetchNode(node, LocationGetInfo[0], null);
			}
			else {
				// No hay ff, se devuelve la información conocida del nodo
				String[] params = 
					{node.getLocationMode(), node.getLocationSysRef(),
					 node.getLocationDataType()};
				return new Response(LocationGetInfo[1], params);
			}
		}
		else 
			return null;
	}
	
	@RequestMapping("/client/LocationGet/")
	public @ResponseBody Response locationGet(
			@RequestParam(value="deviceID", required=true) long deviceId,
			@RequestParam(value="ff", required=false, defaultValue="false") boolean forceFetch,
			@RequestParam(value="h", required=true) String hash){

		Node node = nodeRepository.findByNodeId(deviceId);
		
		if (node != null){
			if (forceFetch){
				// Se encuesta al nodo directamente para obtener su información
				return fetchNode(node, LocationGet[0], null);
			}
			else {
				// No hay ff, se devuelve la última posición conocida del nodo
				
				// Se busca la posición en base al nodo y se limita a un sólo resultado con PageRequest
				List<Location> location = locationRepository.findByNodeOrderByTimeDesc(node, new PageRequest(0, 1));
				
				if (location.size() == 0)
					return null;
				
				String[] params = 
					{location.get(0).getLatitude().toString(),
				     location.get(0).getLongitude().toString(),
				     simpleDateFormat.format(new Timestamp(location.get(0).getTime()))};
				return new Response(LocationGet[1], params);
			}
		}
		else 
			return null;
	}
	
	@RequestMapping("/client/LocationGetAll/")
	public @ResponseBody Response locationGetAll(
			@RequestParam(value="stat", required=false) Set<String> status,
			@RequestParam(value="ff", required=false, defaultValue="false") boolean forceFetch,
			@RequestParam(value="h", required=true) String hash){

		if (forceFetch){
			// Se encuesta a los nodos directamente para obtener su información 
			// Se obtienen los nodos de los que se desea conocer la posición
			List<Node> nodes = (List<Node>) nodeRepository.findAll();
			return fetchNodes(nodes, LocationGet[0], null);
		}
		else {
			// No hay ff, se devuelve la última posición conocida de los nodos
			List<Location> locations = new ArrayList<Location>();

			if (status == null)
				// Si no se especifica status devuelve todos
				locations = locationRepository.findAllLastLocation();
			else {
				locations = locationRepository.findLastLocationByStatus(status);
			}
			
			if (locations.size() == 0)
				// Si no se han encontrado localizaciones devuelve null
				return null;
			
			// Se crea la lista de arrays para incluirla en la respuesta
			ArrayList<String[]> array = new ArrayList<String[]>();
			
			for (int i = 0; i < locations.size(); i++){
				// Para cada localización encontrada se añade un nuevo elemento en la lista
				Location location = locations.get(i);
				array.add(new String[]{Long.toString(location.getNode().getNodeId()), 
									   location.getLatitude().toString(),
									   location.getLongitude().toString(),
									   simpleDateFormat.format(new Timestamp(location.getTime()))});
			}
			// Se comienza a crear el objeto que se devolverá como respuesta
			return new Response(LocationGetAll[1], array);
		}
	}
	
	@RequestMapping("/client/LocationGetSubset/")
	public @ResponseBody Response locationGetSubset(
			@RequestParam(value="ids", required=true) Set<Long> ids, // long[]??
			@RequestParam(value="ff", required=false, defaultValue="false") boolean forceFetch,
			@RequestParam(value="h", required=true) String hash){

		if (forceFetch){
			// Se encuesta a los nodos directamente para obtener su información 
			// Se obtienen los nodos de los que se desea conocer la posición
			List<Node> nodes = nodeRepository.findByNodeIdIn(ids);
			return fetchNodes(nodes, LocationGet[0], null);
		}
		else {
			// No hay ff, se devuelve la última posición conocida de los nodos
			List<Location> locations = locationRepository.findLastLocationByIds(ids);

			if (locations.size() == 0)
				// Si no se han encontrado localizaciones devuelve null
				return null;
			
			// Se crea la lista de arrays para incluirla en la respuesta
			ArrayList<String[]> array = new ArrayList<String[]>();
			
			for (int i = 0; i < locations.size(); i++){
				// Para cada localización encontrada se añade un nuevo elemento en la lista
				Location location = locations.get(i);
				array.add(new String[]{Long.toString(location.getNode().getNodeId()), 
									   location.getLatitude().toString(), 
									   location.getLongitude().toString(),
									   simpleDateFormat.format(new Timestamp(location.getTime()))});
			}
			
			// Se comienza a crear el objeto que se devolverá como respuesta
			return new Response(LocationGetSubset[1], array);
		}
	}
	
	@RequestMapping("/client/LocationSet/")
	public @ResponseBody Response locationSet(
			@RequestParam(value="deviceID", required=true) long deviceId,
			@RequestParam(value="latitude", required=true) String latitude,
			@RequestParam(value="nsIndicator", required=false) String nsIndicator,
			@RequestParam(value="longitude", required=true) String longitude,
			@RequestParam(value="ewIndicator", required=false) String ewIndicator,
			@RequestParam(value="h", required=true) String hash){

		Node node = nodeRepository.findByNodeId(deviceId);
		
		if (node != null){
			// Se encuesta al nodo directamente para establecer la configuración
			int size = 0;
			if ((nsIndicator == null) || (ewIndicator == null))
				size = 2;
			else
				size = 4;
			
			String[] params = new String[size];
			if (size == 2){
				params[0] = latitude;
				params[1] = longitude;
			}
			else if (size == 4){
				params[0] = latitude;
				params[1] = nsIndicator;
				params[2] = longitude;
				params[3] = ewIndicator;
			}
				
			Response response = fetchNode(node, LocationSet[0], params);
			
			if (response.getCMD().equals(LocationSet[1])){
				// La respuesta es ACK, la operación se ha completado correctamente
				// Se añade una nueva medición de posición
				long time = System.currentTimeMillis();
				Location location = 
						new Location(latitude, nsIndicator, longitude, ewIndicator, node, time);
				
				if (locationRepository.save(location) == null)
					return null;
			}
			return response;
		}
		else 
			return null;
	}
	
	@RequestMapping("/client/LocationGetRefreshRate/")
	public @ResponseBody Response locationGetRefreshRate(
			@RequestParam(value="deviceID", required=true) long deviceId,
			@RequestParam(value="ff", required=false, defaultValue="false") boolean forceFetch,
			@RequestParam(value="h", required=true) String hash){
		
		Node node = nodeRepository.findByNodeId(deviceId);
		
		if (node != null){
			if (forceFetch){
				// Se encuesta al nodo directamente para obtener su información
				return fetchNode(node, LocationGetRefreshRate[0], null);
			}
			else {
				// No hay ff, se devuelve la información conocida del nodo
				String[] params = {node.getLocationRefreshRate()};
				return new Response(LocationGetRefreshRate[1], params);
			}
		}
		else 
			return null;
	}
	
	@RequestMapping("/client/LocationSetRefreshRate/")
	public @ResponseBody Response locationSetRefreshRate(
			@RequestParam(value="deviceID", required=true) long deviceId,
			@RequestParam(value="locationRefreshRate", required=true) String locationRefreshRate,
			@RequestParam(value="h", required=true) String hash){

		Node node = nodeRepository.findByNodeId(deviceId);
		
		if (node != null){
			// Se encuesta al nodo directamente para establecer la configuración
			String[] params = {locationRefreshRate};
			Response response = fetchNode(node, LocationSetRefreshRate[0], params);
			
			if (response.getCMD().equals(LocationSetRefreshRate[1])){
				// La respuesta es ACK, la operación se ha completado correctamente
				// Se actualiza la información del nodo
				node.setLocationRefreshRate(locationRefreshRate);
				nodeRepository.save(node);
			}
			return response;
		}
		else 
			return null;
	}
	
	@RequestMapping("/client/LocationHistory/")
	public @ResponseBody Response locationHistory(
			@RequestParam(value="deviceID", required=true) long deviceId,
			@RequestParam(value="maxResults", required=false, defaultValue="-1") int maxResults,
			@RequestParam(value="h", required=true) String hash){

		List<Location> locations = null;
		
		// Se obtiene el histórico de posiciones
		if (maxResults != -1)
			// Se cogen las n-últimas
			locations = 
					locationRepository.findByNodeNodeIdOrderByTimeDesc(deviceId, new PageRequest(0, maxResults));
		else
			// Se cogen todas
			locations = locationRepository.findByNodeNodeIdOrderByTimeDesc(deviceId);

		if (locations.size() == 0)
			// Si no se han encontrado localizaciones devuelve null
			return new Response(SensorHistory[1], new ArrayList<String[]>());
			
		// Se crea la lista de arrays para incluirla en la respuesta
		ArrayList<String[]> array = new ArrayList<String[]>();
		
		for (int i = 0; i < locations.size(); i++){
			// Para cada localización encontrada se añade un nuevo elemento en la lista
			Location location = locations.get(i);
			array.add(new String[]{Long.toString(deviceId), 
								   location.getLatitude().toString(),
								   location.getLongitude().toString(),
								   simpleDateFormat.format(new Timestamp(location.getTime()))});
		}
		
		// Se comienza a crear el objeto que se devolverá como respuesta
		return new Response(LocationHistory[1], array);
	}
	
	/***********************/
	/** Power commands     */
	/***********************/
	
	@RequestMapping("/client/PowerGetInfo/")
	public @ResponseBody Response powerGetInfo(
			@RequestParam(value="deviceID", required=true) long deviceId,
			@RequestParam(value="ff", required=false, defaultValue="false") boolean forceFetch,
			@RequestParam(value="h", required=true) String hash){
		
		Node node = nodeRepository.findByNodeId(deviceId);
		
		if (node != null){
			if (forceFetch){
				// Se encuesta al nodo directamente para obtener su información
				return fetchNode(node, PowerGetInfo[0], null);
			}
			else {
				// No hay ff, se devuelve la información conocida del nodo
				String[] params = 
					{node.getPowerMode(), node.getPowerDataUnits(), 
					 node.getPowerDataType(), Integer.toString(node.getPowerMinimum()), 
					 Integer.toString(node.getPowerMaximum())};
				return new Response(PowerGetInfo[1], params);
			}
		}
		else 
			return null;
	}
	
	@RequestMapping("/client/PowerGetLevel/")
	public @ResponseBody Response powerGetLevel(
			@RequestParam(value="deviceID", required=true) long deviceId,
			@RequestParam(value="ff", required=false, defaultValue="false") boolean forceFetch,
			@RequestParam(value="h", required=true) String hash){
		
		Node node = nodeRepository.findByNodeId(deviceId);
		
		if (node != null){
			if (forceFetch){
				// Se encuesta al nodo directamente para obtener su información
				return fetchNode(node, PowerGetLevel[0], null);
			}
			else {
				// No hay ff, se devuelve la información conocida del nodo
				
				// Se busca la última medición de power en base al nodo 
				// y se limita a un sólo resultado con PageRequest
				List<Power> power = powerRepository.findByNodeOrderByTimeDesc(node, new PageRequest(0, 1));
				
				if (power.size() == 0)
					return null;
				
				String[] params = {Integer.toString(power.get(0).getLevel())};
				return new Response(PowerGetLevel[1], params);
			}
		}
		else 
			return null;	
	}
	
	@RequestMapping("/client/PowerHistory/")
	public @ResponseBody Response powerHistory(
			@RequestParam(value="deviceID", required=true) long deviceId,
			@RequestParam(value="maxResults", required=false, defaultValue="-1") int maxResults,
			@RequestParam(value="h", required=true) String hash){

		List<Power> powers = null;
		
		// Se obtiene el histórico de mediciones de power
		if (maxResults != -1)
			// Se cogen las n-últimas
			powers = 
					powerRepository.findByNodeNodeIdOrderByTimeDesc(deviceId, new PageRequest(0, maxResults));
		else
			// Se cogen todas
			powers = powerRepository.findByNodeNodeIdOrderByTimeDesc(deviceId);

		if (powers.size() == 0)
			// Si no se han encontrado localizaciones devuelve null
			return new Response(SensorHistory[1], new ArrayList<String[]>());
			
		// Se crea la lista de arrays para incluirla en la respuesta
		ArrayList<String[]> array = new ArrayList<String[]>();
		
		for (int i = 0; i < powers.size(); i++){
			// Para cada localización encontrada se añade un nuevo elemento en la lista
			Power power = powers.get(i);
			array.add(new String[]{Long.toString(deviceId), 
								   Long.toString(power.getLevel()),
								   simpleDateFormat.format(new Timestamp(power.getTime()))});
		}
		
		// Se comienza a crear el objeto que se devolverá como respuesta
		return new Response(PowerHistory[1], array);
	}
	
	/***********************/
	/** Sensor commands    */
	/***********************/
	
	@RequestMapping("/client/SensorGetInfo/")
	public @ResponseBody Response sensorGetInfo(
			@RequestParam(value="deviceID", required=true) long deviceId,
			@RequestParam(value="sensorID", required=true) long sensorId,
			@RequestParam(value="ff", required=false, defaultValue="false") boolean forceFetch,
			@RequestParam(value="h", required=true) String hash){
		
		Node node = nodeRepository.findByNodeId(deviceId);
		Sensor sensor = sensorRepository.findByNodeNodeIdAndSensorId(deviceId, sensorId);
		
		if (node != null && sensor != null){
			// Se comprueba que existen el nodo y el sensor
			if (forceFetch){
				// Se encuesta al nodo directamente para obtener su información
				String[] params = {Long.toString(sensorId)};
				return fetchNode(node, SensorGetInfo[0], params);
			}
			else {
				// No hay ff, se devuelve la información conocida del nodo
				String[] params =
					{Long.toString(sensor.getSensorId()), Long.toString(sensor.getType()), sensor.getSensorDataUnits(),
					 sensor.getSensorDataType(), sensor.getSensorDataUncertainity(), sensor.getSensorDataLowerRange(),
					 sensor.getSensorDataUpperRange(), sensor.getSensorDataChannels(), sensor.getSensorDataPacketFormat()};
				return new Response(SensorGetInfo[1], params);
			}
		}
		else 
			return null;
	}
	
	@RequestMapping("/client/SensorGetData/")
	public @ResponseBody Response sensorGetData(
			@RequestParam(value="deviceID", required=true) long deviceId,
			@RequestParam(value="sensorID", required=true) long sensorId,
			@RequestParam(value="ff", required=false, defaultValue="false") boolean forceFetch,
			@RequestParam(value="h", required=true) String hash){
		
		Node node = nodeRepository.findByNodeId(deviceId);
		Sensor sensor = sensorRepository.findByNodeNodeIdAndSensorId(deviceId, sensorId);
		
		if (node != null && sensor != null){
			// Se comprueba que existen el nodo y el sensor
			if (forceFetch){
				// Se encuesta al nodo directamente para obtener su información
				String[] params = {Long.toString(sensorId)};
				return fetchNode(node, SensorGetData[0], params);
			}
			else {
				// No hay ff, se devuelve la información conocida del nodo
				
				// Se busca la última medición de power en base al nodo 
				// y se limita a un sólo resultado con PageRequest
				List<SensorData> sensorData = 
						sensorDataRepository.findByNodeAndSensorOrderByTimeDesc(node, sensor, new PageRequest(0, 1));
				
				if (sensorData.size() == 0)
					return null;
				
				float[] data = sensorData.get(0).getData();
				String[] params = new String[data.length + 1];
				for (int i = 0; i < data.length; i++)
					params[i] = Float.toString(data[i]);
				
				params[params.length - 1] = simpleDateFormat.format(new Timestamp(sensorData.get(0).getTime()));
				return new Response(SensorGetData[1], params);
			}
		}
		else 
			return null;
	}
	
	@RequestMapping("/client/SensorHistory/")
	public @ResponseBody Response sensorHistory(
			@RequestParam(value="deviceID", required=true) long deviceId,
			@RequestParam(value="sensorID", required=true) long sensorId,
			@RequestParam(value="maxResults", required=false, defaultValue="-1") int maxResults,
			@RequestParam(value="h", required=true) String hash){

		List<SensorData> sensorData = null;
		
		// Se obtiene el histórico de mediciones de power
		if (maxResults != -1)
			// Se cogen las n-últimas
			sensorData = 
					sensorDataRepository.findByNodeNodeIdAndSensorSensorIdOrderByTimeDesc(deviceId, sensorId, new PageRequest(0, maxResults));
		else
			// Se cogen todas
			sensorData = sensorDataRepository.findByNodeNodeIdAndSensorSensorIdOrderByTimeDesc(deviceId, sensorId);

		if (sensorData.size() == 0)
			// Si no se han encontrado mediciones devuelve null
			return new Response(SensorHistory[1], new ArrayList<String[]>());
			
		// Se crea la lista de arrays para incluirla en la respuesta
		ArrayList<String[]> array = new ArrayList<String[]>();
		
		for (int i = 0; i < sensorData.size(); i++){
			// Para cada medición de sensor encontrada se añade un nuevo elemento en la lista
			SensorData data = sensorData.get(i);
			// Se coge el campo data de la medición y se transforma a array de String
			float[] info = data.getData();
			String[] infoString = new String[info.length + 1];
			for (int j = 0; j < info.length; j++)
				infoString[j] = Float.toString(info[j]);
			// Se añade el tiempo
			infoString[infoString.length - 1] = simpleDateFormat.format(new Timestamp(data.getTime()));
			// Se añade al array de resultados
			array.add(infoString);
		}
		
		// Se comienza a crear el objeto que se devolverá como respuesta
		return new Response(SensorHistory[1], array);
	}
	
	/************************/
	/** Funciones axiliares */
	/************************/
	
	/**
	 * Función que registra una respuesta a un comando en la base de datos
	 * @param node: nodo al que corresponde la operación
	 * @param opName: nombre de la operación
	 * @param opType: tipo de la operación (comando o evento)
	 * @param opClass: clase de la operación (petición o respuesta)
	 * @param params: parámetros de información del comando
	 * @return long: devuelve el identificador de secuencia utilizado, -1 en caso de error
	 */
	private long logCommand(Node node, String opName, String opType, 
			   String opClass, String[] params){

		long time = System.currentTimeMillis();
		
		long lastSeqId = 
				operationRepository.findMaxSeqId();
		
		Operation operation;
		
		if (lastSeqId != 0)
			operation = new Operation(opName, opType, opClass, params, null, lastSeqId + 1, node, time);
		else
			operation = new Operation(opName, opType, opClass, params, null, 1, node, time);
		
		if (operationRepository.save(operation) != null)
			return operation.getSeqId();
		else
			return -1;
	}
	
	/**
	 * Función que realiza las operaciones necesarias para obtener la información de los nodos.
	 * Primero registra el comando en la base de datos y manda la petición al nodo. Espera a
	 * obtener respuesta del nodo y construye la respuesta con la información adquirida
	 * @param node: nodo al que se manda la petición
	 * @param operationName: nombre de la operación
	 * @param params: parámetros de la petición (null en caso de no tener parámetros)
	 * @return Response: objeto Response con la información procedente del nodo
	 */
	private Response fetchNode(Node node, String operationName, String[] params){
		// Se registra la operación
		long seqIdResult = 
				logCommand(node, operationName, Operation.commandTag, Operation.requestTag, params);
		
		if (seqIdResult == -1)
			// Ha ocurrido un error al registrarla
			return null;
		
		// Se crea la petición
		Request request = new Request(operationName, params, seqIdResult);
		
		// Se envía al nodo
		request.sendToNode(node);
		
		// Espera de respuesta
		Operation receivedAnswer = waitForTheAnswer(node, seqIdResult);
		
		// Se devuelve la respuesta recibida
		if (receivedAnswer.getOperationName().equals(DeviceGetInfo[1]) && receivedAnswer.getE().size() != 0){
			// Si es DeviceGetInfo se incluye la información de los sensores
			return new Response(receivedAnswer.getOperationName(), receivedAnswer.getP(), receivedAnswer.getE());
		}
		else {
			return new Response(receivedAnswer.getOperationName(), receivedAnswer.getP());
		}
	}
	
	private Response fetchNodes(List<Node> nodes, String operationName, String[] params){
		// HashMap que guarda pares de nodo - identificador de petición
		HashMap<Long, Node> seqId_Node = new HashMap<Long, Node>();
		
		// Se registran todas las operaciones
		for (Node node : nodes){
			long seqIdResult = 
					logCommand(node, operationName, Operation.commandTag, Operation.requestTag, params);
			if (seqIdResult == -1)
				// Ha ocurrido un error al registrarla
				return null;
			seqId_Node.put(seqIdResult, node);
		}
		
		// Se envía a los nodos y se crea el semáforo correspondiente
		for (Entry<Long, Node> entry : seqId_Node.entrySet()){
			// Crea el semáforo
			Application.semaphore.create(entry.getKey());
			// Se crea la petición que se enviará
			Request request = new Request(operationName, params, entry.getKey());
			// Manda la petición
//			request.sendToNode(node);
		}
		
		// Espera a las respuestas
		List<Operation> operations = waitForTheAnswers(seqId_Node);
		
		if (operations.size() == 0)
			// Si no se han recibido respuestas devuelve null
			return null;
		
		// Se crea la lista de arrays para incluirla en la respuesta
		ArrayList<String[]> array = new ArrayList<String[]>();
		
		for (int i = 0; i < operations.size(); i++){
			// Para cada localización encontrada se añade un nuevo elemento en la lista
			Operation operation = operations.get(i);
			
			// Se añade al principio del array el identificador de nodo
			String[] p = new String[operation.getP().length + 1];
			p[0] = Long.toString(operation.getNode().getNodeId());
			for (int j = 0; j < operation.getP().length; j++){
				p[j+1] = operation.getP()[j];
			}
			array.add(p);
		}
		
		// Se comienza a crear el objeto que se devolverá como respuesta
		return new Response(operationName, array);
	}
	
	/**
	 * Operación que realiza la espera de respuesta por parte del nodo
	 * @param node: nodo del que se espera respuesta
	 * @param seqId: identificador de secuencia que se espera lleve la respuesta
	 * @return Operation: 
	 */
	private Operation waitForTheAnswer(Node node, long seqId){
		boolean answerReceived = false;
		Operation receivedAnswer = null;
		while (!answerReceived){
			boolean timeoutReached = Application.semaphore.acquire(seqId);
			
			receivedAnswer = 
					operationRepository.findByNodeAndSeqIdAndOperationClass(node, seqId, Operation.responseTag);
			
			if (receivedAnswer != null)
				answerReceived = true;
			else if (timeoutReached) {
				// Se ha producido timeout
				// Se elimina el semáforo
				Application.semaphore.delete(seqId);
				// Se cambia el estado del nodo a dead
				updateNodeStatus(node, "dead");
				// Finalizar y devolver respuesta con timeout
				Operation operation = new Operation();
				operation.setOperationName("TIMEOUT");
				return operation;
			}
			// Se elimina el semáforo anterior para que al volver a iterar se cree uno nuevo
			Application.semaphore.delete(seqId);
		}
		return receivedAnswer;
	}
	
	private List<Operation> waitForTheAnswers(HashMap<Long, Node> seqId_Node){
		List<Operation> operations = new ArrayList<Operation>();
		for (Entry<Long, Node> entry : seqId_Node.entrySet()){
			
			long seqId = entry.getKey();
			Node node = entry.getValue();
			
			boolean answerReceived = false;
			boolean timeoutReached = false;
			Operation receivedAnswer = null;
			while (!answerReceived){
				if (!timeoutReached)
					timeoutReached = Application.semaphore.acquire(seqId);
				
				receivedAnswer = 
						operationRepository.findByNodeAndSeqIdAndOperationClass(node, seqId, Operation.responseTag);
				
				if (receivedAnswer != null)
					answerReceived = true;
				else if (timeoutReached){
					// Ha saltado el timeout, no hay respuesta del nodo
					receivedAnswer = new Operation(null, null, new String[]{"TIMEOUT"}, node, 0);
					answerReceived = true;
					// Se cambia el estado del nodo a dead
					updateNodeStatus(node, "dead");
				}
				// Se elimina el semáforo anterior para que al volver a iterar se cree uno nuevo
				Application.semaphore.delete(seqId);
			}
			operations.add(receivedAnswer);
		}
		return operations;
	}
	
	/**
	 * Actualiza el estado del nodo, pero sólo  si es necesario. Si el nodo está en 
	 * estado "active", sólo se cambia si el nuevo estado es "dead. Si el nodo está
	 * en estado "alive" o "dead", se cambia en cualquier caso.
	 * @param node: nodo que va a cambiar de estado
	 * @param status: nuevo estado ("active", "alive" o "dead")
	 */
	private void updateNodeStatus(Node node, String newStatus){
		String nodeStatus = node.getStatus();
		if (nodeStatus.equals("active")){
			// Se cambia sólo si el nuevo es "dead"
			if (newStatus.equals("dead")){
				node.setStatus(newStatus);
				nodeRepository.save(node);
			}
		}
		else if (!nodeStatus.equals(newStatus)){
			// Es "alive" o "dead" y no es el estado actual, se cambia
			node.setStatus(newStatus);
			nodeRepository.save(node);
		}
	}
}
