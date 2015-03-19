/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.routing.otp;

import it.phoops.geoserver.ols.routing.otp.component.OTP0110Tab;
import it.phoops.geoserver.ols.routing.otp.component.OTPTabFactory;

import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.ResourceModel;
import org.geoserver.config.ServiceInfo;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class OTP0110ServiceProvider extends OTPServiceProvider {

    /** serialVersionUID */
    private static final long serialVersionUID = 1L;
    //Properties Name
    private static final String  PN_ROUTER_ID           = "OLS.serviceProvider.geocoding.otp0110.service.routerId";
    
    public String getRouterId() {
        return getProperties().getProperty(PN_ROUTER_ID);
    }

    public void setRouterId(String routerId) {
        getProperties().setProperty(PN_ROUTER_ID, routerId);
    }
    
    @Override
    public ITab getTab() {
        IModel<String> title = new ResourceModel("OTP v0.11.0+", "OTP v0.11.0+");
        return OTPTabFactory.getOTPTabFactory().getOTP0110Tab(title);
    }

    @Override
    public void handleServiceChange(ServiceInfo service,
            List<String> propertyNames, List<Object> oldValues,
            List<Object> newValues) {
        super.handleServiceChange(service, propertyNames, oldValues, newValues);
        
        String routerId         = ((OTP0110Tab)getTab()).getRouterId();
        
        setRouterId(routerId);
    }

    @Override
    public void setPropertiesTab(ITab otpTab) {
        super.setPropertiesTab(otpTab);
        
        ((OTP0110Tab)otpTab).setRouterId(this.getRouterId());
    }

    @Override
    protected WebResource getWebResource(Client client)
    {
        return client.resource(getEndpointAddress()).path("ws/routers").path(getRouterId()).path("plan");
    }
}
