package it.phoops.geoserver.ols.web.component;

import it.phoops.geoserver.ols.OLS;
import it.phoops.geoserver.ols.OLSAbstractServiceProvider;
import it.phoops.geoserver.ols.OLSInfo;
import it.phoops.geoserver.ols.OLSService;
import it.phoops.geoserver.ols.util.ApplicationContextUtil;
import it.phoops.geoserver.ols.web.OLSAdminPage.OLSGUIService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.PropertyModel;
import org.geoserver.config.GeoServer;
import org.springframework.context.ApplicationContext;

/**
 * DropDownChoice component to select the service OLS
 *
 */
public class ServiceDropDownChoice extends DropDownChoice<OLSGUIService>{
	private OLSGUIService			selectedService = null;
	private Form				form;
	private String 				urlRFC59;
	private String 				timeoutRFC59;
	private List<OLSGUIService>		displayData;

	public ServiceDropDownChoice(String id, PropertyModel<OLSGUIService> model, List<OLSGUIService> displayData, Form form) {
	    super(id,model,displayData);
	    this.form = form;
	    this.displayData = displayData;
	}
	
	@Override
	protected void onSelectionChanged(OLSGUIService newSelection) {
	    System.out.println("onSelectionChanged: "+ newSelection);
	    super.onSelectionChanged(newSelection);
	    setSelectedService(newSelection);
	    showPanelOLS(form);
	}

	@Override
	protected boolean wantOnSelectionChangedNotifications() {
	    return true;
	}

	public OLSGUIService getSelectedService() {
	    return selectedService;
	}


	public void setSelectedService(OLSGUIService selectedService) {
	    this.selectedService = selectedService;
	}
	
	private void showPanelOLS(Form form){
	    ApplicationContextUtil appContextUtil = ApplicationContextUtil.getIstance();
    	    ApplicationContext appContext = appContextUtil.getAppContext();
    	
    	    OLSService selectedService = getSelectedService().getService();
    	
    	    OLS ols = OLS.get();
    	    GeoServer gs = ols.getGeoServer();
    	    OLSInfo	olsInfo = gs.getService(OLSInfo.class);
    	    OLSAbstractServiceProvider	activeProvider = (OLSAbstractServiceProvider)olsInfo.getServiceProvider(selectedService);
    	
    	    for (Object listener : gs.getListeners().toArray()) {
    	        if (listener instanceof OLSAbstractServiceProvider) {
    	            gs.removeListener((OLSAbstractServiceProvider)listener);
    	        }
    	    }
    	
    	//Choose the correct beans
    	    Map<String,OLSAbstractServiceProvider>    beans = appContext.getBeansOfType(OLSAbstractServiceProvider.class);
    	
    	    OLSAbstractServiceProvider                provider = null;
        
    	    List<ITab> tabsOLS = new ArrayList<ITab>();
   		
        for (String beanName : beans.keySet()) {
        	provider = beans.get(beanName);
        	
            System.out.println(beanName + ": " + provider);
            System.out.println("Service Type : " + provider.getServiceType());
            if(provider.getServiceType() == selectedService){
            	if (activeProvider != null) {
            		if (activeProvider instanceof OLSAbstractServiceProvider && provider.getClass().equals(activeProvider.getClass())) {
            			provider = (OLSAbstractServiceProvider)activeProvider;
                                gs.addListener(provider);
            		}else{
            		    gs.addListener(provider);
            		}
            	} else {
            		olsInfo.setServiceProvider(selectedService, provider);
            		activeProvider = provider;
                        gs.addListener(provider);
            	}
            	ITab tab = provider.getTab();
            	provider.setPropertiesTab(tab);
            	tabsOLS.add(tab);
            }
        }
    	
    	
    	form.remove("tabList");
        form.add(new TabbedPanel("tabList", tabsOLS));	
	}
	
	
	
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
