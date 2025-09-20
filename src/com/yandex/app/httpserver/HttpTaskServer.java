package com.yandex.app.httpserver;

import com.sun.net.httpserver.HttpServer;
import com.yandex.app.httpserver.handler.*;
import com.yandex.app.service.Managers;
import com.yandex.app.service.TaskManager;

import java.io.IOException;
import java.net.InetSocketAddress;

public class HttpTaskServer {

    private static final int PORT = 8080;
    private static final TaskManager taskManager = Managers.getDefault();
    private static HttpServer httpServer;

    public static void main(String[] args) {
        start();
    }

    public static TaskManager getTaskManager() {
        return taskManager;
    }

    public HttpTaskServer(TaskManager taskManager) throws IOException {
         httpServer = HttpServer.create(new InetSocketAddress(PORT), 0);

        // Регистрация эндпоинтов
        httpServer.createContext("/tasks", new TaskHandler(taskManager));
        httpServer.createContext("/subtasks", new SubTaskHandler(taskManager));
        httpServer.createContext("/epics", new EpicHandler(taskManager));
        httpServer.createContext("/history", new HistoryHandler(taskManager));
        httpServer.createContext("/prioritized", new PrioritizedHandler(taskManager));
    }


    public static void start() {
        httpServer.start();
        System.out.println("Http Tasks server started:) PORT:" + PORT);

    }

    public static void stop() {
        httpServer.stop(1);
        System.out.println("Http Tasks server stopped");
    }
}
