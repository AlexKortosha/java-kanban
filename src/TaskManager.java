import java.util.HashMap;
import java.util.ArrayList;


public class TaskManager {
    private final HashMap<Integer, Task> tasks = new HashMap<>();
    private final HashMap<Integer, SubTask> subTasks = new HashMap<>();
    private final HashMap<Integer, Epic> epics = new HashMap<>();
    private int newId = 1;

    //геттеры для списков tasks, epics, subTasks


    public HashMap<Integer, Task> getTasks() {
        return tasks;
    }

    public HashMap<Integer, SubTask> getSubTasks() {
        return subTasks;
    }

    public HashMap<Integer, Epic> getEpics() {
        return epics;
    }

    // РАЗДЕЛ TASK======================================================================================================
    public Task addTask(String name, String description) {
        Task task = new Task(newId++,description, name);
        tasks.put(task.getId(), task);
        return task;

    }


    public void deleteAllTasks() {
        tasks.clear();
    }

    public void deleteTask(int id) {
        tasks.remove(id);
    }

    public void updateTask(Task updatedTask) {
        if (tasks.containsKey(updatedTask.getId())) {
            tasks.put(updatedTask.getId(), updatedTask);
        }
    }

    public Task findTaskById(int id) {
        Task task = tasks.get(id);
        return task;
    }

    public ArrayList<Task> getAllTasks() {
        return new ArrayList<>(tasks.values());
    }

    // РАЗДЕЛ SUBTASK===================================================================================================
    public SubTask addSubTask(String name, String description, int epicId) {
        if (!epics.containsKey(epicId)) {
            System.out.println("Epic not found");
            return null;
        }else {

            SubTask subtask = new SubTask(newId++, name, description, epicId);
            subTasks.put(subtask.getId(), subtask);
            epics.get(epicId).addSubtask(subtask);
            return subtask;
        }
    }

    public void deleteAllSubTask() {
        subTasks.clear();
    }

    public void deleteSubtask(int id) {
        SubTask subtask = subTasks.remove(id);
        if (subtask != null) {
            epics.get(subtask.getEpicId()).removeSubtask(id);
        }
    }

    public void updateSubtask(SubTask updatedSubtask) {
        if (subTasks.containsKey(updatedSubtask.getId())) {
            subTasks.put(updatedSubtask.getId(), updatedSubtask);
            epics.get(updatedSubtask.getEpicId()).updateStatus();
        }
    }


    public SubTask findSubTaskById(int id) {
        SubTask subTask = subTasks.get(id);
        return subTask;
    }

    public ArrayList<SubTask> getAllSubtasks() {
        return new ArrayList<>(subTasks.values());
    }

    // РАЗДЕЛ EPIC======================================================================================================
    public Epic addEpic(String name, String description) {
        Epic epic = new Epic(newId++, name, description);
        epics.put(epic.getId(), epic);
        return epic;
    }

    public void deleteAllEpic() {
        epics.clear();
    }

    public void deleteEpic(int id) {
        Epic epic = epics.remove(id);
        if (epic != null) {
            for (Integer subtaskId : epic.getSubTaskIds().keySet()) {
                subTasks.remove(subtaskId);
            }
        }
    }

    public void updateEpic(Epic updatedEpic) {
        if (epics.containsKey(updatedEpic.getId())) {
            Epic epic = epics.get(updatedEpic.getId());
            epic.setName(updatedEpic.getName());
            epic.setDescription(updatedEpic.getDescription());
        }
    }

    public Epic findEpicById(int id) {
        Epic epic = epics.get(id);
        return epic;
    }

    public ArrayList<Epic> getAllEpics() {
        return new ArrayList<>(epics.values());
    }


}
