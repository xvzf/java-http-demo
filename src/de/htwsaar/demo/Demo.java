package de.htwsaar.demo;

import de.htwsaar.simpleHTTP.lib.*;

import java.io.IOException;
import java.util.List;

public class Demo {

    /**
     * Extract ID from query arguments
     *
     * @param req Request
     * @return Extracted ID; null if not present
     */
    private static String getId(Request req) {
        List<String> id = req.getQueryArgs().get("id");
        if (id != null && id.size() > 0) {
            return id.get(0);
        }
        return null;
    }

    /**
     * Generates an internal server error (on purpose)
     */
    @Route(path = "/error")
    public static void genError(Request req, Response res) throws IOException {
        throw new RuntimeException("We want to create an exception");
    }

    /**
     * Query by id; pass "id" in the query string
     */
    @Route(path = "/get/id")
    public static void getID(Request req, Response res) throws IOException {
        String id = getId(req);
        if (id == null) {
            res.withStatusCode(400).send("Missing ID");
            return;
        }

        // Query ID in the dummy database & send row
        res.send(
                Database.getInstance()
                        .select(id)
                        .toString()
        );
    }

    /**
     * Query all; no further arguments required
     */
    @Route(path = "/get/all")
    public static void getAll(Request req, Response res) throws IOException {

        // Send everything contained in the dummy database
        res.send(
                Database.getInstance()
                        .select()
                        .toString()
        );
    }

    /**
     * Insert a new entry in the dummy database; required arguments: id; request body contains the value to set
     */
    @Route(path = "/add", method = "PUT")
    public static void addData(Request req, Response res) throws IOException {
        String id = getId(req);
        if (id == null) {
            res.withStatusCode(400).send("Missing ID");
            return;
        }

        boolean ok = Database.getInstance().insert(id, req.getPayload());

        if (!ok) {
            res.withStatusCode(409).send("ID already exists");
        }
        res.send("OK");
    }

    /**
     * Insert an existing entry in the dummy database; required arguments: id; request body contains the value to set
     */
    @Route(path = "/update", method = "UPDATE")
    public static void updateData(Request req, Response res) throws IOException {
        String id = getId(req);
        if (id == null) {
            res.withStatusCode(400).send("Missing ID");
            return;
        }

        boolean ok = Database.getInstance().insert(id, req.getPayload());

        if (!ok) {
            res.withStatusCode(404).send("Key not found");
        }
        res.send("OK");
    }

    /**
     * Deletes an existing entry in the dummy database; required arguments: id
     */
    @Route(path = "/delete", method = "DELETE")
    public static void deleteData(Request req, Response res) throws IOException {
        String id = getId(req);
        if (id == null) {
            res.withStatusCode(400).send("Missing ID");
            return;
        }

        boolean ok = Database.getInstance().delete(id);

        if (!ok) {
            res.withStatusCode(404).send("Key not found");
        }
        res.send("OK");
    }

    public static void main(String[] args) {
        try {
            // Create server
            Server server = new Server("::", 8080);

            // Add Routers
            server.addRouter("/", new Router(Demo.class));

            // Start the HTTP Server
            server.start();
        } catch (IOException ie) {
            System.err.println(ie);
        }
    }
}