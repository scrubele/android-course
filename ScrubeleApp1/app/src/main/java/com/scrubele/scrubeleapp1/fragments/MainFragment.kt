package com.scrubele.scrubeleapp1.fragments

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.scrubele.scrubeleapp1.R
import com.scrubele.scrubeleapp1.activities.ObjectDetailActivity
import com.scrubele.scrubeleapp1.adapters.DataAdapter
import com.scrubele.scrubeleapp1.models.ProtectedObjectModel
import com.scrubele.scrubeleapp1.retrofit.ApiClient
import kotlinx.android.synthetic.main.data_fragment_list.*
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private var dataList = ArrayList<ProtectedObjectModel>()

class MainFragment : Fragment() {

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        retainInstance = true
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? =
        inflater.inflate(R.layout.data_fragment_list, container, false)


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setProgressBar()
        list_recycler_view.apply {
            layoutManager = LinearLayoutManager(activity)
            adapter = DataAdapter(
                dataList
            ) { item: ProtectedObjectModel -> partItemClicked(item) }
        }
        loadData()
        swipeContainer.setOnRefreshListener {
            refreshData()
        }
    }

    override fun onStart() {
        super.onStart()
        activity?.registerReceiver(
            broadcastReceiver,
            IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION)
        )
    }

    override fun onStop() {
        super.onStop()
        activity?.unregisterReceiver(broadcastReceiver)
    }

    companion object {
        fun newInstance(): MainFragment = MainFragment()
    }

    private fun partItemClicked(partItem: ProtectedObjectModel) {
        Toast.makeText(this.activity, "Clicked: ${partItem.name}", Toast.LENGTH_LONG).show()
        val showDetailActivityIntent = Intent(this.activity, ObjectDetailActivity::class.java)
        showDetailActivityIntent.putExtra("id", partItem.id)
        showDetailActivityIntent.putExtra("name", partItem.name)
        showDetailActivityIntent.putExtra("description", partItem.description)
        showDetailActivityIntent.putExtra("photo", partItem.photo)
        showDetailActivityIntent.putExtra("size", partItem.size)
        startActivity(showDetailActivityIntent)
    }

    private fun loadData() {
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

    private fun changeDataSet(response: Response<List<ProtectedObjectModel>>?) {
        progressBar.visibility = View.INVISIBLE
        dataList.addAll(response!!.body()!!)
        list_recycler_view.adapter?.notifyDataSetChanged()
    }

    private fun setProgressBar() {
        progressBar.visibility = View.VISIBLE
    }

    private fun refreshData() {
        dataList.clear()
        setProgressBar()
        loadData()
        swipeContainer.isRefreshing = false
    }

    private fun launchDisconnectedState() {
        list_recycler_view.visibility = View.INVISIBLE
        progressBar.visibility = View.VISIBLE
        Toast.makeText(
            list_recycler_view.context,
            getString(R.string.network_disconnected),
            Toast.LENGTH_LONG
        ).show()
    }

    private fun launchConnectedState() {
        list_recycler_view.visibility = View.VISIBLE
    }
}