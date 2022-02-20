package it.unimore.dipi.iot.metering.server.resources.raw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Timer;
import java.util.TimerTask;

public abstract class SmartObjectSensor extends SmartObjectResource<Double> {
    private static final Logger logger = LoggerFactory.getLogger(SmartObjectSensor.class);

    public static final int UPDATE_START_DELAY = 5000;
    public static final int UPDATE_PERIOD = 3000;

    protected abstract Double getStartValue();
    protected abstract Double getUpdatedValue();

    // Internal utils
    private Timer updateTimer;

    private Double value;

    public SmartObjectSensor () { super(); }

    public SmartObjectSensor (String id, String type) {
        super(id, type);
    }

    protected void init () {
        try {
            this.updateTimer = new Timer();

            value = getStartValue();

            logger.info("Sensor {}:{} initialized!", getType(), getId());

            start();
        } catch (Exception e) {
            logger.info("Sensor {}:{} NOT initialized! -> Err: {}", getType(), getId(), e.getLocalizedMessage());
        }
    }

    protected void start () {
        logger.info("Starting periodic update on sensor {}:{}", getType(), getId());
        this.updateTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                setValue(getUpdatedValue());
            }
        }, getUpdateStartDelay(), getUpdatePeriod());
    }

    protected void stop () {
        logger.info("Stopping periodic update on sensor {}:{}", getType(), getId());
        this.updateTimer.cancel();
    }

    protected int getUpdateStartDelay() {
        return UPDATE_START_DELAY;
    }

    protected int getUpdatePeriod() {
        return UPDATE_PERIOD;
    }

    @Override
    public Double getValue() {
        return value;
    }

    @Override
    public void setValue(Double value) {
        if (this.value.equals(value)) {
            this.fireValueChange(null, this.value);
        } else {
            Double oldValue = this.value;
            this.value = value;
            this.fireValueChange(oldValue, this.value);
        }
    }
}
