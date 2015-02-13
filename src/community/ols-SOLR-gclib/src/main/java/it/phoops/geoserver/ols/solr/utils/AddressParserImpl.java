/* Copyright (c) 2001 - 2013 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.solr.utils;

import java.util.ArrayList;

public class AddressParserImpl implements AddressParser
{
    private ArrayList<String>   streetTypes;
    
    private String              streetType;
    private String              streetName;
    private String              number;

    public AddressParserImpl()
    {
        streetTypes = null;
        
        streetType = null;
        streetName = null;
        number = null;
    }

    @Override
    public void setSolrServerURL(String solrUrl) throws SolrGeocodingFacadeException {
        //elenco tutti i dug
        SolrDugInspector dugInspector = new SolrDugInspector(solrUrl);
        streetTypes = dugInspector.distinctDugs();
    }

    @Override
    public void setAddress(String freeformString) throws SolrGeocodingFacadeException
    {
        streetType = null;
        streetName = null;
        number = null;

        //ricerco il numero
        int     numberDelimiterIndex = freeformString.indexOf(",");
        if (numberDelimiterIndex != -1) {
            number = freeformString.substring(numberDelimiterIndex+1);
            number = number.trim();
            freeformString = freeformString.substring(0, numberDelimiterIndex);
        }

        if (freeformString != null) {
            freeformString = freeformString.toUpperCase();
            
            if (streetTypes != null && streetTypes.size() > 0) {
                // cerco DUG all'interno della stringa e se trovo, lo cancello
                for (String d : streetTypes) {
                    String regExp = ".*\\b" + d + "\\b.*";
                    
                    if (freeformString.matches(regExp)) {
                        freeformString = freeformString.replaceAll("\\b" + d + "\\b", "");
                        streetName = freeformString;
                        streetType = d;
                        break;
                    }
                }
            }
        }
        
        if (streetName == null) {
            streetName = freeformString;
        }
    }

    @Override
    public String getStreetName() {
        return streetName;
    }

    @Override
    public String getStreetType() {
        return streetType;
    }

    @Override
    public String getNumber() {
        return number;
    }

    @Override
    public void resetStreetType() {
        streetType = null;
    }
}
