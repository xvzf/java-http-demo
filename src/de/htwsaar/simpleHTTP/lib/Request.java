package de.htwsaar.simpleHTTP.lib;

import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.util.*;

public class Request {
    private final String path;
    private final String method;
    private final Map<String, List<String>> queryArgs;
    private final Map<String, List<String>> headers;
    private final String payload;


    /**
     * Request extracts request information from an HttpExchange
     *
     * @param t HttpExchange
     * @throws IOException
     */
    public Request(HttpExchange t) throws IOException {
        this.path = t.getRequestURI().getPath();
        this.method = t.getRequestMethod();
        this.payload = new String(t.getRequestBody().readAllBytes());
        t.getRequestBody().close();

        // Fill request headers
        this.headers = new HashMap<>();
        t.getRequestHeaders().forEach(this.headers::put);

        // Fill query arguments
        this.queryArgs = new HashMap<>();
        String query = t.getRequestURI().getQuery();
        if(query == null) return;
        String[] rawQueryArgs = query.split("&");
        for(String rawArg : rawQueryArgs) {
            String[] arg = rawArg.split("=");
            if(arg.length != 2) {
                System.err.println("Failed parsing query argument '" + rawArg + "'");
                continue;
            }

            List<String> queryArgArray = queryArgs.getOrDefault(arg[0], new LinkedList<>());
            queryArgArray.add(arg[1]);
            queryArgs.put(arg[0], queryArgArray);
        }

    }


    public String getPath() {
        return path;
    }

    public String getMethod() {
        return method;
    }

    public Map<String, List<String>> getQueryArgs() {
        return queryArgs;
    }

    public Map<String, List<String>> getHeaders() {
        return headers;
    }

    public String getPayload() {
        return payload;
    }

    @Override
    public String toString() {
        return "Request{" +
                "path='" + path + '\'' +
                ", method='" + method + '\'' +
                ", queryArgs=" + queryArgs +
                ", headers=" + headers +
                ", payload='" + payload + '\'' +
                '}';
    }
}
