package com.yandex.app;

import com.yandex.app.service.*;
import com.yandex.app.httpserver.HttpTaskServer;
import java.io.IOException;
import com.yandex.app.httpserver.*;

public class Main {
    public static void main(String[] args) throws IOException {
        TaskManager taskManager = Managers.getDefault(); // или InMemoryTaskManager
        HttpTaskServer server = new HttpTaskServer(taskManager);
        server.start();
    }
}
