package am.serghov.marsmaps;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

import am.serghov.marsmaps.Models.TaskModel;

/**
 * Created by serghov on 4/22/2016.
 */
public class TaskListAdapter extends ArrayAdapter<TaskModel> {

   private ArrayList<TaskModel> taskModels;

    public TaskListAdapter(Context context, ArrayList<TaskModel> taskModels) {
        super(context, 0, taskModels);
    }

    public void setAll(ArrayList<TaskModel> taskModels){
        this.taskModels = taskModels;
    }

    @Override
    public int getCount(){
        if (taskModels == null){
            return 0;
        }else return taskModels.size();
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
//        TaskModel taskModel = getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.item_task, parent, false);
        }
        // Lookup view for data population
        TextView taskName = (TextView) convertView.findViewById(R.id.task_name);
        // Populate the data into the template view using the data object

        taskName.setText(taskModels.get(position).getTask());
        // Return the completed view to render on screen
        return convertView;
    }

}
