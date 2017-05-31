package de.vodafone.innogarage.sfcdmonitoring;

import android.content.Context;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import javax.xml.transform.Result;

public class MainActivity extends AppCompatActivity {
    //Define socket variables (ports)
    public static boolean debugMode = true;
    public static int socketPortForBroadcast = 45555;
    public static int socketServerPortForSFCD = 45556;
    //Define variables for connection
    ConnectionManager conMan = new ConnectionManager();
    List<Connection> cons = conMan.getConnections();
    TimerTask timerTask;
    Timer timer = new Timer();


    private Handler handler = new Handler();
    public ListView msgView;
    public ArrayAdapter<String> msgList;
    public ArrayList<String> list;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //globalContext = this;
        /*---------------List of messages initialization - MSG-----------------*/
        msgView = (ListView) findViewById(R.id.lv1);
        msgList = new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1);
        msgView.setAdapter(msgList);

        final Button button = (Button) findViewById(R.id.buttonInvitation);
        button.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v){

                conMan.sendInvitation();
            }
        });



        // TODO: check if this works, if so remove it
        // does not work properly with the given software structure
        // new Inviter().start();

        timerTask = new TimerTask() {

            public void run() {
                new ScannerTask() {

                    protected void onPostExecute(JSONObject result) {

                        if (!cons.isEmpty()&&result!=null) {

                            //ListView listDevices = (ListView) findViewById(R.id.mSFCDList);
                            //listDevices.setAdapter(new ListViewAdapter(globalContext, serverConnection));


                            try {
                                setValues(result);
                            }
                            catch (JSONException e){
                                System.out.println("Could not set values for JSON Object");
                            }

                        }
                    }
                }.execute(conMan);
            }
        };
        timer.schedule(timerTask, 0, 200);

    }
    /*------------Methods for displaying the data on the list--------------*/
    public void setValues(JSONObject jobj) throws JSONException {
        //Integer tag = (Integer) marker.getTag();
        msgList.clear();
        //JSONObject dataset = jobj.getJSONObject(String.valueOf(tag));

        JSONObject dataset = jobj;
        Iterator<?> keys = dataset.keys();
        list = new ArrayList<>();
        while(keys.hasNext()){


            String mKey = (String)keys.next();

            switch (mKey){
                case "IntraFreq":
                    list.add("IntraFreq   >>");
                    break;
                case "InterFreq":
                    list.add("InterFreq   >>");
                    break;
                case "PCC RxD RSSI":
                    list.add("PCC RxD RSSI   >>");

                    break;
                case "PCC RxM RSSI":
                    list.add("PCC RxM RSSI   >>");
                    break;
                case "Serving":
                    list.add("Serving   >>");
                    break;
                case "sats":
                    list.add("Sats   >>");
                    break;
                case "latitude":
                    list.add("Latitude: "+dataset.getString(mKey));
                    break;
                case "longitude":
                    list.add("Longitude: "+dataset.getString(mKey));
                    break;
                case "altitude m":
                    list.add("Altitude: "+dataset.getString(mKey));
                    break;
                default:
                    list.add(mKey.substring(0,1).toUpperCase()+mKey.substring(1) + ": "+dataset.getString(mKey));
                    break;
            }
        }
        Collections.sort(list);
        System.out.println (list);
        msgList = new ArrayAdapter<String>(this,android.R.layout.simple_list_item_1, list);
        displayMsg();
    }

    public void displayMsg() {

        //final String data = msg;

        handler.post(new Runnable() {
            @Override
            public void run() {

                // msgList.add(data);
                msgView.setAdapter(msgList);
                msgView.smoothScrollToPosition(msgList.getCount() - 1);
            }
        });
    }







}
