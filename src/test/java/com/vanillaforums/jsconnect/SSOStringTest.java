package com.vanillaforums.jsconnect;

import org.junit.jupiter.api.Test;

import java.security.InvalidKeyException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SSOStringTest {
    public static final String SECRET = "secret";
    public static final String CLIENT_ID = "clientID";

    @Test
    void testBasicSSOString() throws InvalidKeyException {
        Map<String, String> user = new HashMap();
        user.put("name", "foo");
        user.put("id", "abc");

        jsConnect.Now = 1572315344;

        String actual = jsConnect.SSOString(user, CLIENT_ID, SECRET);
        // This string was snagged from the PHP library.
        String expected = "eyJuYW1lIjoiZm9vIiwiaWQiOiJhYmMiLCJjbGllbnRfaWQiOiJjbGllbnRJRCJ9 104e85028bdb47ee9d1e12cfb27c77b1ba40c63e 1572315344 hmacsha1";
        assertEquals(expected, actual);
    }

    @Test
    void testBasicSSOStringEmptyClientID() throws InvalidKeyException {
        Map<String, String> user = new HashMap();
        user.put("name", "foo");
        user.put("id", "abc");
        user.put("client_id", "");

        jsConnect.Now = 1572315344;

        String actual = jsConnect.SSOString(user, CLIENT_ID, SECRET);
        // This string was snagged from the PHP library.
        String expected = "eyJuYW1lIjoiZm9vIiwiaWQiOiJhYmMiLCJjbGllbnRfaWQiOiJjbGllbnRJRCJ9 104e85028bdb47ee9d1e12cfb27c77b1ba40c63e 1572315344 hmacsha1";
        assertEquals(expected, actual);
    }
}
