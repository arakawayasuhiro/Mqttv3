package com.examples.mqttv3.ui

import android.os.Bundle
import androidx.fragment.app.activityViewModels
import androidx.preference.EditTextPreference
import androidx.preference.PreferenceFragmentCompat
import com.examples.mqttv3.R

class SettingsFragment : PreferenceFragmentCompat() {
    val model by activityViewModels<MqttViewModel>()
    override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
        setPreferencesFromResource(R.xml.root_preferences, rootKey)
        val pref = findPreference<EditTextPreference>(getString(R.string.broker_key))
        pref?.run {
            setOnPreferenceChangeListener { _, newValue ->
                model.brokerUrl.postValue(newValue as String)
                true
            }
        }
    }
}