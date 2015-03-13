/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.routing.otp.component;

import org.apache.wicket.model.IModel;

public class OTPTabFactory {
    private static OTPTabFactory factory = null;
    
    private OTPTab instance = null;
    private OTP0110Tab instance0110 = null;
    
    public static OTPTabFactory getOTPTabFactory() {
        if (factory == null) {
            factory = new OTPTabFactory();
        }
    
        return factory;
    }
    
    public OTPTab getOTPTab(IModel<String> title) {
        if (instance == null) {
            instance = new OTPTab(title);
        }
    
        return instance;
    }
    
    public OTPTab getOTP0110Tab(IModel<String> title) {
        if (instance0110 == null) {
            instance0110 = new OTP0110Tab(title);
        }
    
        return instance0110;
    }
}
