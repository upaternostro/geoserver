package it.phoops.geoserver.ols.geocoding;

import it.phoops.geoserver.ols.OLSException;
import it.phoops.geoserver.ols.OLSServiceProviderGUI;

import javax.xml.bind.JAXBElement;

import net.opengis.www.xls.GeocodeRequestType;
import net.opengis.www.xls.GeocodeResponseType;

public interface GeocodingServiceProvider extends OLSServiceProviderGUI {
    public abstract JAXBElement<GeocodeResponseType> geocode(GeocodeRequestType input) throws OLSException;
}
