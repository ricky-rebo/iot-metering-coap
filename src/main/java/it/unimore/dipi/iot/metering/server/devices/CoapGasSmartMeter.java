package it.unimore.dipi.iot.metering.server.devices;

import it.unimore.dipi.iot.metering.server.resources.coap.CoapGasConsumptionSensor;
import it.unimore.dipi.iot.metering.server.resources.raw.GasConsumptionRawSensor;
import it.unimore.dipi.iot.metering.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoapGasSmartMeter extends CoapSmartMeter<GasConsumptionRawSensor, CoapGasConsumptionSensor> {
    private static final Logger logger = LoggerFactory.getLogger(CoapGasSmartMeter.class);

    private static final String DEVICE_TYPE = "gas";
    private static final Number DEVICE_VERSION = 1.0;

    public CoapGasSmartMeter(int port) {
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
    protected GasConsumptionRawSensor getConsumptionRawSensor() {
        return new GasConsumptionRawSensor();
    }

    @Override
    protected CoapGasConsumptionSensor getCoapConsumptionResource(String deviceID, String name, GasConsumptionRawSensor consumptionRawResource) {
        return new CoapGasConsumptionSensor(deviceID, name, consumptionRawResource);
    }

    public static void main (String[] args) {
        CoapGasSmartMeter meter = new CoapGasSmartMeter(5686);
        meter.start();

        logger.info("GasSmartMeter started! -> Available resources:");
        Utils.logServerResources(logger, meter);
    }
}
