package com.kypcop.chroniclesofwwii.game

import android.util.Log
import com.kypcop.chroniclesofwwii.game.connection.Connection
import java.net.*
import java.util.*

object Const {

    object game{
        const val MISSION_INFO = "MISSION_INFO"
        const val MISSION = "mission"
        const val MULTIPLAYER_GAME_MODE = "mode"
        const val BOARD_SIZE = 8
    }

    object connection{

        const val DEFAULT_PORT = 1337
        /** Constants for socket communication */
        const val REQUEST_FOR_ACCEPT = "request_for_accept"
        const val REQUEST_MISSION_INFO = "request_mission_info"
        const val REJECTED = "rejected"
        const val ACCEPTED = "accepted"
        const val CANCEL_CONNECTION = "cancel_connection"
        const val NOT_RECEIVED = "not received"
        const val INVALID_JSON = "invalid json"

        /** Constants for Activity */
        const val CONNECTED_DEVICE = "connected_device"
        const val HOST = "host"

        const val CLIENT = "Client"
        const val SERVER = "Server"

        @JvmStatic
        fun validIP(ip: String?): Boolean {
            return if (ip == null || ip.length < 7 || ip.length > 15) false else try {
                var x = 0
                var y = ip.indexOf('.')
                if (y == -1 || ip[x] == '-' || ip.substring(x, y).toInt() > 255) return false
                x = ip.indexOf('.', ++y)
                if (x == -1 || ip[y] == '-' || ip.substring(y, x).toInt() > 255) return false
                y = ip.indexOf('.', ++x)
                !(y == -1 || ip[x] == '-' || ip.substring(x, y).toInt() > 255 || ip[++y] == '-' || ip.substring(y, ip.length).toInt() > 255 || ip[ip.length - 1] == '.')
            } catch (e: Exception) {
                false
            }
        }

        @JvmStatic
        fun getSelfIpAddress(): String {
            var selfIp = ""
            try {
                val enumNetworkInterfaces: Enumeration<NetworkInterface> = NetworkInterface.getNetworkInterfaces()
                while (enumNetworkInterfaces.hasMoreElements()) {
                    val networkInterface: NetworkInterface = enumNetworkInterfaces.nextElement()
                    val enumInetAddress: Enumeration<InetAddress> = networkInterface.inetAddresses
                    while (enumInetAddress.hasMoreElements()) {
                        val inetAddress: InetAddress = enumInetAddress
                                .nextElement()
                        if (inetAddress.isSiteLocalAddress) {
                            selfIp = inetAddress.hostAddress
                        }
                    }
                }
            } catch (e: SocketException) {
                e.printStackTrace()
                Log.e("GET_IP", "IP NOT FOUND")
            }
            return selfIp
        }

        @JvmStatic
        var currentConnection: Connection? = null


    }




}