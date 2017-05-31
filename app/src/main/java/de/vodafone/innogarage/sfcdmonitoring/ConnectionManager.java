package de.vodafone.innogarage.sfcdmonitoring;

import android.util.Log;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * Created by paulabohorquez on 5/16/17.
 */

public class ConnectionManager {
    private ServerSocket serverSocketForSFCD = null;
    private List<Connection> connections;


    public ConnectionManager(){
        connections = new CopyOnWriteArrayList<>();
        //Create Socket for receiving SFCD Data
        try {
            serverSocketForSFCD = new ServerSocket(MainActivity.socketServerPortForSFCD);
            Log.e("ConnectionManager "," Constructor SFCD : Server socker successfuly created");

        } catch (IOException e) {
            e.printStackTrace();
            Log.e("ConnectionManager "," Constructor SFCD : Could not create Socket");
        }
        //Start listening TCP messages - *New Thread*
        new ConnectionListenerForSFCD().start();
    }


    public void sendInvitation(){
        new Broadcaster().start();
    }

    /**
     * Thread Class
     * Akzeptiert eingehende TCP Verbindungen und sichert diese Verbindung
     * in einer ConnectionListe
     *
     * @author Steffen.Ryll
     */
    private class ConnectionListenerForSFCD extends Thread {

        public void run() {

            while (true) {

                Socket socket = null;

                try {
                    socket = serverSocketForSFCD.accept();
                    Log.e("ConnectionManager "," ConListener :Waiting for connections!");

                } catch (IOException e) {
                    e.printStackTrace();
                }

                connections.add(new Connection(socket));
                Log.e("ConnectionManager "," ConListener : New SFCD added. IP= " + socket.getInetAddress().toString());

            }
        }

    }


    public List<Connection> getConnections() {

        return connections;
    }

}




