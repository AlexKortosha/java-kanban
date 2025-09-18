package com.yandex.app.httpserver.handler;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.model.SubTask;
import com.yandex.app.service.TaskManager;
import com.yandex.app.httpserver.HttpMethod;
import com.yandex.app.exception.TaskOverlapException;

import java.io.IOException;

public class SubTaskHandler extends BaseHttpHandler {

    public SubTaskHandler(TaskManager taskManager) {
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
            sendNotFound(exchange, "Subtask not found");
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
            SubTask subTask = taskManager.findSubTaskById(id);
            if (subTask != null) {
                sendSuccess(exchange, gson.toJson(subTask));
            } else {
                sendNotFound(exchange, "SubTask with id=" + id + " not found");
            }
        } else {
            sendSuccess(exchange, gson.toJson(taskManager.getAllSubtasks()));
        }
    }

    private void handlePost(HttpExchange exchange) throws IOException {
        String body = readRequestBody(exchange);
        SubTask subTask = gson.fromJson(body, SubTask.class);

        if (subTask.getId() == 0) {
            taskManager.addSubTask(subTask);
        } else {
            taskManager.updateSubtask(subTask);
        }
        sendEmpty(exchange);
    }

    private void handleDelete(HttpExchange exchange) throws IOException {
        String query = exchange.getRequestURI().getQuery();
        if (query != null && query.startsWith("id=")) {
            int id = Integer.parseInt(query.substring(3));
            taskManager.deleteSubtask(id);
        } else {
            taskManager.deleteAllSubTask();
        }
        sendEmpty(exchange);
    }
}
