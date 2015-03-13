/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.rt.webgis.solr.test;

import it.phoops.geoserver.ols.solr.utils.*;
import junit.framework.Assert;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by martina.mazzanti on 02/03/15.
 */
public class SolrSuggestResponseTest {

	private String solrUrl;

	@Before
	public void init(){
		solrUrl = "http://localhost:8080/solr/suggest";
	}

	@Test
	public void testGetResults() throws SolrGeocodingFacadeException, SolrServerException {
		SolrGeocodingFacadeFactory factory = new SolrGeocodingFacadeFactory();
		SolrGeocodingFacade facade = factory.getSolrGeocodingFacade();
		facade.setSolrServerURL("http://localhost:8080/solr/suggest");
		SolrBeanResultsList res = facade.solrSuggestQuery("via giovac");

		System.out.printf("Numero risultati trovati: %d%n", res.size());
		if (res.size() > 0) {
			for (OLSAddressBean olsAddressBean: res) {
				System.out.println("+++++++++++++++++++++++++++");
				System.out.println(olsAddressBean.getStreetType());
				System.out.println(olsAddressBean.getStreetName());
				String number = olsAddressBean.getNumber();
				if (olsAddressBean.getNumberExtension()!=null) {
					number = number + olsAddressBean.getNumberExtension();
				}
				if (olsAddressBean.getNumberColor() != null) {
					number = number + olsAddressBean.getNumberColor();
				}
				System.out.println(number);
				System.out.println(olsAddressBean.getMunicipality());
				System.out.println(olsAddressBean.getCountrySubdivision());
				System.out.println(olsAddressBean.getScore());
			}
			System.out.println("#####################################");
		}
		Assert.assertNotNull(res);
	}


}
