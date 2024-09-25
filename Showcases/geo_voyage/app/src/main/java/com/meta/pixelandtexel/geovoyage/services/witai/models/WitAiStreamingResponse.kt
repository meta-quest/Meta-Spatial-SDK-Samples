// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.witai.models

import com.meta.pixelandtexel.geovoyage.services.witai.enums.WitAiResponseType

/*
The response from WIT ai as it streams chunks.
 */
data class WitAiStreamingResponse(
    val text: String,
    val type: WitAiResponseType,
)
