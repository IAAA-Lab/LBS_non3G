
package com.geoslab.tracking.web.controller;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

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

@Controller
public class NodeController {

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

    /** Strings que identifican los eventos */
    // Device events
    private String DeviceErrorEVT = "DeviceErrorEvent";
    private String DeviceDescriptionEVT = "DeviceDescriptionEvent";
    // Location events
    private String LocationEVT = "LocationEvent";
    // Power events
    private String PowerLowEVT = "PowerLowEvent";
    // Sensor events
    private String SensorDataEVT = "SensorDataEvent";

    /** Strings que identifican las respuestas */
    // Common responses
    private String DeviceACK = "DeviceACKRes";
    private String DeviceError = "DeviceErrorRes";
    // Device responses
    // Es un array en el cual la primera componente es el nombre de la respuesta
    // y
    // la segunda el nombre de la petición asociada
    private String[] DeviceGetInfo = { "DeviceGetInfoRes", "DeviceGetInfoReq" }; // Respuesta
                                                                                 // a
                                                                                 // DeviceGetInfo
    private String[] DeviceGetMode = { "DeviceModeRes", "DeviceGetModeReq" }; // Respuesta
                                                                              // a
                                                                              // DeviceGetMode
    private String[] DeviceGetSMSConfig = { "DeviceSMSConfigRes",
            "DeviceGetSMSConfigReq" }; // Respuesta a DeviceGetSMSConfig
    private String[] DeviceGetHTTPConfig = { "DeviceHTTPConfigRes",
            "DeviceGetHTTPConfigReq" };// Respuesta a DeviceGetHTTPConfig
    // Location responses
    private String[] LocationGetInfo = { "LocationInfoRes",
            "LocationGetInfoReq" }; // Respuesta a LocationGetInfo
    private String[] LocationGet = { "LocationRes", "LocationGetReq" }; // Respuesta
                                                                        // a
                                                                        // LocationGet
    private String[] LocationGetRefreshRate = { "LocationRefreshRateRes",
            "LocationGetRefreshRateReq" }; // Respuesta a LocationGetRefreshRate
    // Power responses
    private String[] PowerGetInfo = { "PowerInfoRes", "PowerGetInfoReq" }; // Respuesta
                                                                           // a
                                                                           // PowerGetInfo
    private String[] PowerGetLevel = { "PowerLevelRes", "PowerGetLevelReq" }; // Respuesta
                                                                              // a
                                                                              // PowerGetLevel
    // Sensor responses
    private String[] SensorGetInfo = { "SensorInfoRes", "SensorGetInfoReq" }; // Respuesta
                                                                              // a
                                                                              // SensorGetInfo
    private String[] SensorGetData = { "SensorDataRes", "SensorGetDataReq" }; // Respuesta
                                                                              // a
                                                                              // SensorGetData

    @RequestMapping("/node/")
    public String node(
            @RequestParam(value = "CMD", required = false, defaultValue = "default") String CMD,
            @RequestParam(value = "EVT", required = false, defaultValue = "default") String EVT) {
        // Se comprueba que haya al menos un parámetro de operación
        if (CMD.equals("default") && EVT.equals("default"))
            return "error";
        else {
            // Device events
            if (EVT.equals(DeviceErrorEVT))
                return "forward:/node/evt/DeviceError/";
            else if (EVT.equals(DeviceDescriptionEVT))
                return "forward:/node/evt/DeviceDescription/";
            // Location events
            else if (EVT.equals(LocationEVT))
                return "forward:/node/evt/Location/";
            // Power events
            else if (EVT.equals(PowerLowEVT))
                return "forward:/node/evt/PowerLow/";
            // Sensor events
            else if (EVT.equals(SensorDataEVT))
                return "forward:/node/evt/SensorData/";

            // Common responses
            else if (CMD.equals(DeviceACK))
                return "forward:/node/cmd/DeviceACK/";
            else if (CMD.equals(DeviceError))
                return "forward:/node/cmd/DeviceError/";
            // Device responses
            else if (CMD.equals(DeviceGetInfo[0]))
                return "forward:/node/cmd/DeviceGetInfo/";
            else if (CMD.equals(DeviceGetMode[0]))
                return "forward:/node/cmd/DeviceGetMode/";
            else if (CMD.equals(DeviceGetSMSConfig[0]))
                return "forward:/node/cmd/DeviceGetSMSConfig/";
            else if (CMD.equals(DeviceGetHTTPConfig[0]))
                return "forward:/node/cmd/DeviceGetHTTPConfig/";
            // Location responses
            else if (CMD.equals(LocationGetInfo[0]))
                return "forward:/node/cmd/LocationGetInfo/";
            else if (CMD.equals(LocationGet[0]))
                return "forward:/node/cmd/LocationGet/";
            else if (CMD.equals(LocationGetRefreshRate[0]))
                return "forward:/node/cmd/LocationGetRefreshRate/";
            // Power responses
            else if (CMD.equals(PowerGetInfo[0]))
                return "forward:/node/cmd/PowerGetInfo/";
            else if (CMD.equals(PowerGetLevel[0]))
                return "forward:/node/cmd/PowerGetLevel/";
            // Sensor responses
            else if (CMD.equals(SensorGetInfo[0]))
                return "forward:/node/cmd/SensorGetInfo/";
            else if (CMD.equals(SensorGetData[0]))
                return "forward:/node/cmd/SensorGetData/";
            else
                return "error";
        }
    }

    /***********************/
    /** Events */
    /***********************/

