// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.events

// Used to communicate between different Activity Scopes
class EventBus {
  private val listeners = mutableListOf<AppEventListener>()

  fun register(listener: AppEventListener) {
    listeners.add(listener)
  }

  fun unregister(listener: AppEventListener) {
    listeners.remove(listener)
  }

  fun post(event: AppEvent) {
    listeners.forEach { it.onEvent(event) }
  }
}
