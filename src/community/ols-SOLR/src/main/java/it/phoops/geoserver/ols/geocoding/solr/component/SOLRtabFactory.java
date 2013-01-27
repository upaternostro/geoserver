package it.phoops.geoserver.ols.geocoding.solr.component;

import org.apache.wicket.model.IModel;

public class SOLRtabFactory {
	private static SOLRtabFactory	factory = null;
	private SOLRTab				instance = null;
	
	public static SOLRtabFactory getSOLRtabFactory() {
		if (factory == null) {
			factory = new SOLRtabFactory();
		}
		
		return factory;
	}

	public SOLRTab getSOLRTab(IModel<String> title) {
		if (instance == null) {
			instance = new SOLRTab(title);
		}
		
		return instance;
	}
}
