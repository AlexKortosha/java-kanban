package test;

import com.yandex.app.service.*;
import com.yandex.app.model.*;
import org.junit.jupiter.api.*;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest {

    File tempFile;
    FileBackedTaskManager manager;

    @BeforeEach
    void setUp() throws IOException {
        tempFile = File.createTempFile("tasks", ".csv");
        manager = new FileBackedTaskManager(tempFile);
    }

    @AfterEach
    void cleanUp() {
        if (tempFile.exists()) {
            tempFile.delete();
        }
    }

    @Test
    void shouldSaveAndLoadEmptyFile() {
        manager.save();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        assertTrue(loaded.getAllTasks().isEmpty(), "Должно быть пусто для задач");
        assertTrue(loaded.getAllEpics().isEmpty(), "Должно быть пусто для эпиков");
        assertTrue(loaded.getAllSubtasks().isEmpty(), "Должно быть пусто для сабтасков");
    }

    @Test
    void shouldSaveAndLoadSingleTask() {
        Task task = new Task(0, "Test task", "Desc");
        manager.addTask(task);
        manager.save();


        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        List<Task> tasks = loaded.getAllTasks();
        assertEquals(1, tasks.size(), "Должна быть одна задача");
        assertEquals("Test task", tasks.get(0).getName());
        assertEquals("Desc", tasks.get(0).getDescription());
    }

    @Test
    void shouldSaveAndLoadEpicWithSubtask() {
        Epic epic = new Epic(0, "Epic1", "desc");
        manager.addEpic(epic);

        SubTask sub = new SubTask(0, "Sub1", "desc", epic.getId());
        manager.addSubTask(sub);

        manager.save();

        FileBackedTaskManager loaded = FileBackedTaskManager.loadFromFile(tempFile);

        List<Epic> epics = loaded.getAllEpics();
        assertEquals(1, epics.size(), "Должен быть один эпик");
        assertEquals("Epic1", epics.get(0).getName());

        List<SubTask> subs = loaded.getAllSubtasks();
        assertEquals(1, subs.size(), "Должна быть одна подзадача");
        assertEquals("Sub1", subs.get(0).getName());
        assertEquals(epics.get(0).getId(), subs.get(0).getEpicId());
    }
}