    @RequestMapping("/node/evt/DeviceError/")
    public String deviceErrorEVT(
            // public @ResponseBody Operation deviceErrorEVT(
            @RequestParam(value = "deviceID", required = false, defaultValue = "-1") long deviceId,
            @RequestParam(value = "deviceErrorCode", required = true) String deviceErrorCode,
            @RequestParam(value = "additionalInfo", required = true) String additionalInfo,
            @RequestParam(value = "h", required = true) String hash,
            @RequestParam(value = "phoneNumber", required = false, defaultValue = "default") String senderPhoneNumber) {

        // /node/?EVT=DeviceErrorEvent&deviceID=321&deviceErrorCode=3&additionalInfo=hola&h=XX

        Node node = null;

        if (!senderPhoneNumber.equals("default"))
            // Evento procedente de GSM
            // Obtener el nodo al que corresponde la medición
            node = nodeRepository.findByPhoneNumber(senderPhoneNumber);
        else
            // Evento procedente de HTTP
            // Obtener el nodo al que corresponde la medición
            node = nodeRepository.findByNodeId(deviceId);

        if (node != null) {
            // Se actualiza el estado del nodo
            updateNodeStatus(node, "alive");

            // Añadir el evento al log de operaciones
            String[] params = { deviceErrorCode, additionalInfo };

            if (logEvent(node, DeviceErrorEVT, Operation.eventTag, params))
                return "ok";
            else
                return "error";
        }
        else
            // Ningún nodo asociado a ese número de teléfono
            return "error";
    }

    @RequestMapping("/node/evt/DeviceDescription/")
    // public @ResponseBody Operation deviceDescriptionEVT(
    public String deviceDescriptionEVT(
            @RequestParam(value = "deviceID", required = true) long deviceId,
            @RequestParam(value = "deviceVersion", required = true) String deviceVersion,
            @RequestParam(value = "deviceDescription", required = true) String deviceDescription,
            @RequestParam(value = "cloudID", required = false, defaultValue = "default") String cloudId,
            @RequestParam(value = "h", required = true) String hash,
            @RequestParam(value = "phoneNumber", required = false, defaultValue = "default") String senderPhoneNumber) {

        // /node/?EVT=DeviceDescriptionEvent&deviceID=321&deviceVersion=1.0&deviceDescription=hola&h=123
        // Se comprueba si ya existe el nodo
        Node node = nodeRepository.findByNodeId(deviceId);
        if (node != null) {
            // Existe
            // El nodo se ha encendido, cambiar su estado a "alive" y actualizar
            // la información
            node.setStatus("alive");
            node.setDeviceVersion(deviceVersion);
            node.setDeviceDescription(deviceDescription);

            if (!cloudId.equals("default"))
                node.setCloudId(cloudId);
            if (!senderPhoneNumber.equals("default"))
                node.setPhoneNumber(senderPhoneNumber);

            if (nodeRepository.save(node) == null)
                return "error";
        }
        else {
            // No existe, añadirlo
            Node new_node = new Node();

            new_node.setNodeId(deviceId);
            new_node.setDeviceVersion(deviceVersion);
            new_node.setDeviceDescription(deviceDescription);

            if (!cloudId.equals("default"))
                new_node.setCloudId(cloudId);
            if (!senderPhoneNumber.equals("default"))
                new_node.setPhoneNumber(senderPhoneNumber);

            new_node.setStatus("alive");

            node = nodeRepository.save(new_node);

            if (node == null)
                return "error";
        }

        // Añadir el evento al log de operaciones
        String[] params = { Long.toString(deviceId), deviceVersion,
                deviceDescription, cloudId };

        if (logEvent(node, DeviceDescriptionEVT, Operation.eventTag, params))
            return "ok";
        else
            return "error";
    }

    @RequestMapping("/node/evt/Location/")
    public String locationEVT(
            // public @ResponseBody Operation locationEVT(
            @RequestParam(value = "deviceID", required = false, defaultValue = "-1") long deviceId,
            @RequestParam(value = "latitude", required = true) String latitude,
            @RequestParam(value = "nsIndicator", required = false) String nsIndicator,
            @RequestParam(value = "longitude", required = true) String longitude,
            @RequestParam(value = "ewIndicator", required = false) String ewIndicator,
            @RequestParam(value = "utcTime", required = true) String utcTime,
            @RequestParam(value = "h", required = true) String hash,
            @RequestParam(value = "phoneNumber", required = false) String senderPhoneNumber) {

        // /node/?EVT=LocationEvent&deviceID=321&latitude=0.43&longitude=43.2&utcTime=1337&h=123

        Node node = null;

        if (senderPhoneNumber != null)
            // Evento procedente de GSM
            // Obtener el nodo al que corresponde la medición
            node = nodeRepository.findByPhoneNumber(senderPhoneNumber);
        else
            // Evento procedente de HTTP
            // Obtener el nodo al que corresponde la medición
            node = nodeRepository.findByNodeId(deviceId);

        if (node != null) {
            // Se actualiza el estado del nodo
            updateNodeStatus(node, "active");

            // Se registra la medición
            try {
                Location location = new Location(latitude, nsIndicator,
                        longitude, ewIndicator, node, utcTime);
                if (locationRepository.save(location) == null)
                    return "error";
            }
            catch (Exception ex) {
                return "error";
            }

        }
        else
            // Ningún nodo asociado a ese número de teléfono
            return "error";

        // Añadir el evento al log de operaciones
        String[] params = { Long.toString(deviceId), latitude, nsIndicator,
                longitude, ewIndicator, utcTime };

        if (logEvent(node, LocationEVT, Operation.eventTag, params))
            return "ok";
        else
            return "error";

    }

