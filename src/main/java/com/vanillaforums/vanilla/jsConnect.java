package com.vanillaforums.vanilla;

import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * @author Todd Burry <todd@vanillaforums.com>
 * @version 2.0 This object contains the client code for Vanilla jsConnect single-sign-on.
 */
public class jsConnect {

    static final String VERSION = "2";
    static final int TIMEOUT = 24 * 60;
    static long Now = 0;

    public static final String HASH_MD5 = "md5";
    public static final String HASH_SHA1 = "sha1";
    public static final String HASH_SHA256 = "sha256";

    /**
     * Convenience method that returns a map representing an error.
     *
     * @param code The code of the error.
     * @param message A user-readable message for the error.
     * @return
     */
    private static Map<String, String> Error(String code, String message) {
        Map<String, String> result = new HashMap<>();
        result.put("error", code);
        result.put("message", message);

        return result;
    }
    /**
     * Returns a JSONP formatted string suitable to be consumed by jsConnect.
     * This is usually the only method you need to call in order to implement
     * jsConnect.
     *
     * @param user A map containing the user information. The map should have
     * the following keys: - uniqueid: An ID that uniquely identifies the user
     * in your system. This value should never change for a given user.
     * @param request: A map containing the query string for the current
     * request. You usually just pass in request.getParameterMap().
     * @param clientID: The client ID for your site. This is usually configured
     * on Vanilla's jsConnect configuration page.
     * @param secret: The secret for your site. This is usually configured on
     * Vanilla's jsConnect configuration page.
     * @param secure: Whether or not to check security on the request. You can
     * leave this false for testing, but you should make it true in production.
     * @return The JSONP formatted string representing the current user.
     */
    @Deprecated
    public static String GetJsConnectString(Map user, Map request, String clientID, String secret, Boolean secure) {
        return GetJsConnectString(user, request, clientID, secret, HASH_MD5, secure);
    }

    /**
     * Returns a JSONP formatted string suitable to be consumed by jsConnect.
     * This is usually the only method you need to call in order to implement
     * jsConnect.
     *
     * @param user A map containing the user information. The map should have
     * the following keys: - uniqueid: An ID that uniquely identifies the user
     * in your system. This value should never change for a given user.
     * @param request A map containing the query string for the current
     * request. You usually just pass in request.getParameterMap().
     * @param clientID The client ID for your site. This is usually configured
     * on Vanilla's jsConnect configuration page.
     * @param secret The secret for your site. This is usually configured on
     * Vanilla's jsConnect configuration page.
     * @param hashType The hash algorithm to use.
     * @return The JSONP formatted string representing the current user.
     */
    public static String GetJsConnectString(Map user, Map request, String clientID, String secret, String hashType) {
        return GetJsConnectString(user, request, clientID, secret, hashType, true);
    }

    /**
     * Returns a response that will support jsConnect v2 or jsConnect v3.
     *
     * @param user A map containing the user information. The map should have the following keys:
     *             - uniqueid: An ID that uniquely identifies the user in your system. This value should never change for a given user.
     * @param uri The URI of the request. This should contain the jsConnect information.
     * @param clientID The client ID for your site. This is usually configured on Vanilla's jsConnect configuration page.
     * @param secret The secret for your site. This is usually configured on Vanilla's jsConnect configuration page.
     * @param hashType The hash algorithm to use.
     * @return Returns a response that indicates whether to output information or redirect.
     */
    public static Response getJsConnectResponse(Map user, URI uri, String clientID, String secret, String hashType) throws InvalidValueException {
        Map<String, String> query = JsConnectV3.splitQuery(uri.getQuery());
        Response response;

        if (query.containsKey(JsConnectV3.FIELD_JWT)) {
            // This is a v3 request.
            JsConnectV3 jsc = new JsConnectV3();
            Map<String, ?> userV3 = ConvertUserToV3(user);

            jsc.setSigningCredentials(clientID, secret);

            if (userV3.isEmpty()) {
                jsc.setGuest(true);
            } else {
                for (Map.Entry<String, ?> entry : userV3.entrySet()){
                    jsc.setUserField(entry.getKey(), entry.getValue());
                }
            }
            String location = jsc.generateResponseLocation(query.get(JsConnectV3.FIELD_JWT));
            response = new Response(302, location, "text/html;charset=utf-8");
        } else {
            String content = GetJsConnectString(user, query, clientID, secret, hashType);
            response = new Response(200, content, "text/javascript;charset=utf-8");
        }

        return response;
    }

