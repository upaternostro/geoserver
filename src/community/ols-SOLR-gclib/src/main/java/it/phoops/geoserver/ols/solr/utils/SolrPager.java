/* Copyright (c) 2001 - 2013 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.solr.utils;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.response.QueryResponse;
import org.apache.solr.common.SolrDocumentList;
import org.apache.solr.common.params.ModifiableSolrParams;

public class SolrPager {
    public static SolrDocumentList query(SolrServer server, ModifiableSolrParams params, int maxRows) throws SolrServerException {
        SolrDocumentList        retval = new SolrDocumentList();
        int                     start = 0;
        QueryResponse           qr;
        SolrDocumentList        list;
        
        if (maxRows > 0) {
            params.set("rows", maxRows > 100 ? 100 : maxRows);
        }
        
        do {
            params.set("start", start);
            
            qr = server.query(params);
            list = qr.getResults();
            
            retval.addAll(list);
            retval.setNumFound(list.getNumFound());
            start = retval.size();
        } while (start < list.getNumFound() && maxRows != SolrGeocodingFacade.MAX_ROWS_SOLR_DEFAULT && (maxRows == SolrGeocodingFacade.MAX_ROWS_ALL || start < maxRows));
        
        return retval;
    }
}
