package com.vanillaforums.vanilla;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.Map;
import java.util.stream.Stream;

class SignJsConnectTest {

    public static final String SECRET = "secret";
    public static final String CLIENT_ID = "clientID";

    @ParameterizedTest
    @MethodSource("provideSignJsConnectTests")
    void testSignJsConnect(Map data, String hashType, String expected) {
        String clientID = CLIENT_ID;
        String secret = SECRET;

        String actual = jsConnect.SignJsConnect(data, clientID, secret, hashType);

        assertEquals(expected, actual);
    }

    @Test
    void testSignJsConnectBC() {
        Map<String, String> john = new java.util.HashMap<>();
        john.put("name", "John PHP");
        john.put("email", "john.php@example.com");
        john.put("unique_id", "123");

        String actual = jsConnect.SignJsConnect(john, CLIENT_ID, SECRET, true);
        assertEquals("f1639a1838bd904cb967423be0567802", actual);
        assertEquals(CLIENT_ID, john.get("client_id"));
        assertEquals("f1639a1838bd904cb967423be0567802", john.get("sig"));
    }

    private static Stream<Arguments> provideSignJsConnectTests() {
        Map<String, String> john = new java.util.HashMap<>();
        john.put("name", "John PHP");
        john.put("email", "john.php@example.com");
        john.put("unique_id", "123");

        Map<String, String> incorrectCase = new java.util.HashMap<>();
        incorrectCase.put("Name", "John PHP");
        incorrectCase.put("eMail", "john.php@example.com");
        incorrectCase.put("UNIQUE_id", "123");

        return Stream.of(
            Arguments.of(
                john,
                "sha256",
                "71528bfbb99aba97734f79beab6d1eca1416e05a0587e9ab55b99095753f74b6"
            ),
            Arguments.of(
                john,
                "sha1",
                "72779f39737e4e2863732c174bd8696ef8d2bba5"
            ),
            Arguments.of(
                john,
                "md5",
                "f1639a1838bd904cb967423be0567802"
            ),
            Arguments.of(
                incorrectCase,
                "sha256",
                "71528bfbb99aba97734f79beab6d1eca1416e05a0587e9ab55b99095753f74b6"
            ),
            Arguments.of(
                john,
                "bad",
                "UNSUPPORTED HASH ALGORITHM"
            )
        );
    }
}