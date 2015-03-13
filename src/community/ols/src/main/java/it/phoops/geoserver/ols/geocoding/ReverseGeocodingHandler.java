/* Copyright (c) 2001 - 2013 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.geocoding;

import it.phoops.geoserver.ols.OLSException;
import it.phoops.geoserver.ols.OLSHandler;
import it.phoops.geoserver.ols.OLSService;
import it.phoops.geoserver.ols.OLSServiceProvider;

import javax.xml.bind.JAXBElement;

import net.opengis.www.xls.AbstractResponseParametersType;
import net.opengis.www.xls.RequestType;
import net.opengis.www.xls.ReverseGeocodeRequestType;

public class ReverseGeocodingHandler implements OLSHandler {
    private ReverseGeocodingServiceProvider    provider;

    @Override
    public JAXBElement<? extends AbstractResponseParametersType> processRequest(RequestType request, String lang, String srsName) throws OLSException
    {
        if (provider == null) {
            throw new OLSException("No reverse geocoding provider activated, please configure GeoServer correctly!");
        }
        
        return provider.reverseGeocode((ReverseGeocodeRequestType)request.getRequestParameters().getValue(), lang, srsName);
    }

    @Override
    public OLSService getService() {
        return OLSService.REVERSE_GEOCODING;
    }

    @Override
    public void setServiceProvider(OLSServiceProvider provider) {
        this.provider = (ReverseGeocodingServiceProvider)provider;
    }
    
    @Override
    public void setActiveServiceProvider(OLSServiceProvider provider) {
        if (provider != null && provider.isServiceActive()) {
            this.provider = (ReverseGeocodingServiceProvider)provider;
        }
    }
}
