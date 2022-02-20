package it.unimore.dipi.iot.metering.server.resources.coap;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unimore.dipi.iot.metering.server.resources.model.MeterInfoDescriptor;
import it.unimore.dipi.iot.metering.utils.SenMLPack;
import it.unimore.dipi.iot.metering.utils.SenMLRecord;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.print.attribute.standard.Media;
import java.util.Optional;

public class CoapMeterInfoResource extends CoapResource {
    private static final Logger logger = LoggerFactory.getLogger(CoapMeterInfoResource.class);

    private static final String RESOURCE_TITLE = "MeterInfo";
    private static final String RESOURCE_TYPE = "iot:config:device-info";
    private static final String INTERFACE_DESCRIPTOR = "core.rp";

    private final MeterInfoDescriptor deviceInfo;

    private final ObjectMapper mapper;

    public CoapMeterInfoResource(String name, MeterInfoDescriptor deviceInfo) {
        super(name);

        this.deviceInfo = deviceInfo;

        this.mapper = new ObjectMapper();

        this.getAttributes().setTitle(RESOURCE_TITLE);
        getAttributes().addAttribute("rt", RESOURCE_TYPE);
        getAttributes().addAttribute("if", INTERFACE_DESCRIPTOR);
        getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.APPLICATION_SENML_JSON));
        getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.TEXT_PLAIN));
    }

    private Optional<String> getDeviceInfoAsSenml () {
        try {
            SenMLPack pack = new SenMLPack() {{
                add(new SenMLRecord() {{
                    setBn("device-info");
                    setBver(deviceInfo.getMeterVersion());
                    setBt(System.currentTimeMillis());
                }});

                add(new SenMLRecord() {{
                    setN("meter-type");
                    setVs(deviceInfo.getMeterType());
                }});

                add(new SenMLRecord() {{
                    setN("meter-id");
                    setVs(deviceInfo.getMeterID());
                }});
            }};

            return Optional.of(this.mapper.writeValueAsString(pack));
        } catch (Exception e) {
            logger.error("Device info serialization failed -> {}", e.getLocalizedMessage());
            return Optional.empty();
        }
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        try {
            int acceptH = exchange.getRequestOptions().getAccept();
            if (acceptH == MediaTypeRegistry.APPLICATION_SENML_JSON || acceptH == MediaTypeRegistry.APPLICATION_JSON) {
                Optional<String> payload = this.getDeviceInfoAsSenml();
                if (payload.isPresent()) {
                    exchange.respond(CoAP.ResponseCode.CONTENT, payload.get(), acceptH);
                } else {
                    exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
                }
            } else {
                exchange.respond(CoAP.ResponseCode.CONTENT, this.deviceInfo.toString(), MediaTypeRegistry.TEXT_PLAIN);
            }
        } catch (Exception e) {
            logger.error("Error occurred in GET request -> {}", e.getLocalizedMessage());
            exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
}
