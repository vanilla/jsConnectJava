package com.vanillaforums.vanilla;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class WriteJsConnectTest {

    public static final String CLIENT_ID = "clientID";
    public static final String SECRET = "secret";

    static void assertJsConnect(Map user, Map request, JSONObject expected, Boolean setTimestamp) throws JSONException {
        if (setTimestamp && request.containsKey("timestamp")) {
            String timestamp = request.get("timestamp").toString();
            jsConnect.Now = Long.parseLong(timestamp);
        }

        String actual = jsConnect.GetJsConnectString(user, request, CLIENT_ID, SECRET, "sha256");
        JSONAssert.assertEquals("jsConnect strings don't match.", actual, expected, JSONCompareMode.LENIENT);
    }

    static void assertJsConnect(Map user, Map request, JSONObject expected) throws JSONException {
        assertJsConnect(user, request, expected, true);
    }

    private static JSONObject error(String error, String message) throws JSONException {
        JSONObject expected = new JSONObject();
        expected.put("error", error);
        expected.put("message", message);

        return expected;
    }

    @BeforeEach
    void setUp() {
        jsConnect.Now = 0;
    }

    @Test
    void testDefault() throws JSONException {
        Map<String, String> user = getDefaultUser();
        Map<String, String> request = getDefaultRequest();

        JSONObject js = new JSONObject();
        js.put("client_id", "clientID");
        js.put("email", "john.php@example.com");
        js.put("ip", "127.0.0.1");
        js.put("name", "John PHP");
        js.put("nonce", "nonceToken");
        js.put("sig", "40c511cac2db1ca7443d4f539f297a9510e8e011a04f66bdd91dc62f967e17ca");
        js.put("unique_id", "123");
        js.put("v", "2");

        assertJsConnect(user, request, js);
    }

    @Test
    void testDefaultBC() throws JSONException {
        Map<String, String> user = getDefaultUser();
        Map<String, String> request = getDefaultRequest();
        request.put("sig", "94d2d624946149e2770960bbe16a9ed9");

        JSONObject js = new JSONObject();
        js.put("client_id", "clientID");
        js.put("email", "john.php@example.com");
        js.put("ip", "127.0.0.1");
        js.put("name", "John PHP");
        js.put("nonce", "nonceToken");
        js.put("sig", "ad973c14c8efe2164d8fd67249430499");
        js.put("unique_id", "123");
        js.put("v", "2");

        String timestamp = request.get("timestamp");
        jsConnect.Now = Long.parseLong(timestamp);

        String actual = jsConnect.GetJsConnectString(user, request, CLIENT_ID, SECRET, true);
        JSONAssert.assertEquals("jsConnect strings don't match.", actual, js, JSONCompareMode.LENIENT);
    }

    private Map<String, String> getDefaultRequest() {
        Map<String, String> request = new HashMap<>();
        request.put("client_id", "clientID");
        request.put("ip", "127.0.0.1");
        request.put("nonce", "nonceToken");
        request.put("sig", "9d530946e38b35b780c0bdd55025ae8ea979ca962f6ae6c65636b819a9f0bd27");
        request.put("timestamp", "1572315344");
        request.put("v", "2");
        return request;
    }

    private Map<String, String> getDefaultUser() {
        Map<String, String> user = new HashMap<>();
        user.put("name", "John PHP");
        user.put("email", "john.php@example.com");
        user.put("unique_id", "123");
        return user;
    }

    @Test
    void testMissingVersion() throws JSONException {
        Map request = new HashMap<>();
        request.put("client_id", "clientID");
        request.put("ip", "127.0.0.1");
        request.put("nonce", "nonceToken");
        request.put("sig", "9d530946e38b35b780c0bdd55025ae8ea979ca962f6ae6c65636b819a9f0bd27");
        request.put("timestamp", "1572315344");

        JSONObject expected = error("invalid_request", "Missing the v parameter.");

        assertJsConnect(new HashMap(), request, expected);
    }

    @Test
    void testWrongVersion() throws JSONException {
        Map request = new HashMap<>();
        request.put("client_id", "clientID");
        request.put("ip", "127.0.0.1");
        request.put("nonce", "nonceToken");
        request.put("sig", "9d530946e38b35b780c0bdd55025ae8ea979ca962f6ae6c65636b819a9f0bd27");
        request.put("timestamp", "1572315344");
        request.put("v", "1");

        JSONObject expected = error("invalid_request", "Unsupported version 1.");

        assertJsConnect(new HashMap(), request, expected);
    }

    @Test
    void testMissingClientID() throws JSONException {
        Map<String, String> request = new HashMap<>();
        request.put("ip", "127.0.0.1");
        request.put("nonce", "nonceToken");
        request.put("sig", "9d530946e38b35b780c0bdd55025ae8ea979ca962f6ae6c65636b819a9f0bd27");
        request.put("timestamp", "1572315344");
        request.put("v", "2");

        JSONObject expected = error("invalid_request", "Missing the client_id parameter.");

        assertJsConnect(new HashMap(), request, expected);
    }

    @Test
    void testMissingSig() throws JSONException {
        Map<String, String> request = new HashMap<>();
        request.put("client_id", "clientID");
        request.put("ip", "127.0.0.1");
        request.put("nonce", "nonceToken");
        request.put("timestamp", "1572315344");
        request.put("v", "2");

        JSONObject expected = error("invalid_request", "Missing the sig parameter.");

        assertJsConnect(new HashMap(), request, expected);
    }

    @Test
    void testMissingNonce() throws JSONException {
        Map<String, String> request = new HashMap<>();
        request.put("client_id", "clientID");
        request.put("ip", "127.0.0.1");
        request.put("sig", "9d530946e38b35b780c0bdd55025ae8ea979ca962f6ae6c65636b819a9f0bd27");
        request.put("timestamp", "1572315344");
        request.put("v", "2");

        JSONObject expected = error("invalid_request", "Missing the nonce parameter.");

        assertJsConnect(new HashMap(), request, expected);
    }

    @Test
    void testMissingIP() throws JSONException {
        Map<String, String> request = new HashMap<>();
        request.put("client_id", "clientID");
        request.put("nonce", "nonceToken");
        request.put("sig", "9d530946e38b35b780c0bdd55025ae8ea979ca962f6ae6c65636b819a9f0bd27");
        request.put("timestamp", "1572315344");
        request.put("v", "2");

        JSONObject expected = error("invalid_request", "Missing the ip parameter.");

        assertJsConnect(new HashMap(), request, expected);
    }

    @Test
    void testMissingTimestamp() throws JSONException {
        Map<String, String> request = new HashMap<>();
        request.put("client_id", "clientID");
        request.put("ip", "127.0.0.1");
        request.put("nonce", "nonceToken");
        request.put("sig", "9d530946e38b35b780c0bdd55025ae8ea979ca962f6ae6c65636b819a9f0bd27");
        request.put("v", "2");

        JSONObject expected = error("invalid_request", "The timestamp parameter is missing or invalid.");

        assertJsConnect(new HashMap(), request, expected);
    }

    @Test
    void testInvalidTimestamp() throws JSONException {
        Map<String, String> request = new HashMap<>();
        request.put("client_id", "clientID");
        request.put("ip", "127.0.0.1");
        request.put("nonce", "nonceToken");
        request.put("sig", "9d530946e38b35b780c0bdd55025ae8ea979ca962f6ae6c65636b819a9f0bd27");
        request.put("timestamp", "invalid");
        request.put("v", "2");

        JSONObject expected = error("invalid_request", "The timestamp parameter is missing or invalid.");

        assertJsConnect(new HashMap(), request, expected, false);
    }

    @Test
    void testTimedOut() throws JSONException {
        Map<String, String> request = getDefaultRequest();

        JSONObject expected = error("invalid_request", "The timestamp is invalid.");

        assertJsConnect(new HashMap(), request, expected, false);
    }

    @Test
    void testWrongClientID() throws JSONException {
        Map<String, String> request = new HashMap<>();
        request.put("client_id", "wrongClientID");
        request.put("ip", "127.0.0.1");
        request.put("nonce", "nonceToken");
        request.put("sig", "9d530946e38b35b780c0bdd55025ae8ea979ca962f6ae6c65636b819a9f0bd27");
        request.put("timestamp", "1572315344");
        request.put("v", "2");

        JSONObject expected = error("invalid_client", "Unknown client wrongClientID.");

        assertJsConnect(new HashMap(), request, expected);
    }

    @Test
    void testBadSignature() throws JSONException {
        Map<String, String> request = new HashMap<>();
        request.put("client_id", "clientID");
        request.put("ip", "127.0.0.1");
        request.put("nonce", "nonceToken");
        request.put("sig", "9d530946e38b35b780c0bdd55025ae8ea979ca962f6ae6c65636b819a9f0bxxx");
        request.put("timestamp", "1572315344");
        request.put("v", "2");

        JSONObject expected = error("access_denied", "Signature invalid.");

        assertJsConnect(new HashMap(), request, expected);
    }

    @Test
    void testNoUser() throws JSONException {
        Map<String, String> request = new HashMap<>();
        request.put("client_id", "clientID");
        request.put("ip", "127.0.0.1");
        request.put("nonce", "nonceToken");
        request.put("v", "2");

        JSONObject js = new JSONObject();
        js.put("name", "");
        js.put("photourl", "");

        assertJsConnect(new HashMap(), request, js);
    }

    @Test
    void testInvalidCallback() {
        Map<String, String> request = getDefaultRequest();
        request.put("callback", "<script>alert(document.domain);</script>");

        String actual = jsConnect.GetJsConnectString(new HashMap(), request, CLIENT_ID, SECRET, "sha256");
        assertEquals("console.error('Invalid callback parameter in jsConnect.')", actual);
    }
}