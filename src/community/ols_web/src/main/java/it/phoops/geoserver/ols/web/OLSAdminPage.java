package it.phoops.geoserver.ols.web;

import it.phoops.geoserver.ols.OLSInfo;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.geoserver.web.services.BaseServiceAdminPage;

/**
 * 
 * @author aCasini
 * 
 */
public class OLSAdminPage extends BaseServiceAdminPage<OLSInfo> {
	
	private List<ITab> tabsOLS = null;
	private String urlRFC59;
	private String timeoutRFC59;
	
    @Override
    protected void build(IModel info, Form form) {
        // TODO Auto-generated method stub
    	tabsOLS = new ArrayList<ITab>();
        //Add the tab RFC59
    	tabsOLS.add(new AbstractTab(new Model<String>("RFC59")) {
			
			@Override
			public Panel getPanel(String panelId) {
				return new RFCP59Panel(panelId);
			}
		});
        
    	//Add the tab SOLR
    	tabsOLS.add(new AbstractTab(new Model<String>("SOLR")) {
			
			@Override
			public Panel getPanel(String panelId) {
				return new SOLRPanel(panelId);
			}
		});
    	
    	//Add the tab OTP
    	tabsOLS.add(new AbstractTab(new Model<String>("OTP")) {
			
			@Override
			public Panel getPanel(String panelId) {
				return new OTPPanel(panelId);
			}
		});
    	
        form.add(new TabbedPanel("tabList", tabsOLS));
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
    
    /**
     * RFC59 tabPanel
     *
     */
    private static class RFCP59Panel extends Panel{
    	private String urlRFC59;
    	private String timeoutRFC59;
    	
    	public RFCP59Panel(String id){
    		super(id);
    		add(new TextField("urlRFC59",new PropertyModel(this,"urlRFC59")));
    		add(new TextField("timeoutRFC59",new PropertyModel(this,"timeoutRFC59")));
    	}

		public String getUrlRFC59() {
			return urlRFC59;
		}

		public void setUrlRFC59(String urlRFC59) {
			this.urlRFC59 = urlRFC59;
		}

		public String getTimeoutRFC59() {
			return timeoutRFC59;
		}

		public void setTimeoutRFC59(String timeoutRFC59) {
			this.timeoutRFC59 = timeoutRFC59;
		}
    	
    };
    
    /**
     * SOLR tabPanel
     *
     */
    private static class SOLRPanel extends Panel{
    	public SOLRPanel(String id){
    		super(id);
    	}
    };
    
    /**
     * OTP tabPanel
     *
     */
    private static class OTPPanel extends Panel{
    	public OTPPanel(String id){
    		super(id);
    	}
    }

	public String getUrlRFC59() {
		return urlRFC59;
	}

	public void setUrlRFC59(String urlRFC59) {
		this.urlRFC59 = urlRFC59;
	}

	public String getTimeoutRFC59() {
		return timeoutRFC59;
	}

	public void setTimeoutRFC59(String timeoutRFC59) {
		this.timeoutRFC59 = timeoutRFC59;
	};
    
    
}
