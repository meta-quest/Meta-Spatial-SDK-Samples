// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.models

data class TriviaQuestion(
    val number: UShort,
    val query: String,
    val answer: String,
    val option2: String,
    val option3: String,
    val difficulty: UShort,
    val latitude: Float,
    val longitude: Float
)
