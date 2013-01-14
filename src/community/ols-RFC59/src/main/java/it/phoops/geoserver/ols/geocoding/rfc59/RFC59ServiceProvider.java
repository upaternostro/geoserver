package it.phoops.geoserver.ols.geocoding.rfc59;

import it.phoops.geoserver.ols.geocoding.GeocodingServiceProvider;

public class RFC59ServiceProvider implements GeocodingServiceProvider {
    private String descriptionKey;
    
    @Override
    public String getDescription() {
        return descriptionKey;
    }

    public void setDescriptionKey(String description) {
        this.descriptionKey = description;
    }
}
