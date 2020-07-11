package com.example.myapplication

import android.content.Context
import android.net.nsd.NsdManager
import android.net.nsd.NsdServiceInfo
import android.util.Log
import androidx.recyclerview.widget.RecyclerView

class NsdHelper(
    mContext: Context,
    var mServiceName: String,
    val serviceList: ArrayList<ServiceModel>
) {

    private val mNsdManager : NsdManager = mContext.getSystemService(Context.NSD_SERVICE) as NsdManager
    private val TAG: String = "NsdHelper"
    private val SERVICE_TYPE = "_http._tcp."

    private var mRegistrationListener : NsdManager.RegistrationListener? = null
    private var mDiscoveryListener : NsdManager.DiscoveryListener? = null
    private var mResolveListener : NsdManager.ResolveListener? = null

    fun registerService(port: Int) {
        tearDown()
        initializeRegistrationListener()
        val serviceInfo = NsdServiceInfo().apply {
            serviceName = mServiceName
            serviceType = SERVICE_TYPE
            setPort(port)
        }
        mNsdManager.registerService(serviceInfo, NsdManager.PROTOCOL_DNS_SD, mRegistrationListener)
    }

    fun discoverServices(){
        stopDiscovery()
        initializeDiscoveryListener()
        initializeResolveListener()
        mNsdManager.discoverServices(SERVICE_TYPE, NsdManager.PROTOCOL_DNS_SD, mDiscoveryListener)
    }

    private fun initializeRegistrationListener() {
        mRegistrationListener = object : NsdManager.RegistrationListener {

            override fun onServiceRegistered(NsdServiceInfo: NsdServiceInfo) {
                // Save the service name. Android may have changed it in order to
                // resolve a conflict, so update the name you initially requested
                // with the name Android actually used.
                mServiceName = NsdServiceInfo.serviceName
                //Toast.makeText(applicationContext, "registered $mServiceName", Toast.LENGTH_LONG).show()
                Log.d(TAG, "Service $mServiceName registered")
            }

            override fun onRegistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                // Registration failed! Put debugging code here to determine why.
                //Toast.makeText(applicationContext,"failed",Toast.LENGTH_SHORT).show()
                Log.e(TAG, "Service ${serviceInfo.serviceName} registration failed")
            }

            override fun onServiceUnregistered(arg0: NsdServiceInfo) {
                // Service has been unregistered. This only happens when you call
                // NsdManager.unregisterService() and pass in this listener.
                Log.d(TAG, "Service ${arg0.serviceName} unregistered")
            }

            override fun onUnregistrationFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                // Unregistration failed. Put debugging code here to determine why.
                Log.e(TAG, "Service ${serviceInfo.serviceName} unregistering failed")
            }
        }
    }

    private fun initializeDiscoveryListener() {
        mDiscoveryListener = object : NsdManager.DiscoveryListener {

            // Called as soon as service discovery begins.
            override fun onDiscoveryStarted(regType: String) {
                Log.d(TAG, "Service discovery started")
            }

            override fun onServiceFound(service: NsdServiceInfo) {
                // A service was found! Do something with it.
                Log.d(TAG, "Service discovery success $service")
                when {
                    service.serviceType != SERVICE_TYPE -> // Service type is the string containing the protocol and transport layer for this service.
                        Log.d(TAG, "Unknown Service Type: ${service.serviceType}")
                    service.serviceName == mServiceName -> // The name of the service tells the user what they'd be connecting to.
                        Log.d(TAG, "Same machine: $mServiceName")

                    service.serviceName != mServiceName -> mNsdManager.resolveService(service, mResolveListener)
                }
            }

            override fun onServiceLost(service: NsdServiceInfo) {
                // When the network service is no longer available.
                // Internal bookkeeping code goes here.
                Log.e(TAG, "service lost: $service")
            }

            override fun onDiscoveryStopped(serviceType: String) {
                Log.d(TAG, "Discovery stopped: $serviceType")
            }

            override fun onStartDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e(TAG, "Discovery failed: Error code:$errorCode")
                mNsdManager.stopServiceDiscovery(this)
            }

            override fun onStopDiscoveryFailed(serviceType: String, errorCode: Int) {
                Log.e(TAG, "Discovery failed: Error code:$errorCode")
                mNsdManager.stopServiceDiscovery(this)
            }
        }
    }

    private fun initializeResolveListener(){
            mResolveListener = object : NsdManager.ResolveListener {

            override fun onResolveFailed(serviceInfo: NsdServiceInfo, errorCode: Int) {
                // Called when the resolve fails. Use the error code to debug.
                Log.e(TAG, "Resolve failed: $errorCode")
            }

            override fun onServiceResolved(serviceInfo: NsdServiceInfo) {
                Log.e(TAG, "Resolve Succeeded. $serviceInfo")

                if (serviceInfo.serviceName == mServiceName) {
                    Log.d(TAG, "Same IP.")
                    return
                }

                serviceList.add(ServiceModel(serviceInfo.serviceName, serviceInfo.serviceType, serviceInfo.host.toString(), serviceInfo.port.toString()))
            }
        }
    }



    fun stopDiscovery() {
        if (mDiscoveryListener != null) {
            try {
                mNsdManager.stopServiceDiscovery(mDiscoveryListener);
            } finally {
            }
            mDiscoveryListener = null;
        }
    }


    fun tearDown() {
        if (mRegistrationListener != null){
            try {
                mNsdManager.unregisterService(mRegistrationListener)
            }finally {
            }
            mRegistrationListener = null
        }
    }
}

