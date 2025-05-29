// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.services.settings

/**
 * The keys used for accessing settings from [SettingsService]. Each enum constant represents a
 * specific setting and holds the actual string key used for storing and retrieving the setting
 * value in [android.content.SharedPreferences].
 *
 * @property value The string representation of the settings key.
 */
enum class SettingsKey(val value: String) {
  ACCEPTED_NOTICE("accepted_user_notice")
}
