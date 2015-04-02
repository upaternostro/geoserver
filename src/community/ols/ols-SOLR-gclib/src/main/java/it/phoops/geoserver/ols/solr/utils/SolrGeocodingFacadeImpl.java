/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.solr.utils;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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

import java.net.MalformedURLException;
import java.text.NumberFormat;
import java.text.ParsePosition;
import java.util.ArrayList;
import java.util.StringTokenizer;
import java.util.regex.Pattern;

public class SolrGeocodingFacadeImpl implements SolrGeocodingFacade {
    private final Log logger = LogFactory.getLog(getClass());
    
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
    private Pattern     specialCharactersPattern;

    public SolrGeocodingFacadeImpl() {
        super();
        
        solrServer = null;
        baseUrl = null;
        
        numberDelimiter = ",";
        numberAfterAddress = true;
        addressTokenDelim = " \t\n\r\f-()^.";
        streetTypeWeigth = 3.0f;
        streetNameWeigth = 10.0f;
        numberSubdivisionSeparator = "/";
        numberWeigth = 5.0f;
        municipalityWeigth = 3.0f;
        countrySubdivisionWeigth = 1.0f;
        maxRows = MAX_ROWS_SOLR_DEFAULT;
        // Lucene special characters: + - && || ! ( ) { } [ ] ^ " ~ * ? : \ /
        setSpecialCharacters("(\\+|-|&&|\\|\\||!|\\(|\\)|\\{|\\}|\\[|\\]|\\^|\"|~|\\*|\\?|:|\\\\|\\/)");
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
    public void setSpecialCharacters(String specialCharacters) {
        specialCharactersPattern = Pattern.compile(specialCharacters);
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

        if (typePrefix != null) {
            typePrefix = specialCharactersPattern.matcher(typePrefix.trim()).replaceAll("\\\\$1");
        }

        if (streetName != null) {
            streetName = specialCharactersPattern.matcher(streetName.trim()).replaceAll("\\\\$1");
        }

        if (number != null) {
            number = specialCharactersPattern.matcher(number.trim()).replaceAll("\\\\$1");
        }

        if (subdivision != null) {
            subdivision = specialCharactersPattern.matcher(subdivision.trim()).replaceAll("\\\\$1");
        }

        if (municipality != null) {
            municipality = specialCharactersPattern.matcher(municipality.trim()).replaceAll("\\\\$1");
        }

        if (countrySubdivision != null) {
            countrySubdivision = specialCharactersPattern.matcher(countrySubdivision.trim()).replaceAll("\\\\$1");
        }

        StringBuffer queryBuffer = new StringBuffer("");
        if (!isStringEmpty(typePrefix)) {
            queryBuffer.append("(street_type:").append(typePrefix)
                    .append(" OR street_type:").append(typePrefix).append("~1")
                    .append(" OR street_type:").append(typePrefix).append("~2")
                    .append(" OR (-street_type:[* TO *] AND *:*) OR street_type:*)").append("^").append(streetTypeWeigth)
                    .append(" AND ");
        }

        queryBuffer.append("(street_name:\"").append(streetName.trim()).append("\"").append("^").append(streetNameWeigth);
        StringTokenizer stringTokenizer = new StringTokenizer(streetName, addressTokenDelim);

        String token;
        ArrayList<String> stringNumbers = new ArrayList<>();
        while (stringTokenizer.hasMoreTokens()) {
            token = stringTokenizer.nextToken();
            String weight = String.valueOf(token.length());
            queryBuffer.append(" OR street_name:").append(token).append("^").append(weight)
                    .append(" OR street_name:").append(token).append("^").append(weight).append("~1")
                    .append(" OR street_name:").append(token).append("^").append(weight).append("~2");
            if (isNumeric(token)) {
                String romanNumber = integerToRoman(Integer.parseInt(token));
                queryBuffer.append(" OR street_name:").append(romanNumber).append("^").append(weight)
                        .append(" OR street_name:").append(romanNumber).append("^").append(weight).append("~1")
                        .append(" OR street_name:").append(romanNumber).append("^").append(weight).append("~2");
                stringNumbers.add(token);
            }
        }
        queryBuffer.append(")");

        String stringNumber = null;
        if (stringNumbers.size()>0 && numberAfterAddress) {
            stringNumber = stringNumbers.get(stringNumbers.size()-1);
        } else if (stringNumbers.size()>0) {
            stringNumber = stringNumbers.get(0);
        }
        if (!isStringEmpty(number)) {
            if (!isStringEmpty(subdivision)) {
                number = number.trim() + numberSubdivisionSeparator + subdivision.trim();
            }
            queryBuffer.append(" AND (building_number:\"").append(number.trim()).append("\"")
                    .append(" OR building_number:\"").append(number.trim()).append("\"").append("~2")
                    .append(" OR (-building_number:[* TO *] AND *:*) OR building_number:*")
                    .append(")").append("^").append(numberWeigth);
        } else if (stringNumber != null) {
            queryBuffer.append(" AND (building_number:\"").append(stringNumber.trim()).append("\"")
                    .append(" OR building_number:\"").append(stringNumber.trim()).append("\"").append("~2")
                    .append(" OR (-building_number:[* TO *] AND *:*) OR building_number:*")
                    .append(")").append("^").append(numberWeigth);
        } else {
            queryBuffer.append(" AND (is_building:FALSE^2.0 OR is_building:TRUE)");
        }

        queryBuffer.append(" AND (municipality:\"").append(municipality).append("\"").append("^").append(municipalityWeigth);
        StringTokenizer municipalityTokenizer = new StringTokenizer(municipality, addressTokenDelim);
        String municipalityToken;
        while (municipalityTokenizer.hasMoreTokens()) {
            municipalityToken = municipalityTokenizer.nextToken();
            queryBuffer.append(" OR municipality:").append(municipalityToken).append("^").append(municipalityWeigth).append(" OR municipality:").append(municipalityToken).append("^").append(municipalityWeigth).append("~1").append(" OR municipality:").append(municipalityToken).append("^").append(municipalityWeigth).append("~2");
        }
        queryBuffer.append(")");

        if (!isStringEmpty(countrySubdivision)) {
            queryBuffer.append(" AND country_subdivision:").append(countrySubdivision.trim()).append("^").append(countrySubdivisionWeigth);
        }

        logger.debug("SOLR query: " + queryBuffer.toString());
        System.out.println("SOLR query: " + queryBuffer.toString());

        if (solrServer == null) {
            throw new SolrGeocodingFacadeException("SolrServer not initialized");
        }

        ModifiableSolrParams solrParams = new ModifiableSolrParams();

        solrParams.set("q", queryBuffer.toString());
        solrParams.set("fl", "*,score");

        SolrBeanResultsList retval = new SolrBeanResultsList();
        int start = 0;
        QueryResponse qr;
        SolrDocumentList list;

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
            }
            while (start < list.getNumFound() && maxRows != SolrGeocodingFacade.MAX_ROWS_SOLR_DEFAULT && (maxRows == SolrGeocodingFacade.MAX_ROWS_ALL || start < maxRows));
        } catch (SolrServerException e) {
            throw new SolrGeocodingFacadeException("Cannot call Solr", e);
        }

