package it.phoops.geoserver.ols.geocoding.rfc59.component;


import it.phoops.geoserver.ols.geocoding.rfc59.Algorithm;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.Localizer;
import org.apache.wicket.extensions.markup.html.tabs.AbstractTab;
import org.apache.wicket.markup.html.form.TextField;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.IModel;
import org.apache.wicket.model.PropertyModel;
import org.geoserver.config.ConfigurationListener;
import org.geoserver.config.GeoServerInfo;
import org.geoserver.config.LoggingInfo;
import org.geoserver.config.ServiceInfo;
import org.geoserver.config.SettingsInfo;

public class RFC59Tab extends AbstractTab{

	private String 								urlRFC59;
	private String 								timeoutRFC59;
	private RFC59Panel 							instancePanel;
	private List<OLSAlgorithmType>				algorithmList = null;
	private OLSAlgorithmType 					selectedAlgorithm;
	private RFC59Tab							instanceTabRFC59 = null;
	
	public class OLSAlgorithmType implements Serializable {
		private Algorithm 			algorithm;
		private String				code;
		private String 				descriptionKey;
		private Component 			component;
		
		public OLSAlgorithmType() {
		}
		
		public OLSAlgorithmType(Algorithm algorithm, String descriptionKey) {
			super();
			this.algorithm = algorithm;
			this.code = algorithm.toString();
			this.descriptionKey = descriptionKey;
//			this.component = component;
		}
		
		public Algorithm getService() {
			return algorithm;
		}

		public void setService(Algorithm algorithm) {
			this.algorithm = algorithm;
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
	
	public RFC59Tab(IModel<String> title) {
		super(title);
		algorithmList = new ArrayList<OLSAlgorithmType>();
		algorithmList.add(new OLSAlgorithmType(Algorithm.FUZZY_QUERIES, "OLSAlgorithmType.fuzzy"));
		algorithmList.add(new OLSAlgorithmType(Algorithm.RADIX, "OLSAlgorithmType.radix"));
		algorithmList.add(new OLSAlgorithmType(Algorithm.TERM_QUERIES, "OLSAlgorithmType.term"));
	}
	
	@Override
	public Panel getPanel(String panelId) {
		instancePanel = new RFC59Panel(panelId);
		instancePanel.setUrlRFC59(urlRFC59);
		instancePanel.setTimeoutRFC59(timeoutRFC59);
		instancePanel.setAlgorithmList(this.algorithmList);
		instancePanel.add(new AlgorithmDropDownChoise("algorithm", new PropertyModel<OLSAlgorithmType>(this, "selectedAlgorithm"), algorithmList));
		this.setSelectedAlgorithm(selectedAlgorithm);
		
		return instancePanel;
	}
	
	private static class RFC59Panel extends Panel{
    	private String 							urlRFC59;
    	private String 							timeoutRFC59;
    	private OLSAlgorithmType				selectedAlgorithm;
    	private List<OLSAlgorithmType>			algorithmList = null;
    	
    	
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

		public List<OLSAlgorithmType> getAlgorithmList() {
			return algorithmList;
		}

		public void setAlgorithmList(List<OLSAlgorithmType> algorithmList) {
			this.algorithmList = algorithmList;
		}

		public OLSAlgorithmType getSelectedAlgorithm() {
			return selectedAlgorithm;
		}

		public void setSelectedAlgorithm(OLSAlgorithmType selectedAlgorithm) {
			this.selectedAlgorithm = selectedAlgorithm;
		}
    }

	public String getUrlRFC59() {
		if(instancePanel != null)
			return instancePanel.getUrlRFC59();
		return urlRFC59;
	}

	public void setUrlRFC59(String urlRFC59) {
		if(instancePanel != null){
			instancePanel.setUrlRFC59(urlRFC59);
		}
		this.urlRFC59 = urlRFC59;
	}

	public String getTimeoutRFC59() {
		if(instancePanel != null)
			return instancePanel.getTimeoutRFC59();
		return timeoutRFC59;
	}

	public void setTimeoutRFC59(String timeoutRFC59) {
		if(instancePanel != null){
			instancePanel.setTimeoutRFC59(timeoutRFC59);
		}
		this.timeoutRFC59 = timeoutRFC59;
	}

	public OLSAlgorithmType getSelectedAlgorithm() {
		if(instancePanel != null){
			return instancePanel.getSelectedAlgorithm();
		}
		return selectedAlgorithm;
	}

	public void setSelectedAlgorithm(OLSAlgorithmType selectedAlgorithm) {
		if(instancePanel != null){
			instancePanel.setSelectedAlgorithm(selectedAlgorithm);
		}
		this.selectedAlgorithm = selectedAlgorithm;
	}
}
