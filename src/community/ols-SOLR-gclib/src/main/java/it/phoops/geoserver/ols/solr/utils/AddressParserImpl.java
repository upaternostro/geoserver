/* Copyright (c) 2001 - 2013 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.solr.utils;

import java.util.ArrayList;

public class AddressParserImpl implements AddressParser
{
    private ArrayList<String>   streetTypes;
    
    private String              numberDelimiter;
    private boolean             numberAfterAddress;
    
    private String              streetType;
    private String              streetName;
    private String              number;

    public AddressParserImpl()
    {
        streetTypes = null;
        
        numberDelimiter = ",";
        numberAfterAddress = true;
        
        streetType = null;
        streetName = null;
        number = null;
    }

    @Override
    public void setSolrServerURL(String solrUrl) throws SolrGeocodingFacadeException {
        //elenco tutti i dug
        SolrStreetTypesInspector streetTypesInspector = new SolrStreetTypesInspector(solrUrl);
        
        streetTypes = streetTypesInspector.getDistinctStreetTypes();
    }

    @Override
    public void setNumberDelimiter(String numberDelimiter) {
        this.numberDelimiter = numberDelimiter;
    }
    
    @Override
    public void setNumberAfterAddress(boolean numberAfterAddress) {
        this.numberAfterAddress = numberAfterAddress;
    }
    
    @Override
    public void setAddress(String freeformString) throws SolrGeocodingFacadeException
    {
        streetType = null;
        streetName = null;
        number = null;

        //ricerco il numero
        int     numberDelimiterIndex = numberAfterAddress ? freeformString.lastIndexOf(numberDelimiter) : freeformString.indexOf(numberDelimiter);
        
        if (numberDelimiterIndex != -1) {
            if (numberAfterAddress) {
                number = freeformString.substring(numberDelimiterIndex+1);
                freeformString = freeformString.substring(0, numberDelimiterIndex);
            } else {
                number = freeformString.substring(0, numberDelimiterIndex);
                freeformString = freeformString.substring(numberDelimiterIndex+1);
            }
            number = number.trim();
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
