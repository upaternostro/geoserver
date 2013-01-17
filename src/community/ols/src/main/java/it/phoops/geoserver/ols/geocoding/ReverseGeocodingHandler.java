package it.phoops.geoserver.ols.geocoding;

import it.phoops.geoserver.ols.OLSException;
import it.phoops.geoserver.ols.OLSHandler;
import it.phoops.geoserver.ols.OLSService;
import it.phoops.geoserver.ols.OLSServiceProvider;

import org.w3c.dom.Document;

public class ReverseGeocodingHandler implements OLSHandler {
    private ReverseGeocodingServiceProvider    provider;

    @Override
    public Document processRequest(Document request) throws OLSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OLSService getService() {
        return OLSService.REVERSE_GEOCODING;
    }

    @Override
    public void setServiceProvider(OLSServiceProvider provider) {
        this.provider = (ReverseGeocodingServiceProvider)provider;
    }
}
