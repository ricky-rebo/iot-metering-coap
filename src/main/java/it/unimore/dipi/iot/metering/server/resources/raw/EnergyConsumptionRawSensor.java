package it.unimore.dipi.iot.metering.server.resources.raw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;
import java.util.UUID;

public class EnergyConsumptionRawSensor extends SmartObjectSensor {
    private static final Logger logger = LoggerFactory.getLogger(EnergyConsumptionRawSensor.class);

    public static final String RESOURCE_TYPE = "iot:sensor:energy-consumption";

    // Random value ranges
    private static final double MIN_START_VALUE = 0.2;
    private static final double MAX_START_VALUE = 3.5;

    private static final double MIN_VALUE_VARIATION = 0.1;
    private static final double MAX_VALUE_VARIATION = 0.8;

    // Internal utils
    private final Random rand;

    //
    private boolean active;

    public EnergyConsumptionRawSensor() {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        this.rand = new Random(System.currentTimeMillis());
        this.active = true;
        init();
    }

    @Override
    protected Double getStartValue() {
        return MIN_START_VALUE + (MAX_START_VALUE - MIN_START_VALUE) * rand.nextDouble();
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
        EnergyConsumptionRawSensor sensor = new EnergyConsumptionRawSensor();
        sensor.addValueChangeListener((event) -> {
            double newVal = Double.parseDouble(event.getNewValue().toString());
            logger.info("New value generated: {}", newVal);

            if (newVal > 5.0) {
                logger.warn("Maximum consumption exceeded!");
                logger.warn("Switching OFF supply");
                sensor.setActive(false);
            }
        });
    }
}
