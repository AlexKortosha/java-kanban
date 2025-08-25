package com.yandex.app.serviсe;

import com.yandex.app.model.Task;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class InMemoryHistoryManager implements HistoryManager {


    private static class Node {
        Task task;                                                      // задача которую хотим хранить в истории
        Node prev;                                                      // ссылка на предыдущий узел
        Node next;                                                      // ссылка на следующий узел

        Node(Task task) {
            this.task = task;
        }
    }

    private final List<Task> history = new LinkedList<>(); // LinkedList для удаления сначала списка (Надеюсь так можно:) )


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

    @Override
    public void remove(int id) {
        history.remove(id);
    }

    private void linkLast(Node newNode) {
        if(head == null) {
            head = newNode;
            tail = newNode;
        } else {
            tail.next = newNode;
            newNode.prev = tail;
            tail = newNode;
        }
    }

}
