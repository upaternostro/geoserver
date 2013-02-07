package it.phoops.geoserver.ols.reverse.solr;

import it.phoops.geoserver.ols.OLSAbstractServiceProvider;
import it.phoops.geoserver.ols.OLSException;
import it.phoops.geoserver.ols.OLSService;
import it.phoops.geoserver.ols.geocoding.ReverseGeocodingServiceProvider;
import it.phoops.geoserver.ols.reverse.solr.component.SOLRTabReverse;
import it.phoops.geoserver.ols.reverse.solr.component.SOLRTabReverseFactory;

import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBElement;

import net.opengis.www.xls.AbstractStreetLocatorType;
import net.opengis.www.xls.AddressType;
import net.opengis.www.xls.BuildingLocatorType;
import net.opengis.www.xls.GeocodeResponseList;
import net.opengis.www.xls.GeocodeResponseType;
import net.opengis.www.xls.GeocodedAddressType;
import net.opengis.www.xls.ObjectFactory;
import net.opengis.www.xls.Place;
import net.opengis.www.xls.PointType;
import net.opengis.www.xls.Pos;
import net.opengis.www.xls.PositionType;
import net.opengis.www.xls.ReverseGeocodeRequestType;
import net.opengis.www.xls.Street;
import net.opengis.www.xls.StreetAddress;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.ModifiableSolrParams;
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
	
	@Override
	public boolean isServiceActive() {
		return Boolean.parseBoolean(this.getActive());
	}

	@Override
	public JAXBElement<ReverseGeocodeRequestType> geocode(
			ReverseGeocodeRequestType input) throws OLSException {
		// TODO Auto-generated method stub
	        ObjectFactory                                           of = new ObjectFactory();
	        GeocodeResponseType                                     output = of.createGeocodeResponseType();
	        JAXBElement<GeocodeResponseType>                        retval = of.createGeocodeResponse(output);
	        SolrServer                                              solrServer = new HttpSolrServer(getEndpointAddress());
	        ModifiableSolrParams                                    solrParams = new ModifiableSolrParams();
	        QueryResponse                                           solrResponse;
	        List<GeocodeResponseList>                               responseList = output.getGeocodeResponseLists();
	        GeocodeResponseList                                     listItem;
	        List<Place>                                             places;
	        StreetAddress                                           streetAddress;
	        List<Street>                                            streets;
	        Street                                                  street;
	        JAXBElement<? extends AbstractStreetLocatorType>        streetLocator;
	        BuildingLocatorType                                     buildingLocator;
	        String                                                  buildingNumber;
	        String                                                  municipality;
	        String                                                  countrySecondarySubdivision;
	        List<GeocodedAddressType>                               geocodedAddresses;
	        // IndirizzoRiconosciuto indirizzoRiconosciuto;
	        GeocodedAddressType                                     geocodedAddress;
	        PointType                                               point;
	        Pos                                                     pos;
	        List<Double>                                            coordinates;
	        // DatiGeoreferenziazioneInd datiGeoreferenziazioneInd;
	        AddressType                                             returnAddress;
	        
	        
	        solrParams.set("q", "");
	        PositionType positionType = input.getPosition();
	        if(positionType == null){
	            throw new OLSException("No match Position Type");
	        }
	        PointType pointType = positionType.getPoint();
	        if(pointType == null){
	            throw new OLSException("Point missing in reverse geocode request");
	        }
	        pos = pointType.getPos();
	        coordinates = pos.getValues();

	        SolrQuery query = new SolrQuery("bounding_box:\"Intersects("+coordinates.get(0)+","+coordinates.get(1)+")\"");
	        
	        try {
	            //CAll Solr
	            solrResponse = solrServer.query(query);
	        } catch (SolrServerException e) {
	            throw new OLSException("SOLR error: " + e.getLocalizedMessage(), e);
	        }
	        
		System.out.println("---- Da Implementare il geocode per ReverseGeorouting");
		return null;
	}
}
