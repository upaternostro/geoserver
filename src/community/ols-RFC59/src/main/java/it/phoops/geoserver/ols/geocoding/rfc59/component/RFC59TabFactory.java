/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.geocoding.rfc59.component;

import org.apache.wicket.model.IModel;

public class RFC59TabFactory {
	private static RFC59TabFactory	factory = null;
	private RFC59Tab				instance = null;
	
	public static RFC59TabFactory getRFC59TabFactory() {
		if (factory == null) {
			factory = new RFC59TabFactory();
		}
		
		return factory;
	}

	public RFC59Tab getRFC59Tab(IModel<String> title) {
		if (instance == null) {
			instance = new RFC59Tab(title);
		}
		
		return instance;
	}
}
