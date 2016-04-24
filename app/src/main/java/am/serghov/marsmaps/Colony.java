package am.serghov.marsmaps;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ExpandedMenuView;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;

import am.serghov.marsmaps.Date.MarsDate;
import am.serghov.marsmaps.Models.TaskModel;
import am.serghov.marsmaps.Models.Tasks;
import am.serghov.marsmaps.Network.CheckNetwork;
import am.serghov.marsmaps.Utils.Constants;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class Colony extends Activity {

    private OkHttpClient client = new OkHttpClient();
    private ListView listTask;
    private TaskListAdapter taskListAdapter;
    private Context mContext;
    private Tasks result;
    private RelativeLayout relProgress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.colony_activity);
        mContext = this;
        listTask = (ListView) findViewById(R.id.list_task);

        taskListAdapter = new TaskListAdapter(mContext, new ArrayList<TaskModel>());
        listTask.setAdapter(taskListAdapter);
        relProgress = (RelativeLayout) findViewById(R.id.rel_progress);
        FloatingActionButton massageFab = (FloatingActionButton) findViewById(R.id.fab_massage);

        massageFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext, MessageActivity.class);
                startActivity(intent);
            }
        });

        if (CheckNetwork.isInternetAvailable(mContext)) {
            new AsyncTaskRunner().execute();
        } else {
            Toast.makeText(mContext, "No internet connection", Toast.LENGTH_LONG).show();
        }

        listTask.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                result.getTasks().get(position).getTime();
                result.getTasks().get(position).getTask();
                result.getTasks().get(position).getType();
                buildAlertMessageTask1(result.getTasks().get(position).getTask(), result.getTasks().get(position).getType());
            }
        });


        if (MapActivity.instance!= null && MapActivity.instance.colony != null)
            ((TextView) (findViewById(R.id.colony_name))).setText(MapActivity.instance.colony.getName());


        Calendar cal = Calendar.getInstance();
        MarsDate curDate = new MarsDate(cal.getTime());

        ((TextView) (findViewById(R.id.txt_data2))).setText("Year " + (int) curDate.getMartianYear() + ", Month " + (int) curDate.getMartianMonth() + ", Sol " + (int) (curDate.getMartianSol() * 10) / 10.0);
    }

    private void buildAlertMessageTask1(final String message, int type) {

        LayoutInflater layoutInflater = LayoutInflater.from(mContext);
        View promptView = layoutInflater.inflate(R.layout.input_dialog, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(mContext);
        alertDialogBuilder.setView(promptView);
        final EditText editText = (EditText) promptView.findViewById(R.id.edit_name);

        if (type == 2) {
            editText.setVisibility(View.GONE);
        }

        final TextView text = (TextView) promptView.findViewById(R.id.text_enter_name);
        text.setText(message);

        // setup a dialog window
        alertDialogBuilder.setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        Toast.makeText(mContext, "Teask Completed", Toast.LENGTH_LONG).show();
                    }
                })
                .setNegativeButton("Cancel",
                        new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                dialog.cancel();
                            }
                        });

        // create an alert dialog
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();

    }


    private Tasks getTasks(String url) throws IOException {
        Request request = new Request.Builder()
                .url(url)
                .build();

        Response response = client.newCall(request).execute();
        return new Gson().fromJson(response.body().string(), Tasks.class);
    }

    private class AsyncTaskRunner extends AsyncTask<String, String, Tasks> {

        @Override
        protected Tasks doInBackground(String... params) {

            Tasks tasks = null;
            try {
                tasks = getTasks(Constants.TASKS_URL + "?token=" + Constants.getToken());
            } catch (IOException e) {
                e.printStackTrace();
            }
            return tasks;
        }

        @Override
        protected void onPostExecute(Tasks result) {
            // execution of result of Long time consuming operation
            relProgress.setVisibility(View.GONE);
            if (!result.isError()) {
                Colony.this.result = result;
                taskListAdapter.setAll(result.getTasks());
                taskListAdapter.notifyDataSetChanged();
                try {
                    ((TextView) (findViewById(R.id.txt_data1))).setText(result.getUsers_count() + " inhabitants");
                } catch (Exception e)
                {}
            }
        }

        @Override
        protected void onPreExecute() {
            // Things to be done before execution of long running operation. For
            // example showing ProgessDialog
            relProgress.setVisibility(View.VISIBLE);
        }

        @Override
        protected void onProgressUpdate(String... text) {

        }
    }


}
