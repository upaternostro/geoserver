package it.phoops.geoserver.ols.web.component;

import it.phoops.geoserver.ols.OLSServiceProvider;
import it.phoops.geoserver.ols.geocoding.GeocodingServiceProvider;
import it.phoops.geoserver.ols.util.ApplicationContextUtil;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.springframework.context.ApplicationContext;

/**
 * DropDownChoice component to select the service OLS
 *
 */
public class ServiceDropDownChoice extends DropDownChoice{
	private String 			selectedService = null;
	private Form			form;
	private String urlRFC59;
	private String timeoutRFC59;

	public ServiceDropDownChoice(String id, PropertyModel<String> model, List<String> displayData, Form form) {
			super(id,model,displayData);
			this.form = form;
	}

	
	@Override
	protected void onSelectionChanged(Object newSelection) {
		System.out.println("onSelectionChanged: "+ newSelection);
		
		super.onSelectionChanged(newSelection);
		setSelectedService(newSelection.toString());
		
		showPanelOLS(form);
		
	}

	@Override
	protected boolean wantOnSelectionChangedNotifications() {
		return true;
	}

	public String getSelectedService() {
		return selectedService;
	}


	public void setSelectedService(String selectedService) {
		this.selectedService = selectedService;
	}
	
	public void showPanelOLS(Form form){
		ApplicationContextUtil appContextUtil = ApplicationContextUtil.getIstance();
    	ApplicationContext appContext = appContextUtil.getAppContext();
    	//Choose the correct beans
   		Map<String,OLSServiceProvider>    beans = appContext.getBeansOfType(OLSServiceProvider.class);
    	
    	GeocodingServiceProvider                provider = null;
        
        for (String beanName : beans.keySet()) {
            System.out.println(beanName + ": " + provider);
        }
		
		
		List<ITab> tabsOLS = new ArrayList<ITab>();
        //Add the tab RFC59
    	
    	tabsOLS.add(new AbstractTab(new Model<String>("RFC59")) {
			
			@Override
			public Panel getPanel(String panelId) {
				return new RFC59Panel(panelId);
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
    	
    	form.remove("tabList");
        form.add(new TabbedPanel("tabList", tabsOLS));	
	}
	
	/**
     * RFC59 tabPanel
     *
     */
    private static class RFC59Panel extends Panel{
    	private String urlRFC59;
    	private String timeoutRFC59;
    	
    	public RFC59Panel(String id){
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

}
