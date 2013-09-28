package it.phoops.geoserver.ols.security;

import org.geoserver.security.config.SecurityAuthFilterConfig;
import org.geoserver.security.config.SecurityFilterConfig;

public class OLSAuthenticationFilterConfig extends SecurityFilterConfig implements SecurityAuthFilterConfig {
    public final static String  DEFAULT_OLS_ROLE_PARAM = "OLS_ROLE";
    
    private static final long serialVersionUID = 1L;
    
    private String olsRoleName;

    public OLSAuthenticationFilterConfig()
    {
        setClassName(OLSAuthenticationFilterConfig.class.getName());
        setName("OLS");
    }

    @Override
    public  boolean providesAuthenticationEntryPoint() {
        return false;
    }
    
    public String getOlsRoleName() {
        return olsRoleName;
    }
    public void setOlsRoleName(String olsRoleName) {
        this.olsRoleName = olsRoleName;
    }
}
