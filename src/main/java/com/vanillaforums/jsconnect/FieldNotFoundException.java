package com.vanillaforums.jsconnect;

public class FieldNotFoundException extends JsConnectException {
    public FieldNotFoundException(String field, String collection) {
        super("Missing field: " + collection + "[" + field + "]");
    }
}
