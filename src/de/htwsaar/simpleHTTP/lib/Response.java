package de.htwsaar.simpleHTTP.lib;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;

public class Response {
    private int statusCode;
    private final HttpExchange httpExchange;

    /**
     * Response wraps around the HttpExchange to create an response
     *
     * @param httpExchange HttpExchange
     */
    public Response(HttpExchange httpExchange) {
        // Default to OK (200)
        this.statusCode = 200;
        this.httpExchange = httpExchange;
    }

    /**
     * Sets the status code for the response
     *
     * @param statusCode Status Code to set
     * @return Response with the requested status code set
     */
    public Response withStatusCode(int statusCode) {
        this.statusCode = statusCode;
        return this;
    }

    /**
     * Sends a string
     *
     * @param toSend String to send
     * @throws IOException
     */
    public void send(String toSend) throws IOException {
        this.httpExchange.sendResponseHeaders(this.statusCode, toSend.length());
        this.httpExchange.getResponseBody().write(toSend.getBytes());
        this.httpExchange.close();
    }

    public int getStatusCode() {
        return statusCode;
    }
}
