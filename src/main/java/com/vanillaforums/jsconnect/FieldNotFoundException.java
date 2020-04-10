package com.vanillaforums.jsconnect;

/**
 * An exception that represents a missing required field.
 */
public class FieldNotFoundException extends JsConnectException {
    /**
     * FieldNotFound constructor.
     * 
     * @param field The name of the field.
     * @param collection The name of the collection.
     */
    public FieldNotFoundException(String field, String collection) {
        super("Missing field: " + collection + "[" + field + "]");
    }
}
