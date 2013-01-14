package it.phoops.geoserver.ols;

import org.geoserver.config.ServiceFactoryExtension;

/**
 * The Factory Extension for the @OLSInfoImpl
 * 
 * @author aCasini
 * 
 */
public class OLSFactoryExtension extends ServiceFactoryExtension<OLSInfo> {
    public OLSFactoryExtension() {
        super(OLSInfo.class);
    }

    @Override
    @SuppressWarnings("unchecked")
    public <T> T create(Class<T> clazz) {
        return (T) new OLSInfoImpl();
    }
}
