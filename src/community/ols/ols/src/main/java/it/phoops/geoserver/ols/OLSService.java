/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols;

public enum OLSService {
    GEOCODING("1"),
    REVERSE_GEOCODING("2"),
    ROUTING_NAVIGATION("3");
    
    private String code;
    
    OLSService(String code) {
    	this.code = code;
    }
    
    @Override
    public String toString() {
    	return code;
    }
}
