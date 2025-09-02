package test.java;

import com.yandex.app.model.*;
import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class TaskTest {
    @Test
    void tasksShouldBeEqualIfIdsAreEqual() {
        // Создаем две задачи с одинаковым id
        Task task1 = new Task(1, "Task 1", "Description 1");
        Task task2 = new Task(1, "Task 2", "Description 2");

        assertEquals(task1, task2, "Задачи с одинаковым id должны быть равны");
    }

    @Test
    void tasksShouldNotBeEqualIfIdsAreDifferent() {
        Task task1 = new Task(1, "Task", "Description");
        Task task2 = new Task(2, "Task", "Description");

        assertNotEquals(task1, task2, "Задачи с разным id не должны быть равны");
    }

    @Test
    void hashCodeShouldBeEqualForSameId() {
        Task task1 = new Task(1, "Task 1", "Description 1");
        Task task2 = new Task(1, "Task 2", "Description 2");

        assertEquals(task1.hashCode(), task2.hashCode(),
                "hashCode должен быть одинаковым для задач с одинаковым id");
    }


    @Test
    void taskShouldNotEqualOtherClass() {
        Task task = new Task(1, "Task", "Description");
        Object otherObject = new Object();
        assertNotEquals(task, otherObject, "Задача не должна быть равна объекту другого класса");
    }


}