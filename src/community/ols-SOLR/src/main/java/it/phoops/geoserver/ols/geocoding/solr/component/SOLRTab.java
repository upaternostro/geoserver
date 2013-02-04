package it.phoops.geoserver.ols.geocoding.solr.component;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;

public class SOLRTab extends AbstractTab{
	private String 			urlSOLR;
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
	
	public SOLR9Panel getInstancePanel() {
	    return instancePanel;
	}

	public void setInstancePanel(SOLR9Panel instancePanel) {
		this.instancePanel = instancePanel;
	}

	@Override
	public Panel getPanel(String panelId) {
	    instancePanel = new SOLR9Panel(panelId);
	    instancePanel.setUrlSOLR(urlSOLR);
	    return instancePanel;
	}

	public SOLRTab(IModel<String> title) {
	    super(title);
	}
	
	private static class SOLR9Panel extends Panel{
	    private String 	urlSOLR;
		
	    public SOLR9Panel(String id){
	        super(id);
	        add(new TextField("urlSOLR",new PropertyModel(this,"urlSOLR")));
    	    }

	    public String getUrlSOLR() {
	        return urlSOLR;
	    }

	    public void setUrlSOLR(String urlSOLR) {
	        this.urlSOLR = urlSOLR;
	    }
	}
}
