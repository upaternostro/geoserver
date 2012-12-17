/* Copyright (c) 2012 TOPP - www.openplans.org.  All rights reserved.
 * This code is licensed under the GPL 2.0 license, availible at the root
 * application directory.
 */
package org.geoserver.wcs2_0.exception;

import org.geoserver.platform.OWS20Exception;

/**
 * This defines an exception that can be turned into a valid xml service exception that wcs clients
 * will expect.
 *
 * All errors should be wrapped in this before returning to clients.
 * 
 * @author Emanuele Tajariol, GeoSolutions SAS
 * @author Simone Giannecchini, GeoSolutionS SAS
 */
public class WCS20Exception extends OWS20Exception {
    /**
	 * 
	 */
	private static final long serialVersionUID = -6110652531274829497L;

	public static class WCS20ExceptionCode extends OWS20Exception.OWSExceptionCode {

        public final static WCS20ExceptionCode EmptyCoverageIdList = new WCS20ExceptionCode("emptyCoverageIdList", 404);
        public final static WCS20ExceptionCode InvalidEncodingSyntax = new WCS20ExceptionCode("InvalidEncodingSyntax", 400);
        
        // Scaling Extension
        public final static WCS20ExceptionCode InvalidScaleFactor = new WCS20ExceptionCode("InvalidScaleFactor", 404);
        public final static WCS20ExceptionCode InvalidExtent = new WCS20ExceptionCode("InvalidExtent", 404);
        public final static WCS20ExceptionCode ScaleAxisUndefined = new WCS20ExceptionCode("ScaleAxisUndefined", 404);
        
        
        // Interpolation Extension
        public final static WCS20ExceptionCode NoSuchAxis = new WCS20ExceptionCode("ScalingAxisUndefined", 404);        
        public final static WCS20ExceptionCode InterpolationMethodNotSupported = new WCS20ExceptionCode("InterpolationMethodNotSupported", 404);
        
        // CRS Extension
        public final static WCS20ExceptionCode NotACrs = new WCS20ExceptionCode("NotACrs", 404);
        public final static WCS20ExceptionCode SubsettingCrsNotSupported = new WCS20ExceptionCode("SubsettingCrs-NotSupported", 404);
        public final static WCS20ExceptionCode OutputCrsNotSupported = new WCS20ExceptionCode("OutputCrs-NotSupported", 404);
        

        // CORE
        public final static WCS20ExceptionCode NoSuchCoverage = new WCS20ExceptionCode("NoSuchCoverage", 404);
        public final static WCS20ExceptionCode InvalidSubsetting = new WCS20ExceptionCode("InvalidSubsetting", 404);
        public final static WCS20ExceptionCode InvalidAxisLabel = new WCS20ExceptionCode("InvalidAxisLabel", 404);
        
        //RangeSubset extension
        public final static WCS20ExceptionCode NoSuchField = new WCS20ExceptionCode("NoSuchField", 404);

        protected WCS20ExceptionCode(String exceptionCode, Integer httpCode) {
            super(exceptionCode, httpCode);
        }        
    }

    public WCS20Exception(String message) {
        super(message);
    }

    public WCS20Exception(Throwable e) {
        super(e);
    }

    public WCS20Exception(String message, String locator) {
        super(message, locator);
    }

    public WCS20Exception(String message, OWS20Exception.OWSExceptionCode code, String locator) {
        super(message, code, locator);
    }

    public WCS20Exception(String message, Throwable cause) {
        super(message, cause);
    }


}
