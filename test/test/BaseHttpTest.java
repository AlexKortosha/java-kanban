package test;

import com.google.gson.Gson;
import com.yandex.app.httpserver.HttpTaskServer;
import com.yandex.app.service.InMemoryTaskManager;
import com.yandex.app.service.TaskManager;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import com.yandex.app.adapters.*;

import java.io.IOException;

public class BaseHttpTest {

    protected TaskManager manager;
    protected HttpTaskServer server;
    protected Gson gson;

    @BeforeEach
    public void setUp() throws IOException {
        manager = new InMemoryTaskManager();
        server = new HttpTaskServer(manager);
        gson = GsonAdapt.getGson();
        HttpTaskServer.start();
    }

    @AfterEach
    public void tearDown() {
        HttpTaskServer.stop();
    }

}
