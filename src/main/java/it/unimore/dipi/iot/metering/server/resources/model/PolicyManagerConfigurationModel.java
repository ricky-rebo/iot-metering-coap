package it.unimore.dipi.iot.metering.server.resources.model;

import java.util.ArrayList;

public class PolicyManagerConfigurationModel {
    // Max energy consumption
    private Double energyConsumptionThreshold;

    // Max water consumption
    private Double waterConsumptionThreshold;

    // Max gas consumption
    private Double gasConsumptionThreshold;

    // Observed meters list
    private ArrayList<String> observedMeters;

    public PolicyManagerConfigurationModel() {}

    public PolicyManagerConfigurationModel(Double energyConsumptionThreshold, Double waterConsumptionThreshold, Double gasConsumptionThreshold, ArrayList<String> observedMeters) {
        this.energyConsumptionThreshold = energyConsumptionThreshold;
        this.waterConsumptionThreshold = waterConsumptionThreshold;
        this.gasConsumptionThreshold = gasConsumptionThreshold;
        this.observedMeters = observedMeters;
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

    public ArrayList<String> getObservedMeters() {
        return observedMeters;
    }

    public void setObservedMeters(ArrayList<String> observedMeters) {
        this.observedMeters = observedMeters;
    }
}
