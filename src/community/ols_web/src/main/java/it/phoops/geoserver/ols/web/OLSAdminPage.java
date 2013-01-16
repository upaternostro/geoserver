package it.phoops.geoserver.ols.web;

import it.phoops.geoserver.ols.OLSInfo;
import it.phoops.geoserver.ols.web.component.ServiceDropDownChoice;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.Localizer;
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
	public class OLSGUIService implements Serializable {
		private String	code;
		private String descriptionKey;
		private Component component;
		
		public OLSGUIService() {
		}
		
		public OLSGUIService(String code, String descriptionKey, Component component) {
			super();
			this.code = code;
			this.descriptionKey = descriptionKey;
			this.component = component;
		}

		public String getCode() {
			return code;
		}

		public void setCode(String code) {
			this.code = code;
		}

		public String getDescriptionKey() {
			return descriptionKey;
		}

		public void setDescriptionKey(String descriptionKey) {
			this.descriptionKey = descriptionKey;
		}
		
		public Component getComponent() {
			return component;
		}

		public void setComponent(Component component) {
			this.component = component;
		}

		@Override
		public String toString() {
			Localizer	localizer = Application.get().getResourceSettings().getLocalizer();
			return localizer.getString(descriptionKey, component);
		}
	}
	
	private List<ITab> 				tabsOLS = null;
	private List<OLSGUIService>		SERVICES = null;
	private TabbedPanel				tabPanelOLS = null;
	private String 					selectedService = null;
	
	private String 					urlRFC59;
	private String 					timeoutRFC59;
	
    @Override
    protected void build(IModel info, Form form) {
        
        SERVICES = new ArrayList<OLSGUIService>();
        SERVICES.add(new OLSGUIService("1", "OLSGUIService.geocoding", this));
        SERVICES.add(new OLSGUIService("2", "OLSGUIService.reverseGeocoding", this));
        SERVICES.add(new OLSGUIService("3", "OLSGUIService.routingNavigation", this));
        
        ServiceDropDownChoice listServices = new ServiceDropDownChoice("service", new PropertyModel<String>(this, "selectedService"), SERVICES, form);
		form.add(listServices);

		if(listServices.getSelectedService() == null){
			//Do nothing - non visualizzare il tab panel
			if(tabsOLS == null){
				tabsOLS = new ArrayList<ITab>();
			}
			tabPanelOLS = new TabbedPanel("tabList", tabsOLS);
			tabPanelOLS.setVisible(false);
			form.add(tabPanelOLS);
		}else{
		
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
	    	
	    	form.remove(tabPanelOLS);
	        form.add(new TabbedPanel("tabList", tabsOLS));
		}
    }

    @Override
    protected Class<OLSInfo> getServiceClass() {
        return OLSInfo.class;
    }

    @Override
    protected String getServiceName() {
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
	}

	public TabbedPanel getTabPanelOLS() {
		return tabPanelOLS;
	}

	public void setTabPanelOLS(TabbedPanel tabPanelOLS) {
		this.tabPanelOLS = tabPanelOLS;
	}

	public String getSelectedService() {
		return selectedService;
	}

	public void setSelectedService(String selectedService) {
		this.selectedService = selectedService;
	}
	
}
