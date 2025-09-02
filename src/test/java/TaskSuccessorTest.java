package test.java;

import com.yandex.app.model.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

public class TaskSuccessorTest {

    @Test
    void epicsWithSameIdShouldBeEqual() {
        Epic epic1 = new Epic(1, "Epic 1", "Description 1");
        Epic epic2 = new Epic(1, "Epic 2", "Description 2"); // Разные названия, но одинаковый id

        assertEquals(epic1, epic2, "Эпики с одинаковым id должны быть равны");
    }

    @Test
    void subtasksWithSameIdShouldBeEqual() {
        Epic epic = new Epic(1, "Epic", "Description");
        SubTask subTask1 = new SubTask(2, "SubTask 1", "Desc 1", epic.getId());
        SubTask subTask2 = new SubTask(2, "SubTask 2", "Desc 2", epic.getId()); // Разные названия, но одинаковый id

        assertEquals(subTask1, subTask2, "Подзадачи с одинаковым id должны быть равны");
    }

    @Test
    void differentTaskTypesWithSameIdShouldNotBeEqual() {
        Epic epic = new Epic(1, "Epic", "Description");
        SubTask subTask = new SubTask(1, "SubTask", "Description", epic.getId());

        assertNotEquals(epic, subTask, "Эпик и подзадача не должны быть равны, даже с одинаковым id");
    }
}
