package it.phoops.geoserver.ols;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;
import org.w3c.dom.Document;

public class OLSDispatcher extends AbstractController {
    private Map<String,OLSHandler>  handlers = new HashMap<String,OLSHandler>();
    
    public OLSDispatcher()
    {
    }
    
    public void setHandlers(Map<String,OLSHandler> handlers)
    {
        this.handlers = handlers;
    }
    
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest httpRequest,
            HttpServletResponse httpResponse) throws Exception {
        if (METHOD_POST.equals(httpRequest.getMethod())) {
            DocumentBuilderFactory      domFactory = DocumentBuilderFactory.newInstance();
            
            domFactory.setNamespaceAware(true);
            
            DocumentBuilder             domBuilder = domFactory.newDocumentBuilder();
            Document                    domRequest = domBuilder.parse(httpRequest.getInputStream());
            XPath                       xPath = XPathFactory.newInstance().newXPath();
            XPathExpression             xPathExpr;
            
            for (String path : handlers.keySet()) {
                xPathExpr = xPath.compile(path);
                
                if ((Boolean)xPathExpr.evaluate(domRequest, XPathConstants.BOOLEAN)) {
                    OLSHandler          handler = handlers.get(path);
                    Document            domResponse = handler.processRequest(getApplicationContext(), domRequest);
                    TransformerFactory  transFactory = TransformerFactory.newInstance();
                    Transformer         transformer = transFactory.newTransformer();
                    
//                    transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
                    transformer.transform(new DOMSource(domResponse.getFirstChild()),
                          new StreamResult(httpResponse.getOutputStream()));
                    break;
                }
            }
        } else {
            httpResponse.getWriter().println("Invoke Open LS service via POST method!");
        }
        
        return null;
    }
}
