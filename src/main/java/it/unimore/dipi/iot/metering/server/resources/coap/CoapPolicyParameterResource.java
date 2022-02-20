package it.unimore.dipi.iot.metering.server.resources.coap;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unimore.dipi.iot.metering.server.resources.model.ConsumptionPolicyModel;
import it.unimore.dipi.iot.metering.server.resources.raw.PolicyManagerRawConfigParameter;
import it.unimore.dipi.iot.metering.utils.SenMLPack;
import it.unimore.dipi.iot.metering.utils.SenMLRecord;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Optional;

public class CoapPolicyParameterResource extends CoapResource {
    private static final Logger logger = LoggerFactory.getLogger(CoapPolicyParameterResource.class);

    private static final String RESOURCE_TITLE = "PolicyManagerConfiguration";
    private static final Number VERSION = 0.1;

    private String deviceID;

    private PolicyManagerRawConfigParameter rawConfigParameter;
    private ConsumptionPolicyModel policy;

    private ObjectMapper mapper;

    public CoapPolicyParameterResource (String deviceID, String name, PolicyManagerRawConfigParameter rawConfigParameter) {
        super(name);

        try {
            if (deviceID == null)
                throw new NullPointerException("deviceID must be defined!");

            if (rawConfigParameter == null)
                throw new NullPointerException("rawConfigParameter must be defined!");

            this.deviceID = deviceID;
            this.rawConfigParameter = rawConfigParameter;
            this.policy = this.rawConfigParameter.getValue();

            this.mapper = new ObjectMapper();

            this.setObservable(true);
            this.setObserveType(CoAP.Type.CON);

            getAttributes().setTitle(RESOURCE_TITLE);
            getAttributes().setObservable();
            getAttributes().addAttribute("rt", rawConfigParameter.getType());
            getAttributes().addAttribute("if", "core.p");
            getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.APPLICATION_SENML_JSON));
            getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.TEXT_PLAIN));

            this.rawConfigParameter.addValueChangeListener(evt -> {
                logger.info("Policy Manager configuration updated! -> New config: {}", evt.getNewValue());
                policy = (ConsumptionPolicyModel) evt.getNewValue();
                changed();
            });
        } catch (Exception e) {
            logger.error("{} coap resource init failed -> {}", name, e.getLocalizedMessage());
        }
    }

    private Optional<String> getValueAsSenml () {
        try {
            SenMLRecord base = new SenMLRecord() {{
                setBver(VERSION);
                setBn(String.format("%s:%s", deviceID, getName()));
            }};

            SenMLRecord maxEnergyConsumption = new SenMLRecord() {{
                setN("max_energy_consumption");
                setV(policy.getEnergyConsumptionThreshold());
            }};

            SenMLRecord maxWaterConsumption = new SenMLRecord() {{
                setN("max_water_consumption");
                setV(policy.getWaterConsumptionThreshold());
            }};

            SenMLRecord maxGasConsumption = new SenMLRecord() {{
                setN("max_gas_consumption");
            }};

            SenMLPack pack = new SenMLPack() {{
                add(base);
                add(maxEnergyConsumption);
                add(maxWaterConsumption);
                add(maxGasConsumption);
            }};

            return Optional.of(this.mapper.writeValueAsString(pack));
        } catch (Exception e) {
            return Optional.empty();
        }
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        try {
            int acceptHeader = exchange.getRequestOptions().getAccept();
            if (acceptHeader == MediaTypeRegistry.APPLICATION_SENML_JSON || acceptHeader == MediaTypeRegistry.APPLICATION_JSON) {
                Optional<String> payload = this.getValueAsSenml();

                if (payload.isPresent()) {
                    exchange.respond(CoAP.ResponseCode.CONTENT, payload.get(), acceptHeader);
                } else {
                    exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
                }
            } else {
                exchange.respond(CoAP.ResponseCode.CONTENT, this.policy.toString(), MediaTypeRegistry.TEXT_PLAIN);
            }
        } catch (Exception e) {
            logger.error("Error occurred trying to respond to {} -> {}", exchange.getRequestOptions().getUriHost(), e.getLocalizedMessage());
            e.printStackTrace();
            exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }

    @Override
    public void handlePUT(CoapExchange exchange) {
        // TODO implement
        super.handlePUT(exchange);
    }
}
