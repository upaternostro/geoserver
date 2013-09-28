package it.phoops.geoserver.ols.routing;

import it.phoops.geoserver.ols.OLSException;
import it.phoops.geoserver.ols.OLSHandler;
import it.phoops.geoserver.ols.OLSService;
import it.phoops.geoserver.ols.OLSServiceProvider;

import javax.xml.bind.JAXBElement;

import net.opengis.www.xls.AbstractResponseParametersType;
import net.opengis.www.xls.DetermineRouteRequestType;
import net.opengis.www.xls.RequestType;

public class RoutingHandler implements OLSHandler {
    private RoutingServiceProvider    provider;

    @Override
    public JAXBElement<? extends AbstractResponseParametersType> processRequest(RequestType request, String lang, String srsName) throws OLSException
    {
        return provider.route((DetermineRouteRequestType)request.getRequestParameters().getValue(), lang, srsName);
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
