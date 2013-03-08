package it.phoops.geoserver.ols.routing.otp;

import it.phoops.geoserver.ols.OLSAbstractServiceProvider;
import it.phoops.geoserver.ols.OLSException;
import it.phoops.geoserver.ols.OLSService;
import it.phoops.geoserver.ols.routing.RoutingServiceProvider;
import it.phoops.geoserver.ols.routing.otp.client.OpentripplannerApiWebapp;
import it.phoops.geoserver.ols.routing.otp.client.OpentripplannerApiWebapp.WsPlan;
import it.phoops.geoserver.ols.routing.otp.client.ns0.Response;
import it.phoops.geoserver.ols.routing.otp.component.OTPTab;
import it.phoops.geoserver.ols.routing.otp.component.OTPTabFactory;

import java.text.DateFormat;
import java.util.List;
import java.util.Properties;

import javax.xml.bind.JAXBElement;
import javax.xml.datatype.XMLGregorianCalendar;

import net.opengis.www.xls.AbstractLocationType;
import net.opengis.www.xls.DetermineRouteRequestType;
import net.opengis.www.xls.DetermineRouteResponseType;
import net.opengis.www.xls.PositionType;
import net.opengis.www.xls.RoutePlan;
import net.opengis.www.xls.WayPointList;
import net.opengis.www.xls.WayPointType;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.geoserver.config.ServiceInfo;

public class OTPServiceProvider extends OLSAbstractServiceProvider implements RoutingServiceProvider {
    //Properties Name
    private static final String  PN_ENDPOINT_ADDRESS = "OLS.serviceProvider.geocoding.otp.service.endpointAddress";
    private static final String  PN_PORT_NUMBER = "OLS.serviceProvider.geocoding.otp.service.portNumber";
    private static final String  PN_ACTIVE_SERVICE = "OLS.serviceProvider.service.active";
    
    private String      descriptionKey;
    private Properties  properties = new Properties();
    
    @Override
    public String getDescriptionKey() {
        return descriptionKey;
    }
    
    public void setDescriptionKey(String description) {
        this.descriptionKey = description;
    }
    
    public String getEndpointAddress() {
        return properties.getProperty(PN_ENDPOINT_ADDRESS);
    }

    public void setEndpointAddress(String endpointAddress) {
        properties.setProperty(PN_ENDPOINT_ADDRESS, endpointAddress);
    }
    
    public String getPortNumber() {
        return properties.getProperty(PN_PORT_NUMBER);
    }

    public void setPortNumber(String portNumber) {
        properties.setProperty(PN_PORT_NUMBER, portNumber);
    }
    
    public String getActive(){
        return properties.getProperty(PN_ACTIVE_SERVICE);
    }
    
    public void setActive(String activeService){
        properties.setProperty(PN_ACTIVE_SERVICE, activeService);
    }
    
    @Override
    public Properties getProperties() {
        return properties;
    }
    
    @Override
    public OLSService getServiceType() {
        return OLSService.ROUTING_NAVIGATION;
    }

    @Override
    public ITab getTab() {
        IModel<String> title = new ResourceModel("OTP ", "OTP");
        return OTPTabFactory.getOTPTabFactory().getOTPTab(title);
    }

    @Override
    public void handleServiceChange(ServiceInfo service,
            List<String> propertyNames, List<Object> oldValues,
            List<Object> newValues) {
        String url = ((OTPTab)getTab()).getUrlOTP();
        String port = ((OTPTab)getTab()).getPortOTP();
        String active = ((OTPTab)getTab()).getActiveOTP();
        
        setEndpointAddress(url);
        setPortNumber(port);
        setActive(active);
        
    }

    @Override
    public boolean isServiceActive() {
        return Boolean.parseBoolean(this.getActive());
    }

    @Override
    public void setPropertiesTab(ITab otpTab) {
        ((OTPTab)otpTab).setUrlOTP(this.getEndpointAddress());
        ((OTPTab)otpTab).setActiveOTP(this.getActive());
        ((OTPTab)otpTab).setPortOTP(this.getPortNumber());
    }

    @Override
    public JAXBElement<DetermineRouteResponseType> geocode(
            DetermineRouteRequestType input) throws OLSException {
        RoutePlan plan = input.getRoutePlan();
        
        if (plan == null) {
            throw new OLSException("Route plan is missing");
        }
        
        WayPointList                                    wpList = plan.getWayPointList();
        WayPointType                                    startPoint = wpList.getStartPoint();
        JAXBElement<? extends AbstractLocationType>     startLocation = startPoint.getLocation();
        
        if (!(startLocation.getValue() instanceof PositionType)) {
            throw new OLSException("Unsupported start point location");
        }
        
        PositionType                                    startPosition = (PositionType)startLocation.getValue();
        WayPointType                                    endPoint = wpList.getEndPoint();
        JAXBElement<? extends AbstractLocationType>     endLocation = endPoint.getLocation();
        
        if (!(endLocation.getValue() instanceof PositionType)) {
            throw new OLSException("Unsupported end point location");
        }
        
        PositionType    endPosition = (PositionType)endLocation.getValue();
        
        XMLGregorianCalendar    cal = plan.getExpectedEndTime();
        boolean                 endTime = true;
        
        if (cal == null) {
            cal = plan.getExpectedStartTime();
            endTime = false;
        }

        DateFormat dateFormat = DateFormat.getDateInstance(DateFormat.SHORT);
        DateFormat timeFormat = DateFormat.getTimeInstance(DateFormat.SHORT);

        WsPlan planner = OpentripplannerApiWebapp.wsPlan();
        
        Response r = planner.getResponseAsTextXml(
                formatPosition(startPosition), // fromplace, 
                formatPosition(endPosition), // toplace, 
                "", // intermediateplaces, 
                "", // intermediateplacesordered, 
                cal == null ? "" : dateFormat.format(cal.toGregorianCalendar().getTime()), // date, 
                cal == null ? "" : timeFormat.format(cal.toGregorianCalendar().getTime()), // time, 
                "", // routerid, 
                cal == null ? "" : Boolean.toString(endTime), // arriveby, 
                "", // wheelchair, 
                "", // maxwalkdistance, 
                "", // walkspeed, 
                "", // bikespeed, 
                "", // trianglesafetyfactor, 
                "", // triangleslopefactor, 
                "", // triangletimefactor, 
                "", // optimize, 
                "", // mode, 
                "", // mintransfertime, 
                "", // numitineraries, 
                "", // preferredroutes, 
                "", // unpreferredroutes, 
                "", // showintermediatestops, 
                "", // bannedroutes, 
                "", // transferpenalty, 
                "", // maxtransfers, 
                "", // batch, 
                "", // starttransitstopid, 
                "", // clampinitialwait, 
                "", // reverseoptimizeonthefly, 
                "", // boardslack, 
                "", // alightslack, 
                "" // locale
        );
        
        return null;
    }
    
    private String formatPosition(PositionType position)
    {
        StringBuffer    sb = new StringBuffer();
        
        for (Double coord : position.getPoint().getPos().getValues()) {
            if (sb.length() != 0) {
                sb.append(" ");
            }
            
            sb.append(coord);
        }
        
        return sb.toString();
    }
}
