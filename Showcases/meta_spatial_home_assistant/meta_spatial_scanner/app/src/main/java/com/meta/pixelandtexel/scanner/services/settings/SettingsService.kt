// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.services.settings

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import androidx.core.content.edit

/**
 * Manages application settings using SharedPreferences with an in-memory caching layer, providing
 * methods to initialize, retrieve, and store settings of various primitive types. Ensures that
 * SharedPreferences is initialized only once and uses a cache to improve performance by reducing
 * direct access to SharedPreferences.
 */
object SettingsService {
    private const val TAG: String = "SettingsService"

    private var initialized = false

    private lateinit var prefs: SharedPreferences

    // cached versions of our key/values
    private var cache = HashMap<SettingsKey, Any>()

    /**
     * Initializes the SettingsService with the application context, setting up the SharedPreferences
     * instance used for storing settings. Ensures that initialization only occurs once. If already
     * initialized, the method will simply return.
     *
     * @param context The application context used to access SharedPreferences.
     */
    fun initialize(context: Context) {
        if (initialized) {
            return
        }

        prefs = context.getSharedPreferences("settings_preferences", Context.MODE_PRIVATE)

        initialized = true
    }

    /**
     * Retrieves a setting value for the given key, first checking an in-memory cache for the value.
     * If the value is found in the cache and matches the expected type, it is returned. Otherwise,
     * the function attempts to retrieve the value from SharedPreferences. If retrieved from
     * SharedPreferences, the value is then stored in the cache for future access.
     *
     * Supported types are [String], [Int], [Boolean], [Long], and [Float].
     *
     * @param T The type of the setting value.
     * @param key The [SettingsKey] identifying the setting.
     * @param default The default value to return if the key is not found or if there's a type
     *   mismatch.
     * @return The setting value associated with the key, or the default value if not found or on type
     *   mismatch.
     * @throws Exception if the requested type is not supported.
     */
    fun <T : Any> get(key: SettingsKey, default: T): T {
        Log.d(TAG, "Fetching prefs '${key.value}' of type ${default::class.simpleName}")

        val cachedValue = cache[key]

        // value in cache AND correct type
        if (cachedValue != null && cachedValue::class == default::class) {
            @Suppress("UNCHECKED_CAST")
            return cachedValue as T
        }

        // value not in cache OR type mismatch; try to get from prefs
        val newValue =
            when (default) {
                is String -> prefs.getString(key.value, default)
                is Int -> prefs.getInt(key.value, default)
                is Boolean -> prefs.getBoolean(key.value, default)
                is Long -> prefs.getLong(key.value, default)
                is Float -> prefs.getFloat(key.value, default)
                else -> throw Exception("Unsupported value type ${default::class.simpleName}")
            }
        cache[key] = newValue!!

        @Suppress("UNCHECKED_CAST")
        return newValue as T
    }

    /**
     * Sets a setting value for the given key, persisting to SharedPreferences and also updated in the
     * in-memory cache.
     *
     * Supported types for the value are [String], [Int], [Boolean], [Long], and [Float].
     *
     * @param T The type of the setting value.
     * @param key The [SettingsKey] identifying the setting.
     * @param value The value to be stored for the setting.
     * @throws Exception if the type of the value is not supported.
     */
    fun <T : Any> set(key: SettingsKey, value: T) {
        Log.d(TAG, "Setting prefs '${key.value}' of type ${value::class.simpleName} to: $value")

        prefs.edit {
            when (value) {
                is String -> putString(key.value, value)
                is Int -> putInt(key.value, value)
                is Boolean -> putBoolean(key.value, value)
                is Long -> putLong(key.value, value)
                is Float -> putFloat(key.value, value)
                else -> throw Exception("Unsupported value type ${value::class.simpleName}")
            }
        }

        cache[key] = value
    }
}
