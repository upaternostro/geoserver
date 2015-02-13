/* Copyright (c) 2001 - 2013 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.solr.utils;

public interface AddressParser {
    public abstract void setSolrServerURL(String solrUrl) throws SolrGeocodingFacadeException;
    
    public abstract void setAddress(String freeformString) throws SolrGeocodingFacadeException;
    
    public abstract String getStreetName();
    public abstract String getStreetType();
    public abstract String getNumber();
    
    public abstract void resetStreetType();
}