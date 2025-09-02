package test;

import com.yandex.app.model.*;
import com.yandex.app.service.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

import java.util.List;

public class LinckedListOrerationTest {

    private InMemoryHistoryManager historyManager;
    private Task task1;
    private Task task2;
    private Task task3;

    @BeforeEach
    void setUp() {
        historyManager = new InMemoryHistoryManager();
        task1 = new Task(1, "Task 1", "Description 1");
        task2 = new Task(2, "Task 2", "Description 2");
        task3 = new Task(3, "Task 3", "Description 3");
    }

    @Test
    void testAddSingleTask() {
        historyManager.add(task1);
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task1, history.get(0));
    }

    @Test
    void testAddMultipleTasks() {
        historyManager.add(task1);
        historyManager.add(task2);
        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(task1, history.get(0));
        assertEquals(task2, history.get(1));
    }

    @Test
    void testRemoveTaskFromHistory() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        Field headField = InMemoryHistoryManager.class.getDeclaredField("head");
        Field tailField = InMemoryHistoryManager.class.getDeclaredField("tail");
        headField.setAccessible(true);
        tailField.setAccessible(true);

        historyManager.remove(task1.getId());
        List<Task> history = historyManager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task2, history.get(0));
    }

    @Test
    void testRemoveLastTaskResultsInEmptyHistory() {
        historyManager.add(task1);
        historyManager.remove(task1.getId());
        List<Task> history = historyManager.getHistory();

        assertTrue(history.isEmpty());
    }

    @Test
    void testAddingSameTaskMovesItToEnd() {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task1);

        List<Task> history = historyManager.getHistory();

        assertEquals(2, history.size());
        assertEquals(task2, history.get(0));
    }


}
