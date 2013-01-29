package it.phoops.geoserver.ols.geocoding.solr;

import it.phoops.geoserver.ols.OLSAbstractServiceProvider;
import it.phoops.geoserver.ols.OLSException;
import it.phoops.geoserver.ols.OLSService;
import it.phoops.geoserver.ols.geocoding.GeocodingServiceProvider;
import it.phoops.geoserver.ols.geocoding.solr.component.SOLRTab;
import it.phoops.geoserver.ols.geocoding.solr.component.SOLRTabFactory;

import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBElement;

import net.opengis.www.xls.GeocodeRequestType;
import net.opengis.www.xls.GeocodeResponseType;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.geoserver.config.ServiceInfo;

public class SOLRServiceProvider extends OLSAbstractServiceProvider implements GeocodingServiceProvider{
	//Properties Name
	private static final String  PN_ENDPOINT_ADDRESS = "OLS.serviceProvider.geocoding.solr.service.endpointAddress";
	private static final String  PN_ACTIVE_SERVICE = "OLS.serviceProvider.geocoding.solr.service.active";
	
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
        public OLSService getServiceType() {
            return OLSService.GEOCODING;
        }
    	
    	@Override
        public Properties getProperties() {
            return properties;
        }

	@Override
	public ITab getTab() {
	    IModel<String> title = new ResourceModel("SOLR ", "SOLR");
	    return SOLRTabFactory.getSOLRTabFactory().getSOLRTab(title);
	}

	@Override
	public void handleServiceChange(ServiceInfo service,
			List<String> propertyNames, List<Object> oldValues,
			List<Object> newValues) {
            String url = ((SOLRTab)getTab()).getUrlSOLR();
            String active = ((SOLRTab)getTab()).getActiveSOLR();
            
            setEndpointAddress(url);
            setActive(active);
		
	}

	@Override
	public void setPropertiesTab(ITab solrTab) {
	    ((SOLRTab)solrTab).setUrlSOLR(this.getEndpointAddress());
	    ((SOLRTab)solrTab).setActiveSOLR(this.getActive());
	}

	@Override
	public JAXBElement<GeocodeResponseType> geocode(GeocodeRequestType input)
			throws OLSException {
		// TODO Auto-generated method stub
	        System.out.println("Call geocode - SOLRServiceProvider");
	        
		return null;
	}
	

}
