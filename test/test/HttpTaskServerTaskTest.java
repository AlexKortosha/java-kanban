package test;

import com.yandex.app.model.Task;
import org.junit.jupiter.api.Test;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class HttpTaskServerTaskTest extends BaseHttpTest {

    @Test
    public void shouldAddTaskSuccessfully() throws Exception {
        Task task = new Task(0, "Test task", "Description");
        task.setDuration(Duration.ofMinutes(15));
        task.setStartTime(LocalDateTime.now());

        String json = gson.toJson(task);

        System.out.println("REQUEST JSON:\n" + json);

        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks"))
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        List<Task> tasks = manager.getAllTasks();
        assertEquals(1, tasks.size());
        assertEquals("Test task", tasks.get(0).getName());
    }

    @Test
    public void shouldReturn404ForNonExistingTask() throws Exception {
        HttpClient client = HttpClient.newHttpClient();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("http://localhost:8080/tasks?id=999"))
                .GET()
                .build();

        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

        assertEquals(404, response.statusCode());
    }

}
