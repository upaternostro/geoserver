package it.phoops.geoserver.ols;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.XMLConstants;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import net.opengis.www.xls.AbstractResponseParametersType;
import net.opengis.www.xls.DetermineRouteRequestType;
import net.opengis.www.xls.ObjectFactory;
import net.opengis.www.xls.RequestHeaderType;
import net.opengis.www.xls.RequestType;
import net.opengis.www.xls.ResponseHeaderType;
import net.opengis.www.xls.ResponseType;
import net.opengis.www.xls.XLS;

import org.apache.xerces.util.XMLCatalogResolver;
import org.geoserver.config.ServiceInfo;
import org.geoserver.config.impl.DefaultGeoServerFacade;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.w3c.dom.Document;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;

public class OLSDispatcher extends AbstractController
{
    public static final String          OLS_VERSION = "1.2";
    
    private Map<String,OLSHandler>      handlers = new HashMap<String,OLSHandler>();
    private static final String         DEFAULT_WS = null;
    
    public OLSDispatcher()
    {
    }
    
    public void setHandlers(Map<String,OLSHandler> handlers)
    {
        this.handlers = handlers;
    }
    
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest httpRequest, HttpServletResponse httpResponse) throws Exception
    {
        if (METHOD_POST.equals(httpRequest.getMethod())) {
            DocumentBuilderFactory      domFactory = DocumentBuilderFactory.newInstance();
            SchemaFactory               schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            
            schemaFactory.setResourceResolver(new XMLCatalogResolver(new String[]{getClass().getResource("/xsd/catalog.xml").toExternalForm()}));
            
            Schema      olsSchema = schemaFactory.newSchema(getClass().getResource("/xsd/OLS/XLS.xsd"));
            
            domFactory.setIgnoringComments(true);
            domFactory.setIgnoringElementContentWhitespace(true);
            domFactory.setNamespaceAware(true);
            domFactory.setSchema(olsSchema);
            
            DocumentBuilder                     domBuilder = domFactory.newDocumentBuilder();
            OLSDispatcherSAXErrorHandler        errorHandler = new OLSDispatcherSAXErrorHandler();
            
            domBuilder.setErrorHandler(errorHandler);
            
            Document    domRequest = domBuilder.parse(httpRequest.getInputStream());
            
            if (!errorHandler.getError()) {
                XPath           xPath = XPathFactory.newInstance().newXPath();
                XPathExpression xPathExpr;
                
                httpResponse.setContentType("text/xml");
                
                String[]        requestArray = httpRequest.getRequestURI().split("/");
                String          workspaceName = "";
                
                if (requestArray.length > 3) {
                    workspaceName = requestArray[3];
                } else {
                    workspaceName = DEFAULT_WS;
                }
                
                for (String path : handlers.keySet()) {
                    xPathExpr = xPath.compile(path);
                    
                    if ((Boolean)xPathExpr.evaluate(domRequest, XPathConstants.BOOLEAN)) {
                        OLSHandler          handler = handlers.get(path);
                        boolean             wsFound = false;
                        // Set configured service provider for this service handler
                        DefaultGeoServerFacade dGeoServer = (DefaultGeoServerFacade)OLS.get().getFacade();
                        OLSInfo olsInfo = null;
                        OLSInfo defaultOlsInfo = null;
                        
                        //Set the correct workspace
                        for (ServiceInfo sInfo : dGeoServer.getAllServices()) {
                            if (sInfo.getClass().equals(OLSInfoImpl.class)) {
                                if (sInfo.getWorkspace() != null && sInfo.getWorkspace().getName().equalsIgnoreCase(workspaceName)) {
                                    olsInfo = (OLSInfo) sInfo;
                                    wsFound = true;
                                }
                                
                                if (sInfo.getWorkspace() == null) {
                                    defaultOlsInfo = (OLSInfo)sInfo;
                                }
                            }
                        }
                        
                        if (!wsFound) {
                            logger.warn("No workspace found, switching to default!");
                            olsInfo = defaultOlsInfo;
                        }
                        
                        OLSServiceProvider sProvidereToSet = OLS.get().getServiceProvider(handler.getService());
                        
                        for (OLSServiceProvider sProvider : olsInfo.getServiceProvider()) {
                            if (sProvider.getServiceType() == handler.getService()) {
                                sProvidereToSet = sProvider;
                                break;
                            }
                        }
                        
                        handler.setActiveServiceProvider(sProvidereToSet);
                        
                        JAXBContext                     jaxbContext = null;
                        
                        try{
                            jaxbContext = JAXBContext.newInstance(DetermineRouteRequestType.class);
                            
                            Unmarshaller                                                unmarshaller = jaxbContext.createUnmarshaller();
                            JAXBElement<XLS>                                            jaxbElement = unmarshaller.unmarshal(domRequest.getFirstChild(), XLS.class);
                            XLS                                                         input = jaxbElement.getValue();
                            RequestHeaderType                                           inputHeader = (RequestHeaderType)input.getHeader().getValue(); 
                            String                                                      sessionId = inputHeader.getSessionID();
                            JAXBElement<? extends AbstractResponseParametersType>       response = handler.processRequest((RequestType)input.getBodies().get(0).getValue(), input.getLang(), inputHeader.getSrsName());
                            ObjectFactory                                               of = new ObjectFactory();
                            ResponseHeaderType                                          outputHeader = of.createResponseHeaderType();
                            ResponseType                                                outputBody = of.createResponseType();
                            XLS                                                         output = of.createXLS();
                            Marshaller                                                  marshaller = jaxbContext.createMarshaller();
                            
                            if (sessionId != null && !"".equals(sessionId)) {
                                outputHeader.setSessionID(sessionId);
                            }
                            
                            outputBody.setResponseParameters(response);
                            
                            output.setHeader(of.createHeader(outputHeader));
                            output.getBodies().add(of.createBody(outputBody));
                            
                            output.setLang(input.getLang());
                            output.setVersion(new BigDecimal(OLS_VERSION));
                            
                            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
                            
                            marshaller.marshal(output, httpResponse.getOutputStream());
                            break;
                        } catch (JAXBException e) {
                            httpResponse.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                            httpResponse.getWriter().println("Error (un)marshalling XML payload: " + e.getMessage());
                            e.printStackTrace(httpResponse.getWriter());
                        }
                    }
                }
            } else {
                httpResponse.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                httpResponse.getWriter().println("Error parsing XML payload!");
                
                for (SAXException e : errorHandler.getExceptions()) {
                    e.printStackTrace(httpResponse.getWriter());
                }
            }
        } else {
            httpResponse.setStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
            httpResponse.getWriter().println("Invoke Open LS service via POST method!");
        }
        
        return null;
    }
    
    class OLSDispatcherSAXErrorHandler implements ErrorHandler {
        private boolean                 error = false;
        private List<SAXParseException> exceptions = new ArrayList<SAXParseException>();

        @Override
        public void error(SAXParseException arg0) throws SAXException {
            error = true;
            exceptions.add(arg0);
        }

        @Override
        public void fatalError(SAXParseException arg0) throws SAXException {
            error = true;
            exceptions.add(arg0);
        }

        @Override
        public void warning(SAXParseException arg0) throws SAXException {
            exceptions.add(arg0);
        }
        
        public boolean getError() {
            return error;
        }
        
        public List<SAXParseException> getExceptions() {
            return exceptions;
        }
    }
}
