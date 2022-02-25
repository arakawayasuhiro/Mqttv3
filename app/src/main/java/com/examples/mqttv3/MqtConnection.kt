package com.examples.mqttv3

import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.channels.sendBlocking
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.flow.collect
import org.eclipse.paho.client.mqttv3.*
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence

class MqttConnection(val callback:MqttConnectionCallback): MqttCallbackExtended {
    interface MqttConnectionCallback {
        fun onConnectionStatusCahange(status:Boolean)
        fun onMessageArrived(topic:String, message:MqttMessage)
    }
    private lateinit var mqtt:MqttAsyncClient
    private lateinit var f:Flow<Pair<String, MqttMessage>>
    val persistence = MemoryPersistence()
    fun connect(broker:String, clientId:String){
        mqtt = MqttAsyncClient(broker, clientId, persistence)
        val options = MqttConnectOptions()
        options.isCleanSession = true
        mqtt.setCallback(this)
        mqtt.connect()
    }
    fun disconnect() {
        mqtt.disconnect()
        callback.onConnectionStatusCahange(false)
    }
    fun subscribe(topic:String) {
        if(mqtt.isConnected) {
            mqtt.subscribe(topic, 0)
        }
     }
    override fun connectionLost(cause: Throwable?) {
        callback.onConnectionStatusCahange(false)
    }

    override fun messageArrived(topic: String?, message: MqttMessage?) {
        if (topic != null && message != null)
            callback.onMessageArrived(topic, message)
    }

    override fun deliveryComplete(token: IMqttDeliveryToken?) {
        TODO("Not yet implemented")
    }

    override fun connectComplete(reconnect: Boolean, serverURI: String?) {
        callback.onConnectionStatusCahange(true)
    }
}