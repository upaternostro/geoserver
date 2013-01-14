package it.phoops.geoserver.ols;

import org.w3c.dom.Document;

public interface OLSHandler {
    public abstract Document processRequest(Document request);
}