    @RequestMapping("/node/evt/PowerLow/")
    public String powerLowEVT(
            // public @ResponseBody Operation powerLowEVT(
            @RequestParam(value = "deviceID", required = false, defaultValue = "-1") long deviceId,
            @RequestParam(value = "powerLevel", required = true) String powerLevel,
            @RequestParam(value = "h", required = true) String hash,
            @RequestParam(value = "phoneNumber", required = false, defaultValue = "default") String senderPhoneNumber) {

        // /node/?EVT=PowerLowEvent&deviceID=321&powerLevel=15&h=XX

        Node node = null;

        if (!senderPhoneNumber.equals("default"))
            // Evento procedente de GSM
            // Obtener el nodo al que corresponde la medición
            node = nodeRepository.findByPhoneNumber(senderPhoneNumber);
        else
            // Evento procedente de HTTP
            // Obtener el nodo al que corresponde la medición
            node = nodeRepository.findByNodeId(deviceId);

        if (node != null) {
            // Se actualiza el estado del nodo
            updateNodeStatus(node, "alive");

            // Añadir el evento al log de operaciones
            String[] params = { Long.toString(deviceId), powerLevel };

            if (logEvent(node, PowerLowEVT, Operation.eventTag, params))
                return "ok";
            else
                return "error";
        }
        else
            return "error";
    }

    @RequestMapping("/node/evt/SensorData/")
    public String sensorDataEVT(
            @RequestParam(value = "deviceID", required = false, defaultValue = "-1") long deviceId,
            @RequestParam(value = "sensorID", required = true) long sensorId,
            @RequestParam(value = "sensorData", required = true) float[] data,
            @RequestParam(value = "utcTime", required = true) long utcTime,
            @RequestParam(value = "h", required = true) String hash,
            @RequestParam(value = "phoneNumber", required = false) String senderPhoneNumber) {

        Node node = null;
        Sensor sensor = null;

        if (senderPhoneNumber != null) {
            // Evento procedente de GSM
            // Obtener el nodo al que corresponde la medición
            node = nodeRepository.findByPhoneNumber(senderPhoneNumber);
            if (node != null)
                // Se busca el sensor al que corresponde la medición
                sensor = sensorRepository.findByNodeNodeIdAndSensorId(
                        node.getNodeId(), sensorId);
        }
        else {
            // Evento procedente de HTTP
            // Obtener el nodo al que corresponde la medición
            node = nodeRepository.findByNodeId(deviceId);
            if (node != null)
                // Se busca el sensor al que corresponde la medición
                sensor = sensorRepository.findByNodeNodeIdAndSensorId(
                        node.getNodeId(), sensorId);
        }

        if (node != null && sensor != null) {
            // Se actualiza el estado del nodo
            updateNodeStatus(node, "active");

            sensor.setStatus(true);
            if (sensorRepository.save(sensor) == null)
                return "error";

            // Se registra la medición
            SensorData sensorData = new SensorData(data, sensor, node, utcTime);
            if (sensorDataRepository.save(sensorData) == null)
                return "error";
        }
        else {
            System.out.println("aquí");
            // Ningún nodo asociado a ese número de teléfono
            return "error";
        }

        // Crea el array de parámetros teniendo en cuenta que data tiene número
        // de datos variable
        String[] params = new String[3 + data.length];

        // Añade los parámetros
        params[0] = Long.toString(deviceId);
        params[1] = Long.toString(sensorId);
        for (int i = 0; i < data.length; i++)
            params[i + 2] = Float.toString(data[i]);
        params[params.length - 1] = Long.toString(utcTime);

        // Añadir el evento al log de operaciones
        if (logEvent(node, SensorDataEVT, Operation.eventTag, params))
            return "ok";
        else
            return "error";

    }

    /***********************/
    /** Responses */
    /***********************/

    @RequestMapping("/node/cmd/DeviceACK/")
    public String deviceACK(
            @RequestParam(value = "commandSource", required = true) String commandSource,
            @RequestParam(value = "h", required = true) String hash,
            @RequestParam(value = "s", required = false, defaultValue = "-1") long seqId,
            @RequestParam(value = "phoneNumber", required = false, defaultValue = "default") String senderPhoneNumber) {
        // Busca el nodo del que proviene la respuesta
        Node node = findNodeByPhoneNumberOrSeqId(senderPhoneNumber, seqId);

        if (node != null) {
            // Se actualiza el estado del nodo
            updateNodeStatus(node, "alive");

            // Se añade el comando al log de operaciones
            String[] params = { commandSource };

            if (logCommand(node, DeviceACK, commandSource,
                    Operation.commandTag, Operation.responseTag, params, null,
                    seqId)) {
                // Respuesta almacenada, liberar el semáforo
                Application.semaphore.release(seqId);
                return "ok";
            }
            else
                return "error";
        }
        else
            // El nodo no existe
            return "error";
    }

