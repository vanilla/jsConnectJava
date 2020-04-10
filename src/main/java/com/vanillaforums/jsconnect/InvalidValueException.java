package com.vanillaforums.jsconnect;

/**
 * An exception that represents a value that is not valid for its intended use.
 */
public class InvalidValueException extends JsConnectException {
    /**
     * InvalidValueException constructor.
     *
     * @param message The exception message.
     */
    public InvalidValueException(String message) {
        super(message);
    }
}
