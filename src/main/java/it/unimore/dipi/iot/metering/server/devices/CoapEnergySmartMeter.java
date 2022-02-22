package it.unimore.dipi.iot.metering.server.devices;

import it.unimore.dipi.iot.metering.server.resources.coap.CoapEnergyConsumptionSensor;
import it.unimore.dipi.iot.metering.server.resources.coap.CoapMeterInfoResource;
import it.unimore.dipi.iot.metering.server.resources.coap.CoapSwitchResource;
import it.unimore.dipi.iot.metering.server.resources.model.MeterInfoDescriptor;
import it.unimore.dipi.iot.metering.server.resources.raw.EnergyConsumptionRawSensor;
import it.unimore.dipi.iot.metering.server.resources.raw.SwitchRawActuator;
import it.unimore.dipi.iot.metering.utils.Utils;
import org.eclipse.californium.core.CoapServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class CoapEnergySmartMeter extends CoapSmartMeter<EnergyConsumptionRawSensor, CoapEnergyConsumptionSensor> {
    private static final Logger logger = LoggerFactory.getLogger(CoapEnergySmartMeter.class);

    private static final Number DEVICE_VERSION = 1.0;
    private static final String DEVICE_TYPE = "energy";

    public CoapEnergySmartMeter(int port) {
        super(port);
    }

    @Override
    protected String getDeviceType() {
        return DEVICE_TYPE;
    }

    @Override
    protected Number getDeviceVersion() {
        return DEVICE_VERSION;
    }

    @Override
    protected EnergyConsumptionRawSensor getConsumptionRawSensor() {
        return new EnergyConsumptionRawSensor();
    }

    @Override
    protected CoapEnergyConsumptionSensor getCoapConsumptionResource(String deviceID, String name, EnergyConsumptionRawSensor consumptionRawResource) {
        return new CoapEnergyConsumptionSensor(deviceID, name, consumptionRawResource);
    }

    public static void main(String[] args) {

        CoapEnergySmartMeter device = new CoapEnergySmartMeter(5684);
        device.start();

        logger.info("Coap Server Started ! Available resources: ");
        Utils.logServerResources(logger, device);
    }
}
