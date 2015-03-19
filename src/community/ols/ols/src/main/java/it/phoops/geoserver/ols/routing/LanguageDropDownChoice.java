/* (c) 2015 Open Source Geospatial Foundation - all rights reserved
 * This code is licensed under the GPL 2.0 license, available at the root
 * application directory.
 */
package it.phoops.geoserver.ols.routing;

import java.util.List;

import org.apache.wicket.markup.html.form.DropDownChoice;
import org.apache.wicket.model.PropertyModel;

public class LanguageDropDownChoice extends DropDownChoice<LanguageType>{
        private LanguageType selectedLanguage;
        
        public LanguageDropDownChoice(String id, PropertyModel<LanguageType> model, List<LanguageType> displayData) {
                        super(id,model,displayData);
        }
        @Override
        protected void onSelectionChanged(LanguageType newSelection) {
                super.onSelectionChanged(newSelection);
                setSelectedLanguage(newSelection);
                System.out.println("Language Selected: "+newSelection.toString());
        }
        
        @Override
        protected boolean wantOnSelectionChangedNotifications() {
                return true;
        }
        public LanguageType getSelectedLanguage() {
                return selectedLanguage;
        }
        public void setSelectedLanguage(LanguageType selectedLanguage) {
                this.selectedLanguage = selectedLanguage;
        }
}
