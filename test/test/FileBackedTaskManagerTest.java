package test;

import com.yandex.app.model.*;
import com.yandex.app.service.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class FileBackedTaskManagerTest extends TaskManagerTest<FileBackedTaskManager> {

    private final File file = new File("test_tasks.csv");

    @Override
    protected FileBackedTaskManager createManager() {
        return new FileBackedTaskManager(file);
    }

    @AfterEach
    void cleanup() {
        if (file.exists()) {
            file.delete();
        }
    }

}
