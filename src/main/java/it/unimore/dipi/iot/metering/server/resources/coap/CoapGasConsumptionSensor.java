package it.unimore.dipi.iot.metering.server.resources.coap;

import it.unimore.dipi.iot.metering.server.resources.raw.GasConsumptionRawSensor;

public class CoapGasConsumptionSensor extends CoapGenericConsumptionResource<GasConsumptionRawSensor> {
    private static final String SENSOR_TITLE = "GasConsumptionSensor";
    private static final String UNIT = "kg";
    private static final Number VERSION = 0.1;

    public CoapGasConsumptionSensor(String deviceID, String name, GasConsumptionRawSensor rawSensor) {
        super(SENSOR_TITLE, UNIT, VERSION, deviceID, name, rawSensor);
    }
}