    /**
     * Convert a jsConnect v2 user to v3.
     * @param user The user to convert.
     * @return Returns the converted user.
     */
    private static Map<String, ?> ConvertUserToV3(Map<?, ?> user) {
        Map result = new HashMap();
        for (Map.Entry<?, ?> entry : user.entrySet()) {
            String key = entry.getKey().toString().toLowerCase();
            switch (key) {
                case "uniqueid":
                    key = JsConnectV3.FIELD_UNIQUE_ID;
                    break;
                case "photourl":
                    key = JsConnectV3.FIELD_PHOTO;
                    break;
            }
            result.put(key, entry.getValue());
        }
        return result;
    }

    /**
     * This is a test version of `GetJsConnectString()` that doesn't check the request.
     */
    public static String GetTestJsConnectString(Map user, Map request, String clientID, String secret, String hashType) {
        return GetJsConnectString(user, request, clientID, secret, hashType, false);
    }

    /**
     * Returns a JSONP formatted string suitable to be consumed by jsConnect.
     * This is usually the only method you need to call in order to implement
     * jsConnect.
     *
     * @param user A map containing the user information. The map should have
     * the following keys: - uniqueid: An ID that uniquely identifies the user
     * in your system. This value should never change for a given user.
     * @param request: A map containing the query string for the current
     * request. You usually just pass in request.getParameterMap().
     * @param clientID: The client ID for your site. This is usually configured
     * on Vanilla's jsConnect configuration page.
     * @param secret: The secret for your site. This is usually configured on
     * Vanilla's jsConnect configuration page.
     * @param hashType The hash algorithm to use.
     * @param secure: Whether or not to check security on the request. You can
     * leave this false for testing, but you should make it true in production.
     * @return The JSONP formatted string representing the current user.
     */
    private static String GetJsConnectString(Map user, Map request, String clientID, String secret, String hashType, Boolean secure) {
        Map error = null;

        long timestamp;
        try {
            timestamp = Long.parseLong(Val(request, "timestamp"));
        } catch (Exception ex) {
            timestamp = 0;
        }
        long currentTimestamp = jsConnect.Timestamp();

        if (request.containsKey("callback") &&
            !request.get("callback").toString().matches("^[$a-zA-Z_][0-9a-zA-Z_$]*$")
        ) {
            return "console.error('Invalid callback parameter in jsConnect.')";
        }

        if (secure) {
            if (Val(request, "v") == null) {
                error = jsConnect.Error("invalid_request", "Missing the v parameter.");
            } else if (!Val(request, "v").equals(VERSION)) {
                error = jsConnect.Error("invalid_request", "Unsupported version " + Val(request, "v") + ".");
            } else if (Val(request, "client_id") == null) {
                error = jsConnect.Error("invalid_request", "Missing the client_id parameter.");
            } else if (!Val(request, "client_id").equals(clientID)) {
                error = jsConnect.Error("invalid_client", "Unknown client " + Val(request, "client_id") + ".");
            } else if (Val(request, "timestamp") == null && Val(request, "sig") == null) {
                error = new HashMap<String, Object>();
                if (user != null && !user.isEmpty()) {
                    error.put("name", user.containsKey("name") ? user.get("name") : "");
                    error.put("photourl", user.containsKey("photourl") ? user.get("photourl") : "");
                    error.put("signedin", true);
                } else {
                    error.put("name", "");
                    error.put("photourl", "");
                }
            } else if (timestamp == 0) {
                error = jsConnect.Error("invalid_request", "The timestamp parameter is missing or invalid.");
            } else if (Val(request, "sig") == null) {
                error = jsConnect.Error("invalid_request", "Missing the sig parameter.");
            } else if (Math.abs(currentTimestamp - timestamp) > TIMEOUT) {
                error = jsConnect.Error("invalid_request", "The timestamp is invalid.");
            } else if (Val(request, "nonce") == null) {
                error = jsConnect.Error("invalid_request", "Missing the nonce parameter.");
            } else if (Val(request, "ip") == null) {
                error = jsConnect.Error("invalid_request", "Missing the ip parameter.");
            } else {
                // Make sure the signature checks out.
                String sig = jsConnect.hash(Val(request, "ip") + Val(request, "nonce") + Long.toString(timestamp) + secret, hashType);
                if (!sig.equals(Val(request, "sig"))) {
                    error = jsConnect.Error("access_denied", "Signature invalid.");
                }
            }
        }

        Map result;

        if (error != null) {
            result = error;
        } else if (user != null && !user.isEmpty()) {
            user.put("ip", Val(request, "ip"));
            user.put("nonce", Val(request, "nonce"));
            result = new LinkedHashMap(user);
            String signature = SignJsConnect(result, clientID, secret, hashType);
            result.put("client_id", clientID);
            result.put("sig", signature);
            result.put("v", VERSION);
        } else {
            result = new LinkedHashMap<String, String>();
            result.put("name", "");
            result.put("photourl", "");
        }

        String json = jsConnect.JsonEncode(result);
        if (Val(request, "callback") == null) {
            return json;
        } else {
            return Val(request, "callback") + "(" + json + ");";
        }
    }

