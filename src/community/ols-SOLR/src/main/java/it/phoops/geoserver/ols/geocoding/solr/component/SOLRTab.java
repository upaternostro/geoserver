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
		return urlSOLR;
	}

	public void setUrlSOLR(String urlSOLR) {
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
		return instancePanel;
	}

	public SOLRTab(IModel<String> title) {
		super(title);
//		algorithmList = new ArrayList<OLSAlgorithmType>();
//		algorithmList.add(new OLSAlgorithmType(Algorithm.FUZZY_QUERIES, "OLSAlgorithmType.fuzzy"));
//		algorithmList.add(new OLSAlgorithmType(Algorithm.RADIX, "OLSAlgorithmType.radix"));
//		algorithmList.add(new OLSAlgorithmType(Algorithm.TERM_QUERIES, "OLSAlgorithmType.term"));
	}
	
	private static class SOLR9Panel extends Panel{
		private String 							urlSOLR;
		
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
