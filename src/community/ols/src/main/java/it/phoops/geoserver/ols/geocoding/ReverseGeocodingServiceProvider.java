package it.phoops.geoserver.ols.geocoding;

import it.phoops.geoserver.ols.OLSException;
import it.phoops.geoserver.ols.OLSServiceProvider;

import javax.xml.bind.JAXBElement;

import net.opengis.www.xls.ReverseGeocodeRequestType;

public interface ReverseGeocodingServiceProvider extends OLSServiceProvider {
	public abstract JAXBElement<ReverseGeocodeRequestType> geocode(ReverseGeocodeRequestType input) throws OLSException;
}
