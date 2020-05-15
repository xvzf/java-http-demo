package de.htwsaar.simpleHTTP.lib;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

/**
 * Server wraps around the internal JavaSE HttpServer and allows adding a custom Router for request routing
 */
public class Server {
    private final HttpServer server;

    /**
     * The constructor creates a new HttpServer
     *
     * @param host Hostname, e.g. "0.0.0.0" or "::"
     * @param port Port to listen on
     * @throws IOException
     */
    public Server(final String host, final int port) throws IOException {
        this.server = HttpServer.create(new InetSocketAddress(host, port), 0);
        this.server.setExecutor(null);
    }

    /**
     * Starts the server
     */
    public void start() {
        this.server.start();
    }

    /**
     * Stops the server
     */
    public void stop() {
        this.server.stop(0);
    }

    /**
     * Adds a new router at a given context
     *
     * @param path Context of the router, i.e. /
     * @param router Actual router
     */
    public void addRouter(String path, Router router) {
        this.server.createContext(path, router);
    }
}
