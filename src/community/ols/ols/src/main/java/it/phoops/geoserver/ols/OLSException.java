/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols;

public class OLSException extends Exception {

    /** serialVersionUID */
    private static final long serialVersionUID = 3883232978842744446L;

    public OLSException() {
        super();
    }

    public OLSException(String message, Throwable cause) {
        super(message, cause);
    }

    public OLSException(String message) {
        super(message);
    }

    public OLSException(Throwable cause) {
        super(cause);
    }

}
