package com.scrubele.scrubeleapp1.activities

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.View
import android.widget.ProgressBar
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.scrubele.scrubeleapp1.R
import com.scrubele.scrubeleapp1.adapters.DataAdapter
import com.scrubele.scrubeleapp1.models.ProtectedObjectModel
import com.scrubele.scrubeleapp1.retrofit.ApiClient
import kotlinx.android.synthetic.main.activity_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class ListActivity : AppCompatActivity() {

    var dataList = ArrayList<ProtectedObjectModel>()
    lateinit var recyclerView: RecyclerView
    private lateinit var progressBar: ProgressBar
    private lateinit var dataAdapter: DataAdapter

    override fun onStart() {
        super.onStart()
        registerReceiver(broadcastReceiver, IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION))
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_list)
        setDataAdapter()
        setLayoutManager()
        setProgressBar()
        swipeContainer.setOnRefreshListener {
            refreshData()
        }
        getData()
    }

    override fun onStop() {
        super.onStop()
        unregisterReceiver(broadcastReceiver)
    }

    private fun setDataAdapter() {
        recyclerView = findViewById(R.id.recyclerView)
        dataAdapter = DataAdapter(dataList)
        dataAdapter.notifyDataSetChanged()
        recyclerView.adapter = dataAdapter
    }

    private fun setLayoutManager() {
        val layoutManager = LinearLayoutManager(recyclerView.context)
        layoutManager.reverseLayout = true
        layoutManager.stackFromEnd = true
        recyclerView.layoutManager = layoutManager
        recyclerView.setHasFixedSize(true)
    }

    private fun setProgressBar() {
        progressBar = findViewById(R.id.progressBar)
        progressBar.visibility = View.VISIBLE
    }

    private fun refreshData() {
        dataList.clear()
        setProgressBar()
        getData()
        swipeContainer.isRefreshing = false
    }

    private fun getData() {
        getProtectedObjects()
        getRobots()
    }

    private fun getProtectedObjects(){
        val call = ApiClient.getClient.getProtectedObjects()
        call.enqueue(object : Callback<List<ProtectedObjectModel>> {

            override fun onResponse(
                call: Call<List<ProtectedObjectModel>>?,
                response: Response<List<ProtectedObjectModel>>?
            ) = changeDataSet(response)

            override fun onFailure(call: Call<List<ProtectedObjectModel>>?, t: Throwable?) {
                progressBar.visibility = View.INVISIBLE
            }
        })
    }

    private fun getRobots() = Unit

    private fun changeDataSet(response: Response<List<ProtectedObjectModel>>?) {
        progressBar.visibility = View.INVISIBLE
        dataList.addAll(response!!.body()!!)
        recyclerView.adapter?.notifyDataSetChanged()
    }

    private var broadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val isDisconnected = intent.getBooleanExtra(
                ConnectivityManager
                    .EXTRA_NO_CONNECTIVITY, false
            )
            when (isDisconnected) {
                true -> launchDisconnectedState()
                false -> launchConnectedState()
            }
        }
    }

    private fun launchDisconnectedState() {
        recyclerView.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
        Toast.makeText(
            recyclerView.context,
            getString(R.string.network_disconnected),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun launchConnectedState() {
        recyclerView.visibility = View.VISIBLE
    }
}