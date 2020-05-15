package de.htwsaar.simpleHTTP.lib;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Map;

public class Response {
    private int statusCode;
    private final HttpExchange httpExchange;

    public final static int HTTP_OK = 200;
    public final static int HTTP_BAD_REQUEST = 400;
    public final static int HTTP_NOT_FOUND = 404;
    public final static int HTTP_INTERNAL_SERVER_ERROR = 500;

    /**
     * Response wraps around the HttpExchange to create an response
     *
     * @param httpExchange HttpExchange
     */
    public Response(HttpExchange httpExchange) {
        // Default to OK (200)
        this.statusCode = HTTP_OK;
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
     * Encodes an objects (to string) and transmits it as string
     *
     * @param toSend String to send
     * @throws IOException
     */
    public void text(String toSend) throws IOException {
        this.httpExchange.getResponseHeaders().add("Content-Type", "text/plain");
        transmitText(toSend);
    }

    /**
     * Encodes an objects (to json) and transmits it as string
     *
     * WARNING: Only a Map String->String
     *
     * @param o Object to serialize
     * @throws IOException
     */
    public void json(Object o) throws IOException {
        if(!(o instanceof Map)) {
            throw new RuntimeException("Dummy JSON serializer, Object type not supported");
        }

        // Hacky way of serializing a simple string->string map to a JSON object
        String toSend = "{\n";
        Map<String, String> m = (Map<String, String>) o;
        toSend += m.entrySet()
                .stream()
                .map(e -> "  \"" + e.getKey() + "\": \"" + e.getValue() + "\"")
                .reduce((xs, x) -> xs + ",\n" + x)
                .orElse("");
        toSend += "\n}";

        this.httpExchange.getResponseHeaders().add("Content-Type", "application/json");
        transmitText(toSend);
    }

    private void transmitText(String t) throws IOException {
        this.httpExchange.sendResponseHeaders(this.statusCode, t.length());
        Writer out = new OutputStreamWriter(httpExchange.getResponseBody());
        out.write(t);
        out.close();
    }

    public int getStatusCode() {
        return statusCode;
    }
}
