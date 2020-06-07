package com.kypcop.chroniclesofwwii.game.Network;

import android.os.Handler;
import android.util.Log;

import com.google.gson.Gson;
import com.kypcop.chroniclesofwwii.game.Engine;
import com.kypcop.chroniclesofwwii.game.Logic.Missions.Mission;
import com.kypcop.chroniclesofwwii.game.Screen.GameScreen;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;

public class WiFiNetwork extends Thread{

    private static final int PORT = 1337;
    private static final int SENT_INFO = 19391945;
    private boolean isServer;
    public static final String SERVER = "isServer";
    public static final String HOST_ADDRESS = "hostAddress";
    public static final Gson GSON = new Gson();

    private Transfer transfer;
    private final Object missionInitWait = new Object();

    private Engine engine;
    private volatile Mission mission;

    private GameScreen gameScreen;





    public void connectEngineAndScreen(Engine engine){
        this.engine = engine;
        this.gameScreen = engine.getGameScreen();
    }

    public void connectScreenToNetwork(GameScreen gameScreen){
        this.gameScreen = gameScreen;
    }

    public void disconnect(){

    }

    public void setServerMission(Mission mission){
        synchronized (missionInitWait) {
            this.mission = mission;
            missionInitWait.notifyAll();
        }

    }


    public void initializeServer(Handler handler){
        isServer = true;
        ServerThread serverThread = new ServerThread(handler);
        serverThread.start();
    }

    public void initializeClient(InetAddress inetAddress){
        isServer = false;
        ClientThread clientThread = new ClientThread(inetAddress);
        clientThread.start();
        Log.i("Kypcop1337", "clientInitialized");
    }

    public void sendMissionInfoToGameScreen(GameScreen gameScreen){
        Log.i("Kypcop1337", "DickBig");
        synchronized(missionInitWait){
            while(mission == null){
                try {
                    missionInitWait.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            Log.i("Kypcop1337", "DickBig2");
            notifyAll();
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
    public void sendIdByNet(int value){
        if(transfer != null){
            transfer.sendId(value);
        }
    }


    class ServerThread extends Thread {

        Socket socket;
        ServerSocket serverSocket;
        Mission mission;
        Handler handler;

        ServerThread(Handler handler){
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
                synchronized (missionInitWait){
                    while(mission == null){
                       missionInitWait.wait();
                    }
                    sendInfoByJson(mission);

                }
            } catch (IOException | InterruptedException exception) {
                exception.printStackTrace();
            }
        }

        private void sendInfoByJson(Mission mission){
            if(isServer){
                String info = GSON.toJson(mission);
                transfer.sendString(info);
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
                synchronized (missionInitWait){
                    while(mission == null){
                        missionInitWait.wait();
                    }

                }
            } catch (IOException | InterruptedException e) {
                e.printStackTrace();
            }
        }
    }


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
            Log.i("Kypcop1337", "TransferInit");
            while (socket != null) {
                try {
                    if(receiveString() == null){
                        int id = dataInputStream.readInt();
                        sendIdToEngine(id);
                    } else{
                        String info = receiveString();
                        mission = GSON.fromJson(info, Mission.class);
                        missionInitWait.notifyAll();
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
                dataOutputStream.writeUTF(string);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        String receiveString(){
            Log.i("Kypcop1337", "receiveString");
            try {
                return dataInputStream.readUTF();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }
    }
}
