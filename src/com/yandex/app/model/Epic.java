package com.yandex.app.model;
import java.util.HashMap;
import java.time.Duration;
import java.time.LocalDateTime;
import java.util.Comparator;
import java.util.Objects;
import java.util.List;

public class Epic extends Task {

    private final HashMap<Integer, SubTask> subtasks = new HashMap<>();
    private LocalDateTime endTime;

    public Epic(int id, String name, String description) {
        super(id, name, description);
        this.startTime = null;
        this.duration = null;
        this.endTime = null;
    }

    public HashMap<Integer, SubTask> getSubTaskIds() {
        return subtasks;
    }

    public void addSubtask(SubTask subtask) {
        subtasks.put(subtask.getId(), subtask);
    }

    public void removeSubtask(int subtaskId) {
        subtasks.remove(subtaskId);
    }

    public void updateStatus() {
        if (subtasks.isEmpty()) {
            setStatus(TaskStatus.NEW);
            return;
        }

        boolean allDone = true;
        boolean anyInProgress = false;

        for (SubTask subtask : subtasks.values()) {
            if (subtask.getStatus() != TaskStatus.DONE) allDone = false;
            if (subtask.getStatus() == TaskStatus.IN_PROGRESS) anyInProgress = true;
        }

        if (allDone) {
            setStatus(TaskStatus.DONE);
        } else if (anyInProgress) {
            setStatus(TaskStatus.IN_PROGRESS);
        } else {
            setStatus(TaskStatus.NEW);
        }
    }

    public void updateTime(List<SubTask> subTasks) {
        if (subTasks == null || subTasks.isEmpty()) {
            this.startTime = null;
            this.duration = null;
            this.endTime = null;
        }

        LocalDateTime newStartTime = subTasks.stream()
                .map(SubTask::getStartTime)
                .filter(Objects::nonNull)
                .min(Comparator.naturalOrder())
                .orElse(null);

        LocalDateTime newEndTime = subTasks.stream()
                .map(SubTask::getEndTime)
                .filter(Objects::nonNull)
                .max(Comparator.naturalOrder())
                .orElse(null);

        Duration newDuration = subTasks.stream()
                .map(SubTask::getDuration)
                .filter(Objects::nonNull)
                .reduce(Duration.ZERO, Duration::plus);

        this.startTime = newStartTime;
        this.duration = newDuration;
        this.endTime = newEndTime;

    }

    @Override
    public LocalDateTime getEndTime() {
        return endTime;
    }

    @Override
    public String toString() {
        return id + ","
                + TaskType.EPIC + ","
                + getName() + ","
                + getStatus() + ","
                + getDescription() + ","
                + startTime + ","
                + duration + ","
                + getEndTime();
    }
}