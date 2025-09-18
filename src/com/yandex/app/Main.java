package com.yandex.app;

import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskManager;
import com.yandex.app.httpserver.HttpTaskServer;

public class Main {
    public static void main(String[] args) throws Exception {
        TaskManager manager = new InMemoryTaskManager(); // твоя реализация TaskManager
        HttpTaskServer httpServer = new HttpTaskServer(manager);
        httpServer.start();
    }
}
