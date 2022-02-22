package it.unimore.dipi.iot.metering.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class PostMeterStateClient {
    private static final Logger logger = LoggerFactory.getLogger(PostMeterStateClient.class);

    private static final String METER_ID = "d30f7044-f026-4f29-87a5-d601805cf8ee";
    private static final String TARGET_URL = String.format("coap://localhost:5683/meters/%s/state", METER_ID);

    public static void main (String[] args) {
        CoapClient client = new CoapClient(TARGET_URL);

        try {
            CoapResponse response = client.advanced(new Request(CoAP.Code.POST) {{
                setConfirmable(true);
            }});

            logger.info("--------------------------------------------------------");
            logger.info("Response code: {}", response.getCode());
            logger.info("Payload: {}", response.getResponseText());
            logger.info("--------------------------------------------------------");
        } catch (ConnectorException | IOException e) {
            e.printStackTrace();
        }
    }
}
