package com.meta.pixelandtexel.scanner.feature.objectdetection.domain.repository.display

import com.meta.pixelandtexel.scanner.models.EntityData
import com.meta.pixelandtexel.scanner.models.smarthomedata.SmartHomeInfoRequest
import com.meta.spatial.core.Entity

/**
 * Defines the contract for a repository that manages the creation
 * and data handling of displayed entities in the scene, such as information panels.
 */
interface IDisplayedEntityRepository {
    /**
     * Holds the data for the next information panel to be displayed.
     * This data is consumed when a new panel entity is created.
     */
    var newViewModelData: EntityData?

    val entitiesHashMap: HashMap<Int, Entity>

    /**
     * Creates a generic information panel entity at a calculated position.
     * It also stores the provided data to be consumed by the panel's composable.
     *
     * @param panelId The resource ID for the panel registration.
     * @param data The information to be displayed on the panel.
     * @return The newly created panel [Entity].
     */
    fun createGenericInfoPanel(
        panelId: Int,
        data: SmartHomeInfoRequest,
    ): Entity

    fun deleteEntity(entityId: Int)

}