    /**
     * JSON encode some data.
     *
     * @param data The data to encode.
     * @return The JSON encoded data.
     */
    public static String JsonEncode(Map data) {
        StringBuilder result = new StringBuilder();

        for (Object o : data.entrySet()) {
            if (result.length() > 0) {
                result.append(",");
            }

            Map.Entry v = (Map.Entry) o;

            String key = v.getKey().toString();
            key = key.replace("\"", "\\\"");

            String value;
            String q = "\"";

            if (v.getValue() == Boolean.TRUE) {
                value = "true";
            } else if (v.getValue() == Boolean.FALSE) {
                value = "false";
            } else {
                value = v.getValue().toString();
                value = q + value.replace("\"", "\\\"") + q;
            }

            result.append(q + key + q + ":" + value);
        }

        return "{" + result.toString() + "}";
    }

    /**
     * Compute the hash of a string.
     *
     * @param password The data to compute the hash on.
     * @param hashType The hash algorithm to use.
     * @return A hex encoded string representing the hash of the string.
     */
    public static String hash(String password, String hashType) {
        String alg;
        try {
            switch (hashType) {
                case HASH_MD5:
                    alg = "MD5";
                    break;
                case HASH_SHA1:
                    alg = "SHA-1";
                    break;
                case HASH_SHA256:
                    alg = "SHA-256";
                    break;
                default:
                    return "UNSUPPORTED HASH ALGORITHM";
            }

            java.security.MessageDigest digest = java.security.MessageDigest.getInstance(alg);
            digest.update(password.getBytes("UTF-8"));
            byte[] hash = digest.digest();
            return hexEncode(hash);
        } catch (Exception ex) {
            return "ERROR";
        }
    }

    /**
     * Backwards compatible version of `hash()`.
     *
     * @param password The data to compute the hash on.
     * @return A hex encoded string representing the hash of the string.
     */
    @Deprecated
    public static String hash(String password) {
        return hash(password, HASH_MD5);
    }

    /**
     * Hex encode a byte array.
     * @param hash The data to encode.
     * @return
     */
    private static String hexEncode(byte[] hash) {
        StringBuilder ret = new StringBuilder();
        for (int i = 0; i < hash.length; i++) {
            String hex = Integer.toHexString(0xFF & hash[i]);
            if (hex.length() == 1) {
                // could use a for loop, but we're only dealing with a single byte
                ret.append('0');
            }
            ret.append(hex);
        }
        return ret.toString();
    }

    /**
     * Get a value from a map.
     *
     * @param request The map to get the value from.
     * @param key The key of the value.
     * @param defaultValue The default value if the map doesn't contain the
     * value.
     * @return The value from the map or the default if it isn't found.
     */
    public static String Val(Map request, String key, String defaultValue) {
        try {
            Object result = null;
            if (request.containsKey(key)) {
                result = request.get(key);
                if (result instanceof String[]) {
                    return ((String[]) request.get(key))[0];
                } else {
                    return result.toString();
                }
            }
        } catch (Exception ex) {
            return defaultValue;
        }
        return defaultValue;
    }

    /**
     * Get a value from a map.
     *
     * @param request The map to get the value from.
     * @param key The key of the value.
     * @return The value from the map or the null if it isn't found.
     */
    public static String Val(Map request, String key) {
        return Val(request, key, null);
    }

