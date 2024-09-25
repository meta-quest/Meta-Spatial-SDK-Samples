// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.enums

enum class SettingsKey(val value: String) {
  LLAMA_SERVER_TYPE("llama_server_type"),
  OLLAMA_URL("ollama_url"),
  WIT_AI_FILTERING_ENABLED("wit_ai_filtering_enabled"),
  SILENCE_DETECTION_ENABLED("silence_detection_enabled"),
  LANDMARKS_ENABLED("landmarks_enabled"),
  LAST_DAILY_QUIZ("last_daily_quiz"),
  ACCEPTED_NOTICE("accepted_user_notice")
}
