package am.serghov.marsmaps.Models;

/**
 * Created by serghov on 4/22/2016.
 */
public class TaskModel {

    private int time;
    private int type;
    private String task;
    private int users_count;

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTask() {
        return task;
    }

    public void setTask(String task) {
        this.task = task;
    }

    public int getUsers_count() {
        return users_count;
    }

    public void setUsers_count(int users_count) {
        this.users_count = users_count;
    }
}
