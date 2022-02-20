package it.unimore.dipi.iot.metering.server.resources.raw;

import java.util.UUID;

public class SwitchRawActuator extends SmartObjectResource<Boolean> {
    public static final String RESOURCE_TYPE = "iot:actuator:switch";

    private Boolean isActive;

    public SwitchRawActuator() {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        this.init();
    }

    private void init() {
        this.isActive = true;
    }

    public Boolean getValue() {
        return isActive;
    }

    public void setValue(Boolean newValue) {
        if (this.isActive.equals(newValue)) {
            this.fireValueChange(null, this.isActive);
        } else {
            Boolean oldValue = this.isActive;
            this.isActive = newValue;
            this.fireValueChange(oldValue, this.isActive);
        }
    }
}
