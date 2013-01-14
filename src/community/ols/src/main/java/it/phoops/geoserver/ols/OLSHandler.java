package it.phoops.geoserver.ols;

import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;

public interface OLSHandler {
    public abstract Document processRequest(ApplicationContext applicationContext, Document request);
}
