package com.vanillaforums.vanilla;

import org.apache.commons.text.StringEscapeUtils;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.URI;
import java.util.HashMap;

public class JsConnectServlet extends HttpServlet {
    private HashMap<String, ?> user;
    private String clientID, secret, hashType;

    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("text/html;charset=utf-8");
        try {
            URI uri = new URI(request.getRequestURI());
            Response jsResponse = jsConnect.getJsConnectResponse(user, uri, clientID, secret, hashType);
            if (jsResponse.getStatus() == 302) {
                response.sendRedirect(jsResponse.getContent());
            } else {
                response.setContentType(jsResponse.getContentType());
                response.getWriter().println(jsResponse.getContent());
            }
        } catch (Exception ex) {
            response.getWriter().println(StringEscapeUtils.escapeHtml4(ex.getMessage()));
        }
    }
}
