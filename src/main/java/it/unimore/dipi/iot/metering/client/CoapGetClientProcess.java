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

public class CoapGetClientProcess {

	private final static Logger logger = LoggerFactory.getLogger(CoapGetClientProcess.class);

//	private static final String COAP_ENDPOINT = "coap://127.0.0.1:5683/meters/3f42c08d-0622-4094-bdb1-d6bc5ad2e4f4/state";
//	private static final String COAP_ENDPOINT = "coap://127.0.0.1:5684/.well-known/core";
	private static final String COAP_ENDPOINT = "coap://127.0.0.1:5684/switch";

	public static void main(String[] args) {
		
		//Initialize coapClient
		CoapClient coapClient = new CoapClient(COAP_ENDPOINT);

		//Request Class is a generic CoAP message: in this case we want a GET.
		//"Message ID", "Token" and other header's fields can be set 
		Request request = new Request(Code.POST);
		request.setOptions(new OptionSet().setAccept(MediaTypeRegistry.APPLICATION_SENML_JSON));
//		request.setOptions(new OptionSet().setAccept(MediaTypeRegistry.APPLICATION_LINK_FORMAT));

		//Set Request as Confirmable
		request.setConfirmable(true);

		logger.info("Request Pretty Print: \n{}", Utils.prettyPrint(request));

		try {
			CoapResponse coapResp = coapClient.advanced(request);

			String text = coapResp.getResponseText();
			logger.info("--------------------------------------------------------");
			logger.info("Response code: {}", coapResp.getCode());
			logger.info("Payload: \n{}", text/*.replace(",", "\n")*/);
			logger.info("Message ID: " + coapResp.advanced().getMID());
			logger.info("Token: " + coapResp.advanced().getTokenString());
			logger.info("--------------------------------------------------------");

		} catch (ConnectorException | IOException e) {
			e.printStackTrace();
		}
	}
}