package de.vodafone.innogarage.sfcdmonitoring;

import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by paulabohorquez on 5/16/17.
 */

public class Connection {
    private Socket socket;
    
    private InputStream incomingStream;
    private List<JSONObject> incomingData;
    private boolean close;
    private String name;



    public Connection(Socket socket) {
        name = socket.getInetAddress().getHostName();
        this.socket = socket;
        incomingData = new CopyOnWriteArrayList<>();
        close = false;

        //Get incoming stream and place it in an arraylist
        try {
            incomingStream = socket.getInputStream();
        } catch (IOException e) {
            e.printStackTrace();
        }

        //Start reading incoming stream above in a and convert it to
        // JSON Object that will be accessed after that. *NEW THREAD*
        new InputStreamThread().start();
    }


    private class InputStreamThread extends Thread {
        BufferedReader breader = new BufferedReader(new InputStreamReader(incomingStream));

        public void run() {
            String inmsg = null;
            JSONObject jObj = null;
            while (!close) {
                try {
                    char[] b = new char[1024];
                    int count = breader.read(b, 0, 1024);
                    inmsg = new String(b, 0, count);
                    inmsg = inmsg.substring(8);
                    System.out.println("Incomming message stream received:  " + inmsg);
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (inmsg != null) {
                    try {
                        jObj = new JSONObject(inmsg);

                    } catch (JSONException e) {
                        e.printStackTrace();
                        Log.e("InputStreamThread: ", "Could not save Json Object with incoming stream");
                    }
                    incomingData.add(jObj);
                    Log.e("Connection: ", socket.getInetAddress() + " Message received: " + jObj.toString() + " => Placed in incomingData, parsed as JSON");


                }
                try {
                    Thread.sleep(10);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }

        }
    }

    public String getName() {return name;}
    public List<JSONObject> getIncomingData(){return incomingData;}

}
