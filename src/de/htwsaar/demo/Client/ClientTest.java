package de.htwsaar.demo.Client;

import de.htwsaar.demo.Server.Database;
import de.htwsaar.demo.Server.Demo;
import de.htwsaar.simpleHTTP.lib.Router;
import de.htwsaar.simpleHTTP.lib.Server;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class ClientTest {

    static Server server;

    static String ENDPOINT = "http://localhost:8080";

    private static final HashMap<String, String> dataset = new HashMap<>();

    static {
        for (int i = 0; i < 10; i++) {
            dataset.put("key" + i, "value" + i);
        }
    }

    /**
     * Spins up a local server
     *
     * @throws IOException
     */
    @org.junit.jupiter.api.BeforeAll
    static void preamble() throws IOException {

        // Create server
        server = new Server("::", 8080);

        // Add Routers
        server.addRouter("/", new Router(Demo.class));

        var runner = new Runnable() {
            @Override
            public void run() {
                // Start the HTTP Server
                server.start();
            }
        };

        new Thread(runner).start();
    }

    /**
     * Tears down the local server
     *
     * @throws IOException
     */
    @org.junit.jupiter.api.AfterAll
    static void teardown() throws IOException {
        server.stop();
    }

    @org.junit.jupiter.api.BeforeEach()
    void cleanup() throws IOException, InterruptedException {
        Database.getInstance().clear();
        var c = new Client(ENDPOINT);

        dataset.forEach((k, v) -> {
            try {
                assertTrue(c.add(k, v));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

    }

    /**
     * Test add functionality (already existing)
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @org.junit.jupiter.api.Test
    void addExisting() throws IOException, InterruptedException {
        var c = new Client(ENDPOINT);

        dataset.forEach((k, v) -> {
            try {
                assertFalse(c.add(k,v));
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Test getAll functionality
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @org.junit.jupiter.api.Test
    void getAll() throws IOException, InterruptedException {
        var c = new Client(ENDPOINT);

        var m = c.getAll();
        assertEquals(m.size(), dataset.size());
        assertTrue(m
                .entrySet()
                .stream()
                .allMatch(
                        e -> e.getValue().equals(dataset.get(e.getKey()))
                ));
    }

    /**
     * Test getByID functionality
     */
    @org.junit.jupiter.api.Test
    void getByID() {
        var c = new Client(ENDPOINT);

        dataset.forEach((k, v) -> {
            try {
                var res = c.getByID(k);
                // Check if key exists
                assertTrue(res.isPresent());
                // Check if key is equal to what we have in our local test map
                res.ifPresent(m -> {
                    assertEquals(1, m.size());
                    assertEquals(v, m.getOrDefault(k, "not" + v));
                });
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Test update functionality
     */
    @org.junit.jupiter.api.Test
    void update() {
        var c = new Client(ENDPOINT);

        // Iterate over every item in the database & update it
        dataset.forEach((k, v) -> {
            try {
                // Build updated value + update it on remote side
                String newValue = v + "updated";
                assertTrue(c.update(k, newValue));

                // Get the updated value from remote side and ensure it is valid
                var res = c.getByID(k);
                assertFalse(res.isEmpty());
                res.ifPresent(m -> {
                    assertEquals(1, m.size());
                    assertEquals(newValue, m.getOrDefault(k, "not" + newValue));
                });

            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

    /**
     * Test update functionality (on failure)
     */
    @org.junit.jupiter.api.Test
    void updateFailed() throws IOException, InterruptedException {
        var c = new Client(ENDPOINT);
        assertFalse(c.update("notinremotedb", "1234"));
    }

    /**
     * Test delete functionality
     *
     * @throws IOException
     * @throws InterruptedException
     */
    @org.junit.jupiter.api.Test
    void delete() throws IOException, InterruptedException {
        var c = new Client(ENDPOINT);
        dataset.forEach((k, v) -> {
            try {
                // Delete key on remote side
                assertTrue(c.delete(k));

                // Check if value is not retrievable from remote anymore
                var res = c.getByID(k);
                assertTrue(res.isEmpty());
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        });

        assertEquals(0, c.getAll().size());
    }
}
