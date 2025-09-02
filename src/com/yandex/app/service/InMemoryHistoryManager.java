package com.yandex.app.service;

import com.yandex.app.model.Task;

import java.util.*;



public class InMemoryHistoryManager implements HistoryManager {


    private static class Node {
        Task task;                                                      // задача которую хотим хранить в истории
        Node prev;                                                      // ссылка на предыдущий узел
        Node next;                                                      // ссылка на следующий узел

        Node(Task task) {
            this.task = task;
        }
    }


    private final Map<Integer, Node> historyMap = new HashMap<>();
    private final Node head = new Node(null);
    private final Node tail = new Node(null);

    public InMemoryHistoryManager() {
        head.next = tail;
        tail.prev = head;
    }


    @Override
    public List<Task> getHistory() {
        List<Task> result = new ArrayList<>();
        Node current = head.next;
        while (current != tail) {
            result.add(current.task);
            current = current.next;
        }
        return result;
    }

    @Override
    public void add(Task task) {
        if (task == null) {
            return;
        }
        remove(task.getId());

        Node newNode = new Node(task);
        linkLast(newNode);
        historyMap.put(task.getId(), newNode);
    }

    @Override
    public void remove(int id) {
        Node node = historyMap.remove(id);
        if (node != null) {
            removeNode(node);
        }
    }

    private void linkLast(Node newNode) {
      Node prev = tail.prev;
      prev.next = newNode;
      newNode.prev = prev;
      newNode.next = tail;
      tail.prev = newNode;
    }

    private void removeNode(Node node) {
        if (node == null) {
            return;
        }
       node.prev.next = node.next;
       node.next.prev = node.prev;
        node.prev = null;
        node.next = null;
        node.task = null;
    }

}
