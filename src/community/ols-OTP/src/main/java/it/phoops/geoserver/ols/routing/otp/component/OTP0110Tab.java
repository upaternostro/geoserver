/* Copyright (c) 2001 - 2013 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.routing.otp.component;

import it.phoops.geoserver.ols.OLSServiceProvider;
import it.phoops.geoserver.ols.routing.otp.OTP0110ServiceProvider;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class OTP0110Tab extends OTPTab
{
    private String      routerId;

    public String getRouterId() {
        if(instancePanel != null)
            return ((OTP0110Panel)instancePanel).getRouterId();
        return routerId;
    }

    public void setRouterId(String routerId) {
        if(instancePanel != null)
            ((OTP0110Panel)instancePanel).setRouterId(routerId);
        this.routerId = routerId;
    }


    public OTP0110Tab(IModel<String> title) {
        super(title);
    }

    @Override
    public Panel getPanel(String panelId) {
        instancePanel = new OTP0110Panel(panelId);
        
        setupPanel();
        
        ((OTP0110Panel)instancePanel).setRouterId(routerId);
        
        return instancePanel;
    }
    
    protected static class OTP0110Panel extends OTPPanel
    {
        private String  routerId;

        public OTP0110Panel(String id) {
            super(id);
            
            add(new TextField<String>("routerId",new PropertyModel<String>(this,"routerId")));
        }

        protected boolean isMyClass(OLSServiceProvider provider)
        {
            return provider instanceof OTP0110ServiceProvider;
        }
        
        public String getRouterId() {
            return routerId;
        }

        public void setRouterId(String routerId) {
            this.routerId = routerId;
        }

    }
}
