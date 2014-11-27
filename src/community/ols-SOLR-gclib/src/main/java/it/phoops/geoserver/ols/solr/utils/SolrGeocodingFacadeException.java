package it.phoops.geoserver.ols.solr.utils;

public class SolrGeocodingFacadeException extends Exception {
    /** serialVersionUID */
    private static final long serialVersionUID = -4586600169922951717L;

    public SolrGeocodingFacadeException() {
        super();
    }

    public SolrGeocodingFacadeException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

    public SolrGeocodingFacadeException(String message, Throwable cause) {
        super(message, cause);
    }

    public SolrGeocodingFacadeException(String message) {
        super(message);
    }

    public SolrGeocodingFacadeException(Throwable cause) {
        super(cause);
    }
}
