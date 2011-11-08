package org.geoserver.community.css.web;

import java.util.Arrays;
import java.util.List;
import org.apache.wicket.Component;
import org.apache.wicket.PageParameters;
import org.apache.wicket.ajax.AjaxRequestTarget;
import org.apache.wicket.ajax.markup.html.AjaxLink;
import org.apache.wicket.markup.html.basic.Label;
import org.apache.wicket.markup.html.panel.Fragment;
import org.apache.wicket.markup.html.panel.Panel;
import org.apache.wicket.model.Model;
import org.apache.wicket.model.IModel;
import org.geoserver.catalog.StyleInfo;
import org.geoserver.web.wicket.GeoServerDataProvider;
import static org.geoserver.web.wicket.GeoServerDataProvider.Property;
import org.geoserver.web.wicket.GeoServerTablePanel;

public class StyleChooser extends Panel {
    private GeoServerDataProvider<StyleInfo> styleProvider;
    private GeoServerTablePanel<StyleInfo> styleTable;

    public StyleChooser(String id, final CssDemoPage demo) {
        super(id);

        styleProvider =
            new GeoServerDataProvider<StyleInfo>() {
                Property<StyleInfo> name =
                    new AbstractProperty<StyleInfo>("Name") {
                        public Object getPropertyValue(StyleInfo x) {
                            return x.getName();
                        }
                    };
                protected List<StyleInfo> getItems() {
                    return demo.catalog().getStyles();
                }
                public List<Property<StyleInfo>> getProperties() {
                    return Arrays.asList(name);
                }
            };
        styleTable =
            new GeoServerTablePanel<StyleInfo>("style.table", styleProvider) {
                @Override
                public Component getComponentForProperty(
                    String id, IModel value, final Property<StyleInfo> property
                ) {
                    final StyleInfo style = (StyleInfo) value.getObject();
                    Fragment fragment =
                        new Fragment(id, "style.link", StyleChooser.this);
                    AjaxLink link =
                        new AjaxLink("link") {
                            { 
                                add(new Label(
                                    "style.name",
                                    new Model(property.getPropertyValue(style).toString())
                                ));
                            }

                            public void onClick(AjaxRequestTarget target) {
                                PageParameters params = new PageParameters();
                                params.put(
                                    "layer",
                                    demo.layerInfo().getPrefixedName()
                                );
                                params.put("style", style.getName());
                                setResponsePage(CssDemoPage.class, params);
                            }
                        };
                    fragment.add(link);
                    return fragment;
                }
            };
        add(styleTable);
    }
}
