package com.vanillaforums.jsconnect;

import com.auth0.jwt.JWT;
import com.auth0.jwt.JWTCreator;
import com.auth0.jwt.JWTVerifier;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.interfaces.Claim;
import com.auth0.jwt.interfaces.Clock;
import com.auth0.jwt.interfaces.DecodedJWT;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URLDecoder;
import java.util.*;

public class JsConnectV3 {
    static final String VERSION = "java:3";

    static final String ALG_HS256 = "HS256";
    static final String ALG_HS384 = "HS384";
    static final String ALG_HS512 = "HS512";

    static final String FIELD_UNIQUE_ID = "id";
    static final String FIELD_PHOTO = "photo";
    static final String FIELD_NAME = "name";
    static final String FIELD_EMAIL = "email";
    static final String FIELD_ROLES = "roles";
    static final String FIELD_JWT = "jwt";
    static final String FIELD_STATE = "st";
    static final String FIELD_USER = "u";
    static final String FIELD_REDIRECT_URL = "rurl";
    static final String FIELD_CLIENT_ID = "kid";
    static final String FIELD_TARGET = "t";

    static final int TIMEOUT = 600;

    /**
     *
     */
    protected String signingSecret = "";

    /**
     * @var String String
     */
    protected String signingClientID = "";

    protected Map<String, Object> user;

    protected boolean guest = false;

    /**
     *
     */
    protected String signingAlgorithm;

    protected String version = null;

    protected long timestamp = 0;

    /**
     * JsConnect constructor.
     */
    public JsConnectV3() {
        this.user = new HashMap<>();
        this.signingAlgorithm = ALG_HS256;
    }

    /**
     * Validate a value that cannot be empty.
     *
     * @param value The value to test.
     * @param valueName The name of the value for the exception message.
     * @throws InvalidValueException Throws an exception when the value is empty.
     */
//    protected static void validateNotEmpty(Object value, String valueName) throws InvalidValueException {
//        if (value == null) {
//            throw new InvalidValueException(valueName + " is required.");
//        }
//        if (value == "") {
//            throw new InvalidValueException(valueName + " cannot be empty.");
//        }
//    }

    /**
     * Create the algorithm with the given name.
     *
     * @param alg    The string identifier of the algorithm.
     * @param secret The secret used to sign the algorithm.
     * @return Returns the new algorithm.
     * @throws InvalidValueException Throws an exception when the signing algorithm string is invalid.
     */
    protected static Algorithm createAlgorithm(String alg, String secret) throws InvalidValueException {
        switch (alg) {
            case JsConnectV3.ALG_HS256:
                return Algorithm.HMAC256(secret);
            case JsConnectV3.ALG_HS384:
                return Algorithm.HMAC384(secret);
            case JsConnectV3.ALG_HS512:
                return Algorithm.HMAC512(secret);
            default:
                throw new InvalidValueException("Invalid signing algorithm: " + alg);
        }
    }

    /**
     * Split a query string into a map of its parts.
     *
     * @param query The query to split.
     * @return Returns the split query
     */
    public static Map<String, String> splitQuery(String query) throws InvalidValueException {
        final Map<String, String> queryPairs = new LinkedHashMap<>();

        if (query == null) {
            return queryPairs;
        }

        final String[] pairs = query.split("&");
        for (String pair : pairs) {
            final int idx = pair.indexOf("=");
            final String key;
            try {
                key = idx > 0 ? URLDecoder.decode(pair.substring(0, idx), "UTF-8") : pair;
                final String value = idx > 0 && pair.length() > idx + 1 ? URLDecoder.decode(pair.substring(idx + 1), "UTF-8") : null;
                queryPairs.put(key, value);
            } catch (UnsupportedEncodingException e) {
                throw new InvalidValueException("The query contains an invalid query encoding.");
            }
        }
        return queryPairs;
    }

    /**
     * Set a field on the current user.
     *
     * @param key   The key on the user.
     * @param value The value to set. This must be a basic type that can be JSON encoded.
     * @return this
     */
    public JsConnectV3 setUserField(String key, Object value) {
        this.user.put(key, value);
        return this;
    }

    /**
     * Set a field on the current user.
     *
     * @param key The key on the user.
     * @return
     */
    public Object getUserField(String key) {
        return this.user.getOrDefault(key, null);
    }

