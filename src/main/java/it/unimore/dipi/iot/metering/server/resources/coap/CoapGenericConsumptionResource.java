package it.unimore.dipi.iot.metering.server.resources.coap;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import it.unimore.dipi.iot.metering.server.resources.raw.SmartObjectSensor;
import it.unimore.dipi.iot.metering.utils.SenMLPack;
import it.unimore.dipi.iot.metering.utils.SenMLRecord;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public abstract class CoapGenericConsumptionResource<T extends SmartObjectSensor> extends CoapResource {
    private static final Logger logger = LoggerFactory.getLogger(CoapGenericConsumptionResource.class);

    private final String unit;
    private final Number version;

    private String deviceID;

    private T rawSensor;
    private Double value;

    private ObjectMapper mapper;

    private boolean initialized = false;

    public CoapGenericConsumptionResource (String title, String unit, Number version, String deviceID, String name, T rawSensor) {
        super(name);

        this.unit = unit;
        this.version = version;

        try {
            if (deviceID == null)
                throw new NullPointerException("deviceID must be defined!");

            if (rawSensor == null)
                throw new NullPointerException("rawSensor must be defined!");

            this.deviceID = deviceID;
            this.rawSensor = rawSensor;
            this.value = this.rawSensor.getValue();

            this.mapper = new ObjectMapper();
            this.mapper.setSerializationInclusion(JsonInclude.Include.NON_NULL);

            this.setObservable(true);
            this.setObserveType(CoAP.Type.CON);

            getAttributes().setTitle(title);
            getAttributes().setObservable();
            getAttributes().addAttribute("rt", rawSensor.getType());
            getAttributes().addAttribute("if", "core.s");
            getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.APPLICATION_SENML_JSON));
            getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.TEXT_PLAIN));

            // Subscribe to  raw sensor updates
            this.rawSensor.addValueChangeListener(evt -> {
                logger.info("Sensor value updated! -> {} {}", evt.getNewValue(), this.unit);
                this.value = (Double)evt.getNewValue();
                changed();
            });

            this.initialized = true;
        } catch (Exception e) {
            logger.error("{} coap resource init failed -> {}", name, e.getLocalizedMessage());
        }
    }

    private Optional<String> getValueAsSenml () {
        try {
            if (!initialized)
                throw new Exception();

            SenMLPack pack = new SenMLPack();

            SenMLRecord record = new SenMLRecord();
            record.setBn(String.format("%s:%s", this.deviceID, this.getName()));
            record.setBver(this.version);
            record.setV(this.value);
            record.setU(this.unit);
            record.setT(System.currentTimeMillis());
            pack.add(record);

            return Optional.of(this.mapper.writeValueAsString(pack));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void handleGET (CoapExchange exchange) {
        try {
            // Set response max age
            exchange.setMaxAge(T.UPDATE_PERIOD);

            int acceptHeader = exchange.getRequestOptions().getAccept();
            if (acceptHeader == MediaTypeRegistry.APPLICATION_SENML_JSON || acceptHeader == MediaTypeRegistry.APPLICATION_JSON) {
                Optional<String> payload = this.getValueAsSenml();
                if (payload.isPresent()) {
                    exchange.respond(CoAP.ResponseCode.CONTENT, payload.get(), acceptHeader);
                } else {
                    exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
                }
            } else {
                exchange.respond(CoAP.ResponseCode.CONTENT, String.valueOf(this.value), MediaTypeRegistry.TEXT_PLAIN);
            }
        } catch (Exception e) {
            logger.error("Error occurred trying to respond to {} -> {}", exchange.getRequestOptions().getUriHost(), e.getLocalizedMessage());
            e.printStackTrace();
            exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
        }

    }
}
