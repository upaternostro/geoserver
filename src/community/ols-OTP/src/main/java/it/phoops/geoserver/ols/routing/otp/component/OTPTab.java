/* Copyright (c) 2001 - 2013 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.routing.otp.component;

import it.phoops.geoserver.ols.OLS;
import it.phoops.geoserver.ols.OLSInfo;
import it.phoops.geoserver.ols.OLSService;
import it.phoops.geoserver.ols.OLSServiceProvider;
import it.phoops.geoserver.ols.OLSServiceProviderGUI;
import it.phoops.geoserver.ols.routing.Language;
import it.phoops.geoserver.ols.routing.LanguageDropDownChoice;
import it.phoops.geoserver.ols.routing.LanguageType;
import it.phoops.geoserver.ols.routing.otp.OTPServiceProvider;
import it.phoops.geoserver.ols.web.validator.ValidateCheckboxTab;

import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;
import org.geoserver.config.GeoServer;

public class OTPTab extends AbstractTab implements ValidateCheckboxTab{
    private String              urlOTP;
    private String              activeOTP;
    private String              navigationInfoOTP;
    private String              navigationInfoShortOTP;
    private String              navigationInfoRelOTP;
    private List<LanguageType>  languageList = null;
    private LanguageType        selectedLanguage;
    protected OTPPanel          instancePanel;
    private int                 codeLanguageSelected;

    public String getUrlOTP() {
        if(instancePanel != null)
            return instancePanel.getUrlOTP();
        return urlOTP;
    }

    public void setUrlOTP(String urlOTP) {
        if(instancePanel != null)
            instancePanel.setUrlOTP(urlOTP);
        this.urlOTP = urlOTP;
    }

    public String getActiveOTP() {
        if(instancePanel != null)
            return instancePanel.getActiveOTP();
        return activeOTP;
    }

    public void setActiveOTP(String activeOTP) {
        if(instancePanel != null)
            instancePanel.setActiveOTP(activeOTP);
        this.activeOTP = activeOTP;
    }

    public String getNavigationInfoOTP() {
        if(instancePanel != null)
            return instancePanel.getNavigationInfoOTP();
        return navigationInfoOTP;
    }

    public void setNavigationInfoOTP(String navigationInfoOTP) {
        if(instancePanel != null)
            instancePanel.setNavigationInfoOTP(navigationInfoOTP);
        this.navigationInfoOTP = navigationInfoOTP;
    }

    public String getNavigationInfoShortOTP() {
        if(instancePanel != null)
            return instancePanel.getNavigationInfoShortOTP();
        return navigationInfoShortOTP;
    }

    public void setNavigationInfoShortOTP(String navigationInfoShortOTP) {
        if(instancePanel != null)
            instancePanel.setNavigationInfoShortOTP(navigationInfoShortOTP);
        this.navigationInfoShortOTP = navigationInfoShortOTP;
    }

    public String getNavigationInfoRelOTP() {
        if(instancePanel != null)
            return instancePanel.getNavigationInfoRelOTP();
        return navigationInfoRelOTP;
    }

    public void setNavigationInfoRelOTP(String navigationInfoRelOTP) {
        if(instancePanel != null)
            instancePanel.setNavigationInfoRelOTP(navigationInfoRelOTP);
        this.navigationInfoRelOTP = navigationInfoRelOTP;
    }

    public LanguageType getSelectedLanguage() {
        if(instancePanel != null)
            return instancePanel.getSelectedLanguage();
        return selectedLanguage;
    }

    public void setSelectedLanguage(LanguageType selectedLanguage) {
        if(instancePanel != null)
            instancePanel.setSelectedLanguage(selectedLanguage);
        this.selectedLanguage = selectedLanguage;
    }

    public OTPPanel getInstancePanel() {
        return instancePanel;
    }

    public void setInstancePanel(OTPPanel instancePanel) {
        this.instancePanel = instancePanel;
    }

    public int getCodeLanguageSelected() {
        return codeLanguageSelected;
    }

    public void setCodeLanguageSelected(int codeLanguageSelected) {
        this.codeLanguageSelected = codeLanguageSelected;
    }

    public OTPTab(IModel<String> title) {
        super(title);
        languageList = new ArrayList<LanguageType>();
        languageList.add(new LanguageType(Language.ITA, "ols.navigation.ita"));//1
        languageList.add(new LanguageType(Language.ENG, "ols.navigation.eng"));//2
    }

    @Override
    public String getCheckboxValue() {
        if(instancePanel != null)
            return instancePanel.getCheckboxOTP().getModelObject().toString();
        return this.getActiveOTP();
    }

    @Override
    public void setChecckboxValue(String value) {
        if(instancePanel != null){
            instancePanel.setActiveOTP(value);
            instancePanel.getCheckboxOTP().setModelObject(Boolean.parseBoolean(value));
        }else{
            setActiveOTP(value);
        }
    }

    @Override
    public Panel getPanel(String panelId) {
//        if(instancePanel == null)
            instancePanel = new OTPPanel(panelId);
        
        setupPanel();
        
        return instancePanel;
    }
    
    protected void setupPanel()
    {
        instancePanel.setActiveOTP(activeOTP);
        instancePanel.getCheckboxOTP().setModelObject(Boolean.parseBoolean(activeOTP));
        instancePanel.setUrlOTP(urlOTP);
        instancePanel.setNavigationInfoOTP(navigationInfoOTP);
        instancePanel.setNavigationInfoShortOTP(navigationInfoShortOTP);
        instancePanel.setNavigationInfoRelOTP(navigationInfoRelOTP);
        instancePanel.add(new LanguageDropDownChoice("language", new PropertyModel<LanguageType>(this, "selectedLanguage"), languageList));
        instancePanel.setSelectedLanguage(this.languageList.get(getCodeLanguageSelected()-1));
    }
    
    protected static class OTPPanel extends Panel{
        private String                  urlOTP;
        private String                  activeOTP;
        private String                  navigationInfoOTP;
        private String                  navigationInfoShortOTP;
        private String                  navigationInfoRelOTP;
        private CheckBox                checkboxOTP;
        private LanguageType            selectedLanguage;
        private List<LanguageType>      languageList = null;
        

        public OTPPanel(String id) {
            super(id);
            checkboxOTP = new CheckBox("checkboxOTP", Model.of(Boolean.FALSE)){
                
                @Override
                protected boolean wantOnSelectionChangedNotifications() {
                    return true;
                }
                    
                @Override
                public void onSelectionChanged() {
                    super.onSelectionChanged();
                    activeOnlyOneCheck(this.getModelObject());
                    setActiveOTP(this.getModelObject().toString());
                }    
                
                private void activeOnlyOneCheck(Boolean value){
                      OLS ols = OLS.get();
                      GeoServer gs = ols.getGeoServer();
                      OLSInfo olsInfo = gs.getService(OLSInfo.class);
                      List<OLSServiceProvider> serviceProvider = olsInfo.getServiceProvider();
                      
                      for (OLSServiceProvider provider : serviceProvider) {
                          if(provider.getServiceType() == OLSService.ROUTING_NAVIGATION
                                  && !isMyClass(provider)
                                  && value){
                              
                              OLSServiceProviderGUI providerGUI = (OLSServiceProviderGUI)provider;
                              Boolean notValue = !value;
                              String strValue = notValue.toString();
                              providerGUI.getProperties().setProperty("OLS.serviceProvider.service.active", strValue);
                              ((ValidateCheckboxTab)providerGUI.getTab()).setChecckboxValue(strValue);
                          }
                      }
                  }
            };
            add(checkboxOTP);
            add(new TextField("urlOTP",new PropertyModel(this,"urlOTP")));
            add(new TextField("navigationInfoOTP",new PropertyModel(this,"navigationInfoOTP")));
            add(new TextField("navigationInfoShortOTP",new PropertyModel(this,"navigationInfoShortOTP")));
            add(new TextField("navigationInfoRelOTP",new PropertyModel(this,"navigationInfoRelOTP")));
        }
        
        protected boolean isMyClass(OLSServiceProvider provider)
        {
            return provider != null && provider.getClass().equals(OTPServiceProvider.class);
        }

        public String getUrlOTP() {
            return urlOTP;
        }

        public void setUrlOTP(String urlOTP) {
            this.urlOTP = urlOTP;
        }

        public String getActiveOTP() {
            return activeOTP;
        }

        public void setActiveOTP(String activeOTP) {
            this.activeOTP = activeOTP;
        }

        public String getNavigationInfoOTP() {
            return navigationInfoOTP;
        }

        public void setNavigationInfoOTP(String navigationInfoOTP) {
            this.navigationInfoOTP = navigationInfoOTP;
        }

        public CheckBox getCheckboxOTP() {
            return checkboxOTP;
        }
        
        public String getNavigationInfoShortOTP() {
            return navigationInfoShortOTP;
        }

        public void setNavigationInfoShortOTP(String navigationInfoShortOTP) {
            this.navigationInfoShortOTP = navigationInfoShortOTP;
        }

        public void setCheckboxOTP(CheckBox checkboxOTP) {
            this.checkboxOTP = checkboxOTP;
        }

        public List<LanguageType> getLanguageList() {
            return languageList;
        }

        public void setLanguageList(List<LanguageType> languageList) {
            this.languageList = languageList;
        }

        public LanguageType getSelectedLanguage() {
            return selectedLanguage;
        }

        public void setSelectedLanguage(LanguageType selectedLanguage) {
            this.selectedLanguage = selectedLanguage;
        }

        public String getNavigationInfoRelOTP() {
            return navigationInfoRelOTP;
        }

        public void setNavigationInfoRelOTP(String navigationInfoRelOTP) {
            this.navigationInfoRelOTP = navigationInfoRelOTP;
        }
    }

}
