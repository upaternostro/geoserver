/* Copyright (c) 2001 - 2013 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.reverse.solr.component;

import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.form.CheckBox;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.PropertyModel;

public class SOLRTabReverse extends AbstractTab{
	private String 			urlSOLRReverse;
	private String          	activeSOLRReverse;
	private String                 crsNameReverse;
        private String                 radiusReverse;
	private SOLRReversePanel 	instancePanel;
	
	public String getUrlSOLRReverse() {
		if(instancePanel != null)
				return instancePanel.getUrlSOLRReverse();
		return urlSOLRReverse;
	}

	public void setUrlSOLRReverse(String urlSOLRReverse) {
		if(instancePanel != null)
			instancePanel.setUrlSOLRReverse(urlSOLRReverse);
		this.urlSOLRReverse = urlSOLRReverse;
	}

	public String getActiveSOLRReverse() {
		if(instancePanel != null)
			return instancePanel.getActiveSOLRReverse();
		return activeSOLRReverse;
	}

	public void setActiveSOLRReverse(String activeSOLRReverse) {
		if(instancePanel != null)
			instancePanel.setActiveSOLRReverse(activeSOLRReverse);
		this.activeSOLRReverse = activeSOLRReverse;
	}

	public String getCrsNameReverse() {
            if(instancePanel != null)
                return instancePanel.getCrsNameReverse();
            return crsNameReverse;
        }
    
        public void setCrsNameReverse(String crsNameReverse) {
            if(instancePanel != null)
                instancePanel.setCrsNameReverse(crsNameReverse);
            this.crsNameReverse = crsNameReverse;
        }
    
        public String getRadiusReverse() {
            if(instancePanel != null)
                return instancePanel.getRadiusReverse();
            return radiusReverse;
        }
    
        public void setRadiusReverse(String radiusReverse) {
            if(instancePanel != null)
                instancePanel.setRadiusReverse(crsNameReverse);
            this.radiusReverse = radiusReverse;
        }
    
        public SOLRReversePanel getInstancePanel() {
		return instancePanel;
	}

	public void setInstancePanel(SOLRReversePanel instancePanel) {
		this.instancePanel = instancePanel;
	}

	public SOLRTabReverse(IModel<String> title) {
		super(title);
	}

	@Override
	public Panel getPanel(String panelId) {
		if(instancePanel == null)
	        instancePanel = new SOLRReversePanel(panelId);
	    instancePanel.setActiveSOLRReverse(activeSOLRReverse);
	    instancePanel.getCheckboxSOLRReverse().setModelObject(Boolean.parseBoolean(activeSOLRReverse));
	    instancePanel.setUrlSOLRReverse(urlSOLRReverse);
            instancePanel.setCrsNameReverse(crsNameReverse);
	    return instancePanel;
	}
	
	private static class SOLRReversePanel extends Panel{
	    private String             urlSOLRReverse;
	    private String     		activeSOLRReverse;
            private String              crsNameReverse;
            private String              radiusReverse;
	    private CheckBox   		checkboxSOLRReverse;
	
	    
	    public SOLRReversePanel(String id){
	        super(id);
	        checkboxSOLRReverse = new CheckBox("checkboxSOLRReverse", Model.of(Boolean.FALSE)){
                    
                    @Override
                    protected boolean wantOnSelectionChangedNotifications() {
                        return true;
                    }
                        
                    @Override
                    public void onSelectionChanged() {
                        super.onSelectionChanged();
                        setActiveSOLRReverse(this.getModelObject().toString());
                    }    
                };
                add(checkboxSOLRReverse);
	        add(new TextField("urlSOLRReverse",new PropertyModel(this,"urlSOLRReverse")));
                add(new TextField("crsNameReverse",new PropertyModel(this,"crsNameReverse")));
                add(new TextField("radiusReverse",new PropertyModel(this,"radiusReverse")));
    	    }
	    
		    public String getUrlSOLRReverse() {
		        return urlSOLRReverse;
		    }
	
		    public void setUrlSOLRReverse(String urlSOLRReverse) {
		        this.urlSOLRReverse = urlSOLRReverse;
		    }

            public String getActiveSOLRReverse() {
                return activeSOLRReverse;
            }
    
            public void setActiveSOLRReverse(String activeSOLRReverse) {
                this.activeSOLRReverse = activeSOLRReverse;
            }
    
            public CheckBox getCheckboxSOLRReverse() {
                return checkboxSOLRReverse;
            }
    
            public void setCheckboxSOLRReverse(CheckBox checkboxSOLRReverse) {
                this.checkboxSOLRReverse = checkboxSOLRReverse;
            }

            public String getCrsNameReverse() {
                return crsNameReverse;
            }

            public void setCrsNameReverse(String crsNameReverse) {
                this.crsNameReverse = crsNameReverse;
            }

            public String getRadiusReverse() {
                return radiusReverse;
            }

            public void setRadiusReverse(String radiusReverse) {
                this.radiusReverse = radiusReverse;
            }
	}
	

}
