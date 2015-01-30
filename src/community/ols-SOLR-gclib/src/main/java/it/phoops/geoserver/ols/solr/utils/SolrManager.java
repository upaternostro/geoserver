package it.phoops.geoserver.ols.solr.utils;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;

import java.io.IOException;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by davide.cesaroni on 22/12/14.
 */
public class SolrManager {

	private String solrURL;


	public SolrManager(String url) {
		solrURL = url;
	}


	public UpdateResponse add(String id,
							  String streetType,
							  String streetName,
							  String number,
							  String numberExtension,
							  String numberColor,
							  String municipality,
							  String countrySubdivision,
							  boolean isBuilding,
							  String centerline,
							  String centroid,
							  String boundingBox)
			throws SolrServerException, IOException {

		SolrServer server = new HttpSolrServer(solrURL);


		OLSAddressBean address = new OLSAddressBean();
		address.setId(id);
		address.setStreetType(streetType);
		address.setStreetName(streetName);
		address.setNumber(number);
		address.setNumberExtension(numberExtension);
		address.setNumberColor(numberColor);
		address.setMunicipality(municipality);
		address.setCountrySubdivision(countrySubdivision);
		address.setBuilding(isBuilding);
		address.setManaged(true);
		address.setCenterline(centerline);
		address.setCentroid(centroid);
		address.setBoundingBox(boundingBox);

		server.addBean(address);
		UpdateResponse ur = server.commit();

		return ur;

	}


	public SolrBeanResultsList search(String streetType,
									  String streetName,
									  String number,
									  String numberExtension,
									  String numberColor,
									  String municipality,
									  String countrySubdivision)
			throws SolrInvalidFieldException, SolrGeocodingFacadeException {


		// TODO replicate the same check for all fields
		if (! isFieldValid(streetType) ){
			throw new SolrInvalidFieldException("The field streetType is not valid");
		}




		String buildingNumber = number + numberExtension;
		if ( ( numberColor != null ) && ( ! numberColor.equals("")) ){
			buildingNumber += "/" + numberColor;
		}

		String freeFormAddress = streetType + " "
				+ streetName + ", "
				+ buildingNumber;

		return search(freeFormAddress, municipality, countrySubdivision);

	}


	public SolrBeanResultsList search(String freeFormAddress,
									  String municipality,
									  String countrySubdisvision)
			throws SolrGeocodingFacadeException{

		SolrGeocodingFacadeFactory factory = new SolrGeocodingFacadeFactory();
		SolrGeocodingFacade facade ;
		SolrBeanResultsList docsResult;

		facade = factory.getSolrGeocodingFacade();
		facade.setAddressTokenDelim(" \t\n\r\f-()^");

		facade.setSolrServerURL(solrURL);


		String tipoRicerca = "AND";
		facade.setFuzzySearchNumber(false);
		facade.setFuzzySearchStreetName(false);
		facade.setFuzzySearchStreetType(false);
		facade.setFuzzySearchMunicipality(false);
		facade.setAndNameTerms(true);

		docsResult = facade.geocodeAddress(freeFormAddress, municipality,
				countrySubdisvision);


		if (docsResult.getNumFound() == 0) {
			// fuzzy search
			tipoRicerca = "FUZZY AND";

			facade.setFuzzySearchNumber(true);
			facade.setFuzzySearchStreetName(true);
			facade.setFuzzySearchStreetType(true);
			facade.setFuzzySearchMunicipality(true);

			docsResult = facade.geocodeAddress(freeFormAddress, municipality,
					countrySubdisvision);


			if (docsResult.getNumFound() == 0) {
				// // fuzzy search in OR
				tipoRicerca = "OR";

				facade.setAndNameTerms(false);

				docsResult = facade.geocodeAddress(freeFormAddress, municipality,
						countrySubdisvision);
			}
		}

		return docsResult;

	}


	public SolrBeanResultsList search(String dug,
									  String address,
									  String municipality,
									  String countrySubdisvision)
			throws SolrGeocodingFacadeException {

		SolrGeocodingFacadeFactory factory = new SolrGeocodingFacadeFactory();
		SolrGeocodingFacade facade ;
		SolrBeanResultsList docsResult;

		facade = factory.getSolrGeocodingFacade();
		facade.setAddressTokenDelim(" \t\n\r\f-()^");

		facade.setSolrServerURL(solrURL);


		String tipoRicerca = "AND";
		facade.setFuzzySearchNumber(false);
		facade.setFuzzySearchStreetName(false);
		facade.setFuzzySearchStreetType(false);
		facade.setFuzzySearchMunicipality(false);
		facade.setAndNameTerms(true);

		docsResult = facade.geocodeAddress(dug, address, municipality, countrySubdisvision);


		if (docsResult.getNumFound() == 0) {
			// fuzzy search
			tipoRicerca = "FUZZY AND";

			facade.setFuzzySearchNumber(true);
			facade.setFuzzySearchStreetName(true);
			facade.setFuzzySearchStreetType(true);
			facade.setFuzzySearchMunicipality(true);

			docsResult = facade.geocodeAddress(dug, address, municipality, countrySubdisvision);

			if (docsResult.getNumFound() == 0) {
				docsResult = facade.geocodeAddress(null,address, municipality, countrySubdisvision);

				if (docsResult.getNumFound() == 0) {
					// // fuzzy search in OR
					tipoRicerca = "OR";
					facade.setAndNameTerms(false);

					docsResult = facade.geocodeAddress(null, address, municipality, countrySubdisvision);
				}
			}

		}

		return docsResult;

	}

	private boolean isFieldValid(String fieldValue) {
		// TODO check field chars validity to make a solr query
		// now is only a stub!!
		return true;
	}

	private StringBuffer generateQueryString(Map<String, String> fields,
											 SolrQueryOperator solrQueryOperator,
											 boolean isFuzzy) {

		StringBuffer queryBuffer = new StringBuffer();
		String boolOp = "";
		String fuzzyOp = "";

		if (solrQueryOperator == SolrQueryOperator.AND_OPERATOR) {
			boolOp = "AND";
		}
		else {
			boolOp = "OR";
		}

		if (isFuzzy) {
			fuzzyOp = "~";
		}

		Iterator<String> keyIterator = fields.keySet().iterator();

		while (keyIterator.hasNext()) {

			String key = keyIterator.next();

			queryBuffer.append(key);
			queryBuffer.append(":\"");
			queryBuffer.append(fields.get(key));
			queryBuffer.append(fuzzyOp);
			queryBuffer.append("\"");

			if (keyIterator.hasNext()) {
				queryBuffer.append(" " + boolOp + " ");
			}

		}


		return queryBuffer;
	}

	public enum SolrQueryOperator {

		AND_OPERATOR,
		OR_OPERATOR

	}


	public class SolrInvalidFieldException extends Exception {

		public SolrInvalidFieldException(String msg) {
			super(msg);
		}

	}


}
