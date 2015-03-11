package it.phoops.rt.normalizzatore;

import it.phoops.geoserver.ols.solr.utils.AddressParser;
import it.phoops.geoserver.ols.solr.utils.AddressParserFactory;
import it.phoops.geoserver.ols.solr.utils.OLSAddressBean;
import it.phoops.geoserver.ols.solr.utils.SolrBeanResultsList;
import it.phoops.geoserver.ols.solr.utils.SolrGeocodingFacade;
import it.phoops.geoserver.ols.solr.utils.SolrGeocodingFacadeException;
import it.phoops.geoserver.ols.solr.utils.SolrGeocodingFacadeFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import org.apache.solr.client.solrj.SolrServerException;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import au.com.bytecode.opencsv.CSVReader;
import au.com.bytecode.opencsv.CSVWriter;

public class NormalizzatoreTest {
    private String solrUrl;

    @Before
    public void init() {
        solrUrl = "http://jarpa.phoops.priv:8081/solr/SINS";
    }

    @Test
    public void normalizerTest() throws IOException, SolrGeocodingFacadeException, SolrServerException {
        InputStream csvStream = getClass().getClassLoader().getResourceAsStream(
                "indirizzixgeoref.csv");
        Assert.assertNotNull(csvStream);

        CSVReader reader = new CSVReader(new InputStreamReader(csvStream), '|');
        Assert.assertNotNull(reader);

        List<String[]> lines = reader.readAll();
        Assert.assertNotNull(lines);

        SolrGeocodingFacadeFactory factory = new SolrGeocodingFacadeFactory();
        Assert.assertNotNull(factory);

        SolrGeocodingFacade facade = null;

        try {
            facade = factory.getSolrGeocodingFacade();
        } catch (SolrGeocodingFacadeException e) {
            e.printStackTrace();
        }

        Assert.assertNotNull(facade);

        facade.setSolrServerURL(solrUrl);
        facade.setAddressTokenDelim(" \t\n\r\f-()^");

        SolrBeanResultsList docs = null;
        File file = File.createTempFile("normalizzatore", ".csv");
        CSVWriter writer = new CSVWriter(new FileWriter(file), '|');
        String[] newLine = new String[5 + 2 + 8 + 8];
        int i;
        OLSAddressBean doc;
        String ambiguita;
        Float punteggio;
        AddressParserFactory apf = new AddressParserFactory();
        AddressParser dugExtractor = apf.getSolrGeocodingFacade(solrUrl);

        for (String[] line : lines) {
            dugExtractor.setAddress(line[0]);
            try {
                docs = getResponse(dugExtractor, facade, line);
            } catch (SolrGeocodingFacadeException e) {
                e.printStackTrace();
            }

            Assert.assertNotNull(docs);

            if (docs.getNumFound() == 0) {

                for (i = 0; i < line.length; i++) {
                    newLine[i] = line[i];
                }

                if (docs.getNumFound() > 0) {
                    doc = docs.get(0);

                    newLine[i++] = toString(doc.getStreetType());
                    newLine[i++] = toString(doc.getStreetName());
                    newLine[i++] = toString(doc.getMunicipality());
                    newLine[i++] = toString(doc.getCountrySubdivision());
                    newLine[i++] = toString(doc.getNumber());
                    newLine[i++] = toString(doc.getNumberExtension());
                    newLine[i++] = toString(doc.getNumberColor());
                    newLine[i++] = toString(doc.getScore());

                    punteggio = (Float) doc.getScore();

                    if (docs.getNumFound() > 1) {
                        doc = docs.get(1);

                        newLine[i++] = toString(doc.getStreetType());
                        newLine[i++] = toString(doc.getStreetName());
                        newLine[i++] = toString(doc.getMunicipality());
                        newLine[i++] = toString(doc.getCountrySubdivision());
                        newLine[i++] = toString(doc.getNumber());
                        newLine[i++] = toString(doc.getNumberExtension());
                        newLine[i++] = toString(doc.getNumberColor());
                        newLine[i++] = toString(doc.getScore());

                        ambiguita = punteggio.equals(doc.getScore()) ? "AMBIGUO" : "NON AMBIGUO";
                    } else {
                        newLine[i++] = "";
                        newLine[i++] = "";
                        newLine[i++] = "";
                        newLine[i++] = "";
                        newLine[i++] = "";
                        newLine[i++] = "";
                        newLine[i++] = "";
                        newLine[i++] = "";

                        ambiguita = "SOLO UNO";
                    }
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
                    newLine[i++] = "";
                    newLine[i++] = "";
                    newLine[i++] = "";
                    newLine[i++] = "";
                    newLine[i++] = "";
                    newLine[i++] = "";
                    newLine[i++] = "";

                    ambiguita = "NON TROVATO";
                }

                newLine[i++] = ambiguita;

                // System.out.println(Arrays.toString(newLine));
                writer.writeNext(newLine);
            }

            writer.close();
            System.out.println(file.getCanonicalPath());
        }
    }

    private String toString(Object o) {
        return o == null ? null : o.toString();
    }

    private SolrBeanResultsList getResponse(AddressParser dugExtractor, SolrGeocodingFacade facade,
            String[] line) throws SolrGeocodingFacadeException {

        return facade.geocodeAddress(dugExtractor.getStreetType(), dugExtractor.getStreetName(),
                line[1], line[2]);
    }
}
