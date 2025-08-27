// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.pixelandtexel.scanner.ecs

import com.meta.pixelandtexel.scanner.HandSide
import com.meta.pixelandtexel.scanner.WristAttached
import com.meta.pixelandtexel.scanner.utils.MathUtils.fromSequentialPYR
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.toolkit.AvatarBody
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible

/**
 * Manages entities that are designated to be attached to the user's wrists, responsible for
 * updating the position, rotation, and visibility of such entities based on the user's hand and
 * head movements.
 */
class WristAttachedSystem : SystemBase() {
  companion object {
    private const val TAG: String = "WristAttachedSystem"
  }

  private val wristAttachedEntities = mutableListOf<Entity>()

  /**
   * Finds new wrist-attached entities, retrieves the current transforms of the player's head and
   * hands, and then updates the pose and visibility of each tracked wrist-attached entity.
   * Visibility is determined by whether the entity (and by extension, the user's palm) is facing
   * towards the user's head, and if the head is looking at the palm.
   */
  override fun execute() {
    findNewEntities()

    // get our head and hands/controllers transforms

    val playerBody = getAvatarBody()
    if (
        !playerBody.head.hasComponent<Transform>() ||
            !playerBody.leftHand.hasComponent<Transform>() ||
            !playerBody.rightHand.hasComponent<Transform>()
    ) {
      // Failed to find transform components on avatar body parts; controllers may be
      // disconnected and hands out of view
      return
    }

    val headTransform = playerBody.head.getComponent<Transform>()
    val leftHandTransform = playerBody.leftHand.getComponent<Transform>()
    val rightHandTransform = playerBody.rightHand.getComponent<Transform>()

    // now process existing entities

    for (entity in wristAttachedEntities) {
      val comp = entity.getComponent<WristAttached>()

      val handTransform =
          when (comp.side) {
            HandSide.LEFT -> leftHandTransform
            HandSide.RIGHT -> rightHandTransform
          }

      // calculate the new pose for the attached entity

      val quatOffset =
          Quaternion.fromSequentialPYR(comp.rotation.x, comp.rotation.y, comp.rotation.z)
      val rotation = handTransform.transform.q.times(quatOffset)

      // use the offset rotation as our basis orientation for translation
      val position = handTransform.transform.t + rotation.times(comp.position)

      val pose = Pose(position, if (comp.faceUser) headTransform.transform.q else rotation)
      entity.setComponent(Transform(pose))

      // hide the entity if the palm isn't facing the user's head

      val vHeadFwd = headTransform.transform.forward()
      val vAnchorFwd = rotation.times(Vector3.Forward)
      val vHeadToAnchor = (position - headTransform.transform.t).normalize()

      val lookingAtHand = vHeadFwd.dot(vHeadToAnchor) > 0.85f
      val handFacingHead = vAnchorFwd.dot(vHeadToAnchor) > 0.4f
      entity.setComponent(Visible(lookingAtHand && handFacingHead))
    }
  }

  /**
   * Handles the deletion of an entity from the system, removing the entity from the internal list
   * of tracked wrist-attached entities.
   *
   * @param entity The entity to be deleted.
   */
  override fun delete(entity: Entity) {
    super.delete(entity)

    wristAttachedEntities.remove(entity)
  }

  /**
   * Finds new entities that should be managed by this system, querying for local entities that have
   * both [WristAttached] and [Transform] components, and adds to the [wristAttachedEntities] list.
   */
  private fun findNewEntities() {
    val query = Query.where { has(WristAttached.id, Transform.id) and changed(WristAttached.id) }
    for (entity in query.eval()) {
      if (wristAttachedEntities.contains(entity)) {
        continue
      }

      if (!entity.isLocal()) {
        continue
      }

      wristAttachedEntities.add(entity)
    }
  }

  /**
   * Retrieves the [AvatarBody] component for the local, player-controlled avatar.
   *
   * @return The [AvatarBody] component of the player's avatar.
   * @throws NoSuchElementException if no local, player-controlled avatar body is found.
   */
  private fun getAvatarBody(): AvatarBody {
    return Query.where { has(AvatarBody.id) }
        .eval()
        .filter { it.isLocal() && it.getComponent<AvatarBody>().isPlayerControlled }
        .first()
        .getComponent<AvatarBody>()
  }
}
