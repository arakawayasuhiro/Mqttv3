package com.examples.mqttv3.ui

import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.examples.mqttv3.R

class MqttFragment : Fragment() {
    val model by activityViewModels<MqttViewModel>()
    private lateinit var rv:RecyclerView
    val adapter by lazy {
        TopicListAdapter()
    }
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
        ): View? {
        setHasOptionsMenu(true)
        // Inflate the layout for this fragment
        val v =  inflater.inflate(R.layout.fragment_mqtt, container, false)
        model.brokerUrl.observe(viewLifecycleOwner) {url->
            view?.run {
                val broker = findViewById<TextView>(R.id.txtBroker)
                broker.text = url
            }
        }
        rv = v.findViewById<RecyclerView>(R.id.listTopic)
        rv.layoutManager = LinearLayoutManager(context)
        rv.adapter = adapter
        model.topics.observe(viewLifecycleOwner) {
            adapter.setTopics(it)
        }
        model.isConnect.observe(viewLifecycleOwner) {
            view?.findViewById<Button>(R.id.btnConn)?.run {
                setText(if (it) R.string.button_disconnect else R.string.button_connect)
            }
        }
        v.findViewById<Button>(R.id.btnConn).setOnClickListener {
            onConnClicked()
        }
        return v
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_mqtt, menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when(item.itemId) {
            R.id.menu_setting->{
                findNavController().navigate(R.id.settingsFragment)
                true
            }
            else->false
        }
    }
    fun onConnClicked() {
        val text =
        if (model.isConnect.value!!) {
            model.disconnect()
            R.string.button_connect
        } else {
            model.connect()
            model.subscribe("ibm/fan/#")
            R.string.button_disconnect
        }
        view?.run {
            findViewById<Button>(R.id.btnConn).setText(text)
        }
    }
}