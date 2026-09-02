package com.meta.pixelandtexel.scanner.feature.objectdetection.model

import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Vector3

data class RaycastRequestModel(
    val headPosition: Vector3,
    val direction: Vector3,
    val rotation: Quaternion
)