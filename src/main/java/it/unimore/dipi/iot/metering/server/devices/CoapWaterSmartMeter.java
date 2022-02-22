package it.unimore.dipi.iot.metering.server.devices;

import it.unimore.dipi.iot.metering.server.resources.coap.CoapWaterConsumptionResource;
import it.unimore.dipi.iot.metering.server.resources.raw.WaterConsumptionRawSensor;
import it.unimore.dipi.iot.metering.utils.Utils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CoapWaterSmartMeter extends CoapSmartMeter<WaterConsumptionRawSensor, CoapWaterConsumptionResource> {
    private static final Logger logger = LoggerFactory.getLogger(CoapWaterSmartMeter.class);

    private static final String DEVICE_TYPE = "water";
    private static final Number DEVICE_VERSION = 1.0;

    public CoapWaterSmartMeter (int port) {
        super(port);
    }

    @Override
    protected String getDeviceType () {
        return DEVICE_TYPE;
    }

    @Override
    protected Number getDeviceVersion () {
        return DEVICE_VERSION;
    }

    @Override
    protected WaterConsumptionRawSensor getConsumptionRawSensor () {
        return new WaterConsumptionRawSensor();
    }

    @Override
    protected CoapWaterConsumptionResource getCoapConsumptionResource (String deviceID, String name, WaterConsumptionRawSensor consumptionRawResource) {
        return new CoapWaterConsumptionResource(deviceID, name, consumptionRawResource);
    }

    public static void main (String[] args) {
        CoapWaterSmartMeter meter = new CoapWaterSmartMeter(5685);
        meter.start();

        logger.info("WaterSmartMeter started! Available resources:");
        Utils.logServerResources(logger, meter);
    }
}
