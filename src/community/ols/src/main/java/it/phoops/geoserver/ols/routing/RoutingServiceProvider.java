/* Copyright (c) 2001 - 2013 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.routing;

import it.phoops.geoserver.ols.OLSException;
import it.phoops.geoserver.ols.OLSServiceProvider;

import javax.xml.bind.JAXBElement;

import net.opengis.www.xls.DetermineRouteRequestType;
import net.opengis.www.xls.DetermineRouteResponseType;

public interface RoutingServiceProvider extends OLSServiceProvider {
    public abstract JAXBElement<DetermineRouteResponseType> route(DetermineRouteRequestType input, String lang, String srsName) throws OLSException;
}