    @RequestMapping("/node/cmd/DeviceError/")
    public String deviceError(
            @RequestParam(value = "commandSource", required = true) String commandSource,
            @RequestParam(value = "deviceErrorCode", required = true) String deviceErrorCode,
            @RequestParam(value = "additionalInfo", required = true) String additionalInfo,
            @RequestParam(value = "h", required = true) String hash,
            @RequestParam(value = "s", required = false, defaultValue = "-1") long seqId,
            @RequestParam(value = "phoneNumber", required = false, defaultValue = "default") String senderPhoneNumber) {

        // /node/?CMD=DeviceErrorRes&commandSource=asd&deviceErrorCode=1&additionalInfo=asdasd&h=XX&s=123

        // Busca el nodo del que proviene la respuesta
        Node node = findNodeByPhoneNumberOrSeqId(senderPhoneNumber, seqId);

        if (node != null) {
            // Se actualiza el estado del nodo
            updateNodeStatus(node, "alive");

            // Se añade el comando al log de operaciones
            String[] params = { commandSource, deviceErrorCode, additionalInfo };

            if (logCommand(node, DeviceError, commandSource,
                    Operation.commandTag, Operation.responseTag, params, null,
                    seqId)) {
                // Respuesta almacenada, liberar el semáforo
                Application.semaphore.release(seqId);
                return "ok";
            }
            else
                return "error";
        }
        else
            // El nodo no existe
            return "error";
    }

    @RequestMapping("/node/cmd/DeviceGetInfo/")
    public String deviceGetInfo(
            @RequestParam(value = "deviceID", required = true) long deviceId,
            @RequestParam(value = "deviceVersion", required = true) String deviceVersion,
            @RequestParam(value = "numEndpoints", required = true) long numEndpoints,
            @RequestParam(value = "e[]", required = false) String[][] endpoints,
            @RequestParam(value = "h", required = true) String hash,
            @RequestParam(value = "s", required = false, defaultValue = "-1") long seqId,
            @RequestParam(value = "phoneNumber", required = false, defaultValue = "default") String senderPhoneNumber,
            HttpServletRequest request) {

        // /node/?CMD=DeviceGetInfoRes&deviceID=321&deviceVersion=1.0&h=XX&phoneNumber=12341234
        // /node/?CMD=DeviceGetInfoRes&deviceID=321&deviceVersion=1.0&h=XX&s=1

        // for (int i = 0; i < endpoints.length; i++){
        // System.out.println("i:" + i);
        // String[] comp = endpoints[i];
        // for (int j = 0; j < comp.length; j++)
        // System.out.println("j:" + j + " = " + comp[j]);
        // }

        // Busca el nodo del que proviene la respuesta
        Node node = findNodeByPhoneNumberOrSeqId(senderPhoneNumber, seqId);
        if (node != null) {
            // Se actualiza el estado del nodo
            updateNodeStatus(node, "alive");

            // El nodo existe, actualizar su información
            node.setNodeId(deviceId);
            node.setDeviceVersion(deviceVersion);

            if (nodeRepository.save(node) == null) {
                return "error";
            }

            // Se marcan todos los sensores como no activos
            List<Sensor> sensors = (List<Sensor>) sensorRepository.findAll();
            for (Sensor sensor : sensors) {
                sensor.setStatus(false);
                sensorRepository.save(sensor);
            }

            ArrayList<String[]> e = new ArrayList<String[]>();
            if (endpoints != null) {
                // Hay endpoints, se añaden los sensores para el nodo
                if (numEndpoints == 1) {
                    // Sólo un sensor
                    // Se comprueba si ya existía
                    Sensor previousSensor = sensorRepository
                            .findByNodeAndSensorId(node,
                                    Long.valueOf(endpoints[0][0]));
                    if (previousSensor == null) {
                        // No existe, se añade
                        Sensor sensor = new Sensor(
                                Long.valueOf(endpoints[0][0]),
                                Integer.valueOf(endpoints[1][0]), node, true);
                        if (sensorRepository.save(sensor) == null)
                            return "error";
                    }
                    else {
                        previousSensor.setStatus(true);
                        previousSensor.setType(Integer
                                .valueOf(endpoints[1][0]));
                        if (sensorRepository.save(previousSensor) == null)
                            return "error";
                    }

                    e.add(new String[] { endpoints[0][0], endpoints[1][0] });
                }
                else {
                    // Varios sensores
                    for (int i = 0; i < endpoints.length; i++) {
                        Sensor previousSensor = sensorRepository
                                .findByNodeAndSensorId(node,
                                        Long.valueOf(endpoints[i][0]));
                        if (previousSensor == null) {
                            // No existe, se añade
                            Sensor sensor = new Sensor(
                                    Long.valueOf(endpoints[i][0]),
                                    Integer.valueOf(endpoints[i][1]), node,
                                    true);
                            if (sensorRepository.save(sensor) == null)
                                return "error";
                        }
                        else {
                            previousSensor.setStatus(true);
                            previousSensor.setType(Integer
                                    .valueOf(endpoints[i][1]));
                            if (sensorRepository.save(previousSensor) == null)
                                return "error";
                        }
                        e.add(new String[] { endpoints[i][0], endpoints[i][1] });
                    }
                }
            }

            // Se añade el comando al log de operaciones
            String[] params = { Long.toString(deviceId), deviceVersion,
                    Long.toString(numEndpoints) };

            if (logCommand(node, DeviceGetInfo[0], DeviceGetInfo[1],
                    Operation.commandTag, Operation.responseTag, params, e,
                    seqId)) {
                // Respuesta almacenada, liberar el semáforo
                Application.semaphore.release(seqId);
                return "ok";
            }
            else
                // Error almacenando la respuesta
                return "error";
        }
        else
            // El nodo no existe
            return "error";
    }

