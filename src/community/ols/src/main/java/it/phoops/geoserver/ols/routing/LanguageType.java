/* Copyright (c) 2001 - 2013 OpenPlans - www.openplans.org. All rights reserved.
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.routing;

import java.io.Serializable;

import org.apache.wicket.Application;
import org.apache.wicket.Component;
import org.apache.wicket.Localizer;

public class LanguageType implements Serializable {
    private Language                    language;
    private String                      code;
    private String                      descriptionKey;
    private Component                   component;
    
        
    public LanguageType() {}
        
    public LanguageType(Language language, String descriptionKey) {
        super();
        this.language = language;
        this.code = language.toString();
        this.descriptionKey = descriptionKey;
    }
        
    public Language getService() {
        return language;
    }

    public void setService(Language language) {
        this.language = language;
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
        Localizer localizer = Application.get().getResourceSettings().getLocalizer();
        return localizer.getString(descriptionKey, component);
    }
}
