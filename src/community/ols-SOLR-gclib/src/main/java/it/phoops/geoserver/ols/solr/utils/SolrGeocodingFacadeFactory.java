/* Copyright (c) 2001 - 2013 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.solr.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class SolrGeocodingFacadeFactory {
    public static final String  PROPERTY_FILE = "SolrGeocodingFacadeFactory.properties";
    
    public static final String  CLASS_PROPERTY = "it.phoops.geoserver.ols.solr.utils.SolrGeocodingFacadeFactory.class";
    public static final String  CLASS_DEFAULT = "it.phoops.geoserver.ols.solr.utils.SolrGeocodingFacadeImpl";

    private Class<? extends SolrGeocodingFacade>  clazz;
    
    public SolrGeocodingFacadeFactory()
    {
        Properties      p = new Properties();
        InputStream     inStream = getClass().getClassLoader().getResourceAsStream(PROPERTY_FILE);
        
        if (inStream != null) {
            try {
                p.load(inStream);
            } catch (IOException e) {
                // use defaults, Luke ;)
            }
        }
        
        try {
            clazz = (Class<? extends SolrGeocodingFacade>)Class.forName(p.getProperty(CLASS_PROPERTY, CLASS_DEFAULT));
        } catch (ClassNotFoundException | ClassCastException e) {
            clazz = (Class<? extends SolrGeocodingFacade>)SolrGeocodingFacadeImpl.class;
        }
    }
    
    public SolrGeocodingFacade getSolrGeocodingFacade() throws SolrGeocodingFacadeException
    {
        try {
            return clazz.newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            throw new SolrGeocodingFacadeException("Cannot instantiate SolrGeocodingFacade", e);
        }
    }
}
