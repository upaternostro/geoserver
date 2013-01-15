package it.phoops.geoserver.ols.geocoding.rfc59;

import javax.xml.bind.JAXBElement;

import it.phoops.geoserver.ols.geocoding.GeocodingServiceProvider;
import net.opengis.www.xls.AddressType;
import net.opengis.www.xls.GeocodeRequestType;
import net.opengis.www.xls.GeocodeResponseType;
import net.opengis.www.xls.ObjectFactory;

public class RFC59ServiceProvider implements GeocodingServiceProvider {
    private String descriptionKey;
    
    @Override
    public String getDescriptionKey() {
        return descriptionKey;
    }

    public void setDescriptionKey(String description) {
        this.descriptionKey = description;
    }

    @Override
    public JAXBElement<GeocodeResponseType> geocode(GeocodeRequestType input) {
        ObjectFactory                           of = new ObjectFactory();
        GeocodeResponseType                     output = of.createGeocodeResponseType();
        JAXBElement<GeocodeResponseType>        retval = of.createGeocodeResponse(output);
        
//        for (AddressType address : input.getAddresses()) {
//            address.get
//        }
        
        output.getGeocodeResponseLists();
        
        // TODO Auto-generated method stub
        return retval;
    }
}
