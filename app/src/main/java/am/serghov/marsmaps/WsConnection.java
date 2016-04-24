package am.serghov.marsmaps;

import android.content.Context;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.codebutler.android_websockets.WebSocketClient;

import org.apache.http.message.BasicNameValuePair;

import java.net.URI;
import java.util.Arrays;
import java.util.List;
import java.util.StringTokenizer;
import java.util.TimerTask;

import am.serghov.marsmaps.Network.CheckNetwork;

/**
 * Created by serghov on 4/22/2016.
 */
public class WsConnection implements WebSocketClient.Listener {

    public WebSocketClient wsClient;
    public static WsConnection instace;
    private Context mContext;
    private String phat = "";

    private WsConnection() {
    }

    public static WsConnection getInstance()
    {
        if (instace  == null){
           return instace = new WsConnection();
        }else{
           return instace;
        }
    }

    @Override
    public void onConnect() {
      MapActivity.instance.sendCordinets();
    }

    @Override
    public void onMessage(String message) {
        Log.e("MESSAGE", message);
        if (MessageActivity.instance!=null)
        {
            MessageActivity.instance.addMessage("other: " + message);
        }
    }

    @Override
    public void onMessage(byte[] data) {

    }

    @Override
    public void onDisconnect(int code, String reason) {

    }

    @Override
    public void onError(Exception error) {
        if (!(phat.equals(""))){

            initializeClient(phat);
        }
    }

    public void initializeClient(String phat) {
        // define empty extra headers (needs for WebSocketClient library)
        List<BasicNameValuePair> extraHeaders = Arrays.asList();
        // Initialize WebSocketClient
        wsClient = new WebSocketClient(URI.create(phat), this, extraHeaders);
        // Connect to WebSocket server
        wsClient.connect();
    }

    public void sendSms(String sms,Context mContext){

        if (CheckNetwork.isInternetAvailable(mContext)){
            if (wsClient.isConnected()){

                wsClient.send(sms);
            }

        }else {

            Toast.makeText(mContext,"No Internet Connection", Toast.LENGTH_LONG).show();

        }
    }

}
