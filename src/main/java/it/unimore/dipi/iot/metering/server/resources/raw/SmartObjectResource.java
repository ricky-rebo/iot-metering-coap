package it.unimore.dipi.iot.metering.server.resources.raw;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

public  abstract class SmartObjectResource<T> {
    private static final Logger logger = LoggerFactory.getLogger(SmartObjectResource.class);

    protected PropertyChangeSupport propertyChangeSupport;

    private String id;
    private String type;

    SmartObjectResource() {
        this.propertyChangeSupport = new PropertyChangeSupport(this);
    }

    public SmartObjectResource(String id, String type) {
        this();
        this.id = id;
        this.type = type;
        logger.info("Resource {}:{} created!", this.type, this.id);
    }

    // Setters and getters
    public abstract T getValue();
    public abstract void setValue(T newValue);

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    // Listeners management
    public void addValueChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.addPropertyChangeListener("value", listener);
    }

    public void removeValueChangeListener(PropertyChangeListener listener) {
        this.propertyChangeSupport.removePropertyChangeListener("value", listener);
    }

    protected void fireValueChange(T oldValue, T newValue) {
        this.propertyChangeSupport.firePropertyChange("value", oldValue, newValue);
    }

    @Override
    public String toString() {
        return String.format("SmartObjectResource {id='%s', type='%s'}", id, type);
    }
}
