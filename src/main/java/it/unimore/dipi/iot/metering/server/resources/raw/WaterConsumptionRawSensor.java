package it.unimore.dipi.iot.metering.server.resources.raw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;

public class WaterConsumptionRawSensor extends SmartObjectSensor {
    private static final Logger logger = LoggerFactory.getLogger(WaterConsumptionRawSensor.class);

    public static final String RESOURCE_TYPE = "iot:sensor:water-consumption";

    // Random value and offset bounds
    private static final Double MIN_START_VALUE = 0.0;
    private static final Double MAX_START_VALUE = 10.5;

    private static final Double MIN_VALUE_VARIATION = 0.5;
    private static final Double MAX_VALUE_VARIATION = 4.5;

    // Internal utils
    private final Random rand;

    public WaterConsumptionRawSensor() {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        this.rand = new Random(System.currentTimeMillis());
        init();
    }

    @Override
    protected Double getStartValue() {
        return MIN_START_VALUE + (MAX_START_VALUE - MIN_START_VALUE) * rand.nextDouble();
    }

    @Override
    protected Double getUpdatedValue() {
        return getValue() + (MAX_VALUE_VARIATION - MIN_VALUE_VARIATION) * rand.nextDouble();
    }

    public static void main (String[] args) {
        WaterConsumptionRawSensor sensor = new WaterConsumptionRawSensor();
        sensor.addValueChangeListener(evt -> {
            double value = Double.parseDouble(evt.getNewValue().toString());
            logger.info("New value received from sensor: {}", value);

            if (value > 7.0 && sensor.isActive()) {
                logger.warn("Max consumption exceeded!");
                logger.warn("Switching OFF supply");
                sensor.setActive(false);
            }
        });
    }
}
