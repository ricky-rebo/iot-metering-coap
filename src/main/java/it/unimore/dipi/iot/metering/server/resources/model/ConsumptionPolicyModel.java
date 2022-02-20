package it.unimore.dipi.iot.metering.server.resources.model;

public class ConsumptionPolicyModel {
    private Double energyConsumptionThreshold;

    private Double waterConsumptionThreshold;

    private Double gasConsumptionThreshold;

    public ConsumptionPolicyModel() {}

    public ConsumptionPolicyModel(Double energyConsumptionThreshold, Double waterConsumptionThreshold, Double gasConsumptionThreshold) {
        this.energyConsumptionThreshold = energyConsumptionThreshold;
        this.waterConsumptionThreshold = waterConsumptionThreshold;
        this.gasConsumptionThreshold = gasConsumptionThreshold;
    }

    public Double getEnergyConsumptionThreshold() {
        return energyConsumptionThreshold;
    }

    public void setEnergyConsumptionThreshold(Double energyConsumptionThreshold) {
        this.energyConsumptionThreshold = energyConsumptionThreshold;
    }

    public Double getWaterConsumptionThreshold() {
        return waterConsumptionThreshold;
    }

    public void setWaterConsumptionThreshold(Double waterConsumptionThreshold) {
        this.waterConsumptionThreshold = waterConsumptionThreshold;
    }

    public Double getGasConsumptionThreshold() {
        return gasConsumptionThreshold;
    }

    public void setGasConsumptionThreshold(Double gasConsumptionThreshold) {
        this.gasConsumptionThreshold = gasConsumptionThreshold;
    }
}
