package it.phoops.geoserver.ols.web.component;

import it.phoops.geoserver.ols.OLSService;
import it.phoops.geoserver.ols.OLSServiceProviderGUI;
import it.phoops.geoserver.ols.util.ApplicationContextUtil;
import it.phoops.geoserver.ols.web.OLSAdminPage.OLSGUIService;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.extensions.markup.html.tabs.ITab;
import org.apache.wicket.extensions.markup.html.tabs.TabbedPanel;
import org.apache.wicket.markup.html.form.ChoiceRenderer;
import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.markup.html.form.Form;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.springframework.context.ApplicationContext;

/**
 * DropDownChoice component to select the service OLS
 *
 */
public class ServiceDropDownChoice extends DropDownChoice<OLSGUIService>{
	private OLSGUIService			selectedService = null;
	private Form					form;
	private String 					urlRFC59;
	private String 					timeoutRFC59;
	private List<OLSGUIService>		displayData;

	public ServiceDropDownChoice(String id, PropertyModel<OLSGUIService> model, List<OLSGUIService> displayData, Form form) {
			super(id,model,displayData);
			this.form = form;
			this.displayData = displayData;
	}
	
	public ServiceDropDownChoice(String id, PropertyModel<OLSGUIService> model, List<OLSGUIService> displayData, ChoiceRenderer cRenderer,Form form) {
		super(id,model,displayData, cRenderer);
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
    	
    	//Choose the correct beans
   		Map<String,OLSServiceProviderGUI>    beans = appContext.getBeansOfType(OLSServiceProviderGUI.class);
    	
   		OLSServiceProviderGUI                provider = null;
        
   		List<ITab> tabsOLS = new ArrayList<ITab>();
   		
        for (String beanName : beans.keySet()) {
        	provider = beans.get(beanName);
        	
            System.out.println(beanName + ": " + provider);
            System.out.println("Service Type : " + provider.getServiceType());
            if(provider.getServiceType() == selectedService){
            	ITab tab = provider.getTab();
            	provider.setPropertiesTab(tab);
            	tabsOLS.add(tab);
            	provider.getProperties();
//            }else if(provider.getServiceType() == selectedService){
//            	//Add the tab SOLR
//            	tabsOLS.add(new AbstractTab(new Model<String>("SOLR")) {
//        			
//        			@Override
//        			public Panel getPanel(String panelId) {
//        				return new SOLRPanel(panelId);
//        			}
//        		});
            }
        }
    	
    	
    	form.remove("tabList");
//    	TabbedPanel tabPanel = new TabbedPanel("tabList", tabsOLS);
//    	if(tabsOLS.size() == 0){
//    		tabPanel.setVisible(Boolean.FALSE);
//    	}else{
//    		tabPanel.setVisible(Boolean.TRUE);
//    	}
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
	
//	private String findKeyServiceSelected(String serviceSelected){
//		for (OLSGUIService element : this.getChoices()) {
//			if(element.toString().equalsIgnoreCase(serviceSelected)){
//				return element.getCode();
//			}
//		}
//		return null;
//	}

}