        return retval;
    }

    public SolrBeanResultsList solrSuggestQuery(String address) throws SolrGeocodingFacadeException, SolrServerException {
        address = address.replaceAll("\"", "");
        address = address.replaceAll(",", "");
        address = address.replaceAll(";", "");

        StringBuffer queryBuffer = new StringBuffer("");

        StringTokenizer stringTokenizer = new StringTokenizer(address, " \t\n\r\f-()^");
        String          token;
        int count = 0;
        while (stringTokenizer.hasMoreTokens()) {
            token = stringTokenizer.nextToken();
            if (count==0) {
                queryBuffer.append("name_suggest:").append(token);
            }
            else {
                queryBuffer.append(" AND name_suggest:").append(token);
            }
            count = count+1;
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

        do {
            solrParams.set("start", start);

            qr = solrServer.query(solrParams);
            list = qr.getResults();

            retval.addAll(qr.getBeans(OLSAddressBean.class));
            retval.setNumFound(list.getNumFound());


            start = retval.size();
        } while (start < list.getNumFound() && maxRows != SolrGeocodingFacade.MAX_ROWS_SOLR_DEFAULT && (maxRows == SolrGeocodingFacade.MAX_ROWS_ALL || start < maxRows));

        return retval;
    }

    private boolean isStringEmpty(String string)
    {
        return string == null || "".equals(string);
    }

    public static boolean isNumeric(String str)
    {
        NumberFormat formatter = NumberFormat.getInstance();
        ParsePosition pos = new ParsePosition(0);
        formatter.parse(str, pos);
        return str.length() == pos.getIndex();
    }

    public static String integerToRoman(int n){
        String roman="";
        int repeat;

        repeat=n/1000;
        for(int i=1; i<=repeat;i++){
            roman=roman+"M";
        }
        n=n%1000;

        repeat=n/900;
        for(int i=1; i<=repeat;i++){
            roman=roman+"CM";
        }
        n=n%900;

        repeat=n/500;
        for(int i=1; i<=repeat;i++){
            roman=roman+"D";
        }
        n=n%500;

        repeat=n/400;
        for(int i=1; i<=repeat;i++){
            roman=roman+"CD";
        }
        n=n%400;

        repeat=n/100;
        for(int i=1; i<=repeat;i++){
            roman=roman+"C";
        }
        n=n%100;

        repeat=n/90;
        for(int i=1; i<=repeat;i++){
            roman=roman+"XC";
        }
        n=n%90;

        repeat=n/50;
        for(int i=1; i<=repeat;i++){
            roman=roman+"L";
        }
        n=n%50;

        repeat=n/40;
        for(int i=1; i<=repeat;i++){
            roman=roman+"XL";
        }
        n=n%40;

        repeat=n/10;
        for(int i=1; i<=repeat;i++){
            roman=roman+"X";
        }
        n=n%10;

        repeat=n/9;
        for(int i=1; i<=repeat;i++){
            roman=roman+"IX";
        }
        n=n%9;

        repeat=n/5;
        for(int i=1; i<=repeat;i++){
            roman=roman+"V";
        }
        n=n%5;

        repeat=n/4;
        for(int i=1; i<=repeat;i++){
            roman=roman+"IV";
        }
        n=n%4;

        repeat=n/1;
        for(int i=1; i<=repeat;i++){
            roman=roman+"I";
        }
        return roman;
    }
}
