package it.phoops.geoserver.ols.solr.utils;

import org.apache.solr.client.solrj.SolrServerException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;

/**
 * Created by martina on 08/01/15.
 */
public class AddressParser {

    private String dug;
    private String address;
    private String number;

    public AddressParser(String freeformString, String solrUrl) throws SolrGeocodingFacadeException, SolrServerException {

        address = null;
        number = null;

        //ricerco il numero
        int     numberDelimiterIndex = freeformString.indexOf(",");
        if (numberDelimiterIndex != -1) {
            number = freeformString.substring(numberDelimiterIndex+1);
            number = number.trim();
            freeformString = freeformString.substring(0, numberDelimiterIndex);
        }

        //elenco tutti i dug
        SolrDugInspector dugInspector = new SolrDugInspector(solrUrl);
        ArrayList<String> dugs = dugInspector.distinctDugs();

        if (freeformString != null) {
            freeformString = freeformString.toUpperCase();
            // cerco DUG all'interno della stringa e se trovo, lo cancello
            for (String d : dugs) {
                String regExp = ".*\\b" + d + "\\b.*";
                if (freeformString.matches(regExp)) {
                    freeformString = freeformString.replaceAll("\\b" + d + "\\b", "");
                    address = freeformString;
                    dug = d;
                    break;
                }
            }
        }
        if (address == null) {
            address = freeformString;
        }
        System.out.println(dug + " - " + address + " - " + number);
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDug() {
        return dug;
    }

    public void setDug(String dug) {
        this.dug = dug;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }
}
