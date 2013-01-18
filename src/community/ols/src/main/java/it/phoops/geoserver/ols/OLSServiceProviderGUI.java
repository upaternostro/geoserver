package it.phoops.geoserver.ols;

import java.util.Properties;

import org.apache.wicket.extensions.markup.html.tabs.ITab;

public interface OLSServiceProviderGUI extends OLSServiceProvider {
    public abstract OLSService getServiceType();
    public abstract ITab getTab();
    public abstract Boolean setPropertiesTab(ITab iTab);
    public abstract Properties getProperties();
}
