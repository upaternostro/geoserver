package it.phoops.geoserver.ols;

import org.geoserver.config.ServiceInfo;

/**
 * Configuration object for Open Location Service.
 * 
 * @author aCasini
 * 
 */
public interface OLSInfo extends ServiceInfo {
    public abstract OLSServiceProvider getServiceProvider(OLSService service);
    public abstract void setServiceProvider(OLSService service, OLSServiceProvider provider);
    public abstract String getProva();
    public abstract void setProva(String prova);
    public abstract String getService();
    public abstract void setService(String service);
    
}
