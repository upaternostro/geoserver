package it.phoops.geoserver.ols;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    private List<OLSServiceProvider>  servicesProviders = new ArrayList<OLSServiceProvider>();
    
    @Override
    public List<OLSServiceProvider> getServiceProvider() {
    	if(servicesProviders == null)
			servicesProviders = new ArrayList<OLSServiceProvider>();
    	return servicesProviders;
    }

    @Override
    public void setServiceProvider(OLSServiceProvider provider) {
    	if(servicesProviders == null)
			servicesProviders = new ArrayList<OLSServiceProvider>();
        servicesProviders.add(provider);
    }
    
    @Override
    public void addServiceProvide(OLSServiceProvider provider){
        servicesProviders.add(provider);
    }
}
