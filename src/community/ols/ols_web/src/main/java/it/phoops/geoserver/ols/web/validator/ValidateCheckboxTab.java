/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.web.validator;

import org.apache.wicket.extensions.markup.html.tabs.ITab;

public interface ValidateCheckboxTab extends ITab{
    public String getCheckboxValue();
    public void setChecckboxValue(String value);
}
