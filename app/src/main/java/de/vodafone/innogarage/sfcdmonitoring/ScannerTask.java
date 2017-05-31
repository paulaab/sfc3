package de.vodafone.innogarage.sfcdmonitoring;
import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class ScannerTask extends AsyncTask<ConnectionManager, Void, JSONObject> {




    @Override
    protected JSONObject doInBackground(ConnectionManager... params) {



        ConnectionManager serverConnection = null;
        if(params.length == 1)
            serverConnection = params[0];

        Connection incomingMessages = null;
        List<JSONObject> msgList = null;
        JSONObject actMsg = null;
        //CopyOnWriteArrayList<String> rs = new CopyOnWriteArrayList<>();
        List<Connection> cons = null;

        if(!serverConnection.getConnections().isEmpty()){

            cons = serverConnection.getConnections();


            if(incomingMessages == null){
                incomingMessages = cons.get(0);
            }

            msgList = incomingMessages.getIncomingData();

            if(!msgList.isEmpty()) {

                System.out.print("Received msglist");
                actMsg = msgList.get(0);
                msgList.remove(0);

                if (actMsg.length()==0){
                    try {
                        actMsg.put("Value", "0");
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }
            }
        }
        return actMsg;
    }








}