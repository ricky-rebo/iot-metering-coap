package it.unimore.dipi.iot.metering.server.devices;

import it.unimore.dipi.iot.metering.server.resources.coap.CoapPolicyParameterResource;
import it.unimore.dipi.iot.metering.server.resources.model.PolicyManagerConfigurationModel;
import it.unimore.dipi.iot.metering.server.resources.raw.PolicyManagerRawConfigParameter;
import org.eclipse.californium.core.CoapServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class CoapPolicyManager extends CoapServer {
    private static final Logger logger = LoggerFactory.getLogger(CoapPolicyManager.class);

    private final String deviceID;

    private PolicyManagerConfigurationModel configuration;
    private Map<String, Object> lastReceived;

    public CoapPolicyManager (int port, PolicyManagerConfigurationModel configuration) {
        super(port);

        this.deviceID = UUID.randomUUID().toString();

        this.lastReceived = new HashMap<>();

        // Create config parameter
        this.configuration = configuration;
        PolicyManagerRawConfigParameter configRawParameter = new PolicyManagerRawConfigParameter(this.configuration);
        CoapPolicyParameterResource coapConfigResource = new CoapPolicyParameterResource(deviceID, "configuration", configRawParameter);
        this.add(coapConfigResource);

        // map config parameter resource changes to internal policy variable
        configRawParameter.addValueChangeListener(evt -> {
            this.configuration = (PolicyManagerConfigurationModel) evt.getNewValue();
        });

        // Register observed meters
        this.configuration.getObservedMeters().forEach(meterURL -> {
            // TODO get request a <meterUrl>/.well_known/core per lista risorse

            // TODO get request a /device-info per ottenere meter id -> add a metersLinkList

            // TODO get request con obs=true a /consumption -> ad ogni nuovo dato ricevuto confronto con policy e in caso stacco la fornitura
        });
    }
}
