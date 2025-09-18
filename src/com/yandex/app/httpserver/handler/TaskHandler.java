package com.yandex.app.httpserver.handler;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.model.Task;
import com.yandex.app.service.TaskManager;
import com.yandex.app.httpserver.HttpMethod;
import com.yandex.app.exception.TaskOverlapException;

import java.io.IOException;

public class TaskHandler extends BaseHttpHandler {

    public TaskHandler(TaskManager taskManager) {
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
            sendNotFound(exchange, "Task not found");
        } catch (TaskOverlapException e) {
            sendHasOverlaps(exchange);
        } catch (Exception e) {
            sendInternalServerError(exchange);
        }
    }

    private void handleGet(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            Task task = taskManager.findTaskById(id);
            if (task != null) {
                sendSuccess(exchange, gson.toJson(task));
            } else {
                sendNotFound(exchange, "Task with id=" + id + " not found");
            }
        } else {
            sendSuccess(exchange, gson.toJson(taskManager.getAllTasks()));
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        Task task = gson.fromJson(body, Task.class);

        if (task.getId() == 0) {
            taskManager.addTask(task);
            exchange.sendResponseHeaders(201, 0);
        } else {
            taskManager.updateTask(task);
            exchange.sendResponseHeaders(200, 0);
        }
        sendEmpty(exchange);
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            taskManager.deleteTask(id);
        } else {
            taskManager.deleteAllTasks();
        }
        sendEmpty(exchange);
    }
}
