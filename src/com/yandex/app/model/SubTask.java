package com.yandex.app.model;

public class SubTask extends Task {

    private int epicId;

    public SubTask(int id, String name, String description, int epicId) {
        super(id, name, description);
        this.epicId = epicId;
    }

    public int getEpicId() {
        return epicId;
    }

    public void setEpicId(int epicId) {
        this.epicId = epicId;
    }

    @Override
    public String toString() {
        return id + ","
                + TaskType.SUBTASK + ","
                + getName() + ","
                + getStatus() + ","
                + getDescription() + ","
                + epicId;
    }
}
