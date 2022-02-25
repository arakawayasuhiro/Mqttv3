package com.examples.mqttv3.ui

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.lifecycle.*
import androidx.recyclerview.widget.RecyclerView
import com.examples.mqttv3.databinding.ItemTopicBinding
import org.eclipse.paho.client.mqttv3.MqttMessage

class Int32LEListItemViewHolder(binding:ItemTopicBinding): TopicListItemViewHolder(binding) {
    override fun parsePayload(msg: MqttMessage): String {
        if (msg.payload.size != 4) {
            return ""
        }
        var ival = 0U
        var base = 1U
        msg.payload.forEach {
            ival += it.toUInt() * base
            base *= 256U
        }
        return ival.toInt().toString()
    }
}
class StringListItemViewHolder(binding:ItemTopicBinding): TopicListItemViewHolder(binding) {
    override fun parsePayload(msg: MqttMessage):String {
        val str = String(msg.payload, Charsets.UTF_8)
        return str
    }
}
open class TopicListItemViewHolder(val binding:ItemTopicBinding): RecyclerView.ViewHolder(binding.root){
    var owner:LifecycleOwner = LifecycleOwner{lcReg}
    private var lcReg = LifecycleRegistry(owner)
    lateinit var payload:LiveData<String>
    lateinit var topic:String
    open fun bindData(item:ItemTopic) {
        lcReg = LifecycleRegistry(owner)
        lcReg.currentState = Lifecycle.State.CREATED
        topic = item.topic
        payload = Transformations.map(item.message) {msg->
            parsePayload(msg)
        }
        binding.holder = this
        binding.lifecycleOwner = owner
    }
    fun onAttached() {
        lcReg.currentState = Lifecycle.State.STARTED
    }
    fun onDetached() {
        lcReg.currentState = Lifecycle.State.RESUMED
    }
    fun onRecycled() {
        lcReg.currentState = Lifecycle.State.DESTROYED
    }
    open fun parsePayload(msg:MqttMessage): String {
        val value = mutableListOf<String>()
        msg.payload.forEach {
            value.add("%02x".format(it))
        }
        return value.joinToString(" ")
    }

}
class TopicListAdapter: RecyclerView.Adapter<TopicListItemViewHolder>() {
    private val typeDefault = 0
    private val typeString = 1
    private val typeInt32LE = 2
    var items = listOf<ItemTopic>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TopicListItemViewHolder {
        val binding = ItemTopicBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return when(viewType) {
            typeInt32LE->Int32LEListItemViewHolder(binding)
            typeString->StringListItemViewHolder(binding)
            else->TopicListItemViewHolder(binding)
        }
    }

    override fun onBindViewHolder(holder: TopicListItemViewHolder, position: Int) {
        holder.bindData(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    override fun getItemViewType(position: Int): Int {
        return when (items[position].topic) {
            "ibm/fan/speed"->typeInt32LE
            "ibm/fan/status","ibm/fan/level"->typeString
            else->typeDefault
        }
    }

    override fun onViewAttachedToWindow(holder: TopicListItemViewHolder) {
        holder.onAttached()
    }

    override fun onViewDetachedFromWindow(holder: TopicListItemViewHolder) {
        holder.onDetached()
    }

    override fun onViewRecycled(holder: TopicListItemViewHolder) {
        holder.onRecycled()
    }
    fun setTopics(topics:List<ItemTopic>) {
        items = topics
        notifyDataSetChanged()
    }
}