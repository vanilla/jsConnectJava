package com.vanillaforums.jsconnect;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.jupiter.api.Test;
import org.skyscreamer.jsonassert.JSONAssert;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class MiscTest {
    @Test
    void testJsonEncode() throws JSONException {
        Map m = new HashMap();
        m.put("t", true);
        m.put("f", false);
        m.put("s", "string");

        JSONObject actual = new JSONObject(jsConnect.JsonEncode(m));
        JSONObject js = new JSONObject(m);
        JSONAssert.assertEquals(js, actual, true);
    }

    @Test
    void testHashBC() {
        String actual = jsConnect.hash("test");
        assertEquals("098f6bcd4621d373cade4e832627b4f6", actual);
    }

    @Test
    void testValStringArray() {
        String[] v = {"Hello", "World"};
        Map m = new HashMap();
        m.put("k", v);

        assertEquals("Hello", jsConnect.Val(m, "k"));
    }
}
