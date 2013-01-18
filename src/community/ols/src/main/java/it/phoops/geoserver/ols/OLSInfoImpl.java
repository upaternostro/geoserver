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
    String prova = "prova";
    String service = "";
    
    @Override
    public OLSServiceProvider getServiceProvider(OLSService service) {
        return servicesProviders.get(service);
    }

    @Override
    public void setServiceProvider(OLSService service, OLSServiceProvider provider) {
    	if(servicesProviders == null)
			servicesProviders = new HashMap<OLSService,OLSServiceProvider>();
        servicesProviders.put(service, provider);
    }

	@Override
	public String getProva() {
		return this.prova;
	}

	@Override
	public void setProva(String prova) {
		this.prova = prova;
	}
	
	@Override
	public String getService() {
		return this.service;
	}

	@Override
	public void setService(String service) {
		this.service = service;
	}
    
}
