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

public class GetObservedMetersClient {
    private static final Logger logger = LoggerFactory.getLogger(GetObservedMetersClient.class);

    private static final String TARGET_URL = "coap://localhost:5683/meters";

    public static void main (String[] args) {
        CoapClient client = new CoapClient(TARGET_URL);

        try {
            CoapResponse response = client.advanced(new Request(CoAP.Code.GET) {{
                setConfirmable(true);
                setOptions(new OptionSet().setAccept(MediaTypeRegistry.APPLICATION_LINK_FORMAT));
            }});

            String text = response.getResponseText();
            logger.info("--------------------------------------------------------");
            logger.info("Response code: {}", response.getCode());
            logger.info("Resources: \n\t{}", text.replace(",", "\n\t"));
            logger.info("--------------------------------------------------------");
        } catch (ConnectorException | IOException e) {
            e.printStackTrace();
        }
    }
}
