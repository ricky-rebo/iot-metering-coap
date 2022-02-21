package it.unimore.dipi.iot.metering.server.resources.coap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unimore.dipi.iot.metering.server.resources.raw.SwitchRawActuator;
import it.unimore.dipi.iot.metering.utils.SenMLPack;
import it.unimore.dipi.iot.metering.utils.SenMLRecord;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class CoapSwitchResource extends CoapResource {
    private static final Logger logger = LoggerFactory.getLogger(CoapSwitchResource.class);

    private static final String RESOURCE_TITLE = "SwitchActuator";
    private static final Number VERSION = 0.1;

    private String deviceID;
    private SwitchRawActuator switchRawActuator;
    private Boolean state;

    private ObjectMapper objectMapper;

    private boolean initialized = false;

    public CoapSwitchResource(String deviceID, String name, SwitchRawActuator switchRawActuator) {
        super(name);

        try {
            if (deviceID == null)
                throw new NullPointerException("deviceID must be defined!");

            if (switchRawActuator == null)
                throw new NullPointerException("switchRawActuator must be defined!");

            this.deviceID = deviceID;
            this.switchRawActuator = switchRawActuator;
            this.state = this.switchRawActuator.getValue();

            this.objectMapper = new ObjectMapper();
            this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            setObservable(true); // enable observing
            setObserveType(CoAP.Type.CON); // configure the notification type to CONs

            getAttributes().setTitle(RESOURCE_TITLE);
            getAttributes().setObservable();
            getAttributes().addAttribute("rt", switchRawActuator.getType());
            getAttributes().addAttribute("if", "core.a");
            getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.APPLICATION_SENML_JSON));
            getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.TEXT_PLAIN));

            this.switchRawActuator.addValueChangeListener(evt -> {
                logger.info("Raw actuator state updated ! New Value: {}", evt.getNewValue());
                state = (Boolean)evt.getNewValue();
                changed();
            });

            this.initialized = true;
        } catch (Exception e) {
            logger.error("{} coap resource init failed -> {}", name, e.getLocalizedMessage());
        }
    }

    public CoapSwitchResource(String deviceID, String name) {
        this(deviceID, name, new SwitchRawActuator());
    }

    private Optional<String> getValueAsSenmlString () {
        try {
            if (!initialized)
                throw new Exception();

            SenMLPack pack = new SenMLPack();

            SenMLRecord record = new SenMLRecord();
            record.setBn(String.format("%s:%s", this.deviceID, this.getName()));
            record.setBver(VERSION);
            record.setVb(state);
            record.setT(System.currentTimeMillis());
            pack.add(record);

            return Optional.of(this.objectMapper.writeValueAsString(pack));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        if (exchange.getRequestOptions().getAccept() == MediaTypeRegistry.APPLICATION_SENML_JSON || exchange.getRequestOptions().getAccept() == MediaTypeRegistry.APPLICATION_JSON) {
            Optional<String> payload = getValueAsSenmlString();
            if (payload.isPresent()) {
                exchange.respond(CoAP.ResponseCode.CONTENT, payload.get(), exchange.getRequestOptions().getAccept());
            } else {
                exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
            }
        } else {
            exchange.respond(CoAP.ResponseCode.CONTENT, String.valueOf(state), MediaTypeRegistry.TEXT_PLAIN);
        }
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        try {
            // Check if payload is null (otherwise the request is unacceptable
            if (exchange.getRequestPayload().length == 0) {
                this.state = !this.state;
                this.switchRawActuator.setValue(this.state);

                logger.info("Resource status updated! -> State: {}", state ? "ON" : "OFF");

                exchange.respond(CoAP.ResponseCode.CHANGED);
            } else {
                // Payload present -> BAD REQUEST
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST);
            }
        } catch (Exception e) {
            this.handleInternalError(exchange, "POST", e);
        }

    }

    @Override
    public void handlePUT(CoapExchange exchange) {
        try {
            if (exchange.getRequestPayload().length != 0) {
                this.state = Boolean.parseBoolean(new String(exchange.getRequestPayload()));
                this.switchRawActuator.setValue(this.state);

                logger.info("Resource status updated! -> State: {}", state ? "ON" : "OFF");

                exchange.respond(CoAP.ResponseCode.CHANGED);
            } else {
                exchange.respond(CoAP.ResponseCode.BAD_REQUEST);
            }
        } catch (Exception e) {
            this.handleInternalError(exchange, "PUT", e);
        }
    }

    private void handleInternalError(CoapExchange exchange, String reqType, Exception e) {
        logger.error("Error occurred during {} request! -> {}", reqType, e.getLocalizedMessage());
        exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
    }
}
