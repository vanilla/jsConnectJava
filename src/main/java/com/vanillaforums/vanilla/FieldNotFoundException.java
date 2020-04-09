package com.vanillaforums.vanilla;

public class FieldNotFoundException extends JsConnectException {
    public FieldNotFoundException(String field, String collection) {
        super("Missing field: " + collection + "[" + field + "]");
    }
}