    /**
     * Get the current user's username.
     *
     * @return
     */
    public String getName() {
        return (String) this.getUserField(FIELD_NAME);
    }

    /**
     * Set the current user's username.
     *
     * @param name The new name.
     * @return this
     */
    public JsConnectV3 setName(String name) {
        return this.setUserField(FIELD_NAME, name);
    }

    /**
     * Get the current user's avatar.
     *
     * @return
     */
    public String getPhotoURL() {
        return (String) this.getUserField(FIELD_PHOTO);
    }

    /**
     * Set the current user's avatar.
     *
     * @param photo The new photo URL.
     * @return this
     */
    public JsConnectV3 setPhotoURL(String photo) {
        return this.setUserField(FIELD_PHOTO, photo);
    }

    /**
     * Get the current user's unique ID.
     *
     * @return
     */
    public String getUniqueID() {
        return (String) this.getUserField(FIELD_UNIQUE_ID);
    }

    /**
     * Validate that a field exists in a collection.
     *
     * @param field The name of the field to validate.
     * @param collection The collection to look at.
     * @param collectionName The name of the collection.
     * @param validateEmpty If true, make sure the value is also not empty.
     * @return Returns the field value if there are no errors.
     * @throws FieldNotFoundException Throws an exception when the field is not in the array.
     * @throws InvalidValueException Throws an exception when the collection isn"t an array or the value is empty.
     */
//    protected static Object validateFieldExists(String field, Object collection, String collectionName, Boolean validateEmpty) throws InvalidValueException, FieldNotFoundException {
//        if (!(collection instanceof Map)) {
//            throw new InvalidValueException("Invalid array: $collectionName");
//        }
//
//        if (!((Map) collection).containsKey(field)) {
//            throw new FieldNotFoundException(field, collectionName);
//        }
//        Object value = ((Map) collection).get(field);
//        if (validateEmpty && (value == "" || value == null)) {
//            throw new InvalidValueException("Field cannot be empty: " + collectionName + "[" + field + "]");
//        }
//
//        return value;
//    }

    /**
     * Set the current user's unique ID.
     *
     * @param id The new unique ID.
     * @return $this
     */
    public JsConnectV3 setUniqueID(String id) {
        return this.setUserField(FIELD_UNIQUE_ID, id);
    }

    /**
     * Generate the location for an SSO redirect.
     *
     * @param requestJWT
     * @return String
     */
    public String generateResponseLocation(String requestJWT) throws InvalidValueException {
        // Validate the request token.
        Map<String, Claim> request = this.jwtDecode(requestJWT);
        Map<String, ?> user;

        if (this.isGuest()) {
            user = new HashMap<>();
        } else {
            user = this.getUser();
        }

        Map<String, ?> state = request.containsKey(JsConnectV3.FIELD_STATE) ?
            request.get(JsConnectV3.FIELD_STATE).asMap() :
            (new HashMap<>());

        String response = this.jwtEncode(user, state);
        String location = request.get(JsConnectV3.FIELD_REDIRECT_URL).asString() + "#jwt=" + response;
        return location;
    }

    /**
     * Generate the response location from a URI object.
     *
     * @param uri The request URI.
     * @return Returns a string URI.
     */
    public String generateResponseLocation(URI uri) throws InvalidValueException, FieldNotFoundException {
        final Map<String, String> query = splitQuery(uri.getQuery());
        final String jwt = query.getOrDefault(FIELD_JWT, "");
        if (jwt.equals("")) {
            throw new FieldNotFoundException(FIELD_JWT, "query");
        }
        return generateResponseLocation(jwt);
    }

    /**
     * Get the current user's email address.
     *
     * @return
     */
    public String getEmail() {
        return (String) this.getUserField(FIELD_EMAIL);
    }

    /**
     * Set the current user's email address.
     *
     * @param email The user's email address.
     * @return this
     */
    public JsConnectV3 setEmail(String email) {
        return this.setUserField(FIELD_EMAIL, email);
    }

