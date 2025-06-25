/*
 * Copyright (c) Meta Platforms, Inc. and affiliates.
 *
 * This source code is licensed under the MIT license found in the
 * LICENSE file in the root directory of this source tree.
 */

package com.meta.spatial.samples.premiummediasample.immersive

import android.os.Bundle
import com.meta.spatial.core.Vector2
import com.meta.spatial.core.Vector3
import com.meta.spatial.samples.premiummediasample.ANCHOR_SPAWN_DISTANCE
import com.meta.spatial.samples.premiummediasample.SCREEN_FOV
import com.meta.spatial.samples.premiummediasample.SPAWN_DISTANCE
import com.meta.spatial.samples.premiummediasample.data.CinemaState
import com.meta.spatial.samples.premiummediasample.data.HomeItem
import com.meta.spatial.samples.premiummediasample.data.MediaSource
import com.meta.spatial.samples.premiummediasample.data.PoseAndSize
import com.meta.spatial.samples.premiummediasample.data.VideoSource
import com.meta.spatial.samples.premiummediasample.entities.HomePanelEntity.Companion.homePanelFov
import com.meta.spatial.samples.premiummediasample.entities.VRCinemaEntity
import com.meta.spatial.samples.premiummediasample.getHeadPose
import com.meta.spatial.samples.premiummediasample.panels.controlsPanel.ControlsPanelActivity.Companion.ControlsPanelCodes
import com.meta.spatial.samples.premiummediasample.placeInFrontOfHead
import com.meta.spatial.samples.premiummediasample.service.IPCService
import com.meta.spatial.samples.premiummediasample.setAbsolutePosition
import com.meta.spatial.samples.premiummediasample.setDistanceFov
import com.meta.spatial.samples.premiummediasample.setSize
import com.meta.spatial.samples.premiummediasample.systems.anchor.AnchorSnappingSystem
import com.meta.spatial.samples.premiummediasample.systems.heroLighting.WallLightingSystem
import com.meta.spatial.samples.premiummediasample.updateSizeFromFov
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.Transform

class CinemaStateHandler(private val immersiveViewModel: ImmersiveViewModel) {
  private var cinema: VRCinemaEntity? = null
  var cinemaState = CinemaState.Home
  var lastTvPoseSize: PoseAndSize? = null
  var homeItem: HomeItem? = null

  private val homePanel = immersiveViewModel.homePanel
  private val controlsPanel = immersiveViewModel.controlsPanel
  private val lightingPassthroughHandler = immersiveViewModel.lightingPassthroughHandler
  private val systemManager = immersiveViewModel.systemManager

  fun moveHomeToTVPosition() {
    // Updates home position from TV position
    if (lastTvPoseSize != null) {
      // Going from Cinema -> Home. Use the last known position of TV
      setAbsolutePosition(homePanel.entity, lastTvPoseSize!!.pose)
      updateSizeFromFov(homePanel.entity, homePanelFov)
      lastTvPoseSize = null
    } else {
      recenter()
    }
  }

