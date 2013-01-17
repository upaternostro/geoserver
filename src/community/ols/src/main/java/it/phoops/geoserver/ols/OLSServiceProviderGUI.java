package it.phoops.geoserver.ols;

import java.util.Properties;

public interface OLSServiceProviderGUI extends OLSServiceProvider {
    public abstract String getServiceType();
    public abstract Properties getProperties();
}