    @RequestMapping("/node/cmd/DeviceGetMode/")
    public String deviceGetMode(
            @RequestParam(value = "mode", required = true) int mode,
            @RequestParam(value = "h", required = true) String hash,
            @RequestParam(value = "s", required = false, defaultValue = "-1") long seqId,
            @RequestParam(value = "phoneNumber", required = false, defaultValue = "default") String senderPhoneNumber) {

        // /client/?CMD=DeviceGetModeReq&deviceID=1&s=1
        // /node/?CMD=DeviceGetModeRes&mode=1&h=xx&s=1

        // Busca el nodo del que proviene la respuesta
        Node node = findNodeByPhoneNumberOrSeqId(senderPhoneNumber, seqId);

        if (node != null) {
            // El nodo existe, actualizar su información
            node.setMode(mode);

            // Actualizar el status en base al modo
            if (mode == 0)
                node.setStatus("alive");
            else
                node.setStatus("active");

            if (nodeRepository.save(node) == null)
                return "error";

            // Se añade el comando al log de operaciones
            String[] params = { Integer.toString(mode) };

            if (logCommand(node, DeviceGetMode[0], DeviceGetMode[1],
                    Operation.commandTag, Operation.responseTag, params, null,
                    seqId)) {
                // Respuesta almacenada, liberar el semáforo
                Application.semaphore.release(seqId);
                return "ok";
            }
            else
                return "error";
        }
        else
            // El nodo no existe
            return "error";
    }

    @RequestMapping("/node/cmd/DeviceGetSMSConfig/")
    public String deviceGetSMSConfig(
            @RequestParam(value = "phoneNumber", required = true) String serverPhoneNumber,
            @RequestParam(value = "smsPollTime", required = true) String smsPollTime,
            @RequestParam(value = "smsTransmitPeriod", required = true) String smsTransmitPeriod,
            @RequestParam(value = "h", required = true) String hash,
            @RequestParam(value = "s", required = false, defaultValue = "-1") long seqId,
            @RequestParam(value = "phoneNumber", required = false, defaultValue = "default") String senderPhoneNumber) {

        // Busca el nodo del que proviene la respuesta
        Node node = findNodeByPhoneNumberOrSeqId(senderPhoneNumber, seqId);

        if (node != null) {
            // Se actualiza el estado del nodo
            updateNodeStatus(node, "alive");

            // El nodo existe, actualizar su información
            node.setServerPhoneNumber(serverPhoneNumber);
            node.setSmsPollTime(smsPollTime);
            node.setSmsTransmitPeriod(smsTransmitPeriod);

            if (nodeRepository.save(node) == null)
                return "error";

            // Se añade el comando al log de operaciones
            String[] params = { serverPhoneNumber, smsPollTime,
                    smsTransmitPeriod };

            if (logCommand(node, DeviceGetSMSConfig[0], DeviceGetSMSConfig[1],
                    Operation.commandTag, Operation.responseTag, params, null,
                    seqId)) {
                // Respuesta almacenada, liberar el semáforo
                Application.semaphore.release(seqId);
                return "ok";
            }
            else
                return "error";
        }
        else
            // El nodo no existe
            return "error";
    }

    @RequestMapping("/node/cmd/DeviceGetHTTPConfig/")
    public String deviceGetHTTPConfig(
            @RequestParam(value = "domainName", required = true) String domainName,
            @RequestParam(value = "httpTransmitPeriod", required = true) String httpTransmitPeriod,
            @RequestParam(value = "h", required = true) String hash,
            @RequestParam(value = "s", required = false, defaultValue = "-1") long seqId,
            @RequestParam(value = "phoneNumber", required = false, defaultValue = "default") String senderPhoneNumber) {

        // Busca el nodo del que proviene la respuesta
        Node node = findNodeByPhoneNumberOrSeqId(senderPhoneNumber, seqId);

        if (node != null) {
            // Se actualiza el estado del nodo
            updateNodeStatus(node, "alive");

            // El nodo existe, actualizar su información
            node.setServerIp(domainName);
            node.setHttpTransmitPeriod(httpTransmitPeriod);

            if (nodeRepository.save(node) == null)
                return "error";

            // Se añade el comando al log de operaciones
            String[] params = { domainName, httpTransmitPeriod };

            if (logCommand(node, DeviceGetHTTPConfig[0],
                    DeviceGetHTTPConfig[1], Operation.commandTag,
                    Operation.responseTag, params, null, seqId)) {
                // Respuesta almacenada, liberar el semáforo
                Application.semaphore.release(seqId);
                return "ok";
            }
            else
                return "error";
        }
        else
            // El nodo no existe
            return "error";
    }

    @RequestMapping("/node/cmd/LocationGetInfo/")
    public String locationGetInfo(
            @RequestParam(value = "locationMode", required = true) String locationMode,
            @RequestParam(value = "locationSysRef", required = false, defaultValue = "WGS84") String locationSysRef,
            @RequestParam(value = "locationDataType", required = true) String locationDataType,
            @RequestParam(value = "h", required = true) String hash,
            @RequestParam(value = "s", required = false, defaultValue = "-1") long seqId,
            @RequestParam(value = "phoneNumber", required = false, defaultValue = "default") String senderPhoneNumber) {

        // Busca el nodo del que proviene la respuesta
        Node node = findNodeByPhoneNumberOrSeqId(senderPhoneNumber, seqId);

        if (node != null) {
            // Se actualiza el estado del nodo
            updateNodeStatus(node, "alive");

            // El nodo existe, actualizar su información
            node.setLocationMode(locationMode);
            node.setLocationSysRef(locationSysRef);
            node.setLocationDataType(locationDataType);

            if (nodeRepository.save(node) == null)
                return "error";

            // Se añade el comando al log de operaciones
            String[] params = { locationMode, locationSysRef, locationDataType };

            if (logCommand(node, LocationGetInfo[0], LocationGetInfo[1],
                    Operation.commandTag, Operation.responseTag, params, null,
                    seqId)) {
                // Respuesta almacenada, liberar el semáforo
                Application.semaphore.release(seqId);
                return "ok";
            }
            else
                return "error";
        }
        else
            // El nodo no existe
            return "error";
    }

