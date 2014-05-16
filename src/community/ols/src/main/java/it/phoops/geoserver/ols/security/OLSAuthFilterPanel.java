/* Copyright (c) 2001 - 2013 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.security;

import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.model.IModel;
import org.geoserver.security.web.auth.AuthenticationFilterPanel;

public class OLSAuthFilterPanel extends AuthenticationFilterPanel<OLSAuthenticationFilterConfig>
{
    private static final long serialVersionUID = 1L;

    public OLSAuthFilterPanel(String id, IModel<OLSAuthenticationFilterConfig> model) {
        super(id, model);
        
        add(new TextField("olsRoleName"));
    }
}
