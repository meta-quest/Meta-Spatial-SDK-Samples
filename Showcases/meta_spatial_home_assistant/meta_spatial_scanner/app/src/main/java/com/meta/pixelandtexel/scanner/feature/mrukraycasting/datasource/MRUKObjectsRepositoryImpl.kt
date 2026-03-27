package com.meta.pixelandtexel.scanner.feature.mrukraycasting.datasource

import android.net.Uri
import com.meta.pixelandtexel.scanner.FollowHead
import com.meta.pixelandtexel.scanner.feature.mrukraycasting.domain.model.MrukRaycastModel
import com.meta.pixelandtexel.scanner.feature.mrukraycasting.domain.model.ObjectEntityModel
import com.meta.pixelandtexel.scanner.feature.mrukraycasting.domain.repository.IMRUKObjectsRepository
import com.meta.pixelandtexel.scanner.models.devices.Device
import com.meta.pixelandtexel.scanner.feature.mrukraycasting.datasource.local.MrukLocalDatasource
import com.meta.spatial.core.Entity
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Vector3
import com.meta.spatial.toolkit.Box
import com.meta.spatial.toolkit.Grabbable
import com.meta.spatial.toolkit.GrabbableType
import com.meta.spatial.toolkit.Mesh
import com.meta.spatial.toolkit.Transform
import com.meta.spatial.toolkit.Visible
import com.meta.spatial.toolkit.createPanelEntity
import com.meta.pixelandtexel.scanner.R
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.withContext


class MRUKObjectsRepositoryImpl(
    private val localDatasource: MrukLocalDatasource,
) : IMRUKObjectsRepository {

    override val mutex: Mutex = Mutex()
    override var lastAddedObjectDevice: Device? = null

    override val mrukEntities: HashMap<String, ObjectEntityModel> = HashMap()

    override suspend fun unlock() {
        mutex.unlock()
    }

    override suspend fun addMRUKObject(addObject: MrukRaycastModel): Boolean {
        if (!mrukEntities.containsKey(addObject.device.name)) {
            localDatasource.save(addObject)

            val newMeshPose = Pose(addObject.pose.t, addObject.pose.q)

            val boxEntity = Entity.create(
                listOf(
                    Mesh(Uri.parse("mesh://box")),
                    Box(Vector3(.1f, .1f, 0.1f)),
                    Transform(newMeshPose),
                    Visible(true)
                )
            )

            coroutineScope {
                launch(Dispatchers.Default) {
                    mutex.lock()
                    lastAddedObjectDevice = addObject.device

                    val panelEntity = Entity.createPanelEntity(
                        R.integer.object_panel_id,
                        Transform(newMeshPose * Pose(Vector3(0f, 0.5f, 0f))),
                        Grabbable(type = GrabbableType.PIVOT_Y),
                        FollowHead(true),
                    )

                    val objectEntity = ObjectEntityModel(
                        objectEntity = boxEntity,
                        panelEntity = panelEntity
                    )
                    mrukEntities[addObject.device.name] = objectEntity
                }
            }

            return true
        } else {
            return false
        }
    }

    override suspend fun deleteFromDatabase(objectId: String): Boolean {
        val deleteMRUKObject = deleteMRUKObject(objectId)
        localDatasource.delete(objectId)
        return deleteMRUKObject
    }

    override suspend fun deleteMRUKObject(objectId: String): Boolean {
        return if (mrukEntities.containsKey(objectId)) {
            val objectEntityModel = mrukEntities[objectId]
            objectEntityModel?.objectEntity?.destroy()
            objectEntityModel?.panelEntity?.destroy()
            mrukEntities.remove(objectId)

            true
        } else {
            false
        }
    }

    override suspend fun deleteAllMRUKObjects(): Boolean {
        mrukEntities.forEach { (_, objectEntityModel) ->
            objectEntityModel.objectEntity.destroy()
            objectEntityModel.panelEntity.destroy()
        }
        mrukEntities.clear()
        return true
    }

    override suspend fun getAllMRUKObjects(): List<MrukRaycastModel> {
        return withContext(Dispatchers.IO) {
            localDatasource.getAll()
        }
    }
}