package it.phoops.geoserver.ols.web;

import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.model.IModel;
import org.geoserver.web.services.BaseServiceAdminPage;
import org.geoserver.wms.WMSInfo;


/**
 * 
 * @author aCasini
 *
 */
public class OLSAdminPage extends BaseServiceAdminPage<WMSInfo> {

	@Override
	protected void build(IModel info, Form form) {
		// TODO Auto-generated method stub
		form.add( new Label( "hellolabel", "Hello World!") );
	}

	@Override
	protected Class<WMSInfo> getServiceClass() {
		// TODO Da sostituire con OLSInfo
		return WMSInfo.class;
	}

	@Override
	protected String getServiceName() {
		// TODO Da sostituire con OLS
		return "WMS";
	}

}
