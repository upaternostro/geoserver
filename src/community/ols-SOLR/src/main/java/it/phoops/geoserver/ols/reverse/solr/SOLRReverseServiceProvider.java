package it.phoops.geoserver.ols.reverse.solr;

import it.phoops.geoserver.ols.OLSAbstractServiceProvider;
import it.phoops.geoserver.ols.OLSService;
import it.phoops.geoserver.ols.geocoding.ReverseGeocodingServiceProvider;
import it.phoops.geoserver.ols.reverse.solr.component.SOLRTabReverse;
import it.phoops.geoserver.ols.reverse.solr.component.SOLRTabReverseFactory;

import java.util.List;
import java.util.Properties;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.geoserver.config.ServiceInfo;

public class SOLRReverseServiceProvider extends OLSAbstractServiceProvider implements ReverseGeocodingServiceProvider{
	//property name
	private static final String  PN_ENDPOINT_ADDRESS = "OLS.serviceProvider.reverse.solr.service.endpointAddress";
	private static final String  PN_ACTIVE_SERVICE = "OLS.serviceProvider.service.active";
	
	private String      descriptionKey;
	private Properties  properties = new Properties();
	
	@Override
    public String getDescriptionKey() {
        return descriptionKey;
    }
    
    public void setDescriptionKey(String description) {
        this.descriptionKey = description;
    }
    
    public String getEndpointAddress() {
        return properties.getProperty(PN_ENDPOINT_ADDRESS);
    }

    public void setEndpointAddress(String endpointAddress) {
        properties.setProperty(PN_ENDPOINT_ADDRESS, endpointAddress);
    }
    
    public String getActive(){
        return properties.getProperty(PN_ACTIVE_SERVICE);
    }
    
    public void setActive(String activeService){
        properties.setProperty(PN_ACTIVE_SERVICE, activeService);
    }
	
	@Override
    public Properties getProperties() {
        return properties;
    }
	
	@Override
	public OLSService getServiceType() {
		return OLSService.REVERSE_GEOCODING;
	}

	@Override
	public ITab getTab() {
		IModel<String> title = new ResourceModel("SOLR ", "SOLR");
	    return SOLRTabReverseFactory.getSOLRTabReverseFactory().getSOLRTabReverse(title);
	}

	@Override
	public void setPropertiesTab(ITab solrTabReverse) {
		((SOLRTabReverse)solrTabReverse).setUrlSOLRReverse(this.getEndpointAddress());
	    ((SOLRTabReverse)solrTabReverse).setActiveSOLRReverse(this.getActive());
	}

	@Override
	public void handleServiceChange(ServiceInfo service,
			List<String> propertyNames, List<Object> oldValues,
			List<Object> newValues) {
        String url = ((SOLRTabReverse)getTab()).getUrlSOLRReverse();
        String active = ((SOLRTabReverse)getTab()).getActiveSOLRReverse();
        
        setEndpointAddress(url);
        setActive(active);
	}

}
