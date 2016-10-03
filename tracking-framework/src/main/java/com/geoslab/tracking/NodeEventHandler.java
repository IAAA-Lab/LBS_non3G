
package com.geoslab.tracking;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.geoslab.tracking.persistence.domain.Location;
import com.geoslab.tracking.persistence.domain.Node;
import com.geoslab.tracking.persistence.domain.Operation;
import com.geoslab.tracking.persistence.repository.LocationRepository;
import com.geoslab.tracking.persistence.repository.NodeRepository;
import com.geoslab.tracking.persistence.repository.OperationRepository;
import com.geoslab.tracking.persistence.repository.PowerRepository;
import com.geoslab.tracking.persistence.repository.SensorDataRepository;
import com.geoslab.tracking.persistence.repository.SensorRepository;
import com.geoslab.tracking.web.domain.Response;

import reactor.event.Event;
import reactor.function.Function;
// ***************************************************************
// COPYRIGHT(C): IAAA 2014, Todos los derechos reservados
// PROYECTO....: tracking-framework
// ARCHIVO.....: NodeEventHandler.java
// CREACION....: 28/11/2014 rodolfo
// ULTIMA MODIF: TODO Fecha y Quién de la última modificacion
// LENGUAJE....: TODO Versión Plataforma Java
// PLATAFORMA..: TODO Microsoft Windows
// REQUERIM....: TODO Librerías de terceros
// DESCRIPCION.: TODO Descripción
// HISTORIA....: TODO Fecha y Quién - Cambios relevantes
// ............:
// ***************************************************************

/**
 * TODO Describir la funcionalidad.
 * 
 * @author TODO creador del interfaz o clase.
 * @version TODO versión del interfaz o clase.
 */
@Service
public class NodeEventHandler implements Function<Event<Response>, String> {
    
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

    @Override
    public String apply(Event<Response> input) {
        Response resp = input.getData();

        Node node;
        if (resp.getEVT().equalsIgnoreCase(Response.LocationEVT)) {
            //Evento de tipo localización
            node = nodeRepository.findByNodeId(resp.getDeviceId());

            if (node != null) {
                // Se actualiza el estado del nodo
                updateNodeStatus(node, "active");

                // Se registra la medición

                Location location = new Location(
                        resp.getLatitude(), resp.getNsIndicator(),
                        resp.getLongitude(),
                        resp.getEwIndicator(), node, resp.getUtcTime());

                if (locationRepository.save(location) == null)
                    return "error";
            }
            else
                // Ningún nodo asociado a ese número de teléfono
                return "error";

            // Añadir el evento al log de operaciones
            String[] params = { Long.toString(resp.getDeviceId()),
                    resp.getLatitude(),
                    resp.getNsIndicator(),
                    resp.getLongitude(),
                    resp.getEwIndicator(), resp.getUtcTimeParamValue() };

            if (logEvent(node, Response.LocationEVT, Operation.eventTag,
                    params))
                return "ok";
            else
                return "error";
        }
        else if (resp.getEVT().equalsIgnoreCase(Response.DeviceDescriptionEVT)) {
            //Evento de tipo descripción
            node = nodeRepository.findByNodeId(resp.getDeviceId());
            if (node != null) {
                // Existe
                // El nodo se ha encendido, cambiar su estado a "alive" y
                // actualizar la información
                node.setStatus("alive");
                node.setDeviceVersion(resp.getDeviceVersion());
                node.setDeviceDescription(resp.getDeviceDescription());

                if (nodeRepository.save(node) == null)
                    return "error";
            }
            else {
                // No existe, añadirlo
                Node new_node = new Node();

                new_node.setNodeId(resp.getDeviceId());
                new_node.setDeviceVersion(resp.getDeviceVersion());
                new_node.setDeviceDescription(resp.getDeviceDescription());

                new_node.setStatus("alive");

                node = nodeRepository.save(new_node);

                if (node == null)
                    return "error";
            }

            // Añadir el evento al log de operaciones
            String[] params = { Long.toString(resp.getDeviceId()),
                    resp.getDeviceVersion(), resp.getDeviceDescription(),
                    resp.getCloudId() };

            if (logEvent(node, Response.DeviceDescriptionEVT,
                    Operation.eventTag, params))
                return "ok";
            else
                return "error";
        }
        else {
            return "error";
        }
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

    private boolean logEvent(Node node, String opName, String opType,
            String[] params) {
        long time = System.currentTimeMillis();
        Operation operation = new Operation(opName, opType, params, node, time);

        if (operationRepository.save(operation) != null)
            return true;
        else
            return false;
    }



}
