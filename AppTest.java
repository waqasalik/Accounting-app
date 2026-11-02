package com.accounting;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpServer;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import org.junit.jupiter.api.Test;

class AppTest {

    @Test
    void healthSummaryAndCreateWorkOverHttp() throws Exception {
        HttpServer server = new App().start(0);
        try {
            String base = "http://localhost:" + server.getAddress().getPort();
            HttpClient client = HttpClient.newHttpClient();

            HttpResponse<String> health = client.send(
                    HttpRequest.newBuilder(URI.create(base + "/health")).build(),
                    HttpResponse.BodyHandlers.ofString());
            assertEquals(200, health.statusCode());
            assertTrue(health.body().contains("ok"));

            HttpResponse<String> created = client.send(
                    HttpRequest.newBuilder(URI.create(base + "/transactions"))
                            .POST(HttpRequest.BodyPublishers.ofString(
                                    "{\"description\":\"Salary\",\"amount\":500,\"type\":\"income\"}"))
                            .build(),
                    HttpResponse.BodyHandlers.ofString());
            assertEquals(201, created.statusCode());

            HttpResponse<String> summary = client.send(
                    HttpRequest.newBuilder(URI.create(base + "/summary")).build(),
                    HttpResponse.BodyHandlers.ofString());
            assertTrue(summary.body().contains("\"balance\":500"));
        } finally {
            server.stop(0);
        }
    }
}
