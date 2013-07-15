package it.phoops.geoserver.ols.routing;

import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;

public enum Language {
    ITA("1"),
    ENG("2");
    
    private static final Map<String,Language> _lookup = new HashMap<String,Language>();
    private String code;
    
    Language(String code) {
        this.code = code;
    }
    
    public String getCode() {
        return code;
    }
    
    @Override
    public String toString() {
        return code;
    }
    
    static {
        for (Language pivot : EnumSet.allOf(Language.class)) {
            _lookup.put(pivot.getCode(), pivot);
        }
    }
    
    public static Language get(String code) {
        return _lookup.get(code);
    }
}
