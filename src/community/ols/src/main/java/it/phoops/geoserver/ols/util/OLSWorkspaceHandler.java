package it.phoops.geoserver.ols.util;

import it.phoops.geoserver.ols.OLSException;
import it.phoops.geoserver.ols.OLSHandler;
import it.phoops.geoserver.ols.OLSService;
import it.phoops.geoserver.ols.OLSServiceProvider;

import javax.xml.bind.JAXBElement;

import net.opengis.www.xls.AbstractResponseParametersType;
import net.opengis.www.xls.RequestType;

public class OLSWorkspaceHandler implements OLSHandler{

    @Override
    public OLSService getService() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void setServiceProvider(OLSServiceProvider provider) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void setActiveServiceProvider(OLSServiceProvider provider) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public JAXBElement<? extends AbstractResponseParametersType> processRequest(RequestType request, String lang, String srsName) throws OLSException
    {
        return null;
    }

}
