package com.meta.pixelandtexel.scanner.feature.objectdetection.domain.repository.detection

import android.media.Image
import com.meta.pixelandtexel.scanner.feature.objectdetection.datasource.detector.models.DetectedObject

data class DetectionState(
    val image: Image,
    val foundObjects: List<DetectedObject>,
    val updatedObjects: List<DetectedObject>,
    val lostObjectIds: List<Int>,
    val finally: () -> Unit,
)