package it.phoops.geoserver.ols.geocoding.solr.component;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.WebMarkupContainer;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.RadioChoice;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.link.Link;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

public class SOLRTab extends AbstractTab{
	private String 			urlSOLR;
	private String          activeSOLR;
	private SOLR9Panel 		instancePanel;
	
	public String getUrlSOLR() {
	    if(instancePanel != null)
	        return instancePanel.getUrlSOLR();
	    return urlSOLR;
	}

	public void setUrlSOLR(String urlSOLR) {
	    if(instancePanel != null)
	        instancePanel.setUrlSOLR(urlSOLR);
	    this.urlSOLR = urlSOLR;
	}
	
	public String getActiveSOLR() {
	    if(instancePanel != null)
	        return instancePanel.getActiveSOLR();
            return activeSOLR;
        }

        public void setActiveSOLR(String activeSOLR) {
            if(instancePanel != null)
                instancePanel.setActiveSOLR(activeSOLR);
            this.activeSOLR = activeSOLR;
        }

        public SOLR9Panel getInstancePanel() {
	    return instancePanel;
	}

	public void setInstancePanel(SOLR9Panel instancePanel) {
		this.instancePanel = instancePanel;
	}

	@Override
	public Panel getPanel(String panelId) {
	    if(instancePanel == null)
	        instancePanel = new SOLR9Panel(panelId);
	    instancePanel.setActiveSOLR(activeSOLR);
	    instancePanel.getCheckboxSOLR().setModelObject(Boolean.parseBoolean(activeSOLR));
	    instancePanel.setUrlSOLR(urlSOLR);
	    return instancePanel;
	}

	public SOLRTab(IModel<String> title) {
	    super(title);
	}
	
	private static class SOLR9Panel extends Panel{
	    private String 	urlSOLR;
	    private String     activeSOLR;
	    private CheckBox   checkboxSOLR;
	
	    
	    public SOLR9Panel(String id){
	        super(id);
	        checkboxSOLR = new CheckBox("checkboxSOLR", Model.of(Boolean.FALSE)){
                    
                    @Override
                    protected boolean wantOnSelectionChangedNotifications() {
                        return true;
                    }
                        
                    @Override
                    public void onSelectionChanged() {
                        super.onSelectionChanged();
                        setActiveSOLR(this.getModelObject().toString());
                    }    
                };
                add(checkboxSOLR);
	        add(new TextField("urlSOLR",new PropertyModel(this,"urlSOLR")));
    	    }
	    
	    public String getUrlSOLR() {
	        return urlSOLR;
	    }

	    public void setUrlSOLR(String urlSOLR) {
	        this.urlSOLR = urlSOLR;
	    }

            public String getActiveSOLR() {
                return activeSOLR;
            }
    
            public void setActiveSOLR(String activeSOLR) {
                this.activeSOLR = activeSOLR;
            }
    
            public CheckBox getCheckboxSOLR() {
                return checkboxSOLR;
            }
    
            public void setCheckboxSOLR(CheckBox checkboxSOLR) {
                this.checkboxSOLR = checkboxSOLR;
            }
	}
}
