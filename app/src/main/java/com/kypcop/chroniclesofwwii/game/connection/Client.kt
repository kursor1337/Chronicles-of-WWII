package com.kypcop.chroniclesofwwii.game.connection

import android.app.Activity
import android.util.Log
import com.google.gson.Gson
import com.kypcop.chroniclesofwwii.game.Const
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.io.PrintWriter
import java.net.InetAddress
import java.net.Socket
import java.net.UnknownHostException

class Client(private val mName: String,
             private val mActivity: Activity,
             private val mSendListener: Connection.SendListener,
             private val mReceiveListener: Connection.ReceiveListener,
             private val mListener: Listener){

    interface Listener{
        fun onConnectionEstablished(connection: Connection)
        fun onException(e: Exception)
    }

    fun connectTo(host: Host){
        val inetAddress = host.inetAddress
        val port = host.port
        val name = host.name
        Thread{
            var socket: Socket? = null
            try {
                Log.i("Client", "Before Connection")
                socket = Socket(inetAddress, port)
                Log.i("Client", "After Connection")
                Log.i("Client", "Sent Client Info")
                val input = BufferedReader(InputStreamReader(socket.getInputStream()))
                val output = PrintWriter(socket.getOutputStream())
                output.println(mName)
                output.flush()
                val connection = Connection(mActivity, input, output, host, mSendListener, mReceiveListener)
                Log.i("Client", "Connection established")
                mActivity.runOnUiThread { mListener.onConnectionEstablished(connection) }
                Log.i("Client", "Client shutdown")
            } catch (e: UnknownHostException){
                Log.e("Client", "UnknownHostException")
            } catch (e: Exception) {
                e.printStackTrace()
                Log.e("Client", e::class.java.name)
                Log.e("Client", "Client Error")
            }
            /*
            finally {
                if (socket != null) {
                    try {
                        socket.close()
                    } catch (e: IOException) {
                        Log.e("Client", "Could Not Close Client")
                        e.printStackTrace()
                    }
                }
            }

             */
        }.start()
    }

}