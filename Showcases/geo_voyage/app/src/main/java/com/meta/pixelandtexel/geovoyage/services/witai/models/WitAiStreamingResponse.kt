// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.geovoyage.services.witai.models

import com.meta.pixelandtexel.geovoyage.services.witai.enums.WitAiResponseType

/*
The response from WIT ai as it streams chunks.
https://wit.ai/docs/http/20240304/
 */
data class WitAiStreamingResponse(
    val text: String,
    val type: WitAiResponseType,
    val error: String?,
    val code: String?,
)
