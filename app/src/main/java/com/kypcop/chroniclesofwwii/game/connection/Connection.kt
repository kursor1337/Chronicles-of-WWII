package com.kypcop.chroniclesofwwii.game.connection

import android.app.Activity
import android.util.Log
import android.widget.Toast
import com.kypcop.chroniclesofwwii.game.Const
import java.io.*
import java.net.ServerSocket
import java.net.Socket
import java.util.concurrent.ArrayBlockingQueue
import java.util.concurrent.BlockingQueue

open class Connection(
        private val mActivity: Activity,
        input: BufferedReader,
        output: PrintWriter,
        val host: Host,
        private val mSendListener: SendListener,
        receiveListener: ReceiveListener) {

    var mReceiveListener = receiveListener

    private val mSender = Sender(output)
    private val mReceiver = Receiver(input)

    init {
        Log.i("Connection", "Init connection to ${host.name}; ${host.inetAddress}; ${host.port}")
        mReceiver.startReceiving()
    }

    interface SendListener{
        fun onSendSuccess()
        fun onSendFailure(e: Exception)
    }

    interface ReceiveListener{
        fun onReceive(string: String)
    }

    fun send(string: String){
        mSender.send(string)
    }

    fun dispose(){
        Log.i("Connection", "Disposing")
        mReceiver.dispose()
        mSender.stopSending()
        Log.i("Connection", "Disposed")
    }

    inner class Sender(private val output: PrintWriter){

        val mMessageQueue: BlockingQueue<String> = ArrayBlockingQueue(10)
        private var sending = true
        init {
            SenderThread().start()
        }

        fun send(string: String){
            mMessageQueue.put(string)
        }

        private inner class SenderThread : Thread(){

            override fun run() {
                while (sending) {
                    try {
                        val msg = mMessageQueue.take()
                        sendMessage(msg)
                    } catch (ie: InterruptedException) {
                        Log.d("Sender", "Message sending loop interrupted, exiting")
                    }
                }
            }

            fun sendMessage(string: String){
                try {
                    Log.e("Sender", "Connected, Sending: $string")
                    output.println(string)
                    output.flush()
                    mActivity.runOnUiThread { mSendListener.onSendSuccess() }
                    Log.e("Sender", "Send Successful: $string")
                } catch (e: Exception) {
                    e.printStackTrace()
                    Log.i("Sender", "_____")
                    mActivity.runOnUiThread{
                        Toast.makeText(mActivity, "Message not sent", Toast.LENGTH_SHORT).show()
                        mSendListener.onSendFailure(e)
                    }
                    e.printStackTrace()
                }
            }
        }

        fun stopSending(){
            Log.i("Sender", "Stopping")
            sending = false
        }
    }

    inner class Receiver(private val input: BufferedReader) {

        private val receiverThread = ReceiverThread()

        fun startReceiving(){
            receiverThread.start()
        }

        private var receiving = true

        private inner class ReceiverThread : Thread() {
            override fun run() {
                try {
                    while (receiving) {
                        sleep(100)
                        val string = input.readLine() ?: continue
                        Log.e("Receiver", "RECEIVED ==> $string")
                        mActivity.runOnUiThread { mReceiveListener.onReceive(string) }
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }

        fun dispose() {
            Log.i("Receiver", "Disposing")
            try {
                receiving = false
                input.close()
                Log.i("Receiver", "Thread stopped")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}