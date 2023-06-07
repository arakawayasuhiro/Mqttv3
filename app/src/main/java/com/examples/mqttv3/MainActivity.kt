package com.examples.mqttv3

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.viewModels
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.examples.mqttv3.ui.MqttViewModel
import com.examples.mqttv3.ui.MqttViewModelFactory

class MainActivity : AppCompatActivity() {
    val vmFactory by lazy {
        MqttViewModelFactory(application)
    }
    val navConn by lazy {
        supportFragmentManager.findFragmentById(R.id.nav_host_fragment)!!.findNavController()
    }
    val model by viewModels<MqttViewModel>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        navConn.navigate(R.id.mqttFragment)
        val pref = PreferenceManager.getDefaultSharedPreferences(applicationContext)
        val broker = pref.getString(getString(R.string.broker_key), getString(R.string.broker_default))
        model.brokerUrl.value = broker
    }

    override fun getDefaultViewModelProviderFactory(): ViewModelProvider.Factory {
        return vmFactory
    }
}