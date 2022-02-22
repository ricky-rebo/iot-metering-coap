package it.unimore.dipi.iot.metering.server.devices;

import it.unimore.dipi.iot.metering.server.resources.coap.CoapGenericConsumptionResource;
import it.unimore.dipi.iot.metering.server.resources.coap.CoapMeterInfoResource;
import it.unimore.dipi.iot.metering.server.resources.coap.CoapSwitchResource;
import it.unimore.dipi.iot.metering.server.resources.model.MeterInfoDescriptor;
import it.unimore.dipi.iot.metering.server.resources.raw.SmartObjectSensor;
import it.unimore.dipi.iot.metering.server.resources.raw.SwitchRawActuator;
import org.eclipse.californium.core.CoapServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public abstract class CoapSmartMeter<RS extends SmartObjectSensor, CR extends CoapGenericConsumptionResource<RS>> extends CoapServer {
    private static final Logger logger = LoggerFactory.getLogger(CoapSmartMeter.class);

    private final String deviceID;

    public CoapSmartMeter(int port) {
        super(port);

        this.deviceID = UUID.randomUUID().toString();

        // Create device info resource
        CoapMeterInfoResource deviceInfoResource = new CoapMeterInfoResource(
                "device-info",
                new MeterInfoDescriptor(deviceID, getDeviceVersion(), getDeviceType())
        );

        // Create consumption sensor
        RS consumptionRawSensor = this.getConsumptionRawSensor();
        CR consumptionCoapResource = this.getCoapConsumptionResource(deviceID, "consumption", consumptionRawSensor);

        // Create switch actuator
        SwitchRawActuator switchRawActuator = new SwitchRawActuator();
        CoapSwitchResource coapSwitchResource = new CoapSwitchResource(deviceID, "switch", switchRawActuator);

        // Add resources
        this.add(deviceInfoResource, consumptionCoapResource, coapSwitchResource);

        // Register to switch state change
        switchRawActuator.addValueChangeListener(evt -> {
            logger.info("Switch state updated! -> {}", (Boolean)evt.getNewValue() ? "ON" : "OFF");
            logger.info("Updating sensor state...");
            consumptionRawSensor.setActive((Boolean)evt.getNewValue());
        });
    }

    protected abstract String getDeviceType();

    protected abstract Number getDeviceVersion();

    protected abstract RS getConsumptionRawSensor();

    protected abstract CR getCoapConsumptionResource(String deviceID, String name, RS consumptionRawResource);

    public String getDeviceID() {
        return deviceID;
    }
}
