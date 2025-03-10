// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services

import android.content.Context
import android.content.SharedPreferences
import com.meta.pixelandtexel.geovoyage.BuildConfig
import com.meta.pixelandtexel.geovoyage.enums.SettingsKey

interface ISettingsKeyChangeReceiver {
  fun onKeyUpdated(newValue: PreferenceValue)
}

sealed class PreferenceValue {
  data class StringValue(val value: String) : PreferenceValue()

  data class IntValue(val value: Int) : PreferenceValue()

  data class BooleanValue(val value: Boolean) : PreferenceValue()
  // Add other types as needed, such as FloatValue, LongValue, etc.
}

object SettingsService {
  private const val TAG: String = "SettingsService"

  private var initialized = false
  private lateinit var prefs: SharedPreferences

  // shared preferences
  private const val PREFS_NAME = "settings_preferences"

  // cached versions of our key/values
  private var cache = HashMap<SettingsKey, Any>()

  private val receiversMap = mutableMapOf<String, MutableList<ISettingsKeyChangeReceiver>>()

  private val preferenceChangeListener =
      SharedPreferences.OnSharedPreferenceChangeListener { sharedPreferences, key ->
        receiversMap[key]?.forEach { receiver ->
          val newValue =
              when (val value = sharedPreferences.all[key]) {
                is String -> PreferenceValue.StringValue(value)
                is Int -> PreferenceValue.IntValue(value)
                is Boolean -> PreferenceValue.BooleanValue(value)
                else -> null
              }
          newValue?.let { receiver.onKeyUpdated(it) }
        }
      }

  fun initialize(context: Context) {
    if (initialized) {
      return
    }

    prefs = context.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    prefs.registerOnSharedPreferenceChangeListener(preferenceChangeListener)

    // cache any initial values
    cache[SettingsKey.OLLAMA_URL] =
        prefs.getString(SettingsKey.OLLAMA_URL.value, BuildConfig.OLLAMA_SERVER_URL)!!

    initialized = true
  }

  fun <T : Any> get(key: SettingsKey, default: T): T {
    if (!cache.containsKey(key)) {
      cache[key] =
          when (default) {
            is String -> prefs.getString(key.value, default)!!
            is Int -> prefs.getInt(key.value, default)
            is Boolean -> prefs.getBoolean(key.value, default)
            is Long -> prefs.getLong(key.value, default)
            is Float -> prefs.getFloat(key.value, default)
            else -> throw Exception("Unsupported value type ${default::class.simpleName}")
          }
    }
    @Suppress("UNCHECKED_CAST")
    return cache[key]!! as T
  }

  fun <T : Any> set(key: SettingsKey, value: T) {
    when (value) {
      is String -> prefs.edit()?.putString(key.value, value as String)?.apply()
      is Int -> prefs.edit()?.putInt(key.value, value.toInt())?.apply()
      is Boolean -> prefs.edit()?.putBoolean(key.value, value as Boolean)?.apply()
      is Long -> prefs.edit()?.putLong(key.value, value.toLong())?.apply()
      is Float -> prefs.edit()?.putFloat(key.value, value.toFloat())?.apply()
      else -> throw Exception("Unsupported value type ${value::class.simpleName}")
    }
    cache[key] = value
  }

  fun subscribeToKeyUpdate(key: String, receiver: ISettingsKeyChangeReceiver) {
    if (receiversMap[key] == null) {
      receiversMap[key] = mutableListOf()
    }
    receiversMap[key]?.add(receiver)
  }
}
