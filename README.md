# Vanilla jsConnect Client Library for Java

[![Travis (.com)](https://img.shields.io/travis/com/vanilla/jsConnectJava)](https://travis-ci.com/vanilla/jsConnectJava)

This repository contains the files you need to use Vanilla's jsConnect with a java project.

## Installation

There are two ways to install jsConnect.

1. You can install this package via maven. It's package name is `com.vanillaforums.jsconnect`.
2. You can copy the source files directly into your application. All of the source files you need are located at `src/main/java/com/vanillaforums/vanilla/*.java`.

## Usage

To use jsConnect you will need to make a web page that gives information about the currently signed in user of your site. To do this you'll need the following information:

- You will need the client ID and secret that you configured from within Vanilla's dashboard.
- The currently signed in user or if there is no signed in user you'll also need that.

### Basic Usage

Here is a basic servlet that describes how to use jsConnect with the version 3 protocol.

```java
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
```

The servlet instantiates a `JsConnectV3` object and sets it up. It then calls `JsConnectV3::generateResponseLocation()` with the current URI to process the request. You need to 302 redirect to that location.

If there is an exception you will need to display that on your page. Remember to escape the message.

### Backwards Compatible Usage

If you previously used the `jsConnect` class to implement your SSO, you will need to update its usage. Here is an example:

```java
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
```

Here are the changes you need to make:

1. Change your call of `jsConnect.GetJsConnectString()` to `jsConnect.getJsConnectResponse()`. It has a similar signature, but takes a `URI` representing the full request URL rather than a `Map` of the query string.
2. You need to look at the response to see what status is returned. If you get a 302 then this indicates a version 3 request and you need to redirect with the response content representing the URL to redirect to. If you get a different response then this indicates a version 2 request and you can out put the content to the page. Don't forget to set the content type.
3. You now have to make sure you exception handle the call to jsConnect and output any exception message on your page. Don't forget to escape the output of the exception. 

### Configuring Vanilla

Once you've made your authentication page you will need to add that URL to your jsConnect settings in Vanilla's dashboard. This is the **authentication URL**.

## Change Log

### Version 3

- Renamed the Maven artifact to `jsconnect` so now the full package is `com.vanillaforums.jsconnect`.
- Added support for the new jsConnect 3 protocol using the `JsConnectV3` class.

### Version 2.1

- Added support for SHA1 and SHA256 hashing. We strongly recommend you use one of these hash methods.
- Removed dependencies on some external libraries.
- Added unit tests for most functionality.
- Moved test SSO string to `jsConnect.GetTestJsConnectString()`.
- Deprecated some of the methods from previous versions that use MD5. 

### Version 2

- Added more security information for the version 2 protocol of jsConnect.
- Fixed some issues with malformed callbacks.
- Added support for the jsConnect SSO string for embedded SSO.
  
## Requirements

This project requires Java 8 at a minimum. You can look at the [build](https://travis-ci.com/vanilla/jsConnectJava) to see what other versions are being built.
