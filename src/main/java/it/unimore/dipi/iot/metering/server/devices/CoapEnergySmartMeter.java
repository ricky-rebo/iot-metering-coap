package it.unimore.dipi.iot.metering.server.devices;

import it.unimore.dipi.iot.metering.server.resources.coap.CoapEnergyConsumptionSensor;
import it.unimore.dipi.iot.metering.server.resources.coap.CoapMeterInfoResource;
import it.unimore.dipi.iot.metering.server.resources.coap.CoapSwitchResource;
import it.unimore.dipi.iot.metering.server.resources.model.MeterInfoDescriptor;
import it.unimore.dipi.iot.metering.server.resources.raw.EnergyConsumptionRawSensor;
import it.unimore.dipi.iot.metering.server.resources.raw.SwitchRawActuator;
import org.eclipse.californium.core.CoapServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class CoapEnergySmartMeter extends CoapServer {
    private static final Logger logger = LoggerFactory.getLogger(CoapEnergySmartMeter.class);

    private static final Number DEVICE_VERSION = 1.0;
    private static final String DEVICE_TYPE = "energy";

    private final String deviceID;

    public CoapEnergySmartMeter(int port) {
        super(port);

        this.deviceID = UUID.randomUUID().toString();

        // Create device info resource
        CoapMeterInfoResource deviceInfoResource = new CoapMeterInfoResource(
                "device-info",
                new MeterInfoDescriptor(deviceID, DEVICE_VERSION, DEVICE_TYPE)
        );

        // Create consumption sensor
        EnergyConsumptionRawSensor energyRawSensor = new EnergyConsumptionRawSensor();
        CoapEnergyConsumptionSensor coapEnergyResource = new CoapEnergyConsumptionSensor(deviceID, "consumption", energyRawSensor);

        // Create switch actuator
        SwitchRawActuator switchRawActuator = new SwitchRawActuator();
        CoapSwitchResource coapSwitchResource = new CoapSwitchResource(deviceID, "switch", switchRawActuator);

        // Add resources
        this.add(deviceInfoResource, coapEnergyResource, coapSwitchResource);

        // Register to switch state change
        switchRawActuator.addValueChangeListener(evt -> {
            logger.info("Switch state updated! -> {}", (Boolean)evt.getNewValue() ? "ON" : "OFF");
            logger.info("Updating sensor state...");
            energyRawSensor.setActive((Boolean)evt.getNewValue());
        });
    }

    public String getDeviceID() {
        return deviceID;
    }

    public static void main(String[] args) {

        CoapEnergySmartMeter device = new CoapEnergySmartMeter(5684);
        device.start();

        logger.info("Coap Server Started ! Available resources: ");

        device.getRoot().getChildren().forEach(resource -> {
            logger.info("Resource {} -> URI: {} (Observable: {})", resource.getName(), resource.getURI(), resource.isObservable());
            if (!resource.getURI().equals("/.well-known")) {
                resource.getChildren().forEach(childResource -> {
                    logger.info("\t Resource {} -> URI: {} (Observable: {})", childResource.getName(), childResource.getURI(), childResource.isObservable());
                });
            }
        });
    }
}
