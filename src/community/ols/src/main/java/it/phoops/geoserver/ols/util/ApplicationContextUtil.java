/* Copyright (c) 2001 - 2013 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.util;

import it.phoops.geoserver.ols.OLS;

import org.springframework.context.ApplicationContext;

public class ApplicationContextUtil{
	private static ApplicationContextUtil instance = null;
	private ApplicationContext appContext = null;
	
	private ApplicationContextUtil(){
		this.appContext = OLS.getApplicationContext();
	}
	
	public static ApplicationContextUtil getIstance(){
		if(instance == null){
			instance = new ApplicationContextUtil();
		}
		return instance;
	}

	public ApplicationContext getAppContext() {
		return appContext;
	}

	public void setAppContext(ApplicationContext appContext) {
		this.appContext = appContext;
	}
}
