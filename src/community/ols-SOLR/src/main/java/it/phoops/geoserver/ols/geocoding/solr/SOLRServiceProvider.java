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

import net.opengis.www.xls.AbstractStreetLocatorType;
import net.opengis.www.xls.AddressType;
import net.opengis.www.xls.BuildingLocatorType;
import net.opengis.www.xls.GeocodeRequestType;
import net.opengis.www.xls.GeocodeResponseType;
import net.opengis.www.xls.Place;
import net.opengis.www.xls.Street;
import net.opengis.www.xls.StreetAddress;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.geoserver.config.ServiceInfo;

public class SOLRServiceProvider extends OLSAbstractServiceProvider implements GeocodingServiceProvider{
	//Properties Name
	private static final String  PN_ENDPOINT_ADDRESS = "OLS.serviceProvider.geocoding.solr.service.endpointAddress";
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
        public boolean isServiceActive() {
            return Boolean.parseBoolean(this.getActive());
        }

	@Override
	public JAXBElement<GeocodeResponseType> geocode(GeocodeRequestType input)
			throws OLSException {
		// TODO Auto-generated method stub
	        System.out.println("Call geocode - SOLRServiceProvider");
	        StreetAddress                                           streetAddress;
	        List<Street>                                            streets;
	        Street                                                  street;
	        String                                                  buildingNumber;
	        JAXBElement<? extends AbstractStreetLocatorType>        streetLocator;
	        BuildingLocatorType                                     buildingLocator;
	        List<Place>                                             places;
	        String                                                  municipality;
	        String                                                  countrySecondarySubdivision;
	        
	        
	        for (AddressType address : input.getAddresses()) {
	            // We cannot parse freeform requests in this backend
	            if (address.getFreeFormAddress() != null && !address.getFreeFormAddress().equals("")) {
	                throw new OLSException("Cannot parse free form requests");
	            }
	            
	            // check for structured address presence
	            streetAddress = address.getStreetAddress();
	            
	            if (streetAddress == null) {
	                throw new OLSException("StreetAddress missing in geocoding request");
	            }
	            
	            // Check for streets presence
	            streets = streetAddress.getStreets();
	            
	            if (streets == null || streets.size() < 1) {
	                throw new OLSException("Streets list missing or empty in geocoding request");
	            }
	            
	            // Only first street is handled here
	            if (streets.size() > 1) {
	                throw new OLSException("Cannot manage street crossing");
	            }
	            
	            street = streets.get(0);
	            
	            if (street == null) {
	                throw new OLSException("Street missing in geocoding request");
	            }
	            
	            // Check for street name presence (structured data ignored)
	            if (street.getValue() == null || street.getValue().equals("")) {
	                throw new OLSException("Street name missing in geocoding request");
	            }
	            
	         // Check for building number (optional)
	            buildingNumber = null;
	            streetLocator = streetAddress.getStreetLocation();
	            
	            if (streetLocator != null) {
	                if (streetLocator.getValue() instanceof BuildingLocatorType) {
	                    buildingLocator = (BuildingLocatorType)streetLocator.getValue();
	                    buildingNumber = buildingLocator.getNumber();
	                    
	                    if (buildingLocator.getSubdivision() != null && !buildingLocator.getSubdivision().equals("")) {
	                        buildingNumber += "/" + buildingLocator.getSubdivision();
	                    }
	                }
	            }
	            
	         // Check places: municipality has to be there (at least) (and once, please)
	            places = address.getPlaces();
	            
	            if (places == null || places.size() < 1) {
	                throw new OLSException("Places list missing or empty in geocoding request");
	            }
	            
	            municipality = null;
	            countrySecondarySubdivision = null;
	            
	            for (Place place : places) {
	                //TODO: implementation
	            }
                }
		return null;
	}
	

}
