package it.phoops.geoserver.ols.web;

import it.phoops.geoserver.ols.OLSInfo;

import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.model.IModel;
import org.geoserver.web.services.BaseServiceAdminPage;

/**
 * 
 * @author aCasini
 * 
 */
public class OLSAdminPage extends BaseServiceAdminPage<OLSInfo> {
    @Override
    protected void build(IModel info, Form form) {
        // TODO Auto-generated method stub
        form.add(new Label("hellolabel", "Hello World!"));
    }

    @Override
    protected Class<OLSInfo> getServiceClass() {
        // TODO Da sostituire con OLSInfo
        return OLSInfo.class;
    }

    @Override
    protected String getServiceName() {
        // TODO Da sostituire con OLS
        return "OLS";
    }
}
