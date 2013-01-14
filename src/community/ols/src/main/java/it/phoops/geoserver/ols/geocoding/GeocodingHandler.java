package it.phoops.geoserver.ols.geocoding;

import it.phoops.geoserver.ols.OLSHandler;
import it.phoops.geoserver.ols.OLSServiceProvider;

import java.util.Map;

import org.springframework.context.ApplicationContext;
import org.w3c.dom.Document;

public class GeocodingHandler implements OLSHandler {

    @Override
    public Document processRequest(ApplicationContext applicationContext, Document request) {
        Map<String,OLSServiceProvider>  beans = applicationContext.getBeansOfType(OLSServiceProvider.class);
        OLSServiceProvider              provider;
        
        for (String beanName : beans.keySet()) {
            provider = beans.get(beanName);
            
            System.out.println(beanName + ": " + provider);
        }
        
        // TODO Auto-generated method stub
        return null;
    }

}
