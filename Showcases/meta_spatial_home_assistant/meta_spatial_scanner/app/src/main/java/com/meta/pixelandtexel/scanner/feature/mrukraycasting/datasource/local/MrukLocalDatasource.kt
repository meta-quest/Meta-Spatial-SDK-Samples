package com.meta.pixelandtexel.scanner.feature.mrukraycasting.datasource.local

import com.meta.pixelandtexel.scanner.feature.mrukraycasting.domain.model.MrukRaycastModel
import com.meta.pixelandtexel.scanner.models.devices.Device
import com.meta.spatial.core.Pose
import com.meta.spatial.core.Quaternion
import com.meta.spatial.core.Vector3

class MrukLocalDatasource(private val dao: MrukDao) {
    suspend fun save(model: MrukRaycastModel) {
        val pose = model.pose
        val q = pose.q
        val t = pose.t

        val entity = MrukEntity(
            id = model.device.name,
            q_w = q.w,
            q_x = q.x,
            q_y = q.y,
            q_z = q.z,
            v_x = t.x,
            v_y = t.y,
            v_z = t.z
        )

        dao.insert(entity)
    }

    suspend fun getAll(): List<MrukRaycastModel> {
        return dao.getAll().map { e ->
            val q = Quaternion(e.q_w, e.q_x, e.q_y, e.q_z)
            val t = Vector3(e.v_x, e.v_y, e.v_z)
            MrukRaycastModel(Device(e.id, emptyList()), Pose(t, q))
        }
    }

    suspend fun delete(id: String) {
        dao.deleteById(id)
    }
}
