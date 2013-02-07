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
import net.opengis.www.xls.GeocodeResponseList;
import net.opengis.www.xls.GeocodeResponseType;
import net.opengis.www.xls.GeocodedAddressType;
import net.opengis.www.xls.ObjectFactory;
import net.opengis.www.xls.Place;
import net.opengis.www.xls.PointType;
import net.opengis.www.xls.Pos;
import net.opengis.www.xls.Street;
import net.opengis.www.xls.StreetAddress;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.params.ModifiableSolrParams;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.geoserver.config.ServiceInfo;

public class SOLRServiceProvider extends OLSAbstractServiceProvider implements GeocodingServiceProvider {
    public static final String COUNTRY_CODE = "IT";

    // Properties Name
    private static final String PN_ENDPOINT_ADDRESS = "OLS.serviceProvider.geocoding.solr.service.endpointAddress";
    private static final String PN_ACTIVE_SERVICE = "OLS.serviceProvider.service.active";

    private String descriptionKey;
    private Properties properties = new Properties();

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
        String active = ((SOLRTab) getTab()).getActiveSOLR();

        setEndpointAddress(url);
        setActive(active);

    }

    @Override
    public void setPropertiesTab(ITab solrTab) {
        ((SOLRTab) solrTab).setUrlSOLR(this.getEndpointAddress());
        ((SOLRTab) solrTab).setActiveSOLR(this.getActive());
    }

    @Override
    public boolean isServiceActive() {
        return Boolean.parseBoolean(this.getActive());
    }

    @Override
    public JAXBElement<GeocodeResponseType> geocode(GeocodeRequestType input) throws OLSException {
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
        // DatiNormalizzazioneInd datiNormalizzazioneInd;

        solrParams.set("q", "");

        for (AddressType address : input.getAddresses()) {
            // We manage only requests regarding Italy (moreover, Tuscany...)
            if (!COUNTRY_CODE.equalsIgnoreCase(address.getCountryCode())) {
                throw new OLSException("Unsupported country code: " + address.getCountryCode());
            }

            streetAddress = address.getStreetAddress();

            // Pass freeform requests to SOLR
            if (address.getFreeFormAddress() != null && !address.getFreeFormAddress().equals("")) {
                solrParams.set("q", "name:" + address.getFreeFormAddress());
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

                // Check for street name presence (structured data ignored)
                if (street.getValue() == null || street.getValue().equals("")) {
                    throw new OLSException("Street name missing in geocoding request");
                }

                solrParams.set("q", "name:" + street.getValue());
            }

            // // Check for building number (optional)
            // buildingNumber = null;
            // streetLocator = streetAddress.getStreetLocation();
            //
            // if (streetLocator != null) {
            // if (streetLocator.getValue() instanceof BuildingLocatorType) {
            // buildingLocator = (BuildingLocatorType)streetLocator.getValue();
            // buildingNumber = buildingLocator.getNumber();
            //
            // if (buildingLocator.getSubdivision() != null && !buildingLocator.getSubdivision().equals("")) {
            // buildingNumber += "/" + buildingLocator.getSubdivision();
            // }
            // }
            // }

            // Check places: municipality has to be there (at least) (and once, please)
            places = address.getPlaces();

            if (places == null || places.size() < 1) {
                throw new OLSException("Places list missing or empty in geocoding request");
            }

            municipality = null;
            countrySecondarySubdivision = null;

            for (Place place : places) {
                switch (place.getType()) {
                case MUNICIPALITY:
                    if (municipality != null && !municipality.equals("")) {
                        throw new OLSException(
                                "Too many municipalities in geocoding request: old one "
                                        + municipality + " new one: " + place.getValue());
                    }

                    municipality = place.getValue();
                    break;
                case COUNTRY_SECONDARY_SUBDIVISION:
                    if (countrySecondarySubdivision != null
                            && !countrySecondarySubdivision.equals("")) {
                        throw new OLSException(
                                "Too many country secondary subdivisions in geocoding request: old one "
                                        + countrySecondarySubdivision + " new one: "
                                        + place.getValue());
                    }

                    countrySecondarySubdivision = place.getValue();
                    break;
                default:
                    break;
                }
            }

            if (municipality == null || municipality.equals("")) {
                throw new OLSException("Municipality missing or empty in geocoding request");
            }

            try {
                solrResponse = solrServer.query(solrParams);
            } catch (SolrServerException e) {
                throw new OLSException("SOLR error: " + e.getLocalizedMessage(), e);
            }

            // Lazy bind to web service
            // if (binding == null) {
            // try {
            // binding = (MusumeSoapBindingStub) new MusumeServiceLocator().getMusume(new URL(getEndpointAddress()));
            // // Time out after a minute
            // binding.setTimeout(Integer.valueOf(getTimeout()));
            // } catch (ServiceException e) {
            // throw new OLSException("JAX-RPC error: " + e.getLocalizedMessage(), e);
            // } catch (MalformedURLException e) {
            // throw new OLSException("Malformed URL error: " + e.getLocalizedMessage(), e);
            // }
            // }
            //
            // try {
            // // Call RFC59 web service
            // rispostaNormalizzata = binding.richiesta(Algorithm.get(getAlgorithm()).toString(),
            // street.getValue() + (buildingNumber == null ? "" : ", " + buildingNumber), municipality, countrySecondarySubdivision,
            // address.getPostalCode(), DATA_SOURCE.toString());
            //
            // listItem = of.createGeocodeResponseList();
            // listItem.setNumberOfGeocodedAddresses(BigInteger.ZERO);
            // geocodedAddresses = listItem.getGeocodedAddresses();
            //
            // switch (ResponseType.get(rispostaNormalizzata.getTipoRispostaNorm())) {
            // case GEOCODING_OK:
            // listItem.setNumberOfGeocodedAddresses(BigInteger.ONE);
            // indirizzoRiconosciuto = rispostaNormalizzata.getIndirizzoRiconosciuto();
            //
            // geocodedAddress = of.createGeocodedAddressType();
            // point = of.createPointType();
            // pos = of.createPos();
            //
            // pos.setDimension(BigInteger.valueOf(2));
            //
            // coordinates = pos.getValues();
            // datiGeoreferenziazioneInd = indirizzoRiconosciuto.getDatiGeoreferenziazioneInd();
            // coordinates.add(Double.valueOf(datiGeoreferenziazioneInd.getLongitudine()));
            // coordinates.add(Double.valueOf(datiGeoreferenziazioneInd.getLatitudine()));
            //
            // // pos.setSrsName(value);
            //
            // point.setPos(pos);
            // // point.setSrsName(value);
            // // point.setId(value);
            // geocodedAddress.setPoint(point);
            // returnAddress = of.createAddressType();
            // datiNormalizzazioneInd = indirizzoRiconosciuto.getDatiNormalizzazioneInd();
            //
            // returnAddress.setCountryCode(COUNTRY_CODE);
            // places = returnAddress.getPlaces();
            //
            // Place place = of.createPlace();
            //
            // place.setType(NamedPlaceClassification.MUNICIPALITY);
            // place.setValue(datiNormalizzazioneInd.getLocalita());
            //
            // places.add(place);
            //
            // place = of.createPlace();
            //
            // place.setType(NamedPlaceClassification.COUNTRY_SECONDARY_SUBDIVISION);
            // place.setValue(datiNormalizzazioneInd.getProvincia());
            //
            // places.add(place);
            //
            // place = of.createPlace();
            //
            // place.setType(NamedPlaceClassification.COUNTRY_SUBDIVISION);
            // place.setValue("Toscana");
            //
            // places.add(place);
            //
            // returnAddress.setPostalCode(datiNormalizzazioneInd.getCap());
            //
            // streetAddress = of.createStreetAddress();
            // street = of.createStreet();
            //
            // if (datiNormalizzazioneInd.getCivico() != null && !datiNormalizzazioneInd.getCivico().equals("") &&
            // !datiNormalizzazioneInd.getCivico().equals("0")) {
            // street.setValue(datiNormalizzazioneInd.getDug() + " " + datiNormalizzazioneInd.getToponimo() + ", " +
            // datiNormalizzazioneInd.getCivico());
            // } else {
            // street.setValue(datiNormalizzazioneInd.getDug() + " " + datiNormalizzazioneInd.getToponimo());
            // }
            //
            // streetAddress.getStreets().add(street);
            //
            // returnAddress.setStreetAddress(streetAddress);
            //
            // geocodedAddress.setAddress(returnAddress);
            //
            // geocodeMatchCode = of.createGeocodeMatchCode();
            // geocodeMatchCode.setMatchType("RFC59-" + getAlgorithm());
            // geocodeMatchCode.setAccuracy(new Float(1));
            //
            // geocodedAddress.setGeocodeMatchCode(geocodeMatchCode);
            // geocodedAddresses.add(geocodedAddress);
            // break;
            // case UNKNOWN_ADDRESS:
            // break;
            // case UNKNOWN_MUNICIPALITY:
            // break;
            // case INPUT_PARAMETER_ERROR:
            // throw new OLSException("Parameter error");
            // case AMBIGUOUS_MUNICIPALITY:
            // // rispostaNormalizzata.getLocalitaAmbigua().getAmbiguitaLoc().getAmbiguitaLocItem()[0].get;
            // break;
            // case AMBIGUOUS_ADDRESS:
            // listItem.setNumberOfGeocodedAddresses(BigInteger.valueOf(rispostaNormalizzata.getIndirizzoAmbiguo().getAmbiguitaInd().getAmbiguitaIndItemCount()));
            //
            // for (AmbiguitaIndItem ambiguitaInd : rispostaNormalizzata.getIndirizzoAmbiguo().getAmbiguitaInd().getAmbiguitaIndItem()) {
            // geocodedAddress = of.createGeocodedAddressType();
            // point = of.createPointType();
            // pos = of.createPos();
            //
            // pos.setDimension(BigInteger.valueOf(2));
            //
            // coordinates = pos.getValues();
            // coordinates.add(Double.valueOf(ambiguitaInd.getX()));
            // coordinates.add(Double.valueOf(ambiguitaInd.getY()));
            //
            // // pos.setSrsName(value);
            //
            // point.setPos(pos);
            // // point.setSrsName(value);
            // // point.setId(value);
            // geocodedAddress.setPoint(point);
            // returnAddress = of.createAddressType();
            //
            // returnAddress.setCountryCode(COUNTRY_CODE);
            //
            // streetAddress = of.createStreetAddress();
            // street = of.createStreet();
            //
            // if (ambiguitaInd.getCivico() != null && !ambiguitaInd.getCivico().equals("")) {
            // street.setValue(ambiguitaInd.getIndirizzo() + ", " + ambiguitaInd.getCivico());
            // } else {
            // street.setValue(ambiguitaInd.getIndirizzo());
            // }
            //
            // streetAddress.getStreets().add(street);
            //
            // returnAddress.setStreetAddress(streetAddress);
            //
            // geocodedAddress.setAddress(returnAddress);
            //
            // geocodeMatchCode = of.createGeocodeMatchCode();
            // geocodeMatchCode.setMatchType("RFC59-" + getAlgorithm());
            // geocodeMatchCode.setAccuracy(new Float(0));
            //
            // geocodedAddress.setGeocodeMatchCode(geocodeMatchCode);
            // geocodedAddresses.add(geocodedAddress);
            // }
            // break;
            // case MUNICIPALITY_DATA_ONLY:
            // datiNormalizzazioneLoc = rispostaNormalizzata.getLocalitaNormalizzata().getDatiNormalizzazioneLoc();
            // listItem.setNumberOfGeocodedAddresses(BigInteger.ONE);
            // geocodedAddress = of.createGeocodedAddressType();
            // point = of.createPointType();
            // pos = of.createPos();
            //
            // pos.setDimension(BigInteger.valueOf(2));
            //
            // coordinates = pos.getValues();
            // coordinates.add(Double.valueOf(datiNormalizzazioneLoc.getXSezioneIstat()));
            // coordinates.add(Double.valueOf(datiNormalizzazioneLoc.getYSezioneIstat()));
            //
            // // pos.setSrsName(value);
            //
            // point.setPos(pos);
            // // point.setSrsName(value);
            // // point.setId(value);
            // geocodedAddress.setPoint(point);
            // returnAddress = of.createAddressType();
            //
            // returnAddress.setCountryCode(COUNTRY_CODE);
            // places = returnAddress.getPlaces();
            //
            // place = of.createPlace();
            //
            // place.setType(NamedPlaceClassification.MUNICIPALITY_SUBDIVISION);
            // place.setValue(datiNormalizzazioneLoc.getFrazione());
            //
            // places.add(place);
            //
            // place = of.createPlace();
            //
            // place.setType(NamedPlaceClassification.MUNICIPALITY);
            // place.setValue(datiNormalizzazioneLoc.getLocalita());
            //
            // places.add(place);
            //
            // place = of.createPlace();
            //
            // place.setType(NamedPlaceClassification.COUNTRY_SECONDARY_SUBDIVISION);
            // place.setValue(datiNormalizzazioneLoc.getProvincia());
            //
            // places.add(place);
            //
            // place = of.createPlace();
            //
            // place.setType(NamedPlaceClassification.COUNTRY_SUBDIVISION);
            // place.setValue("Toscana");
            //
            // places.add(place);
            //
            // returnAddress.setPostalCode(datiNormalizzazioneLoc.getCap());
            //
            // geocodedAddress.setAddress(returnAddress);
            //
            // geocodeMatchCode = of.createGeocodeMatchCode();
            // geocodeMatchCode.setMatchType("RFC59-" + getAlgorithm());
            // geocodeMatchCode.setAccuracy(new Float(1));
            //
            // geocodedAddress.setGeocodeMatchCode(geocodeMatchCode);
            // geocodedAddresses.add(geocodedAddress);
            // break;
            // case DATA_ACCESS_ERROR:
            // throw new OLSException("Data access error");
            // }
            //
            // responseList.add(listItem);
            // } catch (RemoteException e) {
            // throw new OLSException("Remote error: " + e.getLocalizedMessage(), e);
            // }
        }
        return retval;
    }
}
