// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.models

import com.meta.spatial.core.Bound3D
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Vector3

/**
 * Represents a curated object with associated metadata, UI elements, and 3D mesh information. Holds
 * all the necessary details to display and interact with an object within the scene and UI. See the
 * services/CuratedObjectHandler and ui/objectinfo/ classes for how this structure is used to
 * display and render 3D objects representing "curated" objects, or those with pre-written copy and
 * images.
 *
 * Also see the res/xml/objects.xml for the data representing the example curated objects in this
 * app.
 *
 * @property name The user-facing name of the curated object.
 * @property matchingLabels A list of strings used as labels for categorizing or searching for this
 *   object.
 * @property ui A list of [PanelContentBase] defining the UI elements to be displayed for this
 *   object.
 * @property order An integer value used to determine the sorting order of this object among others.
 * @property meshEntity The optional [Entity] representing the 3D model of this object in the scene.
 * @property meshBounds The optional [Bound3D] defining the bounding box of the object's mesh.
 * @property meshPositionOffset A [Vector3] representing the positional offset to be applied to the
 *   mesh. Defaults to no offset.
 * @property meshRotationOffset A [Quaternion] representing the rotational offset to be applied to
 *   the mesh. Defaults to no rotation.
 * @property animationNameToTrack An optional map where keys are animation names and values are
 *   their corresponding track indices.
 * @property initialAnimationTrack An optional integer specifying the animation track to play
 *   initially.
 * @property hasGrabbable A boolean flag indicating whether this object is intended to be grabbable
 *   by the user.
 */
data class CuratedObject(
    val name: String,
    val matchingLabels: List<String>,
    val ui: List<PanelContentBase>,
    val order: Int
) {
  var meshEntity: Entity? = null
  var meshBounds: Bound3D? = null
  var meshPositionOffset: Vector3 = Vector3(0f)
  var meshRotationOffset: Quaternion = Quaternion()
  var animationNameToTrack: Map<String, Int>? = null
  var initialAnimationTrack: Int? = null
}

enum class PanelContentType {
  UNKNOWN,
  TILES,
  IMAGE_COPY
}

abstract class PanelContentBase(
    val title: String,
    val layoutType: PanelContentType,
    val animationTrack: Int? = null
)

class ImageCopyPanelContent(
    title: String,
    animationTrack: Int?,
    val imageResId: Int? = null,
    val copy: String? = null
) : PanelContentBase(title, PanelContentType.IMAGE_COPY, animationTrack)

class TilesPanelContent(title: String, animationTrack: Int?, val tiles: List<TileContent>) :
    PanelContentBase(title, PanelContentType.TILES, animationTrack)

data class TileContent(
    val title: String,
    val subTitle: String,
    val imageResId: Int? = null,
)
