package it.phoops.geoserver.ols.solr.utils;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.solr.client.solrj.SolrServer;
import org.apache.solr.client.solrj.SolrServerException;
import org.apache.solr.client.solrj.impl.HttpSolrServer;
import org.apache.solr.client.solrj.response.UpdateResponse;

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


		docsResult = facade.geocodeAddress(freeFormAddress, municipality,
				countrySubdisvision);

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

		docsResult = facade.geocodeAddress(dug, address, municipality, countrySubdisvision);

		return docsResult;

	}

	public ArrayList<GeocodingResult> suggest(String address)
			throws SolrGeocodingFacadeException, SolrServerException {

		SolrGeocodingFacadeFactory factory = new SolrGeocodingFacadeFactory();
		SolrGeocodingFacade facade ;
		SolrBeanResultsList docsResult;

		facade = factory.getSolrGeocodingFacade();
		facade.setAddressTokenDelim(" \t\n\r\f-()^");

		facade.setSolrServerURL(solrURL);

		ArrayList<GeocodingResult> response = new ArrayList<GeocodingResult>();
		docsResult = facade.solrSuggestQuery(address);
		for (OLSAddressBean addressBean: docsResult) {
			response.add(new GeocodingResult(addressBean));
		}
		return response;

	}

	private boolean isFieldValid(String fieldValue) {
		// TODO check field chars validity to make a solr query
		// now is only a stub!!
		return true;
	}

	public class SolrInvalidFieldException extends Exception {

		public SolrInvalidFieldException(String msg) {
			super(msg);
		}

	}


}
