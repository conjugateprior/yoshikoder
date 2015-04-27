package edu.harvard.wcfia.yoshikoder.util;

import java.util.MissingResourceException;
import java.util.ResourceBundle;

public class ApplicationDetails {
    private static final String BUNDLE_NAME = 
        "localization/application"; 
    
    private static final ResourceBundle RESOURCE_BUNDLE = 
        ResourceBundle.getBundle(BUNDLE_NAME);
    
    private ApplicationDetails() {
    }
    
    public static String getString(String key) {
        try {
            return RESOURCE_BUNDLE.getString(key);
        } catch (MissingResourceException e) {
            return '!' + key + '!';
        }
    }
    
}
