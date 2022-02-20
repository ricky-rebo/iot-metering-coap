package it.unimore.dipi.iot.metering.server.resources.model;

public class MeterInfoDescriptor {
    private String meterID;
    private Number meterVersion;
    private String meterType;

    public MeterInfoDescriptor(String meterID, Number meterVersion, String meterType) {
        this.meterID = meterID;
        this.meterVersion = meterVersion;
        this.meterType = meterType;
    }

    public MeterInfoDescriptor() {
    }

    public String getMeterID() {
        return meterID;
    }

    public void setMeterID(String meterID) {
        this.meterID = meterID;
    }

    public Number getMeterVersion() {
        return meterVersion;
    }

    public void setMeterVersion(Number meterVersion) {
        this.meterVersion = meterVersion;
    }

    public String getMeterType() {
        return meterType;
    }

    public void setMeterType(String meterType) {
        this.meterType = meterType;
    }

    @Override
    public String toString() {
        return String.format("MeterInfoDescriptor{meterID='%s', meterVersion='%s', meterType='%s'}", meterID, meterVersion, meterType);
    }
}
