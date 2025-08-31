package com.yandex.app;

import com.yandex.app.serviсe.InMemoryTaskManager;
import com.yandex.app.serviсe.TaskManager;

public class Main {
    public static void main(String[] args) {
        TaskManager manager = new InMemoryTaskManager();

        System.out.println("###СОЗДАНИЕ ЗАДАЧ###");
    }
}
