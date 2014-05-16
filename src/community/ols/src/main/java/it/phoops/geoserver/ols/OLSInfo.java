/* Copyright (c) 2001 - 2013 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols;

import java.util.List;

import org.geoserver.config.ServiceInfo;

/**
 * Configuration object for Open Location Service.
 * 
 * @author aCasini
 * 
 */
public interface OLSInfo extends ServiceInfo {
        public abstract List<OLSServiceProvider> getServiceProvider();
        public abstract void setServiceProvider(OLSServiceProvider provider);
        public void addServiceProvide(OLSServiceProvider provider);
        public OLSServiceProvider findServiceNotActive(OLSAbstractServiceProvider provider, OLSService service);
        public OLSServiceProvider findServiceActive(OLSService service);
}
