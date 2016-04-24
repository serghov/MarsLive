package am.serghov.marsmaps;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.menu.ActionMenuItemView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

/**
 * Created by serghov on 4/22/2016.
 */
public class MessageActivity extends Activity {

    private Button btnSend;
    private EditText editMassage;
    private Context mContext;

    public static MessageActivity instance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.message_activity);

        instance = this;

        mContext = this;

        btnSend = (Button)findViewById(R.id.btn_send);
        editMassage = (EditText)findViewById(R.id.edit_massage);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!(editMassage.getText().toString().equals(""))){
                   if (WsConnection.getInstance()!=null && WsConnection.getInstance().wsClient !=null && WsConnection.getInstance().wsClient.isConnected()){
                       WsConnection.getInstance().sendSms(editMassage.getText().toString(),mContext);
                       addMessage("me: " + editMassage.getText().toString());
                       editMassage.setText("");
                   }
                }
            }
        });
    }

    public void addMessage(String message)
    {
        TextView chatContents = (TextView)findViewById(R.id.chatContents);
        chatContents.setText(chatContents.getText()+ System.getProperty("line.separator") + message);
    }

}
