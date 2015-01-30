package it.phoops.rt.normalizzatore;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;
import it.phoops.geoserver.ols.solr.utils.*;
import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.*;
import java.util.List;

public class NormalizzatorePunteggioTest
{
	private String solrUrl;
	private SolrManager solrManager;

	@Before
	public void init(){
		solrUrl = "http://jarpa.phoops.priv:8081/solr/SINS";
		solrManager = new SolrManager(solrUrl);
	}

	@Test
	public void normalizerTest() throws IOException, SolrGeocodingFacadeException, SolrManager.SolrInvalidFieldException, SolrServerException
	{
		InputStream csvStream = getClass().getClassLoader().getResourceAsStream("indirizzixgeoref.csv");
		Assert.assertNotNull(csvStream);
		
		CSVReader reader = new CSVReader(new InputStreamReader(csvStream), '|');
		Assert.assertNotNull(reader);
		
		List<String[]> lines = reader.readAll();
		Assert.assertNotNull(lines);
		
        SolrGeocodingFacadeFactory      factory = new SolrGeocodingFacadeFactory();
        Assert.assertNotNull(factory);
        
        SolrGeocodingFacade facade = null;
        
        try {
            facade = factory.getSolrGeocodingFacade();
        } catch (SolrGeocodingFacadeException e) {
            e.printStackTrace();
        }
        
        Assert.assertNotNull(facade);
        
        facade.setSolrServerURL("http://jarpa.phoops.priv:8081/solr/SINS");

        File	file = File.createTempFile("normalizzatore", ".csv");
        CSVWriter	writer = new CSVWriter(new FileWriter(file), '|');
        String[]	newLine = new String[5 + 2 + 8 + 8];
        int			i;
        OLSAddressBean doc;
		
		for (String[] line : lines) {

			AddressParser addressParser = new AddressParser(line[0], solrUrl);
			SolrBeanResultsList res = facade.solrQuery(addressParser.getDug(), addressParser.getAddress(), addressParser.getNumber(), line[1], line[2]);

	        Assert.assertNotNull(res);

	        for (i = 0; i < line.length; i++) {
	        	newLine[i] = line[i];
	        }

			float punteggioTotale = 0;
	        if (res.getNumFound() > 0) {
		        doc = res.get(0);
				float score = doc.getScore();
				float somma = 0;
				float distanza = 0;
				int j;
				for (j = 0; j < res.size(); j++) {
					float localScore = res.get(j).getScore();
					somma = somma + localScore;
					distanza = distanza + (score - localScore);
				}
				punteggioTotale = distanza/somma;

				newLine[i++] = toString(doc.getStreetType());
		        newLine[i++] = toString(doc.getStreetName());
		        newLine[i++] = toString(doc.getMunicipality());
		        newLine[i++] = toString(doc.getCountrySubdivision());
		        newLine[i++] = toString(doc.getNumber());
		        newLine[i++] = toString(doc.getNumberExtension());
		        newLine[i++] = toString(doc.getNumberColor());
		        newLine[i++] = toString(score);
				newLine[i++] = toString(punteggioTotale);

	        } else {
		        newLine[i++] = "";
		        newLine[i++] = "";
		        newLine[i++] = "";
		        newLine[i++] = "";
		        newLine[i++] = "";
		        newLine[i++] = "";
		        newLine[i++] = "";
		        newLine[i++] = "";
		        newLine[i++] = "";
	        }

	        
//			System.out.println(Arrays.toString(newLine));
	        writer.writeNext(newLine);
		}
		
		writer.close();
		System.out.println(file.getCanonicalPath());
	}
	
	private String toString(Object o)
	{
		return o == null ? null : o.toString();
	}

	private SolrBeanResultsList getResponse(AddressParser addressParser, SolrGeocodingFacade facade, String[] line) throws SolrGeocodingFacadeException {

		return facade.geocodeAddress(addressParser.getDug(), addressParser.getAddress(), line[1], line[2]);
	}
}