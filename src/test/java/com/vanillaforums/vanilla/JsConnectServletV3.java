package com.vanillaforums.vanilla;

import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

public class JsConnectServletV3 extends HttpServlet {
    private String clientID, secret;
    private boolean signedIn;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        try {
            URI uri = new URI(request.getRequestURI());
            JsConnectV3 jsc = new JsConnectV3();

            jsc.setSigningCredentials(clientID, secret);

            if (signedIn) {
                jsc
                    .setUniqueID("123")
                    .setName("username")
                    .setEmail("user@exmple.com")
                    .setPhotoURL("https://example.com/avatar.jpg");
            } else {
                jsc.setGuest(true);
            }

            String location = jsc.generateResponseLocation(uri);
            response.sendRedirect(location);
        } catch (Exception ex) {
            response.getWriter().println(StringEscapeUtils.escapeHtml4(ex.getMessage()));
        }
    }
}
