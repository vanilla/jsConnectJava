
<%@page contentType="application/json" pageEncoding="UTF-8"%>
<%
// 1. Get your client ID and secret here. These must match those in your jsConnect settings.
String clientID = "123";
String secret = "123";

// 2. Grab the current user from your session management system or database here.
Boolean signedIn = true; // this is just a placeholder

// YOUR CODE HERE.

// 3. Fill in the user information in a way that Vanilla can understand.
java.util.HashMap user = new java.util.LinkedHashMap();

if (signedIn) {
   // CHANGE THESE FOUR LINES.
   user.put("uniqueid", "1");
   user.put("name", "John Iñtërnâtiônàlizætiøn");
   user.put("email", "john.doe@anonymous.com");
   user.put("photourl", "");
}

// 4. Generate the jsConnect string.
Boolean secure = true; // this should be true unless you are testing.
String js = Vanilla.jsConnect.GetJsConnectString(user, request.getParameterMap(), clientID, secret, secure);

out.write(js);
%>