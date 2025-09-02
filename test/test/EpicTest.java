package test;

import com.yandex.app.model.Epic;
import com.yandex.app.model.SubTask;
import com.yandex.app.service.TaskManager;
import com.yandex.app.service.Managers;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class EpicTest {
    @Test
    void epicCannotBeAddedAsItsOwnSubtask() {
        TaskManager manager = Managers.getDefault();

        Epic epic = manager.addEpic(new Epic(0, "Epic", "Description"));

        SubTask invalidSubTask = new SubTask(epic.getId(), "Invalid", "Desc", epic.getId());

        SubTask result = manager.addSubTask(invalidSubTask);
        assertNull(result, "Система должна возвращать null при попытке добавить эпик как собственную подзадачу");

    }
}