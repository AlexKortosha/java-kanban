package test.java;

import com.yandex.app.model.*;
import com.yandex.app.service.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class ManagersTest {
    @Test
    void getDefaultReturnsInitializedTaskManager() {
        TaskManager taskManager = Managers.getDefault();
        assertNotNull(taskManager, "Менеджер задач не должен быть null");

        Task task = taskManager.addTask(new Task(0, "Test task", "Description"));
        assertNotNull(task.getId(), "Задача должна иметь ID");
        assertEquals(1, taskManager.getAllTasks().size(), "Менеджер должен содержать добавленную задачу");
    }

    @Test
    void getDefaultHistoryReturnsInitializedHistoryManager() {
        HistoryManager historyManager = Managers.getDefaultHistory();
        assertNotNull(historyManager, "Менеджер истории не должен быть null");

        Task task = new Task(1, "Test", "Description");
        historyManager.add(task);
        assertEquals(1, historyManager.getHistory().size(), "История должна содержать добавленную задачу");
    }
}