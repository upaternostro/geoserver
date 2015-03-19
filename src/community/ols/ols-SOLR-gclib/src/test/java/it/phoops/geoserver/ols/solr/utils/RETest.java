package it.phoops.geoserver.ols.solr.utils;

import java.util.regex.Pattern;

import org.junit.Test;

public class RETest {

    @Test
    public void test() {
        String input = "base+plus-minus&amp|pipe&&and||or!bang(bracket)close{curly}cclose[square]sclose^caret\"dquote~tilde*star?question:colon\\bslash/slash";
        String specialCharacters = "(\\+|-|&&|\\|\\||!|\\(|\\)|\\{|\\}|\\[|\\]|\\^|\"|~|\\*|\\?|:|\\\\|\\/)";
        
        System.out.println(input.replaceAll(specialCharacters, "\\\\$1"));
        
        Pattern p = Pattern.compile(specialCharacters);
        
        System.out.println(p.matcher(input).replaceAll("\\\\$1"));
        
    }

}
