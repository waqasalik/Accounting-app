package com.accounting;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/** Mini accounting REST API using only the JDK's built-in HTTP server. */
public class App {

    private final Ledger ledger = new Ledger();

    public static void main(String[] args) throws IOException {
        int port = Integer.parseInt(System.getenv().getOrDefault("PORT", "5000"));
        HttpServer server = new App().start(port);
        System.out.println("Accounting app listening on port " + server.getAddress().getPort());
    }

    /** Starts the server. Pass 0 to pick any free port (used by tests). */
    public HttpServer start(int port) throws IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/health", ex -> send(ex, 200, "{\"status\":\"ok\"}"));
        server.createContext("/summary", ex -> send(ex, 200, ledger.summary().toJson()));
        server.createContext("/transactions", this::handleTransactions);
        server.start();
        return server;
    }

    private void handleTransactions(HttpExchange ex) throws IOException {
        try {
            String path = ex.getRequestURI().getPath();
            String method = ex.getRequestMethod();

            if (path.equals("/transactions") || path.equals("/transactions/")) {
                if (method.equals("GET")) {
                    List<Transaction> all = ledger.list();
                    String json = all.stream().map(Transaction::toJson).collect(Collectors.joining(",", "[", "]"));
                    send(ex, 200, json);
                    return;
                }
                if (method.equals("POST")) {
                    String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
                    Transaction t = ledger.add(
                            JsonUtil.stringField(body, "description"),
                            JsonUtil.numberField(body, "amount"),
                            JsonUtil.stringField(body, "type"));
                    send(ex, 201, t.toJson());
                    return;
                }
            } else if (path.startsWith("/transactions/") && method.equals("DELETE")) {
                int id = Integer.parseInt(path.substring("/transactions/".length()));
                if (ledger.delete(id)) {
                    send(ex, 200, "{\"deleted\":" + id + "}");
                } else {
                    send(ex, 404, error("transaction not found"));
                }
                return;
            }
            send(ex, 405, error("method not allowed"));
        } catch (IllegalArgumentException e) {
            send(ex, 400, error(e.getMessage()));
        }
    }

    private static String error(String message) {
        return "{\"error\":\"" + JsonUtil.escape(String.valueOf(message)) + "\"}";
    }

    private static void send(HttpExchange ex, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().set("Content-Type", "application/json");
        ex.sendResponseHeaders(status, bytes.length);
        try (OutputStream os = ex.getResponseBody()) {
            os.write(bytes);
        }
    }
}
