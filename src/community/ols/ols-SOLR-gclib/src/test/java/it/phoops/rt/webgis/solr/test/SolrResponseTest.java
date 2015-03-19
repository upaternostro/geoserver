/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.rt.webgis.solr.test;

import it.phoops.geoserver.ols.solr.utils.AddressParser;
import it.phoops.geoserver.ols.solr.utils.AddressParserFactory;
import it.phoops.geoserver.ols.solr.utils.OLSAddressBean;
import it.phoops.geoserver.ols.solr.utils.SolrBeanResultsList;
import it.phoops.geoserver.ols.solr.utils.SolrGeocodingFacade;
import it.phoops.geoserver.ols.solr.utils.SolrGeocodingFacadeException;
import it.phoops.geoserver.ols.solr.utils.SolrGeocodingFacadeFactory;
import junit.framework.Assert;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by davide.cesaroni on 28/11/14.
 */
public class SolrResponseTest {

	private String solrUrl;

	@Before
	public void init(){
		solrUrl = "http://jarpa.phoops.priv:8081/solr/SINS";
	}

	@Test
	public void testGetResults()
			throws SolrGeocodingFacadeException, SolrServerException {

	        AddressParserFactory    apf = new AddressParserFactory();
	        AddressParser           addressParser = apf.getSolrGeocodingFacade(solrUrl);
	        
                addressParser.setAddress("via paqsuale pocianti, 16");

		String municipality = "scadnicci";
		String subdivision = "fi";

		SolrBeanResultsList res = new SolrBeanResultsList();

		SolrGeocodingFacadeFactory factory = new SolrGeocodingFacadeFactory();
		SolrGeocodingFacade facade = factory.getSolrGeocodingFacade();
		facade.setSolrServerURL(solrUrl);
		res = facade.geocodeAddress(addressParser.getStreetType(), addressParser.getStreetName(), addressParser.getNumber(), null, municipality, subdivision);

		System.out.printf("Numero risultati trovati: %d%n", res.size());
		if (res.size() > 0) {
			float score = res.get(0).getScore();
			float somma = 0;
			float distanza = 0;
			for (OLSAddressBean olsAddressBean: res) {
				float localScore = olsAddressBean.getScore();
				somma = somma + localScore;
				distanza = distanza + (score - localScore);


				System.out.println("+++++++++++++++++++++++++++");
				System.out.println(olsAddressBean.getStreetType());
				System.out.println(olsAddressBean.getStreetName());
				System.out.println(olsAddressBean.getBuildingNumber());
				System.out.println(olsAddressBean.getMunicipality());
				System.out.println(olsAddressBean.getCountrySubdivision());
				System.out.println(olsAddressBean.getScore());
			}
			System.out.println("#####################################");
			System.out.println("score: " + score);
			System.out.println("distanza: " + distanza/somma);
		}
		Assert.assertNotNull(res);
	}


}
