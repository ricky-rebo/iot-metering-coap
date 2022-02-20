package it.unimore.dipi.iot.metering.server.resources.coap;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoapMetersLinkListResource extends CoapResource {
//    private static final Logger logger = LoggerFactory.getLogger(CoapMetersLinkListResource.class);

    private static final String RESOURCE_TYPE = "iot:link-list";
    private static final String INTERFACE_DESCRIPTOR = "core.ll";

    public CoapMetersLinkListResource(String name, String title) {
        super(name);

        // Set resource attributes
        getAttributes().setTitle(title);
        getAttributes().addAttribute("rt", RESOURCE_TYPE);
        getAttributes().addAttribute("if", INTERFACE_DESCRIPTOR);
        getAttributes().addAttribute("ct", Integer.toString(MediaTypeRegistry.APPLICATION_LINK_FORMAT));
    }

    @Override
    public void handleGET(CoapExchange exchange) {
        try {
            int acceptH = exchange.getRequestOptions().getAccept();
            if(acceptH == MediaTypeRegistry.APPLICATION_LINK_FORMAT) {
                // Compose link list
                StringBuilder sb = new StringBuilder();
                this.getChildren().forEach(child -> {
                    child.getChildren().forEach(meterRef -> {
                        sb.append(
                                String.format("<%s>;rt=\"%s\";if=\"%s\",",
                                    meterRef.getURI(),
                                    meterRef.getAttributes().getResourceTypes().get(0),
                                    meterRef.getAttributes().getInterfaceDescriptions().get(0)
                                )
                        );
                    });
                });
                sb.deleteCharAt(sb.lastIndexOf(","));

                // Send link list
                exchange.respond(ResponseCode.CONTENT, sb.toString(), acceptH);
            } else {
                exchange.respond(ResponseCode.NOT_ACCEPTABLE);
            }
        } catch (Exception e) {
            e.printStackTrace();
            exchange.respond(ResponseCode.INTERNAL_SERVER_ERROR);
        }
    }
}
