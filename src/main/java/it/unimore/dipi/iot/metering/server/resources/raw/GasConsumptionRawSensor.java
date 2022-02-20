package it.unimore.dipi.iot.metering.server.resources.raw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;

public class GasConsumptionRawSensor extends SmartObjectSensor {
    private static final Logger logger = LoggerFactory.getLogger(GasConsumptionRawSensor.class);

    public static final String RESOURCE_TYPE = "iot:sensor:gas-consumption";

    // Random value bounds
    private static final double MIN_START_VALUE = 1.3;
    private static final double MAX_START_VALUE = 5.0;

    private static final double MIN_VALUE_VARIATION = 0.5;
    private static final double MAX_VALUE_VARIATION = 2.5;

    // Internal utils
    private final Random rand;

    //
    private boolean active;

    public GasConsumptionRawSensor() {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        this.rand = new Random((System.currentTimeMillis()));
        this.active = true;
        init();
    }

    @Override
    protected Double getStartValue() {
        return MIN_START_VALUE + rand.nextDouble() * (MAX_START_VALUE - MIN_START_VALUE);
    }

    @Override
    protected Double getUpdatedValue() {
        return active ? (getValue() + (MAX_VALUE_VARIATION - MIN_VALUE_VARIATION) * rand.nextDouble()) : 0.0;
    }

    public boolean isActive() {
        return active;
    }

    public void setActive(boolean active) {
        this.active = active;
    }

    public static void main(String[] args) {
        GasConsumptionRawSensor sensor = new GasConsumptionRawSensor();
        sensor.addValueChangeListener((event) -> {
            double newVal = Double.parseDouble(event.getNewValue().toString());
            logger.info("New value received: {}", newVal);

            if (newVal > 4.0 && sensor.isActive()) {
                // TEST
                logger.warn("Max consumption exceeded!");
                logger.warn("Switching OFF supply");
                sensor.setActive(false);
            }
        });
    }
}
