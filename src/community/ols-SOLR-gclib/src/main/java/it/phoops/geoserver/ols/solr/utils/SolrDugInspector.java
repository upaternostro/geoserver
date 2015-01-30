/* Copyright (c) 2001 - 2013 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.solr.utils;

import org.apache.solr.client.solrj.SolrQuery;
import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.request.QueryRequest;
import org.apache.solr.client.solrj.response.FacetField;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.FacetParams;
import org.apache.solr.common.params.ModifiableSolrParams;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class SolrDugInspector {

    private SolrServer solrServer;

    public SolrDugInspector(String baseURL) {
        solrServer = new HttpSolrServer(baseURL);
    }

    public ArrayList<String> distinctDugs() throws SolrServerException, SolrGeocodingFacadeException {

        if (solrServer == null) {
            throw new SolrGeocodingFacadeException("SolrServer not initialized");
        }


        SolrQuery qry = new SolrQuery("*:*");
        String[] fetchFacetFields = new String[]{"street_type"};
        qry.setFacet(true);
        qry.setFields(fetchFacetFields);
        qry.addFacetField(fetchFacetFields);
        QueryRequest qryReq = new QueryRequest(qry);
        QueryResponse qr = qryReq.process(solrServer);

        ArrayList<String> retval = new ArrayList<String>();

        List<FacetField> facetFields = qr.getFacetFields();
        for (FacetField facetField: facetFields) {
            List<FacetField.Count> values = facetField.getValues();
            System.out.println(values.size());
            for (FacetField.Count value:values) {
                retval.add(value.getName().toUpperCase());
            }
        }

        return retval;
    }
}