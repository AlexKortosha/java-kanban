package test.java;

import com.yandex.app.model.*;
import com.yandex.app.service.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

import java.lang.reflect.Field;

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
    void testLinkLastAddsToEmptyList() throws Exception {
        historyManager.add(task1);

        Field headField = InMemoryHistoryManager.class.getDeclaredField("head");
        Field tailField = InMemoryHistoryManager.class.getDeclaredField("tail");
        headField.setAccessible(true);
        tailField.setAccessible(true);

        Object head = headField.get(historyManager);
        Object tail = tailField.get(historyManager);

        assertNotNull(head);
        assertNotNull(tail);
        assertEquals(head, tail);
    }

    @Test
    void testLinkLastAddsToEnd() throws Exception {
        historyManager.add(task1);
        historyManager.add(task2);
        historyManager.add(task3);

        Field headField = InMemoryHistoryManager.class.getDeclaredField("head");
        Field tailField = InMemoryHistoryManager.class.getDeclaredField("tail");
        headField.setAccessible(true);
        tailField.setAccessible(true);

        Object head = headField.get(historyManager);
        Object tail = tailField.get(historyManager);

        assertNotEquals(head, tail); // Голова и хвост разные
    }



    @Test
    void testRemoveNodeFromSingleElementList() throws Exception {
        historyManager.add(task1);

        Field headField = InMemoryHistoryManager.class.getDeclaredField("head");
        Field tailField = InMemoryHistoryManager.class.getDeclaredField("tail");
        headField.setAccessible(true);
        tailField.setAccessible(true);

        historyManager.remove(task1.getId());

        Object head = headField.get(historyManager);
        Object tail = tailField.get(historyManager);

        assertNull(head);
        assertNull(tail);
    }


}
