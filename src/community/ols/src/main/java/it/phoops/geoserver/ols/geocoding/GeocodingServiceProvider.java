package it.phoops.geoserver.ols.geocoding;

import javax.xml.bind.JAXBElement;

import net.opengis.www.xls.GeocodeRequestType;
import net.opengis.www.xls.GeocodeResponseType;
import it.phoops.geoserver.ols.OLSServiceProvider;

public interface GeocodingServiceProvider extends OLSServiceProvider {

    public abstract JAXBElement<GeocodeResponseType> geocode(GeocodeRequestType input);

}
