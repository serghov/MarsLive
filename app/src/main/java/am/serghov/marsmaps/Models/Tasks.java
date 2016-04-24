package am.serghov.marsmaps.Models;

import android.widget.ArrayAdapter;

import java.util.ArrayList;

/**
 * Created by serghov on 4/22/2016.
 */
public class Tasks {

    private boolean error;

    private ArrayList<TaskModel> tasks;
    private int users_count;

    public boolean isError() {
        return error;
    }

    public void setError(boolean error) {
        this.error = error;
    }

    public ArrayList<TaskModel> getTasks() {
        return tasks;
    }
    public void setTasks(ArrayList<TaskModel> tasks) {
        this.tasks = tasks;
    }

    public int getUsers_count() {
        return users_count;
    }

    public void setUsers_count(int users_count) {
        this.users_count = users_count;
    }
}
