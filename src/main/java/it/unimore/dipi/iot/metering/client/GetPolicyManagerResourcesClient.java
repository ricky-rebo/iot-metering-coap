package it.unimore.dipi.iot.metering.client;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.Utils;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.OptionSet;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class GetPolicyManagerResourcesClient {

	private final static Logger logger = LoggerFactory.getLogger(GetPolicyManagerResourcesClient.class);

	private static final String TARGET_URL = "coap://127.0.0.1:5683/.well-known/core";

	public static void main(String[] args) {
		CoapClient coapClient = new CoapClient(TARGET_URL);

		try {
			CoapResponse coapResp = coapClient.advanced(new Request(Code.GET) {{
				setConfirmable(true);
				setOptions(new OptionSet().setAccept(MediaTypeRegistry.APPLICATION_LINK_FORMAT));
			}});

			String text = coapResp.getResponseText();
			logger.info("--------------------------------------------------------");
			logger.info("Response code: {}", coapResp.getCode());
			logger.info("Resources: \n{}", text.replace(",", "\n\t"));
			logger.info("--------------------------------------------------------");

		} catch (ConnectorException | IOException e) {
			e.printStackTrace();
		}
	}
}