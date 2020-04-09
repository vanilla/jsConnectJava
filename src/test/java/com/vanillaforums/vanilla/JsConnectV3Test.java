package com.vanillaforums.vanilla;

import com.auth0.jwt.JWT;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.apache.commons.codec.binary.StringUtils;
import org.json.JSONException;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.io.FileReader;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.*;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Tests for the `JsConnectV3` class.
 */
public class JsConnectV3Test {
    /**
     * Assert that two response JWT URLs are equal without caring about key order.
     *
     * @param expected The expected URL.
     * @param actual The actual URL
     * @throws UnsupportedEncodingException
     * @throws JSONException
     * @throws URISyntaxException
     */
    public static void assertJWTUrlsEqual(String expected, String actual) throws UnsupportedEncodingException, JSONException, URISyntaxException {
        URI expectedUrl = new URI(expected);
        URI actualUrl = new URI(actual);

        Map<String, String> expectedQuery = splitQuery(expectedUrl.getFragment());
        Map<String, String> actualQuery = splitQuery(actualUrl.getFragment());

        assertJWTEquals(expectedQuery.get("jwt"), actualQuery.get("jwt"));
    }

    /**
     * Split a query strig into a map of its parts.
     *
     * @param query The query to split.
     * @return Returns the split query
     * @throws UnsupportedEncodingException
     */
    protected static Map<String, String> splitQuery(String query) throws UnsupportedEncodingException {
        final Map<String, String> queryPairs = new LinkedHashMap<>();
        final String[] pairs = query.split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
            final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
            queryPairs.put(key, value);
        }
        return queryPairs;
    }

    /**
     * Assert that two JWTs are equal without verifying the tokens themselves.
     *
     * @param expected The expected token.
     * @param actual The actual token.
     * @throws JSONException
     */
    public static void assertJWTEquals(String expected, String actual) throws JSONException {
        DecodedJWT expectedJWT = JWT.decode(expected);
        DecodedJWT actualJWT = JWT.decode(actual);

        JSONAssert.assertEquals(tokenJSON(expectedJWT.getHeader()), tokenJSON(actualJWT.getHeader()), false);
        JSONAssert.assertEquals(tokenJSON(expectedJWT.getPayload()), tokenJSON(actualJWT.getPayload()), false);
    }

    /**
     * Get the JSON represented by part of a JWT token, either the header or the payload.
     * @param base64 The token part to decode.
     * @return Returns a JSON encoded string.
     */
    protected static String tokenJSON(String base64) {
        String headerJson = StringUtils.newStringUtf8(org.apache.commons.codec.binary.Base64.decodeBase64(base64));
        return headerJson;
    }

    /**
     * Provide tests from the tests.json string that were built with the jsConnectPHP library.
     *
     * @return
     * @throws IOException
     * @throws ParseException
     */
    public static List<Arguments> provideTests() throws IOException, ParseException {
        FileReader testFile = new FileReader("src/test/data/tests.json");
        JSONParser jsonParser = new JSONParser();
        JSONObject obj = (JSONObject) jsonParser.parse(testFile);
        List<Arguments> result = new LinkedList<>();

        for (Object o : obj.keySet()) {
            String key = (String) o;
            JSONObject item = (JSONObject) obj.get(key);
            result.add(Arguments.of(key, item));
        }

        return result;
    }

    /**
     * Test a single test from tests.json.
     *
     * @param name
     * @param data
     * @throws InvalidValueException
     * @throws UnsupportedEncodingException
     * @throws JSONException
     * @throws URISyntaxException
     */
    @ParameterizedTest
    @MethodSource("provideTests")
    public void testData(String name, JSONObject data) throws InvalidValueException, UnsupportedEncodingException, JSONException, URISyntaxException {
        JsConnectV3 jsc = new JsConnectV3();

        jsc.setSigningCredentials((String) data.get("clientID"), (String) data.get("secret"));
        jsc.setVersion((String) data.get("version"));
        jsc.setTimestamp((long) data.get("timestamp"));

        JSONObject user = (JSONObject) data.get("user");
        if (user.isEmpty()) {
            jsc.setGuest(true);
        }
        for (Object o : user.keySet()) {
            Object item = user.get(o);
            jsc.setUserField((String) o, item);
        }

        try {
            String responseUrl = jsc.generateResponseLocation((String) data.get("jwt"));
            assertJWTUrlsEqual((String) data.get("response"), responseUrl);
        } catch (Exception ex) {
            if (!data.containsKey("exception")) {
                throw ex;
            }
        }
    }

    @Test
    public void testGettersSetters() {
        JsConnectV3 jsc = new JsConnectV3();

        assertEquals("123", jsc.setUniqueID("123").getUniqueID());
        assertEquals("foo", jsc.setName("foo").getName());
        assertEquals("foo@example.com", jsc.setEmail("foo@example.com").getEmail());
        assertEquals("https://example.com", jsc.setPhotoURL("https://example.com").getPhotoURL());

        jsc.setSigningCredentials("id", "secret");
        assertEquals("id", jsc.getSigningClientID());
        assertEquals("secret", jsc.getSigningSecret());
    }
}