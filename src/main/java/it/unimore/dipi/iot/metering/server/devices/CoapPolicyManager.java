package it.unimore.dipi.iot.metering.server.devices;

import com.fasterxml.jackson.databind.ObjectMapper;
import it.unimore.dipi.iot.metering.server.resources.coap.CoapActuatorProxyResource;
import it.unimore.dipi.iot.metering.server.resources.coap.CoapLinkListResource;
import it.unimore.dipi.iot.metering.server.resources.coap.CoapPolicyParameterResource;
import it.unimore.dipi.iot.metering.server.resources.model.ResourceURIDescriptor;
import it.unimore.dipi.iot.metering.server.resources.model.MeterInfoDescriptor;
import it.unimore.dipi.iot.metering.server.resources.model.PolicyManagerConfigurationModel;
import it.unimore.dipi.iot.metering.server.resources.raw.PolicyManagerRawConfigParameter;
import it.unimore.dipi.iot.metering.utils.SenMLPack;
import it.unimore.dipi.iot.metering.utils.SenMLRecord;
import it.unimore.dipi.iot.metering.utils.Utils;

import org.eclipse.californium.core.*;
import org.eclipse.californium.core.coap.*;
import org.eclipse.californium.core.server.resources.Resource;
import org.eclipse.californium.core.server.resources.ResourceObserverAdapter;
import org.eclipse.californium.elements.exception.ConnectorException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.security.KeyException;
import java.util.*;

public class CoapPolicyManager extends CoapServer {
    private static final Logger logger = LoggerFactory.getLogger(CoapPolicyManager.class);

    // Internal parameters
    private PolicyManagerConfigurationModel configuration;
    private final Map<String, Double> lastReceived;

    // Resources
    private final CoapLinkListResource observedMetersList;

    // Tools
    private final ObjectMapper mapper;

    public CoapPolicyManager (int port, PolicyManagerConfigurationModel configuration) {
        super(port);

        String deviceID = UUID.randomUUID().toString();

        this.lastReceived = new HashMap<>();

        // Init tools
        this.mapper = new ObjectMapper();

        // Create config parameter
        this.configuration = configuration;
        PolicyManagerRawConfigParameter configRawParameter = new PolicyManagerRawConfigParameter(this.configuration);
        CoapPolicyParameterResource coapConfigResource = new CoapPolicyParameterResource(deviceID, "configuration", configRawParameter);
        this.add(coapConfigResource);

        // map config parameter resource changes to internal policy variable
        configRawParameter.addValueChangeListener(evt -> {
            this.configuration = (PolicyManagerConfigurationModel) evt.getNewValue();
        });

        // Create observed-meters link-list
        this.observedMetersList = new CoapLinkListResource("meters", "ObservedMeters");
        observedMetersList.addObserver(new ResourceObserverAdapter() {
            @Override
            public void addedChild(Resource child) {
                logger.info("Added meter {} to observed-meters list", child.getName());
            }

            @Override
            public void removedChild(Resource child) {
                logger.info("Removed meter {} from observed-meters list", child.getName());
            }
        });
        this.add(observedMetersList);
    }

    @Override
    public synchronized void start() {
        super.start();

        // Register observed meters
        this.configuration.getObservedMeters().forEach(meterURL -> {
            // Get meter resources
            List<ResourceURIDescriptor> meterResources = this.getDeviceResources(meterURL);

            try {
                Optional<ResourceURIDescriptor> meterInfoResource = meterResources.stream().filter(res -> res.getRt().equals("iot:config:device-info")).findFirst();
                if (meterInfoResource.isEmpty())
                    throw new Exception(String.format("Device @ %s has no iot:config:device-info resource!", meterURL));

                // Get device-info
                MeterInfoDescriptor deviceInfo = this.getMeterDeviceInfo(String.format("%s%s", meterURL, meterInfoResource.get().getUri()));

                // Get meter switch resource url
                Optional<ResourceURIDescriptor> meterSwitchResource = meterResources.stream().filter(res -> res.getRt().equals("iot:actuator:switch")).findFirst();
                if (meterSwitchResource.isEmpty())
                    throw new Exception(String.format("Device @ %s has no iot:actuator:switch resource!", meterURL));

                // Add meter switch proxy to meters link-list
                CoapResource meterMidLevel = new CoapResource(deviceInfo.getMeterID()); // IMPROVE: this could be a batch -> add proxy to meter consumption and batch GET both resources
                CoapActuatorProxyResource meterStateProxy = new CoapActuatorProxyResource(
                        "state",
                        String.format("%s%s", meterURL, meterSwitchResource.get().getUri()),
                        deviceInfo.getMeterID(),
                        String.format("Meter-%s-switch", deviceInfo.getMeterID()),
                        meterSwitchResource.get().getRt()
                );
                meterMidLevel.add(meterStateProxy);
                this.observedMetersList.add(meterMidLevel);

                // Get consumption resource
                String consumptionSensorRt = String.format("iot:sensor:%s-consumption", deviceInfo.getMeterType());
                Optional<ResourceURIDescriptor> consumptionSensorResourceURI = meterResources.stream().filter(res -> res.getRt().equals(consumptionSensorRt)).findFirst();
                if (consumptionSensorResourceURI.isEmpty())
                    throw new Exception(String.format("device @ %s has no %s resource", meterURL, consumptionSensorRt));

                // Start observing meter consumption
                this.observeMeterConsumption(String.format("%s%s", meterURL, consumptionSensorResourceURI.get().getUri()), deviceInfo.getMeterType());
            } catch (Exception e) {
                logger.error("Unable to register meter -> {}", e.getLocalizedMessage());
                logger.error("Ignoring device @ {}", meterURL);
            }
        });
    }

