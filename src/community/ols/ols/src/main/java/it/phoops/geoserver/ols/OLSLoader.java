/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols;

import org.geoserver.config.GeoServer;
import org.geoserver.config.util.LegacyServiceLoader;
import org.geoserver.config.util.LegacyServicesReader;

/**
 * Configuration loader for Open Location Service.
 * 
 * @author aCasini
 * 
 */
public class OLSLoader extends LegacyServiceLoader<OLSInfo> {
    @Override
    public Class<OLSInfo> getServiceClass() {
        return OLSInfo.class;
    }

    @Override
    public OLSInfo load(LegacyServicesReader reader, GeoServer gs) throws Exception {
        OLSInfoImpl ols = new OLSInfoImpl();
        ols.setId("ols");

//        Map<String, Object> props = reader.wms();

        return ols;
    }
}