    @RequestMapping("/node/cmd/LocationGet/")
    public String locationGet(
            @RequestParam(value = "latitude", required = true) String latitude,
            @RequestParam(value = "nsIndicator", required = false) String nsIndicator,
            @RequestParam(value = "longitude", required = true) String longitude,
            @RequestParam(value = "ewIndicator", required = false) String ewIndicator,
            @RequestParam(value = "utcTime", required = true) long utcTime,
            @RequestParam(value = "h", required = true) String hash,
            @RequestParam(value = "s", required = false, defaultValue = "-1") long seqId,
            @RequestParam(value = "phoneNumber", required = false) String senderPhoneNumber) {

        // Busca el nodo del que proviene la respuesta
        Node node = findNodeByPhoneNumberOrSeqId(senderPhoneNumber, seqId);

        if (node != null) {
            // Se actualiza el estado del nodo
            updateNodeStatus(node, "alive");

            // Se registra la medición
            Location location = new Location(latitude, nsIndicator, longitude,
                    ewIndicator, node, utcTime);

            if (locationRepository.save(location) == null)
                return "error";

            // Se añade el comando al log de operaciones
            String[] params = { latitude, nsIndicator, longitude, ewIndicator,
                    Long.toString(utcTime) };

            if (logCommand(node, LocationGet[0], LocationGet[1],
                    Operation.commandTag, Operation.responseTag, params, null,
                    seqId)) {
                // Respuesta almacenada, liberar el semáforo
                Application.semaphore.release(seqId);
                return "ok";
            }
            else
                return "error";
        }
        else
            // El nodo no existe
            return "error";
    }

    @RequestMapping("/node/cmd/LocationGetRefreshRate/")
    public String locationGetRefreshRate(
            @RequestParam(value = "locationRefreshRate", required = true) String locationRefreshRate,
            @RequestParam(value = "h", required = true) String hash,
            @RequestParam(value = "s", required = false, defaultValue = "-1") long seqId,
            @RequestParam(value = "phoneNumber", required = false, defaultValue = "default") String senderPhoneNumber) {

        // Busca el nodo del que proviene la respuesta
        Node node = findNodeByPhoneNumberOrSeqId(senderPhoneNumber, seqId);

        if (node != null) {
            // Se actualiza el estado del nodo
            updateNodeStatus(node, "alive");

            // El nodo existe, actualizar su información
            node.setLocationRefreshRate(locationRefreshRate);

            if (nodeRepository.save(node) == null)
                return "error";

            // Se añade el comando al log de operaciones
            String[] params = { locationRefreshRate };

            if (logCommand(node, LocationGetRefreshRate[0],
                    LocationGetRefreshRate[1], Operation.commandTag,
                    Operation.responseTag, params, null, seqId)) {
                // Respuesta almacenada, liberar el semáforo
                Application.semaphore.release(seqId);
                return "ok";
            }
            else
                return "error";
        }
        else
            // El nodo no existe
            return "error";
    }

    @RequestMapping("/node/cmd/PowerGetInfo/")
    public String powerGetInfo(
            @RequestParam(value = "powerMode", required = true) String powerMode,
            @RequestParam(value = "powerDataUnits", required = false, defaultValue = "default") String powerDataUnits,
            @RequestParam(value = "powerDataType", required = false, defaultValue = "default") String powerDataType,
            @RequestParam(value = "powerMinimum", required = false, defaultValue = "-1") int powerMinimum,
            @RequestParam(value = "powerMaximum", required = false, defaultValue = "-1") int powerMaximum,
            @RequestParam(value = "h", required = true) String hash,
            @RequestParam(value = "s", required = false, defaultValue = "-1") long seqId,
            @RequestParam(value = "phoneNumber", required = false, defaultValue = "default") String senderPhoneNumber) {

        // Busca el nodo del que proviene la respuesta
        Node node = findNodeByPhoneNumberOrSeqId(senderPhoneNumber, seqId);

        if (node != null) {
            // Se actualiza el estado del nodo
            updateNodeStatus(node, "alive");

            // El nodo existe, actualizar su información
            node.setPowerMode(powerMode);
            node.setPowerDataUnits(powerDataUnits);
            node.setPowerDataType(powerDataType);
            node.setPowerMinimum(powerMinimum);
            node.setPowerMaximum(powerMaximum);

            if (nodeRepository.save(node) == null)
                return "error";

            // Se añade el comando al log de operaciones
            String[] params = { powerMode, powerDataUnits, powerDataType,
                    Integer.toString(powerMinimum),
                    Integer.toString(powerMaximum) };

            if (logCommand(node, PowerGetInfo[0], PowerGetInfo[1],
                    Operation.commandTag, Operation.responseTag, params, null,
                    seqId)) {
                // Respuesta almacenada, liberar el semáforo
                Application.semaphore.release(seqId);
                return "ok";
            }
            else
                return "error";
        }
        else
            // El nodo no existe
            return "error";
    }

