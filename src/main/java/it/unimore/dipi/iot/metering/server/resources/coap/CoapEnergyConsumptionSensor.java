package it.unimore.dipi.iot.metering.server.resources.coap;

import it.unimore.dipi.iot.metering.server.resources.raw.EnergyConsumptionRawSensor;

public class CoapEnergyConsumptionSensor extends CoapGenericConsumptionResource<EnergyConsumptionRawSensor> {
    private static final String SENSOR_TITLE = "EnergyConsumptionSensor";
    private static final String UNIT = "kWh";
    private static final Number VERSION = 0.1;

    public CoapEnergyConsumptionSensor (String name, String deviceID, EnergyConsumptionRawSensor rawSensor) {
        super(SENSOR_TITLE, UNIT, VERSION, name, deviceID, rawSensor);
    }
}
