package com.yandex.app.httpserver.handler;

import com.sun.net.httpserver.HttpExchange;
import com.yandex.app.httpserver.HttpMethod;
import com.yandex.app.service.TaskManager;

import java.io.IOException;


public class HistoryHandler extends BaseHttpHandler {

    public HistoryHandler(TaskManager taskManager) {
        super(taskManager);
    }

    @Override
    public void handle(HttpExchange exchange) throws IOException {
        if (getMethod(exchange) == HttpMethod.GET) {
            sendSuccess(exchange, gson.toJson(taskManager.getHistory()));
        } else {
            sendBadRequest(exchange);
        }
    }
}
