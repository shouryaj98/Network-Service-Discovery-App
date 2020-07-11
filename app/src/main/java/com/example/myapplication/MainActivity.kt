package com.example.myapplication

import android.os.Bundle
import android.os.Handler
import android.provider.Settings
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.*
import kotlin.collections.ArrayList

class MainActivity : AppCompatActivity() {

    private lateinit var mNsdHelper : NsdHelper
    private val TAG = "MainActivity"

    private lateinit var recyclerView: RecyclerView
    private lateinit var viewAdapter: RecyclerView.Adapter<*>
    private lateinit var viewManager: RecyclerView.LayoutManager
    val serviceList = ArrayList<ServiceModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewManager = LinearLayoutManager(this)
        viewAdapter = CustomAdapter(serviceList)
        mNsdHelper = NsdHelper(this, Settings.Secure.getString(contentResolver, "bluetooth_name"), serviceList)

        recyclerView = findViewById<RecyclerView>(R.id.recycler_view).apply {
            setHasFixedSize(true)
            layoutManager = viewManager
            adapter = viewAdapter
        }

    }


    fun clickPublish(view: View) {
        Log.d(TAG, "Publish button clicked")
        mNsdHelper.registerService(80)
    }

    fun clickScan(view: View) {
        Log.d(TAG, "Scan button clicked")
        serviceList.clear()
        mNsdHelper.discoverServices()

        val updateHandler = Handler()
        val runnable = Runnable {
            mNsdHelper.stopDiscovery()
            viewAdapter.notifyDataSetChanged()
        }
        updateHandler.postDelayed(runnable, 1500)
    }


    override fun onPause() {
        mNsdHelper.tearDown()
        super.onPause()
    }

    override fun onDestroy() {
        mNsdHelper.tearDown()
        super.onDestroy()
    }

}