    /**
     * Sign a jsConnect response. Responses are signed so that the site
     * requesting the response knows that this is a valid site signing in.
     *
     * @param data The data to sign.
     * @param clientID The client ID of the site. This is usually configured on
     * Vanilla's jsConnect configuration page.
     * @param secret The secret of the site. This is usually configured on
     * Vanilla's jsConnect configuration page.
     * @param hashType The hash algorithm to use.
     * @return The computed signature of the data.
     */
    public static String SignJsConnect(Map data, String clientID, String secret, String hashType) {
        // Generate a sorted list of the keys.
        String[] keys = new String[data.keySet().size()];
        data.keySet().toArray(keys);
        Arrays.sort(keys, String.CASE_INSENSITIVE_ORDER);

        // Generate the String to sign.
        StringBuilder sigStr = new StringBuilder();
        for (int i = 0; i < keys.length; i++) {
            if (sigStr.length() > 0) {
                sigStr.append("&");
            }

            String key = keys[i];
            String value = data.get(key).toString();

            try {
                sigStr.append(java.net.URLEncoder.encode(key.toLowerCase(), "UTF-8"));
                sigStr.append("=");
                sigStr.append(java.net.URLEncoder.encode(value, "UTF-8"));
            } catch (Exception ex) {
                return "ERROR";
            }
        }

        // MD5 sign the String with the secret.
        String signature = jsConnect.hash(sigStr.toString() + secret, hashType);

        return signature;
    }

    /**
     * Sign a jsConnect response. Responses are signed so that the site
     * requesting the response knows that this is a valid site signing in.
     *
     * @param data The data to sign.
     * @param clientID The client ID of the site. This is usually configured on
     * Vanilla's jsConnect configuration page.
     * @param secret The secret of the site. This is usually configured on
     * Vanilla's jsConnect configuration page.
     * @param setData Whether or not to add the signature information to the
     * data.
     * @return The computed signature of the data.
     */
    @Deprecated
    public static String SignJsConnect(Map data, String clientID, String secret, Boolean setData) {
        String signature = SignJsConnect(data, clientID, secret, HASH_MD5);

        if (setData) {
            data.put("client_id", clientID);
            data.put("sig", signature);
        }
        return signature;
    }

    /**
     * Returns a string suitable for embedded SSO or API calls.
     *
     * @param user A map containing the user information. The map should have
     * the following keys: - uniqueid: An ID that uniquely identifies the user
     * in your system. This value should never change for a given user.
     * @param client_id: The client ID for your site. This is usually configured
     * on Vanilla's jsConnect configuration page.
     * @param secret: The secret for your site. This is usually configured on
     * Vanilla's jsConnect configuration page.
     * @return SSO string.
     */
    public static String SSOString(Map user, String client_id, String secret) throws InvalidKeyException {
        if (!user.containsKey("client_id")) {
            user.put("client_id", client_id);
        }
        if (user.get("client_id") == null || user.get("client_id") == "") {
            user.put("client_id", client_id);
        }

        String json = JsonEncode(user);
        String jsonBase64String = Base64.getEncoder().encodeToString(json.getBytes());
        String timestamp = String.valueOf(Timestamp());

        // Build the signature string.
        StringBuilder signatureString = new StringBuilder();
        signatureString.append(jsonBase64String);
        signatureString.append(" ");
        signatureString.append(timestamp);

        Mac mac;
        byte[] result = null;

        SecretKeySpec keySpec = new SecretKeySpec(secret.getBytes(), "HmacSHA1");

        try {
            mac = Mac.getInstance("HmacSHA1");
            mac.init(keySpec);
            result = mac.doFinal(signatureString.toString().getBytes());

        } catch (NoSuchAlgorithmException ex) {
            Logger.getLogger(jsConnect.class.getName()).log(Level.SEVERE, null, ex);
        }

        String usertext = jsonBase64String;
        String timestamptext = timestamp;
        String hash = new String(hexEncode(result));

        String returnValue = usertext + " " + hash + " " + timestamptext + " hmacsha1";

        return returnValue;
    }

    /**
     * Returns the current timestamp of the server, suitable for syncing with the site.
     *
     * @return The current timestamp.
     */
    public static long Timestamp() {
        if (Now > 0) {
            return Now;
        } else {
            return System.currentTimeMillis() / 1000;
        }
    }
}