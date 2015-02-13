/* Copyright (c) 2001 - 2013 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.geocoding.solr;

import it.phoops.geoserver.ols.OLSAbstractServiceProvider;
import it.phoops.geoserver.ols.OLSException;
import it.phoops.geoserver.ols.OLSService;
import it.phoops.geoserver.ols.geocoding.GeocodingServiceProvider;
import it.phoops.geoserver.ols.geocoding.solr.component.SOLRTab;
import it.phoops.geoserver.ols.geocoding.solr.component.SOLRTabFactory;
import it.phoops.geoserver.ols.solr.utils.OLSAddressBean;
import it.phoops.geoserver.ols.solr.utils.SolrBeanResultsList;
import it.phoops.geoserver.ols.solr.utils.SolrGeocodingFacade;
import it.phoops.geoserver.ols.solr.utils.SolrGeocodingFacadeException;
import it.phoops.geoserver.ols.solr.utils.SolrGeocodingFacadeFactory;
import it.phoops.geoserver.ols.util.SRSTransformer;

import java.io.Serializable;
import java.io.StringReader;
import java.math.BigInteger;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBElement;

import net.opengis.www.xls.AbstractStreetLocatorType;
import net.opengis.www.xls.AddressType;
import net.opengis.www.xls.BuildingLocatorType;
import net.opengis.www.xls.GeocodeMatchCode;
import net.opengis.www.xls.GeocodeRequestType;
import net.opengis.www.xls.GeocodeResponseList;
import net.opengis.www.xls.GeocodeResponseType;
import net.opengis.www.xls.GeocodedAddressType;
import net.opengis.www.xls.NamedPlaceClassification;
import net.opengis.www.xls.ObjectFactory;
import net.opengis.www.xls.Place;
import net.opengis.www.xls.PointType;
import net.opengis.www.xls.Pos;
import net.opengis.www.xls.Street;
import net.opengis.www.xls.StreetAddress;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.geoserver.config.ServiceInfo;
import org.geotools.referencing.CRS;
import org.opengis.referencing.FactoryException;
import org.opengis.referencing.NoSuchAuthorityCodeException;
import org.opengis.referencing.crs.CoordinateReferenceSystem;

import com.vividsolutions.jts.geom.Coordinate;
import com.vividsolutions.jts.geom.Geometry;
import com.vividsolutions.jts.io.ParseException;
import com.vividsolutions.jts.io.WKTReader;

public class SOLRServiceProvider extends OLSAbstractServiceProvider implements GeocodingServiceProvider, Serializable
{
    private static final double AMBIGUITY_FACTOR = 2.0;

    private final Log logger = LogFactory.getLog(getClass());

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;

    public static final String COUNTRY_CODE = "IT";

    // Properties Name
    private static final String PN_ENDPOINT_ADDRESS = "OLS.serviceProvider.geocoding.solr.service.endpointAddress";
    private static final String PN_ACTIVE_SERVICE = "OLS.serviceProvider.service.active";
    private static final String PN_CRS = "OLS.serviceProvider.geocoding.solr.crs";

    private String descriptionKey;
    private Properties properties = new Properties();

    private CoordinateReferenceSystem   solrCrs;
    
    private CoordinateReferenceSystem getSOLRCrs() throws OLSException
    {
        CoordinateReferenceSystem       retval = solrCrs;
        
        if (retval == null) {
            synchronized (this) {
                retval = solrCrs;
                
                if (retval == null) {
                    try {
                        retval = solrCrs = CRS.decode(getSolrCrsName());
                    } catch (NoSuchAuthorityCodeException e) {
                        throw new OLSException("Unknown authority in SRS", e);
                    } catch (FactoryException e) {
                        throw new OLSException("Factory exception converting SRS", e);
                    }
                }
            }
        }
        
        return retval;
    }

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

    public String getActive() {
        return properties.getProperty(PN_ACTIVE_SERVICE);
    }

    public void setActive(String activeService) {
        properties.setProperty(PN_ACTIVE_SERVICE, activeService);
    }

    public String getSolrCrsName() {
        return properties.getProperty(PN_CRS);
    }

    public void setSolrCrsName(String solrCrsName) {
        properties.setProperty(PN_CRS, solrCrsName);
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
    public void handleServiceChange(ServiceInfo service, List<String> propertyNames,
            List<Object> oldValues, List<Object> newValues) {
        String url = ((SOLRTab) getTab()).getUrlSOLR();
        String crs = ((SOLRTab) getTab()).getCrsName();
        String active = ((SOLRTab) getTab()).getActiveSOLR();

        setEndpointAddress(url);
        setSolrCrsName(crs);
        setActive(active);

    }

    @Override
    public void setPropertiesTab(ITab solrTab) {
        ((SOLRTab) solrTab).setUrlSOLR(this.getEndpointAddress());
        ((SOLRTab) solrTab).setCrsName(this.getSolrCrsName());
        ((SOLRTab) solrTab).setActiveSOLR(this.getActive());
    }

    @Override
    public boolean isServiceActive() {
        return Boolean.parseBoolean(this.getActive());
    }

    @Override
    public JAXBElement<GeocodeResponseType> geocode(GeocodeRequestType input, String lang, String srsName) throws OLSException {
        ObjectFactory                                           of = new ObjectFactory();
        GeocodeResponseType                                     output = of.createGeocodeResponseType();
        JAXBElement<GeocodeResponseType>                        retval = of.createGeocodeResponse(output);
        SolrBeanResultsList                                     solrDocs;
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
        GeocodedAddressType                                     geocodedAddress;
        PointType                                               point;
        Pos                                                     pos;
        WKTReader                                               wktReader = null;
        StringReader                                            sr;
        Geometry                                                geometry;
        List<Double>                                            coordinates;
        Place                                                   place;
        AddressType                                             returnAddress;
        GeocodeMatchCode                                        geocodeMatchCode;
        String                                                  declaredSrs;
        SolrGeocodingFacadeFactory                              factory = new SolrGeocodingFacadeFactory();
        SolrGeocodingFacade                                     facade;
        String                                                  freeFormAddress;
        String                                                  streetName;
        String                                                  streetType;
        String                                                  numberSubdivision;
        Float                                                   maxScore;
        Float                                                   secondScore;
        
        try {
            facade = factory.getSolrGeocodingFacade();
        } catch (SolrGeocodingFacadeException e) {
            throw new OLSException("Cannot instantiate SolrGeocodingFacade", e);
        }
        
        facade.setSolrServerURL(getEndpointAddress());
        
        if (srsName != null) {
            declaredSrs = srsName;
        } else {
            declaredSrs = "EPSG:4326";
        }
        
        for (AddressType address : input.getAddresses()) {
            freeFormAddress = null;
            streetName = null;
            streetType = null;
            numberSubdivision = null;
            
            // We manage only requests regarding Italy (moreover, Tuscany...)
            if (!COUNTRY_CODE.equalsIgnoreCase(address.getCountryCode())) {
                throw new OLSException("Unsupported country code: " + address.getCountryCode());
            }

            streetAddress = address.getStreetAddress();

            // Pass freeform requests to SOLR
            if (address.getFreeFormAddress() != null && !address.getFreeFormAddress().equals("")) {
                freeFormAddress = address.getFreeFormAddress();
            } else {
                // check for structured address presence
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

                // Check for street name presence
                if (street.getValue() == null || street.getValue().equals("")) {
                    // Use structured data
                    if (street.getOfficialName() == null || street.getOfficialName().equals("")) {
                        throw new OLSException("Street name missing in geocoding request");
                    }
                    
                    streetName = street.getOfficialName();
                    
                    if (street.getTypePrefix() != null && !street.getTypePrefix().equals("")) {
                        streetType = street.getTypePrefix();
                    }
                } else {
                    // Use free form data
                    //check if the street contains a number
//                    String[] stgStreet = street.getValue().split(",");
//                    if(stgStreet.length > 1){
//                        String stgNumeber = stgStreet[1];
//                        try{
//                            Integer.parseInt(stgNumeber.trim());
//                        }catch(NumberFormatException e){
//                            throw new OLSException("Cannot manage street number");
//                        }
//                        solrQuery = "name:\"" + ClientUtils.escapeQueryChars(stgStreet[0]) +"\"" + " AND number: \""+ ClientUtils.escapeQueryChars(stgNumeber.trim()) +"\"";
//                    }else{
                        freeFormAddress = street.getValue();
//                    }
                }
            }

            // Check for building number (optional)
            buildingNumber = null;
            streetLocator = streetAddress.getStreetLocation();

            if (streetLocator != null) {
                if (streetLocator.getValue() instanceof BuildingLocatorType) {
                    buildingLocator = (BuildingLocatorType) streetLocator.getValue();
                    buildingNumber = buildingLocator.getNumber();

                    if (buildingLocator.getSubdivision() != null
                            && !buildingLocator.getSubdivision().equals("")) {
                        numberSubdivision = buildingLocator.getSubdivision();
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

            for (Place pivot : places) {
                switch (pivot.getType()) {
                case MUNICIPALITY:
                    if (municipality != null && !municipality.equals("")) {
                        throw new OLSException(
                                "Too many municipalities in geocoding request: old one "
                                        + municipality + " new one: " + pivot.getValue());
                    }

                    municipality = pivot.getValue();
                    break;
                case COUNTRY_SECONDARY_SUBDIVISION:
                    if (countrySecondarySubdivision != null
                            && !countrySecondarySubdivision.equals("")) {
                        throw new OLSException(
                                "Too many country secondary subdivisions in geocoding request: old one "
                                        + countrySecondarySubdivision + " new one: "
                                        + pivot.getValue());
                    }

                    countrySecondarySubdivision = pivot.getValue();
                    break;
                default:
                    break;
                }
            }
            
            try {
                if (freeFormAddress != null) {
                    if (buildingNumber != null) {
                        solrDocs = facade.geocodeAddress(freeFormAddress, buildingNumber, numberSubdivision, municipality, countrySecondarySubdivision);
                    } else {
                        solrDocs = facade.geocodeAddress(freeFormAddress, municipality, countrySecondarySubdivision);
                    }
                } else {
                    if (buildingNumber != null) {
                        solrDocs = facade.geocodeAddress(streetType, streetName, buildingNumber, numberSubdivision, municipality, countrySecondarySubdivision);
                    } else {
                        solrDocs = facade.geocodeAddress(streetType, streetName, municipality, countrySecondarySubdivision);
                    }
                }

                listItem = of.createGeocodeResponseList();
                listItem.setNumberOfGeocodedAddresses(BigInteger.valueOf(solrDocs.getNumFound()));
                geocodedAddresses = listItem.getGeocodedAddresses();
                
                maxScore = null;
                secondScore = null;
                
                for (OLSAddressBean solrDoc : solrDocs) {
                    if (maxScore == null) {
                        maxScore = solrDoc.getScore();
                        
                        if (solrDocs.size() > 1) {
                            secondScore = solrDocs.get(1).getScore();
                        }
                    }
                    
                    if (wktReader == null) {
                        wktReader = new WKTReader();
                    }
                    
                    sr = new StringReader(solrDoc.getCentroid());
                    
                    try {
                        geometry = wktReader.read(sr);
                        
                        geocodedAddress = of.createGeocodedAddressType();
                        point = of.createPointType();
                        pos = of.createPos();
                        
                        pos.setDimension(BigInteger.valueOf(2));
                        
                        coordinates = pos.getValues();
                        
                        if (!getSolrCrsName().equals(declaredSrs)) {
                            Coordinate  coords = SRSTransformer.transform(geometry.getCoordinate().getOrdinate(0), geometry.getCoordinate().getOrdinate(1), getSOLRCrs(), declaredSrs);
                            
                            coordinates.add(coords.x);
                            coordinates.add(coords.y);
                        } else {
                            coordinates.add(Double.valueOf(geometry.getCoordinate().getOrdinate(0)));
                            coordinates.add(Double.valueOf(geometry.getCoordinate().getOrdinate(1)));
                        }
                        
                        pos.setSrsName(declaredSrs);
                        
                        point.setPos(pos);
//                        point.setSrsName(value);
//                        point.setId(value);
                        geocodedAddress.setPoint(point);
                    } catch (ParseException e) {
                        throw new OLSException("WKT parse error: " + e.getLocalizedMessage(), e);
                    }
                    
                    returnAddress = of.createAddressType();
                    
                    returnAddress.setCountryCode(COUNTRY_CODE);
                    places = returnAddress.getPlaces();
                    
                    if (solrDoc.getMunicipality() != null) {
                        place = of.createPlace();
                        
                        place.setType(NamedPlaceClassification.MUNICIPALITY);
                        place.setValue(solrDoc.getMunicipality());
                        
                        places.add(place);
                    }
                    
                    if (solrDoc.getCountrySubdivision() != null) {
                        place = of.createPlace();
                        
                        place.setType(NamedPlaceClassification.COUNTRY_SECONDARY_SUBDIVISION);
                        place.setValue(solrDoc.getCountrySubdivision());
                        
                        places.add(place);
                    }
                    
                    place = of.createPlace();
                    
                    place.setType(NamedPlaceClassification.COUNTRY_SUBDIVISION);
                    place.setValue("Toscana");
                    
                    places.add(place);
                    
                    // FIXME Manca, aggiungere a schema.xml?
//                  returnAddress.setPostalCode(datiNormalizzazioneInd.getCap());
                    
                    streetAddress = of.createStreetAddress();
                    street = of.createStreet();
                    
                    // building_number
//                    if (solrDoc.getFieldValue("building_number") != null && !solrDoc.getFieldValue("building_number").equals("") && !solrDoc.getFieldValue("building_number").equals("0")) {
//                        street.setValue(solrDoc.getFieldValue("name") + ", " + solrDoc.getFieldValue("building_number"));
//                    } else {
                        street.setValue(solrDoc.getName());
//                    }
                    
                    streetAddress.getStreets().add(street);
                    
                    returnAddress.setStreetAddress(streetAddress);
                    
                    geocodedAddress.setAddress(returnAddress);
                    
                    geocodeMatchCode = of.createGeocodeMatchCode();
                    geocodeMatchCode.setMatchType("SOLR");
                    if (maxScore.equals(secondScore)) {
                        // Ambigous address
                        geocodeMatchCode.setAccuracy(new Float(solrDoc.getScore() / maxScore / AMBIGUITY_FACTOR));
                    } else {
                        geocodeMatchCode.setAccuracy(new Float(solrDoc.getScore() / maxScore));
                    }
                    
                    geocodedAddress.setGeocodeMatchCode(geocodeMatchCode);
                    geocodedAddresses.add(geocodedAddress);
                }

                responseList.add(listItem);
            } catch (SolrGeocodingFacadeException e) {
                throw new OLSException("SOLR error: " + e.getLocalizedMessage(), e);
            }
        }

        return retval;
    }

}