  fun setCinemaState(
      newState: CinemaState,
      homeItem: HomeItem? = null,
      forceIsPlayingLighting: Boolean? = null,
  ) {
    if (homeItem != null) {
      this.homeItem = homeItem
    }
    val prevState = cinemaState
    cinemaState = newState

    val isPlaying =
        forceIsPlayingLighting
            ?: (immersiveViewModel.exoPlayer.isPlaying ||
                immersiveViewModel.exoPlayer.playWhenReady)
    lightingPassthroughHandler.transitionLighting(newState, isPlaying)

    // Turn off/save old state variables
    when (prevState) {
      CinemaState.Cinema -> {
        if (newState !== CinemaState.Cinema) {
          cinema?.setVisible(false)
        }
      }
      CinemaState.TV -> {
        lastTvPoseSize = PoseAndSize.fromEntity(immersiveViewModel.currentExoPanel!!.entity)
      }
      CinemaState.Equirect180 -> {
        controlsPanel.entity.setComponent(Grabbable(enabled = false))
      }
      CinemaState.Home -> {
        val drmEnabled =
            (homeItem?.media?.videoSource is VideoSource.Url &&
                homeItem?.media?.videoSource?.drmLicenseUrl != null)
        immersiveViewModel.ipcServiceConnection.messageProcess(
            IPCService.CONTROL_PANEL_CHANNEL,
            ControlsPanelCodes.IMMERSIVE_UPDATE_CONTROL_PANEL.ordinal,
            Bundle().apply {
              putSerializable("cinemaState", cinemaState)
              // If we are selecting a new video, need to update lighting slider on control panel.
              if (homeItem != null) {
                putBoolean(
                    "lightingEnabled",
                    !drmEnabled && homeItem.media.videoShape !== MediaSource.VideoShape.Equirect180)
              }
            })
      }
    }

    when (newState) {
      CinemaState.TV -> {
        systemManager.findSystem<WallLightingSystem>().transitionInstant(true)
        controlsPanel.attachToEntity(immersiveViewModel.currentExoPanel!!.entity)

        when (prevState) {
          CinemaState.Cinema -> {
            if (lastTvPoseSize == null) { // If a recenter was triggered in cinema
              recenter()
            } else {
              PoseAndSize.applyToEntity(
                  immersiveViewModel.currentExoPanel!!.entity, lastTvPoseSize!!)
            }
          }
          CinemaState.TV -> {
            recenter()
          }
          CinemaState.Home -> {
            immersiveViewModel.currentExoPanel!!
                .entity
                .setComponent(Transform(homePanel.entity.getComponent<Transform>().transform))
            updateSizeFromFov(immersiveViewModel.currentExoPanel!!.entity, SCREEN_FOV)
          }
          CinemaState.Equirect180 -> {} // This transition is not possible.
        }
      }
      CinemaState.Cinema -> {
        if (prevState == CinemaState.Cinema) {
          return
        }
        systemManager.findSystem<WallLightingSystem>().transitionInstant(true)

        val aspectRatioCropped = this.homeItem?.media?.aspectRatio ?: (1920f / 1080f)
        val cinemaScreenSize = Vector2(22f, 22f / aspectRatioCropped)

        if (cinema == null) {
          cinema =
              VRCinemaEntity(
                  VRCinemaEntity.VRCinemaConfig(
                      floorOnly = true, screenSize = cinemaScreenSize, distanceToScreen = 15f))
        }
        val cinemaPose = getHeadPose()
        cinemaPose.q =
            immersiveViewModel.currentExoPanel!!.entity.getComponent<Transform>().transform.q
        cinema!!.setCinemaPoseRelativeToUser(cinemaPose)

        setSize(
            immersiveViewModel.currentExoPanel!!.entity,
            Vector3(cinemaScreenSize.x, cinemaScreenSize.y, 0f))
        immersiveViewModel.currentExoPanel!!
            .entity
            .setComponent(Transform(cinema!!.getScreenPose()))
        cinema!!.setVisible(true)

        controlsPanel.detachFromEntity()
        controlsPanel.movePanelForCinema(immersiveViewModel.currentExoPanel!!.entity)
      }
      CinemaState.Home -> {
        this.homeItem = null
        systemManager.findSystem<WallLightingSystem>().transitionInstant(false)
      }
      CinemaState.Equirect180 -> {
        systemManager.findSystem<WallLightingSystem>().transitionInstant(false)
        controlsPanel.detachFromEntity()
        controlsPanel.entity.setComponent(Grabbable(type = GrabbableType.PIVOT_Y))
        recenter()
      }
    }
  }

  fun recenter() {
    when (cinemaState) {
      CinemaState.TV -> {
        setDistanceFov(
            immersiveViewModel.currentExoPanel!!.entity,
            ANCHOR_SPAWN_DISTANCE,
            SCREEN_FOV,
            vector = getHeadPose())
        systemManager
            .findSystem<AnchorSnappingSystem>()
            .snapToAnchorViaGaze(immersiveViewModel.currentExoPanel!!.entity, getHeadPose())
      }
      CinemaState.Cinema -> {
        cinema!!.setCinemaPoseRelativeToUser(getHeadPose())
        immersiveViewModel.currentExoPanel!!
            .entity
            .setComponent(Transform(cinema!!.getScreenPose()))
        controlsPanel.movePanelForCinema(immersiveViewModel.currentExoPanel!!.entity)
        lastTvPoseSize = null
      }
      CinemaState.Home -> {
        setDistanceFov(homePanel.entity, SPAWN_DISTANCE, homePanelFov, vector = getHeadPose())
        systemManager
            .findSystem<AnchorSnappingSystem>()
            .snapToAnchorViaGaze(homePanel.entity, getHeadPose())
      }
      CinemaState.Equirect180 -> {
        placeInFrontOfHead(immersiveViewModel.currentExoPanel?.entity!!, .1f)
        placeInFrontOfHead(controlsPanel.entity, 1f, offset = Vector3(0f, -0.75f, 0f))
      }
    }
  }
}
