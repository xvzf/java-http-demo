package de.htwsaar.simpleHTTP.lib;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * The Router class implements an HttpHandler and routes traffic to annotated methods
 */
public class Router implements HttpHandler {
    private final Map<String, Method> routeTable;

    /**
     * The constructor uses reflections for finding annotated methods and adds them to the internal routing table
     *
     * @param cls Class to scan
     */
    public Router(Class<?> cls) {
        this.routeTable = new HashMap<>();

        for (Method method : cls.getMethods()) {
            Route route = method.getAnnotation(Route.class);
            if (route != null) {
                this.routeTable.put(route.method() + ":" + route.path(), method);
            }
        }
    }

    /**
     * Routes incoming requests according to the internal Routing table
     *
     * @param t HttpExchange
     * @throws IOException
     */
    @Override
    public void handle(HttpExchange t) throws IOException {
        Request req = new Request(t);
        Response res = new Response(t);

        try {
            Method method = this.routeTable.get(req.getMethod() + ":" + req.getPath());

            // Try to pass execution to the correct handler function
            method.invoke(null, req, res);

        } catch (InvocationTargetException | IllegalAccessException ie) {
            Router.handleInternalServerError(req, res);
        } catch (NullPointerException ne) {
            Router.handleNotFound(req, res);
        }

        System.out.println(java.time.LocalTime.now() + " [" + req.getPath() + "] " + req.getMethod() + " " + res.getStatusCode());
    }

    /**
     * Sends out a 500 status code for internal server error
     *
     * @param req Request Object
     * @param res Response Object
     * @throws IOException
     */
    private static void handleInternalServerError(Request req, Response res) throws IOException {
        res.withStatusCode(Response.HTTP_INTERNAL_SERVER_ERROR).text("Internal Server Error");
    }

    /**
     * Sends out a 404 status code for internal server error
     *
     * @param req Request Object
     * @param res Response Object
     */
    private static void handleNotFound(Request req, Response res) throws IOException {
        res.withStatusCode(Response.HTTP_NOT_FOUND).text("Not Found");
    }
}
