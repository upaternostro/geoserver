package it.phoops.geoserver.ols.security;

import org.geoserver.security.web.auth.AuthenticationFilterPanelInfo;

public class OLSAuthFilterPanelInfo extends AuthenticationFilterPanelInfo<OLSAuthenticationFilterConfig, OLSAuthFilterPanel>
{
    private static final long serialVersionUID = 1L;

    public OLSAuthFilterPanelInfo() {
        setServiceClass(OLSAuthenticationFilter.class);
        setServiceConfigClass(OLSAuthenticationFilterConfig.class);
        setComponentClass(OLSAuthFilterPanel.class);
    }
}
