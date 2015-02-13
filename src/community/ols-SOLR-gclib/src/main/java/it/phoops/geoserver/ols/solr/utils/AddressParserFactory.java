/* Copyright (c) 2001 - 2013 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.solr.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class AddressParserFactory {
    public static final String  PROPERTY_FILE = "AddressParserFactory.properties";
    
    public static final String  CLASS_PROPERTY = "it.phoops.geoserver.ols.solr.utils.AddressParserFactory.class";
    public static final String  CLASS_DEFAULT = "it.phoops.geoserver.ols.solr.utils.AddressParserImpl";

    private Class<? extends AddressParserImpl>  clazz;
    
    public AddressParserFactory()
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
            clazz = (Class<? extends AddressParserImpl>)Class.forName(p.getProperty(CLASS_PROPERTY, CLASS_DEFAULT));
        } catch (ClassNotFoundException e) {
            clazz = (Class<? extends AddressParserImpl>)AddressParserImpl.class;
        } catch (ClassCastException e) {
            clazz = (Class<? extends AddressParserImpl>)AddressParserImpl.class;
        }
    }
    
    public AddressParser getSolrGeocodingFacade(String solrUrl) throws SolrGeocodingFacadeException
    {
        AddressParser   retval = null;
        
        try {
            retval = clazz.newInstance();
            retval.setSolrServerURL(solrUrl);
        } catch (InstantiationException  e) {
            throw new SolrGeocodingFacadeException("Cannot instantiate AddressParser", e);
        } catch (IllegalAccessException e) {
            throw new SolrGeocodingFacadeException("Cannot instantiate AddressParser", e);
        }
        
        return retval;
    }
}
