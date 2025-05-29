// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.objectdetection.utils

/**
 * Simple Event classes which can be used to emit notifications to listeners, and subscribe to
 * notifications that some event has occurred.
 */
class Event {
  private val observers = mutableSetOf<() -> Unit>()

  operator fun plusAssign(observer: () -> Unit) {
    observers.add(observer)
  }

  operator fun minusAssign(observer: () -> Unit) {
    observers.remove(observer)
  }

  operator fun invoke() {
    for (observer in observers) {
      observer()
    }
  }
}

class Event1<T> {
  private val observers = mutableSetOf<(T) -> Unit>()

  operator fun plusAssign(observer: (T) -> Unit) {
    observers.add(observer)
  }

  operator fun minusAssign(observer: (T) -> Unit) {
    observers.remove(observer)
  }

  operator fun invoke(value: T) {
    for (observer in observers) {
      observer(value)
    }
  }
}

class Event2<T, U> {
  private val observers = mutableSetOf<(T, U) -> Unit>()

  operator fun plusAssign(observer: (T, U) -> Unit) {
    observers.add(observer)
  }

  operator fun minusAssign(observer: (T, U) -> Unit) {
    observers.remove(observer)
  }

  operator fun invoke(value1: T, value2: U) {
    for (observer in observers) {
      observer(value1, value2)
    }
  }
}
