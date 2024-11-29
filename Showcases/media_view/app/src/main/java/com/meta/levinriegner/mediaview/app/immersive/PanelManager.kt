// (c) Meta Platforms, Inc. and affiliates. Confidential and proprietary.

package com.meta.levinriegner.mediaview.app.immersive

import android.content.Intent
import android.os.Handler
import android.os.Looper
import com.meta.levinriegner.mediaview.app.gallery.GalleryActivity
import com.meta.levinriegner.mediaview.app.gallery.filter.MediaFilterActivity
import com.meta.levinriegner.mediaview.app.gallery.menu.GalleryMenuActivity
import com.meta.levinriegner.mediaview.app.immersive.entity.PanelTransformations
import com.meta.levinriegner.mediaview.app.immersive.panel.PanelRegistrationIds
import com.meta.levinriegner.mediaview.app.immersive.panel.model.AppPanelRegistration
import com.meta.levinriegner.mediaview.app.player.PlayerActivity
import com.meta.levinriegner.mediaview.app.player.menu.immersive.ImmersiveMenuActivity
import com.meta.levinriegner.mediaview.app.player.menu.minimized.MinimizedMenuActivity
import com.meta.levinriegner.mediaview.app.privacy.PrivacyPolicyActivity
import com.meta.levinriegner.mediaview.app.shared.model.maximizedBottomCenterPanelVector3
import com.meta.levinriegner.mediaview.app.shared.model.maximizedPanelConfigOptions
import com.meta.levinriegner.mediaview.app.shared.model.minimizedPanelConfigOptions
import com.meta.levinriegner.mediaview.app.shared.model.panelWidthAndHeight
import com.meta.levinriegner.mediaview.app.shared.theme.Dimens
import com.meta.levinriegner.mediaview.app.upload.UploadActivity
import com.meta.levinriegner.mediaview.data.gallery.model.MediaModel
import com.meta.levinriegner.mediaview.data.gallery.model.MediaType.VIDEO_360
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Query
import com.meta.spatial.core.SpatialContext
import com.meta.spatial.core.Vector3
import com.meta.spatial.runtime.AlphaMode
import com.meta.spatial.runtime.PanelConfigOptions
import com.meta.spatial.runtime.PanelConfigOptions.Companion.DEFAULT_DPI
import com.meta.spatial.runtime.PanelSceneObject
import com.meta.spatial.runtime.QuadLayerConfig
import com.meta.spatial.runtime.Scene
import com.meta.spatial.toolkit.AppSystemActivity
import com.meta.spatial.toolkit.GLXFInfo
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.Panel
import com.meta.spatial.toolkit.PanelCreator
import com.meta.spatial.toolkit.PanelRegistration
import com.meta.spatial.toolkit.Scale
import com.meta.spatial.toolkit.SpatialActivityManager
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.TransformParent
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.toolkit.createPanelEntity
import timber.log.Timber

