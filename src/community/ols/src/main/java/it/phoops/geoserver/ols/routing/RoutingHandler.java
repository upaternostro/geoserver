package it.phoops.geoserver.ols.routing;

import it.phoops.geoserver.ols.OLSException;
import it.phoops.geoserver.ols.OLSHandler;
import it.phoops.geoserver.ols.OLSService;
import it.phoops.geoserver.ols.OLSServiceProvider;
import it.phoops.geoserver.ols.geocoding.ReverseGeocodingServiceProvider;

import org.w3c.dom.Document;

public class RoutingHandler implements OLSHandler {
    private RoutingServiceProvider    provider;

    @Override
    public Document processRequest(Document request) throws OLSException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public OLSService getService() {
        return OLSService.ROUTING_NAVIGATION;
    }

    @Override
    public void setServiceProvider(OLSServiceProvider provider) {
        this.provider = (RoutingServiceProvider)provider;
    }
    
    @Override
    public void setActiveServiceProvider(OLSServiceProvider provider) {
        if(provider.isServiceActive())
            this.provider = (RoutingServiceProvider)provider;
    }
}
