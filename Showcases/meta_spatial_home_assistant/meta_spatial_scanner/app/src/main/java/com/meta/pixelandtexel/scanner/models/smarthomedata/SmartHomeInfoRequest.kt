package com.meta.pixelandtexel.scanner.models.smarthomedata

import com.meta.pixelandtexel.scanner.feature.objectdetection.model.RaycastRequestModel

data class SmartHomeInfoRequest(
    val type: TypeSmartHomeInfo,
    val raycastInfo: RaycastRequestModel
)