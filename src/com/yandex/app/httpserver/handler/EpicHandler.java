package com.yandex.app.httpserver.handler;

import com.google.gson.JsonSyntaxException;
import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.service.TaskManager;


import com.yandex.app.model.Epic;
import com.yandex.app.httpserver.HttpMethod;
import java.io.IOException;

public class EpicHandler extends BaseHttpHandler {

    public EpicHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        HttpMethod method = getMethod(exchange);
        if (method == null) {
            sendBadRequest(exchange);
            return;
        }

        try {
            switch (method) {
                case GET -> handleGet(exchange);
                case POST -> handlePost(exchange);
                case DELETE -> handleDelete(exchange);
            }
        } catch (IllegalArgumentException e) {
            sendNotFound(exchange, "Epic not found");
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            Epic epic = taskManager.findEpicById(id);
            if (epic != null) {
                sendSuccess(exchange, gson.toJson(epic));
            } else {
                sendNotFound(exchange, "EPIC with id=" + id + " not found");
            }
        } else {
            sendSuccess(exchange, gson.toJson(taskManager.getAllEpics()));
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        Epic epic = gson.fromJson(body, Epic.class);

        if (epic.getId() == 0) {
            taskManager.addEpic(epic);
        } else {
            taskManager.updateEpic(epic);
        }
        sendEmpty(exchange);
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            taskManager.deleteEpic(id);
        } else {
            taskManager.deleteAllEpic();
        }
        sendEmpty(exchange);
    }
}
