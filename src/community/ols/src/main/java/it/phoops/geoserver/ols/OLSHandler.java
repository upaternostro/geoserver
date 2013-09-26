package it.phoops.geoserver.ols;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

public interface OLSHandler {
    public abstract OLSService getService();
    public abstract void setServiceProvider(OLSServiceProvider provider);
    public abstract void setActiveServiceProvider(OLSServiceProvider provider);
    public abstract Document processRequest(Node request) throws OLSException;
}