    @RequestMapping("/node/cmd/PowerGetLevel/")
    public String powerGetLevel(
            @RequestParam(value = "powerLevel", required = true) int powerLevel,
            @RequestParam(value = "h", required = true) String hash,
            @RequestParam(value = "s", required = false, defaultValue = "-1") long seqId,
            @RequestParam(value = "phoneNumber", required = false, defaultValue = "default") String senderPhoneNumber) {

        // Busca el nodo del que proviene la respuesta
        Node node = findNodeByPhoneNumberOrSeqId(senderPhoneNumber, seqId);

        if (node != null) {
            // Se actualiza el estado del nodo
            updateNodeStatus(node, "alive");

            // Se registra la medición
            long time = System.currentTimeMillis();
            Power power = new Power(powerLevel, node, time);

            if (powerRepository.save(power) == null)
                return "error";

            // Se añade el comando al log de operaciones
            String[] params = { Integer.toString(powerLevel) };

            if (logCommand(node, PowerGetLevel[0], PowerGetLevel[1],
                    Operation.commandTag, Operation.responseTag, params, null,
                    seqId)) {
                // Respuesta almacenada, liberar el semáforo
                Application.semaphore.release(seqId);
                return "ok";
            }
            else
                return "error";
        }
        else
            // El nodo no existe
            return "error";
    }

    @RequestMapping("/node/cmd/SensorGetInfo/")
    public String sensorGetInfo(
            @RequestParam(value = "sensorDataUnits", required = true) String sensorDataUnits,
            @RequestParam(value = "sensorDataType", required = true) String sensorDataType,
            @RequestParam(value = "sensorDataUncertainity", required = true) String sensorDataUncertainity,
            @RequestParam(value = "sensorDataLowerRange", required = true) String sensorDataLowerRange,
            @RequestParam(value = "sensorDataUpperRange", required = true) String sensorDataUpperRange,
            @RequestParam(value = "sensorDataChannels", required = false) String sensorDataChannels,
            @RequestParam(value = "sensorDataPacketFormat", required = false) String sensorDataPacketFormat,
            @RequestParam(value = "h", required = true) String hash,
            @RequestParam(value = "s", required = false, defaultValue = "-1") long seqId,
            @RequestParam(value = "phoneNumber", required = false, defaultValue = "default") String senderPhoneNumber) {

        // Busca el nodo del que proviene la respuesta
        Node node = findNodeByPhoneNumberOrSeqId(senderPhoneNumber, seqId);

        if (node != null) {
            // Se actualiza el estado del nodo
            updateNodeStatus(node, "alive");

            // Se obtiene el sensorID
            Operation operation = operationRepository.findBySeqId(seqId);

            // Se busca el sensor del que proviene la respuesta
            Sensor sensor = sensorRepository.findByNodeAndSensorId(node,
                    Long.valueOf(operation.getP()[0]));

            if (sensor != null) {
                // El sensor existe, actualizar su información
                sensor.setSensorDataUnits(sensorDataUnits);
                sensor.setSensorDataType(sensorDataType);
                sensor.setSensorDataUncertainity(sensorDataUncertainity);
                sensor.setSensorDataLowerRange(sensorDataLowerRange);
                sensor.setSensorDataUpperRange(sensorDataUpperRange);
                sensor.setSensorDataChannels(sensorDataChannels);
                sensor.setSensorDataPacketFormat(sensorDataPacketFormat);

                if (sensorRepository.save(sensor) == null)
                    return "error";

                // Se añade el comando al log de operaciones
                String[] params = { sensorDataUnits, sensorDataType,
                        sensorDataUncertainity, sensorDataLowerRange,
                        sensorDataUpperRange, sensorDataChannels,
                        sensorDataPacketFormat };

                if (logCommand(node, SensorGetInfo[0], SensorGetInfo[1],
                        Operation.commandTag, Operation.responseTag, params,
                        null, seqId)) {
                    // Respuesta almacenada, liberar el semáforo
                    Application.semaphore.release(seqId);
                    return "ok";
                }
                else
                    return "error";
            }
            else
                // El sensor no existe
                return "error";
        }
        else
            // El nodo no existe
            return "error";
    }

    @RequestMapping("/node/cmd/SensorGetData/")
    public String sensorGetData(
            @RequestParam(value = "sensorData", required = true) float[] data,
            @RequestParam(value = "utcTime", required = true) long utcTime,
            @RequestParam(value = "h", required = true) String hash,
            @RequestParam(value = "s", required = false, defaultValue = "-1") long seqId,
            @RequestParam(value = "phoneNumber", required = false, defaultValue = "default") String senderPhoneNumber) {
        // Busca el nodo del que proviene la respuesta
        Node node = findNodeByPhoneNumberOrSeqId(senderPhoneNumber, seqId);

        // /client/?CMD=SensorGetDataReq&deviceID=4&sensorID=1&ff=false&h=asdf
        // /node/?CMD=SensorDataRes&deviceID=4&sensorID=1&sensorData=5,14,22&utcTime=98589706458977&h=asdf&s=1

        if (node != null) {
            // Se actualiza el estado del nodo
            updateNodeStatus(node, "alive");

            // Se obtiene el sensorID
            Operation operation = operationRepository.findBySeqId(seqId);

            // Se busca el sensor del que proviene la respuesta
            Sensor sensor = sensorRepository.findByNodeAndSensorId(node,
                    Long.valueOf(operation.getP()[0]));

            if (sensor != null) {
                // El sensor existe, añadir la medición
                // Se registra la medición
                SensorData sensorData = new SensorData(data, sensor, node,
                        utcTime);
                if (sensorDataRepository.save(sensorData) == null)
                    return "error";

                // Crea el array de parámetros teniendo en cuenta que data tiene
                // número de datos variable
                String[] params = new String[1 + data.length];

                // Añade los parámetros
                // params[0] = Long.toString(node.getNodeId());
                // params[1] = Long.toString(sensor.getSensorId());
                for (int i = 0; i < data.length; i++)
                    params[i] = Float.toString(data[i]);
                params[params.length - 1] = Long.toString(utcTime);

                // Se añade el comando al log de operaciones
                if (logCommand(node, SensorGetData[0], SensorGetData[1],
                        Operation.commandTag, Operation.responseTag, params,
                        null, seqId)) {
                    // Respuesta almacenada, liberar el semáforo
                    Application.semaphore.release(seqId);
                    return "ok";
                }
                else
                    return "error";
            }
            else
                // El sensor no existe
                return "error";
        }
        else
            // El nodo no existe
            return "error";
    }

