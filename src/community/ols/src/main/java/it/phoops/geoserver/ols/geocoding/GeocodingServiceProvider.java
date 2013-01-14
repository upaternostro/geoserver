package it.phoops.geoserver.ols.geocoding;

import net.opengis.www.xls.GeocodeRequestType;
import net.opengis.www.xls.GeocodeResponseType;
import it.phoops.geoserver.ols.OLSServiceProvider;

public interface GeocodingServiceProvider extends OLSServiceProvider {

    public abstract GeocodeResponseType geocode(GeocodeRequestType input);

}
