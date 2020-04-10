package com.vanillaforums.jsconnect;

/**
 * Represents a very basic HTTP response class for use with jsConnect.
 */
public class Response {
    private final String contentType;
    protected final Integer status;
    protected final String content;

    /**
     * Construct a response object.
     *
     * @param status The HTTP response code.
     * @param content The content, this is either the body or the URL for 3xx responses.
     */
    public Response(int status, String content, String contentType) {
        this.status = status;
        this.content = content;
        this.contentType = contentType;
    }

    /**
     * Get the HTTP status.
     */
    public Integer getStatus() {
        return status;
    }

    /**
     * Get the content, this is either the body or the URL for 3xx responses.
     */
    public String getContent() {
        return content;
    }

    /**
     * Get the content MIME type. This is applicable from
     */
    public String getContentType() {
        return contentType;
    }
}
