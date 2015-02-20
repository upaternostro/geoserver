package it.phoops.geoserver.ols.solr.utils;

import org.junit.Assert;
import org.junit.Test;

public class SolrGeocodingFacadeTest {
    @Test
    public void testIt() {
        SolrGeocodingFacadeFactory      factory = new SolrGeocodingFacadeFactory();
        
        Assert.assertNotNull(factory);
        
        SolrGeocodingFacade facade = null;
        
        try {
            facade = factory.getSolrGeocodingFacade();
        } catch (SolrGeocodingFacadeException e) {
            e.printStackTrace();
        }
        
        Assert.assertNotNull(facade);
        
        facade.setSolrServerURL("http://localhost:8080/solr/SINS");
        
        SolrBeanResultsList docs = null;
        try {
            docs = facade.geocodeAddress("via della torretta", "firenze", "fi");
        } catch (SolrGeocodingFacadeException e) {
            e.printStackTrace();
        }
        
        Assert.assertNotNull(docs);
        Assert.assertTrue(docs.getNumFound() > 0);
        
        for (OLSAddressBean doc : docs) {
            Assert.assertNotNull(doc);
            
            Assert.assertEquals("VIA", doc.getStreetType());
            Assert.assertEquals("DELLA TORRETTA", doc.getStreetName());
            Assert.assertEquals("FIRENZE", doc.getMunicipality());
            Assert.assertEquals("FI", doc.getCountrySubdivision());
        }
    }
}
