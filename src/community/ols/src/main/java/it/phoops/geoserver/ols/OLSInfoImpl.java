package it.phoops.geoserver.ols;

import java.util.HashMap;
import java.util.Map;

import org.geoserver.config.impl.ServiceInfoImpl;

/**
 * 
 * Default implementation for the {@link OLSInfo} bean.
 * 
 * @author aCasini
 * 
 */
public class OLSInfoImpl extends ServiceInfoImpl implements OLSInfo {
    private Map<OLSService,OLSServiceProvider>  servicesProviders = new HashMap<OLSService,OLSServiceProvider>();

    @Override
    public OLSServiceProvider getServiceProvider(OLSService service) {
        return servicesProviders.get(service);
    }

    @Override
    public void setServiceProvider(OLSService service, OLSServiceProvider provider) {
        servicesProviders.put(service, provider);
    }
}
