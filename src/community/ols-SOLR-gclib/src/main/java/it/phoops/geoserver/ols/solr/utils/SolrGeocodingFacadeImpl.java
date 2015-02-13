/* Copyright (c) 2001 - 2013 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.solr.utils;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.StringTokenizer;

import org.apache.http.client.HttpClient;
import org.apache.solr.client.solrj.ResponseParser;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.CloudSolrServer;
import org.apache.solr.client.solrj.impl.ConcurrentUpdateSolrServer;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.impl.LBHttpSolrServer;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;

public class SolrGeocodingFacadeImpl implements SolrGeocodingFacade {
    private static final int    MAX_DOCS_PER_REQUEST    = 100;
    
    private SolrServer  solrServer;
    private String      baseUrl;
    private String      numberDelimiter;
    private boolean     numberAfterAddress;
    private String      addressTokenDelim;
    private float       streetTypeWeigth;
    private float       streetNameWeigth;
    private String      numberSubdivisionSeparator;
    private float       numberWeigth;
    private float       municipalityWeigth;
    private float       countrySubdivisionWeigth;
    private int         maxRows;
    private boolean     fuzzySearchStreetType;
    private boolean     fuzzySearchStreetName;
    private boolean     fuzzySearchNumber;
    private boolean     fuzzySearchMunicipality;
    private boolean     fuzzySearchCountrySubdivision;
    private boolean     andNameTerms;
    private boolean     ignoreDug;

    public SolrGeocodingFacadeImpl() {
        super();
        
        solrServer = null;
        baseUrl = null;
        
        numberDelimiter = ",";
        numberAfterAddress = true;
        addressTokenDelim = null;
        streetTypeWeigth = 3.0f;
        streetNameWeigth = 10.0f;
        numberSubdivisionSeparator = "/";
        numberWeigth = 5.0f;
        municipalityWeigth = 3.0f;
        countrySubdivisionWeigth = 1.0f;
        maxRows = MAX_ROWS_SOLR_DEFAULT;
        fuzzySearchStreetType = false;
        fuzzySearchStreetName = false;
        fuzzySearchNumber = false;
        fuzzySearchMunicipality = false;
        fuzzySearchCountrySubdivision = false;
        andNameTerms = true;
        ignoreDug = false;
    }
    
    @Override
    public void setSolrServerURL(String baseURL) {
        this.solrServer = new HttpSolrServer(baseURL);
        this.baseUrl = baseURL;
    }
    
    @Override
    public void setSolrServerURL(String baseURL, HttpClient client) {
        solrServer = new HttpSolrServer(baseURL, client);
    }
    
    @Override
    public void setSolrServerURL(String baseURL, HttpClient client, ResponseParser parser) {
        solrServer = new HttpSolrServer(baseURL, client, parser);
    }
    
    @Override
    public void setSolrServerHost(String zkHost) throws SolrGeocodingFacadeException {
        try {
            solrServer = new CloudSolrServer(zkHost);
        } catch (MalformedURLException e) {
            throw new SolrGeocodingFacadeException("Cannot instantiate SolrServer", e);
        }
    }
    
    @Override
    public void setSolrServerHost(String zkHost, LBHttpSolrServer lbServer) {
        solrServer = new CloudSolrServer(zkHost, lbServer);
    }
    
    @Override
    public void setSolrServerHost(String zkHost, LBHttpSolrServer lbServer, boolean updatesToLeaders) {
        solrServer = new CloudSolrServer(zkHost, lbServer, updatesToLeaders);
    }
    
    @Override
    public void setSolrServerUrl(String solrServerUrl, int queueSize, int threadCount) {
        solrServer = new ConcurrentUpdateSolrServer(solrServerUrl, queueSize, threadCount);
    }
    
    @Override
    public void setSolrServerUrl(String solrServerUrl, HttpClient client, int queueSize, int threadCount) {
        solrServer = new ConcurrentUpdateSolrServer(solrServerUrl, client, queueSize, threadCount);
    }
    
    @Override
    public void setSolrServerUrls(String... solrServerUrls) throws SolrGeocodingFacadeException {
        try {
            solrServer = new LBHttpSolrServer(solrServerUrls);
        } catch (MalformedURLException e) {
            throw new SolrGeocodingFacadeException("Cannot instantiate SolrServer", e);
        }
    }
    
    @Override
    public void setSolrServerUrls(HttpClient httpClient, String... solrServerUrls) throws SolrGeocodingFacadeException {
        try {
            solrServer = new LBHttpSolrServer(httpClient, solrServerUrls);
        } catch (MalformedURLException e) {
            throw new SolrGeocodingFacadeException("Cannot instantiate SolrServer", e);
        }
    }
    
    @Override
    public void setSolrServerUrls(HttpClient httpClient, ResponseParser parser, String... solrServerUrls) throws SolrGeocodingFacadeException {
        try {
            solrServer = new LBHttpSolrServer(httpClient, parser, solrServerUrls);
        } catch (MalformedURLException e) {
            throw new SolrGeocodingFacadeException("Cannot instantiate SolrServer", e);
        }
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
    public void setAddressTokenDelim(String addressTokenDelim) {
        this.addressTokenDelim = addressTokenDelim;
    }
    
    @Override
    public void setStreetTypeWeigth(float streetTypeWeigth) {
        this.streetTypeWeigth = streetTypeWeigth;
    }
    
    @Override
    public void setStreetNameWeigth(float streetNameWeigth) {
        this.streetNameWeigth = streetNameWeigth;
    }
    
    @Override
    public void setNumberSubdivisionSeparator(String numberSubdivisionSeparator) {
        this.numberSubdivisionSeparator = numberSubdivisionSeparator;
    }
    
    @Override
    public void setNumberWeigth(float numberWeigth) {
        this.numberWeigth = numberWeigth;
    }
    
    @Override
    public void setMunicipalityWeigth(float municipalityWeigth) {
        this.municipalityWeigth = municipalityWeigth;
    }
    
    @Override
    public void setCountrySubdivisionWeigth(float countrySubdivisionWeigth) {
        this.countrySubdivisionWeigth = countrySubdivisionWeigth;
    }
    
    @Override
    public void setMaxRows(int maxRows) {
        this.maxRows = maxRows;
    }
    
    @Override
    public void setFuzzySearchStreetType(boolean fuzzySearchStreetType) {
        this.fuzzySearchStreetType = fuzzySearchStreetType;
    }
    
    @Override
    public void setFuzzySearchStreetName(boolean fuzzySearchStreetName) {
        this.fuzzySearchStreetName = fuzzySearchStreetName;
    }
    
    @Override
    public void setFuzzySearchNumber(boolean fuzzySearchNumber) {
        this.fuzzySearchNumber = fuzzySearchNumber;
    }
    
    @Override
    public void setFuzzySearchMunicipality(boolean fuzzySearchMunicipality) {
        this.fuzzySearchMunicipality = fuzzySearchMunicipality;
    }
    
    @Override
    public void setFuzzySearchCountrySubdivision(boolean fuzzySearchCountrySubdivision) {
        this.fuzzySearchCountrySubdivision = fuzzySearchCountrySubdivision;
    }
    
    @Override
    public void setAndNameTerms(boolean andNameTerms) {
        this.andNameTerms = andNameTerms;
    }


    @Override
    public SolrBeanResultsList geocodeAddress(String freeFormAddress, String municipality, String countrySubdivision) throws SolrGeocodingFacadeException {
        AddressParserFactory    apf = new AddressParserFactory();
        AddressParser           addressParser = apf.getSolrGeocodingFacade(baseUrl);
        
        addressParser.setNumberDelimiter(numberDelimiter);
        addressParser.setNumberAfterAddress(numberAfterAddress);
        
        addressParser.setAddress(freeFormAddress);
        
        return geocodeAddress(addressParser.getStreetType(), addressParser.getStreetName(), addressParser.getNumber(), null, municipality, countrySubdivision);
    }
    
    @Override
    public SolrBeanResultsList geocodeAddress(String freeFormAddress, String number, String subdivision, String municipality, String countrySubdivision) throws SolrGeocodingFacadeException {
        AddressParserFactory    apf = new AddressParserFactory();
        AddressParser           addressParser = apf.getSolrGeocodingFacade(baseUrl);
        
        addressParser.setNumberDelimiter(numberDelimiter);
        addressParser.setNumberAfterAddress(numberAfterAddress);
        
        addressParser.setAddress(freeFormAddress);
        
        return geocodeAddress(addressParser.getStreetType(), addressParser.getStreetName(), number, subdivision, municipality, countrySubdivision);
    }
    
    @Override
    public SolrBeanResultsList geocodeAddress(String typePrefix, String streetName, String municipality, String countrySubdivision) throws SolrGeocodingFacadeException {
        AddressParserFactory    apf = new AddressParserFactory();
        AddressParser           addressParser = apf.getSolrGeocodingFacade(baseUrl);
        
        addressParser.setNumberDelimiter(numberDelimiter);
        addressParser.setNumberAfterAddress(numberAfterAddress);
        
        addressParser.setAddress(streetName);
        
        return geocodeAddress(typePrefix, addressParser.getStreetName(), addressParser.getNumber(), null, municipality, countrySubdivision);
    }
    
    @Override
    public SolrBeanResultsList geocodeAddress(String typePrefix, String streetName, String number, String subdivision, String municipality, String countrySubdivision) throws SolrGeocodingFacadeException {
        streetName = streetName.replaceAll("\"", "");
        municipality = municipality.replaceAll("\"", "");

        StringBuffer queryBuffer = new StringBuffer("");
        if (!isStringEmpty(typePrefix)) {
            queryBuffer.append("(street_type:").append(typePrefix)
                    .append(" OR street_type:").append(typePrefix).append("~1")
                    .append(" OR street_type:").append(typePrefix).append("~2")
                    .append(" OR (-street_type:[* TO *] AND *:*) OR street_type:*)").append("^").append(streetTypeWeigth)
                    .append(" AND ");
        }

        queryBuffer.append("(street_name:\"").append(streetName.trim()).append("\"").append("^").append(streetNameWeigth);
        StringTokenizer stringTokenizer = new StringTokenizer(streetName, " \t\n\r\f-()^");
        String          token;
        while (stringTokenizer.hasMoreTokens()) {
            token = stringTokenizer.nextToken();
            String weight = String.valueOf(token.length());
            queryBuffer.append(" OR street_name:").append(token).append("^").append(weight)
                    .append(" OR street_name:").append(token).append("^").append(weight).append("~1")
                    .append(" OR street_name:").append(token).append("^").append(weight).append("~2");
        }
        queryBuffer.append(")");

        if (!isStringEmpty(number)) {
            if (!isStringEmpty(subdivision)) {
                number = number.trim() + numberSubdivisionSeparator + subdivision.trim();
            }
            
            queryBuffer.append(" AND (building_number:\"").append(number.trim()).append("\"")
                    .append(" OR building_number:\"").append(number.trim()).append("\"").append("~2")
                    .append(" OR (-building_number:[* TO *] AND *:*) OR building_number:*")
                    .append(")").append("^").append(numberWeigth);
        }

        queryBuffer.append(" AND (municipality:\"").append(municipality).append("\"").append("^").append(municipalityWeigth);
        StringTokenizer municipalityTokenizer = new StringTokenizer(municipality, " \t\n\r\f-()^");
        String          municipalityToken;
        while (municipalityTokenizer.hasMoreTokens()) {
            municipalityToken = municipalityTokenizer.nextToken();
            queryBuffer.append(" OR municipality:").append(municipalityToken).append("^").append(municipalityWeigth).append(" OR municipality:").append(municipalityToken).append("^").append(municipalityWeigth).append("~1").append(" OR municipality:").append(municipalityToken).append("^").append(municipalityWeigth).append("~2");
        }
        queryBuffer.append(")");

        if (!isStringEmpty(countrySubdivision)) {
            queryBuffer.append(" AND country_subdivision:").append(countrySubdivision.trim()).append("^").append(countrySubdivisionWeigth);
        }

        System.out.println(queryBuffer.toString());

        if (solrServer == null) {
            throw new SolrGeocodingFacadeException("SolrServer not initialized");
        }

        ModifiableSolrParams    solrParams = new ModifiableSolrParams();

        solrParams.set("q", queryBuffer.toString());
        solrParams.set("fl", "*,score");

        SolrBeanResultsList     retval = new SolrBeanResultsList();
        int                     start = 0;
        QueryResponse           qr;
        SolrDocumentList        list;

        if (maxRows > 0) {
            solrParams.set("rows", maxRows > MAX_DOCS_PER_REQUEST ? MAX_DOCS_PER_REQUEST : maxRows);
        }

        try {
            do {
                solrParams.set("start", start);
    
                qr = solrServer.query(solrParams);
                list = qr.getResults();
    
                retval.addAll(qr.getBeans(OLSAddressBean.class));
                retval.setNumFound(list.getNumFound());
    
    
                start = retval.size();
            } while (start < list.getNumFound() && maxRows != SolrGeocodingFacade.MAX_ROWS_SOLR_DEFAULT && (maxRows == SolrGeocodingFacade.MAX_ROWS_ALL || start < maxRows));
        } catch (SolrServerException e) {
            throw new SolrGeocodingFacadeException("Cannot call Solr", e);
        }

        return retval;
    }

    private boolean isStringEmpty(String string)
    {
        return string == null || "".equals(string);
    }
}
