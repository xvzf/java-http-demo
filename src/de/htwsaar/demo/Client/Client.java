package de.htwsaar.demo.Client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.regex.Pattern;

public class Client {
    private final String endpoint;
    private static final Pattern JsonPattern = Pattern.compile("\"(.*)\": +\"(.*)\"");

    public Client(String endpoint) {
        this.endpoint = endpoint;
    }

    public Map<String, String> getAll() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(endpoint + "/get/all"))
                .build();

        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        return jsonParser(res.body());
    }

    public Optional<Map<String, String>> getByID(String id) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(endpoint + "/get/id?id=" + id))
                .build();

        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() == 200) {
            return Optional.of(jsonParser(res.body()));
        }

        return Optional.empty();
    }

    public boolean add(String id, String value) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(endpoint + "/add?id=" + id))
                .method("PUT",
                        HttpRequest.BodyPublishers.ofString(value))
                .build();

        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        return res.statusCode() == 200;
    }

    public boolean update(String id, String value) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(endpoint + "/update?id=" + id))
                .method("UPDATE",
                        HttpRequest.BodyPublishers.ofString(value))
                .build();

        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        return res.statusCode() == 200;
    }

    public boolean delete(String id) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest req = HttpRequest.newBuilder()
                .uri(URI.create(endpoint + "/delete?id=" + id))
                .method("DELETE",
                        HttpRequest.BodyPublishers.noBody()
                )
                .build();

        HttpResponse<String> res = client.send(req, HttpResponse.BodyHandlers.ofString());
        return res.statusCode() == 200;
    }

    private static Map<String, String> jsonParser(String s) {
        Map<String, String> out = new HashMap<>();

        String oneLineStripped = s.strip()
                .replaceAll("[\n\r{}]]", "");

        Arrays.stream(oneLineStripped.split(","))
                .map(String::strip)
                .map(JsonPattern::matcher)
                .forEach(m -> {
                    if (m.find()) {
                        out.put(m.group(1), m.group(2));
                    }
                });

        return out;
    }
}
