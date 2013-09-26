package it.phoops.geoserver.ols.geocoding;

import it.phoops.geoserver.ols.OLSException;
import it.phoops.geoserver.ols.OLSHandler;
import it.phoops.geoserver.ols.OLSService;
import it.phoops.geoserver.ols.OLSServiceProvider;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.opengis.www.xls.GeocodeRequestType;
import net.opengis.www.xls.GeocodeResponseType;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class GeocodingHandler implements OLSHandler {
    private GeocodingServiceProvider    provider;

    @Override
    public Document processRequest(Node request) throws OLSException
    {
        JAXBContext             jaxbContext = null;
        GeocodeRequestType      input = null;
        NodeList                nodeList = request.getChildNodes();
        
        request = null;
        
        for (int i = 0; i < nodeList.getLength(); i++) {
            if (nodeList.item(i).getNodeName().equals("GeocodeRequest")) {
                request = nodeList.item(i);
                break;
            }
        }
        
        if (request == null) {
            throw new OLSException("Request not found");
        }
        
        
        try {
            jaxbContext = JAXBContext.newInstance(GeocodeRequestType.class);
            
            Unmarshaller        unmarshaller = jaxbContext.createUnmarshaller();
            JAXBElement<GeocodeRequestType>     jaxbElement = unmarshaller.unmarshal(request, GeocodeRequestType.class);
            
            input = jaxbElement.getValue();
        } catch (JAXBException e) {
            throw new OLSException("JAXB error", e);
        }
        
        JAXBElement<GeocodeResponseType>        output = provider.geocode(input);
        Document                                domResponse = null;
        
        try {
            Marshaller              marshaller = jaxbContext.createMarshaller();
            DocumentBuilderFactory  domFactory = DocumentBuilderFactory.newInstance();
            
            domFactory.setNamespaceAware(true);
            
            DocumentBuilder         domBuilder = domFactory.newDocumentBuilder();
            
            domResponse = domBuilder.newDocument();
            marshaller.marshal(output, domResponse);
        } catch (JAXBException e) {
            throw new OLSException("JAXB error: " + e.getLocalizedMessage(), e);
        } catch (ParserConfigurationException e) {
            throw new OLSException("JAXP error: " + e.getLocalizedMessage(), e);
        }
        
        return domResponse;
    }

    @Override
    public OLSService getService() {
        return OLSService.GEOCODING;
    }

    @Override
    public void setServiceProvider(OLSServiceProvider provider) {
        this.provider = (GeocodingServiceProvider)provider;
    }

    @Override
    public void setActiveServiceProvider(OLSServiceProvider provider) {
        if(provider.isServiceActive())
            this.provider = (GeocodingServiceProvider)provider;
    }
    
    
}
