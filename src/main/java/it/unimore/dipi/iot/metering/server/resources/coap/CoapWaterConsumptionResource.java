package it.unimore.dipi.iot.metering.server.resources.coap;

import it.unimore.dipi.iot.metering.server.resources.raw.WaterConsumptionRawSensor;

public class CoapWaterConsumptionResource extends CoapGenericConsumptionResource<WaterConsumptionRawSensor> {
    private static final String SENSOR_TITLE = "WaterConsumptionSensor";
    private static final String UNIT = "l/s";
    private static final Number VERSION = 0.1;

    public CoapWaterConsumptionResource(String deviceID, String name, WaterConsumptionRawSensor rawSensor) {
        super(SENSOR_TITLE, UNIT, VERSION, deviceID, name, rawSensor);
    }
}
