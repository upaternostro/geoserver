package it.phoops.geoserver.ols.solr.utils;

import org.apache.solr.client.solrj.beans.Field;

/**
 * Created by davide.cesaroni on 03/12/14.
 */
public class OLSAddressBean {

	@Field
	private String id;
	@Field("street_type")
	private String streetType;
	@Field("street_name")
	private String streetName;
	@Field("municipality")
	private String municipality;
	@Field("municipality_code")
	private String municipalityCode;
	@Field("municipality_code2")
	private String municipalityCode2;
	@Field("country_subdivision")
	private String countrySubdivision;
	@Field("building_number")
	private String buildingNumber;
	@Field
	private String number;
	@Field("number_extension")
	private String numberExtension;
	@Field("number_color")
	private String numberColor;
	@Field
	private String centerline;
	@Field
	private String centroid;
	@Field("bounding_box")
	private String boundingBox;
	@Field("is_building")
	private boolean isBuilding;
	@Field("is_managed")
	private boolean isManaged;
	@Field("_version_")
	private Long version;
	@Field("score")
	private Float score;


	public Float getScore() {
		return score;
	}

	public void setScore(Float score) {
		this.score = score;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getStreetType() {
		return streetType;
	}

	public void setStreetType(String streetType) {
		this.streetType = streetType;
	}

	public String getStreetName() {
		return streetName;
	}

	public void setStreetName(String streetName) {
		this.streetName = streetName;
	}

	public String getMunicipality() {
		return municipality;
	}

	public void setMunicipality(String municipality) {
		this.municipality = municipality;
	}

	public String getMunicipalityCode() {
		return municipalityCode;
	}

	public void setMunicipalityCode(String municipalityCode) {
		this.municipalityCode = municipalityCode;
	}

	public String getMunicipalityCode2() {
		return municipalityCode2;
	}

	public void setMunicipalityCode2(String municipalityCode2) {
		this.municipalityCode2 = municipalityCode2;
	}

	public String getCountrySubdivision() {
		return countrySubdivision;
	}

	public void setCountrySubdivision(String countrySubdivision) {
		this.countrySubdivision = countrySubdivision;
	}

	public String getBuildingNumber() {
		return buildingNumber;
	}

	public void setBuildingNumber(String buildingNumber) {
		this.buildingNumber = buildingNumber;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getNumberExtension() {
		return numberExtension;
	}

	public void setNumberExtension(String numberExtension) {
		this.numberExtension = numberExtension;
	}

	public String getNumberColor() {
		return numberColor;
	}

	public void setNumberColor(String numberColor) {
		this.numberColor = numberColor;
	}

	public String getCenterline() {
		return centerline;
	}

	public void setCenterline(String centerline) {
		this.centerline = centerline;
	}

	public String getCentroid() {
		return centroid;
	}

	public void setCentroid(String centroid) {
		this.centroid = centroid;
	}

	public String getBoundingBox() {
		return boundingBox;
	}

	public void setBoundingBox(String boundingBox) {
		this.boundingBox = boundingBox;
	}

	public boolean isBuilding() {
		return isBuilding;
	}

	public void setBuilding(boolean isBuilding) {
		this.isBuilding = isBuilding;
	}

	public boolean isManaged() {
		return isManaged;
	}

	public void setManaged(boolean isManaged) {
		this.isManaged = isManaged;
	}

	public Long getVersion() {
		return version;
	}

	public void setVersion(Long version) {
		this.version = version;
	}
}
