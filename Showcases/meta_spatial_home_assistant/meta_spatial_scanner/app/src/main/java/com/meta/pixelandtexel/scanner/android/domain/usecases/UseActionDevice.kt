package com.meta.pixelandtexel.scanner.android.domain.usecases

import com.meta.pixelandtexel.scanner.android.domain.repository.SmartHomeRepository

class UseActionDevice(
    private val repository: SmartHomeRepository
) {
    suspend fun run(thingId: String, action: String, newValue: Any?, attribute: String?): Boolean {
        val newValuePair = if (newValue != null && attribute != null) {
            Pair(attribute, newValue)
        } else {
            null
        }
        return repository.getActionForThing(thingId, action, newValuePair)
    }

}