    private ArrayList<ResourceURIDescriptor> getDeviceResources (String deviceURL) {
        CoapClient client = new CoapClient(String.format("%s/.well-known/core", deviceURL));
        try {
            CoapResponse response = client.advanced(new Request(CoAP.Code.GET) {{
                setConfirmable(true);
                setOptions(new OptionSet().setAccept(MediaTypeRegistry.APPLICATION_LINK_FORMAT));
            }});
            if (!response.isSuccess())
                throw new Exception(String.format("Error %s", response.getCode()));

            return Utils.parseLinkFormatText(response.getResponseText());
        } catch (Exception e) {
            logger.error("Unable to retrieve data from {}/.well-known/core -> {}", deviceURL, e.getLocalizedMessage());
            return new ArrayList<>();
        }
    }

    private MeterInfoDescriptor getMeterDeviceInfo (String meterInfoResourceURL) throws Exception {
        CoapClient client = new CoapClient(meterInfoResourceURL);
        try {
            CoapResponse response = client.advanced(new Request(CoAP.Code.GET) {{
                setConfirmable(true);
                setOptions(new OptionSet().setAccept(MediaTypeRegistry.APPLICATION_SENML_JSON));
            }});
            if (!response.isSuccess())
                throw new Exception(String.format("Error %s", response.getCode()));

            Optional<MeterInfoDescriptor> deviceInfo = this.parseDeviceInfoSenml(response.getResponseText());
            if (deviceInfo.isEmpty())
                throw new Exception("device has provided invalid or no information!");

            return deviceInfo.get();
        } catch (Exception e) {
            throw new Exception(String.format("Unable to retrieve device-info! -> %s", e.getLocalizedMessage()), e);
        }
    }

    private void observeMeterConsumption(String meterConsumptionResourceURI, String meterType) {
        logger.info("Start observing {}...", meterConsumptionResourceURI);
        CoapClient client = new CoapClient(meterConsumptionResourceURI);
        client.observe(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse coapResponse) {
                logger.info("New data received from {}: {}", meterConsumptionResourceURI, coapResponse.getResponseText());

                // Parse SenML
                Optional<SenMLPack> senml = Utils.parseSenmlString(coapResponse.getResponseText(), mapper);
                if (senml.isPresent() && senml.get().size() > 0) {
                    SenMLRecord data = senml.get().get(0);
                    String meterID = data.getBn().replace(":consumption", "");

                    if (lastReceived.containsKey(meterID)) {
                        Double maxConsumption = 9999.0;
                        switch (meterType) {
                            case "energy": maxConsumption = configuration.getEnergyConsumptionThreshold(); break;
                            case "water": maxConsumption = configuration.getWaterConsumptionThreshold(); break;
                            case "gas": maxConsumption = configuration.getGasConsumptionThreshold(); break;
                        }

                        if (((Double)data.getV() - (Double)lastReceived.get(meterID)) > maxConsumption) {
                            logger.warn("Meter {}:{} has exceeded consumption policy limit!", meterType, meterID);
                            switchOffMeter(meterID);
                        }

                        // Save received value
                        lastReceived.replace(meterID, (Double)data.getV());
                    } else {
                        // First data received from this meter, save value and do nothing
                        lastReceived.put(meterID, (Double)data.getV());
                    }
                } else {
                    logger.error("Invalid data received!");
                }
            }

            @Override
            public void onError() {
                logger.error("Unable to observe {}", meterConsumptionResourceURI);
            }
        }, MediaTypeRegistry.APPLICATION_SENML_JSON);
    }

    private void switchOffMeter(String meterID) {
        String meterSwitchURL = ((CoapActuatorProxyResource)this.observedMetersList.getChild(meterID).getChild("state")).getTargetURI();
        System.out.println(meterSwitchURL);
        CoapClient client = new CoapClient(meterSwitchURL);
        client.advanced(new CoapHandler() {
            @Override
            public void onLoad(CoapResponse coapResponse) {
                if (coapResponse.isSuccess()) {
                    logger.info("Meter {} switched OFF!", meterID);
                } else {
                    logger.error("Unable to switch OFF meter {} -> Error {}", meterID, coapResponse.getCode());
                }
            }

            @Override
            public void onError() {
                logger.error("Unable to switch OFF meter {} -> Request failed", meterID);
            }
        }, new Request(CoAP.Code.PUT) {{
            setConfirmable(true);
            setPayload(Boolean.toString(false));
        }});
    }

    private Optional<MeterInfoDescriptor> parseDeviceInfoSenml (String payload) {
        try {
            Optional<SenMLPack> senMLPack = Utils.parseSenmlString(payload, this.mapper);
            if (senMLPack.isEmpty())
                throw new Exception();

            MeterInfoDescriptor deviceInfo = new MeterInfoDescriptor();
            senMLPack.get().forEach(senMLRecord -> {
                if (senMLRecord.getBn() != null) {
                    deviceInfo.setMeterVersion(senMLRecord.getBver());
                } else {
                    switch (senMLRecord.getN()) {
                        case "meter-type":
                            deviceInfo.setMeterType(senMLRecord.getVs());
                            break;
                        case "meter-id":
                            deviceInfo.setMeterID(senMLRecord.getVs());
                            break;
                    }
                }
            });

            return Optional.of(deviceInfo);
        } catch (Exception e) {
            logger.error("Unable to parse device information");
            return Optional.empty();
        }
    }

    public static void main (String[] args) {
        CoapPolicyManager manager = new CoapPolicyManager(5683, new PolicyManagerConfigurationModel(0.5, 2.0, 2.0, new ArrayList<>() {{
            add("coap://localhost:5864");
        }}));

        manager.start();
    }
}
