package com.yandex.app.serviсe;

import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {

    private static final int MAX_HISTORY_SIZE = 10; // константа для размера истории


    private final LinkedList<Task> history = new LinkedList<>(); // LinkedList для удаления сначала списка (Надеюсь так можно:) )


    @Override
    public List<Task> getHistory() {
        return  new ArrayList<>(history);
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }

        history.addLast(task);
        if (history.size() > MAX_HISTORY_SIZE) {
            history.removeFirst();
        }
    }

}
