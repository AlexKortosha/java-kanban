package com.yandex.app.httpserver.handler;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.model.Task;
import com.yandex.app.service.TaskManager;
import com.yandex.app.httpserver.HttpMethod;

import java.io.IOException;
import java.util.List;

public class PrioritizedHandler extends BaseHttpHandler {

    public PrioritizedHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (getMethod(exchange) == HttpMethod.GET) {
            sendSuccess(exchange, gson.toJson(taskManager.getPrioritizedTasks()));
        } else {
            sendBadRequest(exchange);
        }
    }
}
