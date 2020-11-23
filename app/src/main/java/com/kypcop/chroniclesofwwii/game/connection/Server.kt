package com.kypcop.chroniclesofwwii.game.connection

import android.app.Activity
import android.os.Message
import android.util.Log
import com.kypcop.chroniclesofwwii.game.Const
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStream
import java.io.PrintWriter
import java.lang.Thread.sleep
import java.net.InetSocketAddress
import java.net.ServerSocket
import java.net.Socket
import java.net.SocketException

class Server(private val mName: String,
             private val mActivity: Activity,
             private val mSendListener: Connection.SendListener,
             private val mReceiveListener: Connection.ReceiveListener,
             private val mServerListener: Listener){

    private val serverSocket = ServerSocket(0) //TODO(dialog onBackPressed)


    interface Listener{
        fun onConnected(connection: Connection)
        fun onServerInfoObtained(hostName: String, port: Int)
        fun onListeningStartError(e: Exception)
    }

    private var waiting = false

    fun startListening(){
        waiting = true
        Thread{
            Log.i("Server", "Thread Started")
            var socket: Socket? = null
            try {
                mActivity.runOnUiThread { mServerListener.onServerInfoObtained(mName, serverSocket.localPort) }
                Log.i("Server", "Listening for connections")
                socket = serverSocket.accept()
                Log.i("Server", "Connection accepted")
                val output = PrintWriter(socket.getOutputStream())
                val input = BufferedReader(InputStreamReader(socket.getInputStream()))
                while(waiting){
                    sleep(50)
                    val name = input.readLine()
                    if(name == null){
                        Log.i("Server", "Client info not yet obtained")
                        continue
                    }
                    val connection = Connection(mActivity, input, output, Host(name, socket.inetAddress, socket.port), mSendListener, mReceiveListener)
                    mActivity.runOnUiThread { mServerListener.onConnected(connection) }
                    Log.i("Server", "Server Shutdown")
                    waiting = false
                    break
                }

            } catch (e: SocketException){
                e.printStackTrace()
            } catch (e: Exception) {
                e.printStackTrace()
                mActivity.runOnUiThread { mServerListener.onListeningStartError(e) }
            } /* finally {
                socket?.close()
            }
            */
        }.start()
    }

    fun stopListening(){
        Log.i("Server", "Stop Listening")
        waiting = false
        serverSocket.close()
    }
}