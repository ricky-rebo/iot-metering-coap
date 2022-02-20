package it.unimore.dipi.iot.metering.server.resources.raw;

import it.unimore.dipi.iot.metering.server.resources.model.PolicyManagerConfigurationModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class PolicyManagerRawConfigParameter extends SmartObjectResource<PolicyManagerConfigurationModel> {
    private static final Logger logger = LoggerFactory.getLogger(PolicyManagerRawConfigParameter.class);

    public static final String RESOURCE_TYPE = "iot.config.policy_manager";

    private PolicyManagerConfigurationModel policy;

    public PolicyManagerRawConfigParameter(PolicyManagerConfigurationModel policy) {
        super(UUID.randomUUID().toString(), RESOURCE_TYPE);
        this.policy = policy;
    }

    @Override
    public PolicyManagerConfigurationModel getValue () {
        return policy;
    }

    @Override
    public void setValue (PolicyManagerConfigurationModel newValue) {
        PolicyManagerConfigurationModel oldValue = this.policy;
        this.policy = newValue;
        this.fireValueChange(oldValue, this.policy);
    }
}
