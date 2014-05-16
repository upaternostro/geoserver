/* Copyright (c) 2001 - 2013 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols;

public interface OLSServiceProvider {
    public abstract OLSService getServiceType();
    public abstract boolean isServiceActive();
}
