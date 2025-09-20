package com.yandex.app.httpserver.handler;

import com.google.gson.Gson;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

import com.yandex.app.adapters.GsonAdapt;
import com.yandex.app.service.TaskManager;
import com.yandex.app.httpserver.HttpMethod;

public abstract class BaseHttpHandler implements HttpHandler {

    protected final TaskManager taskManager;
    protected final Gson gson = GsonAdapt.getGson();

    public BaseHttpHandler(TaskManager taskManager) {
        this.taskManager = taskManager;
    }

    protected HttpMethod getMethod(HttpExchange exchange) {
        try {
            return HttpMethod.valueOf(exchange.getRequestMethod());
        } catch (IllegalArgumentException e) {
            return null;
        }
    }

    protected void sendText(HttpExchange exchange, String text, int statusCode) throws IOException {
        byte[] reply = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, reply.length);
        exchange.getResponseBody().write(reply);
        exchange.close();
    }

    protected void sendSuccess(HttpExchange exchange, String reply) throws IOException {
        sendText(exchange, reply, 200);
    }

    protected void sendEmpty(HttpExchange exchange, int statusCode) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(statusCode, 0);
        exchange.close();
    }

    protected void sendNotFound(HttpExchange exchange, String text) throws IOException {
        byte[] reply = text.getBytes(StandardCharsets.UTF_8);
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(404, reply.length);
        exchange.getResponseBody().write(reply);
        exchange.close();
    }

    protected void sendHasOverlaps(HttpExchange exchange) throws IOException {
        exchange.getResponseHeaders().add("Content-Type", "application/json;charset=utf-8");
        exchange.sendResponseHeaders(406, 0);
        exchange.close();
    }

    protected void sendInternalServerError(HttpExchange exchange) throws IOException {
        sendText(exchange, "Internal Server Error", 500);
    }

    protected void sendBadRequest(HttpExchange exchange) throws IOException {
        sendText(exchange, "Bad Request", 400);
    }

    protected String readRequestBody(HttpExchange exchange) throws IOException {
        try (InputStream inputStream = exchange.getRequestBody()) {
            return new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        }
    }


}
