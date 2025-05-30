// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.spatial.samples.premiummediasample.data.debug

data class DebugStringArrayItem(
    val label: String,
    val values: Array<String>,
    val initialValue: String = values[0],
    var onValueChanged: (value: String) -> Unit,
) : DebugItem() {

  override fun equals(other: Any?): Boolean {
    if (this === other) return true
    if (javaClass != other?.javaClass) return false

    other as DebugStringArrayItem

    if (label != other.label) return false
    if (!values.contentEquals(other.values)) return false
    if (initialValue != other.initialValue) return false
    if (onValueChanged != other.onValueChanged) return false

    return true
  }

  override fun hashCode(): Int {
    var result = label.hashCode()
    result = 31 * result + values.contentHashCode()
    result = 31 * result + initialValue.hashCode()
    result = 31 * result + onValueChanged.hashCode()
    return result
  }
}
