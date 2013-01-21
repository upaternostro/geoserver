package it.phoops.geoserver.ols;


import java.util.List;
import java.util.Properties;

import org.geoserver.config.ConfigurationListener;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.config.LoggingInfo;
import org.geoserver.config.ServiceInfo;
import org.geoserver.config.SettingsInfo;

public abstract class OLSAbstractServiceProvider implements OLSServiceProvider, OLSServiceProviderGUI, ConfigurationListener {
	private String descriptionKey;
	protected Properties properties = new Properties();

	public OLSAbstractServiceProvider() {
		super();
        OLS ols = OLS.get();
        ols.getGeoServer().addListener(this);
	}

	@Override
	public String getDescriptionKey() {
	    return descriptionKey;
	}

	public void setDescriptionKey(String description) {
	    this.descriptionKey = description;
	}

	@Override
	public Properties getProperties() {
	    return properties;
	}

	@Override
	public void handleGlobalChange(GeoServerInfo global, List<String> propertyNames, List<Object> oldValues,
			List<Object> newValues) {
			}

	@Override
	public void handlePostGlobalChange(GeoServerInfo global) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleSettingsAdded(SettingsInfo settings) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleSettingsModified(SettingsInfo settings, List<String> propertyNames,
			List<Object> oldValues, List<Object> newValues) {
				// TODO Auto-generated method stub
				
			}

	@Override
	public void handleSettingsPostModified(SettingsInfo settings) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleSettingsRemoved(SettingsInfo settings) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handleLoggingChange(LoggingInfo logging, List<String> propertyNames, List<Object> oldValues,
			List<Object> newValues) {
				// TODO Auto-generated method stub
				
			}

	@Override
	public void handlePostLoggingChange(LoggingInfo logging) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void handlePostServiceChange(ServiceInfo service) {
		
	}

	@Override
	public void handleServiceRemove(ServiceInfo service) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void reloaded() {
		// TODO Auto-generated method stub
		
	}

}