    /**
     * @param jwt
     * @return array
     */
    protected Map<String, Claim> jwtDecode(String jwt) {
        Algorithm algorithm = Algorithm.HMAC256(this.signingSecret);
        JWTVerifier.BaseVerification verification = (JWTVerifier.BaseVerification) JWT.require(algorithm);
        JWTVerifier verifier;

        if (this.timestamp > 0) {
            Clock clock = new StaticClock(this.timestamp);
            verifier = verification.build(clock);
        } else {
            verifier = verification.build();
        }

        DecodedJWT result = verifier.verify(jwt);

        return result.getClaims();
    }

    /**
     * Whether or not the user is signed in.
     */
    public boolean isGuest() {
        return this.guest;
    }

    /**
     * Set whether or not the user is signed in.
     *
     * @param isGuest The new value.
     */
    public JsConnectV3 setGuest(boolean isGuest) {
        this.guest = isGuest;
        return this;
    }

    /**
     * Wrap a payload in a JWT.
     *
     * @param user  The user part of the response.
     * @param state The state to pass back to Vanilla.
     */
    protected String jwtEncode(Map<String, ?> user, Map<String, ?> state) throws InvalidValueException {
        long now = this.getTimestamp();

        Algorithm algorithm = createAlgorithm(this.signingAlgorithm, this.signingSecret);
        JWTCreator.Builder jwt = JWT.create()
            .withKeyId(this.getSigningClientID())
            .withClaim("v", this.getVersion())
            .withClaim("iat", now)
            .withClaim("exp", now + JsConnectV3.TIMEOUT)
            .withClaim(JsConnectV3.FIELD_USER, user)
            .withClaim(JsConnectV3.FIELD_STATE, state);

        String result = jwt.sign(algorithm);
        return result;
    }

    /**
     * Get the current timestamp.
     * <p>
     * This time is used for signing and verifying tokens.
     */
    protected long getTimestamp() {
        return this.timestamp > 0 ? this.timestamp : System.currentTimeMillis() / 1000;
    }

    /**
     * Override the timestamp used to validate and sign JWTs.
     *
     * @param timestamp The new timestamp.
     * @return Returns this.
     */
    public JsConnectV3 setTimestamp(long timestamp) {
        this.timestamp = timestamp;
        return this;
    }

    /**
     * Get the secret that is used to sign JWTs.
     */
    protected String getSigningSecret() {
        return this.signingSecret;
    }

    /**
     * Get the algorithm used to sign tokens.
     */
//    public String getSigningAlgorithm() {
//        return this.signingAlgorithm;
//    }

    /**
     * Set the algorithm used to sign tokens.
     *
     * @param signingAlgorithm The new signing algorithm.
     */
//    public JsConnectV3 setSigningAlgorithm(String signingAlgorithm) throws InvalidValueException {
//        Algorithm test = createAlgorithm(signingAlgorithm, "a");
//        this.signingAlgorithm = signingAlgorithm;
//        return this;
//    }

    /**
     * Get the client ID that is used to sign JWTs.
     *
     * @return String
     */
    protected String getSigningClientID() {
        return this.signingClientID;
    }

    /**
     * Set the credentials that will be used to sign requests.
     *
     * @param clientID The client ID used as the key ID in responses.
     * @param secret   The secret used to sign responses and validate requests.
     */
    public JsConnectV3 setSigningCredentials(String clientID, String secret) {
        this.signingClientID = clientID;
        this.signingSecret = secret;
        return this;
    }

    public Map<String, ?> getUser() {
        return this.user;
    }

    /**
     * Get the roles on the user.
     *
     * @return
     */
    public List<?> getRoles() {
        return (List<?>) this.getUserField(FIELD_ROLES);
    }

    /**
     * Set the roles on the user.
     *
     * @param roles A list of role names or IDs.
     */
    public JsConnectV3 setRoles(List<?> roles) {
        this.setUserField(JsConnectV3.FIELD_ROLES, roles);
        return this;
    }

    /**
     * Get the version used to sign responses.
     *
     * @return
     */
    public String getVersion() {
        return this.version == null ? VERSION : this.version;
    }

    /**
     * Override the version used in JWT claims.
     *
     * @param version The version override.
     * @return Returns this.
     */
    public JsConnectV3 setVersion(String version) {
        this.version = version;
        return this;
    }

    protected class StaticClock implements Clock {
        protected Date today;

        public StaticClock(long timestamp) {
            this.today = new Date(timestamp * 1000);
        }

        @Override
        public Date getToday() {
            return this.today;
        }
    }
}
