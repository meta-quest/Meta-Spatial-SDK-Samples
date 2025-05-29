// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.objectdetection.utils

/**
 * Meant to be used in tandem with [ObjectPool], implement this interface to support an object
 * pooling pattern, with support for resetting objects as they are returned to the object pool.
 */
interface IPoolable {
  /** Implement this to reset your type before it is returned to the object pool. */
  fun reset()
}

/**
 * A barebones class implementing an object pooling or caching pattern. Useful for often added or
 * deleted structures, as it reuses allocated memory instead of repeatedly allocating and releasing.
 *
 * @param T A structure implementing the [IPoolable] pattern, which will be the pooled object type
 *   specified for this object pool instance.
 * @param initialSize The initial size to pre-populate the object pool, if any.
 * @property factory The factory function which is used to generate new pooled object instances.
 */
class ObjectPool<T : IPoolable>(private val factory: () -> T, initialSize: Int = 0) {
  private val pool = ArrayDeque<T>()

  val availableObjects: Int
    get() = pool.size

  init {
    require(initialSize >= 0) { "Invalid initial size" }

    // optional, initial population of pool
    repeat(initialSize) { pool.addLast(factory()) }
  }

  /**
   * Takes an instance of the [IPoolable] object type from the pool, creating one if necessary.
   *
   * @return The [IPoolable] instance.
   */
  fun take(): T {
    return if (pool.isNotEmpty()) {
      pool.removeLast()
    } else {
      factory()
    }
  }

  /**
   * Returns the instance to the object pool, first resetting it.
   *
   * @param obj The [IPoolable] instance to return.
   */
  fun put(obj: T) {
    obj.reset()
    pool.addLast(obj)
  }
}
