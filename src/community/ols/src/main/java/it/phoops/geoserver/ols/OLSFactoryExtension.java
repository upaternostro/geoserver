/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
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
