/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.solr.utils;

import java.util.ArrayList;
import java.util.List;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;

public class SolrStreetTypesInspector {
    private SolrServer solrServer;

    public SolrStreetTypesInspector(String baseURL) {
        solrServer = new HttpSolrServer(baseURL);
    }

    public ArrayList<String> getDistinctStreetTypes() throws SolrGeocodingFacadeException
    {
        if (solrServer == null) {
            throw new SolrGeocodingFacadeException("SolrServer not initialized");
        }
        
        SolrQuery       qry = new SolrQuery("*:*");
        String[]        fetchFacetFields = new String[]{"street_type"};
        
        qry.setFacet(true);
        qry.setFields(fetchFacetFields);
        qry.addFacetField(fetchFacetFields);
        
        QueryRequest    qryReq = new QueryRequest(qry);
        
        try {
            QueryResponse   qr = qryReq.process(solrServer);
    
            ArrayList<String> retval = new ArrayList<String>();
    
            List<FacetField> facetFields = qr.getFacetFields();
            
            for (FacetField facetField: facetFields) {
                List<FacetField.Count> values = facetField.getValues();
                
                for (FacetField.Count value:values) {
                    retval.add(value.getName().toUpperCase());
                }
            }
    
            return retval;
        } catch (SolrServerException e) {
            throw new SolrGeocodingFacadeException("Cannot query Solr for street types", e);
        }
    }
}