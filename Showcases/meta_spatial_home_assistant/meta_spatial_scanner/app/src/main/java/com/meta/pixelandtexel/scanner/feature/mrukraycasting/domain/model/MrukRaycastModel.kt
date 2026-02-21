package com.meta.pixelandtexel.scanner.feature.mrukraycasting.domain.model

import com.meta.pixelandtexel.scanner.models.devices.Device
import com.meta.spatial.core.Pose

data class MrukRaycastModel(
    val device: Device,
    val pose: Pose
)