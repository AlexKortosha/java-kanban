package test.com.yandex.app;


import com.yandex.app.model.*;
import com.yandex.app.service.*;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.BeforeEach;

public class TaskMutabilityTest {
    private TaskManager taskManager;
    private Task originalTask;

    @BeforeEach
    void setUp() {
        taskManager = Managers.getDefault();
        originalTask = new Task(0, "Task", "Description");
        taskManager.addTask(originalTask);
    }

    @Test
    void testExternalModificationAffectsManager() {
        Task taskFromManager = taskManager.findTaskById(originalTask.getId());

        taskFromManager.setName("Name");
        taskFromManager.setDescription("Description");
        taskFromManager.setStatus(TaskStatus.DONE);

        Task updatedTask = taskManager.findTaskById(originalTask.getId());
        assertEquals("Name", updatedTask.getName());
        assertEquals("Description", updatedTask.getDescription());
        assertEquals(TaskStatus.DONE, updatedTask.getStatus());
    }

    @Test
    void testMultipleReferencesSameObject() {
        Task task1 = taskManager.findTaskById(originalTask.getId());
        Task task2 = taskManager.findTaskById(originalTask.getId());


        task1.setName("Change task 1");

        assertEquals("Change task 1", task2.getName());
    }

}
