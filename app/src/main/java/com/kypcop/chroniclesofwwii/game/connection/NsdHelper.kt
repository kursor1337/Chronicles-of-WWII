package com.kypcop.chroniclesofwwii.game.connection

import android.app.Activity
import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdManager.*
import android.net.nsd.NsdServiceInfo
import android.util.Log


class NsdHelper private constructor(private val mActivity: Activity,
                                    private val mUsage: Usage,
                                    private val mDiscoveryListener: DiscoveryListener,
                                    private val mBroadcastListener: BroadcastListener) {

    constructor(activity: Activity, discoveryListener: DiscoveryListener) : this(activity, Usage.DISCOVERY, discoveryListener,
    object : BroadcastListener{
        override fun onServiceRegistered(serviceInfo: NsdServiceInfo) {}
        override fun onRegistrationFailed(arg0: NsdServiceInfo, arg1: Int) {}
    }){
        Log.i(TAG, "Initialization. Type: ${Usage.DISCOVERY}")
    }

    constructor(activity: Activity, broadcastListener: BroadcastListener) : this(activity, Usage.BROADCAST,
    object : DiscoveryListener {
        override fun onHostFound(host: Host) { }
        override fun onHostLost(name: String) { }
        override fun onDiscoveryFailed(errorCode: Int) { }
        override fun onResolveFailed(errorCode: Int) { }
    }, broadcastListener){
        Log.i(TAG, "Initialization. Type: ${Usage.BROADCAST}")
    }

    interface DiscoveryListener{
        fun onHostFound(host: Host)
        fun onHostLost(name: String)
        fun onDiscoveryFailed(errorCode: Int)
        fun onResolveFailed(errorCode: Int)
    }

    interface BroadcastListener{
        fun onServiceRegistered(serviceInfo: NsdServiceInfo)
        fun onRegistrationFailed(arg0: NsdServiceInfo, arg1: Int)
    }

    enum class Usage{
        DISCOVERY, BROADCAST
    }
    companion object {
        const val SERVICE_NAME = "ChroniclesOfWWII"
        const val SERVICE_TYPE = "_http._tcp."
        const val TAG = "NsdHelper"
    }

    private val mNsdManager: NsdManager = mActivity.getSystemService(Context.NSD_SERVICE) as NsdManager

    private val mNsdDiscoveryListener = object : NsdManager.DiscoveryListener {
        override fun onDiscoveryStarted(regType: String) {
            Log.d(TAG, "Service discovery started")
        }
        override fun onServiceFound(serviceInfo: NsdServiceInfo) {
            Log.d(TAG, "Service discovery success: $serviceInfo")
            when {
                serviceInfo.serviceType != SERVICE_TYPE -> {
                    Log.d(TAG, "Unknown Service Type: ${serviceInfo.serviceType}")
                }
                serviceInfo.serviceName == mServiceName -> {
                    Log.d(TAG, "Same machine: $mServiceName")
                }
                serviceInfo.serviceName.contains(SERVICE_NAME) -> {
                    mNsdManager.resolveService(serviceInfo, object : ResolveListener {
                        override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                            Log.e(TAG, "Resolve failed $errorCode")
                            mActivity.runOnUiThread { mDiscoveryListener.onResolveFailed(errorCode) }
                        }

                        override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                            Log.e(TAG, "Resolve Succeeded. $serviceInfo")
                            if (serviceInfo.serviceName == mServiceName) {
                                Log.d(TAG, "Same IP.")
                                return
                            }
                            mActivity.runOnUiThread { mDiscoveryListener.onHostFound(Host(serviceInfo)) }
                        }
                    })
                }
            }
        }
        override fun onServiceLost(serviceInfo: NsdServiceInfo) {
            Log.e(TAG, "service lost $serviceInfo")
            mActivity.runOnUiThread { mDiscoveryListener.onHostLost(Host.remakeName(serviceInfo)) } //TODO(Peer list remove)
        }
        override fun onDiscoveryStopped(serviceType: String) {
            Log.i(TAG, "Discovery stopped: $serviceType")
        }

        override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e(TAG, "Discovery failed: Error code: $errorCode")
            mNsdManager.stopServiceDiscovery(this)
            mActivity.runOnUiThread { mDiscoveryListener.onDiscoveryFailed(errorCode) }
        }

        override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
            Log.e(TAG, "Discovery failed: Error code: $errorCode")
            mNsdManager.stopServiceDiscovery(this)
            mActivity.runOnUiThread { mDiscoveryListener.onDiscoveryFailed(errorCode) }
        }
    }
    private val mNsdRegistrationListener = object : RegistrationListener {
        override fun onServiceRegistered(serviceInfo: NsdServiceInfo) {
            Log.i(TAG, "onServiceRegistered: ${serviceInfo.port}; " +
                    "${serviceInfo.serviceName}; ${serviceInfo.serviceType} ")
            mServiceName = serviceInfo.serviceName
            mActivity.runOnUiThread { mBroadcastListener.onServiceRegistered(serviceInfo) }
        }
        override fun onRegistrationFailed(arg0: NsdServiceInfo, arg1: Int) {
            mActivity.runOnUiThread { mBroadcastListener.onRegistrationFailed(arg0, arg1) }
        }
        override fun onServiceUnregistered(arg0: NsdServiceInfo) {}
        override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {}
    }

    private var mServiceName = SERVICE_NAME



    fun registerService(name: String, localPort: Int) {
        val serviceInfo = NsdServiceInfo().apply {
            port = localPort
            serviceName = "$mServiceName.$name"
            serviceType = SERVICE_TYPE
            Log.i(TAG, "Service registered: $port; $serviceName; $serviceType ")
        }
        mNsdManager.registerService(serviceInfo, PROTOCOL_DNS_SD, mNsdRegistrationListener)
    }

    fun unregisterService(){
        Log.i(TAG, "Service unregistered")
        mServiceName = SERVICE_NAME
        try{
            mNsdManager.unregisterService(mNsdRegistrationListener)
        } catch (e: Exception) {
            Log.i(TAG, e.message)
        }
    }

    fun startDiscovery() {
        Log.i(TAG, "Start discovery")
        mNsdManager.discoverServices(SERVICE_TYPE, PROTOCOL_DNS_SD, mNsdDiscoveryListener)
    }

    fun stopDiscovery() {
        Log.i(TAG, "Stop discovery")
        try{
            mNsdManager.stopServiceDiscovery(mNsdDiscoveryListener)
        } catch (e: Exception) {
            Log.i(TAG, e.message)
        }
    }

    fun shutdown() {
        when(mUsage){
            Usage.BROADCAST -> {
                unregisterService()
            }
            Usage.DISCOVERY -> {
                stopDiscovery()
            }

        }
        Log.i(TAG, "Shutdown")
    }
}

