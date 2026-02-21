package com.meta.pixelandtexel.scanner.feature.mrukraycasting.domain.repository

import com.meta.pixelandtexel.scanner.feature.mrukraycasting.domain.model.MrukRaycastModel
import com.meta.pixelandtexel.scanner.feature.mrukraycasting.domain.model.ObjectEntityModel
import com.meta.pixelandtexel.scanner.models.devices.Device
import kotlinx.coroutines.sync.Mutex

interface IMRUKObjectsRepository {
    var lastAddedObjectDevice: Device?
    val mrukEntities: HashMap<String, ObjectEntityModel>
    val mutex: Mutex
    suspend fun addMRUKObject(addObject: MrukRaycastModel): Boolean
    suspend fun deleteFromDatabase(objectId: String): Boolean
    suspend fun deleteMRUKObject(objectId: String): Boolean
    suspend fun deleteAllMRUKObjects(): Boolean
    suspend fun getAllMRUKObjects(): List<MrukRaycastModel>
    suspend fun unlock()
}