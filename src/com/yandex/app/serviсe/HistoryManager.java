package com.yandex.app.serviсe;

import com.yandex.app.model.Task;
import java.util.List;

public interface HistoryManager {

    void add(Task task);

    List<Task> getHistory();

    void remove(int id);

}


