// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.theelectricfactory.focus

import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Query
import com.meta.spatial.core.SystemBase
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.ButtonBits
import com.meta.spatial.toolkit.Controller
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.SceneObjectSystem
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.getAbsoluteTransform

// This system allow us to detect when an object is close to a board and give the sensation to stick
// it to the board.
// This doesn't really parent it to the board
class BoardParentingSystem : SystemBase() {

  override fun execute() {

    var boards: MutableList<Entity> = mutableListOf()
    var grabbedChild: Entity? = null

    // Get the list of components that are attachable
    val attachables = Query.where { has(AttachableComponent.id) }
    for (entity in attachables.eval()) {
      val type = entity.getComponent<AttachableComponent>().type

      // Divide them between boards and stickers
      if (type == 1) {
        boards.add(entity)
      } else if (entity.getComponent<Grabbable>().isGrabbed) {
        grabbedChild = entity
      }
    }

    // We check this just for the object that is being moved
    if (grabbedChild == null) return

    // Check position from the grabbed object to each board
    for (board in boards) {

      // Get the controller that is grabbing the sticker
      val controllers = Query.where { has(Controller.id) }
      var grabbingController = Entity.nullEntity()
      for (entity in controllers.eval()) {
        val controller = entity.getComponent<Controller>()
        if (controller.buttonState and (ButtonBits.ButtonSqueezeL or ButtonBits.ButtonSqueezeR) !=
            0) {
          grabbingController = entity
        }
      }

      if (grabbingController == Entity.nullEntity()) return

      // Get absolute transform to compare real distance between controller and sticker
      val grabDistance =
          getAbsoluteTransform(grabbedChild)
              .t
              .distanceTo(getAbsoluteTransform(grabbingController).t)
      // Save a dummy point in space representing the point relative to the controller (position and
      // rotation) at which the sticker remains under normal circumstances
      val grabVector = Vector3(0f, 0f, grabDistance)
      val dummyPoint = grabbingController.getComponent<Transform>().transform.times(grabVector)

      // Get vertices of the board
      val vertices = getMeshBoundsAbsolutePosition(board)
      // Calculate distance from dummy point to board
      val distance =
          pointToRectangleDistance(dummyPoint, vertices[0], vertices[1], vertices[2], vertices[3])

      // If the dummy point is close to the board, we stick the object to the board
      if (distance < 0.08f) {
        grabbedChild.setComponent(
            Transform(Pose(dummyPoint, board.getComponent<Transform>().transform.q)))
      }
    }
  }

  fun getMeshBoundsAbsolutePosition(ent: Entity): MutableList<Vector3> {
    var vertices: MutableList<Vector3> = mutableListOf()
    systemManager.findSystem<SceneObjectSystem>().getSceneObject(ent)?.thenAccept {
      // Get bounds of the mesh
      val meshBounds = it.mesh?.computeCombinedBounds()
      val boardTransform = ent.getComponent<Transform>().transform

      // Calculate absolute position of each vertice of the mesh
      if (meshBounds != null) {
        val v1 =
            boardTransform.t +
                boardTransform.q * Vector3.Right * meshBounds.max.x +
                boardTransform.q * Vector3.Up * meshBounds.max.y
        val v2 =
            boardTransform.t +
                boardTransform.q * Vector3.Right * meshBounds.min.x +
                boardTransform.q * Vector3.Up * meshBounds.max.y
        val v3 =
            boardTransform.t +
                boardTransform.q * Vector3.Right * meshBounds.min.x +
                boardTransform.q * Vector3.Up * meshBounds.min.y
        val v4 =
            boardTransform.t +
                boardTransform.q * Vector3.Right * meshBounds.max.x +
                boardTransform.q * Vector3.Up * meshBounds.min.y

        vertices.add(v1)
        vertices.add(v2)
        vertices.add(v3)
        vertices.add(v4)
      }
    }
    return vertices
  }

  fun pointToRectangleDistance(
      point: Vector3,
      v1: Vector3,
      v2: Vector3,
      v3: Vector3,
      v4: Vector3,
  ): Float {
    // Calculate two edges of the rectangle
    val edge1 = v2.minus(v1)
    val edge2 = v4.minus(v1)

    // Normal of the rectangle's plane
    val normal = edge1.cross(edge2).normalize()

    // Vector from the point to a vertex on the rectangle
    val pointToVertex = point.minus(v1)

    // Project the point onto the plane of the rectangle
    val projection = pointToVertex.dot(normal)

    // Calculate projected point on the rectangle's plane
    val projectedPoint = point.minus(normal.multiply(projection - 0.03f))

    // Check if the projected point is within the rectangle's bounds
    val toProjectedPoint = projectedPoint.minus(v1)
    val dot1 = toProjectedPoint.dot(edge1)
    val dot2 = toProjectedPoint.dot(edge2)

    val edge1LengthSquared = edge1.dot(edge1)
    val edge2LengthSquared = edge2.dot(edge2)

    val insideRectangle =
        dot1 >= 0 && dot1 <= edge1LengthSquared && dot2 >= 0 && dot2 <= edge2LengthSquared

    return if (insideRectangle) {
      // If inside, return the perpendicular distance to the plane
      Math.abs(projection) // Use Math.abs instead of absoluteValue
    } else {
      // Otherwise, calculate the distance to the edges/vertices of the rectangle
      val distances =
          listOf(
              pointToLineSegmentDistance(point, v1, v2),
              pointToLineSegmentDistance(point, v2, v3),
              pointToLineSegmentDistance(point, v3, v4),
              pointToLineSegmentDistance(point, v4, v1),
          )
      distances.minOrNull() ?: 0f
    }
  }

  fun pointToLineSegmentDistance(point: Vector3, v1: Vector3, v2: Vector3): Float {
    val line = v2.minus(v1)
    val lineLengthSquared = line.dot(line)

    if (lineLengthSquared == 0f) {
      return point.minus(v1).length() // Assuming your Vector3 has a length() method
    }

    // Project point onto the line
    val t = (point.minus(v1)).dot(line) / lineLengthSquared
    return when {
      t < 0 -> point.minus(v1).length() // Point is closest to v1
      t > 1 -> point.minus(v2).length() // Point is closest to v2
      else -> {
        // Point is closest to some point on the line
        val projection = v1.plus(line.multiply(t))
        point.minus(projection).length()
      }
    }
  }
}
