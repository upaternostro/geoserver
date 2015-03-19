/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.reverse.solr.component;

import org.apache.wicket.model.IModel;

public class SOLRTabReverseFactory {
	private static SOLRTabReverseFactory factory = null;
	private SOLRTabReverse instance = null;
	
	public static SOLRTabReverseFactory getSOLRTabReverseFactory() {
		if (factory == null) {
			factory = new SOLRTabReverseFactory();
		}
		
		return factory;
	}
	
	public SOLRTabReverse getSOLRTabReverse(IModel<String> title) {
		if (instance == null) {
			instance = new SOLRTabReverse(title);
		}
		
		return instance;
	}
	
}
