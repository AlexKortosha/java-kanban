package test;

import com.yandex.app.model.*;
import com.yandex.app.service.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

abstract class TaskManagerTest<T extends TaskManager> {

    protected T manager;

    protected abstract T createManager();

    @BeforeEach
    void setup() {
        manager = createManager();
    }

    //============================= TASK TESTS =============================

    @Test
    void addAndFindTask() {
        Task task = new Task(0, "Task1", "Description1");
        task.setDuration(Duration.ofMinutes(30));
        task.setStartTime(LocalDateTime.now());

        Task addedTask = manager.addTask(task);
        Task foundTask = manager.findTaskById(addedTask.getId());

        assertNotNull(foundTask);
        assertEquals(addedTask, foundTask);
    }

    @Test
    void deleteTask() {
        Task task = new Task(0, "Task1", "Description1");
        manager.addTask(task);

        manager.deleteTask(task.getId());
        assertNull(manager.findTaskById(task.getId()));
    }

    @Test
    void updateTask() {
        Task task = new Task(0, "Task1", "Description1");
        manager.addTask(task);

        task.setName("Updated");
        manager.updateTask(task);

        Task updated = manager.findTaskById(task.getId());
        assertEquals("Updated", updated.getName());
    }

    //============================= EPIC TESTS =============================

    @Test
    void addAndFindEpic() {
        Epic epic = new Epic(0, "Epic1", "Epic description");
        manager.addEpic(epic);

        Epic found = manager.findEpicById(epic.getId());
        assertNotNull(found);
        assertEquals(epic, found);
    }

    @Test
    void deleteEpicAndSubtasks() {
        Epic epic = new Epic(0, "Epic1", "Epic description");
        manager.addEpic(epic);

        SubTask subTask = new SubTask(0, "SubTask1", "Desc", epic.getId());
        manager.addSubTask(subTask);

        manager.deleteEpic(epic.getId());
        assertNull(manager.findEpicById(epic.getId()));
        assertNull(manager.findSubTaskById(subTask.getId()));
    }

    //============================= SUBTASK TESTS =============================

    @Test
    void addAndFindSubtask() {
        Epic epic = new Epic(0, "Epic1", "Epic description");
        manager.addEpic(epic);

        SubTask subTask = new SubTask(0, "SubTask1", "Desc", epic.getId());
        manager.addSubTask(subTask);

        SubTask found = manager.findSubTaskById(subTask.getId());
        assertNotNull(found);
        assertEquals(subTask, found);
    }

    @Test
    void updateSubtaskAndEpicStatus() {
        Epic epic = new Epic(0, "Epic1", "Epic description");
        manager.addEpic(epic);

        SubTask subTask = new SubTask(0, "SubTask1", "Desc", epic.getId());
        manager.addSubTask(subTask);

        subTask.setStatus(TaskStatus.DONE);
        manager.updateSubtask(subTask);

        SubTask updated = manager.findSubTaskById(subTask.getId());
        Epic updatedEpic = manager.findEpicById(epic.getId());

        assertEquals(TaskStatus.DONE, updated.getStatus());
        assertEquals(TaskStatus.DONE, updatedEpic.getStatus());
    }

    //============================= HISTORY TEST =============================

    @Test
    void historyTracksAccess() {
        Task task = new Task(0, "Task1", "Description1");
        manager.addTask(task);

        manager.findTaskById(task.getId());
        List<Task> history = manager.getHistory();

        assertEquals(1, history.size());
        assertEquals(task, history.get(0));
    }

}
