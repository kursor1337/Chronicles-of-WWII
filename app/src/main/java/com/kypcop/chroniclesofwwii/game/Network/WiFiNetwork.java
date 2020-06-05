package com.kypcop.chroniclesofwwii.game.Network;

import android.os.Handler;
import android.os.Message;

import com.google.gson.Gson;
import com.kypcop.chroniclesofwwii.game.Engine;
import com.kypcop.chroniclesofwwii.game.Logic.Missions.Mission;
import com.kypcop.chroniclesofwwii.game.Screen.GameScreen;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class WiFiNetwork extends Thread{

    private static final int PORT = 1337;
    private static final int SENT_INFO = 19391945;
    private boolean isServer;
    public static final String SERVER = "isServer";
    public static final String HOST_ADDRESS = "hostAddress";
    public static final Gson GSON = new Gson();

    private ServerThread serverThread = null;
    private ClientThread clientThread = null;
    private Transfer transfer;

    private Engine engine;
    private GameScreen gameScreen;
    Mission mission;




    public void connectEngine(Engine engine){
        this.engine = engine;
    }


    public void initializeServer(Mission mission, Handler handler){
        isServer = true;
        serverThread = new ServerThread(mission, handler);
        serverThread.start();
    }

    public void initializeClient(InetAddress inetAddress){
        isServer = false;
        clientThread = new ClientThread(inetAddress);
        clientThread.start();
    }

    private void sendInfoByJson(Mission mission){
        if(isServer){
            String info = GSON.toJson(mission);
            transfer.sendString(info);
        }
    }

    public void sendMissionInfoToGameScreen(GameScreen gameScreen){
        synchronized(mission){
            while(mission == null){
                try {
                    mission.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            gameScreen.receiveMissionInfo(mission);
        }
    }

    /**
     * This method lets engine know about received id
     * @param id
     */
    private void sendIdToEngine(int id){
        if(engine != null){
            engine.receiveId(id);
        }
    }


    /**
     * method for sending id to another player
     * @param value
     */
    public void sendId(int value){
        if(transfer != null){
            transfer.sendId(value);
        }
    }


    class ServerThread extends Thread {

        Socket socket;
        ServerSocket serverSocket;
        Mission mission;
        Handler handler;

        ServerThread(Mission mission, Handler handler){
            this.mission = mission;
            this.handler = handler;
        }

        @Override
        public void run() {
            try {
                serverSocket = new ServerSocket(PORT);
                socket = serverSocket.accept();
                handler.obtainMessage(1);
                transfer = new Transfer(socket);
                transfer.start();
                sendInfoByJson(mission);
            } catch (IOException exception) {
                exception.printStackTrace();
            }
        }

    }


    class ClientThread extends Thread {

        Socket socket;
        String hostAddress;

        ClientThread(InetAddress hostAddress) {
            this.hostAddress = hostAddress.getHostAddress();
            socket = new Socket();
        }

        @Override
        public void run() {
            try {
                socket.connect(new InetSocketAddress(hostAddress, PORT), 5000);
                transfer = new Transfer(socket);
                transfer.start();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }



    Handler handler = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            if(msg.what == SENT_INFO){
                String info = (String) msg.obj;
                sendInfoUp(info);
            } else{
                sendIdToEngine(msg.what);
            }
            return false;
        }

        private void sendInfoUp(String info) {
            mission = GSON.fromJson(info, Mission.class);
        }
    });


    class Transfer extends Thread {

        DataInputStream dataInputStream;
        DataOutputStream dataOutputStream;
        Socket socket;

        Transfer(Socket socket){
            this.socket = socket;
            try {
                dataInputStream = new DataInputStream(socket.getInputStream());
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @Override
        public void run() {
            while (socket != null) {
                try {
                    if(receiveString() == null){
                        int value = dataInputStream.readInt();
                        handler.obtainMessage(value).sendToTarget();
                    } else{
                        String info = receiveString();
                        synchronized (mission){
                            mission = GSON.fromJson(info, Mission.class);
                            mission.notifyAll();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        void sendId(int value){
            try{
                dataOutputStream.writeInt(value);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        void sendString(String string){
            try {
                BufferedWriter out = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream(), StandardCharsets.UTF_8));
                out.write(string);
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String receiveString(){
            try {
                BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream(), StandardCharsets.UTF_8));
                String string = in.readLine();
                in.close();
                return string;
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
