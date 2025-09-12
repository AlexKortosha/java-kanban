package test;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import java.util.List;

import com.yandex.app.service.*;
import com.yandex.app.model.*;

public class EpicStatusTest {

    InMemoryTaskManager manager;

    @BeforeEach
    void setUp() {
        manager = new InMemoryTaskManager();
    }

    @Test
    void epicStatusShouldBeNewWhenAllSubtasksNew() {
        Epic epic = new Epic(0, "Epic", "desc");
        manager.addEpic(epic);

        SubTask sub1 = new SubTask(0, "Sub1", "desc", epic.getId());
        sub1.setStatus(TaskStatus.NEW);
        manager.addSubTask(sub1);

        SubTask sub2 = new SubTask(0, "Sub2", "desc", epic.getId());
        sub2.setStatus(TaskStatus.NEW);
        manager.addSubTask(sub2);

        Epic savedEpic = manager.findEpicById(epic.getId());
        assertEquals(TaskStatus.NEW, savedEpic.getStatus(), "Все подзадачи NEW → эпик NEW");
    }

    @Test
    void epicStatusShouldBeDoneWhenAllSubtasksDone() {
        Epic epic = new Epic(0, "Epic", "desc");
        manager.addEpic(epic);

        SubTask sub1 = new SubTask(0, "Sub1", "desc", epic.getId());
        sub1.setStatus(TaskStatus.DONE);
        manager.addSubTask(sub1);

        SubTask sub2 = new SubTask(0, "Sub2", "desc", epic.getId());
        sub2.setStatus(TaskStatus.DONE);
        manager.addSubTask(sub2);

        Epic savedEpic = manager.findEpicById(epic.getId());
        assertEquals(TaskStatus.DONE, savedEpic.getStatus(), "Все подзадачи DONE → эпик DONE");
    }

    @Test
    void epicStatusShouldBeInProgressWhenSubtasksNewAndDone() {
        Epic epic = new Epic(0, "Epic", "desc");
        manager.addEpic(epic);

        SubTask sub1 = new SubTask(0, "Sub1", "desc", epic.getId());
        sub1.setStatus(TaskStatus.NEW);
        manager.addSubTask(sub1);

        SubTask sub2 = new SubTask(0, "Sub2", "desc", epic.getId());
        sub2.setStatus(TaskStatus.DONE);
        manager.addSubTask(sub2);

        Epic savedEpic = manager.findEpicById(epic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getStatus(), "Смешанные NEW и DONE → эпик IN_PROGRESS");
    }

    @Test
    void epicStatusShouldBeInProgressWhenAnySubtaskInProgress() {
        Epic epic = new Epic(0, "Epic", "desc");
        manager.addEpic(epic);

        SubTask sub1 = new SubTask(0, "Sub1", "desc", epic.getId());
        sub1.setStatus(TaskStatus.IN_PROGRESS);
        manager.addSubTask(sub1);

        SubTask sub2 = new SubTask(0, "Sub2", "desc", epic.getId());
        sub2.setStatus(TaskStatus.NEW);
        manager.addSubTask(sub2);

        Epic savedEpic = manager.findEpicById(epic.getId());
        assertEquals(TaskStatus.IN_PROGRESS, savedEpic.getStatus(), "Есть IN_PROGRESS → эпик IN_PROGRESS");
    }

}