    /************************/
    /** Funciones axiliares */
    /************************/

    /**
     * Función que registra un evento en la base de datos
     * 
     * @param node
     *            : nodo al que corresponde la operación
     * @param opName
     *            : nombre de la operación
     * @param opType
     *            : tipo de la operación (comando o evento)
     * @param params
     *            : parámetros de información de la operación
     * @return boolean: devuelve true en caso de éxito, false en cualquier otro
     *         caso
     */
    private boolean logEvent(Node node, String opName, String opType,
            String[] params) {
        long time = System.currentTimeMillis();
        Operation operation = new Operation(opName, opType, params, node, time);

        if (operationRepository.save(operation) != null)
            return true;
        else
            return false;
    }

    /**
     * Función que registra una respuesta a un comando en la base de datos
     * 
     * @param node
     *            : nodo al que corresponde la operación
     * @param opName
     *            : nombre del comando (respuesta)
     * @param opRefName
     *            : nombre del comando con el que se corresponde (petición)
     * @param opType
     *            : tipo de la operación (comando o evento)
     * @param opClass
     *            : clase de la operación (petición o respuesta)
     * @param params
     *            : parámetros de información del comando
     * @param seqId
     *            : número de secuencia que identifica la operación (-1 si no
     *            existe)
     * @return boolean: devuelve true en caso de éxito, false en cualquier otro
     *         caso
     */
    private boolean logCommand(Node node, String opName, String opRefName,
            String opType, String opClass, String[] params,
            ArrayList<String[]> e, long seqId) {
        long time = System.currentTimeMillis();
        Operation operation;
        if (seqId != -1)
            operation = new Operation(opName, opType, opClass, params, e,
                    seqId, node, time);
        else
            operation = new Operation(opName, opType, opClass, params, e,
                    (Long) null, node, time);

        Operation request = null;

        // Se busca la operación (petición) con la que se corresponde
        if (seqId != -1) {
            // Se busca le petición a partir del número de secuencia
            request = operationRepository.findBySeqId(seqId);
        }
        else {
            // No hay número de secuencia, buscar con nombre de operación y nodo
            request = operationRepository.findByOperationNameAndNode(
                    opRefName, node);
        }

        if (request == null)
            return false;
        else {
            // Se comprueba que la respuesta se corresponde correctamente a la
            // petición
            if (!request.getOperationName().equals(opRefName))
                return false;

            // Se añade la referencia a la petición
            operation.setOperationRef(request);

            Operation savedOperation = operationRepository.save(operation);
            if (savedOperation == null)
                return false;

            // Se añade la referencia a la respuesta en la petición
            request.setOperationRef(savedOperation);

            if (operationRepository.save(request) != null)
                return true;
            else
                return false;

        }
    }

    /**
     * Busca el nodo del que proviene la respuesta. Si seqId tiene valor (!=
     * -1), obtiene el nodo a partir de la operación asociada a dicho número de
     * secuencia. Si no, lo obtiene a partir del número de teléfono. Al menos
     * uno de los dos parámetros debe estar presente.
     * 
     * @param senderPhoneNumber
     *            : número de teléfono del que proviene la respuesta
     * @param seqId
     *            : número de secuencia asociado a la operación
     * @return
     */
    private Node findNodeByPhoneNumberOrSeqId(String senderPhoneNumber,
            long seqId) {
        Node node = null;
        if (seqId == -1) {
            // Se identifica con el número de teléfono
            node = nodeRepository.findByPhoneNumber(senderPhoneNumber);
        }
        else {
            // Se identifica con el número de secuencia
            Operation operation = operationRepository.findBySeqId(seqId);
            if (operation == null) {
                return null;
            }
            node = operation.getNode();
        }

        return node;
    }

    /**
     * Actualiza el estado del nodo, pero sólo si es necesario. Si el nodo está
     * en estado "active", sólo se cambia si el nuevo estado es "dead. Si el
     * nodo está en estado "alive" o "dead", se cambia en cualquier caso.
     * 
     * @param node
     *            : nodo que va a cambiar de estado
     * @param status
     *            : nuevo estado ("active", "alive" o "dead")
     */
    private void updateNodeStatus(Node node, String newStatus) {
        String nodeStatus = node.getStatus();
        if (nodeStatus != null && nodeStatus.equals("active")) {
            // Se cambia sólo si el nuevo es "dead"
            if (newStatus.equals("dead")) {
                node.setStatus(newStatus);
                nodeRepository.saveAndFlush(node);
            }
        }
        else if (nodeStatus != null && !nodeStatus.equals(newStatus)) {
            // Es "alive" o "dead" y no es el estado actual, se cambia
            node.setStatus(newStatus);
            nodeRepository.saveAndFlush(node);
        }
    }
}
