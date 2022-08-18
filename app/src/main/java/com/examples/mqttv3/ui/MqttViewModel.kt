package com.examples.mqttv3.ui

import android.app.Application
import android.os.Handler
import android.os.Looper
import androidx.lifecycle.*
import com.examples.mqttv3.MqttConnection
import org.eclipse.paho.client.mqttv3.MqttMessage

class ItemTopic(val topic:String) {
    val message = MutableLiveData<MqttMessage>()
    fun applyMessage(msg:MqttMessage) {
        message.postValue(msg)
    }
}
class MqttViewModelFactory(val app:Application): ViewModelProvider.Factory {
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MqttViewModel(app) as T
    }
}
class MqttViewModel(application: Application) : AndroidViewModel(application), MqttConnection.MqttConnectionCallback {
    val isConnect = MutableLiveData<Boolean>(false)
    val brokerUrl = MutableLiveData<String>()
    val clientId = "kotlin_v3"
    val topics = MutableLiveData<List<ItemTopic>>(mutableListOf<ItemTopic>())
    val mqtt = MqttConnection(this)
    val subscribes = mutableListOf<String>()
    fun connect() {
        brokerUrl.value?.let {
            mqtt.connect(it, clientId)
        }
    }
    fun disconnect() {
        mqtt.disconnect()
    }

    fun subscribe(topic:String) {
        if (subscribes.firstOrNull{it.equals(topic)} == null) {
            subscribes.add(topic)
        }
        if (isConnect.value!!) {
            mqtt.subscribe(topic)
        }
    }

    override fun onConnectionStatusCahange(status: Boolean) {
        isConnect.postValue(status)
        if (status) {
            subscribes.forEach{
                mqtt.subscribe(it)
            }
        }
    }
    val handler = Handler(Looper.getMainLooper())
    override fun onMessageArrived(topic: String, message: MqttMessage) {
        handler.post(Runnable {
            val list = topics.value!!.toMutableList()
            var item = list.firstOrNull {it.topic.equals(topic)}
            if (item == null) {
                item = ItemTopic(topic)
                list.add(item)
                topics.value = list
            }
            item!!.applyMessage(message)
        })
    }

    override fun onCleared() {
        super.onCleared()
        mqtt.disconnect()
    }
}