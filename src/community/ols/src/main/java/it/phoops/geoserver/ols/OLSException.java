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