class PanelManager(
    private val panelTransformations: PanelTransformations,
    private val scene: Scene,
    private val spatialContext: SpatialContext,
) {
    private val zIndexMenu = 99

    private val galleryPanelDistance = 0.9f
    private val playerPanelDistance = 0.8f
    private val immersiveMenuHeight = 0.1f

    private lateinit var galleryEntity: Entity

    private val registeredPanels = mutableMapOf<Int, AppPanelRegistration>()

    fun providePanelRegistrations(): List<PanelRegistration> {
        val panelRegistrations = listOf(
            PanelCreator(PanelRegistrationIds.GALLERY) { ent ->
                galleryEntity = ent

                createGalleryPanel(ent)
            },
            PanelCreator(PanelRegistrationIds.MEDIA_FILTER) { ent ->
                createMediaFilterPanel(ent)
            },
            PanelCreator(PanelRegistrationIds.GALLERY_MENU) { ent ->
                createGalleryMenuPanel(ent)
            },
            PanelCreator(PanelRegistrationIds.PRIVACY_POLICY) { ent ->
                createPrivacyPolicyPanel(ent)
            },
        )

        panelRegistrations.forEach { panelRegistration ->
            registeredPanels[panelRegistration.registrationId] = AppPanelRegistration(
                registrationId = panelRegistration.registrationId,
                registration = panelRegistration,
            )
        }

        return panelRegistrations
    }

    fun provideUploadPanelRegistration(): PanelRegistration {
        val registration = PanelCreator(PanelRegistrationIds.UPLOAD_MEDIA) { ent ->
            // Set gallery as parent
            ent.setComponent(TransformParent(galleryEntity))

            ent.setComponent(
                // Transform relative to the parent
                Transform(
                    Pose(
                        Vector3(
                            0f,
                            0f,
                            -0.025f
                        ),
                        Quaternion(0f, 0f, 0f)
                    )
                )
            )

            createUploadPanel(ent)
        }

        registeredPanels[registration.registrationId] = AppPanelRegistration(
            registrationId = registration.registrationId,
            registration = registration,
        )

        return registration
    }

    fun providePlayerPanelRegistration(mediaModel: MediaModel): PanelRegistration {
        val registration = PanelCreator(PanelRegistrationIds.media(mediaModel.id)) { ent ->
            // Set initial transform based on head pose
            panelTransformations.applyTransform(ent, playerPanelDistance, Vector3(0f, 0f, 0f))
            createPlayerPanel(ent, mediaModel)
        }

        registeredPanels[registration.registrationId] = AppPanelRegistration(
            registrationId = registration.registrationId,
            registration = registration,
        )

        return registration
    }

    fun providePlayerMenuRegistration(mediaModel: MediaModel): PanelRegistration {
        val registration =
            PanelCreator(PanelRegistrationIds.mediaPopUpButton(mediaModel.id)) { ent ->
                createPlayerMenuPanel(
                    ent,
                    mediaModel
                )
            }

        registeredPanels[registration.registrationId] = AppPanelRegistration(
            registrationId = registration.registrationId,
            registration = registration,
        )

        return registration

    }

    fun provideImmersiveMenuRegistration(mediaModel: MediaModel): PanelRegistration {
        val registration = PanelCreator(PanelRegistrationIds.mediaImmersive(mediaModel.id)) { ent ->
            createImmersiveMenuPanel(ent, mediaModel)
        }

        registeredPanels[registration.registrationId] = AppPanelRegistration(
            registrationId = registration.registrationId,
            registration = registration,
        )

        return registration
    }

    private fun createGalleryPanel(ent: Entity): PanelSceneObject {
        val config =
            PanelConfigOptions(
                enableLayer = true,
                enableTransparent = false,
                includeGlass = false,
            )

        return PanelSceneObject(
            scene,
            spatialContext,
            GalleryActivity::class.java,
            ent,
            config,
        )
    }

    private fun createMediaFilterPanel(ent: Entity): PanelSceneObject {
        val config =
            PanelConfigOptions(
                enableLayer = true,
                enableTransparent = false,
                includeGlass = false,
            )
        return PanelSceneObject(scene, spatialContext, MediaFilterActivity::class.java, ent, config)
    }

    private fun createUploadPanel(ent: Entity): PanelSceneObject {
        val config =
            PanelConfigOptions(
                width = 0.6f,
                height = 0.40f,
                enableLayer = true,
                enableTransparent = true,
                includeGlass = false,

                )

        return PanelSceneObject(scene, spatialContext, UploadActivity::class.java, ent, config)
    }

    private fun createGalleryMenuPanel(ent: Entity): PanelSceneObject {
        val config =
            PanelConfigOptions(
                enableLayer = true,
                enableTransparent = false,
                includeGlass = false,
            )
        return PanelSceneObject(scene, spatialContext, GalleryMenuActivity::class.java, ent, config)
    }

    private fun createPrivacyPolicyPanel(ent: Entity): PanelSceneObject {
        val config =
            PanelConfigOptions(
                enableLayer = true,
                enableTransparent = true,
                includeGlass = false,
            )
        return PanelSceneObject(
            scene,
            spatialContext,
            PrivacyPolicyActivity::class.java,
            ent,
            config
        )
    }

    private fun createPlayerPanel(ent: Entity, mediaModel: MediaModel): PanelSceneObject {
        val config = mediaModel.minimizedPanelConfigOptions()

        return PanelSceneObject(
            scene,
            spatialContext,
            Intent(spatialContext, PlayerActivity::class.java).apply {
                putExtra("mediaModel", mediaModel)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            },
            ent,
            config
        )
    }

    fun createPlayerEntity(mediaModel: MediaModel): Entity {
        val existingPanelsCount = Query.where { has(Panel.id) }.eval().count()
        val panelYOffset = 0.5f * existingPanelsCount

        val appPanelRegistration = registeredPanels[PanelRegistrationIds.media(mediaModel.id)]

        if (appPanelRegistration != null) {
            val playerEntity = Entity.createPanelEntity(
                appPanelRegistration.registrationId,
                Transform.build {
                    move(
                        0f,
                        1f + panelYOffset,
                        -2f
                    ) // Adjust position relative to the gallery panel
                        .then(rotateY(135f))
                },
                Grabbable(),
                Scale(0.5f)
            )

            mediaModel.entityId = playerEntity.id

            return playerEntity
        } else {
            throw RuntimeException("No panel registered for media with id ${mediaModel.id}. Please register it before trying to create an associated Entity.")
        }

    }

    private fun createPlayerMenuPanel(ent: Entity, mediaModel: MediaModel): PanelSceneObject {
        val config =
            PanelConfigOptions(
                width = dpToPx(Dimens.playerMenuTotalWidth) * PIXELS_TO_METERS,
                height = dpToPx(Dimens.playerMenuTotalHeight) * PIXELS_TO_METERS,
                layoutWidthInPx = dpToPx(Dimens.playerMenuTotalWidth),
                layoutHeightInPx = dpToPx(Dimens.playerMenuTotalHeight),
                enableLayer = true,
                enableTransparent = true,
                includeGlass = false,
            )

        return PanelSceneObject(
            scene,
            spatialContext,
            Intent(spatialContext, MinimizedMenuActivity::class.java).apply {
                putExtra("mediaModel", mediaModel)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            },
            ent,
            config
        )
    }

    fun createPlayerMenuEntity(mediaModel: MediaModel, playerEntity: Entity): Entity {
        if (playerEntity.tryGetComponent<Panel>() != null) {
            val appPanelRegistration =
                registeredPanels[PanelRegistrationIds.mediaPopUpButton(mediaModel.id)]

            if (appPanelRegistration != null) {
                val popUpMenuButtonEntity =
                    Entity.createPanelEntity(
                        appPanelRegistration.registrationId,
                        Transform.build { move(0f, 0f, 0f) }, Scale(2f)
                    )
                        .also { mediaModel.minimizedMenuEntityId = it.id }
                val (playerWidth, playerHeight) = mediaModel.panelWidthAndHeight()
                val (menuWidth, menuHeight) =
                    Pair(
                        dpToPx(Dimens.playerMenuTotalWidth) * PIXELS_TO_METERS,
                        dpToPx(Dimens.playerMenuTotalHeight) * PIXELS_TO_METERS
                    )

                // Anchor the menu to the player panel
                popUpMenuButtonEntity.setComponent(TransformParent(playerEntity))
                popUpMenuButtonEntity.setComponent(
                    Transform(
                        Pose(
                            Vector3(
                                playerWidth / 4 +
                                        menuWidth +
                                        (dpToPx(Dimens.medium.value.toInt()) * PIXELS_TO_METERS),
                                playerHeight / 4 - menuHeight / 2,
                                0f
                            ),
                            Quaternion(0f, 0f, 0f)
                        )
                    )
                )

                return popUpMenuButtonEntity
            } else {
                throw RuntimeException("No panel registered for media pop up button with id ${mediaModel.id}. Please register it before trying to create an associated Entity.")
            }
        } else {
            throw RuntimeException("Can't create a Player Menu Entity without a Player Entity for the media with id ${mediaModel.id}. Please, register and create the Player Entity first")
        }


    }

    private fun createImmersiveMenuPanel(ent: Entity, mediaModel: MediaModel): PanelSceneObject {
        val config =
            PanelConfigOptions(
                width = 0.5f,
                height = immersiveMenuHeight,
                layerConfig = QuadLayerConfig(zIndex = zIndexMenu),
                panelShader = "data/shaders/punch/punch",
                alphaMode = AlphaMode.HOLE_PUNCH,
                includeGlass = false,
            )

        return PanelSceneObject(
            scene,
            spatialContext,
            Intent(spatialContext, ImmersiveMenuActivity::class.java).apply {
                putExtra("mediaModel", mediaModel)
                addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                addFlags(Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
            },
            ent,
            config
        )
    }

    private fun createImmersiveMenuEntity(mediaModel: MediaModel): Entity {
        val appPanelRegistration =
            registeredPanels[PanelRegistrationIds.mediaImmersive(mediaModel.id)]

        if (appPanelRegistration != null) {
            return Entity.createPanelEntity(
                PanelRegistrationIds.mediaImmersive(mediaModel.id),
                Transform.createDefaultInstance()
            )
        } else {
            throw RuntimeException("No panel registered for immersive media with id ${mediaModel.id}. Please register it before trying to create an associated Entity.")
        }
    }

    fun closeMediaPanel(mediaModel: MediaModel) {
        if (mediaModel.entityId == null) {
            Timber.w("Cannot close media panel with null entityId")
            return
        }
        val entity =
            Query.where { has(Panel.id) }.eval().firstOrNull { it.id == mediaModel.entityId }
        entity?.destroy() ?: Timber.w("Player panel not found")
        mediaModel.entityId = null
        val menuEntity =
            Query.where { has(Panel.id) }
                .eval()
                .firstOrNull { it.id == mediaModel.minimizedMenuEntityId }
        menuEntity?.destroy() ?: Timber.w("Player menu panel not found")
        mediaModel.minimizedMenuEntityId = null
    }

    fun closeAllMediaPanels(openMedia: List<MediaModel>) {
        for (mediaModel in openMedia) {
            closeMediaPanel(mediaModel)
        }
    }

    fun createUploadEntity(): Entity {
        val appPanelRegistration = registeredPanels[PanelRegistrationIds.UPLOAD_MEDIA]

        if (appPanelRegistration != null) {
            return Entity.createPanelEntity(
                appPanelRegistration.registrationId,
                transform = Transform.createDefaultInstance()
            )
        } else {
            throw RuntimeException("No panel registered for Download Media. Please register it before trying to create an associated Entity.")
        }
    }

    fun destroyUploadEntity(entityId: Long) {
        val panels = Query.where { has(Panel.id) }.eval()
        val uploadPanel = panels.firstOrNull { it.id == entityId }
        uploadPanel?.destroy() ?: Timber.w("Upload panel not found")
    }

    private fun destroyImmersiveMenuEntity(entityId: Long) {
        val panels = Query.where { has(Panel.id) }.eval()
        val menuPanel = panels.firstOrNull { it.id == entityId }
        menuPanel?.destroy() ?: Timber.w("Immersive menu panel not found")
    }

    fun maximizePlayerPanel(mediaModel: MediaModel) {
        if (mediaModel.entityId == null) {
            Timber.w("Cannot maximize media panel with null entityId")
            return
        }
        // Update Player panel
        val entity =
            Query.where { has(Panel.id) }.eval().firstOrNull { it.id == mediaModel.entityId }
                ?: run {
                    Timber.w("Player panel not found")
                    return
                }
        val newPanelShapeConfig = mediaModel.maximizedPanelConfigOptions()
        // Apply new configuration
        panelTransformations.applyNewPanelConfiguration(entity, newPanelShapeConfig)
        // Set grabbable
        when (mediaModel.mediaType) {
            VIDEO_360 -> panelTransformations.setGrabbable(entity, true, GrabbableType.PIVOT_Y)

            else -> panelTransformations.setGrabbable(entity, false)
        }
        // Move the player panel to the center
        panelTransformations.applyTransform(entity, 1f, Vector3(0f, 0f, 0f))

        // Hide other panels
        Query.where { has(Panel.id) }
            .eval()
            .filter { it.id != mediaModel.entityId }
            .forEach { panelTransformations.setPanelVisibility(it, false) }

        // Show menu centerBottom from the player panel
        val immersiveMenu = createImmersiveMenuEntity(mediaModel)
        mediaModel.immersiveMenuEntityId = immersiveMenu.id
        Handler(Looper.getMainLooper())
            .postDelayed(
                {
                    immersiveMenu.setComponent(TransformParent(entity))
                    immersiveMenu.setComponent(
                        Transform(
                            Pose(
                                mediaModel.maximizedBottomCenterPanelVector3(),
                                Quaternion(0f, 0f, 0f)
                            )
                        )
                    )
                },
                100
            )
    }

    fun minimizePlayerPanel(mediaModel: MediaModel) {
        if (mediaModel.entityId == null) {
            Timber.w("Cannot minimize media panel with null entityId")
            return
        }
        // Update Player panel
        val entity =
            Query.where { has(Panel.id) }.eval().firstOrNull { it.id == mediaModel.entityId }
                ?: run {
                    Timber.w("Player panel not found")
                    return
                }
        // Reset the player panel configuration
        panelTransformations.applyNewPanelConfiguration(
            entity, mediaModel.minimizedPanelConfigOptions()
        )
        // Enable grabbable
        panelTransformations.setGrabbable(entity, true)

        // Remove immersive menu
        mediaModel.immersiveMenuEntityId?.let {
            destroyImmersiveMenuEntity(it)
            mediaModel.immersiveMenuEntityId = null
        } ?: Timber.w("Immersive menu panel not found")

        // Display other panels
        Query.where { has(Panel.id) }
            .eval()
            .filter {
                val privacyPolicyPanel =
                    getComposition().tryGetNodeByName(GLXFConstants.NODE_NAME_PRIVACY)

                it.id != mediaModel.entityId && it.id != privacyPolicyPanel?.entity?.id
            }
            .forEach { panelTransformations.setPanelVisibility(it, true) }
    }

    private fun getComposition(): GLXFInfo {
        val activity = SpatialActivityManager.getVrActivity<AppSystemActivity>()
        return activity.glXFManager.getGLXFInfo(GLXFConstants.COMPOSITION_NAME)
    }

    fun togglePrivacyPolicy(show: Boolean) {
        val panel = getComposition().tryGetNodeByName(GLXFConstants.NODE_NAME_PRIVACY)
        if (panel?.entity == null) {
            Timber.w("Privacy policy panel entity not found")
            return
        }
        panel.entity.setComponent(Visible(show))
    }

    fun toggleGallery(show: Boolean) {
        val panel = getComposition().tryGetNodeByName(GLXFConstants.NODE_NAME_GALLERY)
        if (panel?.entity == null) {
            Timber.w("Gallery panel entity not found")
            return
        }
        panel.entity.setComponent(Visible(show))
        // Set media filters visibility
        getComposition().tryGetNodeByName(GLXFConstants.NODE_NAME_MEDIA_FILTERS)
            ?.entity
            ?.setComponent(Visible(show))
    }

    companion object {
        private const val PIXELS_TO_METERS = 0.0254f / 100f

        private fun dpToPx(dp: Int): Int {
            return ((dp * DEFAULT_DPI).toFloat() / 160f).toInt()
        }
    }
}
