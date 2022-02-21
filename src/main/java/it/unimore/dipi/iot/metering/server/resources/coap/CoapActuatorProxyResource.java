package it.unimore.dipi.iot.metering.server.resources.coap;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapHandler;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.CoAP;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoapActuatorProxyResource extends CoapResource {
    private static final Logger logger = LoggerFactory.getLogger(CoapActuatorProxyResource.class);

    private static final String INTERFACE_DESCRIPTOR = "core.a";

    private final String targetURI;
    private final String targetDeviceID;
    private final CoapClient coapClient;

    public CoapActuatorProxyResource(String name, String targetURI, String targetDeviceID, String targetTitle, String targetResourceType) {
        super(name);

        this.targetURI = targetURI;
        this.targetDeviceID = targetDeviceID;

        this.coapClient = new CoapClient(this.targetURI);

        getAttributes().setTitle(targetTitle);
        getAttributes().setObservable();
        getAttributes().addAttribute("rt", targetResourceType);
        getAttributes().addAttribute("if", INTERFACE_DESCRIPTOR);
        // TODO get CTs from arguments
        getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.APPLICATION_SENML_JSON));
        getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.TEXT_PLAIN));
    }

    public String getTargetURI() {
        return targetURI;
    }

    public String getTargetDeviceID() {
        return targetDeviceID;
    }

    private Request getRequestFromExchange (CoapExchange exchange) {
        Request request = new Request(exchange.getRequestCode());
        request.setOptions(exchange.getRequestOptions());
        request.setPayload(exchange.getRequestPayload());
        return request;
    }


    @Override
    public void handleGET(CoapExchange exchange) {
        this.coapClient.advanced(new ProxyCoapHandler(exchange), getRequestFromExchange(exchange));
    }

    @Override
    public void handlePOST(CoapExchange exchange) {
        this.coapClient.advanced(new ProxyCoapHandler(exchange), getRequestFromExchange(exchange));
    }

    @Override
    public void handlePUT(CoapExchange exchange) {
        this.coapClient.advanced(new ProxyCoapHandler(exchange), getRequestFromExchange(exchange));
    }

    private static class ProxyCoapHandler implements CoapHandler {
        private final CoapExchange exchange;

        public ProxyCoapHandler (CoapExchange exchange) {
            this.exchange = exchange;
        }

        @Override
        public void onLoad(CoapResponse coapResponse) {
            if (coapResponse.isSuccess()) {
                System.out.println(coapResponse.getCode());
                exchange.respond(coapResponse.getCode(), coapResponse.getResponseText(), exchange.getRequestOptions().getAccept());
            } else {
                logger.error("PROXY REQUEST NOT SUCCESS");
                logger.error(coapResponse.getResponseText());
                exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
            }
        }

        @Override
        public void onError() {
            logger.error("PROXY REQUEST FAILED");
            exchange.respond(CoAP.ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
}
